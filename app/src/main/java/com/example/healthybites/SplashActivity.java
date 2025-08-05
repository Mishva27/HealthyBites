package com.example.healthybites;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 4000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        new Handler().postDelayed(() -> {
            SharedPreferences prefs = getSharedPreferences("HealthyBytesPrefs", MODE_PRIVATE);
            boolean isFirstTime = prefs.getBoolean("first_time", true);

            if (isFirstTime) {
                //  Save first-time complete
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("first_time", false);
                editor.apply();

                // Show onboarding screen
                Log.d("SplashActivity", "First time user → Onboarding");
                startActivity(new Intent(SplashActivity.this, OnboardingActivity.class));
            } else if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                // Already signed in
                Log.d("SplashActivity", "User already signed in → Home");
                startActivity(new Intent(SplashActivity.this, HomeActivity.class));
            } else {
                // Not first time and not signed in
                Log.d("SplashActivity", "Returning user but not signed in → Onboarding");
                startActivity(new Intent(SplashActivity.this, OnboardingActivity.class));
            }

            finish();
        }, SPLASH_DELAY);
    }
}
