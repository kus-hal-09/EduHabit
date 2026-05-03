package com.kushal.eduhabit;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.kushal.eduhabit.databinding.FragmentAnalyticsBinding;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnalyticsFragment extends Fragment {

    private FragmentAnalyticsBinding binding;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAnalyticsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        fetchAnalyticsData();
    }

    private void fetchAnalyticsData() {
        if (mAuth.getCurrentUser() == null) return;
        String userId = mAuth.getCurrentUser().getUid();

        // 1. Fetch User Stats (XP)
        db.collection("users").document(userId).addSnapshotListener((doc, e) -> {
            if (doc != null && doc.exists()) {
                Long xp = doc.getLong("xp");
                binding.tvTotalXp.setText(String.valueOf(xp != null ? xp : 0));
            }
        });

        // 2. Fetch Submissions for Score & Charts
        db.collection("submissions")
                .whereEqualTo("studentId", userId)
                .orderBy("submittedAt", Query.Direction.ASCENDING)
                .addSnapshotListener((snap, e) -> {
                    if (snap == null || snap.isEmpty()) return;

                    int totalGrade = 0;
                    int gradedCount = 0;
                    List<Entry> trendEntries = new ArrayList<>();
                    Map<String, Integer> skillMap = new HashMap<>();

                    List<DocumentSnapshot> docs = snap.getDocuments();
                    for (int i = 0; i < docs.size(); i++) {
                        DocumentSnapshot doc = docs.get(i);
                        
                        // Calculate Avg Score
                        if ("graded".equals(doc.getString("status"))) {
                            try {
                                String gradeStr = doc.getString("grade");
                                if (gradeStr != null) {
                                    int grade = Integer.parseInt(gradeStr.replaceAll("[^0-9]", ""));
                                    totalGrade += grade;
                                    gradedCount++;
                                }
                            } catch (Exception ignored) {}
                        }

                        // Line Chart Data (Submission count index vs some value, e.g., 1 per submission)
                        trendEntries.add(new Entry(i, i + 1));

                        // Pie Chart Data (by Assignment Title or Category if available)
                        String title = doc.getString("assignmentTitle");
                        if (title != null) {
                            String category = "General";
                            if (title.toLowerCase().contains("grammar")) category = "Grammar";
                            else if (title.toLowerCase().contains("vocab")) category = "Vocabulary";
                            else if (title.toLowerCase().contains("speak")) category = "Speaking";
                            
                            skillMap.put(category, skillMap.getOrDefault(category, 0) + 1);
                        }
                    }

                    if (gradedCount > 0) {
                        binding.tvAvgScore.setText((totalGrade / gradedCount) + "%");
                    } else {
                        binding.tvAvgScore.setText("0%");
                    }

                    setupLineChart(trendEntries);
                    setupPieChart(skillMap);
                });
    }

    private void setupLineChart(List<Entry> entries) {
        LineChart chart = binding.submissionTrendChart;
        LineDataSet dataSet = new LineDataSet(entries, "Submissions");
        dataSet.setColor(Color.parseColor("#4F46E5"));
        dataSet.setCircleColor(Color.parseColor("#4F46E5"));
        dataSet.setLineWidth(2f);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.parseColor("#EEF2FF"));

        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.animateX(1000);
        chart.invalidate();
    }

    private void setupPieChart(Map<String, Integer> skillMap) {
        PieChart chart = binding.skillPieChart;
        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : skillMap.entrySet()) {
            entries.add(new PieEntry(entry.getValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(new int[]{
                Color.parseColor("#4F46E5"), 
                Color.parseColor("#10B981"), 
                Color.parseColor("#F59E0B"),
                Color.parseColor("#DB2777")
        });
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setValueTextSize(12f);

        PieData pieData = new PieData(dataSet);
        chart.setData(pieData);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setVerticalAlignment(com.github.mikephil.charting.components.Legend.LegendVerticalAlignment.BOTTOM);
        chart.setDrawHoleEnabled(true);
        chart.setHoleRadius(50f);
        chart.animateY(1000);
        chart.invalidate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}