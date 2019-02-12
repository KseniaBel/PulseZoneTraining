package com.ksenia.pulsezonetraining.history;

import android.content.Context;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.ksenia.pulsezonetraining.R;
import com.ksenia.pulsezonetraining.database.FitnessSQLiteDBHelper;
import com.ksenia.pulsezonetraining.database.StatisticRepository;
import com.ksenia.pulsezonetraining.utils.PulseZoneUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by ksenia on 11.02.19.
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> listDataHeader;
    private Map<String, List<WorkoutHistoryItem>> listHashMap;
    private StatisticRepository repository;

    public ExpandableListAdapter(Context context) {
        this.context = context;
        //Requests info from statistic table
        FitnessSQLiteDBHelper helper = new FitnessSQLiteDBHelper(context);
        repository = new StatisticRepository(helper);
        listHashMap = repository.getAllWorkoutsGroupedByDate();
        listDataHeader = new ArrayList<>(listHashMap.keySet());
        repository.closeDb();
    }

    @Override
    public int getGroupCount() {
        return listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listHashMap.get(listDataHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listHashMap.get(listDataHeader.get(groupPosition)).get(childPosition);
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
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerDate = (String) getGroup(groupPosition);
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.history_header, null);
        }
        TextView date = convertView.findViewById(R.id.header_date);
        date.setText(headerDate);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        WorkoutHistoryItem historyItem = (WorkoutHistoryItem) getChild(groupPosition, childPosition);
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.history_list_item, null);
        }
        TextView date = convertView.findViewById(R.id.date_textView);
        TextView zone = convertView.findViewById(R.id.zone_textView);
        TextView calories = convertView.findViewById(R.id.calories_textView);
        TextView duration = convertView.findViewById(R.id.duration_textView);
        String dateOfWorkout = PulseZoneUtils.getDate(historyItem.getCurrentTime());
        date.setText(dateOfWorkout);
        zone.setText(historyItem.getZone());
        calories.setText(String.valueOf(historyItem.getTotalCalories()));
        String timeOfWorkout = PulseZoneUtils.fromMillisecondsToTime(historyItem.getElapsedTime());
        duration.setText(timeOfWorkout);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
