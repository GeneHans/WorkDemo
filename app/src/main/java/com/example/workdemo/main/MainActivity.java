package com.example.workdemo.main;

import android.app.DownloadManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.example.common.base.BaseActivity;
import com.example.common.manager.DownloadFileManager;
import com.example.common.manager.DownloadUtils;
import com.example.common.util.Logger;
import com.example.common.util.PermissionManager;
import com.example.common.ConstData;
import com.example.workdemo.activity.AnnotationTestActivity;
import com.example.workdemo.activity.TestQRCodeActivity;
import com.example.workdemo.databinding.ActivityMainBinding;
import com.example.workdemo2.IMyAidlInterface;

import java.util.List;

public class MainActivity extends BaseActivity<ActivityMainBinding> {

    private IMyAidlInterface iMyAidlInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initMainPageAdapter();
        bindAIDL();
    }

    private String data = "no data";

    private void initMainPageAdapter() {
        MainPageAdapter adapter = new MainPageAdapter();
        adapter.addData(new MainListEntity("二维码扫描", "二维码扫描页简易demo，利用zxing-lite实现"));
        adapter.addData(new MainListEntity("DownloadManager", "Android自带的下载工具DownLoadManager测试"));
        adapter.addData(new MainListEntity("APT测试", "利用APT实现BindView效果"));
        adapter.addData(new MainListEntity("AIDL测试", "AIDL通讯测试，Client逻辑"));
        adapter.addData(new MainListEntity("Socket测试","测试Socket连接"));
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                switch (position) {
                    case ConstData.QR_CODE: {
                        startQRCode();
                        break;
                    }
                    case ConstData.DOWNLOAD_MANAGER: {
                        if (!PermissionManager.checkPermissions(activity, PermissionManager.ReadAndWritePermissions)) {
                            MainActivity.super.requestPermission(PermissionManager.ReadAndWritePermissions, new PermissionManager.PermissionCallback() {
                                @Override
                                public void onPermissionGranted(String[] permissions) {
                                    DownloadFileManager.download(activity, DownloadFileManager.testUrl);
                                    MainActivity.this.registerReceiver(DownloadFileManager.receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                                }

                                @Override
                                public void onPermissionDenied(String[] permissions) {
                                    Toast.makeText(activity, "无权限", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Logger.d("开启文件下载");
                            DownloadUtils downloadUtils = new DownloadUtils(activity, DownloadFileManager.testUrl, "2.3.5.apk");
                            downloadUtils.startDownLoad();
                        }
                        break;
                    }
                    case ConstData.ANNOTATION: {
                        Intent intent = new Intent();
                        intent.setClass(activity, AnnotationTestActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case ConstData.AIDL_TEST: {
                        if (iMyAidlInterface != null) {
                            try {
                                data = iMyAidlInterface.getName();
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                        Toast.makeText(MainActivity.this, data, Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case ConstData.SOCKET_IM: {

                        break;
                    }
                }
            }
        });
        binding.mainList.setAdapter(adapter);
        binding.mainList.setLayoutManager(new LinearLayoutManager(this));

    }

    /**
     * 绑定AIDL Service
     */
    private void bindAIDL() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.RESPOND_AIDL_MESSAGE1");
        Intent finalIntent = achieveExplicitFromImplicitIntent(this, intent);
//        intent.setPackage("com.example.workdemo2");
        Logger.d("设置Intent");
        bindService(finalIntent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                iMyAidlInterface = IMyAidlInterface.Stub.asInterface(service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                iMyAidlInterface = null;
                Logger.d("连接断开");
            }
        }, BIND_AUTO_CREATE);
    }

    /**
     * 设置AIDL intent
     *
     * @param context
     * @param implicitIntent
     * @return
     */
    public Intent achieveExplicitFromImplicitIntent(Context context, Intent implicitIntent) {
        // Retrieve all services that can match the given intent
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);

        // Make sure only one match was found
        if (resolveInfo == null || resolveInfo.size() != 1) {
            return null;
        }

        // Get component info and create ComponentName
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);

        // Create a new intent. Use the old one for extras and such reuse
        Intent explicitIntent = new Intent(implicitIntent);

        // Set the component to be explicit
        explicitIntent.setComponent(component);

        return explicitIntent;
    }


    @Override
    protected ActivityMainBinding setBinding() {
        return ActivityMainBinding.inflate(getLayoutInflater());
    }

    /**
     * 打开二维码页面
     */
    private void startQRCode() {
//        startActivityForResult(new Intent(activity, CaptureActivity.class), requestCode);
        Intent intent = new Intent();
        intent.setClass(activity, TestQRCodeActivity.class);
        startActivity(intent);
    }
}