package com.kushal.eduhabit;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.kushal.eduhabit.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseAuth mAuth;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mAuth = FirebaseAuth.getInstance();
        session = new SessionManager(this);

        // 1. Session Check: If user is already logged in, redirect immediately
        if (mAuth.getCurrentUser() != null && session.isLoggedIn()) {
            redirectToDashboard();
            return;
        }

        // If no session, go to WelcomeActivity (the high-fidelity landing page)
        startActivity(new Intent(this, WelcomeActivity.class));
        finish();
    }

    private void redirectToDashboard() {
        Intent intent = session.isTeacher() ? 
            new Intent(this, TeacherDashboardActivity.class) : 
            new Intent(this, StudentDashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
