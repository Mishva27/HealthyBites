package com.example.healthybites;

import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.healthybites.models.FoodItem;
import com.example.healthybites.models.Meal;
import com.example.healthybites.utils.FirebaseUtils;
import com.google.firebase.firestore.*;

import java.util.List;

public class NutritionInsightsActivity extends AppCompatActivity {

    TextView totalCalories, totalCarbs, totalProteins, totalFats, feedback;

    int calories = 0, carbs = 0, proteins = 0, fats = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrition_insights);

        totalCalories = findViewById(R.id.totalCalories);
        totalCarbs = findViewById(R.id.totalCarbs);
        totalProteins = findViewById(R.id.totalProteins);
        totalFats = findViewById(R.id.totalFats);
        feedback = findViewById(R.id.feedback);

        fetchMealsAndAnalyze();
    }

    private void fetchMealsAndAnalyze() {
        String uid = FirebaseUtils.getCurrentUserId();
        if (uid == null) return;

        FirebaseUtils.getFirestore()
                .collection("users").document(uid)
                .collection("meals")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Meal meal = doc.toObject(Meal.class);
                        if (meal != null) {
                            List<FoodItem> items = meal.getFoodItems();
                            for (FoodItem item : items) {
                                calories += item.getCalories();
                                carbs += item.getCarbs();
                                proteins += item.getProteins();
                                fats += item.getFats();
                            }
                        }
                    }

                    showInsights();
                })
                .addOnFailureListener(e ->
                        feedback.setText("Failed to load nutrition data."));
    }

    private void showInsights() {
        totalCalories.setText("Calories: " + calories);
        totalCarbs.setText("Carbs: " + carbs + "g");
        totalProteins.setText("Proteins: " + proteins + "g");
        totalFats.setText("Fats: " + fats + "g");

        StringBuilder fb = new StringBuilder("Feedback:\n");

        if (calories > 2500) fb.append("- You are eating high calories.\n");
        else if (calories < 1500) fb.append("- You may need more energy.\n");

        if (proteins < 50) fb.append("- Add more proteins like pulses or dairy.\n");
        if (carbs > 300) fb.append("- Consider reducing carbohydrate-rich foods.\n");
        if (fats > 70) fb.append("- Try to limit fatty items.\n");

        if (fb.toString().equals("Feedback:\n"))
            fb.append("- Your diet looks balanced! 🥦");

        feedback.setText(fb.toString());
    }
}
