package com.kushal.eduhabit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.kushal.eduhabit.databinding.ActivitySubmissionsListBinding;
import com.kushal.eduhabit.databinding.ItemSubmissionBinding;

public class SubmissionsListActivity extends AppCompatActivity {

    private ActivitySubmissionsListBinding binding;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubmissionsListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        binding.backBtn.setOnClickListener(v -> finish());

        fetchSubmissions();
    }

    private void fetchSubmissions() {
        db.collection("submissions")
                .orderBy("submittedAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        binding.tvEmptyState.setVisibility(View.VISIBLE);
                    } else {
                        binding.tvEmptyState.setVisibility(View.GONE);
                        binding.submissionsContainer.removeAllViews();
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            addSubmissionView(doc);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error fetching submissions: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void addSubmissionView(DocumentSnapshot doc) {
        ItemSubmissionBinding itemBinding = ItemSubmissionBinding.inflate(getLayoutInflater(), binding.submissionsContainer, false);
        
        itemBinding.tvTitle.setText(doc.getString("assignmentTitle"));
        itemBinding.tvStudentEmail.setText(doc.getString("studentEmail"));
        itemBinding.tvStatus.setText(doc.getString("status"));

        if ("graded".equals(doc.getString("status"))) {
            itemBinding.tvGrade.setVisibility(View.VISIBLE);
            itemBinding.tvGrade.setText("Grade: " + doc.getString("grade"));
            itemBinding.btnAction.setText("View Review");
        }

        itemBinding.btnAction.setOnClickListener(v -> {
            Intent intent = new Intent(this, ReviewSubmissionActivity.class);
            intent.putExtra("submissionId", doc.getId());
            startActivity(intent);
        });

        binding.submissionsContainer.addView(itemBinding.getRoot());
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchSubmissions();
    }
}