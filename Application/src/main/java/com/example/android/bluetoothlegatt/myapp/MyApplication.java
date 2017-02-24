package com.example.android.bluetoothlegatt.myapp;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.example.android.bluetoothlegatt.BufferRecycler;
import com.example.android.bluetoothlegatt.DeviceItem;
import com.example.android.bluetoothlegatt.GlobalData;
import com.example.android.bluetoothlegatt.PrefUtils;
import com.example.android.bluetoothlegatt.ble.BleServiceHelper;
import com.example.android.bluetoothlegatt.ble.LinkBleDevice;
import com.example.android.bluetoothlegatt.ble.WriteToDevice;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

/**
 * @author Sopheak Tuon
 * @created on 24-Feb-17
 */

public class MyApplication extends Application {
        public static int Application_Vip = 0;
        static Timer MachTimer = null;
        private static final String TAG;
        private static Runnable ackTime = null;
        private static int count_TickTock = 0;
        public static boolean isNetConn = false;
        public static boolean isNewUser = false;
        public static LinkBleDevice linkDevice = null;
        private static Handler mAckHandler = null;
        public static MyApplication mApplication = null;
        public static BluetoothAdapter mBluetoothAdapter = null;
        private static BluetoothAdapter.LeScanCallback mLeScanCallback = null;
        public static List<DeviceItem> mList_Devices = null;
        private static Handler mMatchHandler = null;
        private static BroadcastReceiver mReceiver = null;
        private static Handler mScanHandler = null;
        private static Runnable mStopLeScan = null;
        private static Handler mTimeHandler = null;
        private static Runnable matchTime = null;
        private static BroadcastReceiver myGattUpdateReceiver = null;
        private static Runnable ticktockTime = null;
        private static BroadcastReceiver timeTickReceiver = null;
        private static final int timeout = 5000;
        private static Handler upDateTimeHandler;
        private static BroadcastReceiver upFirmReceiver;
        private static Runnable updateTime;
        private ConnectivityManager connManager;
        private Process process;
        private Timer timer;
//        private Worker worker;

        /* renamed from: com.worldgn.helo.MyApplication.1 */
        static class C06481 extends BroadcastReceiver {
            C06481() {
            }

            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (GlobalData.ACTION_SERVICE_GATT_CONNECTED.equals(action)) {
                    BleServiceHelper.sendBroadcast(context, GlobalData.DEVICE_CONN);
                } else if (GlobalData.ACTION_SERVICE_GATT_DISCONNECTED.equals(action)) {
                    MyApplication.toCloseAndReSearch();
                } else if (GlobalData.ACTION_SERVICE_GATT_DISCOVERED.equals(action)) {
                    MyApplication.stopAck();
                    GlobalData.status_Connected = true;
                    BleServiceHelper.sendBroadcast(context, GlobalData.BLE_SERVICE);
                } else if (GlobalData.ACTION_GATT_SOS.equals(action)) {
                    BleServiceHelper.sendBroadcast(context, GlobalData.BLE_SOS);
                } else if (GlobalData.ACTION_GATT_DEVICE_MATCH_ACK.equals(action)) {
                    BleServiceHelper.sendBroadcast(MyApplication.mApplication, GlobalData.CLOSE_SEARCH_DIALOG);
                    BleServiceHelper.sendBroadcast(context, GlobalData.BLE_MATCH, intent.getLongExtra(action, -1));
                } else if (GlobalData.ACTION_GATT_DEVICE_BIND_REQUEST.equals(action)) {
                    BleServiceHelper.sendBroadcast(context, GlobalData.BLE_BOND_REQUEST);
                } else if ((GlobalData.BLUETOOTH_STATE_CHANGED.equals(action) || GlobalData.BLUETOOTH_ACTION.equals(action)) && !GlobalData.isRequest) {
                    MyApplication.switchServiceByBleStatus();
                }
            }
        }

        /* renamed from: com.worldgn.helo.MyApplication.2 */
        static class C06492 implements BluetoothAdapter.LeScanCallback {
            C06492() {
            }

            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                if (!GlobalData.status_Scanning) {
                    Log.i(MyApplication.TAG, "506 GlobalData.isScanning = " + GlobalData.status_Scanning);
                } else if (rssi < -100) {
                    Log.i(MyApplication.TAG, "510 \u5c11\u4e8e-100\u7684\u4fe1\u53f7\u5254\u9664 rssi = " + rssi);
                } else if (device.getName() == null) {
                    Log.i(MyApplication.TAG, "514 device.getName() = " + device.getName());
                } else {
                    Log.i(MyApplication.TAG, "522 \u641c\u7d22\u5230\u8bbe\u5907: MAC = " + device.getAddress() + " NAME = " + device.getName());
                    String MAC_SP = PrefUtils.getString(MyApplication.mApplication, GlobalData.DEVICE_TARGET_MAC, "");
                    Log.i(MyApplication.TAG, "\u76ee\u6807\u8bbe\u5907MAC\uff1a = " + MAC_SP);
                    if (!MAC_SP.equals("")) {
                        Log.i(MyApplication.TAG, "551 \u7ed1\u5b9a\u8fc7\u8bbe\u5907 \u5f00\u59cb\u68c0\u67e5\u5f53\u524d\u641c\u7d22\u5230\u7684\u8bbe\u5907\u662f\u5426\u4e3a\u6307\u5b9a\u76ee\u6807");
                        if (!MAC_SP.equals(device.getAddress())) {
                            Log.i(MyApplication.TAG, "690 \u641c\u7d22\u5230\u8bbe\u5907\u4e0d\u662f\u7ed1\u5b9a\u7684\u76ee\u6807\u8bbe\u5907--\u7ee7\u7eed\u641c\u7d22");
                        } else if (device.getName().equals(GlobalData.DEVICE_NAME) || device.getName().equals(GlobalData.DEVICE_SUBNAME1) || device.getName().equals(GlobalData.DEVICE_SUBNAME2)) {
                            Log.i(MyApplication.TAG, "679 \u5f53\u524d\u641c\u7d22\u5230\u7684\u8bbe\u5907\u4e3a\u76ee\u6807\u8bbe\u5907  \u5f00\u59cb\u8fde\u63a5\u5e76\u5173\u95ed\u641c\u7d22");
                            GlobalData.TOP_MAC = device.getAddress();
                            GlobalData.status_Connecting = true;
                            MyApplication.stopScanLeDevice();
                            BleServiceHelper.sendBroadcast(MyApplication.mApplication, GlobalData.BLE_CONNECTING);
                            MyApplication.mList_Devices.clear();
                        } else {
                            Log.i(MyApplication.TAG, "686 \u5f53\u524d\u641c\u7d22\u5230\u7684\u8bbe\u5907\u4e0d\u662f\u76ee\u6807\u8bbe\u5907--\u7ee7\u7eed\u641c\u7d22");
                        }
                    } else if (device.getName().equals(GlobalData.DEVICE_NAME) || device.getName().equals(GlobalData.DEVICE_SUBNAME1) || device.getName().equals(GlobalData.DEVICE_SUBNAME2)) {
                        Log.i(MyApplication.TAG, "695 \u672a\u7ed1\u5b9a\u8bbe\u5907 \u641c\u7d22\u5230\u7684\u8bbe\u5907\u4e3a\u65b0\u7684\u8bbe\u5907 \u52a0\u5165\u641c\u7d22\u5230\u7684\u8bbe\u5907\u5217\u8868");
                        DeviceItem item = new DeviceItem();
                        item.setDeviceMac(device.getAddress());
                        item.setRssi(rssi);
                        MyApplication.mList_Devices.add(item);
                        GlobalData.TOP_MAC = "";
                    }
                }
            }
        }

        /* renamed from: com.worldgn.helo.MyApplication.3 */
        static class C06503 implements Runnable {
            C06503() {
            }

            public void run() {
                Log.d(MyApplication.TAG, "583 \u65f6\u95f4\u5230  \u626b\u63cf\u65f6\u95f4\u5230 --\u300b\u7ed3\u675f  mBluetoothAdapter.getScanMode() " + MyApplication.mBluetoothAdapter.getScanMode());
                MyApplication.stopScanLeDevice();
            }
        }

        /* renamed from: com.worldgn.helo.MyApplication.4 */
        static class C06514 implements Runnable {
            C06514() {
            }

            public void run() {
                MyApplication.upDateTimeHandler.removeCallbacks(MyApplication.updateTime);
                if (!GlobalData.status_Connected || !GlobalData.isFirmWorkVersionInfo) {
                    Log.i("sqs", "\u540c\u6b65\u65f6\u95f4\u5ef6\u65f6\u5e94\u7b54\u5230  \u8bbe\u5907\u5339\u914d\u5931\u8d25 \u65ad\u5f00\u91cd\u641c");
                    BleServiceHelper.sendBroadcast(MyApplication.mApplication, GlobalData.DEVICE_DISCONN);
                    MyApplication.linkDevice.unBindBleService(MyApplication.mApplication);
                    boolean stopBleService = MyApplication.linkDevice.stopBleService(MyApplication.mApplication);
                    Log.i(MyApplication.TAG, "609 \u5173\u95ed\u84dd\u7259\u8fd4\u56de\u7ed3\u679c stopBleService = " + stopBleService);
                    if (stopBleService) {
                        GlobalData.status_Connecting = false;
                        GlobalData.status_Connected = false;
                        BleServiceHelper.sendBroadcast(MyApplication.mApplication, GlobalData.BLE_RESEARCH);
                    }
                }
            }
        }

        /* renamed from: com.worldgn.helo.MyApplication.5 */
        static class C06525 implements Runnable {
            C06525() {
            }

            public void run() {
                MyApplication.mMatchHandler.removeCallbacks(MyApplication.matchTime);
                if (GlobalData.status_Connected && GlobalData.isMatchInfo) {
                    WriteToDevice.secondMach(MyApplication.mApplication, 0);
                    return;
                }
                Log.i("sqs", "605 \u8bbe\u5907\u5339\u914d\u5ef6\u65f6\u65f6\u95f4\u5230  \u8bbe\u5907\u5339\u914d\u5931\u8d25 \u65ad\u5f00\u91cd\u641c");
                BleServiceHelper.sendBroadcast(MyApplication.mApplication, GlobalData.CHANGE_BATTERY_STATUS);
                MyApplication.linkDevice.unBindBleService(MyApplication.mApplication);
                boolean stopBleService = MyApplication.linkDevice.stopBleService(MyApplication.mApplication);
                Log.i(MyApplication.TAG, "609 \u5173\u95ed\u84dd\u7259\u8fd4\u56de\u7ed3\u679c stopBleService = " + stopBleService);
                if (stopBleService) {
                    GlobalData.status_Connecting = false;
                    GlobalData.status_Connected = false;
                    BleServiceHelper.sendBroadcast(MyApplication.mApplication, GlobalData.BLE_RESEARCH);
                }
            }
        }

        /* renamed from: com.worldgn.helo.MyApplication.6 */
        static class C06536 implements Runnable {
            C06536() {
            }

            public void run() {
                Log.i(MyApplication.TAG, "\u84dd\u7259\u8fde\u63a5\u65f6\u95f4\u5230 ble connecting time out...");
                BleServiceHelper.sendBroadcast(MyApplication.mApplication, GlobalData.CHANGE_BATTERY_STATUS);
                MyApplication.linkDevice.unBindBleService(MyApplication.mApplication);
                boolean stopBleService = MyApplication.linkDevice.stopBleService(MyApplication.mApplication);
                Log.i(MyApplication.TAG, "586 \u5173\u95ed\u84dd\u7259\u8fd4\u56de\u7ed3\u679c stopBleService = " + stopBleService);
                if (stopBleService) {
                    GlobalData.status_Connecting = false;
                    GlobalData.status_Connected = false;
                    BleServiceHelper.sendBroadcast(MyApplication.mApplication, GlobalData.BLE_RESEARCH);
                }
            }
        }

        /* renamed from: com.worldgn.helo.MyApplication.7 */
        static class C06547 implements Runnable {
            C06547() {
            }

            public void run() {
                MyApplication.count_TickTock = MyApplication.count_TickTock + 1;
                Log.d(MyApplication.TAG, "\u56fa\u4ef6\u66f4\u65b0\u6b21\u6570\uff1a" + MyApplication.count_TickTock);
                if (MyApplication.count_TickTock == 60) {
                    BleServiceHelper.sendBroadcast(MyApplication.mApplication, GlobalData.CLOSE_UPFIRMWARE_DIALOG);
                    if (GlobalData.status_Connected) {
                        BleServiceHelper.sendBroadcast(MyApplication.mApplication, GlobalData.ACTION_GATT_SUCCESS_CONN, true);
                    }
                    MyApplication.stopTickTock();
                } else if (MyApplication.count_TickTock < 60) {
                    BleServiceHelper.sendBroadcast(MyApplication.mApplication, GlobalData.FIRMWARE_UPING_PROGRESSBAR, MyApplication.count_TickTock);
                    MyApplication.stopTickTock();
                    MyApplication.startTickTock();
                }
            }
        }

        /* renamed from: com.worldgn.helo.MyApplication.8 */
        static class C06568 extends BroadcastReceiver {

            /* renamed from: com.worldgn.helo.MyApplication.8.1 */
            class C06551 implements Runnable {
                C06551() {
                }

                public void run() {
                    MyApplication.stopWaitDialog_UpFirmware();
                }
            }

            C06568() {
            }

            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (GlobalData.ACTION_UP_FIRMWARE_COMPLETE.equals(action)) {
                    Log.i(MyApplication.TAG, "738  \u56fa\u4ef6\u66f4\u65b0\u5b8c\u6210\u5e7f\u64ad");
                    GlobalData.isOnFirmwareUpdating = true;
                    MyApplication.showWaitDialog_UpFirmware();
                } else if (GlobalData.ACTION_MAIN_DATA_FIRM_FAULT.equals(action)) {
                    GlobalData.isOnFirmwareUpdating = false;
                    Log.i(MyApplication.TAG, "\u56fa\u4ef6\u66f4\u65b0\u5931\u8d25");
                    BleServiceHelper.sendBroadcast(MyApplication.mApplication, GlobalData.FIRMWARE_UPDATE_FAILED);
                    new Handler().postDelayed(new C06551(), 3000);
                } else if (GlobalData.ACTION_MAIN_DATA_FIRM_SUCCESS.equals(action)) {
                    GlobalData.isOnFirmwareUpdating = false;
                    PrefUtils.setString(MyApplication.mApplication, "version_firmware", "");
                    BleServiceHelper.sendBroadcast(MyApplication.mApplication, GlobalData.FIRMWARE_UPDATE_SUCESS);
                    Log.i(MyApplication.TAG, "\u56fa\u4ef6\u66f4\u65b0OK");
                }
            }
        }

        /* renamed from: com.worldgn.helo.MyApplication.9 */
//        class C06609 extends BroadcastReceiver {
//
//            /* renamed from: com.worldgn.helo.MyApplication.9.3 */
//            class C06573 implements Runnable {
//                private final /* synthetic */ GetLatAndLng val$mGetLatAndLng;
//
//                C06573(GetLatAndLng getLatAndLng) {
//                    this.val$mGetLatAndLng = getLatAndLng;
//                }
//
//                public void run() {
//                    this.val$mGetLatAndLng.getLatAndLng();
//                }
//            }
//
//            /* renamed from: com.worldgn.helo.MyApplication.9.4 */
//            class C06584 implements Runnable {
//                private final /* synthetic */ Context val$context;
//                private final /* synthetic */ GetLatAndLng val$mGetLatAndLng;
//
//                C06584(GetLatAndLng getLatAndLng, Context context) {
//                    this.val$mGetLatAndLng = getLatAndLng;
//                    this.val$context = context;
//                }
//
//                public void run() {
//                    this.val$mGetLatAndLng.startGPSListener(this.val$context);
//                }
//            }
//
//            /* renamed from: com.worldgn.helo.MyApplication.9.5 */
//            class C06595 implements Runnable {
//                private final /* synthetic */ GetLatAndLng val$mGetLatAndLng;
//
//                C06595(GetLatAndLng getLatAndLng) {
//                    this.val$mGetLatAndLng = getLatAndLng;
//                }
//
//                public void run() {
//                    Log.d("sqs", "MyApplication...\u5f00\u59cb\u79fb\u9664gps\u76d1\u542c");
//                    this.val$mGetLatAndLng.removeGPSListener();
//                }
//            }
//
//            /* renamed from: com.worldgn.helo.MyApplication.9.1 */
//            class C09691 implements RequestTaskListener<JSONObject> {
//                private final /* synthetic */ Context val$context;
//
//                C09691(Context context) {
//                    this.val$context = context;
//                }
//
//                public void onRequestSuccess(JSONObject t, int requestCode) {
//                    if (requestCode == 0) {
//                        Log.i("sqs", "\u79bb\u7ebf\u6570\u636e\u4e0a\u4f20\u6210\u529f  onRequestSuccess and the json = ");
//                        PrefUtils.setString(this.val$context, "offline_data_json_string", "");
//                    }
//                }
//
//                public void onRequestStart(int requestCode) {
//                }
//
//                public void onRequestLoading(int requestCode, long current, long count) {
//                }
//
//                public void onRequestFinish() {
//                }
//
//                public void onRequestFail(int requestCode) {
//                    if (requestCode == 0) {
//                        Log.i(MyApplication.TAG, "\u79bb\u7ebf\u6570\u636e\u4e0a\u4f20\u5931\u8d25 offline data upload failed! the requestCode is " + requestCode);
//                    }
//                }
//            }
//
//            /* renamed from: com.worldgn.helo.MyApplication.9.2 */
//            class C09702 implements RequestTaskListener<JSONObject> {
//                private final /* synthetic */ Context val$context;
//
//                C09702(Context context) {
//                    this.val$context = context;
//                }
//
//                public void onRequestStart(int requestCode) {
//                    if (requestCode == 1001) {
//                        Log.d("sqs", "\u5f00\u59cbvip\u8d44\u683c\u83b7\u53d6....");
//                    }
//                }
//
//                public void onRequestLoading(int requestCode, long current, long count) {
//                }
//
//                public void onRequestSuccess(JSONObject t, int requestCode) {
//                    if (requestCode == 1001) {
//                        Log.d("sqs", "vip\u8d44\u683c\u83b7\u53d6\u6210\u529f\uff01\uff01" + t);
//                        if (t != null) {
//                            try {
//                                if ("1".equals(t.getString("success"))) {
//                                    String wecare = t.getString("wecare");
//                                    String flagTrial = t.optString("flagTrial");
//                                    if ("1".equals(wecare)) {
//                                        PrefUtils.setInt(this.val$context, "VIP_HELO_WORLD_ACCOUNT", 1);
//                                        MyApplication.Application_Vip = 1;
//                                    } else if ("0".equals(wecare)) {
//                                        PrefUtils.setInt(this.val$context, "VIP_HELO_WORLD_ACCOUNT", 0);
//                                        MyApplication.Application_Vip = 0;
//                                    }
//                                    if ("1".equals(flagTrial)) {
//                                        PrefUtils.setInt(this.val$context, "VIP_HELO_WORLD_ACCOUNT_FLAGTRIAL", 1);
//                                    } else if ("0".equals(flagTrial)) {
//                                        PrefUtils.setInt(this.val$context, "VIP_HELO_WORLD_ACCOUNT_FLAGTRIAL", 0);
//                                    }
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                }
//
//                public void onRequestFail(int requestCode) {
//                    if (requestCode == 1001) {
//                        Log.d("sqs", "vip\u8d44\u683c\u83b7\u53d6\u5931\u8d25\uff01\uff01");
//                    }
//                }
//
//                public void onRequestFinish() {
//                }
//            }
//
//            /* renamed from: com.worldgn.helo.MyApplication.9.6 */
//            class C09716 implements RequestTaskListener<JSONObject> {
//                private final /* synthetic */ Context val$context;
//                private final /* synthetic */ long val$currentTimeMillis;
//
//                C09716(Context context, long j) {
//                    this.val$context = context;
//                    this.val$currentTimeMillis = j;
//                }
//
//                public void onRequestStart(int requestCode) {
//                }
//
//                public void onRequestLoading(int requestCode, long current, long count) {
//                }
//
//                public void onRequestSuccess(JSONObject t, int requestCode) {
//                    if (requestCode == 1002 && t != null) {
//                        try {
//                            if (t.getInt(HttpNetworkAccess.RESPONSE_STATUS_CODE) == 1) {
//                                String token = t.getJSONArray(HttpNetworkAccess.RESPONSE_DATA).getJSONObject(0).getString("decryptionToken");
//                                if (token != null) {
//                                    PrefUtils.setString(this.val$context, GlobalData.SHARED_PREFRENCE_DECRY_TOKEN, token);
//                                    PrefUtils.setLong(this.val$context, GlobalData.SHARED_PREFRENCE_DECRY_TIME, this.val$currentTimeMillis);
//                                    Log.d("sqs", "start sqfw \u5f53\u524d\u65f6\u95f4\u5b58\u5165sp :" + this.val$currentTimeMillis);
//                                }
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//
//                public void onRequestFail(int requestCode) {
//                }
//
//                public void onRequestFinish() {
//                }
//            }
//
//            C06609() {
//            }
//
//            public void onReceive(Context context, Intent intent) {
//                if (intent.getAction().equals("android.intent.action.TIME_TICK")) {
//                    Log.i(MyApplication.TAG, "************************* \u672c\u5730\u79bb\u7ebf\u6570\u636e\u4e0a\u4f20 *******************************");
//                    String data_offline = PrefUtils.getString(context, "offline_data_json_string", "");
//                    Log.i("sqs", "\u79bb\u7ebf\u6570\u636e\u4e0a\u4f20\u4fdd\u5b58\u7684\u51c6\u5907\u4e0a\u4f20 it have offline data:");
//                    if (TextUtils.isEmpty(data_offline)) {
//                        Log.i(MyApplication.TAG, "\u6ca1\u6709\u79bb\u7ebf\u6570\u636e dont't have offline data");
//                    } else {
//                        NetWorkAccessTools.initNetWorkAccessTools(context).postRequest(HttpUtil.getURLWithActionName("bleOffData.do"), NetWorkAccessTools.getParameterMap(new String[]{"userID", HttpNetworkAccess.RESPONSE_DATA_APPUP}, UserInformationUtils.getUserIDLocal(context), data_offline), null, 0, new C09691(context));
//                    }
//                    Log.d("sqs", "\u63a5\u6536\u5230timetick,\u5f00\u59cb\u5224\u65ad\u8fdb\u884cvip\u67e5\u8be2...");
//                    Calendar cal = Calendar.getInstance();
//                    int hour = cal.get(11);
//                    int min = cal.get(12);
//                    Log.d("sqs", "\u5f00\u59cb\u8f93\u51fa\u65f6\u95f4\u73b0\u5728\u662f:" + hour + "\u70b9," + min + "\u5206!");
//                    if (hour == 12) {
//                        if (!PrefUtils.getString(context, UserInformationUtils.SP_USER_ID_ORIG, "").isEmpty()) {
//                            NetWorkAccessTools.initNetWorkAccessTools(context).postRequest(HttpUtil.URL_ORIG, NetWorkAccessTools.getParameterMap(new String[]{"key_check", "deviceid", "action", "UserIDHelo"}, HttpUtil.getOrigKeyCheck(context), PhoneUtil.getDeviceID(context), "get_flag_we-care", PrefUtils.getString(context, UserInformationUtils.SP_USER_ID_ORIG, "")), null, 1001, new C09702(context));
//                        } else {
//                            return;
//                        }
//                    }
//                    if (min % 30 == 0) {
//                        Log.d("sqs", "\u5f00\u59cb\u8c03\u7528\u5168\u5c40\u83b7\u53d6\u7ecf\u7eac\u5ea6\u903b\u8f91...");
//                        new Thread(new C06573(new GetLatAndLng(context))).start();
//                    }
//                    if (min % 15 == 0) {
//                        GetLatAndLng getLatAndLng = new GetLatAndLng(context);
//                        new Handler().postDelayed(new C06584(getLatAndLng, context), 0);
//                        new Handler().postDelayed(new C06595(getLatAndLng), 600000);
//                    }
//                    Log.d("sqs", "start sqfw \u5f00\u59cb\u68c0\u67e5...");
//                    long currentTimeMillis = System.currentTimeMillis();
//                    if (currentTimeMillis - PrefUtils.getLong(context, GlobalData.SHARED_PREFRENCE_DECRY_TIME, 0) > 86400000) {
//                        Log.d("sqs", "start sqfw \u65f6\u95f4\u5dee\u5927\u4e8eoneDay...");
//                        String macStr = BleServiceHelper.getMac();
//                        NetWorkAccessTools.initNetWorkAccessTools(context).postRequest(HttpUtil.getURLWithActionName("/licenseproject/findlicenseprojectbycache.action"), NetWorkAccessTools.getParameterMap(new String[]{"mac"}, macStr), null, 1002, new C09716(context, currentTimeMillis));
//                        return;
//                    }
//                    Log.d("sqs", "start sqfw \u65f6\u95f4\u5dee\u5c0f\u4e8eoneDay...");
//                }
//            }
//        }

        public MyApplication() {
            this.connManager = null;
            this.process = null;
        }

        static {
            TAG = MyApplication.class.getSimpleName();
            isNewUser = true;
            isNetConn = false;
            mReceiver = null;
            mScanHandler = null;
            mAckHandler = null;
            mMatchHandler = null;
            mTimeHandler = null;
            upDateTimeHandler = null;
            Application_Vip = 0;
            myGattUpdateReceiver = new C06481();
            mLeScanCallback = new C06492();
            mStopLeScan = new C06503();
            updateTime = new C06514();
            matchTime = new C06525();
            ackTime = new C06536();
            count_TickTock = 0;
            ticktockTime = new C06547();
            upFirmReceiver = new C06568();
//            timeTickReceiver = new C06609();
        }

        public static MyApplication getInstance() {
            if (mApplication == null) {
                mApplication = new MyApplication();
            }
            return mApplication;
        }

        public void onCreate() {
            super.onCreate();
//            CrashHandler2Local.getInstance().init(this);
//            JPushInterface.init(this);
            mApplication = this;
            isNewUser = true;
//            this.connManager = (ConnectivityManager) mApplication.getSystemService("connectivity");
            new Thread(new Runnable() {
                public void run() {
                    MyApplication.this.timerTask();
                }
            }).start();
            mList_Devices = new ArrayList();
            if (mScanHandler == null) {
                mScanHandler = new Handler();
            }
            if (mAckHandler == null) {
                mAckHandler = new Handler();
            }
            if (mMatchHandler == null) {
                mMatchHandler = new Handler();
            }
            if (mTimeHandler == null) {
                mTimeHandler = new Handler();
            }
            if (upDateTimeHandler == null) {
                upDateTimeHandler = new Handler();
            }
            linkDevice = LinkBleDevice.getInstance(mApplication);
            registerReceiver();
            if (PrefUtils.getString(mApplication, GlobalData.DEVICE_TARGET_MAC, "").equals("")) {
                Log.i(TAG, "GlobalData.TARGET_MAC = NULL");
                return;
            }
            Log.i(TAG, "\u540e\u53f0\u641c\u7d22\u84dd\u7259  initBleBack GO");
            if (openBle(mApplication)) {
                scanLeDevice(mApplication, true);
            }
        }

        public boolean openBle(Context context) {
            if (GlobalData.status_Scanning) {
                Log.i(TAG, "722 \u626b\u63cf\u8bbe\u5907\u5df2\u7ecf\u5f00\u59cb \u5f00\u59cb\u626b\u63cf\u6307\u4ee4\u4e2d\u65ad the ble scan is already On");
                return true;
            } else if (GlobalData.status_Connecting || GlobalData.status_Connected) {
                return true;
            } else {
                boolean isBleEnabled = isBleEnabled(context);
                GlobalData.isEnabled = isBleEnabled;
                if (!isBleEnabled) {
                    return false;
                }
                if (mBluetoothAdapter.isEnabled()) {
                    return true;
                }
                GlobalData.isRequest = true;
                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (bluetoothAdapter == null) {
                    return false;
                }
                if (bluetoothAdapter.enable()) {
                    GlobalData.isEnabled = true;
                    GlobalData.isRequest = false;
                    GlobalData.status_Connected = false;
                }
                return bluetoothAdapter.enable();
            }
        }

        public void registerReceiver() {
            if (!GlobalData.INIT_AT_BOOT) {
                Log.i(TAG, "/** \u6ce8\u518c\u5e7f\u64ad\u63a5\u6536\u5668  _______________*/" + TAG);
                Log.i(TAG, "/** \u6ce8\u518c\u5e7f\u64ad\u63a5\u6536\u5668  */");
                Log.i(TAG, "/** \u6ce8\u518c\u5e7f\u64ad\u63a5\u6536\u5668  */");
                Log.i(TAG, "/** \u6ce8\u518c\u5e7f\u64ad\u63a5\u6536\u5668  */");
                GlobalData.INIT_AT_BOOT = true;
                IntentFilter filter = new IntentFilter();
                filter.addAction(GlobalData.BLE_CONNECTING);
                filter.addAction(GlobalData.BLE_HEART_PACKAGE);
                filter.addAction(GlobalData.BLE_SERVICE);
                filter.addAction(GlobalData.BLE_SOS);
                filter.addAction(GlobalData.BLE_MATCH);
                filter.addAction(GlobalData.BLE_BOND_REQUEST);
                filter.addAction(GlobalData.BLE_OPEN);
                filter.addAction(GlobalData.BLE_SCAN_STOP);
                filter.addAction(GlobalData.BLE_RESEARCH);
                filter.addAction(GlobalData.BLE_ON_RESTART);
                filter.addAction(GlobalData.ACTION_DEVICE_FIRMVERSION);
                BroadcastReceiver mReceiver = new BroadcastReceiver() {

                    /* renamed from: com.worldgn.helo.MyApplication.11.1 */
                    class C06411 implements Runnable {
                        private final /* synthetic */ Context val$context;
                        private final /* synthetic */ byte[] val$mac;

                        C06411(Context context, byte[] bArr) {
                            this.val$context = context;
                            this.val$mac = bArr;
                        }

                        public void run() {
                            WriteToDevice.matchInfo(this.val$context, WriteToDevice.bytesToHexString(this.val$mac));
                        }
                    }

                    /* renamed from: com.worldgn.helo.MyApplication.11.2 */
                    class C06422 implements Runnable {
                        private final /* synthetic */ Context val$context;

                        C06422(Context context) {
                            this.val$context = context;
                        }

                        public void run() {
                            WriteToDevice.initDeviceLoadCode(this.val$context);
                        }
                    }

                    /* renamed from: com.worldgn.helo.MyApplication.11.3 */
                    class C06433 implements Runnable {
                        private final /* synthetic */ Context val$context;

                        C06433(Context context) {
                            this.val$context = context;
                        }

                        public void run() {
                            MyApplication.startUpDateTime();
                            WriteToDevice.UpdateNewTime(this.val$context);
                        }
                    }

                    /* renamed from: com.worldgn.helo.MyApplication.11.4 */
                    class C06444 implements Runnable {
                        private final /* synthetic */ Context val$context;

                        C06444(Context context) {
                            this.val$context = context;
                        }

                        public void run() {
//                            Log.i(MyApplication.TAG, PrefUtils.getString(this.val$context, FragmentBpMeasure.BP_MEASUMENT_TO_LOCAL, ""));
//                            if (!TextUtils.isEmpty(PrefUtils.getString(this.val$context, FragmentBpMeasure.BP_MEASUMENT_TO_LOCAL, ""))) {
//                                String[] strs = PrefUtils.getString(this.val$context, FragmentBpMeasure.BP_MEASUMENT_TO_LOCAL, "").split("#");
//                                WriteToDevice.sendStandardBP(this.val$context, Integer.parseInt(strs[0]), Integer.parseInt(strs[1]));
//                            }
                        }
                    }

                    /* renamed from: com.worldgn.helo.MyApplication.11.5 */
                    class C06455 implements Runnable {
                        private final /* synthetic */ Context val$context;

                        C06455(Context context) {
                            this.val$context = context;
                        }

                        public void run() {
                            WriteToDevice.secondMach(this.val$context, 1);
                        }
                    }

                    /* renamed from: com.worldgn.helo.MyApplication.11.6 */
                    class C06466 implements Runnable {
                        private final /* synthetic */ Context val$context;

                        C06466(Context context) {
                            this.val$context = context;
                        }

                        public void run() {
                            BleServiceHelper.heartPackage(this.val$context);
                        }
                    }

                    /* renamed from: com.worldgn.helo.MyApplication.11.7 */
                    class C06477 implements Runnable {
                        private final /* synthetic */ Context val$context;

                        C06477(Context context) {
                            this.val$context = context;
                        }

                        public void run() {
                            BleServiceHelper.heartPackage(this.val$context);
                        }
                    }

                    public void onReceive(Context context, Intent intent) {
                        if (intent.getAction().equals(GlobalData.BLE_OPEN)) {
                            Log.i(MyApplication.TAG, "332 \u84dd\u7259\u6253\u5f00\u8bf7\u6c42\u8fd4\u56de\u6210\u529f request ble ok");
                            GlobalData.isRequest = false;
                            if (!GlobalData.status_Connected) {
                                GlobalData.status_Scanning = false;
                                GlobalData.status_Connecting = false;
                                if (GlobalData.isOnPause) {
                                    BleServiceHelper.sendBroadcast(context, GlobalData.BLE_RESEARCH);
                                    return;
                                }
                                BleServiceHelper.sendBroadcast(context, GlobalData.BLE_SEARCH_PRE);
                            }
                        } else if (intent.getAction().equals(GlobalData.BLE_SCAN_STOP)) {
                            Log.i(MyApplication.TAG, "************ \u626b\u63cf\u7ed3\u675f  \u6216\u4e2d\u65ad\u626b\u63cf\u7136\u540e\u8fdb\u884c\u91cd\u641c *****************\n207 isBleConnecting = " + GlobalData.status_Connecting);
                            if (GlobalData.status_Connected) {
                                Log.i(MyApplication.TAG, "209 \u641c\u7d22\u7ed3\u675f \u4f46\u662f\u6b64\u65f6\u8fde\u63a5\u72b6\u6001\u4e3a \u5df2\u8fde\u63a5\u4e0a\u8bbe\u5907");
                            } else if (GlobalData.status_Connecting) {
                                Log.i(MyApplication.TAG, "214 \u6b63\u5728\u8fde\u63a5\u4e2d break\u9000\u51fa");
                            } else if (MyApplication.mList_Devices.isEmpty()) {
                                Log.i(MyApplication.TAG, "\u641c\u7d22\u5230\u7684Ble\u8bbe\u5907\u5217\u8868\u4e3a\u7a7a \u5f00\u59cb\u91cd\u65b0\u641c\u7d22");
                                BleServiceHelper.sendBroadcast(context, GlobalData.BLE_RESEARCH);
                            } else {
                                Log.i(MyApplication.TAG, "\u641c\u7d22\u5230\u7684Ble\u8bbe\u5907\u5217\u8868 = \n" + MyApplication.mList_Devices.toString());
                                int tempRiss = -100;
                                String tempMac = "";
                                for (DeviceItem item : MyApplication.mList_Devices) {
                                    if (item.getRssi() > tempRiss) {
                                        tempRiss = item.getRssi();
                                        tempMac = item.getDeviceMac();
                                    }
                                }
                                if (tempMac.equals("")) {
                                    Log.i(MyApplication.TAG, "\u641c\u7d22\u5230\u7684\u4fe1\u53f7\u6700\u4f18\u7684Ble\u8bbe\u5907\u7684MAC\u4e3a\u7a7a \u8fdb\u5165\u91cd\u641c");
                                    BleServiceHelper.sendBroadcast(context, GlobalData.BLE_RESEARCH);
                                    return;
                                }
                                Log.i(MyApplication.TAG, "\u4f18\u9009\u4fe1\u53f7 = " + tempMac);
                                GlobalData.TOP_MAC = tempMac;
                                GlobalData.status_Connecting = true;
                                MyApplication.stopScanLeDevice();
                                BleServiceHelper.sendBroadcast(context, GlobalData.BLE_CONNECTING);
                                MyApplication.mList_Devices.clear();
                            }
                        } else if (intent.getAction().equals(GlobalData.BLE_RESEARCH)) {
                            MyApplication.stopScanLeDevice();
                            BleServiceHelper.sendBroadcast(context, GlobalData.BLE_SEARCH_BACK);
                        } else if (intent.getAction().equals(GlobalData.BLE_ON_RESTART)) {
                            GlobalData.status_Connected = false;
                            GlobalData.status_Connecting = false;
                            GlobalData.status_Scanning = false;
                            if (PrefUtils.getString(context, GlobalData.DEVICE_TARGET_MAC, "") == "") {
                                BleServiceHelper.sendBroadcast(context, GlobalData.BLE_SEARCH_PRE);
                                return;
                            }
                            BleServiceHelper.sendBroadcast(context, GlobalData.BLE_RESEARCH);
                        } else if (intent.getAction().equals(GlobalData.BLE_CONNECTING)) {
                            Log.i(MyApplication.TAG, "\u84dd\u7259\u8fde\u63a5\u4e2d");
                            BleServiceHelper.sendBroadcast(context, GlobalData.ACTION_GATT_CONNING, true);
                            MyApplication.startAck();
                            boolean bindStatus = false;
                            if (MyApplication.this.checkMAC(GlobalData.TOP_MAC)) {
                                bindStatus = MyApplication.linkDevice.onBindBleService(context, GlobalData.TOP_MAC);
                            }
                            Log.i(MyApplication.TAG, "\u84dd\u7259\u521d\u6b65\u8fde\u63a5\u7ed3\u679c Connecting ble device status = " + bindStatus);
                            if (bindStatus) {
                                BleServiceHelper.sendBroadcast(context, GlobalData.ACTION_GATT_CONNING);
                                BleServiceHelper.sendBroadcast(context, GlobalData.CHANGE_BATTERY_STATUS);
                                return;
                            }
                            MyApplication.stopAck();
                            GlobalData.status_Connecting = false;
                            BleServiceHelper.sendBroadcast(context, GlobalData.BLE_RESEARCH);
                        } else if (intent.getAction().equals(GlobalData.BLE_SERVICE)) {
                            Log.i("sqs", "\u68c0\u6d4b\u5230 BLE_SERVICE \u7136\u540e\u5c31\u5f00\u59cb\u76d1\u6d4b\u76f8\u5e94\u7684\u901a\u9053\u4e86");
                            MyApplication.linkDevice.setNotifCharacteristic("2aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0100-facebeadaaaa", true);
                            MyApplication.linkDevice.setNotifCharacteristic("1aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0010-facebeadaaaa", true);
                            MyApplication.linkDevice.setNotifCharacteristic("0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0003-facebeadaaaa", true);
                            MyApplication.linkDevice.setNotifCharacteristic("0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0003-facebeadaaaa", true);
                            MyApplication.linkDevice.setNotifCharacteristic("2aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0200-facebeadaaaa", true);
                            Log.i(MyApplication.TAG, "250 \u4f7f\u7528MAC\u5730\u5740\u7ed1\u5b9a\u8bbe\u5907");
                            byte[] mac = BleServiceHelper.getSelfBlueMac(context);
                            Log.i(MyApplication.TAG, "252 \u8981\u7ed1\u5b9a\u8bbe\u5907\u7684MAC\u5730\u5740\uff1a" + WriteToDevice.bytesToHexString(mac));
                            if (mac == null) {
                                GlobalData.DELAY_TIME_MATCHING_BLE = BufferRecycler.DEFAULT_WRITE_CONCAT_BUFFER_LEN;
                            } else {
                                GlobalData.DELAY_TIME_MATCHING_BLE = 8000;
                            }
                            MyApplication.startMatch();
                            GlobalData.isMatchInfo = false;
                            WriteToDevice.APPVersion(MyApplication.mApplication, MyApplication.this.getAppVersion());
                            new Handler().postDelayed(new C06411(context, mac), 500);
                            if (WriteToDevice.initDeviceLoadCode(context) != 1) {
                                new Handler().postDelayed(new C06422(context), 2000);
                            }
                        } else if (intent.getAction().equals(GlobalData.BLE_MATCH)) {
                            MyApplication.stopMatch();
                            long data = intent.getLongExtra(intent.getAction(), 0);
                            Log.e(MyApplication.TAG, "291 \u8bbe\u5907\u53d1\u8fc7\u6765\u5339\u914d=1\u54cd\u5e94\u4fe1\u606f: match = " + data);
                            if (data == 1) {
                                BleServiceHelper.sendBroadcast(context, GlobalData.ACTION_GATT_SUCCESS_CONN, true);
                                PrefUtils.setString(context, GlobalData.DEVICE_TARGET_MAC, GlobalData.TOP_MAC);
                                String token = "";
                                token = PrefUtils.getString(MyApplication.mApplication, GlobalData.SHARED_PREFRENCE_DECRY_TOKEN, "");
                                if (!"".equals(token)) {
                                    byte[] decode = Base64.decode(token.getBytes(), 1);
                                    int startSendDecryToken = WriteToDevice.startSendDecryToken(MyApplication.mApplication, decode.length);
                                    WriteToDevice.sendDecryTokenContent1(MyApplication.mApplication, decode);
                                }
                                Log.d("sqs", "\u51c6\u5907  ~\u5f00\u59cb\u540c\u6b65\u65f6\u95f4\u5e94\u7b54\u8ba1\u65f6\u5668...");
                                new Handler().postDelayed(new C06433(context), 500);
                                MyApplication.linkDevice.setNotifCharacteristic("1aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0010-facebeadaaaa", false);
                                BleServiceHelper.heartPackage(context);
                                new Handler().postDelayed(new C06444(context), 10000);
                                if (WriteToDevice.secondMach(context, 1) != 1) {
                                    new Handler().postDelayed(new C06455(context), 1000);
                                    return;
                                }
                                return;
                            }
                            Log.i(MyApplication.TAG, "\u8bbe\u5907\u5df2\u7ecf\u88ab\u7ed1\u5b9a \u65ad\u5f00\u84dd\u7259\u8fde\u63a5 \u5e76\u91cd\u65b0\u641c\u7d22");
//                            if (!(GlobalData.isOnPause || MainActivity.mActivity == null)) {
//                                LongShowToast.makeText(MainActivity.mActivity, context.getString(C0328R.string.blelink_is_bonded), MyApplication.timeout).show();
//                            }
                            WriteToDevice.secondMach(context, 0);
                            MyApplication.toCloseAndReSearch();
                        } else if (intent.getAction().equals(GlobalData.BLE_BOND_REQUEST)) {
                            MyApplication.stopMatch();
                            Log.i(MyApplication.TAG, "\u6536\u5230\u8bbe\u5907\u53d1\u6765\u8bf7\u6c42\u7ed1\u5b9a\u8bf7\u6c42");
                            if (GlobalData.isOnPause) {
                                if (PrefUtils.getString(MyApplication.mApplication, GlobalData.DEVICE_TARGET_MAC, "") == "") {
                                    GlobalData.isOnBondRequest_OnPause = true;
                                    return;
                                } else {
                                    Log.i(MyApplication.TAG, "377 \u7ed1\u5b9a\u8fc7\u8bbe\u5907\u53c8\u5728\u8bf7\u6c42\u7ed1\u5b9a \u540e\u53f0\u5f00\u59cb\u8fdb\u884c\u81ea\u52a8\u7ed1\u5b9a");
                                    return;
                                }
                            }
                            WriteToDevice.secondMach(context, 0);
                            BleServiceHelper.sendBroadcast(context, GlobalData.SHOW_BIND_DIALOG);
                        } else if (intent.getAction().equals(GlobalData.BLE_HEART_PACKAGE)) {
                            if (!GlobalData.status_Connected) {
                                return;
                            }
                            if (WriteToDevice.heartRate(context) == 1) {
                                Log.w(MyApplication.TAG, "\u5fc3\u8df3\u5305\u53d1\u9001\u6210\u529f");
                                GlobalData.heartFail = 0;
                                new Handler().postDelayed(new C06466(context), GlobalData.PERIOD_HEART_PACKAGE_SUCCESS);
                                return;
                            }
                            Log.i(MyApplication.TAG, "\u5fc3\u8df3\u5305\u53d1\u9001\u5931\u8d25");
                            GlobalData.heartFail++;
                            if (GlobalData.heartFail < 6) {
                                new Handler().postDelayed(new C06477(context), 100);
                            }
                        } else if (intent.getAction().equals(GlobalData.BLE_SOS)) {
//                            ContentMainHelper.sendSosRequest(context);
                        } else if (intent.getAction().equals(GlobalData.ACTION_DEVICE_FIRMVERSION)) {
                            MyApplication.stopUpDateTime();
                            Log.d("sqs", "MyApplication \u6210\u529f\u83b7\u53d6\u56fa\u4ef6\u7248\u672c\u53f7 \u5df2\u505c\u6b62\u540c\u6b65\u65f6\u95f4\u5ef6\u8fdf\u8ba1\u65f6\u5668");
                        }
                    }
                };
                if (getmReceiver() == null) {
                    registerReceiver(mReceiver, filter);
                    setmReceiver(mReceiver);
                }
                registerReceiver(myGattUpdateReceiver, makeGattUpdateIntentFilter());
                IntentFilter timeTickIntentFilter = new IntentFilter();
                timeTickIntentFilter.addAction("android.intent.action.TIME_TICK");
//                registerReceiver(timeTickReceiver, timeTickIntentFilter);
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(GlobalData.ACTION_DEVICE_FIRMVERSION);
                intentFilter.addAction(GlobalData.ACTION_MAIN_DATA_FIRM_FAULT);
                intentFilter.addAction(GlobalData.ACTION_MAIN_DATA_FIRM_SUCCESS);
                intentFilter.addAction(GlobalData.ACTION_UP_FIRMWARE_COMPLETE);
                registerReceiver(upFirmReceiver, intentFilter);
            }
        }

        public static void unRegister(Context context) {
            if (timeTickReceiver != null) {
                context.unregisterReceiver(timeTickReceiver);
            }
            if (myGattUpdateReceiver != null) {
                context.unregisterReceiver(myGattUpdateReceiver);
            }
            if (upFirmReceiver != null) {
                context.unregisterReceiver(upFirmReceiver);
            }
        }

        private static IntentFilter makeGattUpdateIntentFilter() {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(GlobalData.ACTION_SERVICE_GATT_CONNECTED);
            intentFilter.addAction(GlobalData.ACTION_SERVICE_GATT_DISCONNECTED);
            intentFilter.addAction(GlobalData.ACTION_SERVICE_GATT_DISCOVERED);
            intentFilter.addAction(GlobalData.ACTION_GATT_SOS);
            intentFilter.addAction(GlobalData.ACTION_GATT_DEVICE_MATCH_ACK);
            intentFilter.addAction(GlobalData.ACTION_GATT_DEVICE_BIND_REQUEST);
            intentFilter.addAction(GlobalData.ACTION_GATT_DEVICE_UNBIND_ACK);
            intentFilter.addAction(GlobalData.ACTION_SERVICE_GATT_DISCOVERED);
            intentFilter.addAction(GlobalData.BLUETOOTH_STATE_CHANGED);
            intentFilter.addAction(GlobalData.BLUETOOTH_ACTION);
            return intentFilter;
        }

        private static void switchServiceByBleStatus() {
            if (mBluetoothAdapter != null) {
                switch (mBluetoothAdapter.getState()) {
                    case 10: /*10*/
                        GlobalData.status_Connecting = false;
                        GlobalData.status_Connected = false;
                        linkDevice.unBindBleService(mApplication);
                        linkDevice.stopBleService(mApplication);
                        return;
                    case 12: /*12*/
                        BleServiceHelper.sendBroadcast(mApplication, GlobalData.BLE_OPEN);
                        return;
                    default:
                        return;
                }
            }
            Log.i(TAG, "584 \u83b7\u53d6\u5f53\u524d\u84dd\u7259\u72b6\u6001\u5931\u8d25 \u539f\u56e0:\u84dd\u7259\u9002\u914d\u5668\u4e3a\u7a7a mBluetoothAdapter = null");
        }

        private static void startMatch() {
            Log.i(TAG, "592 \u5f00\u59cb\u8bbe\u5907\u5339\u914d\u8fc7\u7a0b\u4e2d\u8ba1\u65f6\uff1a DELAY_TIME = " + GlobalData.DELAY_TIME_MATCHING_BLE + " ms");
            mMatchHandler.removeCallbacks(matchTime);
            mMatchHandler.postDelayed(matchTime, (long) GlobalData.DELAY_TIME_MATCHING_BLE);
        }

        private static void stopMatch() {
            Log.i(TAG, "598 \u7ed3\u675f\u8bbe\u5907\u5339\u914d\u8fc7\u7a0b\u4e2d\u8ba1\u65f6 stopMatch");
            mMatchHandler.removeCallbacks(matchTime);
        }

        private static void startUpDateTime() {
            Log.d("sqs", "\u5f00\u59cb\u540c\u6b65\u65f6\u95f4\u5e94\u7b54\u8ba1\u65f6\u5668...");
            upDateTimeHandler.removeCallbacks(updateTime);
            upDateTimeHandler.postDelayed(updateTime, 1000);
        }

        private static void stopUpDateTime() {
            Log.d("sqs", "\u505c\u6b62\u540c\u6b65\u65f6\u95f4\u5e94\u7b54\u8ba1\u65f6\u5668...");
            upDateTimeHandler.removeCallbacks(updateTime);
        }

        private static void startAck() {
            Log.i(TAG, "\u5f00\u59cb\u84dd\u7259\u8fde\u63a5\u8fc7\u7a0b\u4e2d\u8ba1\u65f6\uff1a DELAY_TIME = 10000 ms");
            mAckHandler.removeCallbacks(ackTime);
            mAckHandler.postDelayed(ackTime, 10000);
        }

        private static void stopAck() {
            Log.i(TAG, "\u7ed3\u675f\u84dd\u7259\u8fde\u63a5\u8fc7\u7a0b\u4e2d\u8ba1\u65f6 stopAck_Linking");
            mAckHandler.removeCallbacks(ackTime);
        }

        public static void stopScanLeDevice() {
            Log.d(TAG, "640 \u4e2d\u65ad\u626b\u63cf stopScanLeDevice....");
            BleServiceHelper.sendBroadcast(mApplication, GlobalData.CLOSE_SEARCH_DIALOG);
            scanLeDevice(mApplication, false);
        }

        public static void scanLeDevice(Context context, boolean search) {
            if (search) {
                if (GlobalData.status_Connected) {
                    Log.i(TAG, "609 GlobalData.isConnected = " + GlobalData.status_Connected);
                } else if (GlobalData.status_Connecting) {
                    Log.i(TAG, "607 GlobalData.isConnecting = " + GlobalData.status_Connecting);
                } else if (GlobalData.status_Scanning) {
                    Log.i(TAG, "603 GlobalData.isScanning = " + GlobalData.status_Scanning);
                } else {
                    BleServiceHelper.sendBroadcast(context, GlobalData.ACTION_GATT_SEARCH);
                    GlobalData.status_Scanning = true;
                    mScanHandler.postDelayed(mStopLeScan, GlobalData.PERIOD_SCAN);
                    Log.i(TAG, "startLeScan(mLeScanCallback);");
                    mBluetoothAdapter.startLeScan(mLeScanCallback);
                    Log.i(TAG, "726 \u5f00\u59cb\u626b\u63cf\u8bbe\u5907 PERIOD TIME = 8000 ms");
                }
            } else if (GlobalData.status_Scanning) {
                GlobalData.status_Scanning = false;
                mScanHandler.removeCallbacks(mStopLeScan);
                if (mLeScanCallback != null) {
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
                BleServiceHelper.sendBroadcast(context, GlobalData.BLE_SCAN_STOP);
                Log.i(TAG, "656 \u7ed3\u675f\u626b\u63cf\u8bbe\u5907\u6210\u529f\u5b8c\u6210");
            } else {
                Log.i(TAG, "730 \u626b\u63cf\u8bbe\u5907\u4e4b\u524d\u5df2\u7ecf\u7ed3\u675f\u8fc7\u4e86 \u7ed3\u675f\u626b\u63cf\u6307\u4ee4\u4e2d\u6b62\u261e the ble scan is already off");
            }
        }

        public static boolean isBleEnabled(Context context) {
            if (GlobalData.isBLeEnabled) {
                return true;
            }
            if (context.getPackageManager().hasSystemFeature("android.hardware.bluetooth_le")) {
                mBluetoothAdapter = ((BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
                if (mBluetoothAdapter == null) {
                    Toast.makeText(context, "Phone not support the ble Buletooth", Toast.LENGTH_SHORT).show();
                    return false;
                }
                GlobalData.isBLeEnabled = true;
                return true;
            }
            Toast.makeText(context, "Phone not support the ble Buletooth", Toast.LENGTH_SHORT).show();
            return false;
        }

        private static void showWaitDialog_UpFirmware() {
            BleServiceHelper.sendBroadcast(mApplication, GlobalData.FIRMWARE_UPING_DIALOG);
            count_TickTock = 0;
            startTickTock();
        }

        private static void stopWaitDialog_UpFirmware() {
            BleServiceHelper.sendBroadcast(mApplication, GlobalData.CLOSE_FIRMWARE_UPING_DIALOG);
            stopTickTock();
        }

        private static void startTickTock() {
            mTimeHandler.removeCallbacks(ticktockTime);
            Log.i(TAG, "Start TickTock wait time ...1000 ms");
            mTimeHandler.postDelayed(ticktockTime, 1000);
        }

        private static void stopTickTock() {
            Log.i(TAG, "Stop TickToc...");
            mTimeHandler.removeCallbacks(ticktockTime);
        }

        private static void toCloseAndReSearch() {
            GlobalData.status_Connected = false;
            GlobalData.status_Connecting = false;
            BleServiceHelper.sendBroadcast(mApplication, GlobalData.DEVICE_DISCONN);
            linkDevice.unBindBleService(mApplication);
            if (linkDevice.stopBleService(mApplication)) {
                BleServiceHelper.sendBroadcast(mApplication, GlobalData.BLE_RESEARCH);
            }
        }

        private void timerTask() {
            this.timer = new Timer();
            this.timer.schedule(new TimerTask() {
                public void run() {
//                    if (CheckInternetStatus.isNetworkAvailable(MyApplication.this.connManager, MyApplication.this.process, MyApplication.this.worker, MyApplication.timeout)) {
//                        MyApplication.isNetConn = true;
//                    } else {
//                        Get.Do(MyApplication.mApplication);
//                    }
                }
            }, 60000, 60000);
        }

//        public static void Notification(String Ticker, String Title, String message, int count, int id) {
//            Log.i(TAG, "Received a Notification  Received a Notification   Received a Notification   Received a Notification");
//            if (MainActivity.mActivity != null && GlobalData.isOnPause) {
//                NotificationManager manager = (NotificationManager) mApplication.getSystemService("notification");
//                PendingIntent pendingIntent = PendingIntent.getActivity(mApplication, 0, new Intent(mApplication, MainActivity.class), 268435456);
//                Builder builder = new Builder(mApplication);
//                builder.setSmallIcon(C0328R.drawable.helo);
//                builder.setTicker(Ticker);
//                builder.setContentTitle(Title);
//                builder.setContentText(message);
//                builder.setNumber(count);
//                if (!Boolean.valueOf(PrefUtils.getBoolean(mApplication, "notify", false)).booleanValue()) {
//                    builder.setContentIntent(pendingIntent);
//                }
//                Notification notify = builder.build();
//                notify.icon = C0328R.drawable.notification;
//                notify.defaults = 1;
//                notify.flags |= 16;
//                manager.notify(id, notify);
//            }
//        }

        private boolean checkMAC(String mac) {
            return Pattern.compile("[0-9a-fA-F][0-9a-fA-F]:[0-9a-fA-F][0-9a-fA-F]:[0-9a-fA-F][0-9a-fA-F]:[0-9a-fA-F][0-9a-fA-F]:[0-9a-fA-F][0-9a-fA-F]:[0-9a-fA-F][0-9a-fA-F]").matcher(mac).matches();
        }

        public static BroadcastReceiver getmReceiver() {
            return mReceiver;
        }

        private void setmReceiver(BroadcastReceiver mReceiver) {
            mReceiver = mReceiver;
        }

        private String getAppVersion() {
            PackageInfo info = null;
            try {
                info = mApplication.getPackageManager().getPackageInfo(mApplication.getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            String str = new StringBuilder(String.valueOf(info.versionCode)).toString();
            Log.i(TAG, "OldVersionCore1 = " + str);
            String result = str.substring(2, 8);
            Log.i(TAG, "OldVersionCore2 = " + result);
            return result;
        }

//        public static void Notificationa(String name) {
//            NotificationManager manager = (NotificationManager) mApplication.getSystemService("notification");
//            Builder builder = new Builder(mApplication);
//            builder.setContentText(new StringBuilder(String.valueOf(name)).append(mApplication.getString(C0328R.string.wecare_add_dialog)).toString());
//            builder.setContentTitle(mApplication.getString(C0328R.string.caretext));
//            builder.setSmallIcon(C0328R.drawable.notification);
//            builder.setTicker(mApplication.getString(C0328R.string.app_name));
//            Intent intent = new Intent(mApplication, MainActivity.class);
//            intent.putExtra("WE_CARE_ADD", 1);
//            PendingIntent pi = PendingIntent.getActivity(mApplication, 0, intent, 134217728);
//            Boolean visible = Boolean.valueOf(PrefUtils.getBoolean(mApplication, "notify", false));
//            builder.setContentIntent(pi);
//            if (!visible.booleanValue()) {
//                builder.setContentIntent(pi);
//            }
//            Notification n = builder.build();
//            n.setLatestEventInfo(mApplication, "WE_CARE_ADD", "RESPONSE_WECARE", pi);
//            n.defaults = 1;
//            manager.notify(1002, n);
//        }

//        public static void NotificationSOS(String lng, String lat) {
//            NotificationManager manager = (NotificationManager) mApplication.getSystemService("notification");
//            Builder builder = new Builder(mApplication);
//            builder.setContentText(mApplication.getString(C0328R.string.sos_new_title1));
//            builder.setContentTitle(mApplication.getString(C0328R.string.setting_changeinfo_sos));
//            builder.setSmallIcon(C0328R.drawable.notification);
//            builder.setTicker(mApplication.getString(C0328R.string.app_name));
//            PendingIntent pi = PendingIntent.getActivity(mApplication, 0, new Intent(mApplication, MainActivity.class), 134217728);
//            if (!Boolean.valueOf(PrefUtils.getBoolean(mApplication, "notify", false)).booleanValue()) {
//                builder.setContentIntent(pi);
//            }
//            Notification n = builder.build();
//            n.defaults = 1;
//            manager.notify(FragmentGuardians.DIALOG_FRAGMENT, n);
//        }

//        public static void NotificationGuardian(String content, int id) {
//            NotificationManager manager = (NotificationManager) mApplication.getSystemService("notification");
//            Builder builder = new Builder(mApplication);
//            builder.setContentText(content);
//            builder.setContentTitle(mApplication.getString(C0328R.string.guardian2));
//            builder.setSmallIcon(C0328R.drawable.notification);
//            builder.setAutoCancel(true);
//            BigTextStyle style = new BigTextStyle();
//            style.bigText(content);
//            style.setBigContentTitle(mApplication.getString(C0328R.string.guardian2));
//            builder.setStyle(style);
//            builder.setTicker(mApplication.getString(C0328R.string.app_name));
//            PendingIntent pi = PendingIntent.getActivity(mApplication, 0, new Intent(mApplication, MainActivity.class), 134217728);
//            if (!Boolean.valueOf(PrefUtils.getBoolean(mApplication, "notify", false)).booleanValue()) {
//                builder.setContentIntent(pi);
//            }
//            Notification n = builder.build();
//            n.icon = C0328R.drawable.icon_logo;
//            n.defaults = 1;
//            manager.notify(id, n);
//        }

}
