package com.kushal.eduhabit.grammar.view;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.kushal.eduhabit.databinding.ItemGrammarLessonBinding;
import com.kushal.eduhabit.grammar.model.GrammarLesson;
import java.util.List;

public class ModularGrammarAdapter extends RecyclerView.Adapter<ModularGrammarAdapter.ViewHolder> {

    private final List<GrammarLesson> lessons;
    private final OnLessonClickListener listener;

    public interface OnLessonClickListener {
        void onLessonClicked(GrammarLesson lesson);
    }

    public ModularGrammarAdapter(List<GrammarLesson> lessons, OnLessonClickListener listener) {
        this.lessons = lessons;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemGrammarLessonBinding binding = ItemGrammarLessonBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GrammarLesson lesson = lessons.get(position);
        holder.binding.tvLessonTitle.setText(lesson.getTitle());
        holder.binding.tvLessonDesc.setText(lesson.getDescription());
        holder.binding.lessonProgress.setProgress(lesson.getProgress());
        
        holder.itemView.setOnClickListener(v -> listener.onLessonClicked(lesson));
    }

    @Override
    public int getItemCount() {
        return lessons.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemGrammarLessonBinding binding;
        public ViewHolder(ItemGrammarLessonBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
