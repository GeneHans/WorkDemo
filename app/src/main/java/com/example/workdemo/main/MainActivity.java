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

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.navi.AmapPageType;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.example.common.ConstData;
import com.example.common.base.BaseActivity;
import com.example.common.manager.DownloadFileManager;
import com.example.common.manager.DownloadUtils;
import com.example.common.util.Logger;
import com.example.common.util.PermissionManager;
import com.example.workdemo.activity.AnnotationTestActivity;
import com.example.workdemo.activity.TestQRCodeActivity;
import com.example.workdemo.databinding.ActivityMainBinding;
import com.example.workdemo2.IMyAidlInterface;
import com.example.workdemo2.People;

import java.util.ArrayList;
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
    private People testPeople = new People(0,"no people");

    private void initMainPageAdapter() {
        MainPageAdapter adapter = new MainPageAdapter();
        adapter.addData(new MainListEntity("???????????????", "????????????????????????demo?????????zxing-lite??????"));
        adapter.addData(new MainListEntity("DownloadManager", "Android?????????????????????DownLoadManager??????"));
        adapter.addData(new MainListEntity("APT??????", "??????APT??????BindView??????"));
        adapter.addData(new MainListEntity("AIDL??????", "AIDL???????????????Client??????"));
        adapter.addData(new MainListEntity("Socket??????", "??????Socket??????"));
        adapter.addData(new MainListEntity("????????????", "????????????SDK????????????"));
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
                                    Toast.makeText(activity, "?????????", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Logger.d("??????????????????");
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
                                testPeople = iMyAidlInterface.getPeople();
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                        Toast.makeText(MainActivity.this, testPeople.toString(), Toast.LENGTH_SHORT).show();
                        Logger.d(testPeople.toString());
                        break;
                    }
                    case ConstData.SOCKET_IM: {
                    }

                    case ConstData.GAODE_MAP: {
                        goToNavi(true);
//                        openMap();
                        break;
                    }
                }
            }
        });
        binding.mainList.setAdapter(adapter);
        binding.mainList.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * ??????AIDL Service
     */
    private void bindAIDL() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.RESPOND_AIDL_MESSAGE1");
        Intent finalIntent = achieveExplicitFromImplicitIntent(this, intent);
//        intent.setPackage("com.example.workdemo2");
        Logger.d("??????Intent");
        bindService(finalIntent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                iMyAidlInterface = IMyAidlInterface.Stub.asInterface(service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                iMyAidlInterface = null;
                Logger.d("????????????");
            }
        }, BIND_AUTO_CREATE);
    }

    /**
     * ??????AIDL intent
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


    private void openMap() {
        if (!PermissionManager.checkPermissions(this, PermissionManager.LocationPermissions)) {
            MainActivity.super.requestPermission(PermissionManager.LocationPermissions, new PermissionManager.PermissionCallback() {
                @Override
                public void onPermissionGranted(String[] permissions) {
                    goToNavi(true);
                }

                @Override
                public void onPermissionDenied(String[] permissions) {
                    Toast.makeText(activity, "???????????????", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            goToNavi(true);
        }
    }

    private void goToNavi(boolean hasDes) {
        if (hasDes) {
            //??????
            Poi start = new Poi("??????????????????", new LatLng(40.080525, 116.603039), "");
            //?????????
            List<Poi> poiList = new ArrayList();
            poiList.add(new Poi("??????", new LatLng(39.918058, 116.397026), ""));
            //??????
            Poi end = new Poi("????????????", new LatLng(39.941823, 116.426319), "");
            // ??????????????????
            AmapNaviParams params = new AmapNaviParams(start, poiList, end, AmapNaviType.DRIVER, AmapPageType.ROUTE);
            // ????????????
            AmapNaviPage.getInstance().showRouteActivity(getApplicationContext(), params, null);
        } else {
            //???????????????????????????????????????????????????????????????????????? ??????????????????
            AmapNaviParams params = new AmapNaviParams(null, null, null, AmapNaviType.DRIVER, AmapPageType.ROUTE);
            //??????????????????
            AmapNaviPage.getInstance().showRouteActivity(getApplicationContext(), params, null);
        }
    }

    @Override
    protected ActivityMainBinding setBinding() {
        return ActivityMainBinding.inflate(getLayoutInflater());
    }

    /**
     * ?????????????????????
     */
    private void startQRCode() {
//        startActivityForResult(new Intent(activity, CaptureActivity.class), requestCode);
        Intent intent = new Intent();
        intent.setClass(activity, TestQRCodeActivity.class);
        startActivity(intent);
    }
}