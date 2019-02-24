package com.ksenia.pulsezonetraining.connectivity;

import android.app.Activity;
import android.content.Context;

import com.ksenia.pulsezonetraining.R;
import com.ksenia.pulsezonetraining.connectivity.bluetooth.BluetoothCommunicationManager;

/**
 * Created by ksenia on 17.02.19.
 */

public class ConnectivityManagerFactory {

    public static ConnectivityManager getInstance(Activity activity, Context context, int buttonId) {
            if (buttonId == R.id.radioButton_Bluetooth) {
                return new BluetoothCommunicationManager(context, activity);
            } else if(buttonId == R.id.radioButton_ANT) {
                return new BluetoothCommunicationManager(context, activity);
            }
            return  null;
    }
}