package com.example.android.bluetoothlegatt.constant;

import com.example.android.bluetoothlegatt.models.MainDataList;

import java.util.ArrayList;
import java.util.List;

public class Constants {
    public static final String ACTION_DEVICE_FIRM_VERSION = "ACTION_DEVICE_FIRM_VERSION";
    public static final String ACTION_GATT_CONNING = "ACTION_GATT_CONNING";
    public static final String ACTION_GATT_DEVICE_BIND_REQUEST = "ACTION_GATT_DEVICE_BIND_REQUEST";
    public static final String ACTION_GATT_DEVICE_MATCH_ACK = "ACTION_GATT_DEVICE_MATCH_ACK";
    public static final String ACTION_GATT_DEVICE_UNBIND_ACK = "ACTION_GATT_DEVICE_UNBIND_ACK";
    public static final String ACTION_GATT_SEARCH = "ACTION_GATT_SEARCH";
    public static final String ACTION_GATT_SOS = "GATT_SOS";
    public static final String ACTION_GATT_SUCCESS_CONN = "ACTION_GATTS_CONN_SUCCES";
    public static final String ACTION_MAIN_DATA_ECG_ALL_DATA = "ACTION_MAIN_DATA_ECG_ALL_DATA";
    public static final String ACTION_MAIN_DATA_FIRM_FAULT = "FIRM_FAULT";
    public static final String ACTION_MAIN_DATA_FIRM_SUCCESS = "FIRM_SUCCESS";
    public static final String ACTION_SERVICE_GATT_CONNECTED = "ACTION_GATT_CONNECTED";
    public static final String ACTION_SERVICE_GATT_DISCONNECTED = "ACTION_GATT_DISCONNECTED";
    public static final String ACTION_SERVICE_GATT_DISCOVERED = "ACTION_GATT_SERVICES_DISCOVERED";
    public static final String ACTION_UP_FIRMWARE_COMPLETE = "ACTION_UP_FIRMWARE_COMPLETE";
    public static final String BLE_BOND_REQUEST = "BLE_BIND_REQUEST";
    public static final String BLE_CONNECTING = "BLE_CONNECTING";
    public static final String BLE_HEART_PACKAGE = "BLE_HEART_PACKAGE";
    public static final String BLE_MATCH = "BLE_MATCHACK";
    public static final String BLE_ON_RESTART = "BLE_ON_RESTART";
    public static final String BLE_OPEN = "BLE_OPEN";
    public static final String BLE_RESEARCH = "BLE_RESEARCH";
    public static final String BLE_SCAN_STOP = "BLE_SCAN_STOP";
    public static final String BLE_SEARCH_BACK = "BLE_SEARCH_BACK";
    public static final String BLE_SEARCH_PRE = "BLE_SEARCH_PRE";
    public static final String BLE_SERVICE = "BLE_SERVICE";
    public static final String BLE_SOS = "BLE_SOS";
    public static final String BLUETOOTH_ACTION = "CONNECTION_STATE_CHANGED";
    public static final String BLUETOOTH_STATE_CHANGED = "STATE_CHANGED";
    public static final String CHANGE_BATTERY_STATUS = "CHANGE_BATTERY_STATUS";
    public static final String CLOSE_FIRMWARE_UPING_DIALOG = "CLOSE_FIRMWARE_UPING_DIALOG";
    public static final String CLOSE_SEARCH_DIALOG = "CLOSE_SEARCH_DIALOG";
    public static final String CLOSE_UP_FIRMWARE_DIALOG = "CLOSE_UP_FIRMWARE_DIALOG";
    public static final int DELAY_MILLIS_UPDATE_DEVICE_TIME = 200;
    public static final int DELAY_TICKTOCK_FIRMWARE_UPDATE = 1000;
    public static final int DELAY_TIME_LINKING_BLE = 10000;
    public static int DELAY_TIME_MATCHING_BLE = 0;
    public static final String DEVICE_CONN = "DEVICE_CONN";
    public static final String DEVICE_DISCONNECT = "DEVICE_DISCONNECT";
    public static final String DEVICE_NAME = "Helo";
    public static final String DEVICE_SUB_NAME1 = "seedmorn";
    public static final String DEVICE_SUB_NAME2 = "HeloHL01";
    public static final String DEVICE_TARGET_MAC = "Target";
    public static final String FIRMWARE_UPDATE_FAILED = "FIRMWARE_UPDATE_FAILED";
    public static final String FIRMWARE_UPDATE_SUCESS = "FIRMWARE_UPDATE_SUCESS";
    public static final String FIRMWARE_UPING_DIALOG = "FIRMWARE_UPING_DIALOG";
    public static final String FIRMWARE_UPING_PROGRESSBAR = "FIRMWARE_UPING_PROGRESSBAR";
    public static ArrayList<String> GlobalData_datedata = null;
    public static List<MainDataList> GlobalData_list = null;
    public static boolean INIT_AT_BOOT = false;
    public static final int NOTIFICATION_FLAG_NEW_APP = 4;
    public static final int NOTIFICATION_FLAG_NEW_FW = 5;
    public static final long PERIOD_HEART_PACKAGE_SUCCESS = 30000;
    public static final long PERIOD_SCAN = 8000;
    public static int POWER_BATTERY = 0;
    public static final String SHARED_PREFRENCE_DECRY_TOKEN = "SHARED_PREFRENCE_DECRY_TOKEN";
    public static final String SHOW_BIND_DIALOG = "SHOW_BIND_DIALOG";
    public static final int STATE_CONNECTED = 2;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_DISCONNED = 0;
    public static final int STATE_SCANNING = 3;
    public static final boolean SWITCHBIND = false;
    public static int VERSION_APP;
    public static int VERSION_FIRM;
    public static int childPosition;
    public static int groupPosition;
    public static int heartFail;
    public static boolean isBLeEnabled;
    public static boolean isCouldUpFirmware;
    public static boolean isEnabled;
    public static boolean isFirmWorkVersionInfo;
    public static boolean isFirst;
    public static boolean isInitBle;
    public static boolean isLoad;
    public static boolean isMain;
    public static boolean isMainFragment;
    public static boolean isMatchInfo;
    public static boolean isMenu;
    public static boolean isNewAppOnServer_OnPause;
    public static boolean isNewFirmwareOnServer_OnPause;
    public static boolean isOnBondRequest_OnPause;
    public static boolean isOnFirmwareUpdating;
    public static boolean isOnPause;
    public static boolean isReconnect;
    public static boolean isRequest;
    public static boolean isSos;
    public static boolean isSysblestatus;
    public static final int[] low_batery;
    public static String message_NewAppOnServer;
    public static String message_NewFirmwareOnServer;
    public static int notification_count_lowbetery;
    public static int notification_count_measures;
    public static int notification_count_newapp;
    public static int notification_count_newfw;
    public static int notification_count_sleep;
    public static int notification_count_sos;
    public static int notification_count_steps;
    public static boolean status_ConnInit;
    public static boolean status_Connected;
    public static boolean status_Connecting;
    public static boolean status_Scanning;
    public static boolean status_matching_or_binding;

    public static final int[] version_ForceUpdate;

    static {
        notification_count_sleep = STATE_DISCONNED;
        notification_count_steps = STATE_DISCONNED;
        notification_count_sos = STATE_DISCONNED;
        notification_count_lowbetery = STATE_DISCONNED;
        notification_count_newapp = STATE_DISCONNED;
        notification_count_newfw = STATE_DISCONNED;
        notification_count_measures = STATE_DISCONNED;
        INIT_AT_BOOT = SWITCHBIND;
        version_ForceUpdate = new int[]{STATE_CONNECTING, STATE_CONNECTED, STATE_SCANNING, NOTIFICATION_FLAG_NEW_APP, NOTIFICATION_FLAG_NEW_FW};
        low_batery = new int[]{30, 15, 10, NOTIFICATION_FLAG_NEW_FW};
        DELAY_TIME_MATCHING_BLE = DELAY_TICKTOCK_FIRMWARE_UPDATE;
        POWER_BATTERY = STATE_DISCONNED;
        isOnFirmwareUpdating = SWITCHBIND;
        isCouldUpFirmware = SWITCHBIND;
        VERSION_FIRM = STATE_DISCONNED;
        VERSION_APP = STATE_DISCONNED;
        groupPosition = STATE_DISCONNED;
        childPosition = STATE_DISCONNED;
        isMain = true;
        isMenu = true;
        isFirst = true;
        status_Scanning = SWITCHBIND;
        status_ConnInit = SWITCHBIND;
        status_Connecting = SWITCHBIND;
        status_matching_or_binding = SWITCHBIND;
        status_Connected = SWITCHBIND;
        isReconnect = SWITCHBIND;
        isInitBle = SWITCHBIND;
        isOnPause = SWITCHBIND;
        isOnBondRequest_OnPause = SWITCHBIND;
        isNewAppOnServer_OnPause = SWITCHBIND;
        isNewFirmwareOnServer_OnPause = SWITCHBIND;
        message_NewFirmwareOnServer = "";
        message_NewAppOnServer = "";
        isSysblestatus = true;
        isRequest = SWITCHBIND;
        isMainFragment = SWITCHBIND;
        isEnabled = SWITCHBIND;
        isBLeEnabled = SWITCHBIND;
        isSos = SWITCHBIND;
        isLoad = SWITCHBIND;
        TOP_MAC = "";
        heartFail = STATE_DISCONNED;
        GlobalData_list = null;
        GlobalData_datedata = null;
        isMatchInfo = SWITCHBIND;
        isFirmWorkVersionInfo = SWITCHBIND;
    }

    public static final String ACTION_GATT_BLOOD_PRESSURE_NOISE = "ACTION_GATT_BLOOD_PRESSURE_NOISE";

    public static final String ACTION_GATT_LOAD_DATA = "ACTION_GATT_LOAD_DATA";
    public static final String ACTION_GATT_LOAD_DATA_SLEEP = "ACTION_GATT_LOAD_DATA_SLEEP";

    public static final String ACTION_GATT_SLEEP_NEW = "ACTION_GATT_SLEEP_NEW";

    public static final String ACTION_MAIN_DATA_BATTERY_POWER = "BUTTERY_POWER";
    public static final String ACTION_MAIN_DATA_BP = "BluetoothLeService.DATA_BLOOD_PRESSURE";
    public static final String ACTION_MAIN_DATA_BREATH = "DATA_BREATH_RATE";
    public static final String ACTION_MAIN_DATA_ECG = "DATA_ECG";

    public static final String ACTION_MAIN_DATA_FATIGUE = "DATA_FATIGUE";

    public static final String ACTION_MAIN_DATA_HR = "DATA_HEART_RATE";
    public static final String ACTION_MAIN_DATA_KLL = "DATA_KLL";
    public static final String ACTION_MAIN_DATA_MOOD = "DATA_MOOD";
    public static final String ACTION_MAIN_DATA_PW = "ACTION_MAIN_DATA_PW";
    public static final String ACTION_MAIN_DATA_SLEEP = "DATA_SLEEP";
    public static final String ACTION_MAIN_DATA_STEPS = "ACTION_MAIN_DATA_STEPS";
    public static final String ACTION_MATCH_INFO_TO_DEVICE = "ACTION_MATCH_INFO_TO_DEVICE";


    public static final String LEDCONTORLLSUCCESS = "LED_CONTORLL_SUCCESS";


    public static String TOP_MAC = null;


    /**
     *Data direction: BLE device -> App
     */
    public final class CMD {
        //BIND COMMAND
        public static final String START_SYCHRONIZATION = "21";
        public static final String STOP_SYCHRONIZATIO = "22";
        public static final String DEVICE_BIND_REQUEST = "23";

        //DATA COMMAND
        public static final String GATT_SOS = "24";
        public static final String DATA_STEP_COUNT = "31";
        public static final String DATA_HEART_RATE = "32";
        public static final String DATA_PPG = "33";
        public static final String DATA_KLL = "34";
        public static final String DATA_SLEEP = "35";
        public static final String DEVICE_MATCH_ACK = "37";
        public static final String DEVICE_UNBIND_ACK = "38";
        public static final String DATA_MOOD = "3B";
        public static final String DATA_FATIGUE = "3C";
        public static final String DATA_BREATH_RATE = "3D";
        public static final String HEALTH_PLAN = "3E";
        public static final String CRC_DATA_SYNCHRONIZATION = "3F";

        public static final String DATA_BLOOD_PRESSURE = "41";
        public static final String DATA_ECG = "42";
        public static final String DATA_BATTERY_POWER = "43";
        public static final String SLEEP_DATA = "44";


    }

    /**
     * Data direction: APP -> BLE Device
     */
    public static final class WriteCommandCode{
        public static final byte STEP_COUNT = 0x01;
        public static final byte HEART_RATE = 0x02;
        public static final byte PPG = 0x03;
        public static final byte CALORY_DATA = 0x04;
        public static final byte SLEEP_DATA = 0x05;
        public static final byte MOOD_FATIGUE = 0x06;
        public static final byte FATIGUE = 0x07;
        public static final byte CLEAR_DISTANCE = 0x08;
        public static final byte REQUEST_UNBIND = 0x09;
        public static final byte ECG = 0x0A;
        public static final byte BREATH_RATE = 0x0B;
        public static final byte BLOOD_PRESSURE = 0x0C;
        public static final byte TURN_OFF_DEVICE = 0x0E;
        public static final byte STOP_CURRENT_MEASUREMENT = 0x0F;
        public static final byte PAIR_INFORMATION = 0x11;
        public static final byte DATE_TIME_SYNCHRONIZATION = 0x12;
        public static final byte REQUEST_TO_BINDING = 0x13;
        public static final byte TIMER = 0x14;
        public static final byte HEALTH_PLAN = 0x17;
        public static final byte CALIBRATION_BLOOD_PRESSURE = 0x19;
        public static final byte SECOND_MATCH = 0x1B;
        public static final byte START_DATA_SYNCHRONIZATION = 0x21;
        public static final byte END_DATA_SYNCHRONIZATION = 0x22;
        public static final byte REQUEST_TO_BIND = 0x23;
        public static final byte SOS = 0x24;
    }


    public final class Smile {
//        public static final byte BYTE_MARKER_END_OF_CONTENT = (byte) -1;
//        public static final byte BYTE_MARKER_END_OF_STRING = (byte) -4;
//        public static final int HEADER_BIT_HAS_RAW_BINARY = 4;
//        public static final int HEADER_BIT_HAS_SHARED_NAMES = 1;
//        public static final int HEADER_BIT_HAS_SHARED_STRING_VALUES = 2;
//        public static final byte HEADER_BYTE_1 = (byte) 58;
//        public static final byte HEADER_BYTE_2 = (byte) 41;
//        public static final byte HEADER_BYTE_3 = (byte) 10;
//        public static final byte HEADER_BYTE_4 = (byte) 0;
//        public static final int HEADER_VERSION_0 = 0;
//        public static final int INT_MARKER_END_OF_STRING = 252;
//        public static final int MAX_SHARED_NAMES = 1024;
//        public static final int MAX_SHARED_STRING_LENGTH_BYTES = 65;
//        public static final int MAX_SHARED_STRING_VALUES = 1024;
//        public static final int MAX_SHORT_NAME_ASCII_BYTES = 64;
//        public static final int MAX_SHORT_NAME_UNICODE_BYTES = 56;
//        public static final int MAX_SHORT_VALUE_STRING_BYTES = 64;
//        public static final int MIN_BUFFER_FOR_POSSIBLE_SHORT_STRING = 196;
//        public static final byte TOKEN_KEY_EMPTY_STRING = (byte) 32;
        public static final byte TOKEN_KEY_LONG_STRING = (byte) 52;
        public static final byte TOKEN_LITERAL_EMPTY_STRING = (byte) 32;
        public static final byte TOKEN_LITERAL_END_ARRAY = (byte) -7;
        public static final byte TOKEN_LITERAL_END_OBJECT = (byte) -5;
        public static final byte TOKEN_LITERAL_FALSE = (byte) 34;
        public static final byte TOKEN_LITERAL_NULL = (byte) 33;
//        public static final byte TOKEN_LITERAL_START_ARRAY = (byte) -8;
//        public static final byte TOKEN_LITERAL_START_OBJECT = (byte) -6;
//        public static final byte TOKEN_LITERAL_TRUE = (byte) 35;
//        public static final int TOKEN_MISC_BINARY_7BIT = 232;
//        public static final int TOKEN_MISC_BINARY_RAW = 253;
//        public static final int TOKEN_MISC_FLOAT_32 = 0;
//        public static final int TOKEN_MISC_FLOAT_64 = 1;
//        public static final int TOKEN_MISC_FLOAT_BIG = 2;
//        public static final int TOKEN_MISC_FP = 40;
//        public static final int TOKEN_MISC_INTEGER = 36;
//        public static final int TOKEN_MISC_INTEGER_32 = 0;
//        public static final int TOKEN_MISC_INTEGER_64 = 1;
//        public static final int TOKEN_MISC_INTEGER_BIG = 2;
//        public static final int TOKEN_MISC_LONG_TEXT_ASCII = 224;
//        public static final int TOKEN_MISC_LONG_TEXT_UNICODE = 228;
//        public static final int TOKEN_MISC_SHARED_STRING_LONG = 236;
//        public static final int TOKEN_PREFIX_KEY_ASCII = 128;
//        public static final int TOKEN_PREFIX_KEY_SHARED_LONG = 48;
//        public static final int TOKEN_PREFIX_KEY_SHARED_SHORT = 64;
//        public static final int TOKEN_PREFIX_KEY_UNICODE = 192;
//        public static final int TOKEN_PREFIX_MISC_OTHER = 224;
//        public static final int TOKEN_PREFIX_SHARED_STRING_SHORT = 0;
//        public static final int TOKEN_PREFIX_SHORT_UNICODE = 160;
//        public static final int TOKEN_PREFIX_SMALL_ASCII = 96;
//        public static final int TOKEN_PREFIX_SMALL_INT = 192;
//        public static final int TOKEN_PREFIX_TINY_ASCII = 64;
//        public static final int TOKEN_PREFIX_TINY_UNICODE = 128;
    }



}
