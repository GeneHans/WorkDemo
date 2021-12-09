package com.example.workdemo;

import android.app.Application;

public class WorkDemoApplication extends Application {

    public static WorkDemoApplication context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }
}
