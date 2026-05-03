package com.kushal.eduhabit;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class PronunciationPracticeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Using common speaking layout for pronunciation practice
        setContentView(R.layout.activity_speaking_practice);
    }
}