package com.kushal.eduhabit;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.kushal.eduhabit.databinding.ItemNotificationBinding;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<DocumentSnapshot> notifications;
    private OnNotificationClickListener listener;

    public interface OnNotificationClickListener {
        void onNotificationClick(DocumentSnapshot doc);
    }

    public NotificationAdapter(List<DocumentSnapshot> notifications, OnNotificationClickListener listener) {
        this.notifications = notifications;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNotificationBinding binding = ItemNotificationBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DocumentSnapshot doc = notifications.get(position);
        holder.binding.tvNotifTitle.setText(doc.getString("title"));
        holder.binding.tvNotifMessage.setText(doc.getString("message"));
        
        Long timestamp = doc.getLong("timestamp");
        if (timestamp != null) {
            String timeAgo = (String) DateUtils.getRelativeTimeSpanString(timestamp, 
                    System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS);
            holder.binding.tvNotifTime.setText(timeAgo);
        }

        boolean read = Boolean.TRUE.equals(doc.getBoolean("read"));
        holder.binding.viewUnreadIndicator.setVisibility(read ? View.GONE : View.VISIBLE);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onNotificationClick(doc);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemNotificationBinding binding;
        public ViewHolder(ItemNotificationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}