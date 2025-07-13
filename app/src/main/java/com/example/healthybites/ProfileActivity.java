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

        ImageView imgNotification = findViewById(R.id.imgNotification);
        TextView txtNotification = findViewById(R.id.txtNotification);
        ImageView arrowNotification = findViewById(R.id.arrowNotification);

        View.OnClickListener notificationClickListener = v -> {
            Intent intent = new Intent(ProfileActivity.this, NotificationActivity.class);
            startActivity(intent);
        };

        imgNotification.setOnClickListener(notificationClickListener);
        txtNotification.setOnClickListener(notificationClickListener);
        arrowNotification.setOnClickListener(notificationClickListener);

        // 🟣 Load user data from Firebase
        loadUserData();

        // 🔙 Back Button Click → Finish
        backBtn.setOnClickListener(v -> finish());

        // 🟢 Open MyDetailsActivity on any click
        View.OnClickListener openMyDetails = v -> {
            startActivity(new Intent(ProfileActivity.this, MyDetailsActivity.class));
        };

        imgMyDetail.setOnClickListener(openMyDetails);
        txtMyDetail.setOnClickListener(openMyDetails);
        arrowMyDetail.setOnClickListener(openMyDetails);
    }

    private void loadUserData() {
        if (mAuth.getCurrentUser() == null) {
            nameText.setText("Guest User");
            emailText.setText("guest@example.com");
            return;
        }

        String uid = mAuth.getCurrentUser().getUid();
        DocumentReference userRef = db.collection("users").document(uid);

        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String name = documentSnapshot.getString("fullName");
                String email = mAuth.getCurrentUser().getEmail();

                nameText.setText(name != null ? name : "Healthy Bites User");
                emailText.setText(email != null ? email : "No email");
            } else {
                Log.e("ProfileActivity", "No such user document.");
            }
        }).addOnFailureListener(e -> Log.e("ProfileActivity", "Failed to fetch user data", e));
    }
}
