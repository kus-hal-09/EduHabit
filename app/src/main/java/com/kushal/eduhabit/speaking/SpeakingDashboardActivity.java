package com.kushal.eduhabit.speaking;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.kushal.eduhabit.R;
import com.kushal.eduhabit.SpeakingPracticeActivity;
import com.kushal.eduhabit.databinding.ActivitySpeakingDashboardBinding;
import java.util.ArrayList;
import java.util.List;

public class SpeakingDashboardActivity extends AppCompatActivity {

    private ActivitySpeakingDashboardBinding binding;
    private List<SpeakingTopic> topics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySpeakingDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        setupData();
        setupRecyclerView();
    }

    private void setupData() {
        topics = new ArrayList<>();
        topics.add(new SpeakingTopic("1", "Self Introduction", "Learn to introduce yourself with confidence.", "Beginner", "Hello, my name is Alex and I am a software engineer.", R.drawable.ic_profile, 0));
        topics.add(new SpeakingTopic("2", "Business Strategy", "Practice high-level professional communication.", "Advanced", "We need to leverage our core competencies to gain a competitive advantage.", R.drawable.ic_assignments, 0));
        topics.add(new SpeakingTopic("3", "Daily Routine", "Talk about your day-to-day activities.", "Intermediate", "I usually wake up at seven and start my day with a cup of coffee.", R.drawable.ic_clock, 0));
    }

    private void setupRecyclerView() {
        SpeakingTopicAdapter adapter = new SpeakingTopicAdapter(topics, topic -> {
            Intent intent = new Intent(this, SpeakingPracticeActivity.class);
            intent.putExtra("topic", topic);
            startActivity(intent);
        });
        binding.rvSpeakingTopics.setLayoutManager(new LinearLayoutManager(this));
        binding.rvSpeakingTopics.setAdapter(adapter);
    }
}
