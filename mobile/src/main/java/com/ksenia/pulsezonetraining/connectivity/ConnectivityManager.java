package com.ksenia.pulsezonetraining.connectivity;

import android.app.Activity;
import android.content.Context;

import java.util.Observable;

/**
 * Created by ksenia on 17.02.19.
 */

public abstract class ConnectivityManager extends Observable{
//    protected ReadingEventConsumer<Integer> eventConsumer;
//    protected ConnectionChangedEventConsumer onConnectionChangeConsumer;
    protected Context context;
    protected Activity activity;

    public ConnectivityManager(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public abstract void disconnect();
    public abstract void connect(String device);

    /*public void unsubscribeHREvent() {
        eventConsumer = (reading) -> {};
    }
    public void subscribeHREvent(ReadingEventConsumer<Integer> consumer) {
        eventConsumer = consumer;
    }*/

    public abstract void scanForDevices();
    public abstract boolean isConnected();
}
