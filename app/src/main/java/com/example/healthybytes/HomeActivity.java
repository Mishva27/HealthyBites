package com.example.healthybytes;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    TextView toolbarUserName;
    ImageView toolbarProfileIcon;
    Button logoutButton;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);  // Your updated XML layout

        mAuth = FirebaseAuth.getInstance();

        // Bind views
        toolbarUserName = findViewById(R.id.toolbarUserName);
        toolbarProfileIcon = findViewById(R.id.toolbarProfileIcon);
        logoutButton = findViewById(R.id.logoutButton);

        // Set user name if available
        if (mAuth.getCurrentUser() != null && mAuth.getCurrentUser().getEmail() != null) {
            String email = mAuth.getCurrentUser().getEmail();

            if (email.contains("@")) {
                String[] parts = email.split("@");
                String namePart = parts[0]; // e.g., mishva123

                // Remove digits (optional)
                namePart = namePart.replaceAll("\\d", ""); // mishva

                // Capitalize first letter
                String formattedName = namePart.length() > 0
                        ? namePart.substring(0, 1).toUpperCase() + namePart.substring(1).toLowerCase()
                        : "User";

                toolbarUserName.setText("Hello, " + formattedName + "!");
            } else {
                toolbarUserName.setText("Hello, User!");
            }

            // Handle logout
            logoutButton.setOnClickListener(view -> {
                new AlertDialog.Builder(HomeActivity.this)
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            FirebaseAuth.getInstance().signOut(); // Sign out from Firebase
                            Intent intent = new Intent(HomeActivity.this, SignInActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        })
                        .setNegativeButton("No", (dialog, which) -> dialog.dismiss()) // Dismiss dialog
                        .show();
            });

            // Handle FAB to open AddMealActivity
            FloatingActionButton fab = findViewById(R.id.fab);

            fab.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(intent);
            });

        }


    }
}
