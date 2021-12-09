package com.example.workdemo.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.example.common.base.BaseActivity;
import com.example.common.util.Logger;
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
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                Logger.d(position+"");
                switch (position){
                    case 0:{
                        startQRCode();
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