/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.bluetoothlegatt.service;

import android.annotation.TargetApi;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.example.android.bluetoothlegatt.SampleGattAttributes;
import com.example.android.bluetoothlegatt.constant.Constants;
import com.example.android.bluetoothlegatt.util.PrefUtils;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.List;
import java.util.UUID;

/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BluetoothLeService extends Service {
    private final static String TAG = BluetoothLeService.class.getSimpleName();

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";

    public final static UUID UUID_HEART_RATE_MEASUREMENT =
            UUID.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT);

    public final static UUID UUID_CHAR10 =
            UUID.fromString(SampleGattAttributes.CHAR10);

    int mState = 0;

    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                Log.i(TAG, "Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
//                List<BluetoothGattService> bluetoothLeServices = mBluetoothGatt.getServices();
//                List<BluetoothGattCharacteristic> bluetoothGattCharacteristics;
//                for (BluetoothGattService bluetoothGattService : bluetoothLeServices) {
//                    bluetoothGattCharacteristics = bluetoothGattService.getCharacteristics();
//                    for (BluetoothGattCharacteristic bluetoothGattCharacteristic : bluetoothGattCharacteristics) {
//                        mBluetoothGatt.readCharacteristic(bluetoothGattCharacteristic);
//                    }
//                }
                List<BluetoothGattService> services = gatt.getServices();
                Log.i("onServicesDiscovered", services.toString());
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                byte[] data = characteristic.getValue();
                StringBuilder stringBuilder = new StringBuilder(data.length);
                int length = data.length;
                for (int i = 0; i < length; i++) {
                    stringBuilder.append(String.format("%02X ", new Object[]{Byte.valueOf(data[i])}));
                }
                broadcastUpdate(ACTION_DATA_AVAILABLE, stringBuilder.toString());
                Log.d("onCharacteristicRead", stringBuilder.toString());
            }

//            setNotifyNextSensor(gatt);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            byte[] data = characteristic.getValue();
            StringBuilder stringBuilder = new StringBuilder(data.length);
            int length = data.length;
            for (int i = 0; i < length; i++) {
                stringBuilder.append(String.format("%02X ", new Object[]{Byte.valueOf(data[i])}));
            }
            String uuid = characteristic.getService().getUuid().toString();
            String characteristicUUID = characteristic.getUuid().toString();
            Log.d("onCharacteristicChanged", stringBuilder.toString());
            if (uuid.equals("0aabcdef-1111-2222-0000-facebeadaaaa") && characteristicUUID.equals("facebead-ffff-eeee-0004-facebeadaaaa")) {
                BluetoothLeService.this.broadcastUpdate(Constants.ACTION_MAIN_DATA_ECG_ALL_DATA, stringBuilder.toString());
            } else if (uuid.equals("0aabcdef-1111-2222-0000-facebeadaaaa") && characteristicUUID.equals("facebead-ffff-eeee-0005-facebeadaaaa")) {
                BluetoothLeService.this.broadcastUpdate(Constants.ACTION_MAIN_DATA_PW, stringBuilder.toString());
            } else if (uuid.equals("1aabcdef-1111-2222-0000-facebeadaaaa")) {
                BluetoothLeService.this.sendBindBroadcast(stringBuilder.toString());
            } else {
//                broadcastUpdate(ACTION_DATA_AVAILABLE, stringBuilder.toString());
                BluetoothLeService.this.sendDataBroadcast(stringBuilder.toString());
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
//            readNextSensor(gatt);
//            if (status == BluetoothGatt.GATT_SUCCESS) {
////                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
//
//
//                final byte[] data = characteristic.getValue();
//                if (data != null && data.length > 0) {
//                    final StringBuilder stringBuilder = new StringBuilder(data.length);
//                    for (byte byteChar : data)
//                        stringBuilder.append(String.format("%02X ", byteChar));
//
//                    Log.d("onCharacteristicWrite", "" +  new String(data) + "\n" + stringBuilder.toString());
//                }
//
//                mState++;
//            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        }
    };

    /**
     *
     * @param data
     */
    private void sendBindBroadcast(String data) {
        String cmdType = data.substring(9, 11);
        if (cmdType.equals(Constants.CMD.DEVICE_MATCH_ACK)) {
            Constants.isMatchInfo = true;
            broadcastUpdate(Constants.ACTION_GATT_DEVICE_MATCH_ACK, Long.valueOf(data.substring(15, 17), 16));
        } else if (cmdType.equals(Constants.CMD.DEVICE_UNBIND_ACK)) {
            String values = data.substring(15, 17);
            broadcastUpdate(Constants.ACTION_GATT_DEVICE_UNBIND_ACK, Long.valueOf(values, 16));
        } else if (cmdType.equals(Constants.CMD.DEVICE_BIND_REQUEST)) {
            broadcastUpdate(Constants.ACTION_GATT_DEVICE_BIND_REQUEST);
        }
    }

    /**
     * @param data
     */
    private void sendDataBroadcast(String data) {
        Log.v(TAG, "Send Data = " + data);
        if (!data.equals("CF")) {
            String dataType = data.substring(9, 11);
            Log.v(TAG, "Data CMD =========== " + dataType);
            if (Constants.CMD.DATA_HEART_RATE.equals(dataType)) {
                broadcastUpdate(Constants.ACTION_MAIN_DATA_HR, parseSingeData(data));
                return;
            }
            if (Constants.CMD.DATA_MOOD.equals(dataType)) {
                broadcastUpdate(Constants.ACTION_MAIN_DATA_MOOD, parseMoodIntData(data));
                return;
            }
            if (Constants.CMD.DATA_FATIGUE.equals(dataType)) {
                broadcastUpdate(Constants.ACTION_MAIN_DATA_FATIGUE, parseMoodIntData(data));
                return;
            }
            if (Constants.CMD.DATA_BREATH_RATE.equals(dataType)) {
                broadcastUpdate(Constants.ACTION_MAIN_DATA_BREATH, parseBRData(data));
                return;
            }
            if (Constants.CMD.DATA_KLL.equals(dataType)) {
                broadcastUpdate(Constants.ACTION_MAIN_DATA_KLL, parseSingeData(data));
                return;
            }
            if (Constants.CMD.DATA_SLEEP.equals(dataType)) {
                broadcastUpdate(Constants.ACTION_MAIN_DATA_SLEEP, parseSleepData(data));
                return;
            }
            if (Constants.CMD.DATA_BLOOD_PRESSURE.equals(dataType)) {
                Log.v(TAG, "bp = " + data);
                broadcastUpdate(Constants.ACTION_MAIN_DATA_BP, parseBpData(data));
                return;
            }
            if (Constants.CMD.DATA_ECG.equals(dataType)) {
                broadcastUpdate(Constants.ACTION_MAIN_DATA_ECG, parseEcgData(data));
                return;
            }
        }

    }


    /**
     *
     * @param data
     * @return value of battery
     */
    private int pareseBatteryData(String data) {
        return Integer.parseInt(data.substring(15, 17), 16);
    }

    /**
     *
     * @param data
     * @return
     */
    private String parseSingeData(String data) {
        String dataStr = data.substring(27, 38);
        return new StringBuilder(String.valueOf((long) Integer.parseInt(dataStr.substring(9, 11) + dataStr.substring(6, 8) + dataStr.substring(3, 5) + dataStr.substring(0, 2), 16))).toString();
    }

    /**
     *
     * @param data
     * @return
     */
    private long parseSleepData(String data) {
        String dataStr = data.substring(27, 38);
        String dateStr = data.substring(15, 26);
        return (long) Integer.parseInt(dataStr.substring(9, 11) + dataStr.substring(6, 8) + dataStr.substring(3, 5) + dataStr.substring(0, 2), 16);
    }

    /**
     *
     * @param data
     * @return
     */
    private String parseBpData(String data) {
        Log.v(TAG, "bp data = " + data);
        String dataStr = data.substring(27, 32);
        Log.v(TAG, "bp dataStr = " + dataStr);
        return dataStr;
    }

    /**
     *
     * @param data
     * @return
     */
    private String parseEcgData(String data) {
        Log.v(TAG, "Ecg data = " + data);
        String dataStr = data.substring(27, 29);
        Log.v(TAG, "Ecg dataStr = " + dataStr);
        return dataStr;
    }

    /**
     *
     * @param data
     * @return
     */
    private String parseBRData(String data) {
        String dataStr = data.substring(27, 29);
        Log.v(TAG, "BR DATA = " + dataStr);
        return new StringBuilder(String.valueOf((long) Integer.parseInt(dataStr, 16))).toString();
    }

    /**
     *
     * @param data
     * @return
     */
    private String parseMoodIntData(String data) {
        return data.substring(27, 29);
    }

    /**
     *
     * @param action
     * @param dataf
     */
    private void broadcastUpdate(String action, long dataf) {
        Intent intent = new Intent(action);
        intent.putExtra(action, dataf);
        sendBroadcast(intent);
    }

    /**
     *
     * @param action
     */
    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    /**
     *
     * @param action
     * @param data
     */
    private void broadcastUpdate(final String action,
                                 final String data) {
        final Intent intent = new Intent(action);
        if (ACTION_DATA_AVAILABLE.equals(action)) {
            intent.putExtra(EXTRA_DATA, data);
        } else {
            intent.putExtra(action, data);
        }
        sendBroadcast(intent);
    }

    public class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close();
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     * @return Return true if the connection is initiated successfully. The connection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
//        Log.d(TAG, String.valueOf(device.createBond()));
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled        If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

        // This is specific to Heart Rate Measurement.
//        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
//            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
//                    UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
//            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//            mBluetoothGatt.writeDescriptor(descriptor);
//        }

        if (UUID_CHAR10.equals(characteristic.getUuid())) {
            List<BluetoothGattDescriptor> bluetoothGattDescriptors = characteristic.getDescriptors();
            if (bluetoothGattDescriptors.size() > 0) {
                BluetoothGattDescriptor descriptor = bluetoothGattDescriptors.get(0);
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                mBluetoothGatt.writeDescriptor(descriptor);
            }
        }

//        List<BluetoothGattDescriptor> descriptors = characteristic.getDescriptors();
//        for (BluetoothGattDescriptor descriptor : descriptors) {
//            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//            mBluetoothGatt.writeDescriptor(descriptor);
//        }
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;

        return mBluetoothGatt.getServices();
    }

    public BluetoothGatt getmBluetoothGatt() {
        return mBluetoothGatt;
    }


    public void writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (this.mBluetoothAdapter == null || this.mBluetoothGatt == null) {
        } else {
            this.mBluetoothGatt.writeCharacteristic(characteristic);
        }
    }

    protected boolean writeRXCharacteristic(String serviceUUID, String charactersticUUID, byte[] value) {
        BluetoothGattService RxService = null;
        if (this.mBluetoothGatt != null) {
            try {
                RxService = this.mBluetoothGatt.getService(UUID.fromString(serviceUUID));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (RxService == null) {
            return false;
        }
        BluetoothGattCharacteristic RxChar = RxService.getCharacteristic(UUID.fromString(charactersticUUID));
        if (RxChar == null) {
            return false;
        }
        RxChar.setValue(value);
        return this.mBluetoothGatt.writeCharacteristic(RxChar);
    }

    public boolean writeUDCharacteristic(String serviceUUID, String charactersticUUID, byte[] value) {
        BluetoothGattService RxService = null;
        try {
            RxService = this.mBluetoothGatt.getService(UUID.fromString(serviceUUID));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (RxService == null) {
            return false;
        }
        BluetoothGattCharacteristic RxChar = RxService.getCharacteristic(UUID.fromString(charactersticUUID));
        if (RxChar == null) {
            return false;
        }
        RxChar.setValue(value);
        RxChar.setWriteType(1);
        boolean status = this.mBluetoothGatt.writeCharacteristic(RxChar);
        return status;
    }

    public boolean setCharacteristicNotification(String serviceUUID, String characteristicUUID, boolean enabled) {
        if (this.mBluetoothAdapter == null || this.mBluetoothGatt == null) {
            return false;
        }
        BluetoothGattService RxService = this.mBluetoothGatt.getService(UUID.fromString(serviceUUID));
        if (RxService == null) {
            return false;
        }
        BluetoothGattCharacteristic TxChar = RxService.getCharacteristic(UUID.fromString(characteristicUUID));
        if (TxChar == null) {
            return false;
        }
        boolean status = this.mBluetoothGatt.setCharacteristicNotification(TxChar, enabled);
        return status;
    }


    public static byte[] getSelfBlueMac(Context ctx) {
        String MacStr = "";
        if (PrefUtils.getString(ctx, "bindMax", "").equals("")) {
            if (Build.VERSION.SDK_INT >= 23) {
                MacStr = getMac();
            } else {
                MacStr = BluetoothAdapter.getDefaultAdapter().getAddress();
            }
            PrefUtils.setString(ctx, "bindMax", MacStr);
        } else {
            MacStr = PrefUtils.getString(ctx, "bindMax", "");
        }
        if (MacStr.equals("")) {
            return null;
        }
        String[] Mac = MacStr.split(":");
        return new byte[]{(byte) Integer.parseInt(Mac[0], 16), (byte) Integer.parseInt(Mac[1], 16), (byte) Integer.parseInt(Mac[2], 16), (byte) Integer.parseInt(Mac[3], 16), (byte) Integer.parseInt(Mac[4], 16), (byte) Integer.parseInt(Mac[5], 16)};
    }

    public static String getMac() {
        String str = "";
        String macSerial = "";
        try {
            LineNumberReader input = new LineNumberReader(new InputStreamReader(Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ").getInputStream()));
            while (str != null) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim();
                    break;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return macSerial;
    }
}
