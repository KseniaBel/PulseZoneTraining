package com.ksenia.pulsezonetraining.connectivity.bluetooth;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.ksenia.pulsezonetraining.connectivity.ConnectivityManager;
import com.ksenia.pulsezonetraining.connectivity.DevicesNamesConsumer;
import com.ksenia.pulsezonetraining.connectivity.ReadingEventConsumer;
import com.ksenia.pulsezonetraining.ui.Activity_PulseZonesFitness;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Logger;

import static android.support.constraint.Constraints.TAG;
import static com.ksenia.pulsezonetraining.ui.Activity_PulseZonesFitness.REQUEST_ENABLE_BT;

/**
 * Created by ksenia on 17.02.19.
 */

public class BluetoothCommunicationManager extends ConnectivityManager {
    public static final int HEART_RATE_SERVICE_UUID = 0x180D;
    public static final int  CHARACTERISTICS_HEART_RATE_MEASUREMENTS = 0x2A37;
    public static final UUID CLIENT_CHARACTERISTIC_CONFIG_UUID = BluetoothUtils.convertFromInteger(0x2902);
    private static final int REQUEST_FINE_LOCATION = 1;
    private static final long SCAN_PERIOD = 10000;

    private Logger logger = Logger.getLogger(this.getClass().getName());
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mGatt;
    private Map<String, BluetoothDevice> mScanResults;
    private boolean mScanning;
    private BluetoothScanCallback mScanCallback;
    private BluetoothLeScanner mBluetoothLeScanner;
    private Handler mHandler;
    private boolean mConnected;
    private boolean mEchoInitialized;
    private int nrOfDevice;


    public BluetoothCommunicationManager(Context context, Activity activity) {
        super(context, activity);
        // Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
    }


    @Override
    public void scanForDevices(DevicesNamesConsumer<String[]> devicesNamesConsumer) {
        //Checks if BLE is supported
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            throw new UnsupportedOperationException("BLE Not Supported");
        }

        //Checks for bluetooth and location permissions and requests missing permissions
        if (mScanning) {
            return;
        } else if(!hasLocationPermissions()) {
            activity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
        } else if(mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            Log.d(TAG, "Requested user enables Bluetooth. Try starting the scan again.");
            Toast.makeText(activity, "Application requires Bluetooth and GPS connections", Toast.LENGTH_SHORT).show();
        } else {
            disconnectGattServer();

            //Scanning for devices with HR service UUID during 10 sec
            List<ScanFilter> filters = new ArrayList<>();
            ScanFilter scanFilter = new ScanFilter.Builder()
                    .setServiceUuid(new ParcelUuid(BluetoothUtils.convertFromInteger(HEART_RATE_SERVICE_UUID)))
                    .build();
            filters.add(scanFilter);
            ScanSettings settings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                    .build();
            mScanResults = new HashMap<>();
            mScanCallback = new BluetoothScanCallback(mScanResults);
            mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
            mBluetoothLeScanner.startScan(filters, settings, mScanCallback);
            mScanning = true;
            mHandler = new Handler();
            mHandler.postDelayed(() -> this.stopScan(devicesNamesConsumer), SCAN_PERIOD);
        }
    }

    @Override
    public boolean isConnected() {
        return mConnected;
    }


    @Override
    public void connect(String deviceToConnect) {
        for(BluetoothDevice device: mScanResults.values()) {
            if(device.getName().equals(deviceToConnect)) {
                logger.info("Connecting to " + device.getAddress());
                GattClientCallback gattClientCallback = new GattClientCallback(this);
                mGatt = device.connectGatt(context, false, gattClientCallback);
                return;
            }
        }
    }

    private boolean hasLocationPermissions() {
        return activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void stopScan(DevicesNamesConsumer<String[]> devicesNamesConsumer) {
        if (mScanning && mBluetoothAdapter != null && mBluetoothAdapter.isEnabled() && mBluetoothLeScanner != null) {
            mBluetoothLeScanner.stopScan(mScanCallback);
            //Returns if no bluetooth devices were found
            if (mScanResults.isEmpty()) {
                Toast.makeText(activity, "No bluetooth device found", Toast.LENGTH_SHORT).show();
                mScanning = false;
                activity.finish();
                return;
            }

            //Adds found device's names to array
            List<String> nameOfDevices = new ArrayList<>();
            for (String deviceAddress : mScanResults.keySet()) {
                Log.d(TAG, "Found device: " + deviceAddress);
                nameOfDevices.add(mScanResults.get(deviceAddress).getName());
            }
            devicesNamesConsumer.accept(nameOfDevices.toArray(new String[nameOfDevices.size()]));
        }
        mScanCallback = null;
        mScanning = false;
        mHandler = null;
    }


    public void disconnectGattServer() {
        logger.info("Closing Gatt connection");
        mConnected = false;
        //mEchoInitialized = false;
        if (mGatt != null) {
            mGatt.disconnect();
            mGatt.close();
        }
    }

    public void setConnected(boolean connected) {
        mConnected = connected;
    }


    protected ReadingEventConsumer<Integer> getEventConsumer() {
        return super.eventConsumer;
    }

    @Override
    public void disconnect() {
        if (mGatt == null) {
            return;
        }
        mGatt.close();
        mGatt = null;
    }
}