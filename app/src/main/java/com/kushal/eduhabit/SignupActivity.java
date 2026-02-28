package com.kushal.eduhabit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kushal.eduhabit.databinding.ActivitySignupBinding;
import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private ActivitySignupBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Setup Dropdown
        String[] roles = {"Student", "Teacher"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, roles);
        binding.roleDropdown.setAdapter(adapter);

        binding.signupButton.setOnClickListener(v -> {
            String role = binding.roleDropdown.getText().toString();
            String name = binding.nameEditText.getText().toString().trim();
            String email = binding.emailEditText.getText().toString().trim();
            String password = binding.passwordEditText.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            saveUserToFirestore(name, email, role);
                        } else {
                            String error = task.getException() != null ? task.getException().getMessage() : "Signup Failed";
                            Toast.makeText(SignupActivity.this, error, Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }

    private void saveUserToFirestore(String name, String email, String role) {
        String userId = mAuth.getCurrentUser().getUid();
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("email", email);
        user.put("role", role.toLowerCase());
        
        // Initialize real data fields for Student
        if ("student".equalsIgnoreCase(role)) {
            user.put("xp", 0);
            user.put("level", 1);
            user.put("tasksDone", 0);
            user.put("streak", 0);
            user.put("grammarProgress", 0);
            user.put("vocabProgress", 0);
        }

        db.collection("users").document(userId)
                .set(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(SignupActivity.this, role + " Account Created", Toast.LENGTH_SHORT).show();
                    
                    Intent intent;
                    if ("teacher".equalsIgnoreCase(role)) {
                        intent = new Intent(SignupActivity.this, TeacherDashboardActivity.class);
                    } else {
                        intent = new Intent(SignupActivity.this, StudentDashboardActivity.class);
                    }
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(SignupActivity.this, "Error saving data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}