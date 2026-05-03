package com.kushal.eduhabit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.kushal.eduhabit.databinding.ActivityProfileBinding;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        session = new SessionManager(this);

        binding.backBtn.setOnClickListener(v -> finish());

        binding.logoutBtn.setOnClickListener(v -> showLogoutConfirmation());

        // ADDED: Delete Account Listener
        binding.btnDeleteAccount.setOnClickListener(v -> showDeleteConfirmation());

        binding.saveBtn.setOnClickListener(v -> saveProfileChanges());

        setupBottomNav();
        fetchUserData();
        fetchTeacherStats();
    }

    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> logout())
                .setNegativeButton("No", null)
                .show();
    }

    private void logout() {
        mAuth.signOut();
        session.clearSession();
        Intent intent = new Intent(ProfileActivity.this, WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("Are you absolutely sure? This will delete all your data and assignments permanently.")
                .setPositiveButton("Delete", (dialog, which) -> deleteUserAccount())
                .setNegativeButton("Cancel", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteUserAccount() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        String uid = user.getUid();

        db.collection("users").document(uid).delete()
            .addOnSuccessListener(aVoid -> {
                user.delete().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Account Deleted Successfully", Toast.LENGTH_SHORT).show();
                        session.clearSession();
                        startActivity(new Intent(this, WelcomeActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Error: Re-authenticate to delete account", Toast.LENGTH_LONG).show();
                    }
                });
            })
            .addOnFailureListener(e -> Toast.makeText(this, "Failed to delete data", Toast.LENGTH_SHORT).show());
    }

    private void setupBottomNav() {
        binding.bottomNavigation.setSelectedItemId(R.id.nav_teacher_profile);
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_teacher_home) {
                startActivity(new Intent(this, TeacherDashboardActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_teacher_tasks) {
                startActivity(new Intent(this, SubmissionsListActivity.class));
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
            }
            return false;
        });
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
        db.collection("users").whereEqualTo("role", "student").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    binding.statTotalStudentsProfile.setText(String.valueOf(queryDocumentSnapshots.size()));
                });

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
                });
    }
}
