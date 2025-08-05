package com.example.healthybites;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MyDetailsActivity extends AppCompatActivity {

    private EditText etAge, etWeight, etEmail;
    private TextView tvUserName;
    private Spinner spinnerGoal, spinnerHeight;
    private RadioGroup radioGroupGender;
    private RadioButton rbMale, rbFemale;
    private Button btnSave;

    private FirebaseFirestore db;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mydetails);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        // Bind Views
        etAge = findViewById(R.id.etAge);
        etWeight = findViewById(R.id.etWeight);
        etEmail = findViewById(R.id.etEmail);
        spinnerGoal = findViewById(R.id.spinnerGoal);
        spinnerHeight = findViewById(R.id.spinnerHeight); // Corrected this
        radioGroupGender = findViewById(R.id.radioGroupGender);
        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);
        btnSave = findViewById(R.id.btnSave);
        tvUserName = findViewById(R.id.tvUserName);  // bind the view

        // Set Spinner Data
        ArrayAdapter<CharSequence> goalAdapter = ArrayAdapter.createFromResource(
                this, R.array.user_goals, android.R.layout.simple_spinner_item);
        goalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGoal.setAdapter(goalAdapter);

        ArrayAdapter<CharSequence> heightAdapter = ArrayAdapter.createFromResource(
                this, R.array.user_heights, android.R.layout.simple_spinner_item);
        heightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHeight.setAdapter(heightAdapter);

        loadUserData();

        btnSave.setOnClickListener(v -> saveUserData());
    }

    private void loadUserData() {
        if (user == null || user.isAnonymous()) {
            // Guest user
            etEmail.setText("Guest User");
            tvUserName.setText("Guest");
            Toast.makeText(this, "Guest users can't update details.", Toast.LENGTH_SHORT).show();
            btnSave.setEnabled(false);
            return;
        }

        db.collection("users").document(user.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // ✅ Display full name and email
                        String fullName = documentSnapshot.getString("fullName");
                        String email = documentSnapshot.getString("email");

                        if (fullName != null) tvUserName.setText(fullName);
                        if (email != null) etEmail.setText(email);

                        // Age & Weight
                        etAge.setText(documentSnapshot.getString("age"));
                        etWeight.setText(documentSnapshot.getString("weight"));

                        // Gender
                        String gender = documentSnapshot.getString("gender");
                        if ("Male".equalsIgnoreCase(gender)) rbMale.setChecked(true);
                        else if ("Female".equalsIgnoreCase(gender)) rbFemale.setChecked(true);

                        // Goal
                        String goal = documentSnapshot.getString("goal");
                        if (goal != null) {
                            int pos = ((ArrayAdapter) spinnerGoal.getAdapter()).getPosition(goal);
                            spinnerGoal.setSelection(pos);
                        }

                        // Height
                        String height = documentSnapshot.getString("height");
                        if (height != null) {
                            int pos = ((ArrayAdapter) spinnerHeight.getAdapter()).getPosition(height);
                            spinnerHeight.setSelection(pos);
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Failed to load data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }


    private void saveUserData() {
        if (user == null || user.isAnonymous()) {
            Toast.makeText(this, "Guest users can't save data.", Toast.LENGTH_SHORT).show();
            return;
        }

        String age = etAge.getText().toString().trim();
        String height = spinnerHeight.getSelectedItem().toString();
        String weight = etWeight.getText().toString().trim();
        String gender = rbMale.isChecked() ? "Male" : "Female";
        String goal = spinnerGoal.getSelectedItem().toString();

        if (age.isEmpty() || height.isEmpty() || weight.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("age", age);
        updatedData.put("height", height);
        updatedData.put("weight", weight);
        updatedData.put("gender", gender);
        updatedData.put("goal", goal);

        db.collection("users").document(user.getUid())
                .update(updatedData)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
