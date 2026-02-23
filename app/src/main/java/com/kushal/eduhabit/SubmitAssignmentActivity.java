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
    private String assignmentId;
    private String assignmentTitle;
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

        assignmentId = getIntent().getStringExtra("assignmentId");
        assignmentTitle = getIntent().getStringExtra("assignmentTitle");

        if (assignmentTitle != null) {
            binding.tvAssignmentTitle.setText(assignmentTitle);
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
                    binding.btnSubmit.setText("Submit Work");
                    Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void submitWork(String fileUrl) {
        String content = binding.etSubmissionContent.getText().toString().trim();

        if (content.isEmpty() && fileUrl == null) {
            Toast.makeText(this, "Please provide an answer or attach a file", Toast.LENGTH_SHORT).show();
            binding.btnSubmit.setEnabled(true);
            binding.btnSubmit.setText("Submit Work");
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;

        binding.btnSubmit.setEnabled(false);
        binding.btnSubmit.setText("Submitting...");

        Map<String, Object> submission = new HashMap<>();
        submission.put("assignmentId", assignmentId);
        submission.put("assignmentTitle", assignmentTitle != null ? assignmentTitle : "Untitled");
        submission.put("studentId", currentUser.getUid());
        submission.put("studentEmail", currentUser.getEmail());
        submission.put("content", content);
        submission.put("attachmentUrl", fileUrl);
        submission.put("submittedAt", System.currentTimeMillis());
        submission.put("status", "pending");

        db.collection("submissions").add(submission)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(SubmitAssignmentActivity.this, "Assignment submitted successfully!", Toast.LENGTH_LONG).show();
                    new android.os.Handler().postDelayed(this::finish, 1000);
                })
                .addOnFailureListener(e -> {
                    binding.btnSubmit.setEnabled(true);
                    binding.btnSubmit.setText("Submit Work");
                    Toast.makeText(SubmitAssignmentActivity.this, "Submission failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}