package com.ksenia.pulsezonetraining;

import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.widget.Chronometer;

/**
 * Created by ksenia on 06.01.19.
 */

public class CustomChronometer extends Chronometer {
    private boolean isRunning = false;
    private int totalElapsedTime;
    private long lastStopTime = 0;
    private long startTime;

    /**
     * Initialization of CustomChronometer
     * @param context - context
     */
    public CustomChronometer(Context context) {
        super(context);

    }

    /**
     * Initialization of CustomChronometer
     * @param context - context
     * @param attrs - attributes
     */
    public CustomChronometer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Initialization of CustomChronometer
     * @param context - context
     * @param attrs - attributes
     * @param defStyle - style
     */
    public CustomChronometer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Starts chronometer from 00:00 or restart after pause from the last stop time
     */
    @Override
    public void start() {
        isRunning = true;
        super.start();
        //on start
        if (lastStopTime == 0) {
            startTime = SystemClock.elapsedRealtime();
            setBase(startTime);
            // on resume after pause
        } else {
            long intervalOnPause = (SystemClock.elapsedRealtime() - lastStopTime);
            setBase(getBase() + intervalOnPause);
        }
    }

    /**
     * Stops chronometer and memories the stop time
     */
    @Override
    public void stop() {
        isRunning = false;
        super.stop();
        lastStopTime = SystemClock.elapsedRealtime();
    }

    /**
     * Returns total elapsed time of the current workout
     * @return total elapsed time in seconds
     */
    public int getElapsedTime() {
        totalElapsedTime = (int)(SystemClock.elapsedRealtime() - getBase());
        return totalElapsedTime/1000;
    }

    public long getLastStopTime() {
        return lastStopTime;
    }

    public long getStartTime() {
        return startTime;
    }

    /**
     * Returns if chronometer is running or not
     * @return - true, if running and false, if not
     */
    public boolean isRunning() {
        return isRunning;
    }
}
