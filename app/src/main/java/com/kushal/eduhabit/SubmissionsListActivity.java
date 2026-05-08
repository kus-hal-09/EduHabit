package com.kushal.eduhabit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kushal.eduhabit.databinding.ActivitySubmissionsListBinding;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class SubmissionsListActivity extends AppCompatActivity {

    private ActivitySubmissionsListBinding binding;
    private FirebaseFirestore db;
    private SessionManager session;
    private SubmissionAdapter adapter;
    private List<Submission> submissionList = new ArrayList<>();
    private String currentFilter = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubmissionsListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        session = new SessionManager(this);

        setupUI();
        setupBottomNav();
        fetchSubmissions();
    }

    private void setupUI() {
        binding.backBtn.setOnClickListener(v -> finish());

        // Setup RecyclerView
        adapter = new SubmissionAdapter(submissionList);
        binding.rvSubmissions.setLayoutManager(new LinearLayoutManager(this));
        binding.rvSubmissions.setAdapter(adapter);

        // Swipe Refresh
        binding.swipeRefresh.setOnRefreshListener(this::fetchSubmissions);

        // Search functionality
        binding.searchSubmissions.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filter(query, currentFilter);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filter(newText, currentFilter);
                return true;
            }
        });

        // Filter functionality
        binding.filterChipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            if (id == R.id.chipAll) {
                currentFilter = "All";
            } else if (id == R.id.chipSubmitted) {
                currentFilter = "submitted";
            } else if (id == R.id.chipGraded) {
                currentFilter = "graded";
            }
            adapter.filter(binding.searchSubmissions.getQuery().toString(), currentFilter);
        });
    }

    private void fetchSubmissions() {
        binding.loadingIndicator.setVisibility(View.VISIBLE);
        String teacherCourse = session.getCourse();
        String teacherSemester = session.getSemester();

        // Optimized: Removed .orderBy() to avoid index requirements (fixing FAILED_PRECONDITION)
        // We will sort locally in the success listener
        db.collection("submissions")
                .whereEqualTo("course", teacherCourse)
                .whereEqualTo("semester", teacherSemester)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    binding.loadingIndicator.setVisibility(View.GONE);
                    binding.swipeRefresh.setRefreshing(false);
                    submissionList.clear();
                    
                    if (queryDocumentSnapshots.isEmpty()) {
                        binding.tvEmptyState.setVisibility(View.VISIBLE);
                        adapter.updateList(submissionList);
                    } else {
                        binding.tvEmptyState.setVisibility(View.GONE);
                        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                            Submission submission = doc.toObject(Submission.class);
                            if (submission != null) {
                                submission.setId(doc.getId());
                                standardizeSubmission(submission);
                                submissionList.add(submission);
                            }
                        }
                        
                        // Sort locally by submission date descending
                        Collections.sort(submissionList, (s1, s2) -> {
                            Date d1 = s1.getSubmittedDate();
                            Date d2 = s2.getSubmittedDate();
                            return d2.compareTo(d1);
                        });
                        
                        adapter.updateList(submissionList);
                    }
                })
                .addOnFailureListener(e -> {
                    binding.loadingIndicator.setVisibility(View.GONE);
                    binding.swipeRefresh.setRefreshing(false);
                    Toast.makeText(this, "Fetch failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void standardizeSubmission(Submission s) {
        if (s.getAssignmentTitle() != null) {
            String title = s.getAssignmentTitle();
            title = title.replace("chapter", "Chapter").replace("lesson", "Lesson");
            s.setAssignmentTitle(title);
        }
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

    @Override
    protected void onResume() {
        super.onResume();
        fetchSubmissions();
    }
}
