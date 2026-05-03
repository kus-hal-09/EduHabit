package com.kushal.eduhabit;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RadioButton;
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
    private boolean isAnswered = false;

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

    private int[] correctAnswers = {0, 0, 0, 0, 0}; 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGrammarPracticeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backBtn.setOnClickListener(v -> finish());

        practiceBinding = FragmentGrammarPracticeBinding.inflate(getLayoutInflater());
        theoryBinding = FragmentGrammarTheoryBinding.inflate(getLayoutInflater());
        progressBinding = FragmentGrammarProgressBinding.inflate(getLayoutInflater());

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
        isAnswered = false;
        practiceBinding.checkAnswerBtn.setText("Check Answer");
        
        if (currentQuestionIndex < questions.length) {
            practiceBinding.questionText.setText(questions[currentQuestionIndex]);
            
            resetOptionStyles();
            
            practiceBinding.option1.setText(options[currentQuestionIndex][0]);
            practiceBinding.option2.setText(options[currentQuestionIndex][1]);
            practiceBinding.option3.setText(options[currentQuestionIndex][2]);
            practiceBinding.option4.setText(options[currentQuestionIndex][3]);
            practiceBinding.optionsGroup.clearCheck();
            
            // Enable options
            for (int i = 0; i < practiceBinding.optionsGroup.getChildCount(); i++) {
                practiceBinding.optionsGroup.getChildAt(i).setEnabled(true);
            }

            binding.progressLabel.setText(currentQuestionIndex + " / " + questions.length);
            binding.progressBar.setProgress((currentQuestionIndex * 100) / questions.length);
            
            // Animation
            Animation slideIn = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
            practiceBinding.getRoot().startAnimation(slideIn);
            
        } else {
            Toast.makeText(this, "Practice Completed!", Toast.LENGTH_SHORT).show();
            showProgressTab();
        }
    }

    private void resetOptionStyles() {
        practiceBinding.option1.setBackgroundResource(R.drawable.option_bg_selector);
        practiceBinding.option2.setBackgroundResource(R.drawable.option_bg_selector);
        practiceBinding.option3.setBackgroundResource(R.drawable.option_bg_selector);
        practiceBinding.option4.setBackgroundResource(R.drawable.option_bg_selector);
    }

    private void setupPracticeLogic() {
        practiceBinding.checkAnswerBtn.setOnClickListener(v -> {
            if (isAnswered) {
                currentQuestionIndex++;
                loadQuestion();
                return;
            }

            int checkedId = practiceBinding.optionsGroup.getCheckedRadioButtonId();
            if (checkedId == -1) {
                Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show();
                return;
            }

            isAnswered = true;
            int selectedIndex = -1;
            RadioButton selectedOption = findViewById(checkedId);
            
            if (checkedId == practiceBinding.option1.getId()) selectedIndex = 0;
            else if (checkedId == practiceBinding.option2.getId()) selectedIndex = 1;
            else if (checkedId == practiceBinding.option3.getId()) selectedIndex = 2;
            else if (checkedId == practiceBinding.option4.getId()) selectedIndex = 3;

            // Disable options after selection
            for (int i = 0; i < practiceBinding.optionsGroup.getChildCount(); i++) {
                practiceBinding.optionsGroup.getChildAt(i).setEnabled(false);
            }

            int correctIndex = correctAnswers[currentQuestionIndex];
            
            if (selectedIndex == correctIndex) {
                score++;
                selectedOption.setBackgroundResource(R.drawable.option_correct);
                Toast.makeText(this, "Correct! ✨", Toast.LENGTH_SHORT).show();
            } else {
                selectedOption.setBackgroundResource(R.drawable.option_wrong);
                // Highlight correct one
                highlightCorrectAnswer(correctIndex);
                Toast.makeText(this, "Wrong Answer ❌", Toast.LENGTH_SHORT).show();
            }

            practiceBinding.checkAnswerBtn.setText("Next Question");
        });
    }

    private void highlightCorrectAnswer(int index) {
        if (index == 0) practiceBinding.option1.setBackgroundResource(R.drawable.option_correct);
        else if (index == 1) practiceBinding.option2.setBackgroundResource(R.drawable.option_correct);
        else if (index == 2) practiceBinding.option3.setBackgroundResource(R.drawable.option_correct);
        else if (index == 3) practiceBinding.option4.setBackgroundResource(R.drawable.option_correct);
    }

    private void updateProgressUI() {
        progressBinding.completedCount.setText(String.valueOf(score));
        int accuracy = (score * 100) / questions.length;
        progressBinding.accuracyRate.setText(accuracy + "%");
        if (accuracy >= 80) progressBinding.currentLevel.setText("Intermediate");
        else progressBinding.currentLevel.setText("Beginner");
    }
}