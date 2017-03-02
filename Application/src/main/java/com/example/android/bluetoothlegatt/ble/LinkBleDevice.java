package com.example.android.bluetoothlegatt.ble;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.example.android.bluetoothlegatt.constant.Constants;

public class LinkBleDevice {
    private static final String TAG;
    private static LinkBleDevice instance;
    private boolean bindStatus;
    private BleService mBLeService;
    private String mDeviceAddress;
    private Intent mGattServiceIntent;
    private ServiceConnection mServiceConn;

    /* renamed from: com.worldgn.helo.ble.LinkBleDevice.1 */
    class BleServiceConnection implements ServiceConnection {
        BleServiceConnection() {
        }

        public void onServiceConnected(ComponentName componentName, IBinder service) {
            LinkBleDevice.this.mBLeService = ((BleService.LocalBinder) service).getService();
            if (!LinkBleDevice.this.mBLeService.initialize()) {
                Log.i(LinkBleDevice.TAG, "45 \u4e0d\u80fd\u521d\u59cb\u5316\u84dd\u7259 Unable to initialize Bluetooth");
            }
            LinkBleDevice.this.connectBle();
        }

        public void onServiceDisconnected(ComponentName componentName) {
        }
    }

    static {
        TAG = LinkBleDevice.class.getSimpleName();
        instance = null;
    }

    public static LinkBleDevice getInstance(Context context) {
        if (instance == null) {
            instance = new LinkBleDevice(context);
        }
        return instance;
    }

    public LinkBleDevice(Context context) {
        this.bindStatus = false;
        this.mServiceConn = null;
        this.mServiceConn = new BleServiceConnection();
    }

    private boolean connectBle() {
        Log.i(TAG, "\u8fde\u63a5ble\u8bbe\u5907:MAC = " + this.mDeviceAddress);
        return this.mBLeService.connect(this.mDeviceAddress);
    }

    public boolean onBindBleService(Context context, String macAddress) {
        Log.i(TAG, "\u7ed1\u5b9a\u84dd\u7259\u8fde\u63a5\u670d\u52a1\u5f00\u59cb onBindMyBleService");
        this.mGattServiceIntent = new Intent(context, BleService.class);
        context.startService(this.mGattServiceIntent);
        this.bindStatus = context.bindService(this.mGattServiceIntent, this.mServiceConn, Context.BIND_AUTO_CREATE);
        Log.i(TAG, "\u7ed1\u5b9a\u84dd\u7259\u8fde\u63a5\u670d\u52a1\u7ed3\u675f onBindBleService bindStatus = " + this.bindStatus);
        this.mDeviceAddress = macAddress;
        return this.bindStatus;
    }

    public void unBindBleService(Context context) {
        if (this.bindStatus) {
            Log.i(TAG, "\u5f00\u59cb\u89e3\u7ed1 unBindService is called the begin and the mServiceConnection's");
            if (this.mServiceConn == null) {
                Log.i(TAG, "\u89e3\u7ed1\u7ec8\u6b62 ServiceConnection\u5df2\u7ecf\u88ab\u89e3\u7ed1\u8fc7\u4e86");
            } else if (context != null) {
                try {
                    context.unbindService(this.mServiceConn);
                } catch (Exception e) {
                    Log.i(TAG, "\u89e3\u7ed1\u5f02\u5e38 \u88ab\u8feb\u7ec8\u6b62");
                    e.printStackTrace();
                }
                Log.i(TAG, "\u89e3\u7ed1\u6210\u529f unBindService is called the end");
            }
        }
    }

    public boolean stopBleService(Context context) {
        this.mGattServiceIntent = new Intent(context, BleService.class);
        boolean stopStatus = context.stopService(this.mGattServiceIntent);
        if (stopStatus) {
            Constants.status_Scanning = false;
        }
        return stopStatus;
    }

    public boolean setDataWriteRXCharacteristic(String serviceUUID, String characteristicUUID, byte[] bs) {
        if (this.mBLeService == null) {
            return false;
        }
        return this.mBLeService.writeRXCharacteristic(serviceUUID, characteristicUUID, bs);
    }

    public boolean setDataWriteUDCharacteristic(String serviceUUID, String characteristicUUID, byte[] bs) {
        if (this.mBLeService == null) {
            return false;
        }
        return this.mBLeService.writeUDCharacteristic(serviceUUID, characteristicUUID, bs);
    }

    public boolean setNotifCharacteristic(String serviceUUID, String characteristicUUID, boolean enabled) {
        if (this.mBLeService == null) {
            return false;
        }
        return this.mBLeService.setCharacteristicNotification(serviceUUID, characteristicUUID, enabled);
    }
}
