package com.example.healthybites;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserGoalSetupActivity extends AppCompatActivity {

    private EditText fullNameEditText, ageEditText, weightEditText;
    private RadioGroup genderRadioGroup;
    private Spinner goalSpinner, heightSpinner;
    private Button saveButton;

    private String selectedGoal, selectedHeight;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_goalsetup);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // UI Components
        fullNameEditText = findViewById(R.id.editTextFullName);
        ageEditText = findViewById(R.id.editTextAge);
        weightEditText = findViewById(R.id.editTextWeight);
        genderRadioGroup = findViewById(R.id.genderGroup);
        goalSpinner = findViewById(R.id.goalSpinner);
        heightSpinner = findViewById(R.id.heightSpinner);
        saveButton = findViewById(R.id.buttonSave);

        // Goal Spinner setup
        ArrayAdapter<CharSequence> goalAdapter = ArrayAdapter.createFromResource(this,
                R.array.user_goals, android.R.layout.simple_spinner_item);
        goalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        goalSpinner.setAdapter(goalAdapter);
        goalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGoal = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Height Spinner setup
        ArrayAdapter<CharSequence> heightAdapter = ArrayAdapter.createFromResource(this,
                R.array.user_heights, android.R.layout.simple_spinner_item);
        heightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        heightSpinner.setAdapter(heightAdapter);
        heightSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedHeight = parent.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Save Button Click Listener
        saveButton.setOnClickListener(v -> saveUserData());
    }

    /**
     * Map human-readable spinner values to Firebase keys
     */
    private String mapGoalToKey(String selectedGoal) {
        switch (selectedGoal.toLowerCase()) {
            case "gain weight":
                return "gain_weight";
            case "lose weight":
                return "lose_weight";
            case "maintain weight":
                return "maintain_weight";
            default:
                return "gain_weight"; // fallback
        }
    }

    private void saveUserData() {
        String fullName = fullNameEditText.getText().toString().trim();
        String age = ageEditText.getText().toString().trim();
        String weight = weightEditText.getText().toString().trim();

        // Gender
        int selectedGenderId = genderRadioGroup.getCheckedRadioButtonId();
        RadioButton selectedGenderRadio = findViewById(selectedGenderId);
        String gender = selectedGenderRadio != null ? selectedGenderRadio.getText().toString() : "";

        // Validation
        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(age) || TextUtils.isEmpty(weight)
                || TextUtils.isEmpty(gender) || TextUtils.isEmpty(selectedGoal) || TextUtils.isEmpty(selectedHeight)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String goalKey = mapGoalToKey(selectedGoal);

        // Prepare user data
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("fullName", fullName);
        userMap.put("email", currentUser.getEmail());
        userMap.put("age", age);
        userMap.put("weight", weight);
        userMap.put("gender", gender);
        userMap.put("goal", selectedGoal);
        userMap.put("height", selectedHeight);

        // Save to Firestore
        db.collection("users")
                .document(currentUser.getUid())
                .set(userMap)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Profile Saved!", Toast.LENGTH_SHORT).show();

                    // ✅ Pass the Firebase key to MealSuggestion
                    Intent intent = new Intent(UserGoalSetupActivity.this, MealSuggestion.class);
                    intent.putExtra("goal", goalKey);
                    startActivity(intent);

                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to save: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
