package com.example.android.bluetoothlegatt.ble;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.text.format.Time;
import android.util.Log;

import com.example.android.bluetoothlegatt.constant.Constants;
import com.example.android.bluetoothlegatt.SmileConstants;
import com.example.android.bluetoothlegatt.TimeUtils;

public class WriteToDevice {
    private static final int ID_MARK = 170;
    private static final String TAG;
    private static byte[] dis2byte;
    private static byte[] bytes;

    static {
        TAG = WriteToDevice.class.getSimpleName();
    }

    public static int heartRate(Context context) {
        return 1;
    }

    private static int identifyClientStyle(Context context) {
        int i = 1;
        Log.i(TAG, "\u6570\u636e\u5199\u5165\u56de\u8c03---\u5199\u5165\u5f00\u59cb\u65f6\u95f4 = " + System.currentTimeMillis() + "\n\u544a\u8bc9\u624b\u73af\u662fapp\u8fde\u63a5,\u94fe\u63a5\u5e73\u53f0\u8fa8\u522b");
        int count = 0;
        boolean result = false;
        byte[] data = new byte[]{(byte) -86};
        while (!result) {
            result = LinkBleDevice.getInstance(context).setDataWriteRXCharacteristic("2aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0100-facebeadaaaa", data);
            if (Constants.status_Connected) {
                if (count > 100) {
                    break;
                }
                count++;
            } else {
                return -1;
            }
        }
        if (!result) {
            i = -1;
        }
        return i;
    }

    public static int UpdateNewTime(Context context) {
        Log.i(TAG, "\u8bbe\u7f6e\u65f6\u533a\u504f\u79fb\u503c offSetTimeZone = " + Integer.parseInt(TimeUtils.offSetTimeZone()));
        String times = getNowTime();
        Log.v(TAG, "\u5f00\u59cb\u540c\u6b65\u65f6\u95f4 getNowTime = " + times);
        Log.i("sqs", "\u5f00\u59cb\u540c\u6b65\u65f6\u95f4 getNowTime = " + times);
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
        while (!result && count < 100000) {
            result = LinkBleDevice.getInstance(context).setDataWriteRXCharacteristic("1aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0020-facebeadaaaa", bytes);
            if (!Constants.status_Connected) {
                return -1;
            }
            count++;
        }
        Log.i(TAG, "\u540c\u6b65\u65f6\u95f4 result = " + result);
        if (result) {
            return 1;
        }
        return -1;
    }

    public static int sendStandardBP(Context context, int sys, int dis) {
        Log.i(TAG, "\u53d1\u9001\u8840\u538b\u6807\u5b9a\u503c");
        byte[] sys2byte = new byte[4];
        dis2byte = new byte[4];
        sys2byte = intToBytes(sys);
        dis2byte = intToBytes(dis);
        bytes = new byte[19];
        int a = (Integer.parseInt("0b", 16) + Integer.parseInt("19", 16)) + Integer.parseInt("08", 16);
        Log.v(TAG, "CHKSUM = " + ((a + sys) + dis));
        byte[] chksum = new byte[4];
        chksum = intToBytes((a + sys) + dis);
        bytes[13] = chksum[0];
        bytes[14] = chksum[1];
        bytes[15] = chksum[2];
        bytes[16] = chksum[3];
        bytes[17] = (byte) 67;
        bytes[18] = SmileConstants.TOKEN_LITERAL_NULL;
        Log.d(TAG, "\u53d1\u9001\u8840\u538b\u6807\u5b9a\u503c \u5339\u914d\u4fe1\u606f : " + bytesToInt(chksum, 0));
        int count = 0;
        boolean writeStatus = false;
        while (!writeStatus) {
            writeStatus = LinkBleDevice.getInstance(context).setDataWriteRXCharacteristic("1aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0010-facebeadaaaa", bytes);
            if (Constants.status_Connected) {
                if (count > 5000) {
                    break;
                }
                count++;
            } else {
                return -1;
            }
        }
        Log.i(TAG, "\u53d1\u9001\u8840\u538b\u6807\u5b9a\u503c\u5199\u5165\u7ed3\u679c\uff1awriteStatus = " + writeStatus);
        if (writeStatus) {
            return 1;
        }
        return -1;
    }

    public static int matchInfo(Context context, int userid) {
        Log.i(TAG, "\u53d1\u9001useid\u5339\u914d\u4fe1\u606f");
        byte[] r4 = new byte[4];
        r4 = intToBytes(userid);
        bytes = new byte[15];
        int a = (Integer.parseInt("0b", 16) + Integer.parseInt("11", 16)) + Integer.parseInt("04", 16);
        Log.v(TAG, "CHKSUM = " + (a + userid));
        byte[] chksum = new byte[4];
        chksum = intToBytes(a + userid);
        bytes[9] = chksum[0];
        bytes[10] = chksum[1];
        bytes[11] = chksum[2];
        bytes[12] = chksum[3];
        bytes[13] = (byte) 67;
        bytes[14] = SmileConstants.TOKEN_LITERAL_NULL;
        Log.v(TAG, "\u5339\u914d\u4fe1\u606f: " + bytesToInt(chksum, 0));
        int count = 0;
        boolean writeStatus = false;
        while (!writeStatus) {
            writeStatus = LinkBleDevice.getInstance(context).setDataWriteRXCharacteristic("1aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0010-facebeadaaaa", bytes);
            if (Constants.status_Connected) {
                if (count > 5000) {
                    break;
                }
                count++;
            } else {
                return -1;
            }
        }
        Log.i(TAG, "\u53d1\u9001useid\u5339\u914d\u4fe1\u606f\u5199\u5165\u7ed3\u679c\uff1awriteStatus = " + writeStatus);
        if (writeStatus) {
            return 1;
        }
        return -1;
    }

    public static int matchInfo(Context context, String mac) {
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
        while (!writeStatus) {
            writeStatus = LinkBleDevice.getInstance(context).setDataWriteRXCharacteristic("1aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0010-facebeadaaaa", byte_info);
            if (Constants.status_Connected) {
                if (count > 50000) {
                    break;
                }
                count++;
            } else {
                return -1;
            }
        }
        Log.i(TAG, "\u53d1\u9001MAC\u5339\u914d\u4fe1\u606f\u5199\u5165\u7ed3\u679c\uff1awriteStatus = " + writeStatus);
        if (writeStatus) {
            return 1;
        }
        return -1;
    }

    public static int ackForBindRequest(Context context, int status) {
        byte b = (byte) 1;
        Log.i(TAG, "\u7ed1\u5b9a\u8bbe\u5907 \u54cd\u5e94\u4fe1\u606f");
        bytes = new byte[12];
        byte[] chksum = new byte[4];
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
        while (!writeStatus) {
            writeStatus = LinkBleDevice.getInstance(context).setDataWriteRXCharacteristic("1aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0010-facebeadaaaa", bytes);
            if (Constants.status_Connected) {
                if (count > 5000) {
                    break;
                }
                count++;
            } else {
                return -1;
            }
        }
        Log.i(TAG, "\u53d1\u9001\u5339\u914d\u4fe1\u606f\u5199\u5165\u7ed3\u679c\uff1awriteStatus = " + writeStatus);
//        if (MainActivity.iOnBondListener != null) {
//            MainActivity.iOnBondListener.onBond(writeStatus);
//        }
        if (!writeStatus) {
            b = (byte) -1;
        }
        return b;
    }

    public static int unbindDevice(Context context) {
        int i = 1;
        Log.i(TAG, "\u89e3\u9664\u5bf9\u8bbe\u5907\u7684\u7ed1\u5b9a");
        bytes = new byte[10];
        byte[] r2 = new byte[4];
        r2 = intToBytes(Integer.parseInt("0a", 16) + Integer.parseInt("09", 16));
        bytes[4] = r2[0];
        bytes[5] = r2[1];
        bytes[6] = r2[2];
        bytes[7] = r2[3];
        bytes[8] = (byte) 67;
        bytes[9] = SmileConstants.TOKEN_LITERAL_NULL;
        Log.v(TAG, "\u89e3\u9664\u7ed1\u5b9a \u54cd\u5e94\u4fe1\u606f" + bytesToInt(r2, 0));
        int count = 0;
        boolean writeStatus = false;
        while (!writeStatus) {
            writeStatus = LinkBleDevice.getInstance(context).setDataWriteRXCharacteristic("1aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0010-facebeadaaaa", bytes);
            if (Constants.status_Connected) {
                if (count > 5000) {
                    break;
                }
                count++;
            } else {
                return -1;
            }
        }
        Log.i(TAG, "\u89e3\u9664\u5bf9\u8bbe\u5907\u7684\u7ed1\u5b9a\u5199\u5165\u7ed3\u679c\uff1awriteStatus = " + writeStatus);
        if (!writeStatus) {
            i = -1;
        }
        return i;
    }

    public static int stopMeasuring(Context context) {
        int i = 1;
        Log.i(TAG, "\u505c\u6b62\u5f53\u524d\u6d4b\u91cf\uff0c\u8ba9\u8bbe\u5907\u5173\u706f");
        byte[] bytes = new byte[]{(byte) 18, SmileConstants.TOKEN_KEY_LONG_STRING, (byte) 10, (byte) 15, (byte) 25, (byte) 0, (byte) 0, (byte) 0, (byte) 67, SmileConstants.TOKEN_LITERAL_NULL};
        int count = 0;
        boolean writeStatus = false;
        while (!writeStatus) {
            writeStatus = LinkBleDevice.getInstance(context).setDataWriteRXCharacteristic("0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0001-facebeadaaaa", bytes);
            if (Constants.status_Connected) {
                if (count > 5000) {
                    break;
                }
                count++;
            } else {
                return -1;
            }
        }
        Log.i(TAG, "\u505c\u6b62\u5f53\u524d\u6d4b\u91cf \u53d1\u9001\u8bbe\u5907\u5173\u706f\u5199\u5165\u7ed3\u679c\uff1awriteStatus = " + writeStatus);
        if (!writeStatus) {
            i = -1;
        }
        return i;
    }

    public static int letMeashineDown(Context context) {
        int i = 1;
        Log.i(TAG, "\u8ba9\u8bbe\u5907\u5173\u673a\u6307\u4ee4");
        byte[] bytes = new byte[]{(byte) 18, SmileConstants.TOKEN_KEY_LONG_STRING, (byte) 10, (byte) 14, (byte) 24, (byte) 0, (byte) 0, (byte) 0, (byte) 67, SmileConstants.TOKEN_LITERAL_NULL};
        int count = 0;
        boolean writeStatus = false;
        while (!writeStatus) {
            writeStatus = LinkBleDevice.getInstance(context).setDataWriteRXCharacteristic("0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0001-facebeadaaaa", bytes);
            if (Constants.status_Connected) {
                if (count > 5000) {
                    break;
                }
                count++;
            } else {
                return -1;
            }
        }
        Log.i(TAG, "\u53d1\u9001\u8bbe\u5907\u5173\u673a\u6307\u4ee4\u5199\u5165\u7ed3\u679c\uff1awriteStatus = " + writeStatus);
        if (!writeStatus) {
            i = -1;
        }
        return i;
    }

    public static int measureHr(Context context) {
        boolean z = true;
        Log.i(TAG, "\u5fc3\u7387\u6d4b\u91cf\u6307\u4ee4");
        byte[] bytes = new byte[]{(byte) 18, SmileConstants.TOKEN_KEY_LONG_STRING, (byte) 10, (byte) 2, (byte) 12, (byte) 0, (byte) 0, (byte) 0, (byte) 67, SmileConstants.TOKEN_LITERAL_NULL};
        int count = 0;
        boolean writeStatus = false;
        while (!writeStatus) {
            count++;
            writeStatus = LinkBleDevice.getInstance(context).setDataWriteRXCharacteristic("0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0002-facebeadaaaa", bytes);
            if (Constants.status_Connected) {
                if (count > 5000) {
                    break;
                }
                count++;
            } else {
                return -1;
            }
        }
        if (writeStatus) {
            LinkBleDevice.getInstance(context).setNotifCharacteristic("0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0002-facebeadaaaa", true);
        }
        Log.i(TAG, "\u53d1\u9001\u5fc3\u7387\u6d4b\u91cf\u6307\u4ee4\u5199\u5165\u7ed3\u679c\uff1awriteStatus = " + writeStatus);
        if (!writeStatus) {
            z = true;
        }
        return z? 1 : -1;
    }

    public static int measureBp(Context context) {
        boolean z = true;
        Log.i(TAG, "\u8840\u538b\u6d4b\u91cf\u6307\u4ee4");
        byte[] bytes = new byte[]{(byte) 18, SmileConstants.TOKEN_KEY_LONG_STRING, (byte) 10, (byte) 12, (byte) 22, (byte) 0, (byte) 0, (byte) 0, (byte) 67, SmileConstants.TOKEN_LITERAL_NULL};
        int count = 0;
        boolean writeStatus = false;
        while (!writeStatus) {
            writeStatus = LinkBleDevice.getInstance(context).setDataWriteRXCharacteristic("0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0002-facebeadaaaa", bytes);
            if (Constants.status_Connected) {
                if (count > Constants.DELAY_TIME_LINKING_BLE) {
                    break;
                }
                count++;
            } else {
                return -1;
            }
        }
        if (writeStatus) {
            LinkBleDevice.getInstance(context).setNotifCharacteristic("0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0002-facebeadaaaa", true);
            LinkBleDevice.getInstance(context).setNotifCharacteristic("2aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0100-facebeadaaaa", true);
        }
        Log.i(TAG, "\u53d1\u9001\u8840\u538b\u6d4b\u91cf\u6307\u4ee4\u5199\u5165\u7ed3\u679c\uff1awriteStatus = " + writeStatus);
        if (!writeStatus) {
            z = true;
        }
        return z ? 1 : -1;
    }

    public static int measureECG(Context context) {
        boolean z = true;
        Log.i(TAG, "ECG\u6d4b\u91cf\u6307\u4ee4");
        byte[] bytes = new byte[]{(byte) 18, SmileConstants.TOKEN_KEY_LONG_STRING, (byte) 10, (byte) 10, (byte) 20, (byte) 0, (byte) 0, (byte) 0, (byte) 67, SmileConstants.TOKEN_LITERAL_NULL};
        int count = 0;
        boolean writeStatus = false;
        while (!writeStatus) {
            writeStatus = LinkBleDevice.getInstance(context).setDataWriteRXCharacteristic("0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0002-facebeadaaaa", bytes);
            if (Constants.status_Connected) {
                if (count > 5000) {
                    break;
                }
                count++;
            } else {
                return -1;
            }
        }
        if (writeStatus) {
            LinkBleDevice.getInstance(context).setNotifCharacteristic("0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0004-facebeadaaaa", true);
            LinkBleDevice.getInstance(context).setNotifCharacteristic("0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0002-facebeadaaaa", true);
        }
        Log.i(TAG, "\u53d1\u9001ECG\u6d4b\u91cf\u6307\u4ee4\u5199\u5165\u7ed3\u679c\uff1awriteStatus = " + writeStatus);
        if (!writeStatus) {
            z = true;
        }
        return z? 1: -1;
    }

    public static int measureBr(Context context) {
        boolean z = true;
        Log.i(TAG, "\u547c\u5438\u9891\u7387\u6d4b\u91cf\u6307\u4ee4");
        byte[] bytes = new byte[]{(byte) 18, SmileConstants.TOKEN_KEY_LONG_STRING, (byte) 10, (byte) 11, (byte) 21, (byte) 0, (byte) 0, (byte) 0, (byte) 67, SmileConstants.TOKEN_LITERAL_NULL};
        int count = 0;
        boolean writeStatus = false;
        while (!writeStatus) {
            writeStatus = LinkBleDevice.getInstance(context).setDataWriteRXCharacteristic("0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0002-facebeadaaaa", bytes);
            if (Constants.status_Connected) {
                if (count > 5000) {
                    break;
                }
                count++;
            } else {
                return -1;
            }
        }
        if (writeStatus) {
            LinkBleDevice.getInstance(context).setNotifCharacteristic("0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0002-facebeadaaaa", true);
        }
        Log.i(TAG, "\u53d1\u9001\u547c\u5438\u9891\u7387\u6d4b\u91cf\u6307\u4ee4\u5199\u5165\u7ed3\u679c\uff1awriteStatus = " + writeStatus);
        if (!writeStatus) {
            z = true;
        }
        return z? 1: -1;
    }

    public static int measureMF(Context context) {
        boolean z = true;
        Log.i(TAG, "\u5fc3\u60c5\u75b2\u52b3\u503c\u6d4b\u91cf\u6307\u4ee4");
        byte[] bytes = new byte[]{(byte) 18, SmileConstants.TOKEN_KEY_LONG_STRING, (byte) 10, (byte) 6, (byte) 16, (byte) 0, (byte) 0, (byte) 0, (byte) 67, SmileConstants.TOKEN_LITERAL_NULL};
        int count = 0;
        boolean writeStatus = false;
        while (!writeStatus) {
            writeStatus = LinkBleDevice.getInstance(context).setDataWriteRXCharacteristic("0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0003-facebeadaaaa", bytes);
            if (Constants.status_Connected) {
                if (count > 5000) {
                    break;
                }
                count++;
            } else {
                return -1;
            }
        }
        if (writeStatus) {
            LinkBleDevice.getInstance(context).setNotifCharacteristic("0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0003-facebeadaaaa", true);
        }
        Log.i(TAG, "\u53d1\u9001\u5fc3\u60c5\u75b2\u52b3\u503c\u6d4b\u91cf\u6307\u4ee4\u5199\u5165\u7ed3\u679c\uff1awriteStatus = " + writeStatus);
        if (!writeStatus) {
            z = true;
        }
        return z? 1: -1;
    }

    public static int measurePW(Context context) {
        boolean z = true;
        Log.i(TAG, "\u8109\u640f\u6ce2\u6d4b\u91cf\u6307\u4ee4");
        byte[] bb = new byte[10];
        byte[] cc = new byte[]{(byte) 18, (byte) 18, SmileConstants.TOKEN_KEY_LONG_STRING, SmileConstants.TOKEN_KEY_LONG_STRING, (byte) 10, (byte) 10, (byte) 97, (byte) 107, (byte) 3, (byte) 13};
        bb[5] = (byte) 0;
        cc[5] = (byte) 0;
        bb[6] = (byte) 0;
        cc[6] = (byte) 0;
        bb[7] = (byte) 0;
        cc[7] = (byte) 0;
        bb[8] = (byte) 67;
        cc[8] = (byte) 67;
        bb[9] = SmileConstants.TOKEN_LITERAL_NULL;
        cc[9] = SmileConstants.TOKEN_LITERAL_NULL;
        int count = 0;
        boolean writeStatus = false;
        while (!writeStatus) {
            writeStatus = LinkBleDevice.getInstance(context).setDataWriteRXCharacteristic("0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0002-facebeadaaaa", bb);
            if (Constants.status_Connected) {
                if (count > 50000) {
                    break;
                }
                count++;
            } else {
                return -1;
            }
        }
        if (writeStatus) {
            count = 0;
            writeStatus = false;
            while (!writeStatus) {
                writeStatus = LinkBleDevice.getInstance(context).setDataWriteRXCharacteristic("0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0002-facebeadaaaa", cc);
                if (Constants.status_Connected) {
                    if (count > 100000) {
                        break;
                    }
                    count++;
                } else {
                    return -1;
                }
            }
            if (writeStatus) {
                LinkBleDevice.getInstance(context).setNotifCharacteristic("0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0005-facebeadaaaa", true);
                LinkBleDevice.getInstance(context).setNotifCharacteristic("0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0004-facebeadaaaa", true);
                LinkBleDevice.getInstance(context).setNotifCharacteristic("0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0002-facebeadaaaa", true);
            }
            Log.i(TAG, "\u53d1\u9001\u8109\u640f\u6ce2\u6d4b\u91cf\u6307\u4ee4\u5199\u5165\u7ed3\u679c\uff1awriteStatus = " + writeStatus);
            if (!writeStatus) {
                z = true;
            }
            return z? 1: -1;
        }
        Log.i(TAG, "\u53d1\u9001\u8109\u640f\u6ce2\u6d4b\u91cf\u6307\u4ee4\u5199\u5165\u7ed3\u679c\uff1awriteStatus = " + writeStatus);
        return -1;
    }

    public static int initDeviceLoadCode(Context context) {
        byte b = (byte) 1;
        Log.i(TAG, "\u4fdd\u62a4\u8bbe\u5907\u7a0b\u5e8f\u52a0\u8f7d");
        bytes = new byte[15];
        Log.i(TAG, "CHKSUM==" + 42);
        byte[] r2 = new byte[4];
        r2 = intToBytes(42);
        bytes[9] = r2[0];
        bytes[10] = r2[1];
        bytes[11] = r2[2];
        bytes[12] = r2[3];
        bytes[13] = (byte) 67;
        bytes[14] = SmileConstants.TOKEN_LITERAL_NULL;
        int count = 0;
        boolean writeStatus = false;
        while (!writeStatus) {
            writeStatus = LinkBleDevice.getInstance(context).setDataWriteRXCharacteristic("2aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0100-facebeadaaaa", bytes);
            if (Constants.status_Connected) {
                if (count > Constants.DELAY_TIME_LINKING_BLE) {
                    break;
                }
                count++;
            } else {
                return -1;
            }
        }
        Log.i(TAG, "\u53d1\u9001\u4fdd\u62a4\u8bbe\u5907\u7a0b\u5e8f\u52a0\u8f7d\u5199\u5165\u7ed3\u679c\uff1awriteStatus = " + writeStatus);
        if (!writeStatus) {
            b = (byte) -1;
        }
        return b;
    }

    public static int secondMach(Context context, int cmd) {
        byte b = (byte) 1;
        Log.i(TAG, "\u4e8c\u6b21\u5339\u914d\u6307\u4ee4");
        bytes = new byte[12];
        int chk = cmd + 39;
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
        Log.i(TAG, "bytes==" + bytesToHexString(bytes));
        while (!writeStatus) {
            writeStatus = LinkBleDevice.getInstance(context).setDataWriteRXCharacteristic("2aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0100-facebeadaaaa", bytes);
            if (Constants.status_Connected) {
                if (count > 20000) {
                    break;
                }
                count++;
            } else {
                return -1;
            }
        }
        Log.i(TAG, "\u4e8c\u6b21\u5339\u914d\u6307\u4ee4\uff1awriteStatus = " + writeStatus);
        if (!writeStatus) {
            b = (byte) -1;
        }
        return b;
    }

    public static int APPVersion(Context context, String version) {
        Log.i(TAG, "APP\u7248\u672c\u53f7\u53d1\u9001");
        byte[] vers = new byte[4];
        String vers16 = bytesToHexString(intToBytes(Integer.parseInt(version)));
        bytes = new byte[15];
        int chk = (((Integer.parseInt(vers16.substring(0, 2), 16) + 43) + Integer.parseInt(vers16.substring(2, 4), 16)) + Integer.parseInt(vers16.substring(4, 6), 16)) + Integer.parseInt(vers16.substring(6), 16);
        Log.i(TAG, "CHKSUM==" + chk);
        byte[] r2 = new byte[4];
        r2 = intToBytes(chk);
        bytes[9] = r2[0];
        bytes[10] = r2[1];
        bytes[11] = r2[2];
        bytes[12] = r2[3];
        bytes[13] = (byte) 67;
        bytes[14] = SmileConstants.TOKEN_LITERAL_NULL;
        int count = 0;
        boolean writeStatus = false;
        Log.i(TAG, "bytes==" + bytesToHexString(bytes));
        Log.i(TAG, "bytes==" + bytesToHexString(bytes));
        while (!writeStatus) {
            writeStatus = LinkBleDevice.getInstance(context).setDataWriteRXCharacteristic("2aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0100-facebeadaaaa", bytes);
            if (Constants.status_Connecting) {
                if (count > 50000) {
                    break;
                }
                count++;
            } else {
                return -1;
            }
        }
        Log.i(TAG, "APP\u7248\u672c\u53f7\u53d1\u9001\u7a0b\u5e8f\u52a0\u8f7d\u5199\u5165\u7ed3\u679c\uff1awriteStatus = " + writeStatus);
        if (writeStatus) {
            return 1;
        }
        return -1;
    }

    public static int getSteps(Context context) {
        boolean z = true;
        Log.i(TAG, "\u83b7\u53d6\u6b65\u6570\u6307\u4ee4");
        byte[] bytes = new byte[]{(byte) 18, SmileConstants.TOKEN_KEY_LONG_STRING, (byte) 10, (byte) 1, (byte) 11, (byte) 0, (byte) 0, (byte) 0, (byte) 67, SmileConstants.TOKEN_LITERAL_NULL};
        int count = 0;
        boolean writeStatus = false;
        while (!writeStatus) {
            count++;
            writeStatus = LinkBleDevice.getInstance(context).setDataWriteRXCharacteristic("0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0001-facebeadaaaa", bytes);
            if (Constants.status_Connected) {
                if (count > 5000) {
                    break;
                }
                count++;
            } else {
                return -1;
            }
        }
        if (writeStatus) {
            LinkBleDevice.getInstance(context).setNotifCharacteristic("0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0001-facebeadaaaa", true);
        }
        Log.i(TAG, "\u53d1\u9001\u83b7\u53d6\u6b65\u6570\u6307\u4ee4\u5199\u5165\u7ed3\u679c\uff1awriteStatus = " + writeStatus);
        if (!writeStatus) {
            z = true;
        }
        return z? 1: -1;
    }

    public static int setLedLight(Context context, int data1, int data2) {
        boolean z = true;
        Log.i("sqs", "\u53d1\u9001\u8bbe\u7f6eled\u4eae\u5ea6");
        byte[] r2 = new byte[4];
        r2 = intToBytes((data1 + 110) + data2);
        byte[] bytes = new byte[]{(byte) 18, SmileConstants.TOKEN_KEY_LONG_STRING, (byte) 11, (byte) 97, (byte) 2, (byte) data1, (byte) data2, r2[0], r2[1], r2[2], r2[3], (byte) 67, SmileConstants.TOKEN_LITERAL_NULL};
        int count = 0;
        boolean writeStatus = false;
        while (!writeStatus) {
            count++;
            writeStatus = LinkBleDevice.getInstance(context).setDataWriteRXCharacteristic("2aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0100-facebeadaaaa", bytes);
            if (Constants.status_Connected) {
                if (count > 5000) {
                    break;
                }
                count++;
            } else {
                return -1;
            }
        }
        if (writeStatus) {
            LinkBleDevice.getInstance(context).setNotifCharacteristic("0aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0002-facebeadaaaa", true);
        }
        if (!writeStatus) {
            z = true;
        }
        return z? 1: -1;
    }

    public static int startSendDecryToken(Context context, int dataLength) {
        byte[] r4 = new byte[4];
        String vers16 = bytesToHexString(intToBytes(dataLength));
        byte[] bb = new byte[15];
        byte[] r2 = new byte[4];
        r2 = intToBytes((((Integer.parseInt(vers16.substring(0, 2), 16) + 44) + Integer.parseInt(vers16.substring(2, 4), 16)) + Integer.parseInt(vers16.substring(4, 6), 16)) + Integer.parseInt(vers16.substring(6), 16));
        bb[9] = r2[0];
        bb[10] = r2[1];
        bb[11] = r2[2];
        bb[12] = r2[3];
        bb[13] = (byte) 67;
        bb[14] = SmileConstants.TOKEN_LITERAL_NULL;
        int count = 0;
        boolean writeStatus = false;
        while (!writeStatus) {
            writeStatus = LinkBleDevice.getInstance(context).setDataWriteRXCharacteristic("1aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0010-facebeadaaaa", bb);
            if (Constants.status_Connected) {
                if (count > 50000) {
                    break;
                }
                count++;
            } else {
                return -1;
            }
        }
        if (writeStatus) {
            return 1;
        }
        return -1;
    }

    public static void sendDecryTokenContent1(Context context, byte[] eightByte) {
        for (int i = 0; i < eightByte.length / 8; i++) {
            byte[] aa = new byte[8];
            for (int j = 0; j < 8; j++) {
                aa[j] = eightByte[(i * 8) + j];
            }
            sendDecryTokenContent(context, aa);
        }
    }

    public static int sendDecryTokenContent(Context context, byte[] eightStr) {
        byte[] r7 = new byte[8];
        r7 = eightStr;
        String bytesToHexString = bytesToHexString(r7);
        byte[] bb = new byte[19];
        bb[0] = (byte) 18;
        bb[1] = SmileConstants.TOKEN_KEY_LONG_STRING;
        bb[2] = (byte) 11;
        bb[3] = (byte) 30;
        bb[4] = (byte) 8;
        for (int i = 0; i < 8; i++) {
            bb[i + 5] = r7[i];
        }
        byte[] r5 = new byte[4];
        r5 = intToBytes((((((((Integer.parseInt(bytesToHexString.substring(0, 2), 16) + 49) + Integer.parseInt(bytesToHexString.substring(2, 4), 16)) + Integer.parseInt(bytesToHexString.substring(4, 6), 16)) + Integer.parseInt(bytesToHexString.substring(6, 8), 16)) + Integer.parseInt(bytesToHexString.substring(8, 10), 16)) + Integer.parseInt(bytesToHexString.substring(10, 12), 16)) + Integer.parseInt(bytesToHexString.substring(12, 14), 16)) + Integer.parseInt(bytesToHexString.substring(14, 16), 16));
        bb[13] = r5[0];
        bb[14] = r5[1];
        bb[15] = r5[2];
        bb[16] = r5[3];
        bb[17] = (byte) 67;
        bb[18] = SmileConstants.TOKEN_LITERAL_NULL;
        int count = 0;
        boolean writeStatus = false;
        while (!writeStatus) {
            writeStatus = LinkBleDevice.getInstance(context).setDataWriteRXCharacteristic("1aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0010-facebeadaaaa", bb);
            if (Constants.status_Connected) {
                if (count > 50000) {
                    break;
                }
                count++;
            } else {
                return -1;
            }
        }
        if (writeStatus) {
            return 1;
        }
        return -1;
    }

//    public static int sendReviseAutoTime(Context context) {
//        byte b = (byte) 1;
//        Log.i(TAG, "sendReviseAutoTime");
//        byte[] bb = new byte[]{(byte) 18, SmileConstants.TOKEN_KEY_LONG_STRING, (byte) 11, (byte) 23, (byte) 1, (byte) -1, (byte) ((((bb[2] + bb[3]) + bb[4]) + bb[5]) % MotionEventCompat.ACTION_MASK), (byte) 1, (byte) 0, (byte) 0, (byte) 67, SmileConstants.TOKEN_LITERAL_NULL};
//        Log.i("min :", new StringBuilder(String.valueOf(bb[5])).toString());
//        int count = 0;
//        boolean writeStatus = false;
//        while (!writeStatus) {
//            count++;
//            writeStatus = LinkBleDevice.getInstance(context).setDataWriteRXCharacteristic("1aabcdef-1111-2222-0000-facebeadaaaa", "facebead-ffff-eeee-0020-facebeadaaaa", bb);
//            if (Constants.status_Connected) {
//                if (count > 20000) {
//                    break;
//                }
//                count++;
//            } else {
//                return -1;
//            }
//        }
//        Log.i(TAG, "\u53d1\u9001sendReviseAutoTime\u6307\u4ee4\u5199\u5165\u7ed3\u679c\uff1awriteStatus = " + writeStatus);
//        if (!writeStatus) {
//            b = (byte) -1;
//        }
//        return b;
//    }

    private static String getNowTime() {
        new Time().setToNow();
        long time = (System.currentTimeMillis() + ((long) Integer.parseInt(TimeUtils.offSetTimeZone()))) / 1000;
        Log.v(TAG, "\u5f00\u59cb\u540c\u6b65\u65f6\u95f4 getNowTime = " + time);
        Log.i(TAG, "\u5f00\u59cb\u540c\u6b65\u65f6\u95f4 getNowTime = " + time);
        Log.d(TAG, "\u5f00\u59cb\u540c\u6b65\u65f6\u95f4 getNowTime = " + time);
        return Long.toHexString(time);
    }

    private static byte[] intToBytes(int value) {
        return new byte[]{(byte) ((value >> 24) & MotionEventCompat.ACTION_MASK), (byte) ((value >> 16) & MotionEventCompat.ACTION_MASK), (byte) ((value >> 8) & MotionEventCompat.ACTION_MASK), (byte) (value & MotionEventCompat.ACTION_MASK)};
    }

    private static int bytesToInt(byte[] src, int offset) {
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
