package com.example.workdemo.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.viewbinding.ViewBinding;

import com.example.common.base.BaseActivity;
import com.example.common.util.Logger;
import com.example.workdemo.R;
import com.example.workdemo.databinding.ActivityTestQRCodeBinding;
import com.king.zxing.CaptureHelper;
import com.king.zxing.DecodeFormatManager;
import com.king.zxing.OnCaptureCallback;
import com.king.zxing.ViewfinderView;
import com.king.zxing.camera.CameraManager;
import com.king.zxing.camera.FrontLightMode;

public class TestQRCodeActivity extends BaseActivity<ActivityTestQRCodeBinding> implements OnCaptureCallback{

    private CaptureHelper captureHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //权限申请
        String[] permissions = new String[]{
                Manifest.permission.CAMERA
        };
        super.requestPermission(permissions,100);

        captureHelper = new CaptureHelper(this, binding.svPreview,binding.vfv,null);
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
            binding.tvTorch.setSelected(torch);
            binding.tvTorch.setText(torch ? "关闭照明" : "打开照明");
        });
        binding.tvTorch.setOnClickListener(v ->
                cameraManager.setTorch(!binding.tvTorch.isSelected()));
        binding.tvTorch.post(this::updateScanFrameLocation);
    }

    @Override
    protected ActivityTestQRCodeBinding setBinding() {
        return ActivityTestQRCodeBinding.inflate(getLayoutInflater());
    }

    private void updateScanFrameLocation() {
//        int top = tvTorch.getTop();
//        int bottom = svPreview.getBottom();
//        int paddingBottom = (bottom - top)/2;
//        vfv.setPadding(0, 0, 0, paddingBottom);
        int temp = (327+184)/2-184;
        Logger.d(temp+"");
        binding.vfv.setPadding(0, 0, 0, dp2px(this,temp));
        binding.vfv.scannerStart = 0; // 动态改变padding时，需要设置该值为0，以触发在onDraw中对其的重新赋值
    }

    public static int dp2px(Context context, int dpVal) {
        if (context == null) return 0;
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpVal * scale + 0.5f);
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