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

import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    EditText firstName, lastName, phone, email, password, confirmPassword;
    CheckBox checkbox;
    Button signUpButton;
    TextView createAccountLink;

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

            // ✅ All validations passed → Proceed with signup
            Toast.makeText(this, "Sign Up Successful!", Toast.LENGTH_SHORT).show();
            // ✅ Redirect to HomeActivity
            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // ✅ Already have account link (Optional)
        createAccountLink.setOnClickListener(v -> {
            Toast.makeText(this, "Redirecting to Login...", Toast.LENGTH_SHORT).show();
             startActivity(new Intent(this, SignInActivity.class));
             finish();
        });
    }
}
