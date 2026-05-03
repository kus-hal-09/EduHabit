package com.kushal.eduhabit.grammar.view;

import android.os.Bundle;
import android.view.View;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.kushal.eduhabit.databinding.ActivityGrammarModularBinding;
import com.kushal.eduhabit.grammar.model.GrammarLesson;
import com.kushal.eduhabit.grammar.viewmodel.GrammarViewModel;

@OptIn(markerClass = UnstableApi.class)
public class ModularGrammarActivity extends AppCompatActivity {

    private ActivityGrammarModularBinding binding;
    private GrammarViewModel viewModel;
    private ExoPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGrammarModularBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(GrammarViewModel.class);

        setupToolbar();
        setupRecyclerView();
        initializePlayer();
        observeViewModel();

        viewModel.loadLessons();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        binding.rvLessons.setLayoutManager(new LinearLayoutManager(this));
    }

    private void observeViewModel() {
        viewModel.getLessons().observe(this, lessons -> {
            ModularGrammarAdapter adapter = new ModularGrammarAdapter(lessons, lesson -> {
                viewModel.selectLesson(lesson);
            });
            binding.rvLessons.setAdapter(adapter);
        });

        viewModel.getSelectedLesson().observe(this, lesson -> {
            if (lesson != null) {
                playVideo(lesson);
            }
        });
    }

    private void initializePlayer() {
        player = new ExoPlayer.Builder(this).build();
        binding.playerView.setPlayer(player);

        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == Player.STATE_BUFFERING) {
                    binding.videoLoader.setVisibility(View.VISIBLE);
                } else {
                    binding.videoLoader.setVisibility(View.GONE);
                }
            }
        });
    }

    private void playVideo(GrammarLesson lesson) {
        binding.tvActiveLesson.setText("Now Playing: " + lesson.getTitle());
        MediaItem mediaItem = MediaItem.fromUri(lesson.getVideoUrl());
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) {
            player.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
        }
    }
}
