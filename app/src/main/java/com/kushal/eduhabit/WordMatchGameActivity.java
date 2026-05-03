package com.kushal.eduhabit;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kushal.eduhabit.databinding.ActivityWordMatchGameBinding;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordMatchGameActivity extends AppCompatActivity {

    private ActivityWordMatchGameBinding binding;
    private Map<String, String> wordPairs;
    private List<String> shuffledItems;
    private MaterialCardView firstSelectedCard = null;
    private String firstSelectedValue = null;
    private int matchesFound = 0;
    private boolean isProcessing = false;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWordMatchGameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnReset.setOnClickListener(v -> resetGame());

        setupGame();
    }

    private void setupGame() {
        wordPairs = new HashMap<>();
        wordPairs.put("Persuade", "Convince");
        wordPairs.put("Abandon", "Leave");
        wordPairs.put("Grateful", "Thankful");
        wordPairs.put("Leverage", "Influence");
        wordPairs.put("Abundant", "Plentiful");
        wordPairs.put("Reluctant", "Unwilling");

        shuffledItems = new ArrayList<>();
        shuffledItems.addAll(wordPairs.keySet());
        shuffledItems.addAll(wordPairs.values());
        Collections.shuffle(shuffledItems);

        renderGrid();
    }

    private void renderGrid() {
        binding.gameGrid.removeAllViews();
        matchesFound = 0;
        updateScore();
        firstSelectedCard = null;
        firstSelectedValue = null;
        isProcessing = false;
        binding.gameProgress.setProgress(0);

        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int cardWidth = (screenWidth - (int)(64 * getResources().getDisplayMetrics().density)) / 2;

        for (String item : shuffledItems) {
            MaterialCardView card = createWordCard(item, cardWidth);
            card.setOnClickListener(v -> onCardClick(card, item));
            binding.gameGrid.addView(card);
        }
    }

    private MaterialCardView createWordCard(String text, int width) {
        MaterialCardView card = new MaterialCardView(this);
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = width;
        params.height = (int) (80 * getResources().getDisplayMetrics().density);
        params.setMargins(12, 12, 12, 12);
        card.setLayoutParams(params);
        
        card.setCardBackgroundColor(Color.WHITE);
        card.setRadius(40f);
        card.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#F1F5F9")));
        card.setStrokeWidth(4);
        card.setCardElevation(0f);
        card.setClickable(true);
        card.setFocusable(true);

        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextColor(Color.parseColor("#0F172A"));
        tv.setTextSize(15f);
        tv.setGravity(Gravity.CENTER);
        tv.setTypeface(null, android.graphics.Typeface.BOLD);
        
        FrameLayout.LayoutParams tvParams = new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        card.addView(tv, tvParams);

        return card;
    }

    private void onCardClick(MaterialCardView card, String value) {
        if (isProcessing || card == firstSelectedCard) return;

        highlightCard(card, true);

        if (firstSelectedCard == null) {
            firstSelectedCard = card;
            firstSelectedValue = value;
        } else {
            isProcessing = true;
            if (checkMatch(firstSelectedValue, value)) {
                handleMatch(firstSelectedCard, card);
            } else {
                handleMismatch(firstSelectedCard, card);
            }
        }
    }

    private void highlightCard(MaterialCardView card, boolean selected) {
        card.setStrokeColor(ColorStateList.valueOf(selected ? Color.parseColor("#4F46E5") : Color.parseColor("#F1F5F9")));
        card.setCardBackgroundColor(selected ? Color.parseColor("#EEF2FF") : Color.WHITE);
        TextView tv = (TextView) card.getChildAt(0);
        tv.setTextColor(selected ? Color.parseColor("#4F46E5") : Color.parseColor("#0F172A"));
        
        if (selected) {
            card.animate().scaleX(1.05f).scaleY(1.05f).setDuration(100).withEndAction(() -> 
                card.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
            ).start();
        }
    }

    private boolean checkMatch(String val1, String val2) {
        return (wordPairs.containsKey(val1) && wordPairs.get(val1).equals(val2)) ||
               (wordPairs.containsKey(val2) && wordPairs.get(val2).equals(val1));
    }

    private void handleMatch(MaterialCardView card1, MaterialCardView card2) {
        card1.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#10B981")));
        card1.setCardBackgroundColor(Color.parseColor("#ECFDF5"));
        card2.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#10B981")));
        card2.setCardBackgroundColor(Color.parseColor("#ECFDF5"));
        
        matchesFound++;
        updateScore();
        updateFirestoreXp(5);
        
        int progress = (int) (((float) matchesFound / wordPairs.size()) * 100);
        binding.gameProgress.setProgress(progress, true);

        mainHandler.postDelayed(() -> {
            card1.animate().alpha(0f).scaleX(0.8f).scaleY(0.8f).setDuration(400).start();
            card2.animate().alpha(0f).scaleX(0.8f).scaleY(0.8f).setDuration(400).withEndAction(() -> {
                card1.setVisibility(View.INVISIBLE);
                card2.setVisibility(View.INVISIBLE);
                isProcessing = false;
                firstSelectedCard = null;
                firstSelectedValue = null;

                if (matchesFound == wordPairs.size()) {
                    showSuccess();
                }
            }).start();
        }, 600);
    }

    private void handleMismatch(MaterialCardView card1, MaterialCardView card2) {
        card1.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#EF4444")));
        card1.setCardBackgroundColor(Color.parseColor("#FEF2F2"));
        card2.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#EF4444")));
        card2.setCardBackgroundColor(Color.parseColor("#FEF2F2"));

        card1.animate().translationX(10f).setDuration(50).withEndAction(() -> 
            card1.animate().translationX(-10f).setDuration(50).withEndAction(() -> 
                card1.animate().translationX(0f).setDuration(50).start()
            ).start()
        ).start();

        card2.animate().translationX(10f).setDuration(50).withEndAction(() -> 
            card2.animate().translationX(-10f).setDuration(50).withEndAction(() -> 
                card2.animate().translationX(0f).setDuration(50).start()
            ).start()
        ).start();

        mainHandler.postDelayed(() -> {
            highlightCard(card1, false);
            highlightCard(card2, false);
            isProcessing = false;
            firstSelectedCard = null;
            firstSelectedValue = null;
        }, 1000);
    }

    private void updateScore() {
        binding.tvScore.setText("Score: " + matchesFound + " / " + wordPairs.size());
    }

    private void updateFirestoreXp(int amount) {
        if (mAuth.getCurrentUser() != null) {
            db.collection("users").document(mAuth.getUid()).update("xp", FieldValue.increment(amount));
        }
    }

    private void showSuccess() {
        Toast.makeText(this, "Mastered! +30 XP Earned", Toast.LENGTH_LONG).show();
    }

    private void resetGame() {
        Collections.shuffle(shuffledItems);
        renderGrid();
    }
}
