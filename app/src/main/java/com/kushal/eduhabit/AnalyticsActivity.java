package com.kushal.eduhabit;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kushal.eduhabit.databinding.ActivityAnalyticsBinding;

public class AnalyticsActivity extends AppCompatActivity {

    private ActivityAnalyticsBinding binding;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAnalyticsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        binding.backBtn.setOnClickListener(v -> finish());

        // --- BOTTOM NAVIGATION LOGIC ---
        binding.teacherBottomNavigation.setSelectedItemId(R.id.nav_teacher_analytics);
        binding.teacherBottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_teacher_home) {
                startActivity(new Intent(this, TeacherDashboardActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_teacher_tasks) {
                startActivity(new Intent(this, SubmissionsListActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_teacher_students) {
                startActivity(new Intent(this, StudentsListActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_teacher_analytics) {
                return true;
            } else if (itemId == R.id.nav_teacher_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                finish();
                return true;
            }
            return false;
        });

        fetchAnalyticsData();
    }

    private void fetchAnalyticsData() {
        // 1. Fetch Students Count
        db.collection("users").whereEqualTo("role", "student").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    binding.studentsCountText.setText(String.valueOf(queryDocumentSnapshots.size()));
                });

        // 2. Fetch and Aggregate Graded Submissions
        db.collection("submissions").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) return;

                    int totalGrade = 0;
                    int gradedCount = 0;
                    int excellent = 0, good = 0, fair = 0;

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        if ("graded".equals(doc.getString("status"))) {
                            try {
                                int grade = Integer.parseInt(doc.getString("grade").replaceAll("[^0-9]", ""));
                                totalGrade += grade;
                                gradedCount++;

                                if (grade >= 90) excellent++;
                                else if (grade >= 75) good++;
                                else if (grade >= 60) fair++;
                            } catch (Exception ignored) {}
                        }
                    }

                    if (gradedCount > 0) {
                        int avg = totalGrade / gradedCount;
                        binding.avgGradeText.setText(avg + "%");
                        binding.completionText.setText(((gradedCount * 100) / queryDocumentSnapshots.size()) + "%");
                        
                        // Update Progress Bars (Mocking max 10 for visual)
                        // In real app, calculate % based on total students
                    }
                });
    }
}