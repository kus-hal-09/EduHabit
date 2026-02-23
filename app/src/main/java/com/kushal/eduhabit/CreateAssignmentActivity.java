package com.kushal.eduhabit;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kushal.eduhabit.databinding.ActivityCreateAssignmentBinding;
import java.util.HashMap;
import java.util.Map;

public class CreateAssignmentActivity extends AppCompatActivity {

    private ActivityCreateAssignmentBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateAssignmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        binding.btnCreate.setOnClickListener(v -> createAssignment());
    }

    private void createAssignment() {
        String title = binding.titleEditText.getText().toString().trim();
        String desc = binding.descEditText.getText().toString().trim();
        String dueDate = binding.dateEditText.getText().toString().trim();

        if (title.isEmpty() || desc.isEmpty() || dueDate.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> assignment = new HashMap<>();
        assignment.put("title", title);
        assignment.put("description", desc);
        assignment.put("dueDate", dueDate);
        assignment.put("teacherId", mAuth.getCurrentUser().getUid());
        assignment.put("createdAt", System.currentTimeMillis());

        db.collection("assignments").add(assignment)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Assignment Created!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}