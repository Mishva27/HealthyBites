package com.example.healthybites;

//log new meals
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthybites.adapters.FoodItemAdapter;
import com.example.healthybites.models.FoodItem;
import com.example.healthybites.models.Meal;
import com.example.healthybites.utils.FirebaseUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AddMealActivity extends AppCompatActivity {

    Spinner mealTypeSpinner;
    EditText foodNameInput, calInput, carbInput, proteinInput, fatInput;
    Button addFoodBtn, saveMealBtn;
    RecyclerView foodRecycler;

    List<FoodItem> foodItemList;
    FoodItemAdapter adapter; // you'll create this adapter
    FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meal);

        mealTypeSpinner = findViewById(R.id.mealTypeSpinner);
        foodNameInput = findViewById(R.id.foodNameInput);
        calInput = findViewById(R.id.calInput);
        carbInput = findViewById(R.id.carbInput);
        proteinInput = findViewById(R.id.proteinInput);
        fatInput = findViewById(R.id.fatInput);
        addFoodBtn = findViewById(R.id.addFoodBtn);
        saveMealBtn = findViewById(R.id.saveMealBtn);
        foodRecycler = findViewById(R.id.foodRecycler);

        db = FirebaseUtils.getFirestore();
        foodItemList = new ArrayList<>();
        adapter = new FoodItemAdapter(foodItemList);
        foodRecycler.setLayoutManager(new LinearLayoutManager(this));
        foodRecycler.setAdapter(adapter);

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("FIREBASE_UID", "User ID: " + uid);


        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item,
                new String[]{"Breakfast", "Lunch", "Dinner", "Snacks"}
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mealTypeSpinner.setAdapter(spinnerAdapter);

        addFoodBtn.setOnClickListener(v -> addFoodItem());
        saveMealBtn.setOnClickListener(v -> saveMealToFirestore());
    }

    private void addFoodItem() {
        String name = foodNameInput.getText().toString().trim();
        int cal = Integer.parseInt(calInput.getText().toString().trim());
        int carbs = Integer.parseInt(carbInput.getText().toString().trim());
        int protein = Integer.parseInt(proteinInput.getText().toString().trim());
        int fat = Integer.parseInt(fatInput.getText().toString().trim());

        foodItemList.add(new FoodItem(name, cal, carbs, protein, fat));
        adapter.notifyDataSetChanged();

        // Clear input fields
        foodNameInput.setText("");
        calInput.setText("");
        carbInput.setText("");
        proteinInput.setText("");
        fatInput.setText("");
    }

    private void saveMealToFirestore() {
        String mealType = mealTypeSpinner.getSelectedItem().toString();
        long timestamp = System.currentTimeMillis();

        Meal meal = new Meal(mealType, timestamp, foodItemList);
        String uid = FirebaseUtils.getCurrentUserId();

        if (uid == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users").document(uid)
                .collection("meals").add(meal)
                .addOnSuccessListener(documentReference ->
                        Toast.makeText(this, "Meal saved!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error saving meal", Toast.LENGTH_SHORT).show());
    }
}
