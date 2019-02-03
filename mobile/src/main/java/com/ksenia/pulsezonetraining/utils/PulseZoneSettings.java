package com.ksenia.pulsezonetraining.utils;

import android.content.Context;
import android.content.SharedPreferences;


import com.ksenia.pulsezonetraining.R;

import java.util.Arrays;

/**
 * Created by ksenia on 01.01.19.
 */

public class PulseZoneSettings {
    private static final String PARAM_APP_PREFERENCES = "prefID";
    private int genderRadioButtonId;
    private int age;
    private int restHr;
    private int maxHr;
    private int zoneRadioButtonId;
    private int weight;
    private SharedPreferences sharedPreferences;

    /**
     *Initialization of pulseZoneSettings
     * @param context - context
     */
    public PulseZoneSettings(Context context) {
        sharedPreferences = context.getSharedPreferences(PARAM_APP_PREFERENCES, Context.MODE_PRIVATE);
        this.genderRadioButtonId = Preferences.getUserSex(sharedPreferences);
        this.age = Preferences.getAge(sharedPreferences);
        this.restHr = Preferences.getRestHr(sharedPreferences);
        this.maxHr = Preferences.getMaxHr(sharedPreferences);
        this.zoneRadioButtonId = Preferences.getPulseZone(sharedPreferences);
        this.weight = Preferences.getWeight(sharedPreferences);
    }

    /**
     * Setts genderRadioButtonId
     * @param genderRadioButtonId - genderRadioButtonId int value
     */
    public void setGenderRadioButtonId(int genderRadioButtonId) {
        this.genderRadioButtonId = genderRadioButtonId;
    }

    /**
     * Setts age
     * @param age - age int value
     */
    public void setAge(int age) {
        this.age = age;
    }

    /**
     * Sets rest heart rate
     * @param restHr - rest heart rate int value
     */
    public void setRestHr(int restHr) {
        this.restHr = restHr;
    }

    /**
     * Sets maximum heart rate
     * @param maxHr - maximum heart rate int value
     */
    public void setMaxHr(int maxHr) {
        this.maxHr = maxHr;
    }

    /**
     * Sets heart rate zoneRadioButtonId
     * @param zoneRadioButtonId - heart rate zoneRadioButtonId int value
     */
    public void setZoneRadioButtonId(int zoneRadioButtonId) {
        this.zoneRadioButtonId = zoneRadioButtonId;
    }

    /**
     * Sets weight
     * @param weight
     */
    public void setWeight(int weight) {
        this.weight = weight;
    }

    /**
     * Returns if genderRadioButtonId is Female
     * @return - true, if genderRadioButtonId is female, false, if genderRadioButtonId is male
     */
    public boolean isFemale() {
        return genderRadioButtonId == R.id.radioButton_Female;
    }


    /**
     * Getter method for genderRadioButtonId
     * @return - genderRadioButtonId
     */
    public int getGenderRadioButtonId() {
        return genderRadioButtonId;
    }

    /**
     * Getter method for age
     * @return - age
     */
    public int getAge() {
        return age;
    }

    /**
     * Getter method for rest heart rate
     * @return - rest heart rate
     */
    public int getRestHr() {
        return restHr;
    }

    /**
     * Getter method for maximum heart rate
     * @return - maximum heart rate
     */
    public int getMaxHr() {
        return maxHr;
    }

    /**
     * Getter method for pulse zoneRadioButtonId
     * @return - pulse zoneRadioButtonId
     */
    public int getZoneRadioButtonId() {
        return zoneRadioButtonId;
    }

    /**
     * Getter method for weight
     * @return
     */
    public int getWeight() {
        return weight;
    }

    /**
     * Saves values of genderRadioButtonId, age. rest hr, maximum hr, pulse zoneRadioButtonId in shared preferences
     */
    public void save() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(Preferences.USER_SEX_KEY.toString(), genderRadioButtonId);
        editor.putInt(Preferences.AGE_KEY.toString(), age);
        editor.putInt(Preferences.REST_HR_KEY.toString(), restHr);
        this.maxHr = maxHr == 0 ? PulseZoneUtils.calculateMaxHrFieldValue(age, isFemale()) : maxHr;
        editor.putInt(Preferences.MAX_HR_KEY.toString(), maxHr);
        editor.putInt(Preferences.PULSE_ZONE_KEY.toString(), zoneRadioButtonId);
        editor.putInt(Preferences.WEIGHT_KEY.toString(), weight);
        editor.apply();
   }

    @Override
    public String toString(){
        return Arrays.asList(genderRadioButtonId, age, restHr, maxHr, zoneRadioButtonId).toString();
    }

    enum Preferences {
        USER_SEX_KEY("userSexKey"),
        AGE_KEY("ageKey"),
        REST_HR_KEY("restHrKey"),
        MAX_HR_KEY("maxHrKey"),
        PULSE_ZONE_KEY("pulseZoneKey"),
        WEIGHT_KEY("weightKey");

        private String prefName;

        Preferences(String prefName) {
            this.prefName = prefName;
        }

        @Override
        public String toString() {
            return prefName;
        }

        public static int getAge(SharedPreferences prefs) {
            return prefs.getInt(Preferences.AGE_KEY.toString(), 0);
        }

        public static int getUserSex(SharedPreferences prefs) {
            return prefs.getInt(Preferences.USER_SEX_KEY.toString(), R.id.radioButton_Female);
        }

        public static int getRestHr(SharedPreferences prefs) {
            return prefs.getInt(Preferences.REST_HR_KEY.toString(), 0);
        }

        public static int getMaxHr(SharedPreferences prefs) {
            return prefs.getInt(Preferences.MAX_HR_KEY.toString(), 0);
        }

        public static int getPulseZone(SharedPreferences prefs) {
            return prefs.getInt(Preferences.PULSE_ZONE_KEY.toString(), R.id.radioButton_Zone1);
        }

        public static int getWeight(SharedPreferences pref) {
            return pref.getInt(Preferences.WEIGHT_KEY.toString(), 0);
        }
    }
}
