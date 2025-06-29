package com.example.healthybytes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.healthybytes.adapters.OnboardingAdapter;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    private OnboardingAdapter onboardingAdapter;
    private ViewPager2 onboardingViewPager;
    private LinearLayout layoutOnboardingIndicators;
    private Button buttonGetStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_one);

        layoutOnboardingIndicators = findViewById(R.id.layoutOnboardingIndicators);
        buttonGetStarted = findViewById(R.id.buttonGetStarted);

        setupOnboardingItems();

        onboardingViewPager = findViewById(R.id.onboardingViewPager);
        onboardingViewPager.setAdapter(onboardingAdapter);

        setupIndicators();
        setCurrentIndicator(0);

        onboardingViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentIndicator(position);

                // Show button only on last page
                if (position == onboardingAdapter.getItemCount() - 1) {
                    buttonGetStarted.setVisibility(View.VISIBLE);
                } else {
                    buttonGetStarted.setVisibility(View.GONE);
                }
            }
        });

        buttonGetStarted.setOnClickListener(v -> {
            // Navigate to Welcome screen
            Intent intent = new Intent(OnboardingActivity.this, WelcomeActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void setupOnboardingItems() {
        List<OnboardingItem> onboardingItems = new ArrayList<>();

        OnboardingItem item1 = new OnboardingItem();
        item1.setTitle("Track Your Meals Easily");
        item1.setDescription("Log every bite with ease and stay on top of your nutrition.\n" + "HealthyBites makes meal tracking fast, simple, and smart.");
        item1.setImage(R.drawable.track_your_meals);

        OnboardingItem item2 = new OnboardingItem();
        item2.setTitle("Get Personalized Health Tips");
        item2.setDescription("Receive daily tips, healthy alternatives, and portion advice — all tailored to your eating habits and goals.");
        item2.setImage(R.drawable.get_tips);

        OnboardingItem item3 = new OnboardingItem();
        item3.setTitle("Reach Your Wellness Goals");
        item3.setDescription("Whether it’s weight loss, balanced eating, or more energy — HealthyBites helps you stay consistent and");
        item3.setImage(R.drawable.reach_goal);

        onboardingItems.add(item1);
        onboardingItems.add(item2);
        onboardingItems.add(item3);

        onboardingAdapter = new OnboardingAdapter(onboardingItems);
    }

    private void setupIndicators() {
        int count = onboardingAdapter.getItemCount();
        ImageView[] indicators = new ImageView[count];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(8, 0, 8, 0);

        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(getDrawable(R.drawable.onboarding_indicator_inactive));
            indicators[i].setLayoutParams(layoutParams);
            layoutOnboardingIndicators.addView(indicators[i]);
        }
    }

    private void setCurrentIndicator(int index) {
        int count = layoutOnboardingIndicators.getChildCount();
        for (int i = 0; i < count; i++) {
            ImageView imageView = (ImageView) layoutOnboardingIndicators.getChildAt(i);
            if (i == index) {
                imageView.setImageDrawable(getDrawable(R.drawable.onboarding_indicator_active));
            } else {
                imageView.setImageDrawable(getDrawable(R.drawable.onboarding_indicator_inactive));
            }
        }
    }
}