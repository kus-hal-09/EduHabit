package com.kushal.eduhabit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.kushal.eduhabit.databinding.ActivityStudentsListBinding;
import com.kushal.eduhabit.databinding.ItemStudentBinding;
import java.util.ArrayList;
import java.util.List;

public class StudentsListActivity extends AppCompatActivity {

    private ActivityStudentsListBinding binding;
    private FirebaseFirestore db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStudentsListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        session = new SessionManager(this);

        binding.backBtn.setOnClickListener(v -> finish());
        setupBottomNav();
        loadStudentsRealTime();
    }

    private void setupBottomNav() {
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
    }

    private void loadStudentsRealTime() {
        String teacherCourse = session.getCourse();
        String teacherSemester = session.getSemester();
        String currentUserId = session.getUid();
        
        binding.tvCourseLabel.setText(teacherCourse + " " + teacherSemester + " Students");

        // STRICT FILTER: role must be "student", and semester/course must match the teacher
        db.collection("users")
                .whereEqualTo("role", "student")
                .whereEqualTo("course", teacherCourse)
                .whereEqualTo("semester", teacherSemester)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (snapshots != null) {
                        // Filter out the current user just in case they have a student role accidentally
                        List<DocumentSnapshot> filteredDocs = new ArrayList<>();
                        for (DocumentSnapshot doc : snapshots.getDocuments()) {
                            if (!doc.getId().equals(currentUserId)) {
                                filteredDocs.add(doc);
                            }
                        }

                        binding.totalStudentsCount.setText(String.valueOf(filteredDocs.size()));
                        
                        if (filteredDocs.isEmpty()) {
                            binding.emptyStateText.setVisibility(View.VISIBLE);
                            binding.studentsContainer.removeAllViews();
                        } else {
                            binding.emptyStateText.setVisibility(View.GONE);
                            binding.studentsContainer.removeAllViews();
                            for (DocumentSnapshot doc : filteredDocs) {
                                fetchStudentStatsAndAddView(doc);
                            }
                        }
                        calculateClassStats(teacherCourse, teacherSemester);
                    }
                });
    }

    private void fetchStudentStatsAndAddView(DocumentSnapshot studentDoc) {
        String studentId = studentDoc.getId();
        ItemStudentBinding itemBinding = ItemStudentBinding.inflate(getLayoutInflater(), binding.studentsContainer, false);
        
        itemBinding.studentName.setText(studentDoc.getString("name"));
        itemBinding.studentEmail.setText(studentDoc.getString("email"));

        // Set status and level badges
        itemBinding.statusBadge.setText("Active");
        itemBinding.levelBadge.setText("New");

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
                                int grade = Integer.parseInt(gradeStr.replaceAll("[^0-9]", ""));
                                totalGrade += grade;
                                gradedCount++;
                            } catch (Exception ignored) {}
                        }
                    }

                    itemBinding.exercisesText.setText(String.valueOf(totalSubmissions));
                    if (gradedCount > 0) {
                        int avgGrade = totalGrade / gradedCount;
                        itemBinding.avgGradeText.setText(avgGrade + "%");
                    } else {
                        itemBinding.avgGradeText.setText("0%");
                    }
                    itemBinding.streakText.setText(String.valueOf(studentDoc.getLong("streak") != null ? studentDoc.getLong("streak") : 0));
                });

        binding.studentsContainer.addView(itemBinding.getRoot());
    }

    private void calculateClassStats(String course, String semester) {
        db.collection("submissions")
                .whereEqualTo("course", course)
                .whereEqualTo("semester", semester)
                .whereEqualTo("status", "graded")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        int totalGrade = 0;
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            try {
                                String gradeStr = doc.getString("grade");
                                if (gradeStr != null) {
                                    int grade = Integer.parseInt(gradeStr.replaceAll("[^0-9]", ""));
                                    totalGrade += grade;
                                }
                            } catch (Exception ignored) {}
                        }
                        binding.avgGradeText.setText((totalGrade / queryDocumentSnapshots.size()) + "%");
                    } else {
                        binding.avgGradeText.setText("0%");
                    }
                });
    }
}
