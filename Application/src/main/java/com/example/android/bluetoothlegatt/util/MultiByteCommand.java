package com.example.android.bluetoothlegatt.util;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

import static com.example.android.bluetoothlegatt.util.ConvertUtil.intToBytes;

/**
 * @author Sopheak Tuon
 * @created on 22-Feb-17
 */

public class MultiByteCommand {

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

    public static int responseTobinding(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int userid) {
        return getDataByType(bluetoothGatt, bluetoothGattCharacteristic, userid, (byte) 0x13, (byte) 0x04);
    }

    public static int secondMatch(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int userid) {
        return getDataByType(bluetoothGatt, bluetoothGattCharacteristic, userid, (byte) 0x1b, (byte) 0x01);
    }
}
