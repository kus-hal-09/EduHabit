package com.kushal.eduhabit;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.kushal.eduhabit.databinding.ActivityVocabularyPracticeBinding;
import java.util.Random;

public class VocabularyPracticeActivity extends AppCompatActivity {

    private ActivityVocabularyPracticeBinding binding;
    private String[][] vocabularyData = {
            {"Benevolent", "Well meaning and kindly."},
            {"Ephemeral", "Lasting for a very short time."},
            {"Luminous", "Full of or shedding light."},
            {"Resilient", "Able to withstand or recover quickly from difficult conditions."},
            {"Vivid", "Producing powerful feelings or strong, clear images in the mind."}
    };
    private int currentIndex = 0;
    private boolean isShowingDefinition = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVocabularyPracticeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backBtn.setOnClickListener(v -> finish());

        updateFlashcard();

        binding.flashcard.setOnClickListener(v -> {
            isShowingDefinition = !isShowingDefinition;
            updateFlashcard();
        });

        binding.nextWordBtn.setOnClickListener(v -> {
            currentIndex = (currentIndex + 1) % vocabularyData.length;
            isShowingDefinition = false;
            updateFlashcard();
        });
    }

    private void updateFlashcard() {
        if (isShowingDefinition) {
            binding.wordText.setText(vocabularyData[currentIndex][1]);
            binding.wordText.setTextSize(18);
        } else {
            binding.wordText.setText(vocabularyData[currentIndex][0]);
            binding.wordText.setTextSize(34);
        }
    }
}