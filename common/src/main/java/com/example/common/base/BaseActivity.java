package com.example.common.base;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewbinding.ViewBinding;

import com.example.common.util.PermissionManager;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseActivity<VB extends ViewBinding> extends AppCompatActivity {

    protected Activity activity;
    protected VB binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        binding = setBinding();
        setContentView(binding.getRoot());
    }

    abstract protected VB setBinding();

    private PermissionManager.PermissionCallback permissionCallback;

    /**
     * 权限请求
     * @param permissions：权限列表
     * @param callback：权限授予回调
     */
    protected void requestPermission(String[] permissions, PermissionManager.PermissionCallback callback) {
        permissionCallback = callback;
        ActivityCompat.requestPermissions(this, permissions, PermissionManager.REQ_CODE_PERMISSION);
    }

    /**
     * 权限回调处理
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionManager.REQ_CODE_PERMISSION) {
            List<String> permissionGranted = new ArrayList<>();
            List<String> permissionDenied = new ArrayList<>();
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    permissionGranted.add(permissions[i]);
                } else {
                    permissionDenied.add(permissions[i]);
                }
            }
            if (permissionGranted.size() != 0 && permissionDenied.size() == 0) {
                if (permissionCallback != null) {
                    permissionCallback.onPermissionGranted(permissionGranted.toArray(new String[permissionGranted.size()]));
                }
            } else {
                if (permissionCallback != null) {
                    permissionCallback.onPermissionDenied(permissionDenied.toArray(new String[permissionDenied.size()]));
                }
            }
        }
    }
}
