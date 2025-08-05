package com.example.healthybites;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private ImageView backBtn, imgMyDetail, arrowMyDetail;
    private TextView nameText, emailText, txtMyDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Bind Views
        backBtn = findViewById(R.id.backBtn);
        nameText = findViewById(R.id.nameText);
        emailText = findViewById(R.id.emailText);
        imgMyDetail = findViewById(R.id.imgmyDetail);
        txtMyDetail = findViewById(R.id.txtmyDetail);
        arrowMyDetail = findViewById(R.id.arrowmyDetail);

        ImageView imgReminder = findViewById(R.id.imgRemindeder);
        TextView txtReminder = findViewById(R.id.txtRemindeder);
        ImageView arrowReminder = findViewById(R.id.arrowRemindeder);

        ImageView imgLogout = findViewById(R.id.imgLogout);
        TextView txtLogout = findViewById(R.id.txtLogout);
        ImageView arrowLogout = findViewById(R.id.arrowLogout);

        View.OnClickListener logoutClickListener = view -> {
            new androidx.appcompat.app.AlertDialog.Builder(ProfileActivity.this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(ProfileActivity.this, SignInActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("No", null)
                    .show();
        };

        imgLogout.setOnClickListener(logoutClickListener);
        txtLogout.setOnClickListener(logoutClickListener);
        arrowLogout.setOnClickListener(logoutClickListener);

        View.OnClickListener notificationClickListener = v -> {
            Intent intent = new Intent(ProfileActivity.this, ReminderActivity.class);
            startActivity(intent);
        };

        imgReminder.setOnClickListener(notificationClickListener);
        txtReminder.setOnClickListener(notificationClickListener);
        arrowReminder.setOnClickListener(notificationClickListener);

        // Load user data from Firebase
        loadUserData();

        // Back Button Click → Finish
        backBtn.setOnClickListener(v -> finish());

        // Open MyDetailsActivity on any click
        View.OnClickListener openMyDetails = v -> {
            startActivity(new Intent(ProfileActivity.this, MyDetailsActivity.class));
        };

        imgMyDetail.setOnClickListener(openMyDetails);
        txtMyDetail.setOnClickListener(openMyDetails);
        arrowMyDetail.setOnClickListener(openMyDetails);

        ImageView imgAboutUs = findViewById(R.id.imgAboutUs);
        TextView txtAboutUs = findViewById(R.id.txtAboutUs);
        ImageView arrowAboutUs = findViewById(R.id.arrowAboutUs);

        View.OnClickListener aboutUsClickListener = v -> {
            Intent intent = new Intent(ProfileActivity.this, AboutUsActivity.class);
            startActivity(intent);
        };

        imgAboutUs.setOnClickListener(aboutUsClickListener);
        txtAboutUs.setOnClickListener(aboutUsClickListener);
        arrowAboutUs.setOnClickListener(aboutUsClickListener);

    }

    private void loadUserData() {
        if (mAuth.getCurrentUser() == null) {
            // Not signed in at all
            nameText.setText("Guest User");
            emailText.setText("guest@example.com");
            return;
        }

        if (mAuth.getCurrentUser().isAnonymous()) {
            // Anonymous user - no Firestore doc or email
            nameText.setText("Guest User");
            emailText.setText("guest@example.com");
            return;
        }

        // Authenticated user with Firestore data
        String uid = mAuth.getCurrentUser().getUid();
        DocumentReference userRef = db.collection("users").document(uid);

        userRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("fullName");
                        String email = mAuth.getCurrentUser().getEmail();

                        nameText.setText(name != null ? name : "Healthy Bites User");
                        emailText.setText(email != null ? email : "No email");
                    } else {
                        nameText.setText("Healthy Bites User");
                        emailText.setText("No email found");
                        Log.e("ProfileActivity", "No such user document.");
                    }
                })
                .addOnFailureListener(e -> {
                    nameText.setText("Error loading name");
                    emailText.setText("Error loading email");
                    Log.e("ProfileActivity", "Failed to fetch user data", e);
                });
    }
}
