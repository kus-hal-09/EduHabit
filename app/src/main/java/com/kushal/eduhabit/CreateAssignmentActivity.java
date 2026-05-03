package com.kushal.eduhabit;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kushal.eduhabit.databinding.ActivityCreateAssignmentBinding;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class CreateAssignmentActivity extends AppCompatActivity {

    private ActivityCreateAssignmentBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private SessionManager session;
    private Calendar selectedDeadline = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateAssignmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        session = new SessionManager(this);

        binding.backBtn.setOnClickListener(v -> finish());
        binding.etDueDate.setOnClickListener(v -> showDatePicker());
        binding.btnPublish.setOnClickListener(v -> createAssignment());
        
        setupSubjectSpinner();
        setupSemesterSpinner();
    }

    private void setupSubjectSpinner() {
        String course = session.getCourse();
        List<String> allSubjects = new ArrayList<>();
        allSubjects.add("General");
        for (int i = 1; i <= 8; i++) {
            allSubjects.addAll(CourseData.getSubjects(course, String.valueOf(i)));
        }
        List<String> unique = new ArrayList<>(new LinkedHashSet<>(allSubjects));
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, unique);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerModule.setAdapter(adapter);
    }

    private void setupSemesterSpinner() {
        String[] sems = {"All Semesters", "1st", "2nd", "3rd", "4th", "5th", "6th", "7th", "8th"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerSemester.setAdapter(adapter);
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        DatePickerDialog dpd = new DatePickerDialog(this, (view, year, month, day) -> {
            selectedDeadline.set(year, month, day);
            binding.etDueDate.setText(day + "/" + (month + 1) + "/" + year);
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        dpd.show();
    }

    private void createAssignment() {
        String title = binding.etTitle.getText().toString().trim();
        String module = binding.spinnerModule.getSelectedItem().toString();
        String targetSemester = binding.spinnerSemester.getSelectedItem().toString();
        String desc = binding.etDescription.getText().toString().trim();

        if (title.isEmpty()) {
            binding.etTitle.setError("Title is required");
            return;
        }

        binding.btnPublish.setEnabled(false);

        Map<String, Object> assignment = new HashMap<>();
        assignment.put("title", title);
        assignment.put("module", module);
        assignment.put("description", desc);
        assignment.put("teacherId", session.getUid());
        assignment.put("teacherName", session.getName());
        assignment.put("course", session.getCourse());
        assignment.put("semester", targetSemester);
        assignment.put("deadline", selectedDeadline.getTime());
        assignment.put("status", "active");
        assignment.put("createdAt", FieldValue.serverTimestamp());

        db.collection("assignments").add(assignment)
                .addOnSuccessListener(ref -> {
                    Toast.makeText(this, "Assignment created!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    binding.btnPublish.setEnabled(true);
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
