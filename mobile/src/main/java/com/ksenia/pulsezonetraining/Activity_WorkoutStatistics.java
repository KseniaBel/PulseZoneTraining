package com.ksenia.pulsezonetraining;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.TextView;

import com.ksenia.pulsezonetraining.database.HRRecordsRepository;
import com.ksenia.pulsezonetraining.utils.PulseZoneUtils;

/**
 * Created by ksenia on 09.01.19.
 */

public class Activity_WorkoutStatistics extends Activity {
    public static final String START_TIME= "startTime";
    public static final String WORKOUT_TIME = "workoutTime";
    public static final String CALORIES = "calories";
    public static final String AVG_HR = "averHr";
    private TextView tv_workoutTime;
    private TextView tv_averageHr;
    private TextView tv_maxWorkoutHr;
    private TextView tv_totalCalories;
    private long startTime;
    private int averageHeartRate;
    private long elapsedTime;
    private int calories;

    private HRRecordsRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_statistic);

        tv_workoutTime = findViewById(R.id.textView_timeWorkout);
        tv_averageHr = findViewById(R.id.textView_averageHr);
        tv_maxWorkoutHr = findViewById(R.id.textView_maximumWorkoutHr);
        tv_totalCalories = findViewById(R.id.textView_calories);

        Intent intent = getIntent();
        startTime = intent.getLongExtra(Activity_PulseZonesFitness.START_TIMING, 0);
        String workoutTime = intent.getStringExtra(Activity_PulseZonesFitness.WORKOUT_TIME);
        int weight = intent.getIntExtra(Activity_PulseZonesFitness.WEIGHT, 0);


        repository = new HRRecordsRepository(this);
        averageHeartRate = repository.getAverageHeartRate(startTime, SystemClock.elapsedRealtime());
        elapsedTime = SystemClock.elapsedRealtime() - startTime;
        tv_maxWorkoutHr.setText(String.valueOf(repository.getMaxHeartRate(startTime, SystemClock.elapsedRealtime())) + "bpm");
        tv_averageHr.setText(String.valueOf(averageHeartRate) + "bpm");
        tv_workoutTime.setText(workoutTime);
        calories = PulseZoneUtils.calculateTotalCalories(weight, elapsedTime, averageHeartRate);
        tv_totalCalories.setText(String.valueOf(calories) + "kcal");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(START_TIME, startTime);
        intent.putExtra(WORKOUT_TIME, elapsedTime);
        intent.putExtra(CALORIES, calories);
        intent.putExtra(AVG_HR, averageHeartRate);
        startActivity(intent);
        this.finish();
    }
}
