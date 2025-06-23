package com.example.healthybytes;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthybytes.adapters.MealAdapter;
import com.example.healthybytes.models.Meal;
import com.example.healthybytes.utils.FirebaseUtils;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.List;

public class MealHistoryActivity extends AppCompatActivity {

    RecyclerView mealRecyclerView;
    MealAdapter adapter;
    List<Meal> mealList;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_history);

        mealRecyclerView = findViewById(R.id.mealRecyclerView);
        mealList = new ArrayList<>();
        adapter = new MealAdapter(mealList);
        mealRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mealRecyclerView.setAdapter(adapter);

        db = FirebaseUtils.getFirestore();

        String userId = FirebaseUtils.getCurrentUserId();
        if (userId != null) {
            db.collection("users").document(userId)
                    .collection("meals")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .addSnapshotListener((value, error) -> {
                        if (error != null) {
                            Toast.makeText(this, "Failed to load meals", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (value != null) {
                            mealList.clear();
                            for (DocumentSnapshot doc : value) {
                                Meal meal = doc.toObject(Meal.class);
                                mealList.add(meal);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    });
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }
}
