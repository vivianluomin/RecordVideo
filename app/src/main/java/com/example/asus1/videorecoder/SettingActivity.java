package com.example.asus1.videorecoder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

public class SettingActivity extends BaseActivity{

    private ImageView mBack;
    private Switch mCamera1;
    private Switch mCamera2;
    private Switch mGPUMuxer;
    private Switch mCPUMuxer;
    private TextView mSave;
    private RecordSetting.CameraType mCameraType = RecordSetting.CameraType.Camera1;
    private RecordSetting.MuxerType mMuxerType = RecordSetting.MuxerType.GPU;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        |View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        |View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        |View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        |View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                        |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

         Intent intent = getIntent();

        mCameraType = (RecordSetting.CameraType)
                intent.getSerializableExtra("camera");
        mMuxerType = (RecordSetting.MuxerType)
                intent.getSerializableExtra("muxer");

        init();
    }

    private void init(){
        mBack = findViewById(R.id.iv_back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mSave = findViewById(R.id.tv_save);
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("camera",mCameraType);
                intent.putExtra("muxer",mMuxerType);
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        mCamera1 = findViewById(R.id.switch_camera_1);
        mCamera1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mCameraType = RecordSetting.CameraType.Camera1;
                    mCamera2.setChecked(false);
                }else {
                    mCameraType = RecordSetting.CameraType.Camera2;
                    mCamera2.setChecked(true);
                }
            }
        });
        mCamera2 = findViewById(R.id.switch_camera_2);
        mCamera2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mCameraType = RecordSetting.CameraType.Camera2;
                    mCamera1.setChecked(false);
                }else {
                    mCameraType = RecordSetting.CameraType.Camera1;
                    mCamera1.setChecked(true);
                }
            }
        });
        mGPUMuxer = findViewById(R.id.switch_muxer_gpu);
        mGPUMuxer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               if(isChecked){
                   mMuxerType = RecordSetting.MuxerType.GPU;
                   mCPUMuxer.setChecked(false);
               }else {
                   mMuxerType = RecordSetting.MuxerType.CPU;
                   mCPUMuxer.setChecked(true);
               }
            }
        });

        mCPUMuxer = findViewById(R.id.switch_muxer_cpu);
        mCPUMuxer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mMuxerType = RecordSetting.MuxerType.CPU;
                    mGPUMuxer.setChecked(false);
                }else {
                    mMuxerType = RecordSetting.MuxerType.GPU;
                    mGPUMuxer.setChecked(true);
                }
            }
        });

        if(mCameraType == RecordSetting.CameraType.Camera1){
            mCamera1.setChecked(true);
        }else {
            mCamera2.setChecked(true);
        }

        if(mMuxerType == RecordSetting.MuxerType.GPU){
            mGPUMuxer.setChecked(true);
        }else {
            mCPUMuxer.setChecked(true);
        }

    }

}
