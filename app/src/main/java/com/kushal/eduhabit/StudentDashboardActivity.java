package com.kushal.eduhabit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kushal.eduhabit.databinding.ActivityStudentDashboardBinding;

public class StudentDashboardActivity extends AppCompatActivity {

    private ActivityStudentDashboardBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStudentDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Dashboard Cards logic
        binding.grammarCard.setOnClickListener(v -> startActivity(new Intent(this, GrammarPracticeActivity.class)));
        binding.vocabularyCard.setOnClickListener(v -> startActivity(new Intent(this, VocabularyPracticeActivity.class)));
        binding.speakingCard.setOnClickListener(v -> startActivity(new Intent(this, SpeakingPracticeActivity.class)));
        binding.assignmentsCard.setOnClickListener(v -> startActivity(new Intent(this, AssignmentListActivity.class)));

        // BOTTOM NAVIGATION LOGIC
        binding.bottomNavigation.setSelectedItemId(R.id.nav_home);
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_learn) {
                startActivity(new Intent(this, GrammarPracticeActivity.class));
                return true;
            } else if (itemId == R.id.nav_speak) {
                startActivity(new Intent(this, SpeakingPracticeActivity.class));
                return true;
            } else if (itemId == R.id.nav_tasks) {
                startActivity(new Intent(this, AssignmentListActivity.class));
                return true;
            } else if (itemId == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });

        resetToRealData();
        fetchUserData();
        fetchAssignmentCount();
    }

    private void resetToRealData() {
        binding.welcomeText.setText("Loading...");
        binding.streakValue.setText("0 Days 🔥");
        binding.tvAssignmentsPending.setText("0 pending");
    }

    private void fetchUserData() {
        if (mAuth.getCurrentUser() == null) return;
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("users").document(userId).get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String name = documentSnapshot.getString("name");
                    binding.welcomeText.setText("Hello, " + name + "! 👋");
                }
            });
    }

    private void fetchAssignmentCount() {
        db.collection("assignments").get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                int count = queryDocumentSnapshots.size();
                binding.tvAssignmentsPending.setText(count + " pending");
            });
    }
}