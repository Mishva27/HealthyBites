package com.example.healthybites;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class UserGoalSetupActivity extends AppCompatActivity {

    EditText fullName, email, age, weight;
    Spinner goalSpinner, heightSpinner;
    RadioGroup genderGroup;
    Button saveButton;
    FirebaseFirestore db;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_goalsetup);

        // Initialize views
        fullName = findViewById(R.id.editTextFullName);
        email = findViewById(R.id.editTextEmail);
        age = findViewById(R.id.editTextAge);
        weight = findViewById(R.id.editTextWeight);
        goalSpinner = findViewById(R.id.goalSpinner);
        heightSpinner = findViewById(R.id.heightSpinner);
        genderGroup = findViewById(R.id.genderGroup);
        saveButton = findViewById(R.id.buttonSave);

        // Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Set Goal Spinner Options
        String[] goals = {"Weight Loss", "Weight Gain", "Maintain Weight"};
        ArrayAdapter<String> goalAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, goals);
        goalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        goalSpinner.setAdapter(goalAdapter);

        // Set Height Spinner Options
        String[] heights = {"4 ft", "4.1 ft", "4.2 ft", "4.3 ft", "4.4 ft", "4.5 ft", "4.6 ft", "4.7 ft", "4.8 ft", "4.9 ft",
                "5 ft", "5.1 ft", "5.2 ft", "5.3 ft", "5.4 ft", "5.5 ft", "5.6 ft", "5.7 ft", "5.8 ft", "5.9 ft",
                "6 ft", "6.1 ft", "6.2 ft", "6.3 ft", "6.4 ft", "6.5 ft"};
        ArrayAdapter<String> heightAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, heights);
        heightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        heightSpinner.setAdapter(heightAdapter);

        saveButton.setOnClickListener(v -> saveUserProfile());
    }

    private void saveUserProfile() {
        String uid = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

        if (uid == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String fullNameStr = fullName.getText().toString().trim();
        String emailStr = email.getText().toString().trim();
        String goalStr = goalSpinner.getSelectedItem().toString();
        String ageStr = age.getText().toString().trim();
        String weightStr = weight.getText().toString().trim();
        String heightStr = heightSpinner.getSelectedItem().toString();

        int selectedGenderId = genderGroup.getCheckedRadioButtonId();
        if (selectedGenderId == -1) {
            Toast.makeText(this, "Please select your gender", Toast.LENGTH_SHORT).show();
            return;
        }
        RadioButton selectedGenderButton = findViewById(selectedGenderId);
        String genderStr = selectedGenderButton.getText().toString();

        if (fullNameStr.isEmpty() || emailStr.isEmpty() || ageStr.isEmpty() || weightStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("fullName", fullNameStr);
        userMap.put("email", emailStr);
        userMap.put("goal", goalStr);
        userMap.put("age", ageStr);
        userMap.put("gender", genderStr);
        userMap.put("height", heightStr);
        userMap.put("weight", weightStr);

        db.collection("users")
                .document(uid)
                .set(userMap)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Profile saved!", Toast.LENGTH_SHORT).show();

                    // Redirect to Home
                    Intent intent = new Intent(UserGoalSetupActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
