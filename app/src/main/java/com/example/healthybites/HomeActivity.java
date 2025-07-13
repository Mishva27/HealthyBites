package com.example.healthybites;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

//packages for Google Fit API
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;


public class HomeActivity extends AppCompatActivity {

    TextView toolbarUserName, tvBMIValue, tvBMIBadge, tvWaterValue, tvWaterGoal, tvReminderStatus,tvStepCount,tvWeightValue, tvWeightMin, tvWeightGoal;
    ImageView toolbarProfileIcon;
    Button logoutButton, btnStartWaterReminder, btnSetWaterGoal,btnAddWeight;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    LineChart weightTrendChart;
    ImageButton btnIncreaseWater, btnDecreaseWater;
    final float[] waterAmount = {0.0f};  // Holds current water intake
    FitnessOptions fitnessOptions;  // Global variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        boolean isGuest = getIntent().getBooleanExtra("isGuest", false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Bind views
        toolbarUserName = findViewById(R.id.toolbarUserName);
        toolbarProfileIcon = findViewById(R.id.toolbarProfileIcon);
        logoutButton = findViewById(R.id.logoutButton);
        btnStartWaterReminder = findViewById(R.id.btnStartWaterReminder);
        tvBMIValue = findViewById(R.id.tvBMIValue);
        weightTrendChart = findViewById(R.id.weightTrendChart);
        tvBMIBadge = findViewById(R.id.tvBMIBadge);
        btnIncreaseWater = findViewById(R.id.btnIncreaseWater);
        btnDecreaseWater = findViewById(R.id.btnDecreaseWater);
        tvWaterValue = findViewById(R.id.tvWaterValue);
        tvWaterGoal = findViewById(R.id.tvWaterGoal);
        btnSetWaterGoal = findViewById(R.id.btnSetWaterGoal);
        tvReminderStatus = findViewById(R.id.tvReminderStatus);
        tvStepCount = findViewById(R.id.tvStepCount);


        //this is used for profile icon
        View appBar = findViewById(R.id.app_bar);
        toolbarProfileIcon = appBar.findViewById(R.id.toolbarProfileIcon);

        toolbarProfileIcon.setOnClickListener(v -> {
            Log.d("ToolbarTest", "Profile icon clicked!");
            Intent intent = new Intent(HomeActivity.this, MyDetailsActivity.class);
            startActivity(intent);
        });//close here


        if (!isGuest) {
            // Only for logged-in users:
            setupProfileIconClick();
            setUserGreeting();
            fetchUserDetailsAndCalculateBMI();
            updateReminderStatus();

            // Google Fit Permission & Access
            fitnessOptions = FitnessOptions.builder()
                    .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                    .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
                    .build();

            GoogleSignInAccount account = GoogleSignIn.getAccountForExtension(this, fitnessOptions);

            if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
                GoogleSignIn.requestPermissions(
                        this,
                        1001,
                        account,
                        fitnessOptions);
            } else {
                accessGoogleFitData();
            }

        } else {
            // For Guest Mode:
            Toast.makeText(this, "Guest Mode Activated", Toast.LENGTH_SHORT).show();
            toolbarUserName.setText("Hello, Guest!");
        }


        // Load water goal
        setupWaterGoalFeature();

        // Load last water amount (optional)
        SharedPreferences prefs = getSharedPreferences("WaterPrefs", MODE_PRIVATE);
        waterAmount[0] = prefs.getFloat("water_amount", 0.0f);
        tvWaterValue.setText(String.format("%.1f L", waterAmount[0]));

        // ➕ Increase Water Button
        btnIncreaseWater.setOnClickListener(v -> {
            waterAmount[0] += 0.25f;  // 250 ml
            tvWaterValue.setText(String.format("%.1f L", waterAmount[0]));
            prefs.edit().putFloat("water_amount", waterAmount[0]).apply(); // Save progress
        });

        // ➖ Decrease Water Button
        btnDecreaseWater.setOnClickListener(v -> {
            if (waterAmount[0] >= 0.25f) {
                waterAmount[0] -= 0.25f;
            } else {
                waterAmount[0] = 0.0f;
            }
            tvWaterValue.setText(String.format("%.1f L", waterAmount[0]));
            prefs.edit().putFloat("water_amount", waterAmount[0]).apply(); // Save progress
        });



        setupProfileIconClick();
        setUserGreeting();
        fetchUserDetailsAndCalculateBMI();
        updateReminderStatus();


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, MainActivity.class)));

        btnStartWaterReminder.setOnClickListener(v -> showWaterReminderDialog());

        logoutButton.setOnClickListener(view -> {
            new AlertDialog.Builder(HomeActivity.this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(HomeActivity.this, SignInActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });


        //Google fit API code

        fitnessOptions =
                FitnessOptions.builder()
                        .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                        .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
                        .build();

        GoogleSignInOptionsExtension fitnessOptions =
                FitnessOptions.builder()
                        .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                        .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
                        .build();

        GoogleSignInAccount account = GoogleSignIn.getAccountForExtension(this, fitnessOptions);

        if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    this, // Activity
                    1001, // Request code
                    account,
                    fitnessOptions);
        } else {
            accessGoogleFitData();
        }

    }//oncreate close here

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001) {
            if (resultCode == RESULT_OK) {
                accessGoogleFitData();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void accessGoogleFitData() {
        Fitness.getHistoryClient(this, GoogleSignIn.getAccountForExtension(this, fitnessOptions))
                .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener(dataSet -> {
                    long totalSteps = dataSet.isEmpty() ? 0 :
                            dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
                    Log.i("GoogleFit", "Total steps: " + totalSteps);
                    tvStepCount.setText("Steps Today: " + totalSteps);
                })
                .addOnFailureListener(e -> Log.e("GoogleFit", "Failed to get steps.", e));
    }//clode google fit api


    private void startWaterReminder(long intervalMinutes) {
        PeriodicWorkRequest waterReminderRequest = new PeriodicWorkRequest.Builder(
                WaterReminderWorker.class,
                intervalMinutes, TimeUnit.MINUTES)
                .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "waterReminder",
                ExistingPeriodicWorkPolicy.REPLACE,
                waterReminderRequest);

        // Save Status as ON
        SharedPreferences prefs = getSharedPreferences("WaterPrefs", MODE_PRIVATE);
        prefs.edit().putBoolean("reminder_status", true).apply();
    }


    private void updateReminderStatus() {
        SharedPreferences prefs = getSharedPreferences("WaterPrefs", MODE_PRIVATE);
        boolean isOn = prefs.getBoolean("reminder_status", false);
        tvReminderStatus.setText("Reminder Status: " + (isOn ? "ON" : "OFF"));
    }


    private void showWaterReminderDialog() {
        final String[] options = {"15 minutes", "30 minutes", "1 hour", "2 hours"};
        final long[] intervals = {15, 30, 60, 120};

        new AlertDialog.Builder(this)
                .setTitle("Set Water Reminder Interval")
                .setSingleChoiceItems(options, 0, null)
                .setPositiveButton("Start", (dialog, which) -> {
                    int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                    long selectedInterval = intervals[selectedPosition];

                    startWaterReminder(selectedInterval);
                    updateReminderStatus();
                    Toast.makeText(this, "Water reminder set for every " + options[selectedPosition], Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void setupWaterGoalFeature() {
        SharedPreferences prefs = getSharedPreferences("WaterPrefs", MODE_PRIVATE);
        float savedGoal = prefs.getFloat("water_goal", 2.0f);
        tvWaterGoal.setText("Goal: " + savedGoal + " L");

        btnSetWaterGoal.setOnClickListener(v -> showSetWaterGoalDialog(prefs));
    }

    private void showSetWaterGoalDialog(SharedPreferences prefs) {
        EditText input = new EditText(this);
        input.setHint("Enter goal in liters");
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        new AlertDialog.Builder(this)
                .setTitle("Set Water Goal")
                .setView(input)
                .setPositiveButton("Save", (dialog, which) -> {
                    String enteredGoal = input.getText().toString();
                    if (!enteredGoal.isEmpty()) {
                        try {
                            float newGoal = Float.parseFloat(enteredGoal);
                            if (newGoal > 0) {
                                prefs.edit().putFloat("water_goal", newGoal).apply();
                                tvWaterGoal.setText("Goal: " + newGoal + " L");
                                Toast.makeText(this, "Water goal set!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Please enter a positive number.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (NumberFormatException e) {
                            Toast.makeText(this, "Invalid input! Please enter a number.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void setUserGreeting() {
        if (mAuth.getCurrentUser() != null && mAuth.getCurrentUser().getEmail() != null) {
            String email = mAuth.getCurrentUser().getEmail();
            String[] parts = email.split("@");
            String namePart = parts[0].replaceAll("\\d", "");
            String formattedName = namePart.length() > 0
                    ? namePart.substring(0, 1).toUpperCase() + namePart.substring(1).toLowerCase()
                    : "User";
            toolbarUserName.setText("Hello, " + formattedName + "!");
        } else {
            toolbarUserName.setText("Hello, User!");
        }
    }

    private void fetchUserDetailsAndCalculateBMI() {
        if (mAuth.getCurrentUser() == null) {
            // ✅ Guest Mode: Skip Firebase calls safely
            tvBMIValue.setText("BMI: N/A");
            tvBMIBadge.setText("Guest");
            weightTrendChart.setNoDataText("Not available for Guest Mode.");
            weightTrendChart.invalidate();
            return;
        }

        // ✅ Logged-in user → Fetch from Firebase
        String uid = mAuth.getCurrentUser().getUid();
        DocumentReference userRef = db.collection("users").document(uid);

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String heightStr = documentSnapshot.getString("height");
                String weightStr = documentSnapshot.getString("weight");
                calculateAndDisplayBMI(heightStr, weightStr);
            }
        });

        loadWeightTrendChart();  // Now safe, because we already checked above.
    }

    //BMI Calculator
    private void calculateAndDisplayBMI(String heightStr, String weightStr) {
        Log.d("BMI_DATA", "Height: " + heightStr + ", Weight: " + weightStr);

        if (heightStr == null || weightStr == null || heightStr.isEmpty() || weightStr.isEmpty()) {
            tvBMIValue.setText("BMI: N/A");
            tvBMIBadge.setText("Missing");
            tvBMIBadge.setBackgroundTintList(null);
            return;
        }

        try {
            // ✅ Remove units like "ft", "cm", etc., and trim whitespace
            heightStr = heightStr.replaceAll("[^\\d.]", "").trim();
            weightStr = weightStr.replaceAll("[^\\d.]", "").trim();

            float heightInFeet = Float.parseFloat(heightStr);
            float heightCm = heightInFeet * 30.48f;  // ✅ Convert feet to cm
            float weightKg = Float.parseFloat(weightStr);

            float heightM = heightCm / 100f;
            float bmi = weightKg / (heightM * heightM);
            String result = String.format("%.1f", bmi);
            tvBMIValue.setText("BMI: " + result);

            if (bmi < 18.5f) {
                tvBMIBadge.setText("Underweight");
                tvBMIBadge.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.blue)));
            } else if (bmi < 24.9f) {
                tvBMIBadge.setText("Normal");
                tvBMIBadge.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.green)));
            } else if (bmi < 29.9f) {
                tvBMIBadge.setText("Overweight");
                tvBMIBadge.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.orange)));
            } else {
                tvBMIBadge.setText("Obese");
                tvBMIBadge.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.red)));
            }

        } catch (Exception e) {
            Log.e("BMI_ERROR", "Error calculating BMI", e);
            tvBMIValue.setText("BMI: Error");
            tvBMIBadge.setText("Error");
            tvBMIBadge.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.red)));
        }
    }

    private void loadWeightTrendChart() {
        if (mAuth.getCurrentUser() == null) {
            //  No user logged in → Guest Mode or error → Skip chart
            weightTrendChart.setNoDataText("Not available for Guest Mode.");
            weightTrendChart.setNoDataTextColor(getColor(R.color.secondary));
            weightTrendChart.invalidate();
            return;
        }

        List<Entry> entries = new ArrayList<>();
        String uid = mAuth.getCurrentUser().getUid();

        db.collection("users")
                .document(uid)
                .collection("weightHistory")
                .orderBy("date")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int index = 1;
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Double weight = doc.getDouble("weight");
                        if (weight != null) {
                            entries.add(new Entry(index++, weight.floatValue()));
                            Log.d("ChartData", "Fetched weight: " + weight);
                        }
                    }

                    if (entries.isEmpty()) {
                        weightTrendChart.setNoDataText("No weight data available.");
                        weightTrendChart.setNoDataTextColor(getColor(R.color.secondary));
                        weightTrendChart.invalidate();
                        return;
                    }

                    LineDataSet dataSet = new LineDataSet(entries, "Weight (kg)");
                    dataSet.setColor(getColor(R.color.primary));
                    dataSet.setCircleColor(getColor(R.color.primary));
                    dataSet.setLineWidth(2f);
                    dataSet.setDrawValues(false);

                    LineData lineData = new LineData(dataSet);
                    weightTrendChart.setData(lineData);
                    weightTrendChart.getDescription().setEnabled(false);
                    weightTrendChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                    weightTrendChart.getAxisRight().setEnabled(false);
                    weightTrendChart.getAxisLeft().setDrawGridLines(false);
                    weightTrendChart.getXAxis().setDrawGridLines(false);
                    weightTrendChart.invalidate();
                });
    }


    private void setupProfileIconClick() {
        View appBar = findViewById(R.id.app_bar);
        ImageView toolbarProfileIcon = appBar.findViewById(R.id.toolbarProfileIcon);

        toolbarProfileIcon.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }

}
