package com.kushal.eduhabit;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.kushal.eduhabit.databinding.ActivityTeacherDashboardBinding;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeacherDashboardActivity extends AppCompatActivity {

    private ActivityTeacherDashboardBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ActivityAdapter activityAdapter;
    private List<DocumentSnapshot> activityList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTeacherDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Screen load animation
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        binding.mainContent.startAnimation(fadeIn);
        binding.contentContainer.setVisibility(View.VISIBLE);

        setupUI();
        fetchRealTimeStats();
        fetchActivityFeed();
        listenToNotifications();
    }

    private void setupUI() {
        activityAdapter = new ActivityAdapter(activityList);
        binding.rvActivityFeed.setLayoutManager(new LinearLayoutManager(this));
        binding.rvActivityFeed.setAdapter(activityAdapter);

        binding.btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        binding.rlNotifications.setOnClickListener(v -> {
            NotificationBottomSheet bottomSheet = new NotificationBottomSheet();
            bottomSheet.show(getSupportFragmentManager(), "notifications");
        });

        binding.cardCreateAssignment.setOnClickListener(v -> 
            startActivity(new Intent(this, CreateAssignmentActivity.class)));
        
        binding.cardViewSubmissions.setOnClickListener(v -> 
            startActivity(new Intent(this, SubmissionsListActivity.class)));

        binding.teacherBottomNavigation.setSelectedItemId(R.id.nav_teacher_home);
        binding.teacherBottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_teacher_home) return true;
            if (id == R.id.nav_teacher_tasks) {
                startActivity(new Intent(this, SubmissionsListActivity.class));
                return true;
            }
            if (id == R.id.nav_teacher_students) {
                startActivity(new Intent(this, StudentsListActivity.class));
                return true;
            }
            if (id == R.id.nav_teacher_analytics) {
                startActivity(new Intent(this, AnalyticsActivity.class));
                return true;
            }
            if (id == R.id.nav_teacher_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });
    }

    private void fetchRealTimeStats() {
        if (mAuth.getCurrentUser() == null) return;

        db.collection("users").document(mAuth.getUid()).addSnapshotListener((doc, e) -> {
            if (doc != null && doc.exists()) {
                binding.tvTeacherName.setText(doc.getString("name"));
            }
        });

        db.collection("users").whereEqualTo("role", "student").addSnapshotListener((snap, e) -> {
            if (snap != null) binding.tvStatStudents.setText(String.valueOf(snap.size()));
        });

        db.collection("assignments").addSnapshotListener((snap, e) -> {
            if (snap != null) binding.tvStatAssignments.setText(String.valueOf(snap.size()));
        });

        db.collection("submissions").addSnapshotListener((snap, e) -> {
            if (e != null) {
                Snackbar.make(binding.getRoot(), "Failed to sync statistics", Snackbar.LENGTH_LONG).show();
                return;
            }
            if (snap != null) {
                aggregateAnalytics(snap.getDocuments());
            }
        });
    }

    private void aggregateAnalytics(List<DocumentSnapshot> submissions) {
        int total = submissions.size();
        int pending = 0;
        int graded = 0;
        Map<Integer, Integer> monthlyData = new HashMap<>();
        List<Entry> trendEntries = new ArrayList<>();

        for (int i = 0; i < submissions.size(); i++) {
            DocumentSnapshot doc = submissions.get(i);
            String status = doc.getString("status");
            if ("pending".equals(status)) pending++;
            else graded++;

            Long timestamp = doc.getLong("submittedAt");
            if (timestamp != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(timestamp);
                int month = cal.get(Calendar.MONTH);
                monthlyData.put(month, monthlyData.getOrDefault(month, 0) + 1);
                trendEntries.add(new Entry(i, i + 1));
            }
        }

        binding.tvStatPending.setText(String.valueOf(pending));
        if (total > 0) {
            int perf = (graded * 100) / total;
            binding.tvStatGrade.setText(perf + "%");
        }

        setupPieChart(pending, graded);
        setupBarChart(monthlyData);
        setupLineChart(trendEntries);
    }

    private void setupPieChart(int pending, int graded) {
        List<PieEntry> entries = new ArrayList<>();
        if (pending > 0) entries.add(new PieEntry(pending, "Pending"));
        if (graded > 0) entries.add(new PieEntry(graded, "Graded"));
        
        PieDataSet set = new PieDataSet(entries, "");
        set.setColors(new int[]{getColor(R.color.accent_amber), getColor(R.color.success_green)});
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(12f);

        binding.pieChart.setData(new PieData(set));
        binding.pieChart.setDrawHoleEnabled(true);
        binding.pieChart.setHoleRadius(50f);
        binding.pieChart.setCenterText("Submissions");
        binding.pieChart.getDescription().setEnabled(false);
        binding.pieChart.animateXY(800, 800);
        binding.pieChart.invalidate();
    }

    private void setupBarChart(Map<Integer, Integer> data) {
        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            entries.add(new BarEntry(i, data.getOrDefault(i, 0)));
        }
        BarDataSet set = new BarDataSet(entries, "Monthly");
        set.setColor(getColor(R.color.accent_indigo));
        binding.barChart.setData(new BarData(set));
        binding.barChart.getDescription().setEnabled(false);
        binding.barChart.getXAxis().setDrawGridLines(false);
        binding.barChart.animateY(1000);
        binding.barChart.invalidate();
    }

    private void setupLineChart(List<Entry> entries) {
        LineDataSet set = new LineDataSet(entries, "Trend");
        set.setColor(getColor(R.color.accent_indigo));
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawFilled(true);
        set.setFillColor(getColor(R.color.pastel_indigo));
        binding.lineChart.setData(new LineData(set));
        binding.lineChart.getDescription().setEnabled(false);
        binding.lineChart.animateX(1000);
        binding.lineChart.invalidate();
    }

    private void fetchActivityFeed() {
        db.collection("submissions").orderBy("submittedAt", Query.Direction.DESCENDING).limit(10)
            .addSnapshotListener((snap, e) -> {
                if (snap != null) {
                    activityList.clear();
                    activityList.addAll(snap.getDocuments());
                    activityAdapter.notifyDataSetChanged();
                }
            });
    }

    private void listenToNotifications() {
        String uid = mAuth.getUid();
        if (uid == null) return;
        db.collection("notifications")
            .whereEqualTo("teacherId", uid)
            .whereEqualTo("read", false)
            .addSnapshotListener((snap, e) -> {
                if (snap != null) {
                    int count = snap.size();
                    if (count > 0) {
                        binding.tvNotifBadge.setVisibility(View.VISIBLE);
                        binding.tvNotifBadge.setText(String.valueOf(count));
                    } else {
                        binding.tvNotifBadge.setVisibility(View.GONE);
                    }
                }
            });
    }
}