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
import com.ksenia.pulsezonetraining.R;
import com.ksenia.pulsezonetraining.connectivity.ConnectivityManager;
import com.ksenia.pulsezonetraining.connectivity.DevicesNamesConsumer;
import com.ksenia.pulsezonetraining.connectivity.ReadingEventConsumer;
import com.ksenia.pulsezonetraining.preferences.PulseZoneSettings;
import com.ksenia.pulsezonetraining.ui.Activity_MultiDeviceSearchSampler;
import com.ksenia.pulsezonetraining.utils.PulseLimits;
import com.ksenia.pulsezonetraining.utils.PulseZoneUtils;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.ksenia.pulsezonetraining.ui.Activity_PulseZonesFitness.REQUEST_CODE_ANT_DEVICES;

/**
 * Created by ksenia on 21.02.19.
 */

public class AntCommunicationManager extends ConnectivityManager {
    public static final String EXTRA_KEY_MULTIDEVICE_SEARCH_RESULT = "com.dsi.ant.antplus.pluginsampler.multidevicesearch.result";

    private Logger logger = Logger.getLogger(this.getClass().getName());
    private AntPlusHeartRatePcc hrPcc = null;
    private PccReleaseHandle<AntPlusHeartRatePcc> releaseHandle = null;
    private String status;

    public AntCommunicationManager(Context context, Activity activity) {
        super(context, activity);
    }

    @Override
    public void scanForDevices() {
        Intent enableBtIntent = new Intent(context, Activity_MultiDeviceSearchSampler.class);
        activity.startActivityForResult(enableBtIntent, REQUEST_CODE_ANT_DEVICES);
    }

    @Override
    public void connect(String device) {
        handleReset();
        requestAccessToPcc(Integer.parseInt(device));
    }

    @Override
    public boolean isConnected() {
        return DeviceState.TRACKING.equals(hrPcc.getCurrentDeviceState());
    }

//    @Override
    public void unsubscribeHREvent() {
        hrPcc.subscribeHeartRateDataEvent((estTimestamp, eventFlags, computedHeartRate, heartBeatCount, heartBeatEventTime, dataState) -> {});
    }

//    @Override
    public void subscribeHREvent(ReadingEventConsumer<Integer> consumer) {
        hrPcc.subscribeHeartRateDataEvent((estTimestamp, eventFlags, computedHeartRate, heartBeatCount, heartBeatEventTime, dataState) -> {
            logger.info(String.format("estTimestamp:%beating_heart$s eventFlags:%2$s computedHeartRate:%3$s heartBeatCount:%4$s heartBeatEventTime:%5$s dataState:%6$s", estTimestamp, eventFlags, computedHeartRate, heartBeatCount, heartBeatEventTime, dataState));
            consumer.accept(computedHeartRate);

        });
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

    protected void requestAccessToPcc(int deviceNumber) {
        //Intent intent = activity.getIntent();
        //if (intent.hasExtra(EXTRA_KEY_MULTIDEVICE_SEARCH_RESULT)) {
            // device has already been selected through the multi-device search
            //MultiDeviceSearch.MultiDeviceSearchResult result = intent
                   // .getParcelableExtra(EXTRA_KEY_MULTIDEVICE_SEARCH_RESULT);

            releaseHandle = AntPlusHeartRatePcc.requestAccess(context, deviceNumber, 0,
                    base_IPluginAccessResultReceiver, base_IDeviceStateChangeReceiver);
        //} else {
            // starts the plugins UI search
           // releaseHandle = AntPlusHeartRatePcc.requestAccess(activity, context,
            //        base_IPluginAccessResultReceiver, base_IDeviceStateChangeReceiver);
       // }
    }

    /**
     * Handle the result, connecting to events on success or reporting failure to user.
     */
    private AntPluginPcc.IPluginAccessResultReceiver<AntPlusHeartRatePcc> base_IPluginAccessResultReceiver =
            (result, resultCode, initialDeviceState) -> {
                logger.info(String.format("result: %s, resultCode: %s, initialDeviceState: %s", result == null ? "null" : result.toString(), resultCode.toString(), initialDeviceState.toString()));
                status = activity.getResources().getString(R.string.connection_string);
                switch(resultCode) {
                    case SUCCESS:
                        hrPcc = result;
                       //status = (String.format(Locale.getDefault(),"%s\nPulse range: %d-%d", PulseZoneUtils.getZoneName(pulseSettings.getZoneRadioButtonId()), pulseLimits.getLowPulseLimit(), pulseLimits.getHighPulseLimit()));
                        //subscribeToHrEvents();

//                 TODO       super.onConnectionChangeConsumer.accept();
                        break;
                   /* case CHANNEL_NOT_AVAILABLE:
//                        tv_status.setText(R.string.error_string);
                        break;
                    case ADAPTER_NOT_DETECTED:
//                        tv_status.setText(R.string.error_string);
                        break;
                    case BAD_PARAMS:
//                        tv_status.setText(R.string.error_string);
                        break;
                    case OTHER_FAILURE:
                        tv_status.setText(R.string.error_string);
                        break;
                    case DEPENDENCY_NOT_INSTALLED:
                        tv_status.setText(R.string.error_string);
                        break;
                    case USER_CANCELLED:
                        tv_status.setText(R.string.error_string);
                        break;
                    case UNRECOGNIZED:
                        tv_status.setText(R.string.error_string);
                        break;*/
                    default:
                        status = activity.getResources().getString(R.string.error_string);
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
//                        TODO super.onConnectionChangeConsumer.accept();
                        Toast.makeText(activity, "Device status changed: " + newDeviceState, Toast.LENGTH_SHORT).show();
                    });

    @Override
    public void disconnect() {
        if(hrPcc != null) {
            hrPcc.releaseAccess();
        }
    }

}
