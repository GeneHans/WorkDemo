package com.example.workdemo;

import android.app.Application;

import com.amap.api.navi.NaviSetting;

public class WorkDemoApplication extends Application {

    public static WorkDemoApplication context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        NaviSetting.updatePrivacyShow(context, true, true);
        NaviSetting.updatePrivacyAgree(context, true);
    }
}
