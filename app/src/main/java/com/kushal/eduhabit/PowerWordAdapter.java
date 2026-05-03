package com.kushal.eduhabit;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.kushal.eduhabit.databinding.ItemWordCardBinding;
import java.util.List;

public class PowerWordAdapter extends RecyclerView.Adapter<PowerWordAdapter.ViewHolder> {

    private List<VocabularyModule.Word> words;
    private OnWordActionListener listener;

    public interface OnWordActionListener {
        void onPronounce(String text);
        void onMastered(VocabularyModule.Word word);
    }

    public PowerWordAdapter(List<VocabularyModule.Word> words, OnWordActionListener listener) {
        this.words = words;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemWordCardBinding binding = ItemWordCardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        
        // Robustly set width for horizontal scrolling cards
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) binding.getRoot().getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new RecyclerView.LayoutParams(
                    (int) (parent.getWidth() * 0.85),
                    RecyclerView.LayoutParams.WRAP_CONTENT
            );
        } else {
            layoutParams.width = (int) (parent.getWidth() * 0.85);
        }
        
        // Safety check for width
        if (layoutParams.width <= 0) {
            layoutParams.width = 800; // Default fallback width
        }
        
        binding.getRoot().setLayoutParams(layoutParams);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (words == null || position >= words.size()) return;
        
        VocabularyModule.Word word = words.get(position);
        holder.binding.tvTerm.setText(word.term);
        holder.binding.tvDefinition.setText(word.definition);
        holder.binding.tvSentence.setText("\"" + word.sentence + "\"");
        
        holder.binding.btnPronounce.setOnClickListener(v -> {
            if (listener != null) listener.onPronounce(word.term);
        });

        holder.binding.btnFlashcard.setOnClickListener(v -> {
            if (listener != null) listener.onMastered(word);
        });
    }

    @Override
    public int getItemCount() {
        return words != null ? words.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemWordCardBinding binding;
        public ViewHolder(ItemWordCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
