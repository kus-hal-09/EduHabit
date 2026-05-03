package com.kushal.eduhabit;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;

public class SoundManager {
    private SoundPool soundPool;
    private int correctSoundId;
    private int wrongSoundId;
    private boolean loaded = false;

    public SoundManager(Context context) {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(2)
                .setAudioAttributes(audioAttributes)
                .build();

        // Note: You'll need to add these mp3 files to res/raw
        // For now, these IDs will be 0 if the resources don't exist
        correctSoundId = context.getResources().getIdentifier("ding", "raw", context.getPackageName());
        wrongSoundId = context.getResources().getIdentifier("thud", "raw", context.getPackageName());

        soundPool.setOnLoadCompleteListener((soundPool1, sampleId, status) -> loaded = true);
    }

    public void playCorrect() {
        if (loaded && correctSoundId != 0) {
            soundPool.play(correctSoundId, 1, 1, 0, 0, 1);
        }
    }

    public void playWrong() {
        if (loaded && wrongSoundId != 0) {
            soundPool.play(wrongSoundId, 1, 1, 0, 0, 1);
        }
    }

    public void release() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }
}
