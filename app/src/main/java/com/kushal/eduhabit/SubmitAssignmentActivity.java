package com.kushal.eduhabit;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kushal.eduhabit.databinding.ActivitySubmitAssignmentBinding;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SubmitAssignmentActivity extends AppCompatActivity {

    private ActivitySubmitAssignmentBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private SessionManager session;
    private String assignmentId;
    private String assignmentTitle;
    private String submissionId; // For editing
    private Uri selectedFileUri;
    private String fileType; // "image" or "document"

    private final ActivityResultLauncher<String> getContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedFileUri = uri;
                    handleFileSelection(uri);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubmitAssignmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        session = new SessionManager(this);

        assignmentId = getIntent().getStringExtra("assignmentId");
        assignmentTitle = getIntent().getStringExtra("assignmentTitle");
        submissionId = getIntent().getStringExtra("submissionId");
        String existingContent = getIntent().getStringExtra("existingContent");

        if (assignmentTitle != null) {
            binding.tvAssignmentTitle.setText(assignmentTitle);
        }

        if (submissionId != null) {
            binding.btnSubmit.setText("Update Submission");
            if (existingContent != null) {
                binding.etSubmissionContent.setText(existingContent);
            }
            checkIfAlreadyGraded();
        }

        binding.backBtn.setOnClickListener(v -> finish());

        binding.btnAddPhoto.setOnClickListener(v -> {
            fileType = "image";
            getContent.launch("image/*");
        });

        binding.btnAddFile.setOnClickListener(v -> {
            fileType = "document";
            getContent.launch("application/*");
        });

        binding.btnSubmit.setOnClickListener(v -> {
            if (selectedFileUri != null) {
                uploadFileAndSubmit();
            } else {
                submitWork(null);
            }
        });
    }

    private void checkIfAlreadyGraded() {
        db.collection("submissions").document(submissionId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String status = documentSnapshot.getString("status");
                        if ("graded".equals(status)) {
                            Toast.makeText(this, "This assignment is already graded and cannot be edited.", Toast.LENGTH_LONG).show();
                            binding.btnSubmit.setEnabled(false);
                            binding.btnAddFile.setEnabled(false);
                            binding.btnAddPhoto.setEnabled(false);
                            binding.etSubmissionContent.setEnabled(false);
                        }
                    }
                });
    }

    private void handleFileSelection(Uri uri) {
        if ("image".equals(fileType)) {
            binding.ivAttachmentPreview.setVisibility(View.VISIBLE);
            binding.ivAttachmentPreview.setImageURI(uri);
            binding.tvFileName.setVisibility(View.GONE);
        } else {
            binding.ivAttachmentPreview.setVisibility(View.GONE);
            binding.tvFileName.setVisibility(View.VISIBLE);
            binding.tvFileName.setText("File selected: " + uri.getLastPathSegment());
        }
    }

    private void uploadFileAndSubmit() {
        binding.btnSubmit.setEnabled(false);
        binding.btnSubmit.setText("Uploading File...");

        String fileName = UUID.randomUUID().toString();
        StorageReference fileRef = storage.getReference().child("submissions/" + fileName);

        fileRef.putFile(selectedFileUri)
                .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    submitWork(uri.toString());
                }))
                .addOnFailureListener(e -> {
                    binding.btnSubmit.setEnabled(true);
                    binding.btnSubmit.setText(submissionId != null ? "Update Submission" : "Submit Work");
                    Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void submitWork(String fileUrl) {
        String content = binding.etSubmissionContent.getText().toString().trim();

        if (content.isEmpty() && fileUrl == null && submissionId == null) {
            Toast.makeText(this, "Please provide an answer or attach a file", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        binding.btnSubmit.setEnabled(false);
        binding.btnSubmit.setText(submissionId != null ? "Updating..." : "Submitting...");

        Map<String, Object> submission = new HashMap<>();
        submission.put("content", content);
        if (fileUrl != null) {
            submission.put("attachmentUrl", fileUrl);
        }
        submission.put("submittedAt", System.currentTimeMillis());
        submission.put("status", "submitted");

        if (submissionId == null) {
            // New submission
            submission.put("assignmentId", assignmentId);
            submission.put("assignmentTitle", assignmentTitle != null ? assignmentTitle : "Untitled");
            submission.put("studentId", currentUser.getUid());
            submission.put("studentEmail", currentUser.getEmail());
            submission.put("studentName", session.getName());
            submission.put("course", session.getCourse());
            submission.put("semester", session.getSemester());

            db.collection("submissions").add(submission)
                    .addOnSuccessListener(documentReference -> {
                        createNotification(currentUser.getUid(), currentUser.getEmail(), assignmentTitle);
                        Toast.makeText(SubmitAssignmentActivity.this, "Assignment submitted successfully!", Toast.LENGTH_LONG).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        binding.btnSubmit.setEnabled(true);
                        binding.btnSubmit.setText("Submit Work");
                        Toast.makeText(SubmitAssignmentActivity.this, "Submission failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        } else {
            // Update existing submission
            db.collection("submissions").document(submissionId).update(submission)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(SubmitAssignmentActivity.this, "Submission updated successfully!", Toast.LENGTH_LONG).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        binding.btnSubmit.setEnabled(true);
                        binding.btnSubmit.setText("Update Submission");
                        Toast.makeText(SubmitAssignmentActivity.this, "Update failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        }
    }

    private void createNotification(String studentId, String studentEmail, String title) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("studentId", studentId);
        notification.put("studentEmail", studentEmail);
        notification.put("title", "New Submission");
        notification.put("message", studentEmail + " submitted " + (title != null ? title : "an assignment"));
        notification.put("timestamp", System.currentTimeMillis());
        notification.put("read", false);
        notification.put("course", session.getCourse());
        notification.put("semester", session.getSemester());

        db.collection("notifications").add(notification);
    }
}
