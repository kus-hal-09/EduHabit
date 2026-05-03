package com.kushal.eduhabit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kushal.eduhabit.databinding.ActivitySubmissionsListBinding;
import com.kushal.eduhabit.databinding.ItemSubmissionBinding;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SubmissionsListActivity extends AppCompatActivity {

    private ActivitySubmissionsListBinding binding;
    private FirebaseFirestore db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubmissionsListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        session = new SessionManager(this);

        binding.backBtn.setOnClickListener(v -> finish());

        setupBottomNav();
        fetchSubmissions();
    }

    private void setupBottomNav() {
        binding.teacherBottomNavigation.setSelectedItemId(R.id.nav_teacher_tasks);
        binding.teacherBottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_teacher_home) {
                startActivity(new Intent(this, TeacherDashboardActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_teacher_tasks) {
                return true;
            } else if (itemId == R.id.nav_teacher_students) {
                startActivity(new Intent(this, StudentsListActivity.class));
                finish();
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

    private void fetchSubmissions() {
        String teacherCourse = session.getCourse();
        String teacherSemester = session.getSemester();

        db.collection("submissions")
                .whereEqualTo("course", teacherCourse)
                .whereEqualTo("semester", teacherSemester)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        binding.tvEmptyState.setVisibility(View.VISIBLE);
                        binding.submissionsContainer.removeAllViews();
                    } else {
                        binding.tvEmptyState.setVisibility(View.GONE);
                        binding.submissionsContainer.removeAllViews();
                        
                        List<DocumentSnapshot> list = new ArrayList<>(queryDocumentSnapshots.getDocuments());
                        Collections.sort(list, (d1, d2) -> {
                            Long t1 = d1.getLong("submittedAt");
                            Long t2 = d2.getLong("submittedAt");
                            if (t1 == null) t1 = 0L;
                            if (t2 == null) t2 = 0L;
                            return t2.compareTo(t1);
                        });

                        for (DocumentSnapshot doc : list) {
                            addSubmissionView(doc);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void addSubmissionView(DocumentSnapshot doc) {
        ItemSubmissionBinding itemBinding = ItemSubmissionBinding.inflate(getLayoutInflater(), binding.submissionsContainer, false);
        
        itemBinding.tvTitle.setText(doc.getString("assignmentTitle"));
        itemBinding.tvStudentEmail.setText(doc.getString("studentEmail"));
        
        String status = doc.getString("status");
        itemBinding.tvStatus.setText(status);

        if ("graded".equals(status)) {
            itemBinding.tvStatus.setBackgroundResource(R.drawable.demo_badge_bg);
            itemBinding.tvStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#E0F2FE")));
            itemBinding.tvStatus.setTextColor(android.graphics.Color.parseColor("#0369A1"));
            
            itemBinding.tvGrade.setVisibility(View.VISIBLE);
            itemBinding.tvGrade.setText("Grade: " + doc.getString("grade"));
            itemBinding.btnAction.setText("View Review");
        } else {
            // Default "submitted" status look
            itemBinding.tvStatus.setBackgroundResource(R.drawable.demo_badge_bg);
            itemBinding.tvStatus.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#F0FDF4")));
            itemBinding.tvStatus.setTextColor(android.graphics.Color.parseColor("#16A34A"));
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
