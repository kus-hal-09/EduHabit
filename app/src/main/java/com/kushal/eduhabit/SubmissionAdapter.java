package com.kushal.eduhabit;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.kushal.eduhabit.databinding.ItemSubmissionModernBinding;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SubmissionAdapter extends RecyclerView.Adapter<SubmissionAdapter.ViewHolder> {

    private List<Submission> submissions;
    private List<Submission> submissionsFull;
    private Context context;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault());

    public SubmissionAdapter(List<Submission> submissions) {
        this.submissions = submissions;
        this.submissionsFull = new ArrayList<>(submissions);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ItemSubmissionModernBinding binding = ItemSubmissionModernBinding.inflate(
                LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Submission submission = submissions.get(position);
        
        // Professional fallback: Use email prefix if name is missing
        String displayName = submission.getStudentName();
        if (displayName == null || displayName.isEmpty() || displayName.equalsIgnoreCase("Unknown Student")) {
            String email = submission.getStudentEmail();
            if (email != null && email.contains("@")) {
                displayName = email.split("@")[0];
            } else {
                displayName = "Student";
            }
        }
        holder.binding.tvStudentName.setText(displayName);
        
        holder.binding.tvStudentEmail.setText(submission.getStudentEmail());
        holder.binding.tvAssignmentTitle.setText(submission.getAssignmentTitle());
        
        if (submission.getSubmittedAt() != null) {
            holder.binding.tvTimestamp.setText("Submitted on " + dateFormat.format(submission.getSubmittedDate()));
        }

        if ("graded".equals(submission.getStatus())) {
            holder.binding.chipStatus.setText("GRADED");
            holder.binding.chipStatus.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#10B981")));
            holder.binding.tvGrade.setVisibility(View.VISIBLE);
            holder.binding.tvGrade.setText(submission.getGrade());
            holder.binding.btnAction.setText("View Feedback");
        } else {
            // Updated: Changed label to "PENDING REVIEW" as requested
            holder.binding.chipStatus.setText("PENDING REVIEW");
            holder.binding.chipStatus.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#F59E0B")));
            holder.binding.tvGrade.setVisibility(View.GONE);
            holder.binding.btnAction.setText("Grade Submission");
        }

        holder.binding.btnAction.setOnClickListener(v -> {
            Intent intent = new Intent(context, ReviewSubmissionActivity.class);
            intent.putExtra("submissionId", submission.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return submissions.size();
    }

    public void updateList(List<Submission> newList) {
        this.submissions = newList;
        this.submissionsFull = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    public void filter(String query, String status) {
        List<Submission> filteredList = new ArrayList<>();
        for (Submission item : submissionsFull) {
            boolean matchesQuery = query.isEmpty() || 
                    item.getStudentName().toLowerCase().contains(query.toLowerCase()) ||
                    item.getAssignmentTitle().toLowerCase().contains(query.toLowerCase());
            
            // Sync filter logic with the "submitted" status
            boolean matchesStatus = status.equals("All") || item.getStatus().equalsIgnoreCase(status);

            if (matchesQuery && matchesStatus) {
                filteredList.add(item);
            }
        }
        submissions = filteredList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemSubmissionModernBinding binding;
        public ViewHolder(ItemSubmissionModernBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
