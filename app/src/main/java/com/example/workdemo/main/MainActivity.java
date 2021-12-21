package com.example.workdemo.main;

import android.app.DownloadManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
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
import com.example.workdemo.ConstData;
import com.example.workdemo.activity.AnnotationTestActivity;
import com.example.workdemo.activity.TestQRCodeActivity;
import com.example.workdemo.databinding.ActivityMainBinding;

public class MainActivity extends BaseActivity<ActivityMainBinding> {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initMainPageAdapter();
    }

    private void initMainPageAdapter(){
        MainPageAdapter adapter = new MainPageAdapter();
        adapter.addData(new MainListEntity("二维码扫描","二维码扫描页简易demo，利用zxing-lite实现"));
        adapter.addData(new MainListEntity("DownloadManager","Android自带的下载工具DownLoadManager测试"));
        adapter.addData(new MainListEntity("APT测试","利用APT实现BindView效果"));
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                Logger.d(position+"");
                switch (position){
                    case ConstData.QR_CODE: {
                        startQRCode();
                        break;
                    }
                    case ConstData.DOWNLOAD_MANAGER: {
                        if(!PermissionManager.checkPermissions(activity,PermissionManager.ReadAndWritePermissions)){
                            MainActivity.super.requestPermission(PermissionManager.ReadAndWritePermissions, new PermissionManager.PermissionCallback() {
                                @Override
                                public void onPermissionGranted(String[] permissions) {
                                    DownloadFileManager.download(activity, DownloadFileManager.testUrl);
                                    MainActivity.this.registerReceiver(DownloadFileManager.receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                                }

                                 @Override
                                public void onPermissionDenied(String[] permissions) {
                                    Toast.makeText(activity,"无权限",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else{
                            Logger.d("开启文件下载");
                            DownloadUtils downloadUtils = new DownloadUtils(activity,DownloadFileManager.testUrl,"2.3.5.apk");
                            downloadUtils.startDownLoad();
//                            DownloadFileManager.download(activity, DownloadFileManager.testUrl);
//                            MainActivity.this.registerReceiver(DownloadFileManager.receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                        }
                        break;
                    }
                    case ConstData.ANNOTATION:{
                        Intent intent = new Intent();
                        intent.setClass(activity, AnnotationTestActivity.class);
                        startActivity(intent);
                        break;
                    }
                }
            }
        });
        binding.mainList.setAdapter(adapter);
        binding.mainList.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected ActivityMainBinding setBinding() {
        return ActivityMainBinding.inflate(getLayoutInflater());
    }

    /**
     * 打开二维码页面
     */
    private void startQRCode(){
//        startActivityForResult(new Intent(activity, CaptureActivity.class), requestCode);
        Intent intent = new Intent();
        intent.setClass(activity, TestQRCodeActivity.class);
        startActivity(intent);
    }
}