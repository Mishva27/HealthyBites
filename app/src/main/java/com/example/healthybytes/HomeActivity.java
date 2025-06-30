package com.example.healthybytes;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

public class HomeActivity extends AppCompatActivity {

    TextView toolbarUserName, tvBMIValue, tvBMIBadge;
    ImageView toolbarProfileIcon;
    Button logoutButton;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    LineChart weightTrendChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Bind views
        toolbarUserName = findViewById(R.id.toolbarUserName);
        toolbarProfileIcon = findViewById(R.id.toolbarProfileIcon);
        logoutButton = findViewById(R.id.logoutButton);
        tvBMIValue = findViewById(R.id.tvBMIValue);
        weightTrendChart = findViewById(R.id.weightTrendChart);
        tvBMIBadge = findViewById(R.id.tvBMIBadge);


        setUserGreeting();
        fetchUserDetailsAndCalculateBMI();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, MainActivity.class)));

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
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });
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
        String uid = mAuth.getCurrentUser().getUid();
        DocumentReference userRef = db.collection("users").document(uid);

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String heightStr = documentSnapshot.getString("height");
                String weightStr = documentSnapshot.getString("weight");
                calculateAndDisplayBMI(heightStr, weightStr);
            }
        });

        // Optional: Load chart with dummy or historical data
        loadWeightTrendChart();
    }

    private void calculateAndDisplayBMI(String heightStr, String weightStr) {
        if (heightStr == null || weightStr == null || heightStr.isEmpty() || weightStr.isEmpty()) {
            tvBMIValue.setText("BMI: N/A");
            tvBMIBadge.setText(""); // Hide badge
            tvBMIBadge.setBackgroundTintList(null);
            return;
        }

        try {
            float heightCm = Float.parseFloat(heightStr);
            float weightKg = Float.parseFloat(weightStr);
            float heightM = heightCm / 100;

            float bmi = weightKg / (heightM * heightM);
            String result = String.format("%.1f", bmi);
            tvBMIValue.setText(result);

            // 🟢 Set BMI badge
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

        } catch (NumberFormatException e) {
            tvBMIValue.setText("BMI: Error");
            tvBMIBadge.setText("Error");
            tvBMIBadge.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.red)));
        }
    }


    private String getBMICategory(float bmi) {
        if (bmi < 18.5)
            return "Underweight";
        else if (bmi < 24.9)
            return "Normal";
        else if (bmi < 29.9)
            return "Overweight";
        else
            return "Obese";
    }

    private void loadWeightTrendChart() {
        List<Entry> entries = new ArrayList<>();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users")
                .document(uid)
                .collection("weightHistory")
                .orderBy("date") // assuming you store weight logs by date
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

                    // ✅ CHART CONFIGURATION GOES HERE
                    weightTrendChart.getDescription().setEnabled(false);
                    weightTrendChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                    weightTrendChart.getAxisRight().setEnabled(false);
                    weightTrendChart.getAxisLeft().setDrawGridLines(false);
                    weightTrendChart.getXAxis().setDrawGridLines(false);

                    weightTrendChart.invalidate(); // redraw chart
                });
    }
}
