package com.example.android.bluetoothlegatt.util;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.support.v4.view.MotionEventCompat;
import android.text.format.Time;
import android.util.Log;

import com.example.android.bluetoothlegatt.SmileConstants;
import com.example.android.bluetoothlegatt.TimeUtils;

import java.util.UUID;

import static com.example.android.bluetoothlegatt.ble.WriteToDevice.bytesToHexString;

/**
 * @author Sopheak Tuon
 * @created on 22-Feb-17
 */

public class MultiByteCommand {

    private static String TAG = "MultiByteCommand";

    private static int getDataByType(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int userid, byte cmd, byte length) {
//        Log.i(TAG, "·¢ËÍuseidÆ¥ÅäÐÅÏ¢");
        byte[] userid2byte = new byte[4];
        userid2byte = intToBytes(userid);
        byte[] bytes = new byte[15];
        bytes[0] = (byte) 0x12;
        bytes[1] = (byte) 0x34;
        bytes[2] = (byte) 0x0b;// type
        bytes[3] = cmd;// cmd
        bytes[4] = length;// length
        bytes[5] = userid2byte[0];
        bytes[6] = userid2byte[1];
        bytes[7] = userid2byte[2];
        bytes[8] = userid2byte[3];
//        int a = Integer.parseInt("0b", 16) + Integer.parseInt("11", 16) + Integer.parseInt("04", 16);
        int a = Integer.parseInt("0b", 16) + Integer.parseInt(String.valueOf(cmd), 16) + Integer.parseInt(String.valueOf(length), 16);

//        Log.v(TAG, "CHKSUM = " + (a + userid));
        byte[] chksum = new byte[4];
        chksum = intToBytes((a + userid));
        bytes[9] = chksum[0];
        bytes[10] = chksum[1];
        bytes[11] = chksum[2];
        bytes[12] = chksum[3];
        bytes[13] = (byte) 0x43;
        bytes[14] = (byte) 0x21;

//        Log.v(TAG, "Æ¥ÅäÐÅÏ¢: " + bytesToInt(chksum));
        int count = 0;
        boolean writeStatus = false;
        bluetoothGatt.setCharacteristicNotification(bluetoothGattCharacteristic, true);
        while (!writeStatus) {
            bluetoothGattCharacteristic.setValue(bytes);
            writeStatus = bluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);
//
//            writeStatus = LinkBleDevice.getInstance(context).setDataWriteRXCharacteristic("1aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0010-facebeadaaaa", bytes);
//            if (GlobalData.status_Connected == false)
//                return -1;
//            if (count > 5000) {
//                break;
//            } else count++;
        }
//        Log.i(TAG, "WriteStatus = " + writeStatus);
        return writeStatus ? 1 : -1;
    }

    public static int matchInfo(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int userid) {
        return getDataByType(bluetoothGatt, bluetoothGattCharacteristic, userid, (byte) 0x11, (byte) 0x04);
    }

    public static int datetimeSynchronization(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int userid) {
        return getDataByType(bluetoothGatt, bluetoothGattCharacteristic, userid, (byte) 0x12, (byte) 0x04);
    }

    public static int responseToBinding(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int userid) {
        return getDataByType(bluetoothGatt, bluetoothGattCharacteristic, userid, (byte) 0x13, (byte) 0x04);
    }

    public static int secondMatch(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int userid) {
        return getDataByType(bluetoothGatt, bluetoothGattCharacteristic, userid, (byte) 0x1b, (byte) 0x01);
    }

    public static int matchInfo(BluetoothGatt bluetoothGatt, String mac) {
        Log.i("sqs", "\u53d1\u9001MAC\u5339\u914d\u4fe1\u606f");
        int s1 = Integer.parseInt(mac.substring(0, 2), 16);
        int s2 = Integer.parseInt(mac.substring(2, 4), 16);
        int s3 = Integer.parseInt(mac.substring(4, 6), 16);
        int s4 = Integer.parseInt(mac.substring(6, 8), 16);
        int s5 = Integer.parseInt(mac.substring(8, 10), 16);
        int s6 = Integer.parseInt(mac.substring(10), 16);
        byte[] byte_info = new byte[15];
        int a = (Integer.parseInt("0b", 16) + Integer.parseInt("11", 16)) + Integer.parseInt("04", 16);
        Log.v(TAG, "CHKSUM = " + ((((a + s5) + s6) + s3) + s4));
        byte[] chksum = new byte[4];
        chksum = intToBytes((((a + s5) + s6) + s3) + s4);
        byte_info[9] = chksum[0];
        byte_info[10] = chksum[1];
        byte_info[11] = chksum[2];
        byte_info[12] = chksum[3];
        byte_info[13] = (byte) 67;
        byte_info[14] = SmileConstants.TOKEN_LITERAL_NULL;
        Log.v(TAG, "\u53d1\u9001MAC\u5339\u914d\u4fe1\u606f  = " + bytesToInt(chksum, 0) + "\nbyte_info = " + bytesToHexString(byte_info));
        int count = 0;
        boolean writeStatus = false;
        BluetoothGattCharacteristic bluetoothGattCharacteristic = bluetoothGatt.getService(UUID.fromString("1aabcdef-1111-2222-0000-facebeadaaaa")).getCharacteristic(UUID.fromString("facebead-ffff-eeee-0010-facebeadaaaa"));
        while (!writeStatus) {
            bluetoothGattCharacteristic.setValue(byte_info);
            writeStatus = bluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);
//            if (GlobalData.status_Connected) {
//                if (count > 50000) {
//                    break;
//                }
//                count++;
//            } else {
//                return -1;
//            }
        }
        Log.i(TAG, "\u53d1\u9001MAC\u5339\u914d\u4fe1\u606f\u5199\u5165\u7ed3\u679c\uff1awriteStatus = " + writeStatus);
        if (writeStatus) {
            bluetoothGatt.setCharacteristicNotification(bluetoothGattCharacteristic, true);
        }
        return writeStatus ? 1 : -1;
    }

    public static int ackForBindRequest(BluetoothGatt bluetoothGatt, int status) {
        byte b = (byte) 1;
        Log.i(TAG, "\u7ed1\u5b9a\u8bbe\u5907 \u54cd\u5e94\u4fe1\u606f");
        byte[] bytes = new byte[12];
        byte[] chksum;
        chksum = intToBytes(((Integer.parseInt("0b", 16) + Integer.parseInt("13", 16)) + Integer.parseInt("01", 16)) + status);
        bytes[6] = chksum[0];
        bytes[7] = chksum[1];
        bytes[8] = chksum[2];
        bytes[9] = chksum[3];
        bytes[10] = (byte) 67;
        bytes[11] = SmileConstants.TOKEN_LITERAL_NULL;
        Log.v(TAG, "\u7ed1\u5b9a\u8bbe\u5907 \u54cd\u5e94\u4fe1\u606f = " + bytesToInt(chksum, 0));
        int count = 0;
        boolean writeStatus = false;
        BluetoothGattCharacteristic bluetoothGattCharacteristic = bluetoothGatt.getService(UUID.fromString("1aabcdef-1111-2222-0000-facebeadaaaa")).getCharacteristic(UUID.fromString("facebead-ffff-eeee-0010-facebeadaaaa"));
        while (!writeStatus) {
            bluetoothGattCharacteristic.setValue(bytes);
            writeStatus = bluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);
//            if (GlobalData.status_Connected) {
//                if (count > 5000) {
//                    break;
//                }
//                count++;
//            } else {
//                return -1;
//            }
        }
        Log.i(TAG, "\u53d1\u9001\u5339\u914d\u4fe1\u606f\u5199\u5165\u7ed3\u679c\uff1awriteStatus = " + writeStatus);
//        if (MainActivity.iOnBondListener != null) {
//            MainActivity.iOnBondListener.onBond(writeStatus);
//        }
        if (!writeStatus) {
            b = (byte) -1;
            if (bluetoothGattCharacteristic != null)
                bluetoothGatt.setCharacteristicNotification(bluetoothGattCharacteristic, true);
        }
        return b;
    }

    public static int secondMach(BluetoothGatt bluetoothGatt) {
        byte b = (byte) 1;
        Log.i(TAG, "\u4e8c\u6b21\u5339\u914d\u6307\u4ee4");
        byte[] bytes = new byte[12];
        int chk = Integer.parseInt(String.valueOf((byte) 0x13 ), 16)+ 39;
        Log.i(TAG, "CHKSUM==" + chk);
        byte[] r2 = new byte[4];
        r2 = intToBytes(chk);
        bytes[6] = r2[0];
        bytes[7] = r2[1];
        bytes[8] = r2[2];
        bytes[9] = r2[3];
        bytes[10] = (byte) 67;
        bytes[11] = SmileConstants.TOKEN_LITERAL_NULL;
        int count = 0;
        boolean writeStatus = false;
        Log.i(TAG, "bytes==" + bytesToHexString(bytes));
        BluetoothGattCharacteristic bluetoothGattCharacteristic = bluetoothGatt.getService(UUID.fromString("2aabcdef-1111-2222-0000-facebeadaaaa")).getCharacteristic(UUID.fromString("facebead-ffff-eeee-0100-facebeadaaaa"));
        while (!writeStatus) {
//            writeStatus = LinkBleDevice.getInstance(context).setDataWriteRXCharacteristic("2aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0100-facebeadaaaa", bytes);
            bluetoothGattCharacteristic.setValue(bytes);
            writeStatus = bluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);

//            if (GlobalData.status_Connected) {
//                if (count > 20000) {
//                    break;
//                }
//                count++;
//            } else {
//                return -1;
//            }
        }
        Log.i(TAG, "\u4e8c\u6b21\u5339\u914d\u6307\u4ee4\uff1awriteStatus = " + writeStatus);
        if (!writeStatus) {
            b = (byte) -1;
            if (bluetoothGattCharacteristic != null)
                bluetoothGatt.setCharacteristicNotification(bluetoothGattCharacteristic, true);
        }
        return b;
    }

    public static int UpdateNewTime(BluetoothGatt bluetoothGatt) {
//        Log.i(TAG, "\u8bbe\u7f6e\u65f6\u533a\u504f\u79fb\u503c offSetTimeZone = " + Integer.parseInt(TimeUtils.offSetTimeZone()));
        String times = getNowTime();
        Log.v(TAG, "\u5f00\u59cb\u540c\u6b65\u65f6\u95f4 getNowTime = " + times);
        byte[] bytes = new byte[15];
        bytes[0] = (byte) 18;
        bytes[1] = SmileConstants.TOKEN_KEY_LONG_STRING;
        bytes[2] = (byte) 11;
        bytes[3] = (byte) 18;
        bytes[4] = (byte) 4;
        bytes[5] = (byte) Integer.parseInt(times.substring(6, 8), 16);
        bytes[6] = (byte) Integer.parseInt(times.substring(4, 6), 16);
        bytes[7] = (byte) Integer.parseInt(times.substring(2, 4), 16);
        bytes[8] = (byte) Integer.parseInt(times.substring(0, 2), 16);
        bytes[9] = (byte) -92;
        bytes[10] = (byte) 1;
        int b = (((((Integer.parseInt(times.substring(6, 8), 16) + Integer.parseInt(times.substring(4, 6), 16)) + Integer.parseInt(times.substring(2, 4), 16)) + Integer.parseInt(times.substring(0, 2), 16)) + Integer.parseInt("0b", 16)) + Integer.parseInt("12", 16)) + Integer.parseInt("04", 16);
        String temp;
        if (Integer.toHexString(b).length() == 0) {
            bytes[9] = (byte) 0;
            bytes[10] = (byte) 0;
        } else if (Integer.toHexString(b).length() == 1) {
            bytes[9] = (byte) Integer.parseInt(Integer.toHexString(b), 16);
            bytes[10] = (byte) 0;
        } else if (Integer.toHexString(b).length() == 2) {
            bytes[9] = (byte) Integer.parseInt(Integer.toHexString(b), 16);
            bytes[10] = (byte) 0;
        } else if (Integer.toHexString(b).length() == 3) {
            temp = "0" + Integer.toHexString(b).toString();
            bytes[10] = (byte) Integer.parseInt(temp.substring(0, 2), 16);
            bytes[9] = (byte) Integer.parseInt(temp.substring(2, 4), 16);
            Log.v(TAG, "\u5f00\u59cb\u540c\u6b65\u65f6\u95f4 bytes[10]= " + bytes[10] + " bytes[9]= " + bytes[9]);
        } else if (Integer.toHexString(b).length() == 4) {
            temp = Integer.toHexString(b).toString();
            bytes[9] = (byte) Integer.parseInt(temp.substring(0, 2), 16);
            bytes[10] = (byte) Integer.parseInt(temp.substring(2, 4), 16);
        }
        bytes[11] = (byte) 0;
        bytes[12] = (byte) 0;
        bytes[13] = (byte) 67;
        bytes[14] = SmileConstants.TOKEN_LITERAL_NULL;
        boolean result = false;
        int count = 0;
        BluetoothGattCharacteristic bluetoothGattCharacteristic = null;
        bluetoothGattCharacteristic = bluetoothGatt.getService(UUID.fromString("1aabcdef-1111-2222-0000-facebeadaaaa")).getCharacteristic(UUID.fromString("facebead-ffff-eeee-0020-facebeadaaaa"));
        while (!result && count < 100000) {
//            result = LinkBleDevice.getInstance(context).setDataWriteRXCharacteristic("1aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0020-facebeadaaaa", bytes);

            bluetoothGattCharacteristic.setValue(bytes);
            result = bluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);
//            if (!GlobalData.status_Connected) {
//                return -1;
//            }
            count++;
        }
        Log.i(TAG, "\u540c\u6b65\u65f6\u95f4 result = " + result);
        if (result) {
            if (bluetoothGattCharacteristic != null) {
                bluetoothGatt.setCharacteristicNotification(bluetoothGattCharacteristic, true);
            }
            return 1;
        }
        return -1;
    }

    private static String getNowTime() {
        new Time().setToNow();
        long time = (System.currentTimeMillis() + ((long) Integer.parseInt(TimeUtils.offSetTimeZone()))) / 1000;
//        Log.v(TAG, "\u5f00\u59cb\u540c\u6b65\u65f6\u95f4 getNowTime = " + time);
//        Log.i(TAG, "\u5f00\u59cb\u540c\u6b65\u65f6\u95f4 getNowTime = " + time);
//        Log.d(TAG, "\u5f00\u59cb\u540c\u6b65\u65f6\u95f4 getNowTime = " + time);
        return Long.toHexString(time);
    }

    private static byte[] intToBytes(int value) {
        return new byte[]{(byte) ((value >> 24) & MotionEventCompat.ACTION_MASK), (byte) ((value >> 16) & MotionEventCompat.ACTION_MASK), (byte) ((value >> 8) & MotionEventCompat.ACTION_MASK), (byte) (value & MotionEventCompat.ACTION_MASK)};
    }

    private static int bytesToInt(byte[] src, int offset) {
        return (((src[offset] & MotionEventCompat.ACTION_MASK) | ((src[offset + 1] & MotionEventCompat.ACTION_MASK) << 8)) | ((src[offset + 2] & MotionEventCompat.ACTION_MASK) << 16)) | ((src[offset + 3] & MotionEventCompat.ACTION_MASK) << 24);
    }
}
