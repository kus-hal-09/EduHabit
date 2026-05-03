package com.kushal.eduhabit;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
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
    private SessionManager session;
    private ActivityAdapter activityAdapter;
    private List<DocumentSnapshot> activityList = new ArrayList<>();
    private List<ListenerRegistration> listeners = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTeacherDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        session = new SessionManager(this);

        // Animation for the professional SaaS look
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
        
        // Initials Logic for the circular avatar
        if (!name.isEmpty()) {
            String initials = name.substring(0, 1).toUpperCase();
            if (name.contains(" ")) {
                initials += name.substring(name.indexOf(" ") + 1, name.indexOf(" ") + 2).toUpperCase();
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
        // Clear listeners BEFORE signing out to avoid "Permission Denied" errors
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
    }

    private void fetchRealTimeStats() {
        String course = session.getCourse();
        String semester = session.getSemester();
        String teacherId = session.getUid();

        // Count students in same faculty (Excluding current user just in case)
        listeners.add(db.collection("users")
                .whereEqualTo("role", "student")
                .whereEqualTo("course", course)
                .whereEqualTo("semester", semester)
                .addSnapshotListener((snap, e) -> {
                    if (e != null) return; // Ignore errors after logout
                    if (snap != null) {
                        int studentCount = 0;
                        for (DocumentSnapshot doc : snap.getDocuments()) {
                            if (!doc.getId().equals(teacherId)) {
                                studentCount++;
                            }
                        }
                        binding.tvStatStudents.setText(String.valueOf(studentCount));
                    }
                }));

        // Count teacher's assignments
        listeners.add(db.collection("assignments")
                .whereEqualTo("teacherId", teacherId)
                .addSnapshotListener((snap, e) -> {
                    if (e != null) return;
                    if (snap != null) binding.tvStatAssignments.setText(String.valueOf(snap.size()));
                }));

        // Sync analytical charts
        listeners.add(db.collection("submissions")
                .whereEqualTo("course", course)
                .whereEqualTo("semester", semester)
                .addSnapshotListener((snap, e) -> {
                    if (e != null) return;
                    if (snap != null) {
                        aggregateAnalytics(snap.getDocuments());
                    }
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
            if ("pending".equals(status) || "submitted".equals(status)) pending++;
            else if ("graded".equals(status)) graded++;

            Object tsObj = doc.get("submittedAt");
            if (tsObj instanceof com.google.firebase.Timestamp) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(((com.google.firebase.Timestamp)tsObj).toDate());
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

        setupDonutChart(pending, graded);
        setupBarChart(monthlyData);
        setupLineChart(trendEntries);
    }

    private void setupDonutChart(int pending, int graded) {
        List<PieEntry> entries = new ArrayList<>();
        if (pending > 0) entries.add(new PieEntry(pending, "Pending"));
        if (graded > 0) entries.add(new PieEntry(graded, "Graded"));

        PieDataSet set = new PieDataSet(entries, "");
        set.setColors(new int[]{Color.parseColor("#F59E0B"), Color.parseColor("#10B981")});
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(12f);

        binding.pieChart.setData(new PieData(set));
        binding.pieChart.setDrawHoleEnabled(true);
        binding.pieChart.setHoleRadius(65f);
        binding.pieChart.setCenterText("Overview");
        binding.pieChart.setCenterTextSize(14f);
        binding.pieChart.setCenterTextColor(Color.parseColor("#64748B"));
        binding.pieChart.getDescription().setEnabled(false);
        binding.pieChart.getLegend().setEnabled(false);
        binding.pieChart.animateXY(800, 800);
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
        binding.barChart.animateY(1000);
        binding.barChart.invalidate();
    }

    private void setupLineChart(List<Entry> entries) {
        LineDataSet set = new LineDataSet(entries, "Growth Trend");
        set.setColor(Color.parseColor("#8B5CF6"));
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setDrawFilled(true);
        set.setFillColor(Color.parseColor("#F5F3FF"));
        binding.lineChart.setData(new LineData(set));
        binding.lineChart.getDescription().setEnabled(false);
        binding.lineChart.animateX(1000);
        binding.lineChart.invalidate();
    }

    private void fetchActivityFeed() {
        listeners.add(db.collection("submissions")
                .whereEqualTo("course", session.getCourse())
                .whereEqualTo("semester", session.getSemester())
                .orderBy("submittedAt", Query.Direction.DESCENDING)
                .limit(10)
                .addSnapshotListener((snap, e) -> {
                    if (e != null) return;
                    if (snap != null) {
                        activityList.clear();
                        activityList.addAll(snap.getDocuments());
                        activityAdapter.notifyDataSetChanged();
                    }
                }));
    }

    private void listenToNotifications() {
        String uid = session.getUid();
        listeners.add(db.collection("notifications")
                .whereEqualTo("teacherId", uid)
                .whereEqualTo("read", false)
                .addSnapshotListener((snap, e) -> {
                    if (e != null) return;
                    if (snap != null) {
                        int count = snap.size();
                        if (count > 0) {
                            binding.tvNotifBadge.setVisibility(View.VISIBLE);
                            binding.tvNotifBadge.setText(String.valueOf(count));
                        } else {
                            binding.tvNotifBadge.setVisibility(View.GONE);
                        }
                    }
                }));
    }
}
