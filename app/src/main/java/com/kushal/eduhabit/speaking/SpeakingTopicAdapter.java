package com.kushal.eduhabit.speaking;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.kushal.eduhabit.R;
import java.util.List;

public class SpeakingTopicAdapter extends RecyclerView.Adapter<SpeakingTopicAdapter.ViewHolder> {

    private final List<SpeakingTopic> topics;
    private final OnTopicClickListener listener;

    public interface OnTopicClickListener {
        void onTopicClick(SpeakingTopic topic);
    }

    public SpeakingTopicAdapter(List<SpeakingTopic> topics, OnTopicClickListener listener) {
        this.topics = topics;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_speaking_topic, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SpeakingTopic topic = topics.get(position);
        holder.tvTitle.setText(topic.title);
        holder.tvDesc.setText(topic.description);
        holder.tvDifficulty.setText(topic.difficulty);
        holder.ivIcon.setImageResource(topic.iconResId);
        
        holder.btnStart.setOnClickListener(v -> listener.onTopicClick(topic));
        holder.itemView.setOnClickListener(v -> listener.onTopicClick(topic));
    }

    @Override
    public int getItemCount() {
        return topics.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc, tvDifficulty;
        ImageView ivIcon;
        MaterialButton btnStart;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTopicTitle);
            tvDesc = itemView.findViewById(R.id.tvTopicDesc);
            tvDifficulty = itemView.findViewById(R.id.tvDifficultyTag);
            ivIcon = itemView.findViewById(R.id.ivTopicIcon);
            btnStart = itemView.findViewById(R.id.btnStartSpeaking);
        }
    }
}
