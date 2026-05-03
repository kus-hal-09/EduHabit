package com.kushal.eduhabit;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.kushal.eduhabit.databinding.ItemWordCardBinding;
import java.util.List;

public class WordListAdapter extends RecyclerView.Adapter<WordListAdapter.ViewHolder> {

    private List<VocabularyModule.Word> words;

    public WordListAdapter(List<VocabularyModule.Word> words) {
        this.words = words;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemWordCardBinding binding = ItemWordCardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VocabularyModule.Word word = words.get(position);
        holder.binding.tvTerm.setText(word.term);
        holder.binding.tvDefinition.setText(word.definition);
        holder.binding.tvSentence.setText("\"" + word.sentence + "\"");
    }

    @Override
    public int getItemCount() {
        return words.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemWordCardBinding binding;
        public ViewHolder(ItemWordCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
