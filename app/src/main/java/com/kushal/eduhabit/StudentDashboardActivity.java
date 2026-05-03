package com.kushal.eduhabit;

import android.os.Bundle;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kushal.eduhabit.databinding.ActivityStudentDashboardBinding;

public class StudentDashboardActivity extends AppCompatActivity {

    private ActivityStudentDashboardBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Fragment homeFragment, tasksFragment, coursesFragment, analyticsFragment, profileFragment;
    private Fragment activeFragment;
    private final FragmentManager fm = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStudentDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initFragments();
        fetchGlobalUserData();

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                switchFragment(homeFragment);
                return true;
            } else if (itemId == R.id.nav_tasks) {
                switchFragment(tasksFragment);
                return true;
            } else if (itemId == R.id.nav_courses) {
                switchFragment(coursesFragment);
                return true;
            } else if (itemId == R.id.nav_analytics) {
                switchFragment(analyticsFragment);
                return true;
            } else if (itemId == R.id.nav_profile) {
                switchFragment(profileFragment);
                return true;
            }
            return false;
        });

        // Fixed Deprecated OnBackPressed
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (activeFragment != homeFragment) {
                    binding.bottomNavigation.setSelectedItemId(R.id.nav_home);
                    switchFragment(homeFragment);
                } else {
                    finish();
                }
            }
        });
    }

    public void updateXP(Long xp) {
        if (xp != null && binding != null) {
            binding.xpPoints.setText(getString(R.string.xp_display, xp));
        }
    }

    public void switchToTasks() {
        binding.bottomNavigation.setSelectedItemId(R.id.nav_tasks);
        switchFragment(tasksFragment);
    }

    private void fetchGlobalUserData() {
        if (mAuth.getCurrentUser() == null) return;
        String userId = mAuth.getCurrentUser().getUid();

        db.collection("users").document(userId).addSnapshotListener((doc, error) -> {
            if (doc != null && doc.exists()) {
                String name = doc.getString("name");
                if (name == null) name = getString(R.string.default_learner);
                
                binding.welcomeText.setText(getString(R.string.welcome_message, name));
                
                updateXP(doc.getLong("xp"));
            }
        });
    }

    private void initFragments() {
        homeFragment = new HomeFragment();
        tasksFragment = new TasksFragment();
        coursesFragment = new CoursesFragment();
        analyticsFragment = new AnalyticsFragment();
        profileFragment = new ProfileFragment();

        activeFragment = homeFragment;

        fm.beginTransaction().add(R.id.fragment_container, profileFragment, "5").hide(profileFragment).commit();
        fm.beginTransaction().add(R.id.fragment_container, analyticsFragment, "4").hide(analyticsFragment).commit();
        fm.beginTransaction().add(R.id.fragment_container, coursesFragment, "3").hide(coursesFragment).commit();
        fm.beginTransaction().add(R.id.fragment_container, tasksFragment, "2").hide(tasksFragment).commit();
        fm.beginTransaction().add(R.id.fragment_container, homeFragment, "1").commit();
    }

    private void switchFragment(Fragment fragment) {
        if (fragment != activeFragment) {
            fm.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .hide(activeFragment)
                .show(fragment)
                .commit();
            activeFragment = fragment;
        }
    }
}
