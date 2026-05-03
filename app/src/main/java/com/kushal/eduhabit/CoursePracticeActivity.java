package com.kushal.eduhabit;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RadioButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kushal.eduhabit.databinding.ActivityGrammarPracticeBinding;
import com.kushal.eduhabit.databinding.FragmentGrammarPracticeBinding;
import java.util.HashMap;
import java.util.Map;

public class CoursePracticeActivity extends AppCompatActivity {

    private ActivityGrammarPracticeBinding binding;
    private FragmentGrammarPracticeBinding practiceBinding;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String courseName;

    private int currentQuestionIndex = 0;
    private int score = 0;
    private boolean isAnswered = false;

    private String[] questions;
    private String[][] options;
    private int[] correctAnswers;

    // SaaS-Level Practice Data for BCA 6th Semester
    private static final Map<String, String[]> QUESTIONS_MAP = new HashMap<>();
    private static final Map<String, String[][]> OPTIONS_MAP = new HashMap<>();
    private static final Map<String, int[]> ANSWERS_MAP = new HashMap<>();

    static {
        // .NET Technology Practice Data
        QUESTIONS_MAP.put(".NET Technology", new String[]{
            "Which component of .NET framework is responsible for converting MSIL to machine code?",
            "What is the root class of all types in .NET?",
            "In C#, which keyword is used to prevent a class from being inherited?",
            "Which ADO.NET object provides a disconnected recordset?",
            "What is the default access modifier for a class in C#?"
        });
        OPTIONS_MAP.put(".NET Technology", new String[][]{
            {"CLR", "JIT Compiler", "CTS", "CLS"},
            {"System.Base", "System.Object", "System.Root", "System.Type"},
            {"static", "abstract", "sealed", "final"},
            {"DataReader", "DataAdapter", "DataSet", "Command"},
            {"public", "private", "internal", "protected"}
        });
        ANSWERS_MAP.put(".NET Technology", new int[]{1, 1, 2, 2, 2});

        // AI Practice Data
        QUESTIONS_MAP.put("Artificial Intelligence", new String[]{
            "Who is considered the father of Artificial Intelligence?",
            "Which search algorithm is optimal and complete if heuristics are admissible?",
            "What is the Turing Test designed to determine?",
            "Which agent type chooses actions based on current percepts only?",
            "In AI, what does 'BFS' stand for?"
        });
        OPTIONS_MAP.put("Artificial Intelligence", new String[][]{
            {"Alan Turing", "John McCarthy", "Elon Musk", "Andrew Ng"},
            {"BFS", "DFS", "A* Search", "Hill Climbing"},
            {"Human-like intelligence", "Memory capacity", "Processing speed", "Network security"},
            {"Goal-based", "Model-based", "Simple reflex", "Utility-based"},
            {"Best First Search", "Breadth First Search", "Basic Field Search", "Binary File System"}
        });
        ANSWERS_MAP.put("Artificial Intelligence", new int[]{1, 2, 0, 2, 1});
        
        // E-Commerce Practice Data
        QUESTIONS_MAP.put("E-Commerce", new String[]{
            "Which model involves business selling directly to consumers?",
            "What is the full form of 'EDI' in e-commerce?",
            "Which protocol is widely used for secure online payments?",
            "A 'Digital Signature' provides which security service?",
            "What is 'B2B' e-commerce?"
        });
        OPTIONS_MAP.put("E-Commerce", new String[][]{
            {"B2B", "C2C", "B2C", "G2C"},
            {"Electronic Data Interchange", "Electronic Digital Information", "Easy Data Integration", "External Data Interface"},
            {"HTTP", "FTP", "SSL/TLS", "SMTP"},
            {"Privacy", "Non-repudiation", "Efficiency", "Storage"},
            {"Business to Business", "Business to Buyer", "Back to Business", "Binary to Business"}
        });
        ANSWERS_MAP.put("E-Commerce", new int[]{2, 0, 2, 1, 0});
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGrammarPracticeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        
        // Handling both Unit titles and Subject names
        String rawName = getIntent().getStringExtra("courseName");
        courseName = normalizeCourseName(rawName);

        setupData();

        binding.backBtn.setOnClickListener(v -> finish());
        binding.title.setText(courseName + " Practice");

        practiceBinding = FragmentGrammarPracticeBinding.inflate(getLayoutInflater());
        binding.fragmentContainer.addView(practiceBinding.getRoot());

        loadQuestion();
        setupLogic();
    }

    private String normalizeCourseName(String name) {
        if (name == null) return ".NET Technology";
        if (name.contains(".NET")) return ".NET Technology";
        if (name.contains("AI") || name.contains("Artificial")) return "Artificial Intelligence";
        if (name.contains("E-Commerce")) return "E-Commerce";
        return ".NET Technology";
    }

    private void setupData() {
        if (courseName != null && QUESTIONS_MAP.containsKey(courseName)) {
            questions = QUESTIONS_MAP.get(courseName);
            options = OPTIONS_MAP.get(courseName);
            correctAnswers = ANSWERS_MAP.get(courseName);
        } else {
            // Default to .NET if context is unclear
            questions = QUESTIONS_MAP.get(".NET Technology");
            options = OPTIONS_MAP.get(".NET Technology");
            correctAnswers = ANSWERS_MAP.get(".NET Technology");
        }
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
            
            for (int i = 0; i < practiceBinding.optionsGroup.getChildCount(); i++) {
                practiceBinding.optionsGroup.getChildAt(i).setEnabled(true);
            }

            binding.progressLabel.setText(currentQuestionIndex + " / " + questions.length);
            binding.progressBar.setProgress((currentQuestionIndex * 100) / questions.length);
            
            Animation slideIn = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
            practiceBinding.getRoot().startAnimation(slideIn);
        } else {
            completeSession();
        }
    }

    private void resetOptionStyles() {
        practiceBinding.option1.setBackgroundResource(R.drawable.option_bg_selector);
        practiceBinding.option2.setBackgroundResource(R.drawable.option_bg_selector);
        practiceBinding.option3.setBackgroundResource(R.drawable.option_bg_selector);
        practiceBinding.option4.setBackgroundResource(R.drawable.option_bg_selector);
    }

    private void setupLogic() {
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

            for (int i = 0; i < practiceBinding.optionsGroup.getChildCount(); i++) {
                practiceBinding.optionsGroup.getChildAt(i).setEnabled(false);
            }

            int correctIndex = correctAnswers[currentQuestionIndex];
            if (selectedIndex == correctIndex) {
                score++;
                selectedOption.setBackgroundResource(R.drawable.option_correct);
                addXp(10);
            } else {
                selectedOption.setBackgroundResource(R.drawable.option_wrong);
                highlightCorrectAnswer(correctIndex);
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

    private void addXp(int amount) {
        if (mAuth.getCurrentUser() != null) {
            db.collection("users").document(mAuth.getUid()).update("xp", FieldValue.increment(amount));
        }
    }

    private void completeSession() {
        Toast.makeText(this, "Session Completed! Accuracy: " + (score * 100 / questions.length) + "%", Toast.LENGTH_LONG).show();
        finish();
    }
}