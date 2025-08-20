package com.example.healthybites.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthybites.R;
import com.example.healthybites.models.MealModel;

import java.util.List;

public class MealSuggestionAdapter extends RecyclerView.Adapter<MealSuggestionAdapter.ViewHolder> {

    private final List<MealModel> mealList;

    public MealSuggestionAdapter(List<MealModel> mealList) {
        this.mealList = mealList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mealsuggestion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MealModel meal = mealList.get(position);

        if (meal != null) {
            holder.tvName.setText(meal.getName());
            holder.tvDescription.setText(meal.getDescription());
            holder.tvCalories.setText("Calories: " + meal.getCalories());
            holder.tvIngredients.setText("Ingredients: " + meal.getIngredients());
        }
    }

    @Override
    public int getItemCount() {
        return (mealList != null) ? mealList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDescription, tvCalories, tvIngredients;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvMealName);
            tvDescription = itemView.findViewById(R.id.tvMealDescription);
            tvCalories = itemView.findViewById(R.id.tvMealCalories);
            tvIngredients = itemView.findViewById(R.id.tvMealIngredients);
        }
    }
}
