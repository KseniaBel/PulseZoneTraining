package com.ksenia.pulsezonetraining.connectivity;

import android.app.Activity;
import android.content.Context;

/**
 * Created by ksenia on 17.02.19.
 */

public abstract class ConnectivityManager {
    protected ReadingEventConsumer<Integer> eventConsumer;
    protected ConnectionChangedEventConsumer onConnectionChangeConsumer;
    protected Context context;
    protected Activity activity;

    public ConnectivityManager(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public abstract void connect(String device);
    public void subscribeHREvent(ReadingEventConsumer<Integer> consumer) {
        eventConsumer = consumer;
    }
    public void onConnectionChanged(ConnectionChangedEventConsumer consumer) {
        this.onConnectionChangeConsumer = consumer;
    }
    public abstract void disconnect();

    public abstract void scanForDevices(DevicesNamesConsumer<String[]> devicesNamesConsumer);
    public abstract boolean isConnected();
    public void unsubscribeHREvent() {
        eventConsumer = (reading) -> {};
    }
}
