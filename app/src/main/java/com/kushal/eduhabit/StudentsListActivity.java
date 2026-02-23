package com.kushal.eduhabit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kushal.eduhabit.databinding.ActivityStudentsListBinding;
import com.kushal.eduhabit.databinding.ItemStudentBinding;
import java.util.ArrayList;
import java.util.List;

public class StudentsListActivity extends AppCompatActivity {

    private ActivityStudentsListBinding binding;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStudentsListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();

        binding.backBtn.setOnClickListener(v -> finish());

        // --- BOTTOM NAVIGATION LOGIC ---
        binding.teacherBottomNavigation.setSelectedItemId(R.id.nav_teacher_students);
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
                return true;
            } else if (itemId == R.id.nav_teacher_analytics) {
                startActivity(new Intent(this, AnalyticsActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_teacher_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                finish();
                return true;
            }
            return false;
        });

        fetchStudents();
    }

    private void fetchStudents() {
        db.collection("users").whereEqualTo("role", "student").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    binding.totalStudentsCount.setText(String.valueOf(queryDocumentSnapshots.size()));
                    
                    if (queryDocumentSnapshots.isEmpty()) {
                        binding.emptyStateText.setVisibility(View.VISIBLE);
                        binding.studentsContainer.removeAllViews();
                    } else {
                        binding.emptyStateText.setVisibility(View.GONE);
                        binding.studentsContainer.removeAllViews();
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            fetchStudentStatsAndAddView(doc);
                        }
                    }
                    calculateClassStats();
                });
    }

    private void fetchStudentStatsAndAddView(DocumentSnapshot studentDoc) {
        String studentId = studentDoc.getId();
        ItemStudentBinding itemBinding = ItemStudentBinding.inflate(getLayoutInflater(), binding.studentsContainer, false);
        
        itemBinding.studentName.setText(studentDoc.getString("name"));
        itemBinding.studentEmail.setText(studentDoc.getString("email"));

        // Fetch submissions to calculate real stats
        db.collection("submissions")
                .whereEqualTo("studentId", studentId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int totalSubmissions = queryDocumentSnapshots.size();
                    int totalGrade = 0;
                    int gradedCount = 0;

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        if ("graded".equals(doc.getString("status"))) {
                            String gradeStr = doc.getString("grade");
                            try {
                                // Try to extract numeric value from grade (e.g., "90%" -> 90)
                                int grade = Integer.parseInt(gradeStr.replaceAll("[^0-9]", ""));
                                totalGrade += grade;
                                gradedCount++;
                            } catch (Exception e) {
                                // Ignore non-numeric grades for calculation
                            }
                        }
                    }

                    itemBinding.exercisesText.setText(String.valueOf(totalSubmissions));
                    if (gradedCount > 0) {
                        int avgGrade = totalGrade / gradedCount;
                        itemBinding.avgGradeText.setText(avgGrade + "%");
                        itemBinding.gradeLevel.setText(avgGrade >= 90 ? "Excellent" : (avgGrade >= 75 ? "Good" : "Fair"));
                    } else {
                        itemBinding.avgGradeText.setText("0%");
                        itemBinding.gradeLevel.setText("New");
                    }
                    
                    // Streak logic (simplified for now)
                    itemBinding.streakText.setText("0"); 
                });

        binding.studentsContainer.addView(itemBinding.getRoot());
    }

    private void calculateClassStats() {
        db.collection("submissions").whereEqualTo("status", "graded").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        int totalGrade = 0;
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            try {
                                int grade = Integer.parseInt(doc.getString("grade").replaceAll("[^0-9]", ""));
                                totalGrade += grade;
                            } catch (Exception ignored) {}
                        }
                        binding.avgGradeText.setText((totalGrade / queryDocumentSnapshots.size()) + "%");
                    }
                });
    }
}