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

public class WritingMasterActivity extends AppCompatActivity {
    private ActivityGrammarPracticeBinding binding;
    private FragmentGrammarPracticeBinding practiceBinding;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private boolean isAnswered = false;

    private String[] questions = {
        "What is the main purpose of a 'Thesis Statement'?",
        "Which of these is a formal 'Transition Word' for adding information?",
        "What does 'Paraphrasing' mean?",
        "In academic writing, which person should you generally avoid using?",
        "What is the role of a 'Topic Sentence'?"
    };

    private String[][] options = {
        {"To introduce the author", "To state the main argument", "To list references", "To conclude the essay"},
        {"Furthermore", "And", "Also", "Plus"},
        {"Copying word for word", "Rewriting in your own words", "Summarizing a whole book", "Translating to another language"},
        {"First person (I)", "Third person (He/She)", "Both", "None"},
        {"To start a new page", "To state the main idea of a paragraph", "To finish a paragraph", "To check spelling"}
    };

    private int[] correctAnswers = {1, 0, 1, 0, 1};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGrammarPracticeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.title.setText("Writing Masterclass Practice");
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
            Toast.makeText(this, "Writing Skills Mastered!", Toast.LENGTH_SHORT).show();
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
                FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getUid()).update("xp", FieldValue.increment(20));
            } else {
                selected.setBackgroundResource(R.drawable.option_wrong);
            }
            practiceBinding.checkAnswerBtn.setText("Next Question");
        });
    }
}