package com.ksenia.pulsezonetraining.connectivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.util.Observable;

/**
 * Created by ksenia on 17.02.19.
 */

public abstract class ConnectivityManager extends Observable{

    public enum CONNECTION_TYPE {
        BLUETOOTH,
        ANT
    }

    protected Context context;
    protected Activity activity;

    public ConnectivityManager(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public abstract void disconnect();
    public abstract void connectToDevice(Intent data);

    public abstract void scanForDevices();
    public abstract boolean isConnected();

    @Override
    public void notifyObservers(Object arg) {
        setChanged();
        super.notifyObservers(arg);
    }
}
