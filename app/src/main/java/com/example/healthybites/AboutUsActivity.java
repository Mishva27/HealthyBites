package com.example.healthybites;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class AboutUsActivity extends AppCompatActivity {

    ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        backBtn = findViewById(R.id.backBtn);

        if (backBtn != null) {
            backBtn.setOnClickListener(v -> {
//                Intent intent = new Intent(AboutUsActivity.this, ProfileActivity.class);
//                startActivity(intent);
                Log.d("DEBUG", "AboutUsActivity started");
                finish();
            });
        }

    }
}
