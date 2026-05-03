package com.kushal.eduhabit;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.ViewHolder> implements Filterable {

    private List<Subject> subjectList;
    private List<Subject> subjectListFull;
    private Context context;

    public SubjectAdapter(Context context, List<Subject> subjectList) {
        this.context = context;
        this.subjectList = subjectList;
        this.subjectListFull = new ArrayList<>(subjectList);
    }

    public void updateList(List<Subject> newList) {
        this.subjectList = newList;
        this.subjectListFull = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subject_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Subject subject = subjectList.get(position);
        holder.tvName.setText(subject.getName());
        holder.tvCode.setText("Course Code: " + subject.getCode());
        holder.tvTypeTag.setText(subject.getType().toUpperCase());
        holder.tvCredits.setText(subject.getCredits() + " Credits");
        
        holder.pbProgress.setProgress(subject.getProgress());
        int completedCount = 0;
        if (subject.getSyllabus() != null) {
            for (Chapter c : subject.getSyllabus()) if (c.isCompleted()) completedCount++;
            holder.tvProgressText.setText(completedCount + "/" + subject.getSyllabus().size() + " Units");
        }

        holder.btnStart.setOnClickListener(v -> {
            Intent intent = new Intent(context, SubjectDetailActivity.class);
            intent.putExtra("subject_name", subject.getName());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return subjectList.size();
    }

    @Override
    public Filter getFilter() {
        return subjectFilter;
    }

    private Filter subjectFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Subject> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(subjectListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Subject item : subjectListFull) {
                    if (item.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void publishResults(CharSequence constraint, FilterResults results) {
            subjectList.clear();
            if (results.values != null) {
                subjectList.addAll((List<Subject>) results.values);
            }
            notifyDataSetChanged();
        }
    };

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvCode, tvTypeTag, tvCredits, tvProgressText;
        android.widget.ProgressBar pbProgress;
        View btnStart;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvSubjectName);
            tvCode = itemView.findViewById(R.id.tvSubjectCode);
            tvTypeTag = itemView.findViewById(R.id.tvTypeTag);
            tvCredits = itemView.findViewById(R.id.tvCredits);
            pbProgress = itemView.findViewById(R.id.pbSubjectProgress);
            tvProgressText = itemView.findViewById(R.id.tvProgressText);
            btnStart = itemView.findViewById(R.id.btnStartLearning);
        }
    }
}
