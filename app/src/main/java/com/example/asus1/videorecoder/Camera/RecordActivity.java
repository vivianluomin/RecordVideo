package com.example.asus1.videorecoder.Camera;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceView;

import com.example.asus1.videorecoder.R;
import com.example.asus1.videorecoder.RecordSetting;

public class RecordActivity extends AppCompatActivity {

    private SurfaceView mSurfaceView;
    private RecordSetting mRecordSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        Intent intent = getIntent();
        mRecordSetting = (RecordSetting) intent.getSerializableExtra("setting");
        init();
        starPreview();
    }

    private void init(){
        mSurfaceView = findViewById(R.id.surface_view);
    }

    private void starPreview(){

    }
}
