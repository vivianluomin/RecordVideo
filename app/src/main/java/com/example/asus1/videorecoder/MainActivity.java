package com.example.asus1.videorecoder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus1.videorecoder.Camera.RecordActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private RadioGroup mFilterGroup;
    private RadioGroup mCameraTypeGroup;
    private RadioGroup mCameraGroup;
    private RadioGroup mMuxerTypeGroup;
    private RecordSetting mRecordSettings;
    private TextView mRecordText;
    private boolean mHasPermission = false;

    private static final String TAG = "MainActivity";

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
                if(!mHasPermission){
                    Log.d(TAG, "startRecord: "+mHasPermission);
                    return;
                }
                Intent intent =  new Intent(MainActivity.this, RecordActivity.class);
                intent.putExtra("setting",mRecordSettings);
                startActivity(intent);
            }
        });

        setPermission();

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



    private void setPermission(){
        String[] permissions = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        };
        List<String> pers = new ArrayList<>();

        for(String per :permissions){
            if(ContextCompat.checkSelfPermission(this,per)
                    != PackageManager.PERMISSION_GRANTED){
                pers.add(per);
            }
        }

        if(pers.size()>0){
            ActivityCompat.requestPermissions(this,pers.toArray(new String[pers.size()]),
                    100);
        }else {
            mHasPermission = true;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for(int i = 0;i<grantResults.length;i++){
            Log.d(TAG, "onRequestPermissionsResult: "+permissions[i]);
            if(grantResults[i]!= PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,getResources().
                        getText(R.string.requestPermission),Toast.LENGTH_SHORT).show();
                mHasPermission = false;
                return;
            }
            mHasPermission = true;
        }
    }

}
