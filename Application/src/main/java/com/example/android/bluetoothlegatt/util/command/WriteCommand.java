package com.example.android.bluetoothlegatt.util.command;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.support.v4.view.MotionEventCompat;
import android.text.format.Time;
import android.util.Log;

import com.example.android.bluetoothlegatt.TimeUtils;

import java.util.UUID;

/**
 * @author Sopheak Tuon
 * @created on 28-Feb-17
 */

public class WriteCommand {
    private static final String TAG = "WriteCommand";


    //HEADER
    public static final byte PACKAGE_HEADER0 = (byte) 0x12;
    public static final byte PACKAGE_HEADER1 = (byte) 0x34;

    //TYPE
    public static final byte SINGLE_BYTE_COMMAND_TYPE = (byte) 0x0A;
    public static final byte MULTI_BYTE_COMMAND_TYPE = (byte) 0x0B;

    //TRAILER
    public static final byte PACKAGE_TRAILER0 = (byte) 0x43;
    public static final byte PACKAGE_TRAILER1 = (byte) 0x21;


    /**
     * @param bluetoothGatt
     * @param userid
     * @return
     */
    public static int matchInfo(BluetoothGatt bluetoothGatt, int userid) {
        Log.i(TAG, "\u53d1\u9001useid\u5339\u914d\u4fe1\u606f");
        byte[] r4;
        r4 = intToBytes(userid);
        byte[] bytes = new byte[15];
        bytes[0] = PACKAGE_HEADER0;
        bytes[1] = PACKAGE_HEADER1;
        bytes[2] = MULTI_BYTE_COMMAND_TYPE;// type
        bytes[3] = (byte) 0x11;// cmd
        bytes[4] = (byte) 0x04;// length
        bytes[5] = r4[0];
        bytes[6] = r4[1];
        bytes[7] = r4[2];
        bytes[8] = r4[3];
        int a = (Integer.parseInt("0b", 16) + Integer.parseInt("11", 16)) + Integer.parseInt("04", 16);
        Log.v(TAG, "CHECK SUM = " + (a + userid));
        byte[] checkSum;
        checkSum = intToBytes(a + userid);
        bytes[9] = checkSum[0];
        bytes[10] = checkSum[1];
        bytes[11] = checkSum[2];
        bytes[12] = checkSum[3];
        bytes[13] = PACKAGE_TRAILER0;
        bytes[14] = PACKAGE_TRAILER1;
        Log.v(TAG, "Match: " + bytesToInt(checkSum, 0));
//        int count = 0;
        boolean writeStatus = false;
        BluetoothGattCharacteristic bluetoothGattCharacteristic = bluetoothGatt.getService(UUID.fromString("1aabcdef-1111-2222-0000-facebeadaaaa")).getCharacteristic(UUID.fromString("facebead-ffff-eeee-0010-facebeadaaaa"));
        while (!writeStatus) {
            bluetoothGattCharacteristic.setValue(bytes);
            writeStatus = bluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);
        }
        if (writeStatus) {
            if (bluetoothGattCharacteristic != null)
                bluetoothGatt.setCharacteristicNotification(bluetoothGattCharacteristic, true);
            return 1;
        }
        return -1;
    }

    /**
     * @param bluetoothGatt
     * @param mac
     * @return int
     */
    public static int matchInfo(BluetoothGatt bluetoothGatt, String mac) {
        int s1 = Integer.parseInt(mac.substring(0, 2), 16);
        int s2 = Integer.parseInt(mac.substring(2, 4), 16);
        int s3 = Integer.parseInt(mac.substring(4, 6), 16);
        int s4 = Integer.parseInt(mac.substring(6, 8), 16);
        int s5 = Integer.parseInt(mac.substring(8, 10), 16);
        int s6 = Integer.parseInt(mac.substring(10), 16);
        byte[] byte_info = new byte[15];

        byte_info[0] = PACKAGE_HEADER0;
        byte_info[1] = PACKAGE_HEADER1;
        byte_info[2] = MULTI_BYTE_COMMAND_TYPE;// type
        byte_info[3] = (byte) 0x11;// cmd
        byte_info[4] = (byte) 0x04;// length
        byte_info[5] = (byte) s3;
        byte_info[6] = (byte) s4;
        byte_info[7] = (byte) s5;
        byte_info[8] = (byte) s6;
        int a = (Integer.parseInt("0b", 16) + Integer.parseInt("11", 16)) + Integer.parseInt("04", 16);
        Log.v(TAG, "Match Info : " + "CHKSUM = " + ((((a + s5) + s6) + s3) + s4));
        byte[] chksum;
        chksum = intToBytes((((a + s5) + s6) + s3) + s4);
        byte_info[9] = chksum[3];
        byte_info[10] = chksum[2];
        byte_info[11] = chksum[0];
        byte_info[12] = chksum[1];
        byte_info[13] = PACKAGE_TRAILER0;
        byte_info[14] = PACKAGE_TRAILER1;
        Log.v(TAG, "Match  = " + bytesToInt(chksum, 0) + "\nbyte_info = " + bytesToHexString(byte_info));
//        int count = 0;
        boolean writeStatus = false;
        BluetoothGattCharacteristic bluetoothGattCharacteristic = bluetoothGatt.getService(UUID.fromString("1aabcdef-1111-2222-0000-facebeadaaaa")).getCharacteristic(UUID.fromString("facebead-ffff-eeee-0010-facebeadaaaa"));
        while (!writeStatus) {
            bluetoothGattCharacteristic.setValue(byte_info);
            writeStatus = bluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);
        }
        bluetoothGatt.setCharacteristicNotification(bluetoothGattCharacteristic, true);
        return -1;
    }

    /**
     * @param bluetoothGatt
     * @param status
     * @return int
     */
    public static int ackForBindRequest(BluetoothGatt bluetoothGatt, int status) {
        byte[] bytes = new byte[12];
        bytes[0] = PACKAGE_HEADER0;
        bytes[1] = PACKAGE_HEADER1;
        bytes[2] = MULTI_BYTE_COMMAND_TYPE;// type
        bytes[3] = (byte) 0x13;// cmd
        bytes[4] = (byte) 0x01;// length
        bytes[5] = (byte) status;
        byte[] chksum;
        Log.v(TAG, "Bind : " + "CHKSUM = " + (Integer.parseInt("0b", 16) + Integer.parseInt("13", 16) + Integer.parseInt("01", 16) + status));
        chksum = intToBytes(((Integer.parseInt("0b", 16) + Integer.parseInt("13", 16)) + Integer.parseInt("01", 16)) + status);
        bytes[6] = chksum[3];
        bytes[7] = chksum[2];
        bytes[8] = chksum[0];
        bytes[9] = chksum[1];
        bytes[10] = PACKAGE_TRAILER0;
        bytes[11] = PACKAGE_TRAILER1;
//        Log.v(TAG, "\u7ed1\u5b9a\u8bbe\u5907 \u54cd\u5e94\u4fe1\u606f = " + bytesToInt(chksum, 0));
        Log.v(TAG, "Ack For Bind Request " + bytesToInt(chksum, 0) + "\nbyte_info = " + bytesToHexString(bytes));
//        int count = 0;
        boolean writeStatus = false;
        BluetoothGattCharacteristic bluetoothGattCharacteristic = bluetoothGatt.getService(UUID.fromString("1aabcdef-1111-2222-0000-facebeadaaaa")).getCharacteristic(UUID.fromString("facebead-ffff-eeee-0010-facebeadaaaa"));
        while (!writeStatus) {
            bluetoothGattCharacteristic.setValue(bytes);
            writeStatus = bluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);
//            if (Constants.status_Connected) {
//                if (count > 5000) {
//                    break;
//                }
//                count++;
//            } else {
//                return -1;
//            }
        }
//        if (MainActivity.iOnBondListener != null) {
//            MainActivity.iOnBondListener.onBond(writeStatus);
//        }
        bluetoothGatt.setCharacteristicNotification(bluetoothGattCharacteristic, true);
        return writeStatus ? 1 : -1;
    }

    /**
     * @param bluetoothGatt
     * @param cmd
     * @return int
     */
    public static int secondMatch(BluetoothGatt bluetoothGatt, int cmd) {
        byte[] bytes = new byte[12];
        bytes[0] = PACKAGE_HEADER0;
        bytes[1] = PACKAGE_HEADER1;
        bytes[2] = MULTI_BYTE_COMMAND_TYPE;// type
        bytes[3] = (byte) 0x1b;// cmd
        bytes[4] = (byte) 0x01;// length
        bytes[5] = (byte) cmd;
        int chk = cmd + 39;
        Log.i(TAG, "Second Match : " + "CHKSUM==" + chk);
        byte[] r2;
        r2 = intToBytes(chk);
        bytes[6] = r2[3];
        bytes[7] = r2[2];
        bytes[8] = r2[0];
        bytes[9] = r2[1];
        bytes[10] = PACKAGE_TRAILER0;
        bytes[11] = PACKAGE_TRAILER1;
//        int count = 0;
        boolean writeStatus = false;
        Log.i(TAG, "bytes==" + bytesToHexString(bytes));
        Log.i(TAG, "bytes==" + bytesToHexString(bytes));
        BluetoothGattCharacteristic bluetoothGattCharacteristic = bluetoothGatt.getService(UUID.fromString("2aabcdef-1111-2222-0000-facebeadaaaa")).getCharacteristic(UUID.fromString("facebead-ffff-eeee-0100-facebeadaaaa"));
        while (!writeStatus) {
            bluetoothGattCharacteristic.setValue(bytes);
            writeStatus = bluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);
        }
        bluetoothGatt.setCharacteristicNotification(bluetoothGattCharacteristic, true);
        return writeStatus ? 1 : -1;
    }

    /**
     * @param bluetoothGatt
     * @return int
     */
    public static int UpdateNewTime(BluetoothGatt bluetoothGatt) {
        String times = getNowTime();
        Log.v(TAG, "\u5f00\u59cb\u540c\u6b65\u65f6\u95f4 getNowTime = " + times);
        byte[] bytes = new byte[15];
        bytes[0] = PACKAGE_HEADER0;
        bytes[1] = PACKAGE_HEADER1;
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
            temp = "0" + Integer.toHexString(b);
            bytes[10] = (byte) Integer.parseInt(temp.substring(0, 2), 16);
            bytes[9] = (byte) Integer.parseInt(temp.substring(2, 4), 16);
            Log.v(TAG, "Update Time bytes[10]= " + bytes[10] + " bytes[9]= " + bytes[9]);
        } else if (Integer.toHexString(b).length() == 4) {
            temp = Integer.toHexString(b);
            bytes[9] = (byte) Integer.parseInt(temp.substring(0, 2), 16);
            bytes[10] = (byte) Integer.parseInt(temp.substring(2, 4), 16);
        }
        bytes[11] = (byte) 0;
        bytes[12] = (byte) 0;
        bytes[13] = PACKAGE_TRAILER0;
        bytes[14] = PACKAGE_TRAILER1;
        boolean result = false;
        int count = 0;
        BluetoothGattCharacteristic bluetoothGattCharacteristic = bluetoothGatt.getService(UUID.fromString("1aabcdef-1111-2222-0000-facebeadaaaa")).getCharacteristic(UUID.fromString("facebead-ffff-eeee-0020-facebeadaaaa"));
        while (!result && count < 100000) {
//            result = LinkBleDevice.getInstance(context).setDataWriteRXCharacteristic("1aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0020-facebeadaaaa", bytes);

            bluetoothGattCharacteristic.setValue(bytes);
            result = bluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);
//            if (!Constants.status_Connected) {
//                return -1;
//            }
            count++;
        }
//        Log.i(TAG, "\u540c\u6b65\u65f6\u95f4 result = " + result);
        if (result) {
            if (bluetoothGattCharacteristic != null) {
                bluetoothGatt.setCharacteristicNotification(bluetoothGattCharacteristic, true);
            }
            return 1;
        }
        return -1;
    }

    /**
     * @param bluetoothGatt
     * @return int
     */
    public static int unbindDevice(BluetoothGatt bluetoothGatt) {
        Log.i(TAG, "Unbind with the device");
        byte[] bytes = new byte[10];
        bytes[0] = PACKAGE_HEADER0;
        bytes[1] = PACKAGE_HEADER1;
        bytes[2] = SINGLE_BYTE_COMMAND_TYPE;// type
        bytes[3] = (byte) 0x09;// cmd
        int a = Integer.parseInt("0a", 16) + Integer.parseInt("09", 16);

        Log.d(TAG, "CHKSUM==" + a);
        byte[] chksum;
        chksum = intToBytes(a);
        bytes[4] = chksum[0];
        bytes[5] = chksum[1];
        bytes[6] = chksum[2];
        bytes[7] = chksum[3];
        bytes[8] = PACKAGE_TRAILER0;
        bytes[9] = PACKAGE_TRAILER1;

        Log.v(TAG, "Unbind with the device" + bytesToInt(chksum, 0));
//        int count = 0;
        boolean writeStatus = false;
        BluetoothGattCharacteristic bluetoothGattCharacteristic = bluetoothGatt.getService(UUID.fromString("1aabcdef-1111-2222-0000-facebeadaaaa")).getCharacteristic(UUID.fromString("facebead-ffff-eeee-0010-facebeadaaaa"));
        while (!writeStatus) {
            bluetoothGattCharacteristic.setValue(bytes);
            writeStatus = bluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);
        }
        if (bluetoothGattCharacteristic != null) {
            bluetoothGatt.setCharacteristicNotification(bluetoothGattCharacteristic, true);
        }
        return writeStatus ? 1 : -1;
    }

    /**
     * ECG Measurement CMD
     *
     * @param bluetoothGatt
     * @return int
     */
    public static int measureECG(BluetoothGatt bluetoothGatt) {
        Log.i(TAG, "ECG Measurement CMD");
        byte[] bytes = new byte[10];
        bytes[0] = PACKAGE_HEADER0;
        bytes[1] = PACKAGE_HEADER1;
        bytes[2] = SINGLE_BYTE_COMMAND_TYPE;
        bytes[3] = (byte) 0x0A;
        bytes[4] = (byte) 0x14;
        bytes[5] = (byte) 0x00;
        bytes[6] = (byte) 0x00;
        bytes[7] = (byte) 0x00;
        bytes[8] = PACKAGE_TRAILER0;
        bytes[9] = PACKAGE_TRAILER1;
//        int count = 0;
        boolean writeStatus = false;

        BluetoothGattService bluetoothGattService = bluetoothGatt.getService(UUID.fromString("0aabcdef-1111-2222-0000-facebeadaaaa"));
        BluetoothGattCharacteristic bluetoothGattCharacteristic = bluetoothGattService.getCharacteristic(UUID.fromString("facebead-ffff-eeee-0002-facebeadaaaa"));
        while (!writeStatus) {
            bluetoothGattCharacteristic.setValue(bytes);
            writeStatus = bluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);
//            if (Constants.status_Connected == false)
//                return -1;
//            if (count > 5000) {
//                break;
//            } else count++;
        }
        bluetoothGatt.setCharacteristicNotification(bluetoothGattCharacteristic, true);
        BluetoothGattCharacteristic bluetoothGattCharacteristic1 = bluetoothGattService.getCharacteristic(UUID.fromString("facebead-ffff-eeee-0004-facebeadaaaa"));
        bluetoothGatt.setCharacteristicNotification(bluetoothGattCharacteristic1, true);
        Log.i(TAG, "result of ECG Measurement CMD£ºwriteStatus = " + writeStatus);
        return writeStatus ? 1 : -1;
    }

    /**
     * PPG Measurement CMD
     *
     * @param bluetoothGatt
     * @return 1 -1
     */
    public static int measurePW(BluetoothGatt bluetoothGatt) throws Exception {
        Log.i(TAG, "PPG Measurement CMD");
        byte[] bb, cc;
        bb = new byte[10];
        cc = new byte[10];
        cc[0] = bb[0] = PACKAGE_HEADER0;
        cc[1] = bb[1] = PACKAGE_HEADER1;

        cc[2] = bb[2] = SINGLE_BYTE_COMMAND_TYPE;

        bb[3] = (byte) 0x61;
        bb[4] = (byte) 0x6b;

        cc[3] = (byte) 0x03;
        cc[4] = (byte) 0x0d;

        cc[5] = bb[5] = (byte) 0x00;
        cc[6] = bb[6] = (byte) 0x00;
        cc[7] = bb[7] = (byte) 0x00;

        cc[8] = bb[8] = PACKAGE_TRAILER0;
        cc[9] = bb[9] = PACKAGE_TRAILER1;
        int count = 0;
        boolean writeStatus = false;

        BluetoothGattService bluetoothGattService = bluetoothGatt.getService(UUID.fromString("0aabcdef-1111-2222-0000-facebeadaaaa"));
        BluetoothGattCharacteristic bluetoothGattCharacteristic = bluetoothGattService.getCharacteristic(UUID.fromString("facebead-ffff-eeee-0002-facebeadaaaa"));
        while (!writeStatus) {
            bluetoothGattCharacteristic.setValue(bb);
            writeStatus = bluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);
//            if (Constants.status_Connected == false)
//                return -1;
//            if (count > 50000) {
//                break;
//            } else count++;
        }
        if (writeStatus) {
//            count = 0;
            writeStatus = false;
            while (!writeStatus) {
                bluetoothGattCharacteristic.setValue(cc);
                writeStatus = bluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);
//                if (Constants.status_Connected == false)
//                    return -1;
//                if (count > 100000) {
//                    break;
//                } else count++;
            }

            if (writeStatus) {
                BluetoothGattCharacteristic bluetoothGattCharacteristic1 = bluetoothGattService.getCharacteristic(UUID.fromString("facebead-ffff-eeee-0005-facebeadaaaa"));
                bluetoothGatt.setCharacteristicNotification(bluetoothGattCharacteristic1, true);
                BluetoothGattCharacteristic bluetoothGattCharacteristic2 = bluetoothGattService.getCharacteristic(UUID.fromString("ffacebead-ffff-eeee-0004-facebeadaaaa"));
                bluetoothGatt.setCharacteristicNotification(bluetoothGattCharacteristic2, true);
                bluetoothGatt.setCharacteristicNotification(bluetoothGattCharacteristic, true);
            }
            Log.i(TAG, "result of PPG Measurement CMD£ºwriteStatus = " + writeStatus);
            return writeStatus ? 1 : -1;
        }

        return writeStatus ? 1 : -1;
    }

    public static int initDeviceLoadCode(BluetoothGatt bluetoothGatt) {
        byte b = (byte) 1;
        Log.i(TAG, "\u4fdd\u62a4\u8bbe\u5907\u7a0b\u5e8f\u52a0\u8f7d");
        byte[] bytes = new byte[15];
        Log.i(TAG, "CHKSUM==" + 42);
        byte[] r2 = new byte[4];
        r2 = intToBytes(42);
        bytes[9] = r2[0];
        bytes[10] = r2[1];
        bytes[11] = r2[2];
        bytes[12] = r2[3];
        bytes[13] = PACKAGE_TRAILER0;
        bytes[14] = PACKAGE_TRAILER1;
        int count = 0;
        boolean writeStatus = false;
        BluetoothGattService bluetoothGattService = bluetoothGatt.getService(UUID.fromString("2aabcdef-1111-2222-0000-facebeadaaaa"));
        BluetoothGattCharacteristic bluetoothGattCharacteristic = bluetoothGattService.getCharacteristic(UUID.fromString("facebead-ffff-eeee-0100-facebeadaaaa"));
        while (!writeStatus) {
            bluetoothGattCharacteristic.setValue(bytes);
            writeStatus = bluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);
//            if (Constants.status_Connected) {
//                if (count > Constants.DELAY_TIME_LINKING_BLE) {
//                    break;
//                }
//                count++;
//            } else {
//                return -1;
//            }
        }
        Log.i(TAG, "\u53d1\u9001\u4fdd\u62a4\u8bbe\u5907\u7a0b\u5e8f\u52a0\u8f7d\u5199\u5165\u7ed3\u679c\uff1awriteStatus = " + writeStatus);
        if (!writeStatus) {
            b = (byte) -1;
        }
        bluetoothGatt.setCharacteristicNotification(bluetoothGattCharacteristic, true);
        return b;
    }

    /**
     * @param bluetoothGatt
     * @return
     */
    public static int stopMeasuring(BluetoothGatt bluetoothGatt) {
        int i = 1;
        byte[] bytes = new byte[]{PACKAGE_HEADER0, PACKAGE_HEADER1, SINGLE_BYTE_COMMAND_TYPE, (byte) 15, (byte) 25, (byte) 0, (byte) 0, (byte) 0, PACKAGE_TRAILER0, PACKAGE_TRAILER1};
        int count = 0;
        boolean writeStatus = false;
        BluetoothGattCharacteristic bluetoothGattCharacteristic = bluetoothGatt.getService(UUID.fromString("0aabcdef-1111-2222-0000-facebeadaaaa")).getCharacteristic(UUID.fromString("facebead-ffff-eeee-0001-facebeadaaaa"));
        while (!writeStatus) {
            bluetoothGattCharacteristic.setValue(bytes);
            writeStatus = bluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);
//            if (Constants.status_Connected) {
//                if (count > 5000) {
//                    break;
//                }
//                count++;
//            } else {
//                return -1;
//            }
        }
        if (!writeStatus) {
            i = -1;
        }
        return i;
    }

    /**
     * @param bluetoothGatt
     * @return int
     */
    public static int measureHr(BluetoothGatt bluetoothGatt) {
        byte[] bytes = new byte[]{PACKAGE_HEADER0, PACKAGE_HEADER1, SINGLE_BYTE_COMMAND_TYPE, (byte) 2, (byte) 12, (byte) 0, (byte) 0, (byte) 0, PACKAGE_TRAILER0, PACKAGE_TRAILER1};
//        int count = 0;
        boolean writeStatus = false;
        BluetoothGattCharacteristic bluetoothGattCharacteristic = bluetoothGatt.getService(UUID.fromString("0aabcdef-1111-2222-0000-facebeadaaaa")).getCharacteristic(UUID.fromString("facebead-ffff-eeee-0002-facebeadaaaa"));
        while (!writeStatus) {
//            count++;
            bluetoothGattCharacteristic.setValue(bytes);
            writeStatus = bluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);
//            if (Constants.status_Connected) {
//                if (count > 5000) {
//                    break;
//                }
//                count++;
//            } else {
//                return -1;
//            }
        }
        bluetoothGatt.setCharacteristicNotification(bluetoothGattCharacteristic, true);
        return writeStatus ? 1 : -1;
    }

    public static int measureBr(BluetoothGatt bluetoothGatt) {
        boolean z = true;
        byte[] bytes = new byte[]{PACKAGE_HEADER0, PACKAGE_HEADER1, SINGLE_BYTE_COMMAND_TYPE, (byte) 11, (byte) 21, (byte) 0, (byte) 0, (byte) 0, PACKAGE_TRAILER0, PACKAGE_TRAILER1};
//        int count = 0;
        boolean writeStatus = false;
        BluetoothGattCharacteristic bluetoothGattCharacteristic = bluetoothGatt.getService(UUID.fromString("0aabcdef-1111-2222-0000-facebeadaaaa")).getCharacteristic(UUID.fromString("facebead-ffff-eeee-0002-facebeadaaaa"));
        while (!writeStatus) {
            bluetoothGattCharacteristic.setValue(bytes);
            writeStatus = bluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);
//            if (Constants.status_Connected) {
//                if (count > 5000) {
//                    break;
//                }
//                count++;
//            } else {
//                return -1;
//            }
        }
        bluetoothGatt.setCharacteristicNotification(bluetoothGattCharacteristic, true);
        return z ? 1 : -1;
    }

    private static String getNowTime() {
        new Time().setToNow();
        long time = (System.currentTimeMillis() + ((long) Integer.parseInt(TimeUtils.offSetTimeZone()))) / 1000;
        return Long.toHexString(time);
    }


    public static byte[] intToBytes(int value) {
        return new byte[]{(byte) ((value >> 24) & MotionEventCompat.ACTION_MASK), (byte) ((value >> 16) & MotionEventCompat.ACTION_MASK), (byte) ((value >> 8) & MotionEventCompat.ACTION_MASK), (byte) (value & MotionEventCompat.ACTION_MASK)};
    }

    public static int bytesToInt(byte[] src, int offset) {
        return (((src[offset] & MotionEventCompat.ACTION_MASK) | ((src[offset + 1] & MotionEventCompat.ACTION_MASK) << 8)) | ((src[offset + 2] & MotionEventCompat.ACTION_MASK) << 16)) | ((src[offset + 3] & MotionEventCompat.ACTION_MASK) << 24);
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (byte b : src) {
            String hv = Integer.toHexString(b & MotionEventCompat.ACTION_MASK);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

}
