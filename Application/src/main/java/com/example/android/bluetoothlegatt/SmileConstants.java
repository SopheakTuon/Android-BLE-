package com.example.android.bluetoothlegatt;

public final class SmileConstants {
    public static final byte BYTE_MARKER_END_OF_CONTENT = (byte) -1;
    public static final byte BYTE_MARKER_END_OF_STRING = (byte) -4;
    public static final int HEADER_BIT_HAS_RAW_BINARY = 4;
    public static final int HEADER_BIT_HAS_SHARED_NAMES = 1;
    public static final int HEADER_BIT_HAS_SHARED_STRING_VALUES = 2;
    public static final byte HEADER_BYTE_1 = (byte) 58;
    public static final byte HEADER_BYTE_2 = (byte) 41;
    public static final byte HEADER_BYTE_3 = (byte) 10;
    public static final byte HEADER_BYTE_4 = (byte) 0;
    public static final int HEADER_VERSION_0 = 0;
    public static final int INT_MARKER_END_OF_STRING = 252;
    public static final int MAX_SHARED_NAMES = 1024;
    public static final int MAX_SHARED_STRING_LENGTH_BYTES = 65;
    public static final int MAX_SHARED_STRING_VALUES = 1024;
    public static final int MAX_SHORT_NAME_ASCII_BYTES = 64;
    public static final int MAX_SHORT_NAME_UNICODE_BYTES = 56;
    public static final int MAX_SHORT_VALUE_STRING_BYTES = 64;
    public static final int MIN_BUFFER_FOR_POSSIBLE_SHORT_STRING = 196;
    public static final byte TOKEN_KEY_EMPTY_STRING = (byte) 32;
    public static final byte TOKEN_KEY_LONG_STRING = (byte) 52;
    public static final byte TOKEN_LITERAL_EMPTY_STRING = (byte) 32;
    public static final byte TOKEN_LITERAL_END_ARRAY = (byte) -7;
    public static final byte TOKEN_LITERAL_END_OBJECT = (byte) -5;
    public static final byte TOKEN_LITERAL_FALSE = (byte) 34;
    public static final byte TOKEN_LITERAL_NULL = (byte) 33;
    public static final byte TOKEN_LITERAL_START_ARRAY = (byte) -8;
    public static final byte TOKEN_LITERAL_START_OBJECT = (byte) -6;
    public static final byte TOKEN_LITERAL_TRUE = (byte) 35;
    public static final int TOKEN_MISC_BINARY_7BIT = 232;
    public static final int TOKEN_MISC_BINARY_RAW = 253;
    public static final int TOKEN_MISC_FLOAT_32 = 0;
    public static final int TOKEN_MISC_FLOAT_64 = 1;
    public static final int TOKEN_MISC_FLOAT_BIG = 2;
    public static final int TOKEN_MISC_FP = 40;
    public static final int TOKEN_MISC_INTEGER = 36;
    public static final int TOKEN_MISC_INTEGER_32 = 0;
    public static final int TOKEN_MISC_INTEGER_64 = 1;
    public static final int TOKEN_MISC_INTEGER_BIG = 2;
    public static final int TOKEN_MISC_LONG_TEXT_ASCII = 224;
    public static final int TOKEN_MISC_LONG_TEXT_UNICODE = 228;
    public static final int TOKEN_MISC_SHARED_STRING_LONG = 236;
    public static final int TOKEN_PREFIX_KEY_ASCII = 128;
    public static final int TOKEN_PREFIX_KEY_SHARED_LONG = 48;
    public static final int TOKEN_PREFIX_KEY_SHARED_SHORT = 64;
    public static final int TOKEN_PREFIX_KEY_UNICODE = 192;
    public static final int TOKEN_PREFIX_MISC_OTHER = 224;
    public static final int TOKEN_PREFIX_SHARED_STRING_SHORT = 0;
    public static final int TOKEN_PREFIX_SHORT_UNICODE = 160;
    public static final int TOKEN_PREFIX_SMALL_ASCII = 96;
    public static final int TOKEN_PREFIX_SMALL_INT = 192;
    public static final int TOKEN_PREFIX_TINY_ASCII = 64;
    public static final int TOKEN_PREFIX_TINY_UNICODE = 128;
    public static final int[] sUtf8UnitLengths;

    static {
//        int[] table = new int[Opcodes.ACC_NATIVE];
//        for (int c = TOKEN_PREFIX_TINY_UNICODE; c < Opcodes.ACC_NATIVE; c += TOKEN_MISC_INTEGER_64) {
//            int code;
//            if ((c & TOKEN_PREFIX_MISC_OTHER) == TOKEN_PREFIX_SMALL_INT) {
//                code = TOKEN_MISC_INTEGER_64;
//            } else if ((c & 240) == TOKEN_PREFIX_MISC_OTHER) {
//                code = TOKEN_MISC_INTEGER_BIG;
//            } else if ((c & 248) == 240) {
//                code = 3;
//            } else {
//                code = -1;
//            }
//            table[c] = code;
//        }
//        sUtf8UnitLengths = table;
        sUtf8UnitLengths = new int[10];
    }
}
