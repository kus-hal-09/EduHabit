package com.kushal.eduhabit;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.kushal.eduhabit.databinding.ActivityTeacherDashboardBinding;

public class TeacherDashboardActivity extends AppCompatActivity {

    private ActivityTeacherDashboardBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTeacherDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // QUICK ACTIONS
        binding.cardCreateAssignment.setOnClickListener(v -> {
            startActivity(new Intent(this, CreateAssignmentActivity.class));
        });

        binding.cardAllAssignments.setOnClickListener(v -> {
            startActivity(new Intent(this, SubmissionsListActivity.class));
        });

        binding.cardStudentProgress.setOnClickListener(v -> {
            startActivity(new Intent(this, AnalyticsActivity.class));
        });

        // LOGOUT
        binding.logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        // BOTTOM NAVIGATION LOGIC
        binding.teacherBottomNavigation.setSelectedItemId(R.id.nav_teacher_home);
        binding.teacherBottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_teacher_home) {
                return true;
            } else if (itemId == R.id.nav_teacher_tasks) {
                startActivity(new Intent(this, SubmissionsListActivity.class));
                return true;
            } else if (itemId == R.id.nav_teacher_students) {
                startActivity(new Intent(this, StudentsListActivity.class));
                return true;
            } else if (itemId == R.id.nav_teacher_analytics) {
                startActivity(new Intent(this, AnalyticsActivity.class));
                return true;
            } else if (itemId == R.id.nav_teacher_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });

        fetchTeacherName();
        fetchStats();
        fetchRecentAssignments();
    }

    private void fetchTeacherName() {
        if (mAuth.getCurrentUser() == null) return;
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        binding.teacherWelcomeText.setText("Welcome, " + name + "!");
                    }
                });
    }

    private void fetchStats() {
        // Fetch Total Students
        db.collection("users").whereEqualTo("role", "student").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    binding.tvTotalStudents.setText(String.valueOf(queryDocumentSnapshots.size()));
                });

        // Fetch Total Assignments
        db.collection("assignments").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    binding.tvTotalAssignments.setText(String.valueOf(queryDocumentSnapshots.size()));
                });
        
        // Pending Reviews
        db.collection("submissions").whereEqualTo("status", "pending").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    binding.tvPendingReviews.setText(String.valueOf(queryDocumentSnapshots.size()));
                });
                
        // Fetch Graded Count
        db.collection("submissions").whereEqualTo("status", "graded").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    binding.tvGradedCount.setText(String.valueOf(queryDocumentSnapshots.size()));
                });
    }

    private void fetchRecentAssignments() {
        db.collection("assignments")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(3)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        binding.tvEmptyAssignments.setVisibility(View.GONE);
                        binding.teacherRecentAssignmentsList.removeAllViews();
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            View itemView = LayoutInflater.from(this).inflate(android.R.layout.simple_list_item_2, null);
                            TextView title = itemView.findViewById(android.R.id.text1);
                            TextView subtitle = itemView.findViewById(android.R.id.text2);
                            
                            title.setText(doc.getString("title"));
                            subtitle.setText("Due: " + doc.getString("dueDate"));
                            binding.teacherRecentAssignmentsList.addView(itemView);
                        }
                    } else {
                        binding.tvEmptyAssignments.setVisibility(View.VISIBLE);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchStats();
        fetchRecentAssignments();
    }
}