package com.kushal.eduhabit;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.kushal.eduhabit.databinding.ActivityStudentDashboardBinding;

public class StudentDashboardActivity extends AppCompatActivity {

    private ActivityStudentDashboardBinding binding;
    private Fragment homeFragment, tasksFragment, coursesFragment, analyticsFragment, profileFragment;
    private Fragment activeFragment;
    private final FragmentManager fm = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStudentDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initFragments();

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
    }

    private void initFragments() {
        homeFragment = new HomeFragment();
        tasksFragment = new TasksFragment();
        coursesFragment = new CoursesFragment();
        analyticsFragment = new AnalyticsFragment();
        profileFragment = new ProfileFragment();

        activeFragment = homeFragment;

        // Initialize all fragments but hide others to preserve state
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

    @Override
    public void onBackPressed() {
        if (activeFragment != homeFragment) {
            binding.bottomNavigation.setSelectedItemId(R.id.nav_home);
        } else {
            super.onBackPressed();
        }
    }
}