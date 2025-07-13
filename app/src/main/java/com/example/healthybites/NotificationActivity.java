package com.example.healthybites;

import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthybites.adapters.NotificationAdapter;
import com.example.healthybites.models.NotificationModel;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private NotificationAdapter adapter;
    private RecyclerView notificationRecycler;
    private List<NotificationModel> notificationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        // Bind Views
        notificationRecycler = findViewById(R.id.notificationRecycler);
        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(v -> finish());

        // RecyclerView Setup
        notificationRecycler.setLayoutManager(new LinearLayoutManager(this));

        // Load Dummy Data
        notificationList = new ArrayList<>();
        loadDummyNotifications();

        // Set Adapter
        adapter = new NotificationAdapter(notificationList);
        notificationRecycler.setAdapter(adapter);

        // Animation
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(
                this, R.anim.layout_slide_from_bottom);
        notificationRecycler.setLayoutAnimation(animation);
    }

    private void loadDummyNotifications() {
        notificationList.add(new NotificationModel("Drink Water", "Stay hydrated! Drink a glass now.", "Reminder", "2 mins ago"));
        notificationList.add(new NotificationModel("Workout Completed", "Great job finishing your workout today!", "Achievement", "30 mins ago"));
        notificationList.add(new NotificationModel("Healthy Tip", "Try adding lemon to your water for extra benefits!", "Tip", "1 hour ago"));
    }
}
