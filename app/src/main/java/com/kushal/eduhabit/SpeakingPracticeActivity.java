package com.kushal.eduhabit;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.kushal.eduhabit.databinding.ActivitySpeakingPracticeBinding;
import java.util.ArrayList;
import java.util.Locale;

public class SpeakingPracticeActivity extends AppCompatActivity {

    private ActivitySpeakingPracticeBinding binding;
    private TextToSpeech tts;
    private SpeechRecognizer speechRecognizer;
    private String targetSentence = "We need to leverage our core competencies.";
    private boolean isListening = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySpeakingPracticeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());
        binding.tvTargetSentence.setText("\"" + targetSentence + "\"");

        setupTTS();
        setupSTT();

        binding.btnListen.setOnClickListener(v -> {
            if (tts != null) {
                tts.speak(targetSentence, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });

        binding.fabMic.setOnClickListener(v -> {
            if (!isListening) {
                startListening();
            } else {
                stopListening();
            }
        });
    }

    private void setupTTS() {
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) tts.setLanguage(Locale.US);
        });
    }

    private void setupSTT() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    processSpeech(matches.get(0));
                }
                stopListening();
            }

            @Override public void onReadyForSpeech(Bundle params) { binding.tvStatus.setText("Listening..."); }
            @Override public void onBeginningOfSpeech() {}
            @Override public void onRmsChanged(float rmsdB) {}
            @Override public void onBufferReceived(byte[] buffer) {}
            @Override public void onEndOfSpeech() {}
            @Override public void onError(int error) { 
                stopListening();
                Toast.makeText(SpeakingPracticeActivity.this, "Speech recognition error", Toast.LENGTH_SHORT).show();
            }
            @Override public void onPartialResults(Bundle partialResults) {}
            @Override public void onEvent(int eventType, Bundle params) {}
        });
    }

    private void startListening() {
        isListening = true;
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        speechRecognizer.startListening(intent);
        
        // Start pulse animation manually since Lottie file is missing
        startPulse();
    }

    private void stopListening() {
        isListening = false;
        speechRecognizer.stopListening();
        binding.tvStatus.setText("Tap to start speaking");
        stopPulse();
    }

    private void startPulse() {
        AlphaAnimation pulse = new AlphaAnimation(0f, 0.5f);
        pulse.setDuration(800);
        pulse.setRepeatMode(Animation.REVERSE);
        pulse.setRepeatCount(Animation.INFINITE);
        binding.vPulse.startAnimation(pulse);
        binding.vPulse.setVisibility(View.VISIBLE);
    }

    private void stopPulse() {
        binding.vPulse.clearAnimation();
        binding.vPulse.setVisibility(View.GONE);
    }

    private void processSpeech(String spokenText) {
        binding.cvResults.setVisibility(View.VISIBLE);
        float accuracy = calculateAccuracy(targetSentence, spokenText);
        binding.tvAccuracy.setText(String.format(Locale.US, "%.0f%% Accuracy", accuracy * 100));
    }

    private float calculateAccuracy(String target, String spoken) {
        String t = target.toLowerCase().replaceAll("[^a-zA-Z ]", "");
        String s = spoken.toLowerCase().replaceAll("[^a-zA-Z ]", "");
        String[] targetWords = t.split("\\s+");
        String[] spokenWords = s.split("\\s+");
        
        int correct = 0;
        for (String sw : spokenWords) {
            for (String tw : targetWords) {
                if (sw.equals(tw)) {
                    correct++;
                    break;
                }
            }
        }
        return Math.min(1.0f, (float) correct / targetWords.length);
    }

    @Override
    protected void onDestroy() {
        if (tts != null) { tts.stop(); tts.shutdown(); }
        if (speechRecognizer != null) speechRecognizer.destroy();
        super.onDestroy();
    }
}
