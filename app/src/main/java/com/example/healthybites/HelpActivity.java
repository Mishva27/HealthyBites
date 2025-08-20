// HelpActivity.java
package com.example.healthybites;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

// This activity is designed to display the help and support page.
public class HelpActivity extends AppCompatActivity {

    // Declare the UI components from the activity_help.xml layout.
    // We'll link these variables to the views in the onCreate method.
    private ImageView backBtn;
    private TextView helpTitle; // Corrected variable name to match XML ID
    private FloatingActionButton fab;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This line links this Java activity to the XML layout file.
        setContentView(R.layout.activity_help);

        // Initialize the UI components by finding them by their IDs.
        backBtn = findViewById(R.id.backBtn);
        // We're using notificationTitle here to match the ID in the provided XML.
        helpTitle = findViewById(R.id.helpTitle);
        fab = findViewById(R.id.fab);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // --- Set up event listeners for interactive components ---

        // Set a click listener for the back button.
        // This button will close the current activity and return to the previous screen.
        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(HelpActivity.this, HomeActivity.class);
            // Clear other activities so Home becomes the top
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });


        // Set a click listener for the Floating Action Button.
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Display a simple Toast message to confirm the click.
                Toast.makeText(HelpActivity.this, "FAB clicked!", Toast.LENGTH_SHORT).show();
            }
        });


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> startActivity(new Intent(HelpActivity.this, MainActivity.class)));

        // Bottom Navigation Setup
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_help);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                Intent intent = new Intent(HelpActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_coach) {
                Intent intent = new Intent(HelpActivity.this, CoachActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.mealPlanner) {
                Intent intent = new Intent(HelpActivity.this, MealSuggestion.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_help) {
                return true;
            }

            return false;
        });

        // Note: Make sure the menu IDs (nav_home, nav_coach, mealPlanner, nav_help)
        // are correctly defined in your 'res/menu/bottom_nav_menu.xml' file.
    }
}
