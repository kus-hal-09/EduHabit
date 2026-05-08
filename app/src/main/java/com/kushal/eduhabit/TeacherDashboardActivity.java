package com.kushal.eduhabit;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.kushal.eduhabit.databinding.ActivityTeacherDashboardBinding;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TeacherDashboardActivity extends AppCompatActivity {

    private ActivityTeacherDashboardBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private SessionManager session;
    private ActivityAdapter activityAdapter;
    private List<DocumentSnapshot> activityList = new ArrayList<>();
    private List<ListenerRegistration> listeners = new ArrayList<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTeacherDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Handle system bars overlap
        ViewCompat.setOnApplyWindowInsetsListener(binding.mainContent, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            binding.appBar.setPadding(0, systemBars.top, 0, 0);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        session = new SessionManager(this);

        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        binding.mainContent.startAnimation(fadeIn);

        setupUI();
        fetchRealTimeStats();
        fetchActivityFeed();
        listenToNotifications();
    }

    private void setupUI() {
        String name = session.getName();
        binding.tvTeacherName.setText(name.isEmpty() ? "Educator" : name);
        
        if (!name.isEmpty()) {
            String initials = name.substring(0, 1).toUpperCase();
            if (name.contains(" ")) {
                int nextSpace = name.indexOf(" ");
                if (nextSpace + 1 < name.length()) {
                    initials += name.substring(nextSpace + 1, nextSpace + 2).toUpperCase();
                }
            }
            binding.tvTeacherInitials.setText(initials);
        }

        activityAdapter = new ActivityAdapter(activityList);
        binding.rvActivityFeed.setLayoutManager(new LinearLayoutManager(this));
        binding.rvActivityFeed.setAdapter(activityAdapter);

        binding.btnLogout.setOnClickListener(v -> showLogoutConfirmation());

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

    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> logout())
                .setNegativeButton("No", null)
                .show();
    }

    private void logout() {
        removeListeners();
        mAuth.signOut();
        session.clearSession();
        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void removeListeners() {
        for (ListenerRegistration listener : listeners) {
            if (listener != null) listener.remove();
        }
        listeners.clear();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeListeners();
        executorService.shutdown();
    }

    private void fetchRealTimeStats() {
        String course = session.getCourse();
        String semester = session.getSemester();
        String teacherId = session.getUid();

        // Query 1: Students count
        listeners.add(db.collection("users")
                .whereEqualTo("role", "student")
                .whereEqualTo("course", course)
                .whereEqualTo("semester", semester)
                .addSnapshotListener((snap, e) -> {
                    if (e != null || snap == null) return;
                    binding.tvStatStudents.setText(String.valueOf(snap.size()));
                }));

        // Query 2: Teacher's assignments
        listeners.add(db.collection("assignments")
                .whereEqualTo("teacherId", teacherId)
                .addSnapshotListener((snap, e) -> {
                    if (e != null || snap == null) return;
                    binding.tvStatAssignments.setText(String.valueOf(snap.size()));
                }));

        // Query 3: Submissions analytics
        listeners.add(db.collection("submissions")
                .whereEqualTo("course", course)
                .whereEqualTo("semester", semester)
                .addSnapshotListener((snap, e) -> {
                    if (e != null || snap == null) return;
                    executorService.execute(() -> aggregateAnalytics(snap.getDocuments()));
                }));
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
            if ("graded".equals(status)) graded++;
            else pending++;

            Object tsObj = doc.get("submittedAt");
            if (tsObj instanceof com.google.firebase.Timestamp) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(((com.google.firebase.Timestamp)tsObj).toDate());
                int month = cal.get(Calendar.MONTH);
                monthlyData.put(month, monthlyData.getOrDefault(month, 0) + 1);
            }
        }
        
        for (int i = 0; i < Math.min(10, submissions.size()); i++) {
            trendEntries.add(new Entry(i, i + 1));
        }

        final int finalPending = pending;
        final int finalGraded = graded;
        final int finalTotal = total;
        
        runOnUiThread(() -> {
            binding.tvStatPending.setText(String.valueOf(finalPending));
            if (finalTotal > 0) {
                int perf = (finalGraded * 100) / finalTotal;
                binding.tvStatGrade.setText(perf + "%");
            } else {
                binding.tvStatGrade.setText("0%");
            }

            setupDonutChart(finalPending, finalGraded);
            setupBarChart(monthlyData);
            setupLineChart(trendEntries);
        });
    }

    private void setupDonutChart(int pending, int graded) {
        List<PieEntry> entries = new ArrayList<>();
        if (pending > 0) entries.add(new PieEntry(pending, "Pending"));
        if (graded > 0) entries.add(new PieEntry(graded, "Graded"));

        if (entries.isEmpty()) entries.add(new PieEntry(1, "No Data"));

        PieDataSet set = new PieDataSet(entries, "");
        set.setColors(new int[]{Color.parseColor("#F59E0B"), Color.parseColor("#10B981"), Color.parseColor("#E2E8F0")});
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(12f);

        binding.pieChart.setData(new PieData(set));
        binding.pieChart.setDrawHoleEnabled(true);
        binding.pieChart.setHoleRadius(65f);
        binding.pieChart.setCenterText("Overview");
        binding.pieChart.setCenterTextColor(Color.parseColor("#64748B"));
        binding.pieChart.getDescription().setEnabled(false);
        binding.pieChart.getLegend().setEnabled(false);
        binding.pieChart.invalidate();
    }

    private void setupBarChart(Map<Integer, Integer> data) {
        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            entries.add(new BarEntry(i, data.getOrDefault(i, 0)));
        }
        BarDataSet set = new BarDataSet(entries, "Monthly Submissions");
        set.setColor(Color.parseColor("#6366F1"));
        binding.barChart.setData(new BarData(set));
        binding.barChart.getDescription().setEnabled(false);
        binding.barChart.getXAxis().setDrawGridLines(false);
        binding.barChart.invalidate();
    }

    private void setupLineChart(List<Entry> entries) {
        LineDataSet set = new LineDataSet(entries, "Activity");
        set.setColor(Color.parseColor("#8B5CF6"));
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawFilled(true);
        set.setFillColor(Color.parseColor("#F5F3FF"));
        binding.lineChart.setData(new LineData(set));
        binding.lineChart.getDescription().setEnabled(false);
        binding.lineChart.invalidate();
    }

    private void fetchActivityFeed() {
        // Optimization: Remove orderBy from Firestore to avoid index requirement
        listeners.add(db.collection("submissions")
                .whereEqualTo("course", session.getCourse())
                .whereEqualTo("semester", session.getSemester())
                .limit(20)
                .addSnapshotListener((snap, e) -> {
                    if (e != null || snap == null) return;
                    
                    List<DocumentSnapshot> docs = new ArrayList<>(snap.getDocuments());
                    // Sort locally
                    Collections.sort(docs, (d1, d2) -> {
                        Object t1 = d1.get("submittedAt");
                        Object t2 = d2.get("submittedAt");
                        if (t1 instanceof com.google.firebase.Timestamp && t2 instanceof com.google.firebase.Timestamp) {
                            return ((com.google.firebase.Timestamp) t2).compareTo((com.google.firebase.Timestamp) t1);
                        }
                        return 0;
                    });

                    activityList.clear();
                    for (int i = 0; i < Math.min(10, docs.size()); i++) {
                        activityList.add(docs.get(i));
                    }
                    activityAdapter.notifyDataSetChanged();
                }));
    }

    private void listenToNotifications() {
        String uid = session.getUid();
        listeners.add(db.collection("notifications")
                .whereEqualTo("teacherId", uid)
                .whereEqualTo("read", false)
                .addSnapshotListener((snap, e) -> {
                    if (e != null || snap == null) return;
                    int count = snap.size();
                    binding.tvNotifBadge.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
                    binding.tvNotifBadge.setText(String.valueOf(count));
                }));
    }
}
