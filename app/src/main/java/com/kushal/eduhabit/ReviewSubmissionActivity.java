package com.kushal.eduhabit;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kushal.eduhabit.databinding.ActivityReviewSubmissionBinding;
import java.util.HashMap;
import java.util.Map;

public class ReviewSubmissionActivity extends AppCompatActivity {

    private ActivityReviewSubmissionBinding binding;
    private FirebaseFirestore db;
    private String submissionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReviewSubmissionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        submissionId = getIntent().getStringExtra("submissionId");

        binding.backBtn.setOnClickListener(v -> finish());

        if (submissionId != null) {
            fetchSubmissionDetails();
        }

        binding.btnSubmitReview.setOnClickListener(v -> submitReview());
    }

    private void fetchSubmissionDetails() {
        db.collection("submissions").document(submissionId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        binding.tvAssignmentTitle.setText(documentSnapshot.getString("assignmentTitle"));
                        binding.tvStudentContent.setText(documentSnapshot.getString("content"));
                        
                        String grade = documentSnapshot.getString("grade");
                        String feedback = documentSnapshot.getString("feedback");
                        
                        if (grade != null) binding.etGrade.setText(grade);
                        if (feedback != null) binding.etFeedback.setText(feedback);
                    }
                });
    }

    private void submitReview() {
        String grade = binding.etGrade.getText().toString().trim();
        String feedback = binding.etFeedback.getText().toString().trim();

        if (grade.isEmpty()) {
            Toast.makeText(this, "Please enter a grade", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> review = new HashMap<>();
        review.put("grade", grade);
        review.put("feedback", feedback);
        review.put("status", "graded");

        db.collection("submissions").document(submissionId)
                .update(review)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Review saved successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}