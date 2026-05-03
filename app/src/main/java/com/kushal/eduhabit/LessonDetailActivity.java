package com.kushal.eduhabit;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.google.android.material.tabs.TabLayoutMediator;
import com.kushal.eduhabit.databinding.ActivityLessonDetailBinding;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;

public class LessonDetailActivity extends AppCompatActivity {

    private ActivityLessonDetailBinding binding;
    private GrammarModule module;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLessonDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String moduleTitle = getIntent().getStringExtra("module_title");
        // In a real app, you'd fetch this from the ViewModel by title
        // For this demo, we'll re-initialize or use a shared state.
        
        binding.btnBack.setOnClickListener(v -> finish());
        binding.tvTitle.setText(moduleTitle);

        setupYouTube();
        setupTabs();
    }

    private void setupYouTube() {
        getLifecycle().addObserver(binding.youtubePlayerView);
        binding.youtubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                // Load video for this module
                // For demo, using a fixed working ID or dynamic if found
                youTubePlayer.cueVideo("6H-f_LY_688", 0f);
            }
        });
    }

    private void setupTabs() {
        binding.viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                if (position == 0) return new TheoryFragment(); // Need to update TheoryFragment
                return new PracticeFragment(); // Need to create PracticeFragment
            }

            @Override
            public int getItemCount() {
                return 2;
            }
        });

        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
            tab.setText(position == 0 ? "Theory" : "Practice");
        }).attach();
    }
}
