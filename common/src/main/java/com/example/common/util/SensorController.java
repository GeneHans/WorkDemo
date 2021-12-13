package com.example.common.util;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.example.common.utilInterface.ISensorCallBack;

/**
 * 监听参数
 */
public class SensorController {
    private SensorManager sensorManager;
    private Sensor sensor;
    //最低触发时间间隔
    private volatile long timeInterval = 3000;
    //最低触发速度，用力控制灵敏度
    private volatile double acceleration = 10;

    private float lastX = 0f;
    private float lastY = 0f;
    private float lastZ = 0f;
    private long lastTime = 0;
    //判断是否初始化成功
    private boolean isInitPosition = false;
    private SensorEventListener listener;
    private ISensorCallBack iSensorCallBack;

    public SensorController(Context context, long time, double acceleration, ISensorCallBack iSensorCallBack) {
        this.iSensorCallBack = iSensorCallBack;
        initSensorController(context);
        updateTimeAndAcceleration(time, acceleration);
    }

    /**
     * 初始化
     *
     * @param context
     */
    private void initSensorController(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        lastTime = System.currentTimeMillis();
        listener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                try {
                    long currentTime = System.currentTimeMillis();
                    if (timeInterval > currentTime - lastTime) {
                        return;//如果两次回调间隔过小，直接忽略
                    }
                    //读取传感器监听到加速度 m/(s^2)
                    float[] values = event.values;
                    if(!isInitPosition){
                        //手机的状态不同，其x,y,z的值不同，重新对x,y,z赋值
                        lastX = values[0];
                        lastY = values[1];
                        lastZ = values[2];
                        //仅限在初始化赋值
                        isInitPosition = true;
                    }
                    float x = values[0];
                    float y = values[1];
                    float z = values[2];

                    //计算和上次的变化值
                    float deltaX = x - lastX;
                    float deltaY = y - lastY;
                    float deltaZ = z - lastZ;

                    //更新变化值
                    lastX = x;
                    lastY = y;
                    lastZ = z;
                    //计算灵敏度 m/(s^3)
                    double speed = (Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) / timeInterval) * 1000;
                    if (speed > acceleration) {
                        iSensorCallBack.action();
                        lastTime = System.currentTimeMillis();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
    }

    /**
     * 更新摇一摇参数
     *
     * @param time:触发的最小间隔
     * @param acceleration:触发的最低加速度
     */
    public void updateTimeAndAcceleration(long time, double acceleration) {
        timeInterval = time;
        this.acceleration = acceleration;
    }

    /**
     * 注册监听
     */
    public void register() {
        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI);
    }

    /**
     * 取消监听注册
     */
    public void unregister() {
        sensorManager.unregisterListener(listener, sensor);
    }
}