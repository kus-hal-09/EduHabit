package com.kushal.eduhabit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kushal.eduhabit.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 1. Session Check: If user is already logged in, hide UI and check role
        if (mAuth.getCurrentUser() != null) {
            binding.getRoot().setVisibility(View.GONE); // Hide landing page while loading
            checkUserRoleAndNavigate();
        }

        // Standard Sign In
        binding.btnSignIn.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        });

        // Create Account
        binding.btnCreateAccount.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SignupActivity.class));
        });

        // Quick Demo Access: Student
        binding.btnViewStudent.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.putExtra("email", "student@gmail.com");
            intent.putExtra("password", "student");
            startActivity(intent);
        });

        // Quick Demo Access: Teacher
        binding.btnViewTeacher.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.putExtra("email", "teacher@gmail.com");
            intent.putExtra("password", "teacher");
            startActivity(intent);
        });
    }

    private void checkUserRoleAndNavigate() {
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String role = documentSnapshot.getString("role");
                        if ("teacher".equalsIgnoreCase(role)) {
                            startActivity(new Intent(MainActivity.this, TeacherDashboardActivity.class));
                        } else {
                            startActivity(new Intent(MainActivity.this, StudentDashboardActivity.class));
                        }
                        finish();
                    } else {
                        // Document not found, show landing page to allow re-login
                        binding.getRoot().setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Session Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    binding.getRoot().setVisibility(View.VISIBLE);
                });
    }
}