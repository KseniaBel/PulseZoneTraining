package com.ksenia.pulsezonetraining.history;


import com.ksenia.pulsezonetraining.R;

/**
 * Created by ksenia on 01.02.19.
 */

public class WorkoutHistoryItem {
    private long elapsedTime;
    private String zone;
    private int totalCalories;

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    private long currentTime;

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public int getTotalCalories() {
        return totalCalories;
    }

    public void setTotalCalories(int totalCalories) {
        this.totalCalories = totalCalories;
    }

}
