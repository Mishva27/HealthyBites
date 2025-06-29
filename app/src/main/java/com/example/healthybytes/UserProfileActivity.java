package com.example.healthybytes;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class UserProfileActivity extends AppCompatActivity {

    EditText fullName, email, goal, age, gender, height, weight;
    Button saveButton;
    FirebaseFirestore db;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        fullName = findViewById(R.id.editTextFullName);
        email = findViewById(R.id.editTextEmail);
        goal = findViewById(R.id.editTextGoal);
        age = findViewById(R.id.editTextAge);
        gender = findViewById(R.id.editTextGender);
        height = findViewById(R.id.editTextHeight);
        weight = findViewById(R.id.editTextWeight);
        saveButton = findViewById(R.id.buttonSave);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        saveButton.setOnClickListener(v -> saveUserProfile());
    }

    private void saveUserProfile() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String uid = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

        if (uid == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String fullNameStr = fullName.getText().toString().trim();
        String emailStr = email.getText().toString().trim();
        String goalStr = goal.getText().toString().trim();
        String ageStr = age.getText().toString().trim();
        String genderStr = gender.getText().toString().trim();
        String heightStr = height.getText().toString().trim();
        String weightStr = weight.getText().toString().trim();

        if (fullNameStr.isEmpty() || emailStr.isEmpty() || goalStr.isEmpty() ||
                ageStr.isEmpty() || genderStr.isEmpty() || heightStr.isEmpty() || weightStr.isEmpty()) {
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
                    // Navigate to home screen or next activity
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
