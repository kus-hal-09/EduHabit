package com.kushal.eduhabit;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.kushal.eduhabit.databinding.ActivitySpeakingPracticeBinding;

public class SpeakingPracticeActivity extends AppCompatActivity {

    private ActivitySpeakingPracticeBinding binding;
    private boolean isRecording = false;
    private int seconds = 0;
    private Handler handler = new Handler();
    private Runnable timerRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySpeakingPracticeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backBtn.setOnClickListener(v -> finish());

        // Topic Selection Logic
        binding.topicIntro.setOnClickListener(v -> selectTopic("Introduction", "Tell me about yourself, your hobbies, and your goals.", 120));
        binding.topicRoutine.setOnClickListener(v -> selectTopic("Daily Routine", "Describe your typical day from morning to evening.", 90));
        
        // Recording Logic
        binding.recordBtn.setOnClickListener(v -> {
            if (!isRecording) {
                startRecording();
            } else {
                stopRecording();
            }
        });
    }

    private void selectTopic(String title, String desc, int duration) {
        binding.selectedTopicTitle.setText(title);
        binding.selectedTopicDesc.setText(desc);
        binding.recommendedDuration.setText("Recommended duration: " + duration + " seconds");
        stopRecording(); // Reset timer if switching topics
        seconds = 0;
        binding.timerText.setText("0:00");
        
        // Visual feedback for selection
        int grayColor = ContextCompat.getColor(this, android.R.color.darker_gray);
        int pinkColor = 0xFFD81B60; // Matching the theme color
        
        binding.topicIntro.setStrokeColor(grayColor);
        binding.topicRoutine.setStrokeColor(grayColor);
        
        if (title.equals("Introduction")) {
            binding.topicIntro.setStrokeColor(pinkColor);
        } else {
            binding.topicRoutine.setStrokeColor(pinkColor);
        }
    }

    private void startRecording() {
        isRecording = true;
        binding.recordBtn.setText("Stop Recording");
        binding.recordBtn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFEF4444)); // Red
        
        seconds = 0;
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                seconds++;
                int mins = seconds / 60;
                int secs = seconds % 60;
                binding.timerText.setText(String.format("%d:%02d", mins, secs));
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(timerRunnable, 1000);
        Toast.makeText(this, "Recording started...", Toast.LENGTH_SHORT).show();
    }

    private void stopRecording() {
        if (!isRecording) return;
        isRecording = false;
        binding.recordBtn.setText("Start Recording");
        binding.recordBtn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFD81B60)); // Original Pink
        handler.removeCallbacks(timerRunnable);
        Toast.makeText(this, "Recording saved! Analyzing fluency...", Toast.LENGTH_LONG).show();
    }
}