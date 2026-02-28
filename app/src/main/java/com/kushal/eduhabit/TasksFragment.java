package com.kushal.eduhabit;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.kushal.eduhabit.databinding.FragmentTasksBinding;
import com.kushal.eduhabit.databinding.ItemAssignmentBinding;
import com.kushal.eduhabit.databinding.ItemSubmissionBinding;
import java.util.HashSet;
import java.util.Set;

public class TasksFragment extends Fragment {

    private FragmentTasksBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Set<String> submittedAssignmentIds = new HashSet<>();

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

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                refreshList();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

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
        if (selectedTab == 0) fetchPendingAssignments();
        else fetchSubmittedAssignments();
    }

    private void fetchPendingAssignments() {
        db.collection("assignments").orderBy("createdAt", Query.Direction.DESCENDING).get()
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
        db.collection("submissions").whereEqualTo("studentId", mAuth.getUid()).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    binding.assignmentsContainer.removeAllViews();
                    if (queryDocumentSnapshots.isEmpty()) {
                        binding.tvEmptyState.setVisibility(View.VISIBLE);
                        binding.tvEmptyState.setText("No submitted assignments yet.");
                    } else {
                        binding.tvEmptyState.setVisibility(View.GONE);
                        for (DocumentSnapshot doc : queryDocumentSnapshots) addSubmissionView(doc);
                    }
                });
    }

    private void addAssignmentView(DocumentSnapshot doc) {
        ItemAssignmentBinding itemBinding = ItemAssignmentBinding.inflate(getLayoutInflater(), binding.assignmentsContainer, false);
        itemBinding.tvTitle.setText(doc.getString("title"));
        itemBinding.tvDueDate.setText("Due: " + doc.getString("dueDate"));
        itemBinding.tvDescription.setText(doc.getString("description"));
        itemBinding.btnSubmit.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SubmitAssignmentActivity.class);
            intent.putExtra("assignmentId", doc.getId());
            intent.putExtra("assignmentTitle", doc.getString("title"));
            startActivity(intent);
        });
        binding.assignmentsContainer.addView(itemBinding.getRoot());
    }

    private void addSubmissionView(DocumentSnapshot doc) {
        ItemSubmissionBinding itemBinding = ItemSubmissionBinding.inflate(getLayoutInflater(), binding.assignmentsContainer, false);
        itemBinding.tvTitle.setText(doc.getString("assignmentTitle"));
        itemBinding.tvStatus.setText(doc.getString("status"));
        if ("graded".equals(doc.getString("status"))) {
            itemBinding.tvGrade.setVisibility(View.VISIBLE);
            itemBinding.tvGrade.setText("Grade: " + doc.getString("grade") + "\nFeedback: " + doc.getString("feedback"));
        }
        itemBinding.btnAction.setVisibility(View.GONE);
        binding.assignmentsContainer.addView(itemBinding.getRoot());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}