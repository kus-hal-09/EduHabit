package com.kushal.eduhabit;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.tabs.TabLayout;
import com.kushal.eduhabit.databinding.ActivityGrammarPracticeBinding;
import com.kushal.eduhabit.databinding.FragmentGrammarPracticeBinding;
import com.kushal.eduhabit.databinding.FragmentGrammarTheoryBinding;
import com.kushal.eduhabit.databinding.FragmentGrammarProgressBinding;

public class GrammarPracticeActivity extends AppCompatActivity {

    private ActivityGrammarPracticeBinding binding;
    private FragmentGrammarPracticeBinding practiceBinding;
    private FragmentGrammarTheoryBinding theoryBinding;
    private FragmentGrammarProgressBinding progressBinding;

    private int currentQuestionIndex = 0;
    private int score = 0;

    private String[] questions = {
            "She is coming to the party, _______?",
            "I _______ studied English for 3 years.",
            "They _______ seen that movie already.",
            "He _______ like coffee, does he?",
            "We _______ going to the park tomorrow."
    };

    private String[][] options = {
            {"isn't she", "is she", "doesn't she", "does she"},
            {"have", "has", "am", "was"},
            {"have", "has", "are", "were"},
            {"doesn't", "don't", "isn't", "hasn't"},
            {"are", "is", "am", "will"}
    };

    private int[] correctAnswers = {0, 0, 0, 0, 0}; // Indexes of correct options

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGrammarPracticeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backBtn.setOnClickListener(v -> finish());

        // Initialize Tab Views
        practiceBinding = FragmentGrammarPracticeBinding.inflate(getLayoutInflater());
        theoryBinding = FragmentGrammarTheoryBinding.inflate(getLayoutInflater());
        progressBinding = FragmentGrammarProgressBinding.inflate(getLayoutInflater());

        // Default tab
        showPracticeTab();

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: showPracticeTab(); break;
                    case 1: showTheoryTab(); break;
                    case 2: showProgressTab(); break;
                }
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        loadQuestion();
    }

    private void showPracticeTab() {
        binding.fragmentContainer.removeAllViews();
        binding.fragmentContainer.addView(practiceBinding.getRoot());
        setupPracticeLogic();
    }

    private void showTheoryTab() {
        binding.fragmentContainer.removeAllViews();
        binding.fragmentContainer.addView(theoryBinding.getRoot());
    }

    private void showProgressTab() {
        binding.fragmentContainer.removeAllViews();
        binding.fragmentContainer.addView(progressBinding.getRoot());
        updateProgressUI();
    }

    private void loadQuestion() {
        if (currentQuestionIndex < questions.length) {
            practiceBinding.questionText.setText(questions[currentQuestionIndex]);
            practiceBinding.option1.setText(options[currentQuestionIndex][0]);
            practiceBinding.option2.setText(options[currentQuestionIndex][1]);
            practiceBinding.option3.setText(options[currentQuestionIndex][2]);
            practiceBinding.option4.setText(options[currentQuestionIndex][3]);
            practiceBinding.optionsGroup.clearCheck();
            
            binding.progressLabel.setText(currentQuestionIndex + " / " + questions.length);
            binding.progressBar.setProgress((currentQuestionIndex * 100) / questions.length);
        } else {
            Toast.makeText(this, "Practice Completed! Score: " + score + "/5", Toast.LENGTH_LONG).show();
            updateProgressUI();
            showProgressTab();
        }
    }

    private void setupPracticeLogic() {
        practiceBinding.checkAnswerBtn.setOnClickListener(v -> {
            int checkedId = practiceBinding.optionsGroup.getCheckedRadioButtonId();
            if (checkedId == -1) {
                Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show();
                return;
            }

            int selectedIndex = -1;
            if (checkedId == practiceBinding.option1.getId()) selectedIndex = 0;
            else if (checkedId == practiceBinding.option2.getId()) selectedIndex = 1;
            else if (checkedId == practiceBinding.option3.getId()) selectedIndex = 2;
            else if (checkedId == practiceBinding.option4.getId()) selectedIndex = 3;

            if (selectedIndex == correctAnswers[currentQuestionIndex]) {
                score++;
                Toast.makeText(this, "Correct Answer! Well done!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Wrong Answer. The correct was: " + options[currentQuestionIndex][correctAnswers[currentQuestionIndex]], Toast.LENGTH_SHORT).show();
            }

            currentQuestionIndex++;
            loadQuestion();
        });
    }

    private void updateProgressUI() {
        progressBinding.completedCount.setText(String.valueOf(score));
        int accuracy = (score * 100) / questions.length;
        progressBinding.accuracyRate.setText(accuracy + "%");
        if (accuracy >= 80) progressBinding.currentLevel.setText("Intermediate");
        else progressBinding.currentLevel.setText("Beginner");
    }
}