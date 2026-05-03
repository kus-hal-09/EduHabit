package com.kushal.eduhabit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kushal.eduhabit.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        session = new SessionManager(this);

        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();
            // Emergency Reset for locked accounts
            if (email.equals("teacher7@gmail.com") && binding.etPassword.getText().toString().isEmpty()) {
                emergencyPasswordReset(email);
            } else {
                loginUser();
            }
        });
        
        binding.btnSignup.setOnClickListener(v -> {
            startActivity(new Intent(this, SignupActivity.class));
        });

        binding.btnBack.setOnClickListener(v -> finish());
        
        setupRoleToggles();
    }

    private void emergencyPasswordReset(String email) {
        Toast.makeText(this, "Emergency Reset: Sending email...", Toast.LENGTH_LONG).show();
        mAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Reset link sent to " + email, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void loginUser() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true);

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener(result -> {
                String uid = result.getUser().getUid();
                fetchUserAndRedirect(uid);
            })
            .addOnFailureListener(e -> {
                showLoading(false);
                Toast.makeText(this, "Login Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }

    private void fetchUserAndRedirect(String uid) {
        db.collection("users").document(uid).get()
            .addOnSuccessListener(doc -> {
                if (doc.exists()) {
                    String name = doc.getString("name");
                    String email = doc.getString("email");
                    String role = doc.getString("role");
                    String course = doc.getString("course");
                    String semester = doc.getString("semester") != null ? doc.getString("semester") : "";
                    String subject = doc.getString("subject") != null ? doc.getString("subject") : "";

                    // AUTO-REPAIR LOGIC:
                    // If this is our known teacher account but role is "student", fix it automatically!
                    if ("teacher7@gmail.com".equalsIgnoreCase(email) && !"teacher".equals(role)) {
                        role = "teacher";
                        db.collection("users").document(uid).update("role", "teacher");
                        Toast.makeText(this, "Account role repaired to Teacher", Toast.LENGTH_SHORT).show();
                    }

                    session.saveSession(uid, name, email, role, course, semester, subject);
                    showLoading(false);

                    Intent intent = new Intent(this, "teacher".equals(role) ? TeacherDashboardActivity.class : StudentDashboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    showLoading(false);
                    Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show();
                }
            })
            .addOnFailureListener(e -> {
                showLoading(false);
                Toast.makeText(this, "Failed to load user data", Toast.LENGTH_SHORT).show();
            });
    }

    private void setupRoleToggles() {
        binding.pillStudent.setOnClickListener(v -> {
            binding.pillStudent.setBackgroundResource(android.R.color.white);
            binding.pillStudent.setTextColor(getResources().getColor(R.color.midnight_blue, null));
            binding.pillTeacher.setBackground(null);
            binding.pillTeacher.setTextColor(android.R.color.white);
        });

        binding.pillTeacher.setOnClickListener(v -> {
            binding.pillTeacher.setBackgroundResource(android.R.color.white);
            binding.pillTeacher.setTextColor(getResources().getColor(R.color.midnight_blue, null));
            binding.pillStudent.setBackground(null);
            binding.pillStudent.setTextColor(android.R.color.white);
        });
    }

    private void showLoading(boolean show) {
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.btnLogin.setEnabled(!show);
    }
}
