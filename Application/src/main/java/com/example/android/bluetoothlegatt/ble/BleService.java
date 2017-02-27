package com.example.android.bluetoothlegatt.ble;

import android.annotation.TargetApi;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.example.android.bluetoothlegatt.GlobalData;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class BleService extends Service {
    private static final int ISMATCHINFOTODEVICE = 1001;
    private static final UUID SEEDMORN_BNAD_ONE_CHARACTERISTIC_UUID;
    private static final UUID SEEDMORN_BNAD_SERVICE_UUID;
    private static final UUID SEEDMORN_BNAD_TWO_CHARACTERISTIC_UUID;
    public static final String TAG;
    private static Runnable guardian_timetask;
    private static Handler handler;
    private static boolean isMatchInfo;
    private final IBinder mBinder;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothManager mBluetoothManager;
    private int mConnectionState;
    private BluetoothGattCallback mGattCallback;
    private BluetoothGattCharacteristic mOneCharacteristic;
    private BluetoothGattCharacteristic mTwoCharacteristic;
    private BroadcastReceiver receiver;

    /* renamed from: com.worldgn.helo.ble.BleService.1 */
    static class BleHandler extends Handler {
        BleHandler() {
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
            }
        }
    }

    /* renamed from: com.worldgn.helo.ble.BleService.2 */
    static class BleTimer implements Runnable {
        BleTimer() {
        }

        public void run() {
            BleService.handler.sendEmptyMessage(BleService.ISMATCHINFOTODEVICE);
        }
    }

    /* renamed from: com.worldgn.helo.ble.BleService.3 */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    class C06633 extends BluetoothGattCallback {
        C06633() {
        }

        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == 2) {
                Log.i(BleService.TAG, "373 \u6210\u529f\u8fde\u63a5\u5230GATT\u670d\u52a1 Connected to GATT server.");
                BleService.this.mConnectionState = 2;
                BleService.this.mBluetoothGatt.discoverServices();
                GlobalData.status_ConnInit = true;
                BleService.this.sendBroadcast(GlobalData.ACTION_SERVICE_GATT_CONNECTED, true);
            } else if (newState == 0) {
                BleService.this.mConnectionState = 0;
                BleService.this.refreshDeviceCache();
                BleService.this.mBluetoothGatt.close();
                BleService.this.mBluetoothGatt = null;
                GlobalData.status_ConnInit = false;
                BleService.this.sendBroadcast(GlobalData.ACTION_SERVICE_GATT_DISCONNECTED, false);
            }
        }

        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == 0) {
                Log.i(BleService.TAG, "394 \u5bfb\u627e\u84dd\u7259\u8fde\u63a5 onServicesDiscovered success.");
                for (BluetoothGattService bluetoothGattService : gatt.getServices()) {
                    Log.v(BleService.TAG, "397 \u5bfb\u627e\u84dd\u7259\u8fde\u63a5 bluetoothGattService.getUuid() = " + bluetoothGattService.getUuid());
                }
                BluetoothGattService service = gatt.getService(BleService.SEEDMORN_BNAD_SERVICE_UUID);
                if (service == null) {
                    Log.i(BleService.TAG, "onServicesDiscovered = SEEDMORN_BNAD_SERVICE_UUID failure");
                    BleService.this.disconnect();
                    return;
                }
                Log.i(BleService.TAG, "onServicesDiscovered = SEEDMORN_BNAD_SERVICE_UUID.ok");
                BleService.this.mOneCharacteristic = service.getCharacteristic(BleService.SEEDMORN_BNAD_ONE_CHARACTERISTIC_UUID);
                if (BleService.this.mOneCharacteristic == null) {
                    BleService.this.disconnect();
                    return;
                }
                BleService.this.mTwoCharacteristic = service.getCharacteristic(BleService.SEEDMORN_BNAD_TWO_CHARACTERISTIC_UUID);
                if (BleService.this.mOneCharacteristic == null) {
                    BleService.this.disconnect();
                    return;
                } else if (BleService.this.mTwoCharacteristic == null) {
                    BleService.this.disconnect();
                    return;
                } else {
                    BleService.this.broadcastUpdate(GlobalData.ACTION_SERVICE_GATT_DISCOVERED);
                    return;
                }
            }
            BleService.this.disconnect();
        }

        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == 0) {
                byte[] data = characteristic.getValue();
                StringBuilder stringBuilder = new StringBuilder(data.length);
                int length = data.length;
                for (int i = 0; i < length; i++) {
                    stringBuilder.append(String.format("%02X ", new Object[]{Byte.valueOf(data[i])}));
                }
                Log.i(BleService.TAG, "467 \u63a5\u6536\u8bbe\u5907\u53d1\u8fc7\u6765\u7684\u6d88\u606f onCharacteristicRead:" + characteristic.getUuid() + "[" + stringBuilder.toString() + "]");
            }
        }

        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == 0) {
                Log.i(BleService.TAG, "478 \u5411\u8bbe\u5907\u53d1\u9001\u6d88\u606f \u5199\u5165\u6210\u529f");
            }
        }

        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.i(BleService.TAG, "487 \u8bbe\u5907\u7aef\u6709\u6570\u636e\u66f4\u65b0 bletoothleservice onCharacteristicChanged:\n Uuid = " + characteristic.getService().getUuid().toString());
            byte[] data = characteristic.getValue();
            StringBuilder stringBuilder = new StringBuilder(data.length);
            int length = data.length;
            for (int i = 0; i < length; i++) {
                stringBuilder.append(String.format("%02X ", new Object[]{Byte.valueOf(data[i])}));
            }
            Log.i(BleService.TAG, "495 \u5c5e\u6027UUID characteristic = " + characteristic.getUuid() + "[" + stringBuilder.toString() + "]");
            String uuid = characteristic.getService().getUuid().toString();
            String charactUUID = characteristic.getUuid().toString();
            if (uuid.equals("2aabcdef-1111-2222-0000-facebeadaaaa")) {
                BleService.this.isNewSleep(stringBuilder.toString());
                BleService.this.sendUpdataFirmInfo(stringBuilder.toString());
            } else if (uuid.equals("0aabcdef-1111-2222-0000-facebeadaaaa") && charactUUID.equals("facebead-ffff-eeee-0004-facebeadaaaa")) {
                BleService.this.sendEcgDataAll(stringBuilder.toString());
            } else if (uuid.equals("0aabcdef-1111-2222-0000-facebeadaaaa") && charactUUID.equals("facebead-ffff-eeee-0005-facebeadaaaa")) {
                BleService.this.sendPwDataAll(stringBuilder.toString());
            } else if (uuid.equals("1aabcdef-1111-2222-0000-facebeadaaaa")) {
                BleService.this.sendBindBroadcast(stringBuilder.toString());
            } else {
                BleService.this.sendDataBroadcast(stringBuilder.toString());
            }
        }
    }

    /* renamed from: com.worldgn.helo.ble.BleService.4 */
    class C06644 extends BroadcastReceiver {
        C06644() {
        }

        public void onReceive(Context context, Intent intent) {
            if (GlobalData.ACTION_MATCH_INFO_TO_DEVICE.equals(intent.getAction())) {
                BleService.handler.postDelayed(BleService.guardian_timetask, 1000);
            }
        }
    }

    public class LocalBinder extends Binder {
        public BleService getService() {
            return BleService.this;
        }
    }

    public BleService() {
        this.mConnectionState = 0;
        this.mGattCallback = null;
        this.mBinder = new LocalBinder();
    }

    static {
        TAG = BleService.class.getSimpleName();
        SEEDMORN_BNAD_SERVICE_UUID = UUID.fromString("1aabcdef-1111-2222-0000-facebeadaaaa");
        SEEDMORN_BNAD_ONE_CHARACTERISTIC_UUID = UUID.fromString("facebead-ffff-eeee-0010-facebeadaaaa");
        SEEDMORN_BNAD_TWO_CHARACTERISTIC_UUID = UUID.fromString("facebead-ffff-eeee-0020-facebeadaaaa");
        handler = new BleHandler();
        guardian_timetask = new BleTimer();
        isMatchInfo = false;
    }

    private void sendBindBroadcast(String data) {
        Log.d(TAG, "71 \u6536\u5230\u7ed1\u5b9a\u7684\u76f8\u5173\u6570\u636e\u53d1\u5e7f\u64ad \u7c7b\u578b:");
        String cmdType = data.substring(9, 11);
        if (cmdType.equals("37")) {
            Log.i(TAG, "74 \u5339\u914d\u54cd\u5e94");
            GlobalData.isMatchInfo = true;
            Log.d("sqs", "\u6536\u5230\u5339\u914d\u54cd\u5e94 IS_MATCH_INFO_FROM_DEVICE = true");
            broadcastUpdate(GlobalData.ACTION_GATT_DEVICE_MATCH_ACK, Long.valueOf(data.substring(15, 17), 16).longValue());
        } else if (cmdType.equals("38")) {
            String valuse = data.substring(15, 17);
            Log.i(TAG, "80 \u89e3\u7ed1\u54cd\u5e94");
            broadcastUpdate(GlobalData.ACTION_GATT_DEVICE_UNBIND_ACK, Long.valueOf(valuse, 16).longValue());
        } else if (cmdType.equals("23")) {
            Log.i(TAG, "84 \u8bbe\u5907\u8bf7\u6c42\u7ed1\u5b9a");
            Log.d("sqs", "\u6536\u5230\u8bf7\u6c42\u7ed1\u5b9a IS_MATCH_INFO_FROM_DEVICE");
            broadcastUpdate(GlobalData.ACTION_GATT_DEVICE_BIND_REQUEST);
        }
    }

    private boolean refreshDeviceCache() {
        boolean z = false;
        Log.i(TAG, "234 \u6e05\u7a7a\u7cfb\u7edf\u84dd\u7259\u7f13\u5b58 BluetoothLeService refreshDeviceCache");
        if (this.mBluetoothGatt != null) {
            try {
                Log.i(TAG, "237 mBluetoothGatt\u670d\u52a1\u5668\u4e0d\u4e3a\u7a7a BleService.refreshDeviceCache : mBluetoothGatt != null");
                BluetoothGatt localBluetoothGatt = this.mBluetoothGatt;
                Method localMethod = localBluetoothGatt.getClass().getMethod("refresh", new Class[0]);
                if (localMethod != null) {
                    z = ((Boolean) localMethod.invoke(localBluetoothGatt, new Object[0])).booleanValue();
                }
            } catch (Exception localException) {
                Log.i(TAG, "102 \u5728\u66f4\u65b0\u8bbe\u5907\u7684\u8fc7\u7a0b\u4e2d\u53d1\u751f\u4e0d\u53ef\u6355\u83b7\u7684\u5f02\u5e38 An exception occured while refreshing device");
                localException.printStackTrace();
            }
        }
        return z;
    }

    private void broadcastUpdate(String action) {
        Intent intent = new Intent(action);
        Log.d(TAG, "142 \u53d1\u9001\u72b6\u6001\u53d8\u5316\u4fe1\u606f\u5e7f\u64ad broadcastUpdate\uff1a\n 142 \u52a8\u4f5c\u662f: action = " + action);
        sendBroadcast(intent);
    }

    private void sendPwDataAll(String string) {
        Log.d(TAG, "~~~~~~\u8109\u640f\u6ce2~~~~~~~~~~~~~ = " + string);
        if (string.length() != 0) {
            broadcastUpdate(GlobalData.ACTION_MAIN_DATA_PW, string);
        }
    }

    private void sendEcgDataAll(String string) {
        Log.d(TAG, "~~~~~~ecgAllData~~~~~~~~~~~~~:" + string);
        if (string.length() != 0) {
            broadcastUpdate(GlobalData.ACTION_MAIN_DATA_ECGALLDATA, string);
        }
    }

    private void sendUpdataFirmInfo(String data) {
        Log.v(TAG, "139 \u56fa\u4ef6\u4fe1\u606f :" + data);
        String cmdType = data.substring(9, 11);
        String dataType = data.substring(15, 17);
        if (cmdType.equals("3A")) {
            Log.i(TAG, "dataType.equals('3A')");
            if (dataType.equals("01")) {
                Log.i(TAG, "145 \u53d1\u9001\u8bbe\u5907\u7aef\u4f20\u8fc7\u6765\u7684\u56fa\u4ef6\u4fe1\u606f\u5e7f\u64ad ACTION_MAIN_DATA_FIR_SUCCESS");
                broadcastUpdate(GlobalData.ACTION_MAIN_DATA_FIRM_SUCCESS);
                return;
            }
            Log.i(TAG, "148 \u53d1\u9001\u8bbe\u5907\u7aef\u4f20\u8fc7\u6765\u7684\u56fa\u4ef6\u4fe1\u606f\u5e7f\u64ad ACTION_MAIN_DATA_FIR_FAULT");
            broadcastUpdate(GlobalData.ACTION_MAIN_DATA_FIRM_FAULT);
        } else if (cmdType.equals("39")) {
            int version;
            String[] str = new String[]{data.substring(15, 17), data.substring(18, 20), data.substring(21, 23), data.substring(24, 26)};
            String tem = str[3] + str[2] + str[1] + str[0];
            Log.d(TAG, "\u7248\u672c\u53f7 str:versioncore = " + tem);
            Log.d(TAG, "\u7248\u672c\u53f7 int:versioncore = " + Long.valueOf(tem, 16));
            if (tem.equals("FFFFFFFF")) {
                Log.d(TAG, "\u7248\u672c\u53f7 \u4fdd\u5b58\u4e3a\u96f6" + tem);
                version = 1;
            } else {
                version = Integer.valueOf(tem, 16).intValue();
            }
            GlobalData.VERSION_FIRM = version;
//            PrefUtils.setString(getApplicationContext(), "version_firmware", new StringBuilder(String.valueOf(version)).toString());
            Log.d("sqs", "166 \u56fa\u4ef6\u7248\u672c\u53f7\u83b7\u53d6\u6210\u529f \u5e7f\u64ad\u53d1\u51fa\u53bb ACTION_DEVICE_FIRMVERSION");
            Log.i(TAG, "167 ACTION_DEVICE_FIRMVERSION = " + version);
            broadcastUpdate(GlobalData.ACTION_DEVICE_FIRMVERSION, new StringBuilder(String.valueOf(version)).toString());
        } else if (cmdType.equals("3E")) {
            Log.v(TAG, "load data----------------" + data);
            broadcastUpdate(GlobalData.ACTION_GATT_LOAD_DATA, data);
        } else if (cmdType.equals("44")) {
            broadcastUpdate(GlobalData.ACTION_GATT_LOAD_DATA_SLEEP, data);
        } else if (cmdType.equals("4F")) {
            Log.d("sqs", "\u63a5\u6536\u5230\u8bbe\u5907\u7aef\u4f20\u6765\u566a\u97f34F\u6570\u636e..." + data);
            broadcastUpdate(GlobalData.ACTION_GATT_BLOOD_PRESSURE_NOISE, data);
        }
    }

    private void sendDataBroadcast(String data) {
        Log.v(TAG, "210 \u53d1\u9001\u5e7f\u64ad\u7684\u6570\u636e = " + data);
        if (!data.equals("CF")) {
            String dataType = data.substring(9, 11);
            Log.v(TAG, "\u6570\u636e\u7c7b\u522b =========== " + dataType);
            if (dataType.equals("31")) {
                try {
                    String steps = parseSingeData(data);
                    Log.i(TAG, "parseSingeData(data) = " + steps);
                    int parseInt = Integer.parseInt(steps);
                    if (parseInt >= 5000 && GlobalData.isOnPause) {
                        String date_now = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
//                        String date_sp = PrefUtils.getString(MyApplication.mApplication, GlobalData.Today, "");
//                        int step = parseInt / 5000;
//                        int count = PrefUtils.getInt(MyApplication.mApplication, GlobalData.StepCount, 0);
//                        if (date_sp.equals("") || !date_now.equals(date_sp) || step != count) {
//                            PrefUtils.setInt(MyApplication.mApplication, GlobalData.StepCount, step);
//                            PrefUtils.setString(MyApplication.mApplication, GlobalData.Today, date_now);
//                            GlobalData.notification_count_steps++;
//                            MyApplication.Notification(getResources().getString(C0328R.string.new_message_coming), getApplicationContext().getString(C0328R.string.app_name), getApplicationContext().getString(C0328R.string.notification_steps), GlobalData.notification_count_steps, 1);
//                        } else {
//                            return;
//                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                broadcastUpdate(GlobalData.ACTION_MAIN_DATA_STEPS, parseSingeData(data));
                return;
            }
            if (dataType.equals("32")) {
                Log.v(TAG, "\u5fc3\u7387 = " + parseSingeData(data));
                broadcastUpdate(GlobalData.ACTION_MAIN_DATA_HR, parseSingeData(data));
                return;
            }
            if (dataType.equals("3B")) {
                broadcastUpdate(GlobalData.ACTION_MAIN_DATA_MOOD, parseMoodIntData(data));
                return;
            }
            if (dataType.equals("3C")) {
                broadcastUpdate(GlobalData.ACTION_MAIN_DATA_FATIGUE, parseMoodIntData(data));
                return;
            }
            if (dataType.equals("3D")) {
                broadcastUpdate(GlobalData.ACTION_MAIN_DATA_BREATH, parseBRData(data));
                return;
            }
            if (dataType.equals("34")) {
                broadcastUpdate(GlobalData.ACTION_MAIN_DATA_KLL, parseSingeData(data));
                return;
            }
            if (dataType.equals("35")) {
                broadcastUpdate(GlobalData.ACTION_MAIN_DATA_SLEEP, parseSleepData(data));
                return;
            }
            if (dataType.equals("41")) {
                Log.v(TAG, "bp = " + data);
                broadcastUpdate(GlobalData.ACTION_MAIN_DATA_BP, parseBpData(data));
                return;
            }
            if (dataType.equals("42")) {
                broadcastUpdate(GlobalData.ACTION_MAIN_DATA_ECG, parseEcgData(data));
                return;
            }
            if (dataType.equals("43")) {
                int batteryData = pareseBatteryData(data);
                int i = 0;
                while (true) {
                    int length = GlobalData.low_batery.length;
                    int r0 = 0;
                    if (i >= r0) {
                        GlobalData.POWER_BATTERY = batteryData;
                        long j = (long) batteryData;
                        broadcastUpdate(GlobalData.ACTION_MAIN_DATA_BATTERY_POWER, j);
                        return;
                    }
                    if (batteryData == GlobalData.low_batery[i]) {
//                        String replace = getResources().getString(C0328R.string.notification_lowbatery).replace("{0}", new StringBuilder(String.valueOf(batteryData)).toString());
//                        MyApplication.Notification(getResources().getString(C0328R.string.app_name), getResources().getString(C0328R.string.app_name), replace, GlobalData.notification_count_lowbetery, 6);
                    }
                    i++;
                }
            } else {
                if (dataType.equals("24")) {
                    GlobalData.notification_count_sos++;
//                    MyApplication.Notification(getResources().getString(C0328R.string.new_message_coming), getResources().getString(C0328R.string.app_name), getResources().getString(C0328R.string.helo_had_send_a_sos), GlobalData.notification_count_sos, 2);
                    broadcastUpdate(GlobalData.ACTION_GATT_SOS);
                    return;
                }
                if (dataType.equals("45")) {
                    Log.d("sqs", "\u8bbe\u5907\u53d1\u6765 LED  result = " + data);
                    if (data != null) {
                        String substring = data.substring(15, 17);
                        Log.d("sqs", "\u8bbe\u5907\u53d1\u6765 LED substring = " + substring);
                        if ("01".equals(substring)) {
                            broadcastUpdate(GlobalData.LEDCONTORLLSUCCESS);
                        }
                    }
                }
            }
        }
    }

    private int pareseBatteryData(String data) {
        return Integer.parseInt(data.substring(15, 17), 16);
    }

    private String parseSingeData(String data) {
        String dataStr = data.substring(27, 38);
        return new StringBuilder(String.valueOf((long) Integer.parseInt(dataStr.substring(9, 11) + dataStr.substring(6, 8) + dataStr.substring(3, 5) + dataStr.substring(0, 2), 16))).toString();
    }

    private long parseSleepData(String data) {
        String dataStr = data.substring(27, 38);
        String dateStr = data.substring(15, 26);
        return (long) Integer.parseInt(dataStr.substring(9, 11) + dataStr.substring(6, 8) + dataStr.substring(3, 5) + dataStr.substring(0, 2), 16);
    }

    private String parseBpData(String data) {
        Log.v(TAG, "bp data = " + data);
        String dataStr = data.substring(27, 32);
        Log.v(TAG, "bp dataStr = " + dataStr);
        return dataStr;
    }

    private String parseEcgData(String data) {
        Log.v(TAG, "Ecg data = " + data);
        String dataStr = data.substring(27, 29);
        Log.v(TAG, "Ecg dataStr = " + dataStr);
        return dataStr;
    }

    private String parseBRData(String data) {
        String dataStr = data.substring(27, 29);
        Log.v(TAG, "BR DATA = " + dataStr);
        return new StringBuilder(String.valueOf((long) Integer.parseInt(dataStr, 16))).toString();
    }

    private String parseMoodIntData(String data) {
        return data.substring(27, 29);
    }

    private void broadcastUpdate(String action, long dataf) {
        Intent intent = new Intent(action);
        intent.putExtra(action, dataf);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(String action, String dataf) {
        Intent intent = new Intent(action);
        intent.putExtra(action, dataf);
        sendBroadcast(intent);
    }

    private void sendBroadcast(String action, boolean status) {
        Intent intent = new Intent(action);
        intent.putExtra(action, status);
        sendBroadcast(intent);
    }

    public void onCreate() {
        Log.d(TAG, "325 \u521b\u5efa\u670d\u52a1 onCreate go");
        super.onCreate();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public boolean stopService(Intent name) {
        Log.i(TAG, "355 \u505c\u6b62\u670d\u52a1 action stopService ble");
        return super.stopService(name);
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    public void onDestroy() {
        Log.i(TAG, "358 \u5f00\u59cb\u9500\u6bc1\u670d\u52a1 onDestroy on");
        disconnect();
        close();
        if (this.mBluetoothAdapter != null) {
            this.mBluetoothAdapter.cancelDiscovery();
        }
        this.mBluetoothAdapter = null;
        this.mBluetoothManager = null;
        super.onDestroy();
    }

    public IBinder onBind(Intent intent) {
        Log.i(TAG, "368 \u5f00\u59cb\u7ed1\u5b9a\u670d\u52a1 onbind on");
        this.mGattCallback = new C06633();
        return this.mBinder;
    }

    private boolean isNewSleep(String data) {
        Log.d("sqs", "\u65b0\u7761\u7720 data1 = " + data);
        if ("44".equals(data.substring(3, 5))) {
            Log.d("sqs", "\u65b0\u7761\u7720 data2 = " + data);
            broadcastUpdate(GlobalData.ACTION_GATT_SLEEP_NEW, data);
        }
        return false;
    }

    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "516 BluetoothLeService\u670d\u52a1\u88ab\u89e3\u9664\u7ed1\u5b9a onUnbind on");
        return super.onUnbind(intent);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean initialize() {
        Log.i(TAG, "528 \u521d\u59cb\u5316\u84dd\u7259\u9002\u914d\u5668 initialize BluetoothManager");
        if (this.mBluetoothManager == null) {
            this.mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (this.mBluetoothManager == null) {
                Log.i(TAG, "538 \u521d\u59cb\u5316\u84dd\u7259\u9002\u914d\u5668\u5931\u8d25 \u539f\u56e0\uff1a BluetoothManager\u4e3a\u7a7a  Unable to initialize BluetoothManager.");
                return false;
            }
        }
        this.mBluetoothAdapter = this.mBluetoothManager.getAdapter();
        if (this.mBluetoothAdapter != null) {
            return true;
        }
        Log.i(TAG, "545 \u521d\u59cb\u5316\u84dd\u7259\u9002\u914d\u5668\u5931\u8d25 \u539f\u56e0\uff1a BluetoothAdapter\u4e3a\u7a7a Unable to obtain a BluetoothAdapter.");
        return false;
    }

    public boolean connect(String address) {
        if (this.mBluetoothAdapter == null || address == null) {
            Log.i(TAG, "\u8fde\u63a5\u5230\u6258\u7ba1\u5728\u84dd\u7259BLE\u8bbe\u5907GATT\u670d\u52a1\u5668\u5931\u8d25BluetoothAdapter not initialized or unspecified address.");
            return false;
        } else if (this.mBluetoothDeviceAddress == null || !address.equals(this.mBluetoothDeviceAddress) || this.mBluetoothGatt == null) {
            BluetoothDevice device = this.mBluetoothAdapter.getRemoteDevice(address);
            if (device == null) {
                Log.i(TAG, "\u8fde\u63a5\u5230\u6258\u7ba1\u5728\u84dd\u7259BLE\u8bbe\u5907GATT\u670d\u52a1\u5668 Device not found. Unable to connect.");
                return false;
            }
            this.mBluetoothGatt = device.connectGatt(this, false, this.mGattCallback);
            Log.i(TAG, "\u8fde\u63a5\u5230\u6258\u7ba1\u5728\u84dd\u7259BLE\u8bbe\u5907GATT\u670d\u52a1\u5668\u6210\u529f Trying to create a new connection.");
            this.mBluetoothDeviceAddress = address;
            this.mConnectionState = 1;
            return true;
        } else {
            Log.v(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            Log.d(TAG, "\u5148\u524d\u66fe\u8fde\u63a5\u88c5\u7f6e \u5219\u5c1d\u8bd5\u91cd\u65b0\u8fde\u63a5 action bleconnection address:" + address);
            if (!this.mBluetoothGatt.connect()) {
                return false;
            }
            this.mConnectionState = 1;
            return true;
        }
    }

    public void disconnect() {
        if (this.mBluetoothAdapter == null || this.mBluetoothGatt == null) {
            Log.i(TAG, "565 \u65ad\u5f00\u8fde\u63a5\u5931\u8d25 \uff1aBluetoothAdapter not initialized");
        } else if (this.mConnectionState == 2) {
            this.mBluetoothGatt.disconnect();
        }
    }

    public void close() {
        if (this.mBluetoothGatt != null) {
            this.mBluetoothGatt.close();
            this.mBluetoothGatt = null;
        }
    }

    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (this.mBluetoothAdapter == null || this.mBluetoothGatt == null) {
            Log.i(TAG, "\u8bfb\u53d6\u5931\u8d25 \u539f\u56e0:\u84dd\u7259\u9002\u914d\u5668\u5c1a\u672a\u521d\u59cb\u5316 BluetoothAdapter not initialized");
        } else {
            this.mBluetoothGatt.readCharacteristic(characteristic);
        }
    }

    public void writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (this.mBluetoothAdapter == null || this.mBluetoothGatt == null) {
            Log.i(TAG, "\u5199\u5165\u5931\u8d25 \u539f\u56e0:\u84dd\u7259\u9002\u914d\u5668\u5c1a\u672a\u521d\u59cb\u5316 BluetoothAdapter not initialized");
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
            Log.i(TAG, "653 \u5199\u5165\u5931\u8d25 GATT\u670d\u52a1\u672a\u627e\u5230 Rx service not found!");
            return false;
        }
        BluetoothGattCharacteristic RxChar = RxService.getCharacteristic(UUID.fromString(charactersticUUID));
        if (RxChar == null) {
            Log.i(TAG, "659 \u5199\u5165\u5931\u8d25 GATT\u5c5e\u6027\u672a\u627e\u5230 Rx charateristic not found!");
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
            Log.i(TAG, "681 \u5199\u5165\u5931\u8d25 GATT\u670d\u52a1\u672a\u627e\u5230 Rx service not found!");
            return false;
        }
        BluetoothGattCharacteristic RxChar = RxService.getCharacteristic(UUID.fromString(charactersticUUID));
        if (RxChar == null) {
            Log.i(TAG, "687 \u5199\u5165\u5931\u8d25 GATT\u5c5e\u6027\u672a\u627e\u5230 Rx charateristic not found!");
            return false;
        }
        RxChar.setValue(value);
        RxChar.setWriteType(1);
        boolean status = this.mBluetoothGatt.writeCharacteristic(RxChar);
        Log.i(TAG, "661 \u5199\u5165\u6210\u529f \u8fd4\u56de\u54cd\u5e94\u503c :write TXchar - status = " + status);
        return status;
    }

    public boolean setCharacteristicNotification(String serviceUUID, String characteristicUUID, boolean enabled) {
        if (this.mBluetoothAdapter == null || this.mBluetoothGatt == null) {
            Log.w(TAG, "\u8bbe\u7f6eCharacteristicnotification\u76d1\u542c\u5931\u8d25 \u539f\u56e0:\u84dd\u7259\u9002\u914d\u5668\u672a\u521d\u59cb\u5316 BluetoothAdapter not initialized");
            return false;
        }
        BluetoothGattService RxService = this.mBluetoothGatt.getService(UUID.fromString(serviceUUID));
        if (RxService == null) {
            Log.i(TAG, "\u8bbe\u7f6eCharacteristicnotification\u76d1\u542c\u5931\u8d25 \u539f\u56e0:GATT\u670d\u52a1\u4e3a\u7a7a null Service");
            return false;
        }
        BluetoothGattCharacteristic TxChar = RxService.getCharacteristic(UUID.fromString(characteristicUUID));
        if (TxChar == null) {
            Log.i(TAG, "\u8bbe\u7f6eCharacteristicnotification\u76d1\u542c\u5931\u8d25 \u539f\u56e0:GATT\u5c5e\u6027\u4e3a\u7a7a null Characteristic");
            return false;
        }
        boolean status = this.mBluetoothGatt.setCharacteristicNotification(TxChar, enabled);
        Log.i(TAG, "\u8bbe\u7f6eCharacteristicnotification\u76d1\u542c\u6210\u529f \u8fd4\u56de\u54cd\u5e94\u503c :Characteristic on status = " + status);
        return status;
    }

    private void registerBroadcast() {
        new IntentFilter().addAction(GlobalData.ACTION_MATCH_INFO_TO_DEVICE);
        this.receiver = new C06644();
    }
}
