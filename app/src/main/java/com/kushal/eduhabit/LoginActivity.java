package com.kushal.eduhabit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kushal.eduhabit.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Setup Dropdown
        String[] roles = {"Student", "Teacher"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, roles);
        binding.roleDropdown.setAdapter(adapter);

        // Pre-fill if coming from Demo buttons
        if (getIntent().hasExtra("email")) {
            binding.emailEditText.setText(getIntent().getStringExtra("email"));
            binding.passwordEditText.setText(getIntent().getStringExtra("password"));
            // Auto-set dropdown based on demo email
            if ("teacher@gmail.com".equals(getIntent().getStringExtra("email"))) {
                binding.roleDropdown.setText("Teacher", false);
            } else {
                binding.roleDropdown.setText("Student", false);
            }
        }

        binding.loginButton.setOnClickListener(v -> {
            String email = binding.emailEditText.getText().toString().trim();
            String password = binding.passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter credentials", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            checkUserRoleAndNavigate();
                        } else {
                            Toast.makeText(LoginActivity.this, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        if (binding.backBtn != null) {
            binding.backBtn.setOnClickListener(v -> finish());
        }
    }

    private void checkUserRoleAndNavigate() {
        String userId = mAuth.getCurrentUser().getUid();
        
        // Fetch role from Firestore
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String role = documentSnapshot.getString("role");
                        
                        if ("teacher".equalsIgnoreCase(role)) {
                            Toast.makeText(this, "Welcome, Teacher!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, TeacherDashboardActivity.class));
                        } else {
                            Toast.makeText(this, "Welcome, Student!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, StudentDashboardActivity.class));
                        }
                        finish();
                    } else {
                        // FALLBACK: If user document doesn't exist, check the dropdown selection
                        String selectedRole = binding.roleDropdown.getText().toString();
                        if ("Teacher".equals(selectedRole)) {
                            startActivity(new Intent(LoginActivity.this, TeacherDashboardActivity.class));
                        } else {
                            startActivity(new Intent(LoginActivity.this, StudentDashboardActivity.class));
                        }
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    // Fallback on network error
                    startActivity(new Intent(LoginActivity.this, StudentDashboardActivity.class));
                    finish();
                });
    }
}