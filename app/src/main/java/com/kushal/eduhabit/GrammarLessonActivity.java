package com.kushal.eduhabit;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.google.android.material.tabs.TabLayoutMediator;
import com.kushal.eduhabit.databinding.ActivityGrammarLessonBinding;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;

import java.util.List;

public class GrammarLessonActivity extends AppCompatActivity {

    private ActivityGrammarLessonBinding binding;
    private GrammarLessonViewModel viewModel;
    private YouTubePlayer activePlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGrammarLessonBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(GrammarLessonViewModel.class);

        // Updated to use the Toolbar's navigation button since btnBack was removed in the SaaS redesign
        binding.toolbar.setNavigationOnClickListener(v -> finish());

        // Handle the incoming lesson title from GrammarActivity
        String initialLessonTitle = getIntent().getStringExtra("lesson_title");
        if (initialLessonTitle != null) {
            viewModel.getLessons().observe(this, lessons -> {
                for (Lesson lesson : lessons) {
                    if (lesson.title.equalsIgnoreCase(initialLessonTitle)) {
                        viewModel.selectLesson(lesson);
                        break;
                    }
                }
            });
        }

        setupYouTube();
        setupTabs();

        // Observe selected lesson to update video and UI
        viewModel.getSelectedLesson().observe(this, lesson -> {
            if (lesson != null) {
                binding.tvToolbarTitle.setText(lesson.title);
                if (activePlayer != null) {
                    activePlayer.cueVideo(lesson.videoId, 0f);
                }
            }
        });
    }

    private void setupYouTube() {
        getLifecycle().addObserver(binding.youtubePlayerView);
        binding.youtubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                activePlayer = youTubePlayer;
                Lesson selected = viewModel.getSelectedLesson().getValue();
                if (selected != null) {
                    activePlayer.cueVideo(selected.videoId, 0f);
                }
            }
        });
    }

    private void setupTabs() {
        binding.viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 0: return new GrammarCurriculumFragment();
                    case 1: return new GrammarTheoryFragment();
                    case 2: return new GrammarPracticeFragment();
                    default: return new GrammarCurriculumFragment();
                }
            }

            @Override
            public int getItemCount() {
                return 3;
            }
        });

        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("Curriculum"); break;
                case 1: tab.setText("Theory"); break;
                case 2: tab.setText("Practice"); break;
            }
        }).attach();
    }
}
