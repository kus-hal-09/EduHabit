package com.kushal.eduhabit;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kushal.eduhabit.databinding.ActivityVocabularyPracticeBinding;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class VocabularyPracticeActivity extends AppCompatActivity {

    private ActivityVocabularyPracticeBinding binding;
    private VocabularyViewModel viewModel;
    private List<VocabularyModule> modules;
    private SoundManager soundManager;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private int currentQuestionIndex = 0;
    private int sessionXp = 0;
    private boolean isAnswerChecked = false;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVocabularyPracticeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        viewModel = new ViewModelProvider(this).get(VocabularyViewModel.class);
        soundManager = new SoundManager(this);
        
        setupData();
        setupUI();
        fetchUserXp();

        viewModel.selectedModule.observe(this, this::checkAndStartModule);
        if (!modules.isEmpty()) viewModel.selectedModule.setValue(modules.get(0));
    }

    private void checkAndStartModule(VocabularyModule module) {
        if (module == null) return;
        
        // CHECK DAILY COMPLETION LOGIC
        db.collection("users").document(mAuth.getUid()).get()
            .addOnSuccessListener(snapshot -> {
                String key = "lastVocabDate_" + module.moduleTitle.toLowerCase().replace(" ", "_");
                Long lastDate = snapshot.getLong(key);
                if (lastDate != null && isSameDay(lastDate, System.currentTimeMillis())) {
                    showDailyCompletedDialog(module.moduleTitle);
                } else {
                    startLoadingGate(module);
                }
            })
            .addOnFailureListener(e -> startLoadingGate(module));
    }

    private boolean isSameDay(long t1, long t2) {
        Calendar cal1 = Calendar.getInstance(); cal1.setTimeInMillis(t1);
        Calendar cal2 = Calendar.getInstance(); cal2.setTimeInMillis(t2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    private void showDailyCompletedDialog(String title) {
        new AlertDialog.Builder(this)
                .setTitle("Goal Mastered! 🌟")
                .setMessage("You have already finished the " + title + " practice for today. Fresh challenges reset every 24 hours!")
                .setPositiveButton("Explore Others", (dialog, which) -> {})
                .setCancelable(true)
                .show();
    }

    private void fetchUserXp() {
        if (mAuth.getCurrentUser() == null) return;
        db.collection("users").document(mAuth.getUid()).addSnapshotListener((doc, e) -> {
            if (doc != null && doc.exists() && binding != null) {
                long totalXp = doc.getLong("xp") != null ? doc.getLong("xp") : 0;
                binding.tvSessionXp.setText(totalXp + " XP");
            }
        });
    }

    private void startLoadingGate(VocabularyModule module) {
        if (module == null) return;
        
        binding.mainLayout.animate().alpha(0f).setDuration(300).start();
        binding.loadingLayout.setVisibility(View.VISIBLE);
        binding.loadingLayout.setAlpha(0f);
        binding.loadingLayout.animate().alpha(1f).setDuration(300).start();
        
        binding.tvLoadingTitle.setText("Syncing " + module.moduleTitle + "...");

        mainHandler.postDelayed(() -> {
            binding.loadingLayout.animate().alpha(0f).setDuration(300).withEndAction(() -> {
                binding.loadingLayout.setVisibility(View.GONE);
                binding.mainLayout.setVisibility(View.VISIBLE);
                binding.mainLayout.setAlpha(0f);
                binding.mainLayout.animate().alpha(1f).setDuration(500).start();
            }).start();
            currentQuestionIndex = 0;
            updateModuleUI(module);
        }, 1500);
    }

    private void setupData() {
        modules = new ArrayList<>();
        
        // STABLE DAILY RANDOMIZATION Logic in setupData
        long seed = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        Random rng = new Random(seed);

        modules.add(new VocabularyModule("BCA Core", "Technical Foundations", "10 Words", "Intermediate", R.drawable.ic_book, 
            Arrays.asList(new VocabularyModule.Word("Scalability", "", ""), new VocabularyModule.Word("Encryption", "", "")),
            generateDailyQuestions(rng, "BCA")
        ));

        modules.add(new VocabularyModule("Verbal Mastery", "Advanced Communication", "12 Words", "Advanced", R.drawable.ic_book, 
            new ArrayList<>(),
            generateDailyQuestions(rng, "Verbal")
        ));
    }

    private List<VocabularyModule.Question> generateDailyQuestions(Random rng, String category) {
        List<VocabularyModule.Question> pool = new ArrayList<>();
        if (category.equals("BCA")) {
            pool.add(new VocabularyModule.Question("Which term refers to 'handling growth'?", Arrays.asList("Encryption", "Scalability", "Agile"), 1, ""));
            pool.add(new VocabularyModule.Question("What is the opposite of 'Decryption'?", Arrays.asList("Encryption", "Coding", "Mining"), 0, ""));
            pool.add(new VocabularyModule.Question("A blueprint for an object is called:", Arrays.asList("Class", "Method", "Variable"), 0, ""));
        } else {
            pool.add(new VocabularyModule.Question("Choose the most formal word:", Arrays.asList("Get", "Obtain", "Grab"), 1, ""));
            pool.add(new VocabularyModule.Question("A person who is 'eloquent' speaks:", Arrays.asList("Fluently", "Slowly", "Quietly"), 0, ""));
        }
        Collections.shuffle(pool, rng);
        return pool;
    }

    private void setupUI() {
        // FIXED: Using toolbar navigation icon instead of non-existent btnBack
        binding.toolbar.setNavigationOnClickListener(v -> finish());
        
        binding.rvModules.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvModules.setAdapter(new VocabModuleAdapter(modules, module -> viewModel.selectedModule.setValue(module)));
        
        binding.btnActionPrimary.setOnClickListener(v -> handleQuizAction());
        binding.btnRestart.setOnClickListener(v -> startLoadingGate(viewModel.selectedModule.getValue()));
        binding.btnClaimRewards.setOnClickListener(v -> finish());

        binding.rgOptions.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1 && !isAnswerChecked) {
                binding.btnActionPrimary.setEnabled(true);
                binding.btnActionPrimary.animate().scaleX(1.02f).scaleY(1.02f).setDuration(200).start();
                binding.btnActionPrimary.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#6366F1")));
                binding.btnActionPrimary.setTextColor(Color.WHITE);
            }
        });
    }

    private void handleQuizAction() {
        VocabularyModule module = viewModel.selectedModule.getValue();
        if (module == null) return;

        if (isAnswerChecked) {
            animateQuestionTransition(module);
            return;
        }

        int selectedId = binding.rgOptions.getCheckedRadioButtonId();
        if (selectedId == -1) return;

        VocabularyModule.Question q = module.quizSet.get(currentQuestionIndex);

        if (selectedId == q.correctOptionIndex) {
            sessionXp += 10;
            updateFirestoreXp(10);
            if (soundManager != null) soundManager.playCorrect();
            triggerCircularReveal();
            showAdvancedFloatingXp(true);
            highlightAnswer(selectedId, true);
        } else {
            if (soundManager != null) soundManager.playWrong();
            ObjectAnimator.ofFloat(binding.quizCard, "translationX", 0, 30, -30, 20, -20, 10, -10, 0).setDuration(500).start();
            highlightAnswer(q.correctOptionIndex, false);
            showAdvancedFloatingXp(false);
        }

        isAnswerChecked = true;
        binding.btnActionPrimary.setText("Next Challenge");
        binding.btnActionPrimary.animate().scaleX(1f).scaleY(1f).setDuration(200).start();
    }

    private void animateQuestionTransition(VocabularyModule module) {
        binding.quizCard.animate()
            .translationX(-1000f)
            .alpha(0f)
            .setDuration(400)
            .setInterpolator(new AnticipateOvershootInterpolator())
            .withEndAction(() -> {
                currentQuestionIndex++;
                if (currentQuestionIndex >= module.quizSet.size()) {
                    saveDailyCompletion(module);
                    showCompletion();
                } else {
                    loadQuestion(module);
                    binding.quizCard.setTranslationX(1000f);
                    binding.quizCard.animate()
                        .translationX(0f)
                        .alpha(1f)
                        .setDuration(500)
                        .setInterpolator(new OvershootInterpolator())
                        .start();
                }
            }).start();
    }

    private void saveDailyCompletion(VocabularyModule module) {
        if (mAuth.getUid() == null) return;
        String key = "lastVocabDate_" + module.moduleTitle.toLowerCase().replace(" ", "_");
        db.collection("users").document(mAuth.getUid()).update(key, System.currentTimeMillis());
    }

    private void showAdvancedFloatingXp(boolean correct) {
        binding.tvFloatingXp.clearAnimation();
        binding.tvFloatingXp.setVisibility(View.VISIBLE);
        binding.tvFloatingXp.setAlpha(0f);
        binding.tvFloatingXp.setScaleX(0.5f);
        binding.tvFloatingXp.setScaleY(0.5f);
        binding.tvFloatingXp.setTranslationY(0f);
        
        binding.tvFloatingXp.setText(correct ? "+10 XP" : "Keep Learning!");
        binding.tvFloatingXp.setTextColor(correct ? Color.parseColor("#10B981") : Color.parseColor("#EF4444"));

        binding.tvFloatingXp.animate()
                .alpha(1f)
                .scaleX(1.2f)
                .scaleY(1.2f)
                .translationY(-300f)
                .setDuration(1000)
                .setInterpolator(new OvershootInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        binding.tvFloatingXp.animate().alpha(0f).setDuration(200).start();
                    }
                })
                .start();
    }

    private void updateFirestoreXp(int amount) {
        if (mAuth.getCurrentUser() != null) {
            db.collection("users").document(mAuth.getUid()).update("xp", FieldValue.increment(amount));
        }
    }

    private void triggerCircularReveal() {
        int cx = binding.quizCard.getWidth() / 2;
        int cy = binding.quizCard.getHeight() / 2;
        float finalRadius = (float) Math.hypot(cx, cy);
        Animator anim = ViewAnimationUtils.createCircularReveal(binding.quizCard, cx, cy, 0f, finalRadius);
        anim.setDuration(700);
        anim.start();
    }

    private void highlightAnswer(int index, boolean isCorrect) {
        for (int i = 0; i < binding.rgOptions.getChildCount(); i++) {
            RadioButton rb = (RadioButton) binding.rgOptions.getChildAt(i);
            rb.setEnabled(false);
            if (i == index) {
                rb.setBackgroundResource(R.drawable.option_correct);
                rb.setTextColor(Color.WHITE);
                rb.animate().scaleX(1.05f).scaleY(1.05f).setDuration(200).start();
            } else if (rb.isChecked()) {
                rb.setBackgroundResource(R.drawable.option_wrong);
                rb.setTextColor(Color.WHITE);
            } else {
                rb.animate().alpha(0.4f).setDuration(300).start();
            }
        }
    }

    private void loadQuestion(VocabularyModule module) {
        isAnswerChecked = false;
        binding.rgOptions.clearCheck();
        binding.btnActionPrimary.setEnabled(false);
        binding.btnActionPrimary.setAlpha(0.6f);
        binding.btnActionPrimary.setText("Confirm Answer");
        binding.btnActionPrimary.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F1F5F9")));
        binding.btnActionPrimary.setTextColor(Color.parseColor("#94A3B8"));
        
        binding.rgOptions.removeAllViews();
        VocabularyModule.Question q = module.quizSet.get(currentQuestionIndex);
        binding.tvQuizQuestion.setText(q.question);
        
        for (int i = 0; i < q.options.size(); i++) {
            RadioButton rb = new RadioButton(this);
            rb.setText(q.options.get(i));
            rb.setId(i);
            rb.setTextColor(Color.parseColor("#1E293B")); 
            rb.setPadding(52, 52, 52, 52);
            rb.setTextSize(16f);
            rb.setTypeface(null, android.graphics.Typeface.BOLD);
            
            RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            );
            lp.setMargins(0, 0, 0, 24);
            rb.setLayoutParams(lp);
            rb.setBackgroundResource(R.drawable.option_bg_selector);
            rb.setButtonDrawable(null);
            
            binding.rgOptions.addView(rb);
        }

        int progress = (int) (((float) (currentQuestionIndex + 1) / module.quizSet.size()) * 100);
        binding.quizProgress.setProgress(progress, true);
    }

    private void showCompletion() {
        binding.mainLayout.animate().alpha(0f).translationY(200f).setDuration(500).withEndAction(() -> {
            binding.mainLayout.setVisibility(View.GONE);
            binding.completionLayout.setVisibility(View.VISIBLE);
            binding.completionLayout.setAlpha(0f);
            binding.completionLayout.setTranslationY(-200f);
            binding.completionLayout.animate().alpha(1f).translationY(0f).setDuration(600).setInterpolator(new OvershootInterpolator()).start();
        }).start();

        binding.tvFinalXp.setText("+" + sessionXp + " XP REWARDED");
        binding.quizProgress.setProgress(100, true);
        
        binding.lottieConfetti.setVisibility(View.VISIBLE);
        binding.lottieConfetti.playAnimation();
    }

    private void updateModuleUI(VocabularyModule module) {
        binding.tvTitle.setText(module.moduleTitle);
        loadQuestion(module);
    }

    @Override protected void onDestroy() { 
        if (soundManager != null) soundManager.release();
        super.onDestroy(); 
    }
}
