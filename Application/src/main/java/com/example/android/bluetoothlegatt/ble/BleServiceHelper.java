package com.example.android.bluetoothlegatt.ble;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;

import com.example.android.bluetoothlegatt.constant.Constants;
import com.example.android.bluetoothlegatt.PrefUtils;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class BleServiceHelper {
    private static final String TAG;

    static {
        TAG = BleServiceHelper.class.getSimpleName();
    }

    public static boolean openBle(Activity activity, boolean isBleEnabled, BluetoothAdapter mBluetoothAdapter) {
        Log.i(TAG, "Constants.isEnabled = " + Constants.isEnabled + " isBleEnabled = " + isBleEnabled + " mBluetoothAdapter.isEnabled() = " + mBluetoothAdapter.isEnabled());
        Constants.isEnabled = isBleEnabled;
        if (!isBleEnabled || mBluetoothAdapter.isEnabled()) {
            return true;
        }
        activity.startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), 1);
        Constants.isRequest = true;
        return false;
    }

//    public static int checkfirmwareUpdata(Context context, int version) {
//        Log.d(TAG, "82 \u5f53\u524d\u56fa\u4ef6\u7248\u672c version = " + version);
//        Map<String, Object> result = HttpUrlRequest.getInstance().queryVersionUpdataAction(context, version, UpdateAppManager.FirmwareCODE, "getFirmwarepath.do");
//        if (result == null) {
//            return -1;
//        }
//        VerisonInfo verisonInfo = (VerisonInfo) result.get(HttpNetworkAccess.RESPONSE_DATA_APPUP);
//        if (((Integer) result.get(HttpNetworkAccess.RESPONSE_STATUS_CODE)).intValue() == StatusCodeBean.VERSION_NOT_LASTEST.intValue()) {
//            String versionUrl = verisonInfo.getFilepath();
//            String versionName = verisonInfo.getVersionName();
//            int versioncore = verisonInfo.getVersionCode().intValue();
//            String md5 = verisonInfo.getDescription();
//            int flag = verisonInfo.getUpdateFlag().intValue();
//            Log.d(TAG, "versionUrl = " + versionUrl + " versionname = " + String.valueOf(versionName));
//            UpdataFirmware upfirmwareManager = new UpdataFirmware(context);
//            if (Constants.POWER_BATTERY < 30) {
//                return -1;
//            }
//            if (flag == 1) {
//                PrefUtils.setBoolean(context, "notify", true);
//                return upfirmwareManager.checkUpdateInfo(versionUrl, versionName, versioncore, md5, flag);
//            }
//            PrefUtils.setBoolean(context, "notify", true);
//            return upfirmwareManager.checkUpdateInfo(versionUrl, versionName, versioncore, md5);
//        }
//        StatusCodeBean.VERISON_IS_LASTEST.intValue();
//        return -1;
//    }

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

    public static void sendBroadcast(Context context, String action, boolean status) {
        if (context != null) {
            Intent intent = new Intent(action);
            intent.putExtra(action, status);
            context.sendBroadcast(intent);
        }
    }

    public static void sendBroadcast(Context context, String action, long status) {
        if (context != null) {
            Intent intent = new Intent(action);
            intent.putExtra(action, status);
            context.sendBroadcast(intent);
        }
    }

    public static void sendBroadcast(Context context, String action, int count) {
        if (context != null) {
            Intent intent = new Intent(action);
            intent.putExtra(action, count);
            context.sendBroadcast(intent);
        }
    }

    public static void sendBroadcast(Context context, String action) {
        if (context != null) {
            Intent intent = new Intent(action);
            Log.d(TAG, "147 \u53d1\u9001\u72b6\u6001\u53d8\u5316\u4fe1\u606f\u5e7f\u64ad broadcastUpdate.\n147 \u52a8\u4f5c\u662f: action = " + action);
            context.sendBroadcast(intent);
        }
    }

    public static void heartPackage(Context context) {
        if (Constants.status_Connected) {
            sendBroadcast(context, Constants.BLE_HEART_PACKAGE);
        } else if (context != null) {
//            UIToastUtil.setToast(context, context.getResources().getString(C0328R.string.ble_connect_status_ble_nolinkble));
        }
    }

    public static String getLocalMacAddressFromIp(Context context) {
        String mac_s = "OFF_LINE";
        try {
            mac_s = byte2hex(NetworkInterface.getByInetAddress(InetAddress.getByName(getLocalIpAddress())).getHardwareAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mac_s;
    }

    private static String getLocalIpAddress() {
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()) {
                Enumeration<InetAddress> enumIpAddr = ((NetworkInterface) en.nextElement()).getInetAddresses();
                while (enumIpAddr.hasMoreElements()) {
                    InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.i(TAG, "WifiPreference IpAddress = " + ex.toString());
        }
        return null;
    }

    private static String byte2hex(byte[] b) {
        StringBuffer hs = new StringBuffer(b.length);
        String stmp = "";
        for (byte b2 : b) {
            stmp = Integer.toHexString(b2 & MotionEventCompat.ACTION_MASK);
            if (stmp.length() == 1) {
                hs = hs.append("0").append(stmp);
            } else {
                hs = hs.append(stmp);
            }
        }
        return String.valueOf(hs);
    }
}
