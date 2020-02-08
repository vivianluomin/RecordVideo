package com.example.asus1.videorecoder.Camera;

import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.view.Surface;

import com.example.asus1.videorecoder.RecordSetting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Camera1 implements CameraListener {

    //private Surface mSurface;
    private RecordSetting.CameraOrientation mFacing;
    private int mIndex;
    private int mRotation;
    private Camera mCameraDevice;

    @Override
    public void init(RecordSetting.CameraOrientation position) {
        int nums = Camera.getNumberOfCameras();
        int facing = position == RecordSetting.CameraOrientation.front?
                Camera.CameraInfo.CAMERA_FACING_FRONT:
                Camera.CameraInfo.CAMERA_FACING_BACK;
        mFacing = position;
        int index = -1;
        for(int i = 0;i<nums;i++){
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i,info);
            if(facing == info.facing){
                index = i;
                break;
            }
        }

        if(index < 0){
            index = 0;
            mFacing = RecordSetting.CameraOrientation.back;
        }

        mIndex = index;

        mCameraDevice = Camera.open(mIndex);
        Camera.Parameters params = mCameraDevice.getParameters();

        if (params.getMaxNumMeteringAreas() > 0){ // check that metering areas are supported
            List<Camera.Area> meteringAreas = new ArrayList<>();

            Rect areaRect1 = new Rect(-100, -100, 100, 100);    // specify an area in center of image
            meteringAreas.add(new Camera.Area(areaRect1, 600)); // set weight to 60%
            Rect areaRect2 = new Rect(800, -1000, 1000, -800);  // specify an area in upper right of image
            meteringAreas.add(new Camera.Area(areaRect2, 400)); // set weight to 40%
            params.setMeteringAreas(meteringAreas);
        }

        params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        mCameraDevice.setParameters(params);
    }

    @Override
    public void startPreview(SurfaceTexture surfaceTexture) {
        try {
            surfaceTexture.setDefaultBufferSize(1280,720);
            mCameraDevice.setPreviewTexture(surfaceTexture);
            mCameraDevice.startPreview();
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    @Override
    public int getRotation() {
        return mRotation;
    }

    @Override
    public void stopPreview() {
        mCameraDevice.stopPreview();
    }

    @Override
    public void release() {
        mCameraDevice.release();
    }
}
