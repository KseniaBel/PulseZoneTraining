package com.ksenia.pulsezonetraining;

/**
 * Created by ksenia on 01.02.19.
 */

public class Workout {
    private String date;
    private String startTime;
    private int totalCalories;
    private String duration;
    private int avrHeartRate;

    public Workout(String date, String startTime, int totalCalories, String duration, int avrHeartRate) {

        this.date = date;
        this.startTime = startTime;
        this.totalCalories = totalCalories;
        this.duration = duration;
        this.avrHeartRate = avrHeartRate;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public int getTotalCalories() {
        return totalCalories;
    }

    public void setTotalCalories(int totalCalories) {
        this.totalCalories = totalCalories;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getAvrHeartRate() {
        return avrHeartRate;
    }

    public void setAvrHeartRate(int avrHeartRate) {
        this.avrHeartRate = avrHeartRate;
    }
}
