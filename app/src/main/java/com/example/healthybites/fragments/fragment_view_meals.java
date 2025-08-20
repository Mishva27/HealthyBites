package com.example.healthybites.fragments;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.healthybites.R;
import com.example.healthybites.adapters.MealHistoryAdapter;
import com.google.firebase.database.*;

import java.util.*;

public class fragment_view_meals extends Fragment {

    private ExpandableListView expandableListView;
    private DatabaseReference mealsRef;

    private List<String> dateList = new ArrayList<>();
    private Map<String, Map<String, List<String>>> mealsData = new HashMap<>();

    public fragment_view_meals() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_view_meals, container, false);
        expandableListView = root.findViewById(R.id.expandableListView);
        mealsRef = FirebaseDatabase.getInstance().getReference("meals");

        loadMealHistory();
        return root;
    }

    private void loadMealHistory() {
        mealsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dateList.clear();
                mealsData.clear();

                for (DataSnapshot dateSnap : snapshot.getChildren()) {
                    String date = dateSnap.getKey();
                    Map<String, List<String>> mealTypes = new HashMap<>();

                    for (DataSnapshot mealTypeSnap : dateSnap.getChildren()) {
                        String mealType = mealTypeSnap.getKey();
                        List<String> entries = new ArrayList<>();
                        for (DataSnapshot entrySnap : mealTypeSnap.getChildren()) {
                            String value = entrySnap.getValue(String.class);
                            if (value != null) entries.add(value);
                        }
                        mealTypes.put(mealType, entries);
                    }

                    dateList.add(date);
                    mealsData.put(date, mealTypes);
                }

                MealHistoryAdapter adapter = new MealHistoryAdapter(requireContext(), dateList, mealsData);
                expandableListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load history", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
