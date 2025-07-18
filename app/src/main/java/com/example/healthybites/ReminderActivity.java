package com.example.healthybites;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ReminderActivity extends AppCompatActivity {

    private LinearLayout waterReminder, mealReminder;
    private ImageView backBtn;
    private Switch waterSwitch, mealSwitch;

    private final String[] reminderOptions = {
            "15 minutes", "30 minutes", "1 hour", "2 hours", "3 hours", "4 hours", "5 hours", "6 hours"
    };

    // SharedPreferences key
    private static final String PREFS_NAME = "ReminderPrefs";
    private static final String WATER_INTERVAL_KEY = "water_interval";
    private static final String MEAL_INTERVAL_KEY = "meal_interval";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        // Initialize UI
        waterReminder = findViewById(R.id.waterReminderLayout);
        mealReminder = findViewById(R.id.mealReminderLayout);
        backBtn = findViewById(R.id.backBtn);

        waterSwitch = findViewById(R.id.waterReminderSwitch);
        mealSwitch = findViewById(R.id.mealReminderSwitch);

        // Back button
        backBtn.setOnClickListener(v -> finish());

        // Card layout click navigates to setup activity
        waterReminder.setOnClickListener(view -> {
            startActivity(new Intent(ReminderActivity.this, WaterReminderSetupActivity.class));
        });

        mealReminder.setOnClickListener(view -> {
            startActivity(new Intent(ReminderActivity.this, MealReminderSetupActivity.class));
        });

        // Handle switch toggles
        setupSwitchToggle(waterSwitch, "Water");
        setupSwitchToggle(mealSwitch, "Meal");
    }

    private void setupSwitchToggle(Switch toggleSwitch, String type) {
        toggleSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                showReminderIntervalDialog(toggleSwitch, type);
                toggleSwitch.getThumbDrawable().setTint(getResources().getColor(R.color.primary));
                toggleSwitch.getTrackDrawable().setTint(getResources().getColor(R.color.secondary));
            } else {
                toggleSwitch.getThumbDrawable().setTint(getResources().getColor(android.R.color.darker_gray));
                toggleSwitch.getTrackDrawable().setTint(getResources().getColor(android.R.color.darker_gray));
                Toast.makeText(this, type + " reminder turned OFF", Toast.LENGTH_SHORT).show();

                // Remove saved preference
                SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
                editor.remove(type.equals("Water") ? WATER_INTERVAL_KEY : MEAL_INTERVAL_KEY);
                editor.apply();
            }
        });
    }

    private void showReminderIntervalDialog(Switch toggleSwitch, String type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set " + type + " Reminder Interval");

        builder.setItems(reminderOptions, (dialog, which) -> {
            String selected = reminderOptions[which];

            // Save to SharedPreferences
            SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
            if (type.equals("Water")) {
                editor.putString(WATER_INTERVAL_KEY, selected);
            } else {
                editor.putString(MEAL_INTERVAL_KEY, selected);
            }
            editor.apply();

            Toast.makeText(ReminderActivity.this,
                    type + " Reminder set for " + selected, Toast.LENGTH_SHORT).show();

            // TODO: You can now use this interval to set AlarmManager logic.
        });

        builder.setOnCancelListener(dialog -> {
            // If user cancels the dialog, revert the switch to OFF
            toggleSwitch.setChecked(false);
        });

        builder.show();
    }
}
