package com.example.healthybites.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthybites.R;
import com.example.healthybites.models.FoodItem;
import java.util.List;

public class FoodItemAdapter extends RecyclerView.Adapter<FoodItemAdapter.FoodViewHolder> {

    private List<FoodItem> foodItemList;

    public FoodItemAdapter(List<FoodItem> foodItemList) {
        this.foodItemList = foodItemList;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        FoodItem item = foodItemList.get(position);
        holder.foodName.setText(item.getName());
        holder.calories.setText("Cal: " + item.getCalories());
        holder.macros.setText("C: " + item.getCarbs() + "g | P: " + item.getProteins() + "g | F: " + item.getFats() + "g");
    }

    @Override
    public int getItemCount() {
        return foodItemList.size();
    }

    static class FoodViewHolder extends RecyclerView.ViewHolder {
        TextView foodName, calories, macros;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.foodName);
            calories = itemView.findViewById(R.id.calories);
            macros = itemView.findViewById(R.id.macros);
        }
    }
}
