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
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kushal.eduhabit.databinding.FragmentHomeBinding;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

        // Learning Modules Navigation
        binding.grammarCard.setOnClickListener(v -> 
            startActivity(new Intent(getContext(), GrammarActivity.class)));

        binding.vocabularyCard.setOnClickListener(v -> 
            startActivity(new Intent(getContext(), VocabularyPracticeActivity.class)));

        binding.achievementsCard.setOnClickListener(v -> 
            startActivity(new Intent(getContext(), AchievementsActivity.class)));
        
        binding.tasksCard.setOnClickListener(v -> {
            if (getActivity() instanceof StudentDashboardActivity) {
                ((StudentDashboardActivity) getActivity()).switchToTasks();
            }
        });

        // Fun Learning Zone Navigation
        binding.btnPlayWordMatch.setOnClickListener(v -> 
            startActivity(new Intent(getContext(), WordMatchGameActivity.class)));

        binding.btnPlayGuessNumber.setOnClickListener(v -> 
            startActivity(new Intent(getContext(), GuessNumberGameActivity.class)));

        fetchUserData();
        fetchPendingAssignmentsCount();
    }

    private void fetchUserData() {
        if (mAuth.getCurrentUser() == null) return;
        String userId = mAuth.getCurrentUser().getUid();
        
        db.collection("users").document(userId).addSnapshotListener((doc, error) -> {
            if (doc != null && doc.exists() && binding != null) {
                Long xpLong = doc.getLong("xp");
                long xp = xpLong != null ? xpLong : 0;
                long level = (xp / 100) + 1;
                
                Long streakLong = doc.getLong("streak");
                long streak = streakLong != null ? streakLong : 0;

                binding.levelLabel.setText(getString(R.string.level_display, (int)level));
                binding.streakValue.setText(getString(R.string.streak_display, (int)streak));
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
                    if (snap != null && binding != null) {
                        int gradedCount = 0;
                        for (DocumentSnapshot doc : snap.getDocuments()) {
                            if ("graded".equals(doc.getString("status"))) gradedCount++;
                        }
                        binding.tasksCompleted.setText(String.valueOf(gradedCount));
                        updateActivityChart(snap.getDocuments());
                    }
                });
    }

    private void fetchPendingAssignmentsCount() {
        if (mAuth.getCurrentUser() == null) return;
        String userId = mAuth.getCurrentUser().getUid();

        db.collection("submissions")
                .whereEqualTo("studentId", userId)
                .get()
                .addOnSuccessListener(submissionSnaps -> {
                    if (binding == null) return;
                    Set<String> submittedIds = new HashSet<>();
                    for (DocumentSnapshot doc : submissionSnaps) {
                        submittedIds.add(doc.getString("assignmentId"));
                    }

                    db.collection("assignments").get().addOnSuccessListener(assignmentSnaps -> {
                        if (binding == null) return;
                        int pendingCount = 0;
                        for (DocumentSnapshot doc : assignmentSnaps) {
                            if (!submittedIds.contains(doc.getId())) {
                                pendingCount++;
                            }
                        }
                        binding.tvTasksStatus.setText(getString(R.string.pending_tasks, pendingCount));
                    });
                });
    }

    private void updateActivityChart(List<DocumentSnapshot> submissions) {
        if (binding == null) return;
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
        for (int i = 0; i < 7; i++) {
            entries.add(new BarEntry(i, dailyCounts[i]));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Activities Completed");
        dataSet.setColor(Color.parseColor("#6366F1"));
        dataSet.setDrawValues(true);
        dataSet.setValueTextSize(10f);
        dataSet.setValueTextColor(Color.parseColor("#64748B"));

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.6f);

        chart.setData(data);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);

        XAxis xAxis = chart.getXAxis();
        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        xAxis.setValueFormatter(new IndexAxisValueFormatter(days));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setGranularity(1f);
        xAxis.setTextColor(Color.parseColor("#64748B"));

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.parseColor("#E2E8F0"));
        leftAxis.setDrawAxisLine(false);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setGranularity(1f);
        leftAxis.setTextColor(Color.parseColor("#64748B"));

        chart.getAxisRight().setEnabled(false);
        chart.animateY(1200);
        chart.invalidate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
