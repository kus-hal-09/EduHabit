package com.kushal.eduhabit;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kushal.eduhabit.databinding.FragmentHomeBinding;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        binding.grammarCard.setOnClickListener(v -> startActivity(new Intent(getContext(), GrammarPracticeActivity.class)));
        binding.vocabularyCard.setOnClickListener(v -> startActivity(new Intent(getContext(), VocabularyPracticeActivity.class)));

        fetchUserData();
    }

    private void fetchUserData() {
        if (mAuth.getCurrentUser() == null) return;
        String userId = mAuth.getCurrentUser().getUid();
        
        db.collection("users").document(userId).addSnapshotListener((doc, error) -> {
            if (doc != null && doc.exists()) {
                long xp = doc.contains("xp") ? doc.getLong("xp") : 0;
                long level = (xp / 100) + 1;
                long streak = doc.contains("streak") ? doc.getLong("streak") : 0;

                binding.levelLabel.setText("Level " + level);
                binding.streakValue.setText(streak + " Days");
                binding.levelProgress.setProgress((int)(xp % 100));
                
                String title = "Beginner";
                if (level > 10) title = "Advanced";
                else if (level > 5) title = "Intermediate";
                binding.levelTitle.setText(title);
            }
        });

        db.collection("submissions")
                .whereEqualTo("studentId", userId)
                .addSnapshotListener((snap, error) -> {
                    if (snap != null) {
                        int gradedCount = 0;
                        for (DocumentSnapshot doc : snap.getDocuments()) {
                            if ("graded".equals(doc.getString("status"))) gradedCount++;
                        }
                        binding.tasksCompleted.setText(String.valueOf(gradedCount));
                        updateActivityChart(snap.getDocuments());
                    }
                });
    }

    private void updateActivityChart(List<DocumentSnapshot> submissions) {
        BarChart chart = binding.weeklyActivityChart;
        int[] dailyCounts = new int[7];
        Calendar cal = Calendar.getInstance();
        for (DocumentSnapshot doc : submissions) {
            Long timestamp = doc.getLong("submittedAt");
            if (timestamp != null) {
                cal.setTimeInMillis(timestamp);
                int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
                int index = (dayOfWeek + 5) % 7; 
                dailyCounts[index]++;
            }
        }

        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < 7; i++) entries.add(new BarEntry(i, dailyCounts[i]));

        BarDataSet dataSet = new BarDataSet(entries, "Activity");
        dataSet.setColor(Color.parseColor("#4F46E5"));
        dataSet.setDrawValues(false);

        chart.setData(new BarData(dataSet));
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.animateY(1000);
        chart.invalidate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}