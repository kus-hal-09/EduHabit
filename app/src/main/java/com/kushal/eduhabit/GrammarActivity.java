package com.kushal.eduhabit;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kushal.eduhabit.databinding.ActivityGrammarDashboardBinding;
import java.util.ArrayList;
import java.util.List;

public class GrammarActivity extends AppCompatActivity {

    private ActivityGrammarDashboardBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGrammarDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        session = new SessionManager(this);

        binding.toolbar.setNavigationOnClickListener(v -> finish());
        
        fetchRealUserData();
        setupLevels();
    }

    private void fetchRealUserData() {
        if (mAuth.getCurrentUser() == null) return;
        String uid = mAuth.getUid();

        db.collection("users").document(uid).addSnapshotListener((doc, e) -> {
            if (doc != null && doc.exists()) {
                long xp = doc.getLong("xp") != null ? doc.getLong("xp") : 0;
                long streak = doc.getLong("streak") != null ? doc.getLong("streak") : 0;
                
                binding.tvTotalPoints.setText(String.valueOf(xp));
                binding.tvDayStreak.setText(String.valueOf(streak));
            }
        });

        db.collection("submissions")
            .whereEqualTo("studentId", uid)
            .addSnapshotListener((snap, e) -> {
                if (snap != null) {
                    int count = snap.size();
                    binding.tvTodayCount.setText(String.valueOf(count));
                    
                    int progress = Math.min(count, 10);
                    binding.dailyChallengeProgress.setProgress(progress * 10);
                    binding.tvDailyProgressText.setText(progress + "/10 completed");
                }
            });
    }

    private void setupLevels() {
        List<GrammarLevel> levels = new ArrayList<>();
        levels.add(new GrammarLevel("Beginner", "Articles, basic tenses", 1, 10, R.drawable.ic_book));
        levels.add(new GrammarLevel("Intermediate", "Modals, perfect tenses", 2, 25, R.drawable.ic_book));
        levels.add(new GrammarLevel("Advanced", "Advanced clauses & nuance", 3, 50, R.drawable.ic_book));

        binding.llLevelContainer.removeAllViews();
        for (GrammarLevel level : levels) {
            View levelView = LayoutInflater.from(this).inflate(R.layout.item_grammar_level_card, binding.llLevelContainer, false);
            
            TextView title = levelView.findViewById(R.id.tvLevelTitle);
            TextView desc = levelView.findViewById(R.id.tvLevelDesc);
            TextView points = levelView.findViewById(R.id.tvPointsPerQuestion);
            ImageView icon = levelView.findViewById(R.id.ivLevelIcon);
            LinearLayout starsContainer = levelView.findViewById(R.id.llStars);

            title.setText(level.title);
            desc.setText(level.description);
            points.setText("+" + level.points + " pts/qn");
            icon.setImageResource(level.iconRes);

            for (int i = 0; i < starsContainer.getChildCount(); i++) {
                ImageView star = (ImageView) starsContainer.getChildAt(i);
                if (i < level.stars) {
                    star.setColorFilter(android.graphics.Color.parseColor("#FACC15")); 
                } else {
                    star.setColorFilter(android.graphics.Color.parseColor("#E2E8F0")); 
                }
            }

            levelView.setOnClickListener(v -> {
                Intent intent = new Intent(this, PracticeExerciseActivity.class);
                intent.putExtra("level_name", level.title);
                startActivity(intent);
            });

            binding.llLevelContainer.addView(levelView);
        }
    }

    private static class GrammarLevel {
        String title, description;
        int stars, points, iconRes;

        GrammarLevel(String title, String description, int stars, int points, int iconRes) {
            this.title = title;
            this.description = description;
            this.stars = stars;
            this.points = points;
            this.iconRes = iconRes;
        }
    }
}
