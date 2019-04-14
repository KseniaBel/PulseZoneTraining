package com.ksenia.pulsezonetraining.connectivity.ant;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.dsi.ant.plugins.antplus.pcc.AntPlusHeartRatePcc;
import com.dsi.ant.plugins.antplus.pcc.defines.DeviceState;
import com.dsi.ant.plugins.antplus.pccbase.AntPluginPcc;
import com.dsi.ant.plugins.antplus.pccbase.MultiDeviceSearch;
import com.dsi.ant.plugins.antplus.pccbase.PccReleaseHandle;
import com.ksenia.pulsezonetraining.connectivity.ConnectivityManager;
import com.ksenia.pulsezonetraining.connectivity.Event;
import com.ksenia.pulsezonetraining.ui.Activity_MultiDeviceSearchSampler;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by ksenia on 21.02.19.
 */

public class AntCommunicationManager extends ConnectivityManager {
    public static final int REQUEST_CODE_ANT_DEVICES = 3;
    public static final String EXTRA_KEY_MULTIDEVICE_SEARCH_RESULT = "com.dsi.ant.antplus.pluginsampler.multidevicesearch.result";

    private Logger logger = Logger.getLogger(this.getClass().getName());
    private AntPlusHeartRatePcc hrPcc = null;
    private PccReleaseHandle<AntPlusHeartRatePcc> releaseHandle = null;

    public AntCommunicationManager(Context context, Activity activity) {
        super(context, activity);
    }

    @Override
    public void scanForDevices() {
        Intent enableBtIntent = new Intent(context, Activity_MultiDeviceSearchSampler.class);
        activity.startActivityForResult(enableBtIntent, REQUEST_CODE_ANT_DEVICES);
    }

    @Override
    public void connectToDevice(Intent data) {
        MultiDeviceSearch.MultiDeviceSearchResult result = data.getParcelableExtra(EXTRA_KEY_MULTIDEVICE_SEARCH_RESULT);
        handleReset();
        releaseHandle = AntPlusHeartRatePcc.requestAccess(context, result.getAntDeviceNumber(), 0,
                base_IPluginAccessResultReceiver, base_IDeviceStateChangeReceiver);
    }

    @Override
    public boolean isConnected() {
        return DeviceState.TRACKING.equals(hrPcc.getCurrentDeviceState());
    }

    /**
     * Resets the PCC connection to request access again and clears any existing display data.
     */
    protected void handleReset() {
        //Release the old access if it exists
        if(releaseHandle != null) {
            releaseHandle.close();
        }
        //requestAccessToPcc();
    }

    /**
     * Handle the result, connecting to events on success or reporting failure to user.
     */
    private AntPluginPcc.IPluginAccessResultReceiver<AntPlusHeartRatePcc> base_IPluginAccessResultReceiver =
            (result, resultCode, initialDeviceState) -> {
                logger.info(String.format("result: %s, resultCode: %s, initialDeviceState: %s", result == null ? "null" : result.toString(), resultCode.toString(), initialDeviceState.toString()));
                switch(resultCode) {
                    case SUCCESS:
                        hrPcc = result;
                        //sometimes the device starts moves directly to tracking after connecting.
                        if (DeviceState.TRACKING.equals(hrPcc.getCurrentDeviceState())) {
                            this.notifyObservers(new Event(Event.Type.STATUS_CHANGED_CONNTECTED));
                        }
                        hrPcc.subscribeHeartRateDataEvent((estTimestamp, eventFlags, computedHeartRate, heartBeatCount, heartBeatEventTime, dataState) -> {
                            logger.info(String.format("estTimestamp:%beating_heart$s eventFlags:%2$s computedHeartRate:%3$s heartBeatCount:%4$s heartBeatEventTime:%5$s dataState:%6$s", estTimestamp, eventFlags, computedHeartRate, heartBeatCount, heartBeatEventTime, dataState));
                            this.notifyObservers(new Event(Event.Type.READING, computedHeartRate));
                        });
                        break;
                    default:
                        this.notifyObservers(new Event(Event.Type.STATUS_CHANGED_DISCONNTECTED));
                        break;
                }
            };

    /**
     * Receives state changes and shows it on the status display line
     */
    private AntPluginPcc.IDeviceStateChangeReceiver base_IDeviceStateChangeReceiver =
            (DeviceState newDeviceState) ->
                    activity.runOnUiThread(() -> {
                        logger.log(Level.INFO, hrPcc.getDeviceName() + ": " + newDeviceState);

                        switch (newDeviceState) {
                            case TRACKING:
                                this.notifyObservers(new Event(Event.Type.STATUS_CHANGED_CONNTECTED)); break;
                            case SEARCHING: break;
                            default:
                                this.notifyObservers(new Event(Event.Type.STATUS_CHANGED_DISCONNTECTED)); break;
                        }

                        Toast.makeText(activity, "Device status changed: " + newDeviceState, Toast.LENGTH_SHORT).show();
                    });

    @Override
    public void disconnect() {
        if(hrPcc != null) {
            hrPcc.releaseAccess();
        }
    }

}
