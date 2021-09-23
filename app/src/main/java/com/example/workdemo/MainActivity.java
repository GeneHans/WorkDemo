package com.example.workdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.common.base.BaseActivity;
import com.king.zxing.CaptureActivity;

public class MainActivity extends BaseActivity {
    private Activity activity;
    private TextView textView;
    private String[] permissions;
    private final int requestCode = 111;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;
        textView = findViewById(R.id.tv_main);
        permissions = new String[]{
                Manifest.permission.CAMERA
        };
        super.requestPermission(permissions,100);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivityForResult(new Intent(activity, CaptureActivity.class),requestCode);
                Intent intent = new Intent();
                intent.setClass(activity,TestQRCodeActivity.class);
                startActivityForResult(intent,requestCode);
            }
        });
    }
}