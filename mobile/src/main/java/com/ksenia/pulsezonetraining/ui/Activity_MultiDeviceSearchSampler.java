/*
This software is subject to the license described in the License.txt file
included with this software distribution. You may not use this file except in compliance
with this license.

Copyright (c) Dynastream Innovations Inc. 2014
All rights reserved.
 */

package com.ksenia.pulsezonetraining.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dsi.ant.plugins.antplus.pcc.MultiDeviceSearch;
import com.dsi.ant.plugins.antplus.pcc.MultiDeviceSearch.RssiSupport;
import com.dsi.ant.plugins.antplus.pcc.defines.DeviceType;
import com.dsi.ant.plugins.antplus.pcc.defines.RequestAccessResult;
import com.dsi.ant.plugins.antplus.pccbase.MultiDeviceSearch.MultiDeviceSearchResult;
import com.ksenia.pulsezonetraining.R;
import com.ksenia.pulsezonetraining.ui.custom.ArrayAdapter_MultiDeviceSearchResult;

import java.util.ArrayList;
import java.util.EnumSet;

/**
 * Searches for multiple devices on the same channel using the multi-device
 * search interface
 */
public class Activity_MultiDeviceSearchSampler extends AppCompatActivity {
    /**
     * Relates a MultiDeviceSearchResult with an RSSI value
     */
    public class MultiDeviceSearchResultWithRSSI
    {
        public MultiDeviceSearchResult mDevice;
        public int mRSSI = Integer.MIN_VALUE;
    }
    public static final String EXTRA_KEY_MULTIDEVICE_SEARCH_RESULT = "com.dsi.ant.antplus.pluginsampler.multidevicesearch.result";
    public static final String BUNDLE_KEY = "com.dsi.ant.antplus.pluginsampler.multidevicesearch.bundle";
    public static final String FILTER_KEY = "com.dsi.ant.antplus.pluginsampler.multidevicesearch.filter";
    public static final int RESULT_SEARCH_STOPPED = RESULT_FIRST_USER;

    private Context mContext;
    private TextView mStatus;

    private ListView mFoundDevicesList;
    private ArrayList<MultiDeviceSearchResultWithRSSI> mFoundDevices = new ArrayList<MultiDeviceSearchResultWithRSSI>();
    private ArrayAdapter_MultiDeviceSearchResult mFoundAdapter;

    private ListView mConnectedDevicesList;
    private ArrayList<MultiDeviceSearchResultWithRSSI> mConnectedDevices = new ArrayList<MultiDeviceSearchResultWithRSSI>();
    private ArrayAdapter_MultiDeviceSearchResult mConnectedAdapter;

    private MultiDeviceSearch mSearch;
    private ProgressDialog loadingBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multidevice_scan);
        Toolbar toolbar = findViewById(R.id.toolbar_ant);
        setSupportActionBar(toolbar);

        mContext = getApplicationContext();
        mStatus = findViewById(R.id.textView_workoutStatus);

        mFoundDevicesList = findViewById(R.id.listView_FoundDevices);
        mFoundDevicesList.setDivider(getDrawable(R.drawable.divider));

        mFoundAdapter = new ArrayAdapter_MultiDeviceSearchResult(this, mFoundDevices);
        mFoundDevicesList.setAdapter(mFoundAdapter);

        mFoundDevicesList.setOnItemClickListener((parent, view, position, id) -> launchConnection(mFoundAdapter.getItem(position).mDevice));

        //mConnectedDevicesList = findViewById(R.id.listView_AlreadyConnectedDevices);
        //mConnectedDevicesList.setDivider(getDrawable(R.drawable.divider));

       // mConnectedAdapter = new ArrayAdapter_MultiDeviceSearchResult(this, mConnectedDevices);
        //mConnectedDevicesList.setAdapter(mConnectedAdapter);

       // mConnectedDevicesList.setOnItemClickListener((parent, view, position, id) -> launchConnection(mConnectedAdapter.getItem(position).mDevice));

        // start the multi-device search
        mSearch = new MultiDeviceSearch(this, EnumSet.of(DeviceType.HEARTRATE), mCallback, null);

        //Start progress bar
        loadingBar = new ProgressDialog(this);
        loadingBar.setTitle("Scanning for devices");
        loadingBar.setMessage("Seconds left: " + 10);
        loadingBar.show();

        //Scan for 10 seconds
        new CountDownTimer(10000, 1000) {
            public void onTick(long millisUntilFinished) {
                loadingBar.setMessage("Seconds left: " + millisUntilFinished/1000);
            }

            public void onFinish() {
                if(mFoundDevices.isEmpty()) {
                    onBackPressed();
                }
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // close and clean-up the multi-device search
        mSearch.close();
    }

    public void launchConnection(MultiDeviceSearchResult result) {
        if (result != null) {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_KEY_MULTIDEVICE_SEARCH_RESULT, result);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if(mFoundDevices.isEmpty()) {
            loadingBar.cancel();
            Toast.makeText(this, "No ANT+ devices were found", Toast.LENGTH_SHORT).show();
        }
        super.onBackPressed();
    }

    /**
     * Callbacks from the multi-device search interface
     */
    private MultiDeviceSearch.SearchCallbacks mCallback = new MultiDeviceSearch.SearchCallbacks() {
        /**
         * Called when a device is found. Display found devices in connected and
         * found lists
         */
        public void onDeviceFound(final MultiDeviceSearchResult deviceFound) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    loadingBar.cancel();
                }
            });

            final MultiDeviceSearchResultWithRSSI result = new MultiDeviceSearchResultWithRSSI();
            result.mDevice = deviceFound;

            if (deviceFound == null) {
                onBackPressed();
                /*runOnUiThread(() -> {
                    // connected device category is invisible unless there
                    // are some present
                    /*if (mConnectedAdapter.isEmpty())
                    {
                        findViewById(R.id.textView_AlreadyConnectedTitle).setVisibility(
                                View.VISIBLE);
                        mConnectedDevicesList.setVisibility(View.VISIBLE);
                    }

                    mConnectedAdapter.add(result);
                    mConnectedAdapter.notifyDataSetChanged();
                });*/
            } else {
                runOnUiThread(() -> {
                    mFoundAdapter.add(result);
                    mFoundAdapter.notifyDataSetChanged();
                });
            }
        }

        /**
         * The search has been stopped unexpectedly
         */
        public void onSearchStopped(RequestAccessResult reason) {
            Intent result = new Intent();
            result.putExtra(EXTRA_KEY_MULTIDEVICE_SEARCH_RESULT, reason.getIntValue());
            setResult(RESULT_SEARCH_STOPPED, result);
            finish();
        }

        @Override
        public void onSearchStarted(RssiSupport supportsRssi) {
            if(supportsRssi == RssiSupport.UNAVAILABLE)
            {
                Toast.makeText(mContext, "Rssi information not available.", Toast.LENGTH_SHORT).show();
            } else if(supportsRssi == RssiSupport.UNKNOWN_OLDSERVICE)
            {
                Toast.makeText(mContext, "Rssi might be supported. Please upgrade the plugin service.", Toast.LENGTH_SHORT).show();
            }
        }
    };

    /**
     * Callback for RSSI data of previously found devices
     */
    /*private MultiDeviceSearch.RssiCallback mRssiCallback = new MultiDeviceSearch.RssiCallback()
    {
        /**
         * Receive an RSSI data update from a specific found device
         */
        /*@Override
        public void onRssiUpdate(final int resultId, final int rssi)
        {
            runOnUiThread(() -> {
                for (MultiDeviceSearchResultWithRSSI result : mFoundDevices)
                {
                    if (result.mDevice.resultID == resultId)
                    {
                        result.mRSSI = rssi;
                        mFoundAdapter.notifyDataSetChanged();

                        break;
                    }
                }
            });
        }
    };*/
}
