package com.example.healthybites;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Build;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;

import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;

public class HomeActivity extends AppCompatActivity {

    TextView toolbarUserName, tvBMIValue, tvBMIBadge, tvWaterValue, tvWaterGoal, tvStepCount, tvCalories, tvDistance, tvMoveMinutes;
    ImageView toolbarProfileIcon;
    Button btnSetWaterGoal;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    ImageButton btnIncreaseWater, btnDecreaseWater;
    final float[] waterAmount = {0.0f};
    private FitnessOptions fitnessOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        boolean isGuest = getIntent().getBooleanExtra("isGuest", false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        toolbarUserName = findViewById(R.id.toolbarUserName);
        toolbarProfileIcon = findViewById(R.id.toolbarProfileIcon);
        tvBMIValue = findViewById(R.id.tvBMIValue);
        tvBMIBadge = findViewById(R.id.tvBMIBadge);
        btnIncreaseWater = findViewById(R.id.btnIncreaseWater);
        btnDecreaseWater = findViewById(R.id.btnDecreaseWater);
        tvWaterValue = findViewById(R.id.tvWaterValue);
        tvWaterGoal = findViewById(R.id.tvWaterGoal);
        btnSetWaterGoal = findViewById(R.id.btnSetWaterGoal);

        // Initialize Views for Google Fit
        tvStepCount = findViewById(R.id.tvStepCount);
        tvCalories = findViewById(R.id.tvCalories);
        tvDistance = findViewById(R.id.tvDistance);
        tvMoveMinutes = findViewById(R.id.tvMoveMinutes);

        View appBar = findViewById(R.id.app_bar);
        toolbarProfileIcon = appBar.findViewById(R.id.toolbarProfileIcon);

        toolbarProfileIcon.setOnClickListener(v -> {
            Log.d("ToolbarTest", "Profile icon clicked!");
            Intent intent = new Intent(HomeActivity.this, MyDetailsActivity.class);
            startActivity(intent);
        });

        // Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                return true; // Already on Home
            } else if (itemId == R.id.nav_coach) {
                Intent intent = new Intent(HomeActivity.this, CoachActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.mealPlanner) {
                Intent intent = new Intent(HomeActivity.this, MealSuggestion.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_help) {
                Intent intent = new Intent(HomeActivity.this, HelpActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
            }

            return false;
        });

        // Setup Google Fit
        fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_DISTANCE_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_MOVE_MINUTES, FitnessOptions.ACCESS_READ)
                .build();

        if (!isGuest) {
            setupProfileIconClick();
            setUserGreeting();
            fetchUserDetailsAndCalculateBMI();

            if (fitnessOptions != null) {
                GoogleSignInAccount account = GoogleSignIn.getAccountForExtension(this, fitnessOptions);
                if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
                    GoogleSignIn.requestPermissions(this, 1001, account, fitnessOptions);
                } else {
                    accessGoogleFitData();
                }
            } else {
                Log.e("GoogleFit", "FitnessOptions not initialized");
            }
        } else {
            Toast.makeText(this, "Guest Mode Activated", Toast.LENGTH_SHORT).show();
            toolbarUserName.setText("Hello, Guest!");
        }

        requestActivityRecognitionPermission();

        setupWaterGoalFeature();

        SharedPreferences prefs = getSharedPreferences("WaterPrefs", MODE_PRIVATE);
        waterAmount[0] = prefs.getFloat("water_amount", 0.0f);
        tvWaterValue.setText(String.format("%.1f L", waterAmount[0]));

        btnIncreaseWater.setOnClickListener(v -> {
            waterAmount[0] += 0.25f;
            tvWaterValue.setText(String.format("%.1f L", waterAmount[0]));
            prefs.edit().putFloat("water_amount", waterAmount[0]).apply();
        });

        btnDecreaseWater.setOnClickListener(v -> {
            if (waterAmount[0] >= 0.25f) {
                waterAmount[0] -= 0.25f;
            } else {
                waterAmount[0] = 0.0f;
            }
            tvWaterValue.setText(String.format("%.1f L", waterAmount[0]));
            prefs.edit().putFloat("water_amount", waterAmount[0]).apply();
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, MainActivity.class)));
    }

    private static final int PERMISSION_REQUEST_ACTIVITY_RECOGNITION = 1001;

    private void requestActivityRecognitionPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACTIVITY_RECOGNITION},
                        PERMISSION_REQUEST_ACTIVITY_RECOGNITION);
            } else {
                accessGoogleFitData(); // Already granted
            }
        } else {
            accessGoogleFitData();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_ACTIVITY_RECOGNITION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                accessGoogleFitData();
            } else {
                Toast.makeText(this, "Permission required for step tracking", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            accessGoogleFitData();
        }
    }

    private void accessGoogleFitData() {
        Calendar cal = Calendar.getInstance();
        long endTime = cal.getTimeInMillis();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long startTime = cal.getTimeInMillis();

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .aggregate(DataType.TYPE_DISTANCE_DELTA, DataType.AGGREGATE_DISTANCE_DELTA)
                .aggregate(DataType.TYPE_CALORIES_EXPENDED, DataType.AGGREGATE_CALORIES_EXPENDED)
                .aggregate(DataType.TYPE_MOVE_MINUTES, DataType.AGGREGATE_MOVE_MINUTES)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        Fitness.getHistoryClient(this, GoogleSignIn.getAccountForExtension(this, fitnessOptions))
                .readData(readRequest)
                .addOnSuccessListener(response -> {
                    int totalSteps = 0;
                    float totalDistance = 0f;
                    float totalCalories = 0f;
                    int totalMoveMinutes = 0;

                    for (Bucket bucket : response.getBuckets()) {
                        for (DataSet dataSet : bucket.getDataSets()) {
                            for (DataPoint dp : dataSet.getDataPoints()) {
                                for (Field field : dp.getDataType().getFields()) {
                                    Value value = dp.getValue(field);
                                    switch (field.getName()) {
                                        case "steps":
                                            totalSteps += value.asInt();
                                            break;
                                        case "distance":
                                            totalDistance += value.asFloat();
                                            break;
                                        case "calories":
                                            totalCalories += value.asFloat();
                                            break;
                                        case "duration":
                                            totalMoveMinutes += (int) TimeUnit.MILLISECONDS.toMinutes(dp.getEndTime(TimeUnit.MILLISECONDS) - dp.getStartTime(TimeUnit.MILLISECONDS));
                                            break;
                                        default:
                                            Log.d("GoogleFit", "Unhandled field: " + field.getName());
                                    }
                                }
                            }
                        }
                    }

                    tvStepCount.setText(String.valueOf(totalSteps));
                    tvDistance.setText(String.format(Locale.getDefault(), "%.2f m", totalDistance));
                    tvCalories.setText(String.format(Locale.getDefault(), "%.1f kcal", totalCalories));
                    tvMoveMinutes.setText(totalMoveMinutes + " min");

                })
                .addOnFailureListener(e -> {
                    Log.e("GoogleFit", "Failed to read data", e);
                    Toast.makeText(this, "Failed to fetch Google Fit data", Toast.LENGTH_SHORT).show();
                });
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
                            }
                        } catch (NumberFormatException ignored) {}
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
            tvBMIValue.setText("BMI: N/A");
            tvBMIBadge.setText("Guest");
            return;
        }

        String uid = mAuth.getCurrentUser().getUid();
        DocumentReference userRef = db.collection("users").document(uid);

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String heightStr = documentSnapshot.getString("height");
                String weightStr = documentSnapshot.getString("weight");
                calculateAndDisplayBMI(heightStr, weightStr);
            }
        });
    }

    private void calculateAndDisplayBMI(String heightStr, String weightStr) {
        if (heightStr == null || weightStr == null || heightStr.isEmpty() || weightStr.isEmpty()) {
            tvBMIValue.setText("BMI: N/A");
            tvBMIBadge.setText("Missing");
            tvBMIBadge.setBackgroundTintList(null);
            return;
        }

        try {
            float heightInFeet = 0f;

            if (heightStr.contains("'")) {
                String[] parts = heightStr.split("'");
                int feet = Integer.parseInt(parts[0].trim());
                int inches = parts.length > 1 ? Integer.parseInt(parts[1].replaceAll("[^\\d]", "").trim()) : 0;
                heightInFeet = feet + (inches / 12.0f);
            } else if (heightStr.toLowerCase().contains("ft")) {
                String[] parts = heightStr.toLowerCase().split("ft");
                int feet = Integer.parseInt(parts[0].replaceAll("[^\\d]", "").trim());
                int inches = (parts.length > 1) ? Integer.parseInt(parts[1].replaceAll("[^\\d]", "").trim()) : 0;
                heightInFeet = feet + (inches / 12.0f);
            } else {
                heightInFeet = Float.parseFloat(heightStr.replaceAll("[^\\d.]", "").trim());
            }

            float heightCm = heightInFeet * 30.48f;
            float heightM = heightCm / 100f;
            float weightKg = Float.parseFloat(weightStr.replaceAll("[^\\d.]", "").trim());

            if (heightM <= 0 || weightKg <= 0) {
                tvBMIValue.setText("BMI: Invalid");
                tvBMIBadge.setText("Check values");
                return;
            }

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
            tvBMIValue.setText("BMI: Error");
            tvBMIBadge.setText("Error");
            tvBMIBadge.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.red)));
            Log.e("BMI", "Error calculating BMI", e);
        }
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
