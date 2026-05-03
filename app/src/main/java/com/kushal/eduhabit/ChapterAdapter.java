package com.kushal.eduhabit;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.kushal.eduhabit.databinding.ItemChapterExpandableBinding;
import java.util.List;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ViewHolder> {

    private List<Chapter> chapterList;
    private Context context;

    public ChapterAdapter(List<Chapter> chapterList) {
        this.chapterList = chapterList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        ItemChapterExpandableBinding binding = ItemChapterExpandableBinding.inflate(
                LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chapter chapter = chapterList.get(position);
        holder.binding.tvChapterTitle.setText(chapter.getTitle());
        holder.binding.tvTopics.setText(chapter.getTopicsCovered());
        holder.binding.tvWeightage.setText(chapter.getWeightage() + " Marks");
        holder.binding.tvStudyHours.setText(chapter.getStudyHours() + " Hours");
        
        holder.binding.tvNotes.setText(chapter.getNotes());
        holder.binding.tvExamples.setText(chapter.getExamples());
        
        // Show count of MCQs in the summary
        if (chapter.getMcqs() != null && !chapter.getMcqs().isEmpty()) {
            holder.binding.tvMCQs.setText(chapter.getMcqs().size() + " Practice Questions Available");
        } else {
            holder.binding.tvMCQs.setText("No MCQs available");
        }
        
        StringBuilder bqBuilder = new StringBuilder();
        if (chapter.getBoardQuestions() != null) {
            for (String q : chapter.getBoardQuestions()) {
                bqBuilder.append("• ").append(q).append("\n");
            }
        }
        holder.binding.tvBoardQuestions.setText(bqBuilder.toString().trim());

        // Handle Expansion
        boolean isExpanded = chapter.isExpanded();
        holder.binding.llExpandableContent.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.binding.ivExpandArrow.setRotation(isExpanded ? 180f : 0f);

        holder.binding.rlChapterHeader.setOnClickListener(v -> {
            chapter.setExpanded(!chapter.isExpanded());
            notifyItemChanged(position);
        });

        // Content Action (Open interactive reader)
        holder.binding.btnStudyNow.setOnClickListener(v -> {
            Intent intent = new Intent(context, GrammarLearningActivity.class);
            intent.putExtra("chapter", chapter);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return chapterList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemChapterExpandableBinding binding;

        public ViewHolder(ItemChapterExpandableBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
