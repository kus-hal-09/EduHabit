package com.kushal.eduhabit;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.kushal.eduhabit.databinding.ItemLessonCurriculumBinding;
import java.util.List;

public class LessonCurriculumAdapter extends RecyclerView.Adapter<LessonCurriculumAdapter.ViewHolder> {

    private final List<Lesson> lessons;
    private final OnLessonClickListener listener;

    public interface OnLessonClickListener {
        void onLessonClicked(Lesson lesson);
    }

    public LessonCurriculumAdapter(List<Lesson> lessons, OnLessonClickListener listener) {
        this.lessons = lessons;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemLessonCurriculumBinding binding = ItemLessonCurriculumBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Lesson lesson = lessons.get(position);
        holder.binding.tvTitle.setText(lesson.title);
        holder.itemView.setOnClickListener(v -> listener.onLessonClicked(lesson));
    }

    @Override
    public int getItemCount() {
        return lessons.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemLessonCurriculumBinding binding;
        public ViewHolder(ItemLessonCurriculumBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
