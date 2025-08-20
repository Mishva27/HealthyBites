package com.example.healthybites.adapters;

import android.content.Context;
import android.view.*;
import android.widget.*;
import java.util.*;

public class MealHistoryAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> dateList; // Group headers
    private Map<String, Map<String, List<String>>> mealsData; // date → mealType → entries

    public MealHistoryAdapter(Context context, List<String> dateList, Map<String, Map<String, List<String>>> mealsData) {
        this.context = context;
        this.dateList = dateList;
        this.mealsData = mealsData;
    }

    @Override
    public int getGroupCount() {
        return dateList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        String date = dateList.get(groupPosition);
        return mealsData.get(date).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return dateList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        String date = dateList.get(groupPosition);
        return new ArrayList<>(mealsData.get(date).entrySet()).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() { return false; }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String date = (String) getGroup(groupPosition);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_expandable_list_item_1, parent, false);
        }
        ((TextView) convertView.findViewById(android.R.id.text1)).setText("📅 " + date);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        Map.Entry<String, List<String>> mealEntry =
                (Map.Entry<String, List<String>>) getChild(groupPosition, childPosition);
        String mealType = mealEntry.getKey();
        List<String> items = mealEntry.getValue();

        StringBuilder text = new StringBuilder("🍽 " + capitalize(mealType) + "\n");
        int total = 0;

        for (String item : items) {
            text.append("• ").append(item).append("\n");
            if (item.contains("=")) {
                try {
                    String calStr = item.split("=")[1].replace("kcal", "").trim();
                    total += Integer.parseInt(calStr);
                } catch (Exception ignored) {}
            }
        }
        text.append("🔥 Total: ").append(total).append(" kcal");

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_expandable_list_item_2, parent, false);
        }

        ((TextView) convertView.findViewById(android.R.id.text1)).setText(capitalize(mealType));
        ((TextView) convertView.findViewById(android.R.id.text2)).setText(text.toString());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    private String capitalize(String str) {
        return str.substring(0,1).toUpperCase() + str.substring(1);
    }
}
