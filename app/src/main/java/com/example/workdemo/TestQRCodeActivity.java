package com.example.workdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.common.base.BaseActivity;
import com.king.zxing.CaptureHelper;
import com.king.zxing.DecodeFormatManager;
import com.king.zxing.OnCaptureCallback;
import com.king.zxing.ViewfinderView;
import com.king.zxing.camera.CameraManager;
import com.king.zxing.camera.FrontLightMode;

public class TestQRCodeActivity extends BaseActivity implements OnCaptureCallback{

    private ViewfinderView vfv;
    private CaptureHelper captureHelper;
    private SurfaceView svPreview;
    private TextView tvTorch;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_q_r_code);
        vfv = findViewById(R.id.vfv);
        svPreview = findViewById(R.id.sv_preview);
        tvTorch = findViewById(R.id.tv_torch);
        activity = this;
        captureHelper = new CaptureHelper(this, svPreview,vfv,null);
        captureHelper.setOnCaptureCallback(this);
        captureHelper.decodeFormats(DecodeFormatManager.QR_CODE_FORMATS)
                .supportAutoZoom(true) // 自动缩放
                .fullScreenScan(true) // 全屏扫码识别
                .supportLuminanceInvert(true) // 是否支持识别反色码，黑白颜色反转，开启提高识别效率
                .continuousScan(true) // 开启连续扫描
                .autoRestartPreviewAndDecode(false) // 连续扫描开启情况下，取消自动继续扫描，自己处理完后调用restartPreviewAndDecode()
                .playBeep(true) // 播放beep声音
                .supportZoom(true) // 支持双指缩放
                .frontLightMode(FrontLightMode.OFF) // 默认关闭闪光灯
                .setOnCaptureCallback(this) // 设置回调
                .onCreate();
        CameraManager cameraManager = captureHelper.getCameraManager();
        cameraManager.setOnTorchListener((torch) -> {
            tvTorch.setSelected(torch);
            tvTorch.setText(torch ? "关闭照明" : "打开照明");
        });
        tvTorch.setOnClickListener(v ->
                cameraManager.setTorch(!tvTorch.isSelected()));
    }

    @Override
    protected void onResume() {
        //注意在声明周期的设定，否则可能不会出现
        captureHelper.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        captureHelper.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        captureHelper.onDestroy();
        super.onDestroy();
    }

    /**
     * 接收扫码结果回调
     * @param result 扫码结果
     * @return 返回true表示拦截，将不自动执行后续逻辑，为false表示不拦截，默认不拦截
     */
    @Override
    public boolean onResultCallback(String result) {
        Toast.makeText(this, result, Toast.LENGTH_LONG).show();
        return false;
    }
}