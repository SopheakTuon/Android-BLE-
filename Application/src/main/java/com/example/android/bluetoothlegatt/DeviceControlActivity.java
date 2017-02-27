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

package com.example.android.bluetoothlegatt;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import com.example.android.bluetoothlegatt.ble.BleServiceHelper;
import com.example.android.bluetoothlegatt.ble.WriteToDevice;
import com.example.android.bluetoothlegatt.util.HexUtil;
import com.example.android.bluetoothlegatt.util.MultiByteCommand;
import com.example.android.bluetoothlegatt.util.SingleByteCommand;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Activity
 * communicates with {@code BluetoothLeService}, which in turn interacts with the
 * Bluetooth LE API.
 */
public class DeviceControlActivity extends Activity {
    private final static String TAG = DeviceControlActivity.class.getSimpleName();

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private TextView mConnectionState;
    private TextView mDataField;
    private String mDeviceName;
    private String mDeviceAddress;
    private ExpandableListView mGattServicesList;
    private BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<>();
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    private Button buttonECG, buttonPW, buttonHeart;

    private boolean isMeasuring;

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };


    List<Float> ecgdataallList = new ArrayList<>();
    private List<String> ecgdataSaveStr = new ArrayList<>();
    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            } else if (GlobalData.ACTION_MAIN_DATA_ECGALLDATA.equals(action)) {
                String ecg = intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
                Log.d("ECG", ecg);
                if (ecg != null) {
                    if (time < TIME_DONE) {
                        if (ecgdataallList.size() == 0) {
                            ecgdataallList.add(Float.valueOf(parseEcgdata(ecg.substring(48))));
                            ecgdataallList.add(Float.valueOf(parseEcgdata(ecg.substring(36, 47))));
                            ecgdataallList.add(Float.valueOf(parseEcgdata(ecg.substring(24, 35))));
                            ecgdataallList.add(Float.valueOf(parseEcgdata(ecg.substring(12, 23))));
                            ecgdataallList.add(Float.valueOf(parseEcgdata(ecg.substring(0, 11))));
                            listToEcgAlg2();
                        } else {
                            ecgdataallList.add(0, Float.valueOf(parseEcgdata(ecg.substring(0, 11))));
                            ecgdataallList.add(0, Float.valueOf(parseEcgdata(ecg.substring(12, 23))));
                            ecgdataallList.add(0, Float.valueOf(parseEcgdata(ecg.substring(24, 35))));
                            ecgdataallList.add(0, Float.valueOf(parseEcgdata(ecg.substring(36, 47))));
                            ecgdataallList.add(0, Float.valueOf(parseEcgdata(ecg.substring(48))));
                            listToEcgAlg2();
                            float ecgNumber1 = ((Float) ecgdataallList.get(0)).floatValue();
                            float ecgNumber2 = ((Float) ecgdataallList.get(1)).floatValue();
                            float ecgNumber3 = ((Float) ecgdataallList.get(2)).floatValue();
                            float ecgNumber4 = ((Float) ecgdataallList.get(3)).floatValue();
                            float ecgNumber5 = ((Float) ecgdataallList.get(4)).floatValue();
                            String ecgNumber1Str = String.valueOf((int) ecgNumber1);
                            String ecgNumber2Str = String.valueOf((int) ecgNumber2);
                            String ecgNumber3Str = String.valueOf((int) ecgNumber3);
                            String ecgNumber4Str = String.valueOf((int) ecgNumber4);
                            String ecgNumber5Str = String.valueOf((int) ecgNumber5);
                            ecgdataSaveStr.add(0, new StringBuilder(String.valueOf(ecgNumber5Str)).append(",").append(ecgNumber4Str).append(",").append(ecgNumber3Str).append(",").append(ecgNumber2Str).append(",").append(ecgNumber1Str).append(",").toString());
                        }
                        displayData(ecg);
                    } else if (time == TIME_DONE && isMeasuring) {
                        String ecgString = "[";
                        for (int i = 0; i < ecgdataallList.size(); i++) {
                            if (i == ecgdataallList.size() - 1) {
                                ecgString += ecgdataallList.get(i) + "]";
                            } else {
                                ecgString += ecgdataallList.get(i) + ", ";
                            }
                        }
                        stopMeasureECG();
                        Log.d("ECG", ecgString);
                        displayData(ecgString);
                        enableElements(true);
                    }

                }
            }
        }
    };
    private int time = 0;
    public static final int TIME_DONE = 120;

    private void listToEcgAlg2() {
        int[] result = new int[5];
        if (this.ecgdataallList != null && this.ecgdataallList.size() != 0) {
            time++;
            Log.d("sqs", "time ==== " + time);
            for (int i = 0; i < 5; i++) {
                result[i] = (int) ((Float) this.ecgdataallList.get(i)).floatValue();
            }
//            int[] hpass_Filter = this.data_process.Hpass_Filter(5, this.data_process.filters(5, result));
//            Log.d("sqs", "hpass_Filter" + hpass_Filter[2]);
//            int[] wave_Show = this.data_process.Wave_Show(5, hpass_Filter, time);
//            Log.d("sqs", "wave_Show" + wave_Show[2]);
//            for (int j = 0; j < 5; j++) {
//                ecgdataallList2.add(0, Float.valueOf((float) wave_Show[j]));
//            }
        }
    }

    // If a given GATT characteristic is selected, check for supported features.  This sample
    // demonstrates 'Read' and 'Notify' features.  See
    // http://d.android.com/reference/android/bluetooth/BluetoothGatt.html for the complete
    // list of supported characteristic features.
    private final ExpandableListView.OnChildClickListener servicesListClickListner =
            new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                            int childPosition, long id) {
                    if (mGattCharacteristics != null) {
                        final BluetoothGattCharacteristic characteristic =
                                mGattCharacteristics.get(groupPosition).get(childPosition);
                        final int charaProp = characteristic.getProperties();
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                            // If there is an active notification on a characteristic, clear
                            // it first so it doesn't update the data field on the user interface.
                            if (mNotifyCharacteristic != null) {
                                mBluetoothLeService.setCharacteristicNotification(mNotifyCharacteristic, false);
                                mNotifyCharacteristic = null;
                            }
                            mBluetoothLeService.readCharacteristic(characteristic);
                        }
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                            mNotifyCharacteristic = characteristic;
                            mBluetoothLeService.setCharacteristicNotification(characteristic, true);
                        }
                        return true;
                    }
                    return false;
                }
            };

    protected float parsePwdata(String string) {
        String hex = string.substring(9, 11) + string.substring(6, 8) + string.substring(3, 5) + string.substring(0, 2);
        Log.d("sqs", "hex:  " + hex);
        byte[] hexStringToBytes = HexUtil.hexStringToBytes(hex);
        Log.d("sqs", "PPG+bytesToString:  " + HexUtil.bytesToHexString(hexStringToBytes));
        int bytesToInt = HexUtil.getInt(hexStringToBytes, false, 4);
        Log.d("sqs", "PPG+bytesToInt" + bytesToInt);
        return (float) bytesToInt;
    }

    protected float parseEcgdata(String string) {
        return (float) new BigInteger(string.substring(9, 11) + string.substring(6, 8) + string.substring(3, 5) + string.substring(0, 2), 16).intValue();
    }

    private void clearUI() {
        mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
        mDataField.setText(R.string.no_data);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gatt_services_characteristics);

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        // Sets up UI references.
        ((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);
        mGattServicesList = (ExpandableListView) findViewById(R.id.gatt_services_list);
        mGattServicesList.setOnChildClickListener(servicesListClickListner);
        mConnectionState = (TextView) findViewById(R.id.connection_state);
        mDataField = (TextView) findViewById(R.id.data_value);

        getActionBar().setTitle(mDeviceName);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        init();
        initEventListener();


    }

    private void enableElements(boolean enable) {
        buttonECG.setEnabled(enable);
        buttonPW.setEnabled(enable);
        buttonHeart.setEnabled(enable);
    }

    private String byeteToString(byte[] bytes) {
        String s = "[";
        for (int i = 0; i < bytes.length; i++) {
            if (i == bytes.length - 1) {
                s += bytes[i] + "]";
            } else {
                s += bytes[i] + ",";
            }

        }
        return s;
    }


    private void init() {
        buttonECG = (Button) findViewById(R.id.buttonPair);
        buttonPW = (Button) findViewById(R.id.buttonTurnOff);
        buttonHeart = (Button) findViewById(R.id.buttonHeart);
    }

    private void initEventListener() {
        buttonECG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMeasuring) {
                    stopMeasureECG();
                    enableElements(true);
                } else {
                    isMeasuring = measureECG() == 1;
                    enableElements(false);
                }
            }
        });

        buttonPW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                measurePW();
            }
        });
        buttonHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unBindDevice();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_connect:
                mBluetoothLeService.connect(mDeviceAddress);
                return true;
            case R.id.menu_disconnect:
                mBluetoothLeService.disconnect();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
            }
        });
    }

    private void displayData(final String data) {
        if (data != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mDataField.setText(data);
                }
            });
        }
    }

    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView
    // on the UI.
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
//        String uuid = null;
//        String unknownServiceString = getResources().getString(R.string.unknown_service);
//        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
//        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<>();
//        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData = new ArrayList<>();
//        mGattCharacteristics = new ArrayList<>();
////        String allServiceUUID = "";
//        // Loops through available GATT Services.
//        for (BluetoothGattService gattService : gattServices) {
//            HashMap<String, String> currentServiceData = new HashMap<>();
//            uuid = gattService.getUuid().toString();
////            Log.d("Service", "UUID : " + uuid + "\n" + "Type : " + gattService.getType() + "\n" + "InstanceId : " + gattService.getInstanceId());
////            allServiceUUID += uuid + "\n";
//            currentServiceData.put(LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
//            currentServiceData.put(LIST_UUID, uuid);
//            gattServiceData.add(currentServiceData);
//
//            ArrayList<HashMap<String, String>> gattCharacteristicGroupData = new ArrayList<>();
//            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
//            ArrayList<BluetoothGattCharacteristic> charas = new ArrayList<>();
//
////            String stringChar = "Service UUID  = " + uuid + "\n";
//            // Loops through available Characteristics.
//            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
//                charas.add(gattCharacteristic);
//                HashMap<String, String> currentCharaData = new HashMap<>();
//                uuid = gattCharacteristic.getUuid().toString();
////                stringChar += uuid + "\n";
//                currentCharaData.put(LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
//                currentCharaData.put(LIST_UUID, uuid);
//                gattCharacteristicGroupData.add(currentCharaData);
//            }
////            Log.d("UUID", stringChar);
//            mGattCharacteristics.add(charas);
//            gattCharacteristicData.add(gattCharacteristicGroupData);
//        }
//
//        SimpleExpandableListAdapter gattServiceAdapter = new SimpleExpandableListAdapter(
//                this,
//                gattServiceData,
//                android.R.layout.simple_expandable_list_item_2,
//                new String[]{LIST_NAME, LIST_UUID},
//                new int[]{android.R.id.text1, android.R.id.text2},
//                gattCharacteristicData,
//                android.R.layout.simple_expandable_list_item_2,
//                new String[]{LIST_NAME, LIST_UUID},
//                new int[]{android.R.id.text1, android.R.id.text2}
//        );
//        mGattServicesList.setAdapter(gattServiceAdapter);
//        Log.d("UUID", allServiceUUID);

        Log.d("Mac", "" + WriteToDevice.bytesToHexString(BleServiceHelper.getSelfBlueMac(DeviceControlActivity.this)));

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                matchInfo();
                bind();
//                updateTime();
                secondMatch();
            }
        });
    }

    private void matchInfo() {
        MultiByteCommand.matchInfo(mBluetoothLeService.getmBluetoothGatt(), WriteToDevice.bytesToHexString(BleServiceHelper.getSelfBlueMac(DeviceControlActivity.this)));
    }

    private void bind() {
        MultiByteCommand.ackForBindRequest(mBluetoothLeService.getmBluetoothGatt(), 1);
    }

    private void secondMatch() {
        MultiByteCommand.secondMach(mBluetoothLeService.getmBluetoothGatt(), 1);
    }

    private void updateTime() {
        MultiByteCommand.UpdateNewTime(mBluetoothLeService.getmBluetoothGatt());
    }

    private void unBindDevice() {
        SingleByteCommand.unbindDevice(mBluetoothLeService.getmBluetoothGatt());
    }


    private void startPairDevice() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                matchInfo();

            }
        });
    }

    private int measureECG() {
        return SingleByteCommand.measureECG(mBluetoothLeService.getmBluetoothGatt());
    }

    private int stopMeasureECG() {
        time = 0;
        isMeasuring = false;
        return SingleByteCommand.stopMeasuring(mBluetoothLeService.getmBluetoothGatt());
    }

    private void measurePW() {
        SingleByteCommand.measurePW(mBluetoothLeService.getmBluetoothGatt());
    }


    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(GlobalData.ACTION_MAIN_DATA_ECGALLDATA);
        return intentFilter;
    }


    public interface OnBondListener {
        void onBond(boolean z);
    }

    /* renamed from: com.seedmorn.w22androidapp.activity.MainActivity.7 */
    class BleOnBondListener implements OnBondListener {

        /* renamed from: com.seedmorn.w22androidapp.activity.MainActivity.7.1 */
        class C03481 implements Runnable {
            C03481() {
            }

            public void run() {
//                Log.i(MainActivity.TAG, "\u66f4\u65b0\u65f6\u95f4\u5931\u8d25\uff0c\u518d\u6b21\u66f4\u65b0\u65f6\u95f4");
                WriteToDevice.UpdateNewTime(DeviceControlActivity.this);
            }
        }

        /* renamed from: com.seedmorn.w22androidapp.activity.MainActivity.7.2 */
        class HeartRunnable implements Runnable {
            HeartRunnable() {
            }

            public void run() {
                BleServiceHelper.heartPackage(DeviceControlActivity.this);
            }
        }

        BleOnBondListener() {
        }

        public void onBond(boolean result) {
            if (result) {
                BleServiceHelper.sendBroadcast(DeviceControlActivity.this, GlobalData.ACTION_GATT_SUCCESS_CONN, true);
                GlobalData.status_Connecting = false;
                GlobalData.status_Connected = true;
                String token = "";
//                token = PrefUtils.getString(MainActivity.mApplication, GlobalData.SHARED_PREFRENCE_DECRY_TOKEN, "");
                if (!"".equals(token)) {
                    byte[] decode = Base64.decode(token.getBytes(), 1);
                    String decodeStr = null;
                    for (byte append : decode) {
                        decodeStr = new StringBuilder(String.valueOf(decodeStr)).append(append).append(" ").toString();
                    }
                    Log.e("sqs", "decode \u957f\u5ea61111111111:" + decodeStr);
                    int startSendDecryToken = WriteToDevice.startSendDecryToken(DeviceControlActivity.this, decode.length);
                    WriteToDevice.sendDecryTokenContent1(DeviceControlActivity.this, decode);
                }
                int done = WriteToDevice.UpdateNewTime(DeviceControlActivity.this);
                Handler handler = new Handler();
                if (done == -1) {
                    handler.postDelayed(new C03481(), 200);
                }
//                PrefUtils.setString(MainActivity.mApplication, GlobalData.DEVICE_TARGET_MAC, GlobalData.TOP_MAC);
                handler.postDelayed(new HeartRunnable(), 500);
            } else if (DeviceControlActivity.this != null) {
//                UIToastUtil.setToast(MainActivity.mActivity, MainActivity.mActivity.getResources().getString(C0328R.string.ble_bind_bind_failures));
            }
        }
    }

}
