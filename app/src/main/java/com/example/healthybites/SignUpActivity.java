package com.example.healthybites;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;


import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    EditText firstName, lastName, phone, email, password, confirmPassword;
    CheckBox checkbox;
    Button signUpButton;
    TextView createAccountLink;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;


    boolean isPasswordVisible = false;
    boolean isConfirmPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize views
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        checkbox = findViewById(R.id.checkbox);
        signUpButton = findViewById(R.id.signUpButton);
        createAccountLink = findViewById(R.id.createAccountLink);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        // ✅ Password Show/Hide Toggle
        password.setOnTouchListener((v, event) -> {
            final int DRAWABLE_END = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (password.getRight() - password.getCompoundDrawables()[DRAWABLE_END].getBounds().width())) {
                    if (isPasswordVisible) {
                        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        isPasswordVisible = false;
                    } else {
                        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        isPasswordVisible = true;
                    }
                    password.setSelection(password.getText().length());
                    return true;
                }
            }
            return false;
        });

        confirmPassword.setOnTouchListener((v, event) -> {
            final int DRAWABLE_END = 2;
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (confirmPassword.getRight() - confirmPassword.getCompoundDrawables()[DRAWABLE_END].getBounds().width())) {
                    if (isConfirmPasswordVisible) {
                        confirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        isConfirmPasswordVisible = false;
                    } else {
                        confirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        isConfirmPasswordVisible = true;
                    }
                    confirmPassword.setSelection(confirmPassword.getText().length());
                    return true;
                }
            }
            return false;
        });

        // ✅ Sign Up Button Validation
        signUpButton.setOnClickListener(v -> {
            String first = firstName.getText().toString().trim();
            String last = lastName.getText().toString().trim();
            String phoneNo = phone.getText().toString().trim();
            String userEmail = email.getText().toString().trim();
            String pass = password.getText().toString();
            String confirmPass = confirmPassword.getText().toString();

            if (first.isEmpty()) {
                firstName.setError("First Name is required");
                firstName.requestFocus();
                return;
            } else if (first.length() < 2) {
                firstName.setError("Enter at least 2 characters");
                firstName.requestFocus();
                return;
            }

            if (last.isEmpty()) {
                lastName.setError("Last Name is required");
                lastName.requestFocus();
                return;
            } else if (last.length() < 2) {
                lastName.setError("Enter at least 2 characters");
                lastName.requestFocus();
                return;
            }

            if (userEmail.isEmpty()) {
                email.setError("Email is required");
                email.requestFocus();
                return;
            }

            if (pass.isEmpty()) {
                password.setError("Password is required");
                password.requestFocus();
                return;
            }

            if (!pass.equals(confirmPass)) {
                confirmPassword.setError("Passwords do not match");
                confirmPassword.requestFocus();
                return;
            }

            if (!checkbox.isChecked()) {
                Toast.makeText(this, "Please accept Terms & Conditions", Toast.LENGTH_SHORT).show();
                return;
            }

            // ✅ Firebase create user
            mAuth.createUserWithEmailAndPassword(userEmail, pass)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                // ✅ Save user details to Firestore
                                Map<String, Object> userMap = new HashMap<>();
                                userMap.put("fullName", first + " " + last);
                                userMap.put("email", user.getEmail());
                                userMap.put("phone", phoneNo);
                                // Add empty/default values so MyDetailsActivity can update them later
                                userMap.put("age", "");
                                userMap.put("height", "");
                                userMap.put("weight", "");
                                userMap.put("gender", "");
                                userMap.put("goal", "");

                                db.collection("users").document(user.getUid())
                                        .set(userMap)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(SignUpActivity.this, "Sign Up Successful!", Toast.LENGTH_SHORT).show();
                                            // Redirect to Sign In
                                            Intent intent = new Intent(SignUpActivity.this, UserGoalSetupActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                        })
                                        .addOnFailureListener(e ->
                                                Toast.makeText(SignUpActivity.this, "Failed to save user data: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                        );
                            }
                        } else {
                            if (task.getException() != null && task.getException().getMessage() != null &&
                                    task.getException().getMessage().contains("The email address is already in use")) {

                                Toast.makeText(SignUpActivity.this, "Email already registered. Redirecting to Sign In...", Toast.LENGTH_SHORT).show();

                                // Optional: delay slightly before redirecting
                                new android.os.Handler().postDelayed(() -> {
                                    Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                                    intent.putExtra("email", userEmail); // pass email to prefill
                                    startActivity(intent);
                                    finish();
                                }, 1500);

                            } else {
                                Toast.makeText(SignUpActivity.this, "Sign Up Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });

        // ✅ Already have account link (Optional)
        createAccountLink.setOnClickListener(v -> {
            Toast.makeText(this, "Redirecting to Login...", Toast.LENGTH_SHORT).show();
             startActivity(new Intent(this, SignInActivity.class));
             finish();
        });
    }
}
