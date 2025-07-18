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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;

public class HomeActivity extends AppCompatActivity {

    TextView toolbarUserName, tvBMIValue, tvBMIBadge, tvWaterValue, tvWaterGoal, tvStepCount;
    ImageView toolbarProfileIcon;
    Button btnSetWaterGoal, btnAddWeight;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    LineChart weightTrendChart;
    ImageButton btnIncreaseWater, btnDecreaseWater;
    final float[] waterAmount = {0.0f};
    FitnessOptions fitnessOptions;

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
        weightTrendChart = findViewById(R.id.weightTrendChart);
        tvBMIBadge = findViewById(R.id.tvBMIBadge);
        btnIncreaseWater = findViewById(R.id.btnIncreaseWater);
        btnDecreaseWater = findViewById(R.id.btnDecreaseWater);
        tvWaterValue = findViewById(R.id.tvWaterValue);
        tvWaterGoal = findViewById(R.id.tvWaterGoal);
        btnSetWaterGoal = findViewById(R.id.btnSetWaterGoal);
        tvStepCount = findViewById(R.id.tvStepCount);

        View appBar = findViewById(R.id.app_bar);
        toolbarProfileIcon = appBar.findViewById(R.id.toolbarProfileIcon);

        toolbarProfileIcon.setOnClickListener(v -> {
            Log.d("ToolbarTest", "Profile icon clicked!");
            Intent intent = new Intent(HomeActivity.this, MyDetailsActivity.class);
            startActivity(intent);
        });

        if (!isGuest) {
            setupProfileIconClick();
            setUserGreeting();
            fetchUserDetailsAndCalculateBMI();

            fitnessOptions = FitnessOptions.builder()
                    .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                    .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
                    .build();

            GoogleSignInAccount account = GoogleSignIn.getAccountForExtension(this, fitnessOptions);

            if (!GoogleSignIn.hasPermissions(account, fitnessOptions)) {
                GoogleSignIn.requestPermissions(this, 1001, account, fitnessOptions);
            } else {
                accessGoogleFitData();
            }
        } else {
            Toast.makeText(this, "Guest Mode Activated", Toast.LENGTH_SHORT).show();
            toolbarUserName.setText("Hello, Guest!");
        }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            accessGoogleFitData();
        }
    }

    private void accessGoogleFitData() {
        Fitness.getHistoryClient(this, GoogleSignIn.getAccountForExtension(this, fitnessOptions))
                .readDailyTotal(DataType.TYPE_STEP_COUNT_DELTA)
                .addOnSuccessListener(dataSet -> {
                    long totalSteps = dataSet.isEmpty() ? 0 :
                            dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
                    tvStepCount.setText("Steps Today: " + totalSteps);
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
            weightTrendChart.setNoDataText("Not available for Guest Mode.");
            weightTrendChart.invalidate();
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

        loadWeightTrendChart();
    }

    private void calculateAndDisplayBMI(String heightStr, String weightStr) {
        if (heightStr == null || weightStr == null || heightStr.isEmpty() || weightStr.isEmpty()) {
            tvBMIValue.setText("BMI: N/A");
            tvBMIBadge.setText("Missing");
            tvBMIBadge.setBackgroundTintList(null);
            return;
        }

        try {
            heightStr = heightStr.replaceAll("[^\\d.]", "").trim();
            weightStr = weightStr.replaceAll("[^\\d.]", "").trim();

            float heightInFeet = Float.parseFloat(heightStr);
            float heightCm = heightInFeet * 30.48f;
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
            tvBMIValue.setText("BMI: Error");
            tvBMIBadge.setText("Error");
            tvBMIBadge.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.red)));
        }
    }

    private void loadWeightTrendChart() {
        if (mAuth.getCurrentUser() == null) {
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