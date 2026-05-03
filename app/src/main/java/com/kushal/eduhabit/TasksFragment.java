package com.kushal.eduhabit;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.kushal.eduhabit.databinding.FragmentTasksBinding;
import com.kushal.eduhabit.databinding.ItemAssignmentModernBinding;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TasksFragment extends Fragment {

    private FragmentTasksBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private SessionManager session;
    private Map<String, DocumentSnapshot> userSubmissions = new HashMap<>();
    private List<DocumentSnapshot> allAssignments = new ArrayList<>();
    private ListenerRegistration submissionsListener, assignmentsListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTasksBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        session = new SessionManager(requireContext());

        binding.taskTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                loadList();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        binding.swipeRefresh.setOnRefreshListener(this::startRealTimeListeners);
        startRealTimeListeners();
    }

    private void startRealTimeListeners() {
        String uid = session.getUid();
        String studentCourse = session.getCourse();
        String studentSemester = session.getSemester();
        
        if (submissionsListener != null) submissionsListener.remove();
        if (assignmentsListener != null) assignmentsListener.remove();

        binding.swipeRefresh.setRefreshing(true);

        submissionsListener = db.collection("submissions")
                .whereEqualTo("studentId", uid)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        if (getContext() != null) binding.swipeRefresh.setRefreshing(false);
                        return;
                    }
                    userSubmissions.clear();
                    if (snapshots != null) {
                        for (DocumentSnapshot doc : snapshots.getDocuments()) {
                            userSubmissions.put(doc.getString("assignmentId"), doc);
                        }
                    }
                    loadList();
                });

        // --- STRICT SEMESTER FILTERING IN QUERY ---
        Query query = db.collection("assignments")
                .whereEqualTo("course", studentCourse)
                .whereEqualTo("semester", studentSemester) // Filter by student's semester
                .whereEqualTo("status", "active");

        assignmentsListener = query.orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        if (getContext() != null) binding.swipeRefresh.setRefreshing(false);
                        return;
                    }
                    allAssignments.clear();
                    if (snapshots != null) {
                        allAssignments.addAll(snapshots.getDocuments());
                    }
                    loadList();
                    if (getContext() != null) binding.swipeRefresh.setRefreshing(false);
                });
    }

    private void loadList() {
        if (binding == null) return;
        
        int selectedTab = binding.taskTabLayout.getSelectedTabPosition();
        binding.tasksContainer.removeAllViews();
        
        boolean found = false;
        for (DocumentSnapshot assignmentDoc : allAssignments) {
            DocumentSnapshot submissionDoc = userSubmissions.get(assignmentDoc.getId());
            
            String calculatedStatus;
            if (submissionDoc == null) {
                calculatedStatus = "pending";
            } else {
                String dbStatus = submissionDoc.getString("status");
                if ("graded".equals(dbStatus)) {
                    calculatedStatus = "graded";
                } else {
                    calculatedStatus = "submitted"; 
                }
            }

            if (selectedTab == 0 && "pending".equals(calculatedStatus)) {
                addCard(assignmentDoc, null, "pending");
                found = true;
            } else if (selectedTab == 1 && "submitted".equals(calculatedStatus)) {
                addCard(assignmentDoc, submissionDoc, "submitted");
                found = true;
            } else if (selectedTab == 2 && "graded".equals(calculatedStatus)) {
                addCard(assignmentDoc, submissionDoc, "graded");
                found = true;
            }
        }
        
        binding.llEmptyState.setVisibility(found ? View.GONE : View.VISIBLE);
    }

    private void addCard(DocumentSnapshot assignment, DocumentSnapshot submission, String status) {
        ItemAssignmentModernBinding itemBinding = ItemAssignmentModernBinding.inflate(getLayoutInflater(), binding.tasksContainer, false);
        
        itemBinding.tvTitle.setText(assignment.getString("title"));
        itemBinding.tvTeacher.setText("Module: " + assignment.getString("module")); 
        
        Date deadline = assignment.getDate("deadline");
        if (deadline != null) {
            itemBinding.tvDeadline.setText("Deadline: " + formatDate(deadline.getTime()));
        }

        if ("pending".equals(status)) {
            itemBinding.statusBadge.setCardBackgroundColor(android.graphics.Color.parseColor("#9CA3AF"));
            itemBinding.tvStatus.setText("⏳ Pending");
            itemBinding.btnSubmit.setVisibility(View.VISIBLE);
            itemBinding.btnSubmit.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), SubmitAssignmentActivity.class);
                intent.putExtra("assignmentId", assignment.getId());
                intent.putExtra("assignmentTitle", assignment.getString("title"));
                startActivity(intent);
            });
        } else if ("submitted".equals(status)) {
            itemBinding.statusBadge.setCardBackgroundColor(android.graphics.Color.parseColor("#3B82F6"));
            itemBinding.tvStatus.setText("📤 Submitted");
            itemBinding.llSubmissionInfo.setVisibility(View.VISIBLE);
            itemBinding.tvWaiting.setVisibility(View.VISIBLE);
            
            Object tsObj = submission.get("submittedAt");
            if (tsObj instanceof com.google.firebase.Timestamp) {
                itemBinding.tvSubmittedAt.setText("Submitted: " + formatDate(((com.google.firebase.Timestamp)tsObj).toDate().getTime()));
            }
            
            itemBinding.btnEdit.setVisibility(View.VISIBLE);
            itemBinding.btnCancel.setVisibility(View.VISIBLE);
            itemBinding.btnSpacer.setVisibility(View.VISIBLE);
            itemBinding.btnSubmit.setVisibility(View.GONE);

            itemBinding.btnCancel.setOnClickListener(v -> cancelSubmission(submission));
            itemBinding.btnEdit.setOnClickListener(v -> editSubmission(assignment, submission));

        } else if ("graded".equals(status)) {
            itemBinding.statusBadge.setCardBackgroundColor(android.graphics.Color.parseColor("#10B981"));
            itemBinding.tvStatus.setText("✅ Graded");
            itemBinding.llSubmissionInfo.setVisibility(View.VISIBLE);
            itemBinding.llGradingInfo.setVisibility(View.VISIBLE);
            
            Object tsObj = submission.get("submittedAt");
            if (tsObj instanceof com.google.firebase.Timestamp) {
                itemBinding.tvSubmittedAt.setText("Submitted: " + formatDate(((com.google.firebase.Timestamp)tsObj).toDate().getTime()));
            }
            itemBinding.tvGrade.setText("⭐ Grade: " + submission.getString("grade"));
            itemBinding.tvFeedback.setText("💬 " + submission.getString("feedback"));
            
            itemBinding.btnSubmit.setVisibility(View.GONE);
            itemBinding.btnEdit.setVisibility(View.GONE);
            itemBinding.btnCancel.setVisibility(View.GONE);
            itemBinding.btnSpacer.setVisibility(View.GONE);
            itemBinding.tvWaiting.setVisibility(View.GONE);
        }

        binding.tasksContainer.addView(itemBinding.getRoot());
    }

    private void cancelSubmission(DocumentSnapshot submission) {
        if ("graded".equals(submission.getString("status"))) {
            Toast.makeText(getContext(), "Cannot remove a graded assignment.", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(getContext())
                .setTitle("Cancel Submission")
                .setMessage("Are you sure you want to cancel this submission?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    db.collection("submissions").document(submission.getId()).delete()
                            .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Submission removed", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void editSubmission(DocumentSnapshot assignment, DocumentSnapshot submission) {
        if ("graded".equals(submission.getString("status"))) {
            Toast.makeText(getContext(), "Cannot edit a graded assignment.", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(getContext(), SubmitAssignmentActivity.class);
        intent.putExtra("assignmentId", assignment.getId());
        intent.putExtra("assignmentTitle", assignment.getString("title"));
        intent.putExtra("submissionId", submission.getId());
        intent.putExtra("existingContent", submission.getString("content"));
        startActivity(intent);
    }

    private String formatDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (submissionsListener != null) submissionsListener.remove();
        if (assignmentsListener != null) assignmentsListener.remove();
        binding = null;
    }
}
