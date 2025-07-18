package com.example.healthybites;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class UserGoalSetupActivity extends AppCompatActivity {

    private EditText fullName, age, weight;
    private Spinner heightSpinner, goalSpinner;
    private RadioGroup genderGroup;
    private RadioButton selectedGender;
    private Button saveProfileBtn;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_goalsetup);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // UI references
        fullName = findViewById(R.id.editTextFullName);
        age = findViewById(R.id.editTextAge);
        weight = findViewById(R.id.editTextWeight);
        heightSpinner = findViewById(R.id.heightSpinner);
        goalSpinner = findViewById(R.id.goalSpinner);
        genderGroup = findViewById(R.id.genderGroup);
        saveProfileBtn = findViewById(R.id.buttonSave);

        saveProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserProfile();
            }
        });
    }

    private void saveUserProfile() {
        String nameStr = fullName.getText().toString().trim();
        String ageStr = age.getText().toString().trim();
        String weightStr = weight.getText().toString().trim();
        String heightStr = heightSpinner.getSelectedItem().toString();
        String goalStr = goalSpinner.getSelectedItem().toString();

        if (genderGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please select your gender", Toast.LENGTH_SHORT).show();
            return;
        }

        selectedGender = findViewById(genderGroup.getCheckedRadioButtonId());
        String genderStr = selectedGender.getText().toString();

        // Check for empty fields
        if (nameStr.isEmpty() || ageStr.isEmpty() || weightStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate numeric fields
        int ageInt;
        float weightFloat;
        try {
            ageInt = Integer.parseInt(ageStr);
            weightFloat = Float.parseFloat(weightStr);

            if (ageInt <= 0 || ageInt > 120 || weightFloat <= 0) {
                Toast.makeText(this, "Enter valid age and weight", Toast.LENGTH_SHORT).show();
                return;
            }

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Age and weight must be numeric", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get authenticated email from FirebaseAuth
        String emailStr = auth.getCurrentUser().getEmail();
        String uid = auth.getCurrentUser().getUid();

        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("fullName", nameStr);
        userMap.put("email", emailStr);
        userMap.put("age", ageInt);
        userMap.put("weight", weightFloat);
        userMap.put("height", heightStr);
        userMap.put("goal", goalStr);
        userMap.put("gender", genderStr);
        userMap.put("createdAt", FieldValue.serverTimestamp());

        db.collection("users").document(uid)
                .set(userMap)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Profile saved successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UserGoalSetupActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to save profile", Toast.LENGTH_SHORT).show();
                });
    }
}
