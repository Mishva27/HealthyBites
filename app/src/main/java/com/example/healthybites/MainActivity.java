package com.example.healthybites;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.healthybites.fragments.fragment_add_meal;
import com.example.healthybites.fragments.fragment_view_meals;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private Button btnAddMeal, btnNutritionInsights, btnMealSuggestion;
    private EditText etMealName;
    private DatabaseReference mealsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Firebase reference
        mealsRef = FirebaseDatabase.getInstance().getReference("UserSuggestions");

        // Bind buttons to IDs from your XML
        btnAddMeal = findViewById(R.id.btnAddMeal);
        btnNutritionInsights = findViewById(R.id.btnNutritionInsights);
        btnMealSuggestion = findViewById(R.id.btnUserSuggestion);
        etMealName = findViewById(R.id.etMealName);

        // Back button
        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(v -> {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
                findViewById(R.id.main_buttons).setVisibility(View.VISIBLE);
            } else {
                finish();
            }
        });

        // "Add New Meal"
        btnAddMeal.setOnClickListener(v -> {
            fragment_add_meal fragment = new fragment_add_meal();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
            findViewById(R.id.main_buttons).setVisibility(View.GONE);
        });

        // "Nutrition Insights"
        btnNutritionInsights.setOnClickListener(v -> {
            fragment_view_meals fragment = new fragment_view_meals();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
            findViewById(R.id.main_buttons).setVisibility(View.GONE);
        });

        // Submit Meal to Firebase
        btnMealSuggestion.setOnClickListener(v -> {
            String mealName = etMealName.getText().toString().trim();
            if (!TextUtils.isEmpty(mealName)) {
                String mealId = mealsRef.push().getKey();
                mealsRef.child(mealId).setValue(mealName)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(MainActivity.this, "Meal submitted!", Toast.LENGTH_SHORT).show();
                            etMealName.setText("");
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(MainActivity.this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                        );
            } else {
                Toast.makeText(MainActivity.this, "Please enter a meal name", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
