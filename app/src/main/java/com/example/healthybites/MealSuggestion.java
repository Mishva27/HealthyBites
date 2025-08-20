package com.example.healthybites;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthybites.adapters.MealSuggestionAdapter;
import com.example.healthybites.models.Meal;
import com.example.healthybites.models.MealModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MealSuggestion extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MealSuggestionAdapter mealSuggestionAdapter;
    private List<MealModel> mealList;
    private DatabaseReference databaseReference;

    private ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_suggestion);

        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MealSuggestion.this, HomeActivity.class);
            // Clear other activities so Home becomes the top
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        recyclerView = findViewById(R.id.recyclerMealSuggestion);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mealList = new ArrayList<>();
        mealSuggestionAdapter = new MealSuggestionAdapter(mealList);
        recyclerView.setAdapter(mealSuggestionAdapter);

        // ✅ Map goal to Firebase key
        String selectedGoal = getIntent().getStringExtra("goal");

        if (selectedGoal == null) {
            selectedGoal = "gain weight"; // fallback if nothing is passed
        }

        String goalKey = mapGoalToKey(selectedGoal);
        Log.d("MealSuggestion", "SelectedGoal: " + selectedGoal + " | GoalKey: " + goalKey);

        // ✅ Reference to meals_suggestion/{goalKey}
        databaseReference = FirebaseDatabase.getInstance()
                .getReference("meals_suggetion")
                .child(goalKey);


        fetchMealsFromFirebase();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> startActivity(new Intent(MealSuggestion.this, MainActivity.class)));

        // Bottom Navigation Setup
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.mealPlanner);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                Intent intent = new Intent(MealSuggestion.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_coach) {
                Intent intent = new Intent(MealSuggestion.this, CoachActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.mealPlanner) {
                return true;
            } else if (itemId == R.id.nav_help) {
                Intent intent = new Intent(MealSuggestion.this, HelpActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
            }

            return false;
        });
    }

    private void fetchMealsFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mealList.clear();
                for (DataSnapshot mealSnapshot : snapshot.getChildren()) {
                    MealModel meal = mealSnapshot.getValue(MealModel.class);
                    if (meal != null) {
                        mealList.add(meal);
                    }
                }
                mealSuggestionAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("MealSuggestion", "Database error: " + error.getMessage());
            }
        });
    }

    // ✅ Converts user-friendly goal → Firebase key
    private String mapGoalToKey(String selectedGoal) {
        switch (selectedGoal.toLowerCase()) {
            case "gain weight":
                return "gain_weight";
            case "lose weight":
                return "lose_weight";
            case "maintain weight":
                return "maintain_weight";
            default:
                return "gain_weight"; // fallback
        }
    }
}
