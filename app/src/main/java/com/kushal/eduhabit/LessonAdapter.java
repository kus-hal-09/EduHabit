package com.kushal.eduhabit;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.kushal.eduhabit.databinding.ItemLessonCardBinding;
import java.util.List;

public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.LessonViewHolder> {

    private List<Lesson> lessons;
    private OnLessonClickListener listener;
    private int activePosition = -1;

    public interface OnLessonClickListener {
        void onLessonClicked(Lesson lesson, int position);
    }

    public LessonAdapter(List<Lesson> lessons, OnLessonClickListener listener) {
        this.lessons = lessons;
        this.listener = listener;
    }

    public void setActivePosition(int position) {
        int oldPosition = activePosition;
        activePosition = position;
        notifyItemChanged(oldPosition);
        notifyItemChanged(activePosition);
    }

    @NonNull
    @Override
    public LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemLessonCardBinding binding = ItemLessonCardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new LessonViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull LessonViewHolder holder, int position) {
        Lesson lesson = lessons.get(position);
        holder.bind(lesson, position == activePosition);
        
        // Micro-interaction: Fade-in animation
        Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), android.R.anim.fade_in);
        holder.itemView.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return lessons.size();
    }

    class LessonViewHolder extends RecyclerView.ViewHolder {
        private ItemLessonCardBinding binding;

        public LessonViewHolder(ItemLessonCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Lesson lesson, boolean isActive) {
            binding.tvLessonTitle.setText(lesson.title);
            binding.tvLessonDuration.setText(lesson.duration);
            
            // Active State UI Polish: Subtle background tint and border
            if (isActive) {
                binding.getRoot().setStrokeWidth(4);
                binding.getRoot().setStrokeColor(Color.parseColor("#4F46E5"));
                binding.getRoot().setCardBackgroundColor(Color.parseColor("#F5F3FF"));
            } else {
                binding.getRoot().setStrokeWidth(0);
                binding.getRoot().setCardBackgroundColor(Color.WHITE);
            }

            // State Logic
            switch (lesson.state) {
                case "completed":
                    binding.ivStateBadge.setVisibility(View.VISIBLE);
                    binding.lessonProgress.setProgress(100);
                    binding.lessonProgress.setIndicatorColor(Color.parseColor("#10B981"));
                    binding.ivLock.setVisibility(View.GONE);
                    break;
                case "in_progress":
                    binding.ivStateBadge.setVisibility(View.GONE);
                    binding.lessonProgress.setVisibility(View.VISIBLE);
                    binding.lessonProgress.setProgress(lesson.progressValue);
                    binding.lessonProgress.setIndicatorColor(Color.parseColor("#4F46E5"));
                    binding.ivLock.setVisibility(View.GONE);
                    break;
                case "locked":
                    binding.ivStateBadge.setVisibility(View.GONE);
                    binding.lessonProgress.setVisibility(View.GONE);
                    binding.ivLock.setVisibility(View.VISIBLE);
                    binding.getRoot().setAlpha(0.6f);
                    break;
            }

            binding.getRoot().setOnClickListener(v -> {
                if (!lesson.state.equals("locked")) {
                    listener.onLessonClicked(lesson, getAdapterPosition());
                }
            });
        }
    }

    public static class Lesson {
        String title;
        String duration;
        String state; // "completed", "in_progress", "locked"
        int progressValue;

        public Lesson(String title, String duration, String state, int progressValue) {
            this.title = title;
            this.duration = duration;
            this.state = state;
            this.progressValue = progressValue;
        }
    }
}
