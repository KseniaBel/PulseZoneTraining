package com.ksenia.pulsezonetraining.connectivity.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.util.Log;

import java.util.List;
import java.util.Map;

import static android.support.constraint.Constraints.TAG;

/**
 * Created by ksenia on 17.02.19.
 */

public class BluetoothScanCallback extends ScanCallback {
    private Map<String, BluetoothDevice> mScanResults;

    public BluetoothScanCallback(Map<String, BluetoothDevice> mScanResults) {
        this.mScanResults = mScanResults;
    }

    @Override
    public void onScanResult(int callbackType, ScanResult result) {
        addScanResult(result);
    }
    @Override
    public void onBatchScanResults(List<ScanResult> results) {
        for (ScanResult result : results) {
            addScanResult(result);
        }
    }
    @Override
    public void onScanFailed(int errorCode) {
        Log.e(TAG, "BLE Scan Failed with code " + errorCode);
    }

    private void addScanResult(ScanResult result) {
        BluetoothDevice device = result.getDevice();
        String deviceAddress = device.getAddress();
        mScanResults.put(deviceAddress, device);
    }
}
