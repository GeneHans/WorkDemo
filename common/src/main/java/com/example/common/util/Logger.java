package com.example.common.util;

import android.util.Log;

public class Logger {
    private static final String TAG = "Logger";

    public static void d(String message) {
        Log.d(TAG, message);
    }

    public static void d(String tag, String message) {
        Log.d(tag, message);
    }

    public static void e(String message) {
        Log.e(TAG, message);
    }

    public static void e(String tag, String message) {
        Log.e(tag, message);
    }

    public static void i(String message) {
        Log.i(TAG, message);
    }

    public static void i(String tag, String message) {
        Log.i(tag, message);
    }

    public static void v(String message) {
        Log.v(TAG, message);
    }

    public static void v(String tag, String message) {
        Log.v(tag, message);
    }
}
