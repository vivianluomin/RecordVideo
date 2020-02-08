package com.example.asus1.videorecoder.Camera;

import android.graphics.SurfaceTexture;

import com.example.asus1.videorecoder.RecordSetting;

public interface CameraListener {

     void init(RecordSetting.CameraOrientation position);
     void startPreview(SurfaceTexture surfaceTexture);
     void stopPreview();
     void release();

     int getRotation();

}
