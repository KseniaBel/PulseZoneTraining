package com.ksenia.pulsezonetraining.utils;

import com.ksenia.pulsezonetraining.R;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by ksenia on 09.01.19.
 */

public class PulseZoneUtils {
    /**
     * Calculates the low and high limit of particular pulse zone
     */
    public static PulseLimits calculateZonePulse(PulseZoneSettings settings) {
        int hrReserve = settings.getMaxHr() - settings.getRestHr();
        int lowPulseLimit = 0, highPulseLimit = 0;
        switch(settings.getZoneRadioButtonId()) {
            case R.id.radioButton_Zone1:
                lowPulseLimit = (int)Math.round(settings.getRestHr() + hrReserve*0.4);
                highPulseLimit = (int)Math.round(settings.getRestHr() + hrReserve*0.51);
                break;
            case R.id.radioButton_Zone2:
                lowPulseLimit = (int)Math.round(settings.getRestHr()+ hrReserve*0.52);
                highPulseLimit = (int)Math.round(settings.getRestHr() + hrReserve*0.63);
                break;
            case R.id.radioButton_Zone3:
                lowPulseLimit = (int)Math.round(settings.getRestHr() + hrReserve*0.64);
                highPulseLimit = (int)Math.round(settings.getRestHr() + hrReserve*0.75);
                break;
            case R.id.radioButton_Zone4:
                lowPulseLimit = (int)Math.round(settings.getRestHr() + hrReserve*0.76);
                highPulseLimit = (int)Math.round(settings.getRestHr() + hrReserve*0.87);
                break;
            case R.id.radioButton_Zone5:
                lowPulseLimit = (int)Math.round(settings.getRestHr() + hrReserve*0.88);
                highPulseLimit = settings.getMaxHr();
                break;
        }
        return new PulseLimits(lowPulseLimit, highPulseLimit);
    }

    /**
     * Calculates maximum heart rate value based on age and gender
     * @param ageFieldValue - age value
     * @param isFemale - gender value
     * @return
     */
    public static int calculateMaxHrFieldValue(int ageFieldValue, boolean isFemale) {
        int maxHrFieldValue;
        if(isFemale) {
            maxHrFieldValue = (int) Math.round(209 - ageFieldValue * 0.7);
        } else {
            maxHrFieldValue = (int) Math.round(214 - ageFieldValue * 0.8);
        }
        return maxHrFieldValue;
    }

    /**
     * Calculates upper limit for axis maximum
     * @param highPulseLimit - maximum pulse
     * @return
     */
    public static float calculateUpperRangeLimit(int highPulseLimit) {
        return highPulseLimit + highPulseLimit*0.1f;
    }

    /**
     * Calculates lower limit for axis minimum
     * @param lowPulseLimit - rest pulse
     * @return
     */
    public static float calculateLowerRangeLimit(int lowPulseLimit) {
        return lowPulseLimit - lowPulseLimit*0.1f;
    }

    /**
     * Converts time from milliseconds to String in format hh:mm:ss
     * @param miliSeconds - time in milliseconds
     * @return
     */
    public static String fromMillisecondsToTime(long miliSeconds) {
        int hrs = (int) TimeUnit.MILLISECONDS.toHours(miliSeconds) % 24;
        int min = (int) TimeUnit.MILLISECONDS.toMinutes(miliSeconds) % 60;
        int sec = (int) TimeUnit.MILLISECONDS.toSeconds(miliSeconds) % 60;
        return String.format("%02d:%02d:%02d", hrs, min, sec);
    }

    /**
     * Calculates the tottal calories burned during workout
     * @param weight - the weight of the user
     * @param workoutTime - the total workout time
     * @param heartRateAverage - the average heart rate during workout
     * @return - total calories burned
     */
    public static int calculateTotalCalories(int weight, long workoutTime, int heartRateAverage) {
        int minutes = (int) TimeUnit.MILLISECONDS.toMinutes(workoutTime) % 60;
        return (int) Math.round(0.014*weight*minutes*(0.12*heartRateAverage - 7));
    }

    public static String getDate() {
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayNr = calendar.get(Calendar.DAY_OF_WEEK);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        String dayOfWeek = "";
        switch (dayNr){
            case Calendar.MONDAY:
                dayOfWeek = "MON";
                break;
            case Calendar.TUESDAY:
                dayOfWeek = "TUES";
                break;
            case Calendar.WEDNESDAY:
                dayOfWeek = "WED";
                break;
            case Calendar.THURSDAY:
                dayOfWeek = "THURS";
                break;
            case Calendar.FRIDAY:
                dayOfWeek = "FRI";
                break;
            case Calendar.SATURDAY:
                dayOfWeek = "SAT";
                break;
            case Calendar.SUNDAY:
                dayOfWeek = "SUN";
                break;
        }

        return String.format("%s %d", dayOfWeek, dayOfMonth);
    }
}
