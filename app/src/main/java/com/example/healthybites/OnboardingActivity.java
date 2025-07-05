package com.example.healthybites;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.healthybites.adapters.OnboardingAdapter;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    private OnboardingAdapter onboardingAdapter;
    private ViewPager2 onboardingViewPager;
    private LinearLayout layoutOnboardingIndicators;
    private Button buttonGetStarted;
    private TextView textSkip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        // Initialize views
        layoutOnboardingIndicators = findViewById(R.id.layoutOnboardingIndicators);
        buttonGetStarted = findViewById(R.id.buttonGetStarted);
        textSkip = findViewById(R.id.textSkip);
        onboardingViewPager = findViewById(R.id.onboardingViewPager);

        setupOnboardingItems();

        onboardingViewPager.setAdapter(onboardingAdapter);
        setupIndicators();
        setCurrentIndicator(0);

        // Page change listener
        onboardingViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentIndicator(position);

                boolean isLastPage = position == onboardingAdapter.getItemCount() - 1;
                buttonGetStarted.setVisibility(isLastPage ? View.VISIBLE : View.GONE);
                textSkip.setVisibility(isLastPage ? View.GONE : View.VISIBLE);
            }
        });

        // Skip action
        textSkip.setOnClickListener(v -> {
            startActivity(new Intent(OnboardingActivity.this, WelcomeActivity.class));
            finish();
        });

        // Get started action
        buttonGetStarted.setOnClickListener(v -> {
            startActivity(new Intent(OnboardingActivity.this, WelcomeActivity.class));
            finish();
        });
    }

    private void setupOnboardingItems() {
        List<OnboardingItem> onboardingItems = new ArrayList<>();

        OnboardingItem item1 = new OnboardingItem();
        item1.setTitle("Track Your Meals Easily");
        item1.setDescription("Log every bite with ease and stay on top of your nutrition. HealthyBites makes meal tracking fast, simple, and smart.");
        item1.setImage(R.drawable.track_your_meals);

        OnboardingItem item2 = new OnboardingItem();
        item2.setTitle("Get Personalized Health Tips");
        item2.setDescription("Receive daily tips, healthy alternatives, and portion advice — all tailored to your eating habits and goals.");
        item2.setImage(R.drawable.get_tips);

        OnboardingItem item3 = new OnboardingItem();
        item3.setTitle("Reach Your Wellness Goals");
        item3.setDescription("Whether it’s weight loss, balanced eating, or more energy — HealthyBites helps you stay consistent and motivated.");
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
            indicators[i].setImageDrawable(
                    ContextCompat.getDrawable(
                            getApplicationContext(),
                            R.drawable.onboarding_indicator_inactive
                    )
            );
            indicators[i].setLayoutParams(layoutParams);
            layoutOnboardingIndicators.addView(indicators[i]);
        }
    }

    private void setCurrentIndicator(int index) {
        int count = layoutOnboardingIndicators.getChildCount();
        for (int i = 0; i < count; i++) {
            ImageView imageView = (ImageView) layoutOnboardingIndicators.getChildAt(i);
            int drawableId = (i == index)
                    ? R.drawable.onboarding_indicator_active
                    : R.drawable.onboarding_indicator_inactive;
            imageView.setImageDrawable(
                    ContextCompat.getDrawable(getApplicationContext(), drawableId)
            );
        }
    }
}
