package com.example.healthybites;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.widget.Button;

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