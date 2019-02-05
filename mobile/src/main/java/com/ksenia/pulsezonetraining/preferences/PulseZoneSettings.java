package com.ksenia.pulsezonetraining.preferences;

import android.content.Context;
import android.content.SharedPreferences;


import com.ksenia.pulsezonetraining.R;
import com.ksenia.pulsezonetraining.utils.PulseZoneUtils;

import java.util.Arrays;
import java.util.stream.Stream;

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

    public void read(Context context)  {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PARAM_APP_PREFERENCES, Context.MODE_PRIVATE);
        this.genderRadioButtonId = Preferences.USER_SEX_KEY.getValue(sharedPreferences);
        this.age = Preferences.AGE_KEY.getValue(sharedPreferences);
        this.restHr = Preferences.REST_HR_KEY.getValue(sharedPreferences);
        this.maxHr = Preferences.MAX_HR_KEY.getValue(sharedPreferences);
        this.zoneRadioButtonId = Preferences.PULSE_ZONE_KEY.getValue(sharedPreferences);
        this.weight = Preferences.WEIGHT_KEY.getValue(sharedPreferences);
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
    public void save(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PARAM_APP_PREFERENCES, Context.MODE_PRIVATE);
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

    public void validate(Context context) throws InvalidPreferenceException {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PARAM_APP_PREFERENCES, Context.MODE_PRIVATE);
        for(Preferences value: Preferences.values()) {
            value.validate(sharedPreferences);
        }
    }

    enum Preferences {
        USER_SEX_KEY ("userSexKey") {
            @Override
            public int getValue(SharedPreferences prefs)  {
                this.value = prefs.getInt(Preferences.USER_SEX_KEY.toString(), R.id.radioButton_Female);
                return super.getValue(prefs);
            }
            @Override
            public void validate(SharedPreferences prefs) throws InvalidPreferenceException{
                int gender = getValue(prefs);
                if ((gender != R.id.radioButton_Female) && (gender != R.id.radioButton_Male)) {
                    super.validate(prefs);
                }
            }
        },
        AGE_KEY("ageKey") {
            @Override
            public int getValue(SharedPreferences prefs)  {
                this.value = prefs.getInt(Preferences.AGE_KEY.toString(), 0);
                return super.getValue(prefs);
            }
            @Override
            public void validate(SharedPreferences prefs) throws InvalidPreferenceException{
                int age = getValue(prefs);
                if (age == 0) {
                    super.validate(prefs);
                }
            }
        },
        REST_HR_KEY("restHrKey") {
            @Override
            public int getValue(SharedPreferences prefs)  {
                this.value = prefs.getInt(Preferences.REST_HR_KEY.toString(), 0);
                return super.getValue(prefs);
            }
            @Override
            public void validate(SharedPreferences prefs) throws InvalidPreferenceException{
                int restHR = getValue(prefs);
                if (restHR == 0) {
                    super.validate(prefs);
                }
            }
        },
        MAX_HR_KEY("maxHrKey") {
            @Override
            public int getValue(SharedPreferences prefs)  {
                this.value = prefs.getInt(Preferences.MAX_HR_KEY.toString(), 0);
                return super.getValue(prefs);
            }
            @Override
            public void validate(SharedPreferences prefs) throws InvalidPreferenceException{
                int maxHr = getValue(prefs);
                if (maxHr == 0) {
                    super.validate(prefs);
                }
            }
        },
        PULSE_ZONE_KEY("pulseZoneKey") {
            @Override
            public int getValue(SharedPreferences prefs)  {
                this.value = prefs.getInt(Preferences.PULSE_ZONE_KEY.toString(), R.id.radioButton_Zone1);
                return super.getValue(prefs);
            }
            @Override
            public void validate(SharedPreferences prefs) throws InvalidPreferenceException{
                //Don't need to validate, always has default value
            }
        },
        WEIGHT_KEY("weightKey") {
            @Override
            public int getValue(SharedPreferences prefs)  {
                this.value = prefs.getInt(Preferences.WEIGHT_KEY.toString(), 0);
                return super.getValue(prefs);
            }
            @Override
            public void validate(SharedPreferences prefs) throws InvalidPreferenceException{
                int weight = getValue(prefs);
                if (weight == 0) {
                    super.validate(prefs);
                }
            }
        };

        private String prefName;
        protected int value;

        Preferences(String prefName) {
            this.prefName = prefName;
        }

        @Override
        public String toString() {
            return prefName;
        }

        public void validate(SharedPreferences prefs) throws InvalidPreferenceException {
            throw new InvalidPreferenceException(this.prefName + " is not valid");
        }
        public int getValue(SharedPreferences prefs) {
            return value;
        }
        /*public static int getAge(SharedPreferences prefs) {
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
        }*/
    }
}
