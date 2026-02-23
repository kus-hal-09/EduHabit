package com.kushal.eduhabit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.kushal.eduhabit.databinding.ActivityProfileBinding;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // 1. Setup Back Button
        binding.backBtn.setOnClickListener(v -> finish());

        // 2. Setup Logout Button
        binding.logoutBtn.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // 3. Setup Save Changes Button
        binding.saveBtn.setOnClickListener(v -> saveProfileChanges());

        // 4. Setup Bottom Navigation Logic
        binding.bottomNavigation.setSelectedItemId(R.id.nav_teacher_profile);
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_teacher_home) {
                startActivity(new Intent(this, TeacherDashboardActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_teacher_tasks) {
                startActivity(new Intent(this, CreateAssignmentActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_teacher_students) {
                startActivity(new Intent(this, StudentsListActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_teacher_analytics) {
                startActivity(new Intent(this, AnalyticsActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_teacher_profile) {
                return true;
            }
            return false;
        });

        fetchUserData();
        fetchTeacherStats();
    }

    private void fetchUserData() {
        if (mAuth.getCurrentUser() == null) return;
        
        String userId = mAuth.getCurrentUser().getUid();
        String userEmail = mAuth.getCurrentUser().getEmail();
        
        binding.profileHeaderEmail.setText(userEmail);
        binding.editEmail.setText(userEmail);

        db.collection("users").document(userId).get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String name = documentSnapshot.getString("name");
                    binding.profileHeaderName.setText(name);
                    binding.editName.setText(name);
                }
            });
    }

    private void fetchTeacherStats() {
        // Real Student Count
        db.collection("users").whereEqualTo("role", "student").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    binding.statTotalStudentsProfile.setText(String.valueOf(queryDocumentSnapshots.size()));
                });

        // Real Assignment Count
        db.collection("assignments").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    binding.statTotalAssignmentsProfile.setText(String.valueOf(queryDocumentSnapshots.size()));
                });
    }

    private void saveProfileChanges() {
        String newName = binding.editName.getText().toString().trim();
        
        if (newName.isEmpty()) {
            Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", newName);

        db.collection("users").document(userId)
                .set(updates, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ProfileActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                    binding.profileHeaderName.setText(newName);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ProfileActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}