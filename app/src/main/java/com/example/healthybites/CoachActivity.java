package com.example.healthybites;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthybites.adapters.CoachAdapter;
import com.example.healthybites.models.CoachModel;

// Import the necessary Firebase Auth and Realtime Database classes
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CoachActivity extends AppCompatActivity {

    private static final String TAG = "CoachActivity";

    private RecyclerView recyclerView;
    private CoachAdapter coachAdapter;
    private List<CoachModel> coachList;
    private TextView noCoachesTextView;

    private ImageView backBtn;
    private FirebaseAuth mAuth;
    private DatabaseReference coachesRef; // Use DatabaseReference for Realtime Database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coach);

        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(v -> finish());

        // Initialize Firebase Auth and Realtime Database
        mAuth = FirebaseAuth.getInstance();
        // Get a reference to the "coaches" node in your Realtime Database.
        coachesRef = FirebaseDatabase.getInstance().getReference("coaches");

        recyclerView = findViewById(R.id.recyclerCoach);
        noCoachesTextView = findViewById(R.id.noCoachesTextView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        coachList = new ArrayList<>();
        coachAdapter = new CoachAdapter(this, coachList);
        recyclerView.setAdapter(coachAdapter);

        // Check if a user is already signed in.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Log.d(TAG, "User already signed in. UID: " + currentUser.getUid());
            loadCoachesFromRealtimeDatabase();
        } else {
            Log.d(TAG, "No user signed in. Attempting anonymous sign-in.");
            // Assuming you have a working signInAnonymously() method
            // This is required because your security rules still require authentication.
            mAuth.signInAnonymously().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Anonymous sign-in successful. Loading data...");
                    loadCoachesFromRealtimeDatabase();
                } else {
                    Log.w(TAG, "Anonymous sign-in failed.", task.getException());
                    Toast.makeText(CoachActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    noCoachesTextView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            });
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> startActivity(new Intent(CoachActivity.this, MainActivity.class)));

        // Bottom Navigation Setup
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.nav_coach);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                Intent intent = new Intent(CoachActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_coach) {
                return true;
            } else if (itemId == R.id.mealPlanner) {
                Intent intent = new Intent(CoachActivity.this, MealSuggestion.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.nav_help) {
                Intent intent = new Intent(CoachActivity.this, HelpActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
            }

            return false;
        });
    }

    private void loadCoachesFromRealtimeDatabase() {
        Log.d(TAG, "Attempting to load coaches from Realtime Database...");
        coachesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Log.d(TAG, "Data loaded successfully! Number of coaches: " + snapshot.getChildrenCount());
                    coachList.clear();
                    for (DataSnapshot coachSnapshot : snapshot.getChildren()) {
                        // Use getValue() to map the data to the CoachModel class
                        CoachModel coach = coachSnapshot.getValue(CoachModel.class);
                        if (coach != null) {
                            Log.d(TAG, "Fetched coach: " + coach.getName());
                            coachList.add(coach);
                        }
                    }
                    coachAdapter.notifyDataSetChanged();
                    noCoachesTextView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    Log.w(TAG, "Snapshot does not exist. No coaches found in Realtime Database.");
                    noCoachesTextView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Realtime Database query failed.", error.toException());
                Toast.makeText(CoachActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                noCoachesTextView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        });
    }
}
