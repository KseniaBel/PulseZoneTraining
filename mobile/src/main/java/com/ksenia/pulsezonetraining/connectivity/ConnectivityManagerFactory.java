package com.ksenia.pulsezonetraining.connectivity;

import android.app.Activity;
import android.content.Context;

import com.ksenia.pulsezonetraining.R;
import com.ksenia.pulsezonetraining.connectivity.ant.AntCommunicationManager;
import com.ksenia.pulsezonetraining.connectivity.bluetooth.BluetoothCommunicationManager;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by ksenia on 17.02.19.
 */

public class ConnectivityManagerFactory {


    public static ConnectivityManager getInstance(Activity activity, Observer observer, Context context, ConnectivityManager.CONNECTION_TYPE connectionType) {
        ConnectivityManager connectivityManager = null;
        if (ConnectivityManager.CONNECTION_TYPE.BLUETOOTH.equals(connectionType)) {
            connectivityManager = new BluetoothCommunicationManager(context, activity);
        } else if(ConnectivityManager.CONNECTION_TYPE.ANT.equals(connectionType)) {
            connectivityManager = new AntCommunicationManager(context, activity);
        }

        connectivityManager.addObserver(observer);
        return  connectivityManager;
    }
}