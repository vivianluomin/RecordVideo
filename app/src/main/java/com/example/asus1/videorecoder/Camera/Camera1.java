package com.example.asus1.videorecoder.Camera;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;
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
    private Activity mActivity;

    private static final String TAG = "Camera1";


    public Camera1(Activity activity){
        mActivity = activity;
    }

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
            //params.setMeteringAreas(meteringAreas);
        }

        //params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        //mCameraDevice.setParameters(params);

        setCameraDisplayOrientation(mActivity,mIndex,mCameraDevice);
    }

    @Override
    public void startPreview(SurfaceTexture surfaceTexture) {
        try {
            Log.d(TAG, "startPreview: ");
            surfaceTexture.setDefaultBufferSize(1920,1080);
            mCameraDevice.setPreviewTexture(surfaceTexture);
            mCameraDevice.startPreview();
        }catch (IOException e){
            e.printStackTrace();
        }

    }


    public void setCameraDisplayOrientation(Activity activity, int cameraId, android.hardware.Camera camera) {

        Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();

        android.hardware.Camera.getCameraInfo(cameraId, info);

        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();

        int degrees = 0;

        switch (rotation) {

            case Surface.ROTATION_0:
                degrees = 0;
                break;

            case Surface.ROTATION_90:
                degrees = 90;
                break;

            case Surface.ROTATION_180:
                degrees = 180;
                break;

            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;

        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {

            result = (info.orientation + degrees) % 360;

            result = (360 - result) % 360; // compensate the mirror

        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }

        camera.setDisplayOrientation(result);
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
