package com.example.healthybytes.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthybytes.OnboardingItem;
import com.example.healthybytes.R;

import java.util.List;

public class OnboardingAdpter extends RecyclerView.Adapter<OnboardingAdpter.OnboardingVieHolder> {

    private List<OnboardingItem> onboardingItems;

    public OnboardingAdpter(List<OnboardingItem> onboardingItems) {
        this.onboardingItems = onboardingItems;
    }

    @NonNull
    @Override
    public OnboardingVieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OnboardingVieHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_container_onboarding,parent,false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull OnboardingVieHolder holder, int position) {
        holder.setOnboardingData(onboardingItems.get(position ));
    }

    @Override
    public int getItemCount() {
        return onboardingItems.size();
    }

    class OnboardingVieHolder extends RecyclerView.ViewHolder{

        private TextView textTitle;
        private TextView textDescription;
        private ImageView imageOnboarding;

        public OnboardingVieHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textDescription = itemView.findViewById(R.id.textDescription);
            imageOnboarding = itemView.findViewById(R.id.imageOnboarding);
        }
        void setOnboardingData(OnboardingItem onboardingItem){
            textTitle.setText(onboardingItem.getTitle());
            textDescription.setText(onboardingItem.getDescription());
            imageOnboarding.setImageResource(onboardingItem.getImage());
        }
    }
}
