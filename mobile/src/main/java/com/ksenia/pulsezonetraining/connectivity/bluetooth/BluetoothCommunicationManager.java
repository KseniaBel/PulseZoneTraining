package com.ksenia.pulsezonetraining.connectivity.bluetooth;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
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
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.Toast;

import com.ksenia.pulsezonetraining.connectivity.ConnectivityManager;
import com.ksenia.pulsezonetraining.connectivity.DevicesNamesConsumer;
import com.ksenia.pulsezonetraining.connectivity.Event;
import com.ksenia.pulsezonetraining.ui.Activity_BluetoothDevices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import static android.support.constraint.Constraints.TAG;
import static com.ksenia.pulsezonetraining.ui.Activity_PulseZonesFitness.REQUEST_ENABLE_BT;
import static com.ksenia.pulsezonetraining.ui.Activity_PulseZonesFitness.SCAN_RESULTS;

/**
 * Created by ksenia on 17.02.19.
 */

public class BluetoothCommunicationManager extends ConnectivityManager {
    public static final int REQUEST_CODE_BLUETOOTH_DEVICES = 2;
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
    private ProgressDialog loadingBar;


    public BluetoothCommunicationManager(Context context, Activity activity) {
        super(context, activity);
        // Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
    }


    @Override
    public void scanForDevices() {
        //Checks if BLE is supported
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(activity, "BLE not supported on this device", Toast.LENGTH_SHORT).show();
            activity.finish();
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
            DevicesNamesConsumer<String[]> devicesNamesConsumer = namesOfDevices -> {
                Intent intent = new Intent(context, Activity_BluetoothDevices.class);
                intent.putExtra(SCAN_RESULTS, namesOfDevices);
                activity.startActivityForResult(intent, REQUEST_CODE_BLUETOOTH_DEVICES);
            };
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

            //Start progress bar
            loadingBar = new ProgressDialog(activity);
            loadingBar.setTitle("Scanning for devices");
            loadingBar.setMessage("Seconds left: " + 10);
            loadingBar.show();

            //Scan for 10 seconds
            new CountDownTimer(10000, 1000) {
                public void onTick(long millisUntilFinished) {
                    loadingBar.setMessage("Seconds left: " + millisUntilFinished/1000);
                }

                public void onFinish() {
                    loadingBar.cancel();
                    if (mScanResults.isEmpty()) {
                        Toast.makeText(activity, "No bluetooth device found", Toast.LENGTH_SHORT).show();
                        mScanning = false;
                        activity.finish();
                    }
                }
            }.start();

            mHandler.postDelayed(() -> this.stopScan(devicesNamesConsumer), SCAN_PERIOD);
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

    @Override
    public void connectToDevice(Intent data) {
        String deviceToConnect = data.getStringExtra(Activity_BluetoothDevices.DEVICE_TO_CONNECT_NR);

        for(BluetoothDevice device: mScanResults.values()) {
            if(device.getName().equals(deviceToConnect)) {
                logger.info("Connecting to " + device.getAddress());
                GattClientCallback gattClientCallback = new GattClientCallback(this);
                mGatt = device.connectGatt(context, false, gattClientCallback);
                return;
            }
        }
    }

    public void disconnectGattServer() {
        logger.info("Closing Gatt connection");
        notifyObservers(new Event(Event.Type.STATUS_CHANGED_DISCONNTECTED));
        mConnected = false;
        //mEchoInitialized = false;
        if (mGatt != null) {
            mGatt.disconnect();
            mGatt.close();
        }
    }

    @Override
    public boolean isConnected() {
        return mConnected;
    }

    public void connectGattServer() {
        mConnected = true;
        notifyObservers(new Event(Event.Type.STATUS_CHANGED_CONNTECTED));
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
