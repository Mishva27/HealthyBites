package com.example.healthybites.models;

public class MealModel {
    private int calories;
    private String description;
    private String ingredients;
    private String name;

    // Empty constructor required for Firebase
    public MealModel() {}

    public MealModel(int calories, String description, String ingredients, String name) {
        this.calories = calories;
        this.description = description;
        this.ingredients = ingredients;
        this.name = name;
    }

    public int getCalories() {
        return calories;
    }

    public String getDescription() {
        return description;
    }

    public String getIngredients() {
        return ingredients;
    }

    public String getName() {
        return name;
    }
}
