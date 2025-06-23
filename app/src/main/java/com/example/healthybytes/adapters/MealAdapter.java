package com.example.healthybytes.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthybytes.R;
import com.example.healthybytes.models.FoodItem;
import com.example.healthybytes.models.Meal;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {

    private List<Meal> mealList;

    public MealAdapter(List<Meal> mealList) {
        this.mealList = mealList;
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_meal, parent, false);
        return new MealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        Meal meal = mealList.get(position);
        holder.mealType.setText("Meal: " + meal.getMealType());

        String formattedDate = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                .format(meal.getTimestamp());
        holder.timestamp.setText(formattedDate);

        StringBuilder items = new StringBuilder();
        for (FoodItem item : meal.getFoodItems()) {
            items.append("- ").append(item.getName())
                    .append(" (").append(item.getCalories()).append(" cal)\n");
        }
        holder.foodItems.setText(items.toString().trim());
    }

    @Override
    public int getItemCount() {
        return mealList.size();
    }

    static class MealViewHolder extends RecyclerView.ViewHolder {
        TextView mealType, timestamp, foodItems;

        public MealViewHolder(@NonNull View itemView) {
            super(itemView);
            mealType = itemView.findViewById(R.id.mealType);
            timestamp = itemView.findViewById(R.id.timestamp);
            foodItems = itemView.findViewById(R.id.foodItems);
        }
    }
}
