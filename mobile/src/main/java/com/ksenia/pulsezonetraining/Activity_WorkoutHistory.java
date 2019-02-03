package com.ksenia.pulsezonetraining;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ksenia.pulsezonetraining.utils.PulseZoneUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by ksenia on 31.01.19.
 */

public class Activity_WorkoutHistory extends AppCompatActivity {
    private RecyclerView workoutsRecyclerView;
    List<Workout> workoutList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_history);
        workoutsRecyclerView = findViewById(R.id.workouts_recycle_view);
        workoutsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        workoutsRecyclerView.setAdapter(new WorkoutsAdapter(workoutList));

        //Get info from the previous activity
        Intent intent = getIntent();
        long startTime = intent.getLongExtra(Activity_WorkoutStatistics.START_TIME, 0);
        long duration = intent.getLongExtra(Activity_WorkoutStatistics.WORKOUT_TIME, 0);
        int calories = intent.getIntExtra(Activity_WorkoutStatistics.CALORIES, 0);
        int avgHeartRate = intent.getIntExtra(Activity_WorkoutStatistics.AVG_HR, 0);

        //Get the date
        String date = PulseZoneUtils.getDate();

        //Create a new workout
        Workout workout = new Workout(date, PulseZoneUtils.fromMillisecondsToTime(startTime), calories,
                PulseZoneUtils.fromMillisecondsToTime(duration), avgHeartRate);
        workoutList = new ArrayList<>();
        workoutList.add(workout);
    }

    public void addRecord(Workout workout) {
        workoutList.add(workout);
    }


    class WorkoutsAdapter extends RecyclerView.Adapter<WorkoutsViewHolder> {
        private List<Workout> workoutList;

        public WorkoutsAdapter(List<Workout> list) {
            this.workoutList = list;
        }

        @Override
        public WorkoutsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new WorkoutsViewHolder(parent);
        }

        @Override
        public void onBindViewHolder(WorkoutsViewHolder holder, int position) {
            holder.bind(this.workoutList.get(position));
        }

        @Override
        public int getItemCount() {
            return this.workoutList.size();
        }


    }
    class WorkoutsViewHolder extends RecyclerView.ViewHolder {
        private TextView date;
        private TextView startTime;
        private TextView calories;
        private TextView duration;
        private TextView avgHR;

        public WorkoutsViewHolder(ViewGroup container) {
            super(LayoutInflater.from(Activity_WorkoutHistory.this).inflate(R.layout.history_list_item, container, false));
            date = itemView.findViewById(R.id.date_textView);
            startTime = itemView.findViewById(R.id.start_time_textView);
            calories = itemView.findViewById(R.id.calories_textView);
            duration = itemView.findViewById(R.id.duration_textView);
            avgHR = itemView.findViewById(R.id.av_hr_textView);
        }

            public void bind(Workout workout) {
                date.setText(workout.getDate());
                startTime.setText(workout.getStartTime());
                calories.setText(String.valueOf(workout.getTotalCalories()));
                duration.setText(workout.getDuration());
                avgHR.setText(String.valueOf(workout.getAvrHeartRate()));
            }
    }
}

