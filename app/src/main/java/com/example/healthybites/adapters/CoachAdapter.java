package com.example.healthybites.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.healthybites.R;
import com.example.healthybites.models.CoachModel;

import java.util.List;

public class CoachAdapter extends RecyclerView.Adapter<CoachAdapter.CoachViewHolder> {
    private final Context context;
    private final List<CoachModel> coachList;

    public CoachAdapter(Context context, List<CoachModel> coachList) {
        this.context = context;
        this.coachList = coachList;
    }

    @NonNull
    @Override
    public CoachViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_coach, parent, false);
        return new CoachViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CoachViewHolder holder, int position) {
        CoachModel coach = coachList.get(position);

        if (coach != null) {
            holder.tvCoachName.setText(coach.getName() != null ? coach.getName() : "N/A");
            holder.tvCoachSpecialization.setText(coach.getSpecialization() != null ? coach.getSpecialization() : "N/A");
            holder.tvCoachExperience.setText(coach.getExperience() != null ? String.format("%s experience", coach.getExperience()) : "N/A");
            holder.tvCoachBio.setText(coach.getBio() != null ? coach.getBio() : "N/A");
            holder.tvCoachRating.setText(String.format("⭐ %.1f", coach.getRating()));

            // Use Glide to load the image from the URL into the ImageView.
            if (coach.getImageUrl() != null && !coach.getImageUrl().isEmpty()) {
                Glide.with(context)
                        .load(coach.getImageUrl())
                        .placeholder(R.drawable.ic_placeholder) // optional
                        // optional, displays if image fails to load
                        .into(holder.imageCoach);
            } else {
                holder.imageCoach.setImageResource(R.drawable.ic_placeholder);
            }


            holder.btnCoachContact.setOnClickListener(v -> {
                if (coach.getContact() != null && !coach.getContact().isEmpty()) {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:" + coach.getContact()));
                    try {
                        context.startActivity(intent);
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(context, "No email app installed.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Contact information not available.", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return coachList != null ? coachList.size() : 0;
    }

    public static class CoachViewHolder extends RecyclerView.ViewHolder {
        ImageView imageCoach;
        TextView tvCoachName, tvCoachSpecialization, tvCoachExperience, tvCoachBio, tvCoachRating;
        Button btnCoachContact;

        public CoachViewHolder(@NonNull View itemView) {
            super(itemView);
            imageCoach = itemView.findViewById(R.id.imageCoach);
            tvCoachName = itemView.findViewById(R.id.tvCoachName);
            tvCoachSpecialization = itemView.findViewById(R.id.tvCoachSpecialization);
            tvCoachExperience = itemView.findViewById(R.id.tvCoachExperience);
            tvCoachBio = itemView.findViewById(R.id.tvCoachBio);
            tvCoachRating = itemView.findViewById(R.id.tvCoachRating);
            btnCoachContact = itemView.findViewById(R.id.btnCoachContact);
        }
    }
}
