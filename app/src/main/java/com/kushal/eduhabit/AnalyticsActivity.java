package com.kushal.eduhabit;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kushal.eduhabit.databinding.ActivityAnalyticsBinding;
import java.util.ArrayList;
import java.util.List;

public class AnalyticsActivity extends AppCompatActivity {

    private ActivityAnalyticsBinding binding;
    private FirebaseFirestore db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAnalyticsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Fix overlapping with status bar
        ViewCompat.setOnApplyWindowInsetsListener(binding.analyticsHeader, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), systemBars.top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        db = FirebaseFirestore.getInstance();
        session = new SessionManager(this);

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
        String currentTeacherId = session.getUid();
        
        // 1. Fetch Students Count (Filter out the current user)
        db.collection("users").whereEqualTo("role", "student").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int count = 0;
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        if (!doc.getId().equals(currentTeacherId)) {
                            count++;
                        }
                    }
                    binding.studentsCountText.setText(String.valueOf(count));
                });

        // 2. Fetch and Aggregate Graded Submissions
        db.collection("submissions").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) return;

                    int totalGrade = 0;
                    int gradedCount = 0;
                    int totalSubmissions = queryDocumentSnapshots.size();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        if ("graded".equals(doc.getString("status"))) {
                            try {
                                String gradeStr = doc.getString("grade");
                                if (gradeStr != null) {
                                    int grade = Integer.parseInt(gradeStr.replaceAll("[^0-9]", ""));
                                    totalGrade += grade;
                                    gradedCount++;
                                }
                            } catch (Exception ignored) {}
                        }
                    }

                    if (gradedCount > 0) {
                        int avg = totalGrade / gradedCount;
                        binding.avgGradeText.setText(avg + "%");
                        binding.completionText.setText(((gradedCount * 100) / totalSubmissions) + "%");
                    }
                });
    }
}
