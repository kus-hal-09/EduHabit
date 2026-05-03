package com.kushal.eduhabit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kushal.eduhabit.databinding.ActivityPracticeExerciseBinding;
import com.kushal.eduhabit.grammar.data.GrammarRepository;
import com.kushal.eduhabit.grammar.model.GrammarTopic;
import com.kushal.eduhabit.grammar.model.QuizQuestion;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class PracticeExerciseActivity extends AppCompatActivity {

    private ActivityPracticeExerciseBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private List<QuizQuestion> questions;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private boolean isAnswerChecked = false;
    private String levelName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPracticeExerciseBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        levelName = getIntent().getStringExtra("level_name");
        GrammarTopic topic = (GrammarTopic) getIntent().getSerializableExtra("topic");

        if (levelName == null && topic != null) levelName = topic.getName();
        if (levelName == null) levelName = "Beginner";

        checkDailyCompletion();
    }

    private void checkDailyCompletion() {
        if (mAuth.getCurrentUser() == null) return;
        
        db.collection("users").document(mAuth.getUid()).get()
            .addOnSuccessListener(snapshot -> {
                Long lastDate = snapshot.getLong("lastPracticeDate_" + levelName.toLowerCase());
                if (lastDate != null && isSameDay(lastDate, System.currentTimeMillis())) {
                    showDailyCompletedDialog();
                } else {
                    initializeSession();
                }
            })
            .addOnFailureListener(e -> initializeSession());
    }

    private boolean isSameDay(long t1, long t2) {
        Calendar cal1 = Calendar.getInstance(); cal1.setTimeInMillis(t1);
        Calendar cal2 = Calendar.getInstance(); cal2.setTimeInMillis(t2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    private void showDailyCompletedDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Daily Goal Reached! ✅")
                .setMessage("You have already completed the " + levelName + " practice for today. Fresh questions will be available tomorrow!")
                .setPositiveButton("Go Back", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    private void initializeSession() {
        setupUI();
        loadDailyQuestions();
        if (questions.isEmpty()) {
            Toast.makeText(this, "No questions found.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        displayQuestion();
    }

    private void setupUI() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
        binding.tvPracticeTitle.setText(levelName + " Practice");
        binding.btnSubmit.setOnClickListener(v -> { if (!isAnswerChecked) checkAnswer(); });
        binding.btnNext.setOnClickListener(v -> {
            currentQuestionIndex++;
            if (currentQuestionIndex < questions.size()) displayQuestion();
            else finalizeSession();
        });
    }

    private void loadDailyQuestions() {
        List<QuizQuestion> all = GrammarRepository.getQuestionsForLevel(levelName);
        if (all.isEmpty()) {
            all = new ArrayList<>();
            all.add(new QuizQuestion("She ___ to school every day.", List.of("go", "goes", "gone", "going"), 1, "Habitual action."));
            all.add(new QuizQuestion("I ___ seen that movie.", List.of("am", "have", "is", "did"), 1, "Present perfect."));
        }
        
        // STABLE DAILY SHUFFLE: Use current day of year as seed
        long seed = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        Collections.shuffle(all, new Random(seed));
        
        // Limit to 5 questions per session
        questions = all.subList(0, Math.min(all.size(), 5));
    }

    private void displayQuestion() {
        isAnswerChecked = false;
        binding.btnSubmit.setVisibility(View.VISIBLE);
        binding.btnSubmit.setEnabled(true);
        binding.btnNext.setVisibility(View.GONE);
        binding.llResultContainer.setVisibility(View.GONE);
        binding.rgOptions.clearCheck();
        binding.rgOptions.removeAllViews();

        QuizQuestion q = questions.get(currentQuestionIndex);
        binding.tvQuestionText.setText(q.getQuestion());
        binding.tvQuestionProgress.setText("STEP " + (currentQuestionIndex + 1) + " OF " + questions.size());
        
        int progress = (int) (((float) (currentQuestionIndex + 1) / questions.size()) * 100);
        binding.practiceProgress.setProgress(progress);

        for (int i = 0; i < q.getOptions().size(); i++) {
            RadioButton rb = (RadioButton) getLayoutInflater().inflate(R.layout.layout_quiz_option, binding.rgOptions, false);
            rb.setText(q.getOptions().get(i));
            rb.setId(i);
            binding.rgOptions.addView(rb);
        }
    }

    private void checkAnswer() {
        int checkedId = binding.rgOptions.getCheckedRadioButtonId();
        if (checkedId == -1) { Toast.makeText(this, "Select an answer!", Toast.LENGTH_SHORT).show(); return; }

        isAnswerChecked = true;
        QuizQuestion q = questions.get(currentQuestionIndex);
        for (int i = 0; i < binding.rgOptions.getChildCount(); i++) binding.rgOptions.getChildAt(i).setEnabled(false);

        if (checkedId == q.getCorrectIndex()) {
            score++;
            binding.rgOptions.getChildAt(checkedId).setBackgroundResource(R.drawable.option_correct);
            binding.tvResultStatus.setText("Correct! ✨");
            binding.tvResultStatus.setTextColor(ContextCompat.getColor(this, R.color.success_green));
        } else {
            binding.rgOptions.getChildAt(checkedId).setBackgroundResource(R.drawable.option_wrong);
            binding.rgOptions.getChildAt(q.getCorrectIndex()).setBackgroundResource(R.drawable.option_correct);
            binding.tvResultStatus.setText("Incorrect ❌");
            binding.tvResultStatus.setTextColor(ContextCompat.getColor(this, R.color.error_red));
        }

        binding.tvExplanationText.setText(q.getExplanation());
        binding.llResultContainer.setVisibility(View.VISIBLE);
        binding.btnSubmit.setVisibility(View.GONE);
        binding.btnNext.setVisibility(View.VISIBLE);
    }

    private void finalizeSession() {
        if (mAuth.getCurrentUser() == null) return;
        String uid = mAuth.getUid();
        int xpEarned = score * 10;
        DocumentReference userRef = db.collection("users").document(uid);
        
        db.runTransaction(transaction -> {
            DocumentSnapshot snapshot = transaction.get(userRef);
            long currentXP = snapshot.getLong("xp") != null ? snapshot.getLong("xp") : 0;
            long currentStreak = snapshot.getLong("streak") != null ? snapshot.getLong("streak") : 0;
            Long lastPracticeDateGlobal = snapshot.getLong("lastPracticeDate");

            Calendar cal = Calendar.getInstance();
            long now = cal.getTimeInMillis();
            cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0);
            long todayStart = cal.getTimeInMillis();
            cal.add(Calendar.DATE, -1); long yesterdayStart = cal.getTimeInMillis();

            long newStreak = currentStreak;
            if (lastPracticeDateGlobal == null || lastPracticeDateGlobal < yesterdayStart) newStreak = 1;
            else if (lastPracticeDateGlobal >= yesterdayStart && lastPracticeDateGlobal < todayStart) newStreak = currentStreak + 1;

            transaction.update(userRef, "xp", currentXP + xpEarned);
            transaction.update(userRef, "streak", newStreak);
            transaction.update(userRef, "lastPracticeDate", now);
            // Track completion for this specific level
            transaction.update(userRef, "lastPracticeDate_" + levelName.toLowerCase(), now);

            return null;
        }).addOnSuccessListener(aVoid -> {
            showFinalResults(xpEarned);
            saveToHistory();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error saving progress", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void saveToHistory() {
        SessionManager session = new SessionManager(this);
        Map<String, Object> sub = new HashMap<>();
        sub.put("assignmentId", "grammar_" + levelName.toLowerCase() + "_" + System.currentTimeMillis());
        sub.put("assignmentTitle", levelName + " Grammar Practice");
        sub.put("studentId", mAuth.getUid());
        sub.put("studentEmail", mAuth.getCurrentUser().getEmail());
        sub.put("course", session.getCourse());
        sub.put("semester", session.getSemester());
        sub.put("submittedAt", System.currentTimeMillis());
        sub.put("status", "graded");
        sub.put("grade", score + "/" + questions.size());
        db.collection("submissions").add(sub);
    }

    private void showFinalResults(int xp) {
        new AlertDialog.Builder(this)
                .setTitle("Practice Complete! 🏆")
                .setMessage("You scored " + score + "/" + questions.size() + "\nXP Earned: +" + xp)
                .setPositiveButton("Finish", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }
}
