package com.ksenia.pulsezonetraining.connectivity.bluetooth;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.ksenia.pulsezonetraining.connectivity.Event;
import com.ksenia.pulsezonetraining.connectivity.ReadingEventConsumer;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import static android.support.constraint.Constraints.TAG;
import static com.ksenia.pulsezonetraining.connectivity.bluetooth.BluetoothCommunicationManager.HEART_RATE_SERVICE_UUID;
import static com.ksenia.pulsezonetraining.connectivity.bluetooth.BluetoothUtils.convertFromInteger;

/**
 * Created by ksenia on 17.02.19.
 */

public class GattClientCallback extends BluetoothGattCallback {
     Logger logger = Logger.getLogger(this.getClass().getName());
    private BluetoothCommunicationManager bluetoothCommunicationManager;

    public GattClientCallback(BluetoothCommunicationManager bluetoothCommunicationManager) {
        this.bluetoothCommunicationManager = bluetoothCommunicationManager;
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        super.onConnectionStateChange(gatt, status, newState);
        logger.info("onConnectionStateChange newState: " + newState);

        if (status == BluetoothGatt.GATT_FAILURE) {
            logger.info("Connection Gatt failure status " + status);
            bluetoothCommunicationManager.disconnectGattServer();
            return;
        } else if (status != BluetoothGatt.GATT_SUCCESS) {
            // handle anything not SUCCESS as failure
            logger.info("Connection not GATT sucess status " + status);
            bluetoothCommunicationManager.disconnectGattServer();
            return;
        }

        if (newState == BluetoothProfile.STATE_CONNECTED) {
            logger.info("Connected to device " + gatt.getDevice().getAddress());
            bluetoothCommunicationManager.connectGattServer();
            gatt.discoverServices();
        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            logger.info("Disconnected from device");
            bluetoothCommunicationManager.disconnectGattServer();
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        super.onServicesDiscovered(gatt, status);

        if (status != BluetoothGatt.GATT_SUCCESS) {
            logger.info("Device service discovery unsuccessful, status " + status);
            return;
        }

        List<BluetoothGattCharacteristic> matchingCharacteristics = BluetoothUtils.findCharacteristics(gatt);
        if (matchingCharacteristics.isEmpty()) {
            logger.info("Unable to find characteristics.");
            return;
        }

        logger.info("Initializing: setting write type and enabling notification");
        for (BluetoothGattCharacteristic characteristic : matchingCharacteristics) {
            enableCharacteristicNotification(gatt, characteristic);
        }
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicChanged(gatt, characteristic);
        logger.info("Characteristic changed, " + characteristic.getUuid().toString());
        readCharacteristic(characteristic);
    }

    private void enableCharacteristicNotification(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        boolean characteristicWriteSuccess = gatt.setCharacteristicNotification(characteristic, true);
        if (characteristicWriteSuccess) {
            logger.info("Characteristic notification set successfully for " + characteristic.getUuid().toString());
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(BluetoothCommunicationManager.CLIENT_CHARACTERISTIC_CONFIG_UUID);
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            gatt.writeDescriptor(descriptor);

        } else {
            logger.info("Characteristic notification set failure for " + characteristic.getUuid().toString());
        }
    }

    private void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        int flag = characteristic.getProperties();
        int format = -1;
        if ((flag & 0x01) != 0) {
            format = BluetoothGattCharacteristic.FORMAT_UINT16;
            Log.d(TAG, "Heart rate format UINT16.");
        } else {
            format = BluetoothGattCharacteristic.FORMAT_UINT8;
            Log.d(TAG, "Heart rate format UINT8.");
        }
        final int heartRate = characteristic.getIntValue(format, 1);
        bluetoothCommunicationManager.notifyObservers(new Event(Event.Type.READING, heartRate));
        Log.d(TAG, String.format("Received heart rate: %d", heartRate));
    }
}
