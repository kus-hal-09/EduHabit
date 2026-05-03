package com.kushal.eduhabit;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kushal.eduhabit.databinding.ActivityGrammarPracticeBinding;
import com.kushal.eduhabit.databinding.FragmentGrammarPracticeBinding;

public class IELTSPreparationActivity extends AppCompatActivity {
    private ActivityGrammarPracticeBinding binding;
    private FragmentGrammarPracticeBinding practiceBinding;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private boolean isAnswered = false;

    private String[] questions = {
        "What is the total duration of the IELTS Listening test?",
        "How many tasks are in the IELTS Academic Writing test?",
        "Which of these is NOT a section in the IELTS Speaking test?",
        "What does 'Skimming' refer to in the Reading test?",
        "What is the highest possible band score in IELTS?"
    };

    private String[][] options = {
        {"30 minutes", "60 minutes", "15 minutes", "2 hours"},
        {"1 Task", "2 Tasks", "3 Tasks", "4 Tasks"},
        {"Introduction", "Individual long turn", "Two-way discussion", "Group discussion"},
        {"Reading for detail", "Reading for general idea", "Copying words", "Memorizing text"},
        {"Band 7", "Band 8", "Band 9", "Band 10"}
    };

    private int[] correctAnswers = {0, 1, 3, 1, 2};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGrammarPracticeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.title.setText("IELTS Prep Practice");
        binding.backBtn.setOnClickListener(v -> finish());

        practiceBinding = FragmentGrammarPracticeBinding.inflate(getLayoutInflater());
        binding.fragmentContainer.addView(practiceBinding.getRoot());

        loadQuestion();
        setupLogic();
    }

    private void loadQuestion() {
        isAnswered = false;
        practiceBinding.checkAnswerBtn.setText("Check Answer");
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
            Toast.makeText(this, "IELTS Skills Confirmed!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupLogic() {
        practiceBinding.checkAnswerBtn.setOnClickListener(v -> {
            if (isAnswered) {
                currentQuestionIndex++;
                loadQuestion();
                return;
            }
            int checkedId = practiceBinding.optionsGroup.getCheckedRadioButtonId();
            if (checkedId == -1) return;
            isAnswered = true;
            RadioButton selected = findViewById(checkedId);
            int selectedIndex = practiceBinding.optionsGroup.indexOfChild(selected);
            if (selectedIndex == correctAnswers[currentQuestionIndex]) {
                score++;
                selected.setBackgroundResource(R.drawable.option_correct);
                FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getUid()).update("xp", FieldValue.increment(15));
            } else {
                selected.setBackgroundResource(R.drawable.option_wrong);
            }
            practiceBinding.checkAnswerBtn.setText("Next Question");
        });
    }
}