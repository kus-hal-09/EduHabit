package com.kushal.eduhabit;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kushal.eduhabit.databinding.ActivityAchievementsBinding;
import java.util.ArrayList;
import java.util.List;

public class AchievementsActivity extends AppCompatActivity {

    private ActivityAchievementsBinding binding;
    private AchievementAdapter adapter;
    private List<Achievement> achievements;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAchievementsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        setupToolbar();
        setupAchievements();
        fetchUserData();
    }

    private void setupToolbar() {
        // Use the Toolbar navigation icon since the standalone btnBack was replaced in the UI overhaul
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupAchievements() {
        achievements = new ArrayList<>();
        achievements.add(new Achievement("1", "First Steps", "Completed your first lesson", R.drawable.ic_book, 1));
        achievements.add(new Achievement("2", "Vocabulary Voyager", "Learn 50 new words", R.drawable.ic_assignments, 50));
        achievements.add(new Achievement("3", "Grammar Guru", "Master 5 grammar modules", R.drawable.ic_book, 5));
        achievements.add(new Achievement("4", "Perfect Week", "Maintain a 7-day streak", R.drawable.ic_achievements, 7));
        achievements.add(new Achievement("5", "Task Master", "Complete 10 assignments", R.drawable.ic_assignments, 10));
        achievements.add(new Achievement("6", "Scholar", "Reach Level 5", R.drawable.logo_bg, 5));

        adapter = new AchievementAdapter(this, achievements);
        binding.rvBadges.setLayoutManager(new GridLayoutManager(this, 3));
        binding.rvBadges.setAdapter(adapter);
    }

    private void fetchUserData() {
        if (mAuth.getCurrentUser() == null) return;
        String userId = mAuth.getCurrentUser().getUid();

        db.collection("users").document(userId).addSnapshotListener((doc, error) -> {
            if (doc != null && doc.exists()) {
                // Real data from Firebase
                long xp = doc.contains("xp") ? doc.getLong("xp") : 0;
                long level = (xp / 100) + 1;
                long streak = doc.contains("streak") ? doc.getLong("streak") : 0;
                
                // For demo, we'll use these to update progress
                updateProgress(0, 1); // First Steps (Manual for now)
                updateProgress(1, 20); // Vocabulary (Mocked 20/50)
                updateProgress(2, 2);  // Grammar (Mocked 2/5)
                updateProgress(3, (int)streak); 
                updateProgress(5, (int)level);

                // Update Milestone Progress Bars
                binding.pbMilestone1.setProgress((int)((20.0/50.0)*100)); // Vocab Voyager progress
                binding.pbMilestone2.setProgress((int)((2.0/5.0)*100));  // Grammar progress
            }
        });
    }

    private void updateProgress(int index, int value) {
        if (index < achievements.size()) {
            Achievement a = achievements.get(index);
            a.currentProgress = value;
            a.isUnlocked = a.currentProgress >= a.targetValue;
            adapter.notifyItemChanged(index);
        }
    }
}
