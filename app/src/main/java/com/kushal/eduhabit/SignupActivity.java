package com.kushal.eduhabit;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kushal.eduhabit.databinding.ActivitySignupBinding;
import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {
    private ActivitySignupBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private SessionManager session;
    
    private int currentStep = 1;
    private String selectedRole = "student";
    private String selectedCourse = "";
    private String selectedSemester = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        session = new SessionManager(this);

        setupStep1();
        setupStep2();
        setupStep3();

        binding.btnBack.setOnClickListener(v -> {
            if (currentStep > 1) goToStep(currentStep - 1);
            else finish();
        });
    }

    private void setupStep1() {
        binding.btnNext1.setOnClickListener(v -> {
            if (validateStep1()) {
                goToStep(2);
            }
        });
    }

    private void setupStep2() {
        binding.pillStudent.setOnClickListener(v -> {
            selectedRole = "student";
            binding.pillStudent.setBackgroundResource(android.R.color.white);
            binding.pillStudent.setTextColor(getResources().getColor(R.color.midnight_blue, null));
            binding.pillTeacher.setBackground(null);
            binding.pillTeacher.setTextColor(android.R.color.white);
            binding.layoutStudentExtra.setVisibility(View.VISIBLE);
            binding.layoutTeacherExtra.setVisibility(View.GONE);
        });

        binding.pillTeacher.setOnClickListener(v -> {
            selectedRole = "teacher";
            binding.pillTeacher.setBackgroundResource(android.R.color.white);
            binding.pillTeacher.setTextColor(getResources().getColor(R.color.midnight_blue, null));
            binding.pillStudent.setBackground(null);
            binding.pillStudent.setTextColor(android.R.color.white);
            binding.layoutTeacherExtra.setVisibility(View.VISIBLE);
            binding.layoutStudentExtra.setVisibility(View.GONE);
        });

        binding.cardBCA.setOnClickListener(v -> {
            selectedCourse = "BCA";
            binding.cardBCA.setStrokeWidth(4);
            binding.cardBCA.setStrokeColor(getResources().getColor(R.color.accent_indigo, null));
            binding.cardBIT.setStrokeWidth(0);
        });

        binding.cardBIT.setOnClickListener(v -> {
            selectedCourse = "BIT";
            binding.cardBIT.setStrokeWidth(4);
            binding.cardBIT.setStrokeColor(getResources().getColor(R.color.accent_indigo, null));
            binding.cardBCA.setStrokeWidth(0);
        });

        binding.semesterChipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                Chip chip = findViewById(checkedIds.get(0));
                selectedSemester = chip.getText().toString();
            }
        });

        binding.btnSignupAction.setOnClickListener(v -> {
            if (validateStep2()) {
                createAccount();
            }
        });
    }

    private void setupStep3() {
        binding.btnGoToDashboard.setOnClickListener(v -> redirectToDashboardNow());
    }

    private void goToStep(int step) {
        currentStep = step;
        binding.viewFlipper.setDisplayedChild(step - 1);
        updateStepIndicator(step);
    }

    private void updateStepIndicator(int step) {
        binding.dot1.setBackgroundTintList(android.content.res.ColorStateList.valueOf(step >= 1 ? android.graphics.Color.WHITE : android.graphics.Color.parseColor("#334155")));
        binding.dot2.setBackgroundTintList(android.content.res.ColorStateList.valueOf(step >= 2 ? android.graphics.Color.WHITE : android.graphics.Color.parseColor("#334155")));
        binding.dot3.setBackgroundTintList(android.content.res.ColorStateList.valueOf(step >= 3 ? android.graphics.Color.WHITE : android.graphics.Color.parseColor("#334155")));
    }

    private boolean validateStep1() {
        String name = binding.etName.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String confirm = binding.etConfirmPassword.getText().toString().trim();

        if (name.isEmpty()) { binding.etName.setError("Name required"); return false; }
        if (email.isEmpty()) { binding.etEmail.setError("Email required"); return false; }
        if (password.length() < 6) { binding.etPassword.setError("Min 6 characters"); return false; }
        if (!password.equals(confirm)) { binding.etConfirmPassword.setError("Passwords don't match"); return false; }
        return true;
    }

    private boolean validateStep2() {
        if (selectedCourse.isEmpty()) {
            Toast.makeText(this, "Please select a course", Toast.LENGTH_SHORT).show();
            return false;
        }
        // Teachers ALSO need a semester to manage their students
        if (selectedSemester.isEmpty()) {
            Toast.makeText(this, "Please select a semester", Toast.LENGTH_SHORT).show();
            return false;
        }
        if ("teacher".equals(selectedRole) && binding.etSubject.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Please enter your subject", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void createAccount() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String name = binding.etName.getText().toString().trim();
        
        showLoading(true);
        
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener(result -> {
                String uid = result.getUser().getUid();
                saveUserProfile(uid, name, email);
            })
            .addOnFailureListener(e -> {
                showLoading(false);
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
    }

    private void saveUserProfile(String uid, String name, String email) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("uid", uid);
        userData.put("name", name);
        userData.put("email", email);
        userData.put("role", selectedRole);
        userData.put("course", selectedCourse);
        userData.put("semester", selectedSemester); // ALWAYS SAVE SEMESTER
        userData.put("createdAt", FieldValue.serverTimestamp());
        
        String subject = binding.etSubject.getText().toString().trim();

        if ("student".equals(selectedRole)) {
            userData.put("xp", 0);
            userData.put("streak", 0);
        } else {
            userData.put("subject", subject);
        }
        
        db.collection("users").document(uid).set(userData)
            .addOnSuccessListener(v -> {
                session.saveSession(uid, name, email, selectedRole, selectedCourse, selectedSemester, "teacher".equals(selectedRole) ? subject : "");
                showLoading(false);
                binding.tvSuccessSub.setText("You're enrolled in " + selectedCourse + " as a " + selectedRole.toUpperCase());
                goToStep(3);
                redirectToDashboard();
            })
            .addOnFailureListener(e -> {
                showLoading(false);
                Toast.makeText(this, "Firestore error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }

    private void showLoading(boolean show) {
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.viewFlipper.setEnabled(!show);
    }

    private void redirectToDashboard() {
        new Handler().postDelayed(this::redirectToDashboardNow, 2000);
    }

    private void redirectToDashboardNow() {
        Intent intent = "teacher".equals(selectedRole) ? 
            new Intent(this, TeacherDashboardActivity.class) : 
            new Intent(this, StudentDashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
