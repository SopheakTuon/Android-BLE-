/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.bluetoothlegatt;

import java.util.HashMap;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class SampleGattAttributes {
    private static HashMap<String, String> attributes = new HashMap();
    public static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
//    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";


    public static String SERVICE1 = "0aabcdef-1111-2222-0000-facebeadaaaa";
    public static String SERVICE2 = "1aabcdef-1111-2222-0000-facebeadaaaa";
    public static String SERVICE3 = "2aabcdef-1111-2222-0000-facebeadaaaa";
    public static String SERVICE4 = "eca95120-f940-11e4-9ed0-0002a5d5c51b";
    public static String SERVICE5 = "00001800-0000-1000-8000-00805f9b34fb";
    public static String SERVICE6 = "00001801-0000-1000-8000-00805f9b34fb";

    //Characteristics Service One
    public static String CHAR1 = "facebead-ffff-eeee-0001-facebeadaaaa";
    //Heart Rate, Blood Pressure
    public static String CHAR2 = "facebead-ffff-eeee-0002-facebeadaaaa";
    public static String CHAR3 = "facebead-ffff-eeee-0003-facebeadaaaa";
    //ECG
    public static String CHAR4 = "facebead-ffff-eeee-0004-facebeadaaaa";
    public static String CHAR5 = "facebead-ffff-eeee-0005-facebeadaaaa";

    //Characteristics Service Two
    public static String CHAR6 = "facebead-ffff-eeee-0010-facebeadaaaa";
    public static String CHAR7 = "facebead-ffff-eeee-0020-facebeadaaaa";

    //Characteristics Service Three
    public static String CHAR8 = "facebead-ffff-eeee-0100-facebeadaaaa";
    public static String CHAR9 = "facebead-ffff-eeee-0200-facebeadaaaa";

    //Characteristics Service Four
    public static String CHAR10 = "c1c8a4a0-f941-11e4-a534-0002a5d5c51b";

    //Characteristics Service Five
    public static String CHAR11 = "00002a00-0000-1000-8000-00805f9b34fb";
    public static String CHAR12 = "00002a01-0000-1000-8000-00805f9b34fb";

    //Characteristics Service Six
    public static String CHAR13 = "00002a05-0000-1000-8000-00805f9b34fb";

    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9";

    static {
        // Sample Services.
        attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Heart Rate Service");
        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");

        attributes.put(SERVICE1, "SERVICE 1");
        attributes.put(SERVICE2, "SERVICE 2");
        attributes.put(SERVICE3, "SERVICE 3");
        attributes.put(SERVICE4, "SERVICE 4");
        attributes.put(SERVICE5, "SERVICE 5");
        attributes.put(SERVICE6, "SERVICE 6");

        // Sample Characteristics.
        attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement");

        attributes.put(CHAR1, "Characteristics 1");
        attributes.put(CHAR2, "Characteristics 2");
        attributes.put(CHAR3, "Characteristics 3");
        attributes.put(CHAR4, "Characteristics 4");
        attributes.put(CHAR5, "Characteristics 5");

        attributes.put(CHAR6, "Characteristics 6");
        attributes.put(CHAR7, "Characteristics 7");

        attributes.put(CHAR8, "Characteristics 8");
        attributes.put(CHAR9, "Characteristics 9");

        attributes.put(CHAR10, "Characteristics 10");

        attributes.put(CHAR11, "Characteristics 11");
        attributes.put(CHAR12, "Characteristics 12");

        attributes.put(CHAR13, "Characteristics 13");
        attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
