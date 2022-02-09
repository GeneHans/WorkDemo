package com.example.common.util;

import android.Manifest;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class PermissionManager {
    public static final int REQ_CODE_PERMISSION = 101;
    //工程内的文件读写权限
    public static String[] ReadAndWritePermissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};
    //工程内的位置权限
    public static String[] LocationPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS
    };

    /**
     * 检查是否存在某项权限
     * @param context
     * @param permission
     * @return true：存在  false：不存在
     */
    public static boolean checkPermission(Context context, String permission) {
        int result = ActivityCompat.checkSelfPermission(context, permission);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 检查是否多个权限都同时存在
     * @param context
     * @param permissions
     * @return true：都存在  false：有不存在的权限
     */
    public static boolean checkPermissions(Context context, String[] permissions) {
        boolean hasPermissions = true;
        if (permissions == null || permissions.length == 0)
            return hasPermissions;
        for (String permission : permissions) {
            int result = ActivityCompat.checkSelfPermission(context, permission);
            hasPermissions = hasPermissions && (result == PackageManager.PERMISSION_GRANTED);
        }
        return hasPermissions;
    }

    /**
     * 检查应用内是否存在应用内安装应用权限，如果没有则直接执行权限申请
     *
     * @param activity
     * @param callback
     */
    public static void checkInstallPermission(Activity activity, PermissionCallback callback) {
        boolean haveInstallPermission = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            haveInstallPermission = activity.getPackageManager().canRequestPackageInstalls();
            if (!haveInstallPermission) {
                //没有权限则需要跳转到设置页面进行权限开启
                Uri packURI = Uri.parse("package:" + activity.getPackageName());
                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packURI);
                activity.startActivityForResult(intent, REQ_CODE_PERMISSION);
            } else {
                //有权限直接执行有权限的回调
                callback.onPermissionGranted(new String[]{Manifest.permission.INSTALL_PACKAGES});
            }
        } else {
            //Android 8.0以下可以直接安装
        }
    }


    /**
     * 申请悬浮窗权限
     *
     * @param context
     */
    public static void applyFloatingPermission(Context context) {
        try {
            int sdkInt = Build.VERSION.SDK_INT;
            if (sdkInt >= Build.VERSION_CODES.O) {//8.0以上
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                context.startActivity(intent);
            } else if (sdkInt >= Build.VERSION_CODES.M) {//6.0-8.0
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                context.startActivity(intent);
            } else {//4.4-6.0以下
                //无需处理
            }
        } catch (Exception e) {
            gotoSettings(context);
        }
    }


    /**
     * 判断是否存在悬浮窗权限
     *
     * @param context
     * @return
     */
    public static boolean checkFloatingPermission(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
            return false;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            try {
                Class cls = Class.forName("android.content.Context");
                Field declaredField = cls.getDeclaredField("APP_OPS_SERVICE");
                declaredField.setAccessible(true);
                Object obj = declaredField.get(cls);
                if (!(obj instanceof String)) {
                    return false;
                }
                String str2 = (String) obj;
                obj = cls.getMethod("getSystemService", String.class).invoke(context, str2);
                cls = Class.forName("android.app.AppOpsManager");
                Field declaredField2 = cls.getDeclaredField("MODE_ALLOWED");
                declaredField2.setAccessible(true);
                Method checkOp = cls.getMethod("checkOp", Integer.TYPE, Integer.TYPE, String.class);
                int result = (Integer) checkOp.invoke(obj, 24, Binder.getCallingUid(), context.getPackageName());
                return result == declaredField2.getInt(cls);
            } catch (Exception e) {
                return false;
            }
        } else {
            AppOpsManager appOpsMgr = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            if (appOpsMgr == null)
                return false;
            int mode = appOpsMgr.checkOpNoThrow("android:system_alert_window", android.os.Process.myUid(), context
                    .getPackageName());
            return Settings.canDrawOverlays(context) || mode == AppOpsManager.MODE_ALLOWED;
        }
    }

    /**
     * 进入到系统设置页面
     */
    public static void gotoSettings(Context context) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        context.startActivity(intent);
    }

    public static abstract class PermissionCallback {
        /**
         * 权限被授予回调
         *
         * @param permissions
         */
        abstract public void onPermissionGranted(String[] permissions);

        /**
         * 权限被拒绝回调
         *
         * @param permissions
         */
        abstract public void onPermissionDenied(String[] permissions);
    }
}
