package com.example.healthybites.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.healthybites.R; // Make sure this matches your actual package
import com.google.firebase.database.*;

import java.util.*;

public class fragment_add_meal extends Fragment {

    private RadioGroup mealTypeGroup;
    private AutoCompleteTextView foodSearch;
    private EditText quantityInput;
    private Button addButton;
    private TextView totalCaloriesText;
    private ListView mealListView;

    private ArrayAdapter<String> autoCompleteAdapter;
    private ArrayAdapter<String> mealAdapter;

    private List<String> foodNames = new ArrayList<>();
    private Map<String, Integer> foodCalories = new HashMap<>();
    private List<String> mealEntries = new ArrayList<>();
    private int totalCalories = 0;

    private DatabaseReference foodRef;

    public fragment_add_meal() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add_meal, container, false);

        // Find views by ID
        mealTypeGroup = root.findViewById(R.id.meal_type_group);
        foodSearch = root.findViewById(R.id.food_search);
        quantityInput = root.findViewById(R.id.quantity_input);
        addButton = root.findViewById(R.id.add_button);
        totalCaloriesText = root.findViewById(R.id.total_calories_text);
        mealListView = root.findViewById(R.id.meal_list_view);

        // Setup adapters
        autoCompleteAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, foodNames);
        foodSearch.setAdapter(autoCompleteAdapter);
        foodSearch.setThreshold(1); // start showing suggestions after 1 character

        mealAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, mealEntries);
        mealListView.setAdapter(mealAdapter);

        // Reference to Firebase
        foodRef = FirebaseDatabase.getInstance().getReference("foods");

        // Load food list from Firebase
        loadFoodItemsFromFirebase();

        // Button click
        addButton.setOnClickListener(v -> {
            String food = foodSearch.getText().toString().trim();
            String qtyStr = quantityInput.getText().toString().trim();

            if (food.isEmpty() || !foodCalories.containsKey(food)) {
                Toast.makeText(getContext(), "Please select a valid food item", Toast.LENGTH_SHORT).show();
                return;
            }

            if (qtyStr.isEmpty()) {
                Toast.makeText(getContext(), "Enter quantity", Toast.LENGTH_SHORT).show();
                return;
            }

            int quantity = Integer.parseInt(qtyStr);
            int calories = (foodCalories.containsKey(food) ? foodCalories.get(food) : 0) * quantity;
            totalCalories += calories;

            String entry = food + " × " + quantity + " = " + calories + " kcal";
            mealEntries.add(entry);
            mealAdapter.notifyDataSetChanged();

            totalCaloriesText.setText("Total Calories: " + totalCalories);
            quantityInput.setText("");
            foodSearch.setText("");

            // Get meal type
            int selectedId = mealTypeGroup.getCheckedRadioButtonId();
            String mealType = "unknown";
            if (selectedId == R.id.radio_breakfast) {
                mealType = "breakfast";
            } else if (selectedId == R.id.radio_lunch) {
                mealType = "lunch";
            } else if (selectedId == R.id.radio_dinner) {
                mealType = "dinner";
            }

            // Get current date
            String date = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O
                    ? java.time.LocalDate.now().toString()
                    : new java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            // Save to Firebase
            DatabaseReference mealRef = FirebaseDatabase.getInstance()
                    .getReference("meals")
                    .child(date)
                    .child(mealType);

            mealRef.push().setValue(entry);
        });

        return root;
    }

    private void loadFoodItemsFromFirebase() {
        foodRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                foodNames.clear();
                foodCalories.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    String name = itemSnapshot.child("name").getValue(String.class);
                    Double calories = itemSnapshot.child("calories").getValue(Double.class);
                    if (name != null && calories != null) {
                        foodNames.add(name);
                        foodCalories.put(name, calories.intValue());
                    }
                }
                autoCompleteAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load food items", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
