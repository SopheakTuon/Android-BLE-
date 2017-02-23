package com.example.android.bluetoothlegatt.util;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;

import static com.example.android.bluetoothlegatt.util.ConvertUtil.intToBytes;

/**
 * @author Sopheak Tuon
 * @created on 22-Feb-17
 */

public class SingleByteCommand {

    private static int getDataByType(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, byte command) {
//        byte[] userid2byte = new byte[4];
//        userid2byte = intToBytes(userid);
        byte[] bytes = new byte[10];
        bytes[0] = (byte) 0x12;
        bytes[1] = (byte) 0x34;
        bytes[2] = (byte) 0x0a;// type
        bytes[3] = command;// cmd
//        bytes[4] = (byte) 0x04;// length
//        bytes[4] = userid2byte[0];
//        bytes[5] = userid2byte[1];
//        bytes[6] = userid2byte[2];
//        bytes[7] = userid2byte[3];
//        int a = Integer.parseInt("0a", 16) + Integer.parseInt("1", 16);
        int a = Integer.parseInt("0a", 16) + Integer.parseInt(String.valueOf(command), 16);

//        Log.v(TAG, "CHKSUM = " + (a));
        byte[] chksum = new byte[4];
        chksum = intToBytes(a);
        bytes[4] = chksum[0];
        bytes[5] = chksum[1];
        bytes[6] = chksum[2];
        bytes[7] = chksum[3];
        bytes[8] = (byte) 0x43;
        bytes[9] = (byte) 0x21;

//        Log.v(TAG, "Æ¥ÅäÐÅÏ¢: " + bytesToInt(chksum));
        int count = 0;
        boolean writeStatus = false;
        bluetoothGatt.setCharacteristicNotification(bluetoothGattCharacteristic, true);
        while (!writeStatus) {
            bluetoothGattCharacteristic.setValue(bytes);
            writeStatus = bluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);//
//            writeStatus = LinkBleDevice.getInstance(context).setDataWriteRXCharacteristic("1aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0010-facebeadaaaa", bytes);
//            if (GlobalData.status_Connected == false)
//                return -1;
//            if (count > 5000) {
//                break;
//            } else count++;
        }
//        Log.i(TAG, "·¢ËÍuseidÆ¥ÅäÐÅÏ¢Ð´Èë½á¹û£ºwriteStatus = " + writeStatus);
        String bytesString = "[";
        for (int i = 0; i < bytes.length; i++) {
            if (i == bytes.length - 1) {
                bytesString += bytes[i] + "]";
            } else {
                bytesString += bytes[i] + ",";
            }
        }
        Log.i("Data", bytesString);
        return writeStatus ? 1 : -1;
    }

    public static int getStepCount(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int userid) {
        return getDataByType(bluetoothGatt, bluetoothGattCharacteristic, (byte) 0x01);
    }

    public static int getHeartRate(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int userid) {
        return getDataByType(bluetoothGatt, bluetoothGattCharacteristic, (byte) 0x02);
    }

    public static int getPPG(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int userid) {
        return getDataByType(bluetoothGatt, bluetoothGattCharacteristic, (byte) 0x03);
    }

    public static int getCalory(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int userid) {
        return getDataByType(bluetoothGatt, bluetoothGattCharacteristic, (byte) 0x04);
    }


    public static int getSleepData(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int userid) {
        return getDataByType(bluetoothGatt, bluetoothGattCharacteristic, (byte) 0x05);
    }

    public static int getHRV(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int userid) {
        return getDataByType(bluetoothGatt, bluetoothGattCharacteristic, (byte) 0x06);
    }

    public static int getFatigue(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int userid) {
        return getDataByType(bluetoothGatt, bluetoothGattCharacteristic, (byte) 0x07);
    }

    public static int clearDistance(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int userid) {
        return getDataByType(bluetoothGatt, bluetoothGattCharacteristic, (byte) 0x08);
    }

    public static int unBind(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int userid) {
        return getDataByType(bluetoothGatt, bluetoothGattCharacteristic, (byte) 0x09);
    }


    public static int getECG(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int userid) {
        return getDataByType(bluetoothGatt, bluetoothGattCharacteristic, (byte) 0x0a);
    }


    public static int getBreathRate(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int userid) {
        return getDataByType(bluetoothGatt, bluetoothGattCharacteristic, (byte) 0x0b);
    }


    public static int getBloodPressure(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int userid) {
        return getDataByType(bluetoothGatt, bluetoothGattCharacteristic, (byte) 0x0c);
    }

    public static int getECGResult(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int userid) {
        return getDataByType(bluetoothGatt, bluetoothGattCharacteristic, (byte) 0x0d);
    }

    public static int turnOffDevice(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int userid) {
        return getDataByType(bluetoothGatt, bluetoothGattCharacteristic, (byte) 0x0e);
    }

    public static int stopCurrentMeasurement(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int userid) {
        return getDataByType(bluetoothGatt, bluetoothGattCharacteristic, (byte) 0x0f);
    }

    public static int startDataSynchronization(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int userid) {
        return getDataByType(bluetoothGatt, bluetoothGattCharacteristic, (byte) 0x21);
    }

    public static int endDataSynchronization(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int userid) {
        return getDataByType(bluetoothGatt, bluetoothGattCharacteristic, (byte) 0x22);
    }

    public static int requestToBind(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int userid) {
        return getDataByType(bluetoothGatt, bluetoothGattCharacteristic, (byte) 0x23);
    }

    public static int sos(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int userid) {
        return getDataByType(bluetoothGatt, bluetoothGattCharacteristic, (byte) 0x24);
    }
}
