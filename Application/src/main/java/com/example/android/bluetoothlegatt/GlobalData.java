package com.example.android.bluetoothlegatt;

import java.util.ArrayList;
import java.util.List;

public class GlobalData {
    public static final String ACTION_DEVICE_FIRMVERSION = "ACTION_DEVICE_FIRMVERSION";
    public static final String ACTION_GATT_CONNING = "ACTION_GATT_CONNING";
    public static final String ACTION_GATT_DEVICE_BIND_REQUEST = "ACTION_GATT_DEVICE_BIND_REQUEST";
    public static final String ACTION_GATT_DEVICE_MATCH_ACK = "ACTION_GATT_DEVICE_MATCH_ACK";
    public static final String ACTION_GATT_DEVICE_UNBIND_ACK = "ACTION_GATT_DEVICE_UNBIND_ACK";
    public static final String ACTION_GATT_SEARCH = "com.worldgn.helo.ACTION_GATT_SEARCH";
    public static final String ACTION_GATT_SOS = "com.worldgn.helo.ble.BluetoothLeService.ACTION_GATT_SOS";
    public static final String ACTION_GATT_SUCCESS_CONN = "com.worldgn.helo.ACTION_GATTS_CONN_SUCCES";
    public static final String ACTION_MAIN_DATA_ECGALLDATA = "com.worldgn.helo.ble.BluetoothLeService.ACTION_MAIN_DATA_ECGALLDATA";
    public static final String ACTION_MAIN_DATA_FIRM_FAULT = "com.worldgn.helo.ble.BluetoothLeService.FIRM_FAULT";
    public static final String ACTION_MAIN_DATA_FIRM_SUCCESS = "com.worldgn.helo.ble.BluetoothLeService.FIRM_SUCCESS";
    public static final String ACTION_SERVICE_GATT_CONNECTED = "com.worldgn.helo.ble.BluetoothLeService.ACTION_GATT_CONNECTED";
    public static final String ACTION_SERVICE_GATT_DISCONNECTED = "com.worldgn.helo.ble.BluetoothLeService.ACTION_GATT_DISCONNECTED";
    public static final String ACTION_SERVICE_GATT_DISCOVERED = "com.worldgn.helo.ble.BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED";
    public static final String ACTION_UP_FIRMWARE_COMPLETE = "com.worldgn.helo.utils.UpdataFirmwareUpdata.ACTION_UP_FIRMWARE_COMPLETE";
    public static final String BLE_BOND_REQUEST = "com.worldgn.helo.BLE_BIND_REQUEST";
    public static final String BLE_CONNECTING = "com.worldgn.helo.BLE_CONNECTING";
    public static final String BLE_HEART_PACKAGE = "com.worldgn.helo.BLE_HEART_PACKAGE";
    public static final String BLE_MATCH = "com.worldgn.helo.BLE_MATCHACK";
    public static final String BLE_ON_RESTART = "com.worldgn.helo.BLE_ON_RESTART";
    public static final String BLE_OPEN = "com.worldgn.helo.BLE_OPEN";
    public static final String BLE_RESEARCH = "com.worldgn.helo.BLE_RESEARCH";
    public static final String BLE_SCAN_STOP = "com.worldgn.helo.BLE_SCAN_STOP";
    public static final String BLE_SEARCH_BACK = "com.worldgn.helo.BLE_SEARCH_BACK";
    public static final String BLE_SEARCH_PRE = "com.worldgn.helo.BLE_SEARCH_PRE";
    public static final String BLE_SERVICE = "com.worldgn.helo.BLE_SERVICE";
    public static final String BLE_SOS = "com.worldgn.helo.BLE_SOS";
    public static final String BLUETOOTH_ACTION = "android.bluetooth.a2dp.profile.action.CONNECTION_STATE_CHANGED";
    public static final String BLUETOOTH_STATE_CHANGED = "android.bluetooth.adapter.action.STATE_CHANGED";
    public static final String CHANGE_BATTERY_STATUS = "com.worldgn.helo.CHANGE_BATTERY_STATUS";
    public static final String CLOSE_FIRMWARE_UPING_DIALOG = "com.worldgn.helo.CLOSE_FIRMWARE_UPING_DIALOG";
    public static final String CLOSE_SEARCH_DIALOG = "com.worldgn.helo.CLOSE_SEARCH_DIALOG";
    public static final String CLOSE_UPFIRMWARE_DIALOG = "com.worldgn.helo.CLOSE_UPFIRMWARE_DIALOG";
    public static final int DELAYMILLIS_UPDATE_DEVICE_TIME = 200;
    public static final int DELAY_TICKTOCK_FIRMWAREUPDATE = 1000;
    public static final int DELAY_TIME_LINKING_BLE = 10000;
    public static int DELAY_TIME_MATCHING_BLE = 0;
    public static final String DEVICE_CONN = "com.worldgn.helo.DEVICE_CONN";
    public static final String DEVICE_DISCONN = "com.worldgn.helo.DEVICE_DISCONN";
    public static final String DEVICE_NAME = "Helo";
    public static final String DEVICE_SUBNAME1 = "seedmorn";
    public static final String DEVICE_SUBNAME2 = "HeloHL01";
    public static final String DEVICE_TARGET_MAC = "Target";
    public static final String FIRMWARE_UPDATE_FAILED = "com.worldgn.helo.FIRMWARE_UPDATE_FAILED";
    public static final String FIRMWARE_UPDATE_SUCESS = "com.worldgn.helo.FIRMWARE_UPDATE_SUCESS";
    public static final String FIRMWARE_UPING_DIALOG = "com.worldgn.helo.FIRMWARE_UPING_DIALOG";
    public static final String FIRMWARE_UPING_PROGRESSBAR = "com.worldgn.helo.FIRMWARE_UPING_PROGRESSBAR";
    public static ArrayList<String> GlobalData_datedata = null;
    public static List<MainDataList> GlobalData_list = null;
    public static boolean INIT_AT_BOOT = false;
    public static final int NOTIFICATION_FLAG_NEW_APP = 4;
    public static final int NOTIFICATION_FLAG_NEW_FW = 5;
    public static final long PERIOD_HEART_PACKAGE_SUCCESS = 30000;
    public static final long PERIOD_SCAN = 8000;
    public static int POWER_BATTERY = 0;
    public static final String SHARED_PREFRENCE_DECRY_TOKEN = "SHARED_PREFRENCE_DECRY_TOKEN";
    public static final String SHOW_BIND_DIALOG = "com.worldgn.helo.SHOW_BIND_DIALOG";
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
        DELAY_TIME_MATCHING_BLE = DELAY_TICKTOCK_FIRMWAREUPDATE;
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

    public static final String ACTION_GATT_BLOOD_PRESSURE_NOISE = "com.worldgn.helo.ble.BluetoothLeService.ACTION_GATT_BLOOD_PRESSURE_NOISE";
    public static final String ACTION_GATT_CLOSED = "com.worldgn.helo.ACTION_GATT_CLOSED";

    public static final String ACTION_GATT_LOAD_DATA = "com.worldgn.helo.ble.BluetoothLeService.ACTION_GATT_LOAD_DATA";
    public static final String ACTION_GATT_LOAD_DATA_SLEEP = "com.worldgn.helo.ble.BluetoothLeService.ACTION_GATT_LOAD_DATA_SLEEP";

    public static final String ACTION_GATT_SLEEP_NEW = "com.worldgn.helo.ble.BluetoothLeService.ACTION_GATT_SLEEP_NEW";

    public static final String ACTION_MAIN_DATA_BATTERY_POWER = "com.worldgn.helo.ble.BluetoothLeService.BUTTERY_POWER";
    public static final String ACTION_MAIN_DATA_BP = "com.worldgn.helo.ble.BluetoothLeService.ACTION_MAIN_DATA_BP";
    public static final String ACTION_MAIN_DATA_BREATH = "com.worldgn.helo.ble.BluetoothLeService.ACTION_MAIN_DATA_BREATH";
    public static final String ACTION_MAIN_DATA_ECG = "com.worldgn.helo.ble.BluetoothLeService.ACTION_MAIN_DATA_ECG";

    public static final String ACTION_MAIN_DATA_FATIGUE = "com.worldgn.helo.ble.BluetoothLeService.ACTION_MAIN_DATA_FATIGUE";

    public static final String ACTION_MAIN_DATA_HR = "com.worldgn.helo.ble.BluetoothLeService.ACTION_MAIN_DATA_HR";
    public static final String ACTION_MAIN_DATA_KLL = "com.worldgn.helo.ble.BluetoothLeService.ACTION_MAIN_DATA_KLL";
    public static final String ACTION_MAIN_DATA_MOOD = "com.worldgn.helo.ble.BluetoothLeService.ACTION_MAIN_DATA_MOOD";
    public static final String ACTION_MAIN_DATA_PW = "com.worldgn.helo.ble.BluetoothLeService.ACTION_MAIN_DATA_PW";
    public static final String ACTION_MAIN_DATA_SLEEP = "com.worldgn.helo.ble.BluetoothLeService.ACTION_MAIN_DATA_SLEEP";
    public static final String ACTION_MAIN_DATA_STEPS = "com.worldgn.helo.ble.BluetoothLeService.ACTION_MAIN_DATA_STEPS";
    public static final String ACTION_MATCH_INFO_TO_DEVICE = "com.worldgn.helo.ble.BluetoothLeService.ACTION_MATCH_INFO_TO_DEVICE";
    public static final String ACTION_NEWVERSION_APP = "com.worldgn.helo.ACTION_NEWVERSION_APP";
    public static final String ACTION_NEWVERSION_FIRMVERSION = "com.worldgn.helo.ACTION_NEWVERSION_FIRMVERSION";

    public static final String ACTION_UP_APP_VERSION = "com.worldgn.helo.upapp";
    public static final String ACTION_UP_FIRMWARE_CANCEL = "com.worldgn.helo.utils.UpdataFirmwareUpdata.ACTION_UP_FIRMWARE_CANCEL";

    public static final String ACTION_UP_FIRMWARE_VERSION = "com.worldgn.helo.upfirm";
    public static final String ACTION_WECARE_ADD_FRENDS = "com.worldgn.helo.MY_BROADCASTRECEIVER";
    public static final String ACTION_WECARE_FRESHFRENDS = "com.worldgn.helo.MY_BROADCASTRECEIVER_REFRESH";
    public static final String ACTION_WECARE_FRESHFRENDS_FOLLOWER = "com.worldgn.helo.MY_BROADCASTRECEIVER_REFRESH_FOLLOWER";
    public static final String ACTION_WECARE_FRESHFRENDS_FOLOWING = "com.worldgn.helo.MY_BROADCASTRECEIVER_REFRESH_FOLOWING";

        public static final String BLE_SOS_REQUEST_FAIL = "com.worldgn.helo.BLE_SOS_REQUEST_FAIL";
    public static final String BLE_SOS_REQUEST_OK = "com.worldgn.helo.BLE_SOS_REQUEST_OK";

        public static final int DELAYMILLIS_REFRESH_BATTERYLEVEL = 500;

        public static final String LEDCONTORLLSUCCESS = "LED_CONTORLL_SUCCESS";
    public static final int NOTIFICATION_FLAG_LOWBETERY = 6;
    public static final int NOTIFICATION_FLAG_MEASURES = 3;

        public static final int NOTIFICATION_FLAG_SLEEP = 0;
    public static final int NOTIFICATION_FLAG_SOS = 2;
    public static final int NOTIFICATION_FLAG_STEPS = 1;
    public static final long PERIOD_HEART_PACKAGE_FAILED = 100;

        public static final int REQUEST_CODE_GET_PERSONAL_INFO = 291;
    public static final int REQUEST_ENABLE_BT = 1;
    public static final String SHARED_PREFRENCE_DECRY_TIME = "SHARED_PREFRENCE_DECRY_TIME";

        public static final float SOS_GPS_DISTANCE = 0.0f;
    public static final long SOS_GPS_TIME = 300000;

    public static final String StepCount = "StepCount";
    public static String TOP_MAC = null;
    public static final String Today = "Today";
    public static final long UPDATETIMEOUT = 1000;

    public static long userid;

}
