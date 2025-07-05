package com.example.healthybites.models;

public class FoodItem {
    private String name;
    private int calories;
    private int carbs;
    private int proteins;
    private int fats;

    public FoodItem() {}

    public FoodItem(String name, int calories, int carbs, int proteins, int fats) {
        this.name = name;
        this.calories = calories;
        this.carbs = carbs;
        this.proteins = proteins;
        this.fats = fats;
    }

    // Getters
    public String getName() { return name; }
    public int getCalories() { return calories; }
    public int getCarbs() { return carbs; }
    public int getProteins() { return proteins; }
    public int getFats() { return fats; }
}
