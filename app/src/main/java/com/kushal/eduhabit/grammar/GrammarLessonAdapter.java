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

public class GrammarLessonAdapter extends RecyclerView.Adapter<GrammarLessonAdapter.ViewHolder> {

    private final List<GrammarLesson> lessons;
    private final OnLessonClickListener listener;

    public interface OnLessonClickListener {
        void onLessonClick(GrammarLesson lesson);
    }

    public GrammarLessonAdapter(List<GrammarLesson> lessons, OnLessonClickListener listener) {
        this.lessons = lessons;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_grammar_lesson_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GrammarLesson lesson = lessons.get(position);
        holder.tvTitle.setText(lesson.getTitle());
        holder.tvDesc.setText(lesson.getDescription());
        holder.progressIndicator.setProgress(lesson.getProgress());
        
        holder.btnStart.setOnClickListener(v -> listener.onLessonClick(lesson));
    }

    @Override
    public int getItemCount() {
        return lessons.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc;
        LinearProgressIndicator progressIndicator;
        MaterialButton btnStart;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvLessonTitle);
            tvDesc = itemView.findViewById(R.id.tvLessonDesc);
            progressIndicator = itemView.findViewById(R.id.lessonProgress);
            btnStart = itemView.findViewById(R.id.btnStart);
        }
    }
}
