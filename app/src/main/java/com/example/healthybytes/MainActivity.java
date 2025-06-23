package com.example.healthybytes;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button addMealBtn, historyBtn, insightsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addMealBtn = findViewById(R.id.btnAddMeal);
        historyBtn = findViewById(R.id.btnMealHistory);
        insightsBtn = findViewById(R.id.btnNutritionInsights);

        addMealBtn.setOnClickListener(v -> startActivity(new Intent(this, AddMealActivity.class)));
        historyBtn.setOnClickListener(v -> startActivity(new Intent(this, MealHistoryActivity.class)));
        insightsBtn.setOnClickListener(v -> startActivity(new Intent(this, NutritionInsightsActivity.class)));
    }
}