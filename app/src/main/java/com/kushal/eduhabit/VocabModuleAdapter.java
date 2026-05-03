package com.kushal.eduhabit;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.kushal.eduhabit.databinding.ItemVocabModuleBinding;
import java.util.List;

public class VocabModuleAdapter extends RecyclerView.Adapter<VocabModuleAdapter.ViewHolder> {

    private List<VocabularyModule> modules;
    private OnModuleClickListener listener;

    public interface OnModuleClickListener {
        void onModuleClick(VocabularyModule module);
    }

    public VocabModuleAdapter(List<VocabularyModule> modules, OnModuleClickListener listener) {
        this.modules = modules;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemVocabModuleBinding binding = ItemVocabModuleBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VocabularyModule module = modules.get(position);
        holder.binding.tvModuleTitle.setText(module.moduleTitle);
        holder.binding.tvDifficulty.setText(module.difficulty);
        holder.binding.ivModuleIcon.setImageResource(module.iconResId);
        
        holder.itemView.setOnClickListener(v -> listener.onModuleClick(module));
    }

    @Override
    public int getItemCount() {
        return modules.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemVocabModuleBinding binding;
        public ViewHolder(ItemVocabModuleBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
