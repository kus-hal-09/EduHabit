package com.kushal.eduhabit;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.kushal.eduhabit.databinding.ItemGrammarCardBinding;
import java.util.List;

public class GrammarPathAdapter extends RecyclerView.Adapter<GrammarPathAdapter.ViewHolder> {

    private final List<Lesson> lessons;
    private final OnLessonClickInterface listener;

    public interface OnLessonClickInterface {
        void onLessonClick(Lesson lesson);
    }

    public GrammarPathAdapter(List<Lesson> lessons, OnLessonClickInterface listener) {
        this.lessons = lessons;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemGrammarCardBinding binding = ItemGrammarCardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Lesson lesson = lessons.get(position);
        holder.binding.tvCardTitle.setText(lesson.title);
        holder.binding.tvCardDesc.setText("Master the foundations of " + lesson.title + ".");
        
        holder.itemView.setOnClickListener(v -> listener.onLessonClick(lesson));
    }

    @Override
    public int getItemCount() {
        return lessons != null ? lessons.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemGrammarCardBinding binding;
        public ViewHolder(ItemGrammarCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
