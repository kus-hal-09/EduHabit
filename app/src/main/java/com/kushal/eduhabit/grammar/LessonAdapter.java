package com.kushal.eduhabit.grammar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.kushal.eduhabit.R;
import java.util.List;

public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.ViewHolder> {

    private final List<LessonModel> lessons;
    private final OnLessonClickListener listener;

    public interface OnLessonClickListener {
        void onStartQuiz(LessonModel lesson);
    }

    public LessonAdapter(List<LessonModel> lessons, OnLessonClickListener listener) {
        this.lessons = lessons;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lesson_card_isolated, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LessonModel lesson = lessons.get(position);
        holder.tvTitle.setText(lesson.getTitle());
        holder.tvDesc.setText(lesson.getDescription());
        holder.progressIndicator.setProgress(lesson.getProgress());
        holder.tvPercent.setText(lesson.getProgress() + "%");

        holder.btnStart.setOnClickListener(v -> listener.onStartQuiz(lesson));
    }

    @Override
    public int getItemCount() {
        return lessons.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc, tvPercent;
        LinearProgressIndicator progressIndicator;
        MaterialButton btnStart;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvLessonTitle);
            tvDesc = itemView.findViewById(R.id.tvLessonDesc);
            tvPercent = itemView.findViewById(R.id.tvProgressPercent);
            progressIndicator = itemView.findViewById(R.id.lessonProgress);
            btnStart = itemView.findViewById(R.id.btnStart);
        }
    }
}
