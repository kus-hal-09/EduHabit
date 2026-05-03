package com.kushal.eduhabit.grammar;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.kushal.eduhabit.R;
import com.kushal.eduhabit.grammar.model.GrammarTopic;
import java.util.List;

public class GrammarTopicAdapter extends RecyclerView.Adapter<GrammarTopicAdapter.ViewHolder> {

    private List<GrammarTopic> topicList;
    private final OnTopicClickListener listener;

    public interface OnTopicClickListener {
        void onTopicClick(GrammarTopic topic);
    }

    public GrammarTopicAdapter(List<GrammarTopic> topicList, OnTopicClickListener listener) {
        this.topicList = topicList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grammar_topic, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GrammarTopic topic = topicList.get(position);
        holder.tvName.setText(topic.getName());
        holder.tvDesc.setText(topic.getDescription());
        holder.tvDifficulty.setText(topic.getDifficulty());
        holder.progress.setProgress(topic.getProgress());
        holder.tvLessonCount.setText(topic.getCompletedLessons() + " / " + topic.getTotalLessons() + " lessons");
        
        // Topic Click
        holder.itemView.setOnClickListener(v -> listener.onTopicClick(topic));
        holder.btnContinue.setOnClickListener(v -> listener.onTopicClick(topic));
    }

    @Override
    public int getItemCount() {
        return topicList.size();
    }

    public void updateList(List<GrammarTopic> newList) {
        this.topicList = newList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDesc, tvDifficulty, tvLessonCount;
        ImageView ivIcon;
        LinearProgressIndicator progress;
        MaterialButton btnContinue;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvTopicName);
            tvDesc = itemView.findViewById(R.id.tvTopicDesc);
            tvDifficulty = itemView.findViewById(R.id.tvDifficultyTag);
            tvLessonCount = itemView.findViewById(R.id.tvLessonCount);
            ivIcon = itemView.findViewById(R.id.ivTopicIcon);
            progress = itemView.findViewById(R.id.topicProgress);
            btnContinue = itemView.findViewById(R.id.btnContinue);
        }
    }
}
