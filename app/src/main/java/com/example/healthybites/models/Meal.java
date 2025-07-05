package com.example.healthybites.models;

import java.util.List;

public class Meal {
    private String mealType; // Breakfast/Lunch/Dinner
    private long timestamp;
    private List<FoodItem> foodItems;

    public Meal() {} // Required for Firestore

    public Meal(String mealType, long timestamp, List<FoodItem> foodItems) {
        this.mealType = mealType;
        this.timestamp = timestamp;
        this.foodItems = foodItems;
    }

    // Getters and setters
    public String getMealType() { return mealType; }
    public long getTimestamp() { return timestamp; }
    public List<FoodItem> getFoodItems() { return foodItems; }
}
