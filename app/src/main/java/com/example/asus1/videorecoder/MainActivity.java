package com.example.asus1.videorecoder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

public class MainActivity extends BaseActivity {

    private RadioGroup mFilterGroup;
    private RadioGroup mCameraTypeGroup;
    private RadioGroup mCameraGroup;
    private RadioGroup mMuxerTypeGroup;
    private RecordSetting mRecordSettings;
    private TextView mRecordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        setGroupListener();
    }

    private void init(){
        mFilterGroup = findViewById(R.id.rg_filter);
        mCameraTypeGroup = findViewById(R.id.rg_camera_type);
        mCameraGroup = findViewById(R.id.rg_camera);
        mMuxerTypeGroup = findViewById(R.id.rg_muxer);
        mRecordText = findViewById(R.id.tv_record);
        mRecordSettings = RecordSetting.BUILD()
                .setFiler(RecordSetting.Filter.normal)
                .setCameraType(RecordSetting.CameraType.Camera1)
                .setCameraOri(RecordSetting.CameraOrientation.front)
                .setMuxerType(RecordSetting.MuxerType.GPU);
        mRecordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    private void setGroupListener(){
        mFilterGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                   case  R.id.filter_normal:
                       mRecordSettings.setFiler(RecordSetting.Filter.normal);
                       break;
                    case  R.id.filter_dark:
                        mRecordSettings.setFiler(RecordSetting.Filter.dark);
                        break;
                    case  R.id.filter_fudiao:
                        mRecordSettings.setFiler(RecordSetting.Filter.fudiao);
                        break;
                    case  R.id.filter_mohu:
                        mRecordSettings.setFiler(RecordSetting.Filter.mohu);
                        break;
                    case  R.id.filter_mopi:
                        mRecordSettings.setFiler(RecordSetting.Filter.mopi);
                        break;
                }
            }
        });

        mCameraTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.camera1:
                        mRecordSettings.setCameraType(RecordSetting.CameraType.Camera1);
                        break;
                    case R.id.camera2:
                        mRecordSettings.setCameraType(RecordSetting.CameraType.Camera2);
                        break;
                }
            }
        });

        mCameraGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.camera_front:
                        mRecordSettings.setCameraOri(RecordSetting.CameraOrientation.front);
                        break;
                    case R.id.camera_back:
                        mRecordSettings.setCameraOri(RecordSetting.CameraOrientation.back);
                        break;
                }
            }
        });

        mMuxerTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.muxer_GPU:
                        mRecordSettings.setMuxerType(RecordSetting.MuxerType.GPU);
                        break;
                    case R.id.muxer_CPU:
                        mRecordSettings.setMuxerType(RecordSetting.MuxerType.CPU);
                        break;
                }

            }
        });
    }

}
