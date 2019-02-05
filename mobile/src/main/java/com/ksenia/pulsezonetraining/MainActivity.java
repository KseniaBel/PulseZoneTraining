package com.ksenia.pulsezonetraining;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ksenia.pulsezonetraining.database.FitnessSQLiteDBHelper;
import com.ksenia.pulsezonetraining.database.HRRecordsRepository;
import com.ksenia.pulsezonetraining.database.StatisticRepository;
import com.ksenia.pulsezonetraining.preferences.InvalidPreferenceException;
import com.ksenia.pulsezonetraining.preferences.PulseZoneSettings;
import com.ksenia.pulsezonetraining.utils.PulseZoneUtils;

import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.List;

import static android.support.v7.widget.RecyclerView.HORIZONTAL;

public class MainActivity extends AppCompatActivity {
    private RecyclerView workoutsRecyclerView;
    List<WorkoutHistoryItem> workoutsList;
    StatisticRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ImageButton settingButton = findViewById(R.id.button_settings);

        settingButton.setOnClickListener(view -> {
                    Dialog_DataInput dialog = new Dialog_DataInput();
                    dialog.show(getSupportFragmentManager(), "Data input settings");
            }
        );

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            try {
                PulseZoneSettings pulseZoneSettings =  new PulseZoneSettings();
                pulseZoneSettings.validate(getApplicationContext());
                Dialog_ZoneInput dialog_zoneInput = new Dialog_ZoneInput();
                dialog_zoneInput.show(getSupportFragmentManager(), "Pulse zone settings");
            } catch (InvalidPreferenceException e) {
                Toast.makeText(getApplicationContext(), getString(R.string.input_settings_error), Toast.LENGTH_LONG).show();
                settingButton.callOnClick();
            }

            populateWorkoutHistory();
        });

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        populateWorkoutHistory();
    }

    private void populateWorkoutHistory() {
        //Requests info from statistic table
        FitnessSQLiteDBHelper helper = new FitnessSQLiteDBHelper(this);
        repository = new StatisticRepository(helper);
        workoutsList = repository.listAllWorkouts();

        //Creates recycler view and sets adapter
        workoutsRecyclerView = findViewById(R.id.workouts_recycle_view);
        workoutsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        workoutsRecyclerView.setAdapter(new MainActivity.WorkoutsAdapter(workoutsList));

        //Adds decoration and animation
        DividerItemDecoration itemDecor = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        workoutsRecyclerView.addItemDecoration(itemDecor);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(1000);
        itemAnimator.setRemoveDuration(1000);
        workoutsRecyclerView.setItemAnimator(itemAnimator);
    }

    class WorkoutsAdapter extends RecyclerView.Adapter<WorkoutsViewHolder> {
        private List<WorkoutHistoryItem> workoutHistoryItemList;

        public WorkoutsAdapter(List<WorkoutHistoryItem> list) {
            this.workoutHistoryItemList = list;
        }

        @Override
        public WorkoutsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new WorkoutsViewHolder(parent);
        }

        @Override
        public void onBindViewHolder(WorkoutsViewHolder holder, int position) {
            holder.bind(this.workoutHistoryItemList.get(position));
        }

        @Override
        public int getItemCount() {
            return this.workoutHistoryItemList.size();
        }
    }

    class WorkoutsViewHolder extends RecyclerView.ViewHolder {
        private TextView date;
        private TextView zone;
        private TextView calories;
        private TextView duration;

        public WorkoutsViewHolder(ViewGroup container) {
            super(LayoutInflater.from(MainActivity.this).inflate(R.layout.history_list_item, container, false));
            date = itemView.findViewById(R.id.date_textView);
            zone = itemView.findViewById(R.id.zone_textView);
            calories = itemView.findViewById(R.id.calories_textView);
            duration = itemView.findViewById(R.id.duration_textView);
        }

        public void bind(WorkoutHistoryItem workoutHistoryItem) {
            String dateOfWorkout = PulseZoneUtils.getDate(workoutHistoryItem.getCurrentTime());
            date.setText(dateOfWorkout);
            zone.setText(workoutHistoryItem.getZone());
            calories.setText(String.valueOf(workoutHistoryItem.getTotalCalories()));
            String timeOfWorkout = PulseZoneUtils.fromMillisecondsToTime(workoutHistoryItem.getElapsedTime());
            duration.setText(timeOfWorkout);
        }
    }
}
