package com.example.workdemo.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.annotationtest.BindView;
import com.example.apt_lib.BindViewTools;
import com.example.common.util.Logger;
import com.example.workdemo.R;
import com.example.workdemo.WorkDemoApplication;

public class AnnotationTestActivity extends AppCompatActivity {

    @BindView(R.id.tv_annotation_test1)
    TextView tvAnnotationTest1;

    @BindView(R.id.tv_annotation_test2)
    TextView tvAnnotationTest2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annotation_test);
        BindViewTools.bind(this);
        tvAnnotationTest1.setText("测试文本");
        tvAnnotationTest1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.toast(WorkDemoApplication.context,"控件1:"+R.id.tv_annotation_test1);
            }
        });
        tvAnnotationTest2.setText("另一个文本");
        tvAnnotationTest2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.toast(WorkDemoApplication.context,"控件2:"+R.id.tv_annotation_test2);
            }
        });
    }
}