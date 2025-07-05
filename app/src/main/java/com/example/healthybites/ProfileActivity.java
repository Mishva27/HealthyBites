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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private EditText etFullName, etAge, etHeight, etWeight;
    private TextView tvEmail;
    private Spinner spinnerGoal;
    private RadioGroup radioGroupGender;
    private RadioButton rbMale, rbFemale;
    private Button btnSave;

    private FirebaseFirestore db;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        // Bind Views
        etFullName = findViewById(R.id.etFullName);
        etAge = findViewById(R.id.etAge);
        etHeight = findViewById(R.id.etHeight);
        etWeight = findViewById(R.id.etWeight);
        tvEmail = findViewById(R.id.tvEmail);
        spinnerGoal = findViewById(R.id.spinnerGoal);
        radioGroupGender = findViewById(R.id.radioGroupGender);
        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);
        btnSave = findViewById(R.id.btnSave);

        // Set Spinner Data
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.user_goals, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGoal.setAdapter(adapter);

        // Load User Data
        loadUserData();

        // Save Changes
        btnSave.setOnClickListener(v -> saveUserData());
    }

    private void loadUserData() {
        if (user != null) {
            db.collection("users").document(user.getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            etFullName.setText(documentSnapshot.getString("fullName"));
                            tvEmail.setText(documentSnapshot.getString("email"));
                            etAge.setText(documentSnapshot.getString("age"));
                            etHeight.setText(documentSnapshot.getString("height"));
                            etWeight.setText(documentSnapshot.getString("weight"));

                            // Gender
                            String gender = documentSnapshot.getString("gender");
                            if ("Male".equals(gender)) rbMale.setChecked(true);
                            else if ("Female".equals(gender)) rbFemale.setChecked(true);

                            // Goal
                            String goal = documentSnapshot.getString("goal");
                            if (goal != null) {
                                ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinnerGoal.getAdapter();
                                int position = adapter.getPosition(goal);
                                spinnerGoal.setSelection(position);
                            }
                        }
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(ProfileActivity.this, "Failed to load data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void saveUserData() {
        String name = etFullName.getText().toString();
        String age = etAge.getText().toString();
        String height = etHeight.getText().toString();
        String weight = etWeight.getText().toString();
        String gender = rbMale.isChecked() ? "Male" : "Female";
        String goal = spinnerGoal.getSelectedItem().toString();

        if (name.isEmpty() || age.isEmpty() || height.isEmpty() || weight.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("fullName", name);
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
