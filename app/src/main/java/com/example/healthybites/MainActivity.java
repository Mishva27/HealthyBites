package com.example.healthybites; // Change to your actual package name

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.healthybites.fragments.fragment_add_meal;
import com.example.healthybites.fragments.fragment_view_meals;

public class MainActivity extends AppCompatActivity {

    private Button btnAddMeal, btnNutritionInsights, btnMealSuggestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind buttons to IDs from your XML
        btnAddMeal = findViewById(R.id.btnAddMeal);
        btnNutritionInsights = findViewById(R.id.btnNutritionInsights);

        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(v -> {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
                findViewById(R.id.main_buttons).setVisibility(View.VISIBLE);
            } else {
                finish();
            }
        });



        // Handle "Add New Meal" button click
        btnAddMeal.setOnClickListener(v -> {
            fragment_add_meal fragment = new fragment_add_meal();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null) // Essential!
                    .commit();

            findViewById(R.id.main_buttons).setVisibility(View.GONE);
        });


        // Handle "Nutrition Insights" button click
        btnNutritionInsights.setOnClickListener(v -> {
            fragment_view_meals fragment = new fragment_view_meals();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null) // Essential!
                    .commit();

            findViewById(R.id.main_buttons).setVisibility(View.GONE);
        });

    }

    @Override
    public void onBackPressed() {
        // If you want default back behavior:
        super.onBackPressed();
    }
}
