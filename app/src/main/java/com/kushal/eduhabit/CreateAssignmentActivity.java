package com.kushal.eduhabit;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kushal.eduhabit.databinding.ActivityCreateAssignmentBinding;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CreateAssignmentActivity extends AppCompatActivity {

    private ActivityCreateAssignmentBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String selectedDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateAssignmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        binding.backBtn.setOnClickListener(v -> finish());

        // Date Picker Logic
        binding.etDueDate.setOnClickListener(v -> showDatePicker());

        // Publish Action
        binding.btnPublish.setOnClickListener(v -> publishAssignment());
        
        binding.btnSaveDraft.setOnClickListener(v -> {
            Toast.makeText(this, "Draft saved locally", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1;
                    binding.etDueDate.setText(selectedDate);
                }, year, month, day);
        datePickerDialog.show();
    }

    private void publishAssignment() {
        String title = binding.etTitle.getText().toString().trim();
        String desc = binding.etDescription.getText().toString().trim();
        String points = binding.etPoints.getText().toString().trim();

        if (title.isEmpty() || desc.isEmpty() || selectedDate.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.btnPublish.setEnabled(false);
        binding.btnPublish.setText("Publishing...");

        Map<String, Object> assignment = new HashMap<>();
        assignment.put("title", title);
        assignment.put("description", desc);
        assignment.put("points", points);
        assignment.put("dueDate", selectedDate);
        assignment.put("teacherId", mAuth.getCurrentUser().getUid());
        assignment.put("createdAt", System.currentTimeMillis());
        assignment.put("status", "active");

        db.collection("assignments").add(assignment)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Assignment published successfully!", Toast.LENGTH_LONG).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    binding.btnPublish.setEnabled(true);
                    binding.btnPublish.setText("Publish");
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}