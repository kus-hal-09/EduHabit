package com.kushal.eduhabit;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.kushal.eduhabit.databinding.ItemActivityBinding;
import java.util.List;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ViewHolder> {

    private List<DocumentSnapshot> activities;

    public ActivityAdapter(List<DocumentSnapshot> activities) {
        this.activities = activities;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemActivityBinding binding = ItemActivityBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DocumentSnapshot doc = activities.get(position);
        
        // Handling dynamic data from submissions
        String studentEmail = doc.getString("studentEmail");
        String assignmentTitle = doc.getString("assignmentTitle");
        String status = doc.getString("status");
        Long timestamp = doc.getLong("submittedAt");

        holder.binding.tvActivityTitle.setText(studentEmail != null ? studentEmail.split("@")[0] : "Student");
        holder.binding.tvActivityDesc.setText("Submitted: " + (assignmentTitle != null ? assignmentTitle : "Assignment"));
        
        if (timestamp != null) {
            String timeAgo = (String) DateUtils.getRelativeTimeSpanString(timestamp, 
                    System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS);
            holder.binding.tvActivityTime.setText(timeAgo);
        }

        holder.binding.chipStatus.setText(status != null ? status.toUpperCase() : "PENDING");
        
        if ("graded".equals(status)) {
            holder.binding.chipStatus.setChipBackgroundColorResource(R.color.pastel_emerald);
            holder.binding.chipStatus.setTextColor(holder.itemView.getContext().getColor(R.color.accent_emerald));
        } else {
            holder.binding.chipStatus.setChipBackgroundColorResource(R.color.pastel_amber);
            holder.binding.chipStatus.setTextColor(holder.itemView.getContext().getColor(R.color.accent_amber));
        }
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemActivityBinding binding;
        public ViewHolder(ItemActivityBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
