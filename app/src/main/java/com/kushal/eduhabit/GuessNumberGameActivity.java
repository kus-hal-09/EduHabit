package com.kushal.eduhabit;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.kushal.eduhabit.databinding.ActivityGuessNumberGameBinding;
import java.util.Random;

public class GuessNumberGameActivity extends AppCompatActivity {

    private ActivityGuessNumberGameBinding binding;
    private int randomNumber;
    private int attempts = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGuessNumberGameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnCheck.setOnClickListener(v -> checkGuess());
        binding.btnPlayAgain.setOnClickListener(v -> startNewGame());

        startNewGame();
    }

    private void startNewGame() {
        randomNumber = new Random().nextInt(101);
        attempts = 0;
        updateAttempts();

        binding.tvGameStatusIcon.setText("🤔");
        binding.tvHint.setText("Take a shot!");
        binding.tvHint.setTextColor(ContextCompat.getColor(this, R.color.accent_indigo));
        binding.etGuess.setText("");
        binding.etGuess.setEnabled(true);
        binding.btnCheck.setVisibility(View.VISIBLE);
        binding.btnPlayAgain.setVisibility(View.GONE);
        
        startPulseAnimation();
    }

    private void checkGuess() {
        String input = binding.etGuess.getText().toString();
        if (input.isEmpty()) {
            Toast.makeText(this, "Enter a number!", Toast.LENGTH_SHORT).show();
            return;
        }

        attempts++;
        updateAttempts();
        int guess = Integer.parseInt(input);

        if (guess == randomNumber) {
            handleWin();
        } else if (guess < randomNumber) {
            handleFeedback("Too Low! ↑", "📉", Color.RED);
        } else {
            handleFeedback("Too High! ↓", "📈", Color.RED);
        }
        hideKeyboard();
    }

    private void handleWin() {
        binding.tvGameStatusIcon.setText("🎉");
        binding.tvHint.setText("WOW! It was " + randomNumber);
        binding.tvHint.setTextColor(ContextCompat.getColor(this, R.color.success_green));
        binding.etGuess.setEnabled(false);
        binding.btnCheck.setVisibility(View.GONE);
        binding.btnPlayAgain.setVisibility(View.VISIBLE);
        
        // Wow win animation
        animateWin();
        Toast.makeText(this, "Brilliant! +30 XP!", Toast.LENGTH_LONG).show();
    }

    private void handleFeedback(String hint, String emoji, int color) {
        binding.tvHint.setText(hint);
        binding.tvHint.setTextColor(color);
        binding.tvGameStatusIcon.setText(emoji);
        
        // Shake animation
        ObjectAnimator.ofFloat(binding.etGuess, "translationX", 0, 25, -25, 25, -25, 15, -15, 6, -6, 0)
                .setDuration(500)
                .start();
        
        binding.etGuess.setText("");
    }

    private void startPulseAnimation() {
        ScaleAnimation pulse = new ScaleAnimation(1f, 1.2f, 1f, 1.2f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        pulse.setDuration(1000);
        pulse.setRepeatMode(Animation.REVERSE);
        pulse.setRepeatCount(Animation.INFINITE);
        binding.animCircle.startAnimation(pulse);
    }

    private void animateWin() {
        binding.animCircle.clearAnimation();
        
        // Scale up and change color
        binding.animCircle.animate()
                .scaleX(2f)
                .scaleY(2f)
                .alpha(0.5f)
                .setDuration(1000)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .start();

        ValueAnimator colorAnim = ValueAnimator.ofObject(new ArgbEvaluator(), 
                ContextCompat.getColor(this, R.color.pastel_indigo), 
                ContextCompat.getColor(this, R.color.pastel_emerald));
        colorAnim.setDuration(1000);
        colorAnim.addUpdateListener(anim -> 
                binding.animCircle.setBackgroundTintList(android.content.res.ColorStateList.valueOf((int) anim.getAnimatedValue())));
        colorAnim.start();
    }

    private void updateAttempts() {
        binding.tvAttempts.setText("Attempts: " + attempts);
    }
    
    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
