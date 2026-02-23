package com.kushal.eduhabit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.kushal.eduhabit.databinding.ActivityAssignmentListBinding;
import com.kushal.eduhabit.databinding.ItemAssignmentBinding;
import com.kushal.eduhabit.databinding.ItemSubmissionBinding;
import java.util.HashSet;
import java.util.Set;

public class AssignmentListActivity extends AppCompatActivity {

    private ActivityAssignmentListBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Set<String> submittedAssignmentIds = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAssignmentListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        binding.backBtn.setOnClickListener(v -> finish());

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                refreshList();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        // First find out what the user has already submitted
        fetchUserSubmissions();
    }

    private void fetchUserSubmissions() {
        if (mAuth.getCurrentUser() == null) return;
        
        db.collection("submissions")
                .whereEqualTo("studentId", mAuth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    submittedAssignmentIds.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        submittedAssignmentIds.add(doc.getString("assignmentId"));
                    }
                    refreshList();
                });
    }

    private void refreshList() {
        int selectedTab = binding.tabLayout.getSelectedTabPosition();
        if (selectedTab == 0) {
            fetchPendingAssignments();
        } else {
            fetchSubmittedAssignments();
        }
    }

    private void fetchPendingAssignments() {
        db.collection("assignments")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    binding.assignmentsContainer.removeAllViews();
                    boolean hasPending = false;
                    
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        if (!submittedAssignmentIds.contains(doc.getId())) {
                            addAssignmentView(doc);
                            hasPending = true;
                        }
                    }
                    
                    binding.tvEmptyState.setVisibility(hasPending ? View.GONE : View.VISIBLE);
                    binding.tvEmptyState.setText("No pending assignments!");
                });
    }

    private void fetchSubmittedAssignments() {
        db.collection("submissions")
                .whereEqualTo("studentId", mAuth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    binding.assignmentsContainer.removeAllViews();
                    if (queryDocumentSnapshots.isEmpty()) {
                        binding.tvEmptyState.setVisibility(View.VISIBLE);
                        binding.tvEmptyState.setText("No submitted assignments yet.");
                    } else {
                        binding.tvEmptyState.setVisibility(View.GONE);
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            addSubmissionView(doc);
                        }
                    }
                });
    }

    private void addAssignmentView(DocumentSnapshot doc) {
        ItemAssignmentBinding itemBinding = ItemAssignmentBinding.inflate(getLayoutInflater(), binding.assignmentsContainer, false);
        itemBinding.tvTitle.setText(doc.getString("title"));
        itemBinding.tvDueDate.setText("Due: " + doc.getString("dueDate"));
        itemBinding.tvDescription.setText(doc.getString("description"));
        itemBinding.btnSubmit.setOnClickListener(v -> {
            Intent intent = new Intent(this, SubmitAssignmentActivity.class);
            intent.putExtra("assignmentId", doc.getId());
            intent.putExtra("assignmentTitle", doc.getString("title"));
            startActivity(intent);
        });
        binding.assignmentsContainer.addView(itemBinding.getRoot());
    }

    private void addSubmissionView(DocumentSnapshot doc) {
        ItemSubmissionBinding itemBinding = ItemSubmissionBinding.inflate(getLayoutInflater(), binding.assignmentsContainer, false);
        itemBinding.tvTitle.setText(doc.getString("assignmentTitle"));
        itemBinding.tvStudentEmail.setText("Submitted on: " + new java.util.Date(doc.getLong("submittedAt")).toString());
        
        String status = doc.getString("status");
        itemBinding.tvStatus.setText(status);
        
        if ("graded".equals(status)) {
            itemBinding.tvGrade.setVisibility(View.VISIBLE);
            itemBinding.tvGrade.setText("Grade: " + doc.getString("grade") + "\nFeedback: " + doc.getString("feedback"));
            itemBinding.tvStatus.setBackgroundColor(0xFFD1FAE5); // Light Green
            itemBinding.tvStatus.setTextColor(0xFF059669); // Dark Green
        }

        itemBinding.btnAction.setVisibility(View.GONE); // Students only view, don't review
        binding.assignmentsContainer.addView(itemBinding.getRoot());
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchUserSubmissions(); // Refresh when returning from SubmitAssignmentActivity
    }
}