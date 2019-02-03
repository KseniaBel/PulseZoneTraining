package com.ksenia.pulsezonetraining.utils;

/**
 * Created by ksenia on 12.01.19.
 */

public class PulseLimits {
    private int lowPulseLimit;
    private int highPulseLimit;

    /**
     * Initialization of PulseLimits
     * @param lowPulseLimit - lowest pulse
     * @param highPulseLimit - highest pulse
     */
    public PulseLimits(int lowPulseLimit, int highPulseLimit) {
        this.highPulseLimit = highPulseLimit;
        this.lowPulseLimit = lowPulseLimit;
    }

    /**
     * Getter for low pulse limit
     * @return low pulse limit
     */
    public int getLowPulseLimit() {
        return lowPulseLimit;
    }

    /**
     * Getter for high pulse limit
     * @return - high pulse limit
     */
    public int getHighPulseLimit() {
        return highPulseLimit;
    }
}
