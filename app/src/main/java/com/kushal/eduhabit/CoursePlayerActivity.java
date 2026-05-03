package com.kushal.eduhabit;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.tabs.TabLayout;
import com.kushal.eduhabit.databinding.ActivityCoursePlayerBinding;
import java.util.HashMap;
import java.util.Map;

public class CoursePlayerActivity extends AppCompatActivity {

    private ActivityCoursePlayerBinding binding;
    private String courseName;

    // Working high-quality educational YouTube embeds for BCA 6th Semester
    private static final Map<String, String> SUBJECT_VIDEOS = new HashMap<>();
    private static final Map<String, String> LESSON_VIDEOS = new HashMap<>();

    static {
        // .NET Technology - Using a well-known .NET tutorial
        SUBJECT_VIDEOS.put(".NET Technology", "https://www.youtube.com/embed/GhQdlIFylQ8");
        LESSON_VIDEOS.put("1. Introduction to .NET", "https://www.youtube.com/embed/hZ1DASY09rk");
        LESSON_VIDEOS.put("2. C# Fundamentals", "https://www.youtube.com/embed/GhQdlIFylQ8");
        LESSON_VIDEOS.put("3. ASP.NET Core MVC", "https://www.youtube.com/embed/C5cnZ-gZy2I");
        
        // Artificial Intelligence - MIT/Stanford level educational videos
        SUBJECT_VIDEOS.put("Artificial Intelligence", "https://www.youtube.com/embed/2ePf9rue1Ao");
        LESSON_VIDEOS.put("1. AI Basics", "https://www.youtube.com/embed/2ePf9rue1Ao");
        LESSON_VIDEOS.put("2. Intelligent Agents", "https://www.youtube.com/embed/5nx7j8X7Ois");
        LESSON_VIDEOS.put("3. Search Algorithms", "https://www.youtube.com/embed/D5a86_Sst_k");
        
        // E-Commerce
        SUBJECT_VIDEOS.put("E-Commerce", "https://www.youtube.com/embed/7mQvcsSOnTY");
        LESSON_VIDEOS.put("1. Intro to E-Commerce", "https://www.youtube.com/embed/7mQvcsSOnTY");
        LESSON_VIDEOS.put("2. Business Models", "https://www.youtube.com/embed/mXmS_y6ZIdg");
        
        // Client Side Scripting (JavaScript/Modern Web)
        SUBJECT_VIDEOS.put("Client Side Scripting", "https://www.youtube.com/embed/PkZNo7MFNFg");
        LESSON_VIDEOS.put("1. JS Basics", "https://www.youtube.com/embed/PkZNo7MFNFg");
        LESSON_VIDEOS.put("2. DOM Manipulation", "https://www.youtube.com/embed/y17RuWkWdn8");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCoursePlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        courseName = getIntent().getStringExtra("courseName");
        binding.tvCourseTitle.setText(courseName != null ? courseName : "BCA 6th Semester");

        binding.btnBack.setOnClickListener(v -> finish());

        setupWebView();
        loadVideo(SUBJECT_VIDEOS.get(courseName));

        // Setup Fragments
        Fragment curriculumFragment = CurriculumFragment.newInstance(courseName);
        Fragment theoryFragment = TheoryFragment.newInstance(courseName);
        Fragment practiceFragment = CoursePracticeFragment.newInstance(courseName);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.courseContentContainer, curriculumFragment)
                .commit();

        binding.courseTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment selectedFragment = null;
                switch (tab.getPosition()) {
                    case 0: selectedFragment = curriculumFragment; break;
                    case 1: selectedFragment = theoryFragment; break;
                    case 2: selectedFragment = practiceFragment; break;
                }
                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                        .replace(R.id.courseContentContainer, selectedFragment)
                        .setTransition(androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
                }
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupWebView() {
        WebSettings webSettings = binding.webViewPlayer.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        binding.webViewPlayer.setWebViewClient(new WebViewClient());
    }

    public void loadVideo(String url) {
        if (url == null) url = "https://www.youtube.com/embed/hZ1DASY09rk";
        // Professional YouTube embed with controls and no related videos
        String frame = "<html><body style=\"margin:0;padding:0;\"><iframe width=\"100%\" height=\"100%\" src=\"" + url + "?rel=0&modestbranding=1\" frameborder=\"0\" allowfullscreen></iframe></body></html>";
        binding.webViewPlayer.loadData(frame, "text/html", "utf-8");
    }

    public void playLessonVideo(String lessonTitle) {
        String videoUrl = LESSON_VIDEOS.get(lessonTitle);
        if (videoUrl != null) {
            loadVideo(videoUrl);
        }
    }
}
