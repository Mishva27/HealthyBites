package com.example.healthybytes;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;


public class SignUpActivity extends AppCompatActivity {

    EditText emailField, passwordField;
    Button signUpButton;
    TextView loginLink;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up); // Use your actual XML layout file

        // Firebase Auth instance
        mAuth = FirebaseAuth.getInstance();

        // View binding
        emailField = findViewById(R.id.email);      // Replace with your email field ID
        passwordField = findViewById(R.id.password); // Replace with your password field ID
        signUpButton = findViewById(R.id.signUpButton);      // Sign Up button ID
        loginLink = findViewById(R.id.createAccountLink);   // "Login" TextView ID

        signUpButton.setOnClickListener(view -> {
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(SignUpActivity.this, "Fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String userId = mAuth.getCurrentUser().getUid();
                            FirebaseFirestore db = FirebaseFirestore.getInstance();

                            Map<String, Object> user = new HashMap<>();
                            user.put("email", email);
                            user.put("uid", userId);

                            db.collection("Users").document(userId)
                                    .set(user)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(SignUpActivity.this, "User data saved", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(SignUpActivity.this, "Signup successful, but failed to save user data", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                                        finish();
                                    });
                        } else {
                            Toast.makeText(SignUpActivity.this, "Signup failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });


        // Link to Sign In
        loginLink.setOnClickListener(view -> {
            startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
            finish();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        }
    }

}
