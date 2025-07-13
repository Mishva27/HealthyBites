package com.example.healthybites;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    Button loginButton, createAccountButton;
    TextView guestText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        loginButton = findViewById(R.id.loginButton);
        createAccountButton = findViewById(R.id.createAccountButton);
        guestText = findViewById(R.id.guestText);

        // Mark first time completed
        SharedPreferences prefs = getSharedPreferences("HealthyBytesPrefs", MODE_PRIVATE);
        prefs.edit().putBoolean("first_time", false).apply();

        loginButton.setOnClickListener(view -> {
            startActivity(new Intent(WelcomeActivity.this, SignInActivity.class));
            finish();
        });

        createAccountButton.setOnClickListener(view -> {
            startActivity(new Intent(WelcomeActivity.this, SignUpActivity.class));
            finish();
        });

        guestText.setOnClickListener(view -> {
            Intent intent = new Intent(WelcomeActivity.this, HomeActivity.class);
            intent.putExtra("isGuest", true);
            startActivity(intent);
            finish();
        });
    }
}
