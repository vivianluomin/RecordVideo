package com.example.asus1.videorecoder.Camera;

import android.app.Activity;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Surface;

import com.example.asus1.videorecoder.RecordSetting;

import java.util.ArrayList;
import java.util.List;

public class Camera2 extends BaseCamera {

    private CameraManager mCameraManager;

    private String mCameraId;
    private Handler mMainHandler;
    private CameraDevice mCameraDevice;
    private CaptureRequest.Builder mPreviewBuilder;
    private CameraCharacteristics mCameraCharacteristics;
    private CameraCaptureSession mCameraCaptureSession;

    private static final String TAG = "Camera2";


    public Camera2(Activity activity){
        mActivity = activity;
        mMainHandler = new android.os.Handler(Looper.getMainLooper());
        mCameraManager = (CameraManager) mActivity.getSystemService(Context.CAMERA_SERVICE);
    }

    @Override
    public void init(RecordSetting.CameraOrientation position) {

        try {
            for(String id : mCameraManager.getCameraIdList()){
                CameraCharacteristics characteristics
                        = mCameraManager.getCameraCharacteristics(id);
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                RecordSetting.CameraOrientation fa =
                        facing == CameraCharacteristics.LENS_FACING_BACK?
                        RecordSetting.CameraOrientation.back:
                        RecordSetting.CameraOrientation.front;
                if( position == fa){
                    mCameraId = id;
                    break;
                }
            }

            if(mCameraId!=null){
                //打开摄像头
                mCameraCharacteristics = mCameraManager.getCameraCharacteristics(mCameraId);
            }

        }catch (CameraAccessException e){
            e.printStackTrace();
        }

    }

    @Override
    public void startPreview(SurfaceTexture surfaceTexture) {
        try {
            mCameraManager.openCamera(mCameraId,mStateCallback
                    ,mMainHandler);
            mSurfaceTexture = surfaceTexture;
        }catch (CameraAccessException e){
            e.printStackTrace();
        }

    }

    @Override
    void changeCamera(RecordSetting.CameraOrientation position) {
        stopPreview();
        release();
        init(position);
        startPreview(mSurfaceTexture);
    }

    @Override
    public void stopPreview() {
        try {
            mCameraCaptureSession.stopRepeating();
            mCameraCaptureSession.close();
            mCameraCaptureSession = null;
        }catch (CameraAccessException e){
            e.printStackTrace();
        }
    }

    @Override
    public void release() {
        mCameraDevice.close();
        mCameraDevice = null;
    }

    private CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            //相机设备
            mCameraDevice = camera;
            createPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {

            mCameraDevice.close();
            mCameraDevice = null;

        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {

            mCameraDevice.close();
            mCameraDevice = null;

        }
    };

    private void createPreview(){
        List<Surface> surfaces = new ArrayList<>();
        mSurfaceTexture.setDefaultBufferSize(1280,720);
        Surface surface = new Surface(mSurfaceTexture);
        surfaces.add(surface);
        try {
            //设置一个具有输出Surface的CaptureRequest.Builder
            mPreviewBuilder =  mCameraDevice.
                    createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewBuilder.addTarget(surface);

            //进行相机预览
            mCameraDevice.createCaptureSession(surfaces,mStateCallbackSession,null);

        }catch (CameraAccessException e){
            e.printStackTrace();
        }
    }


    private CameraCaptureSession.StateCallback  mStateCallbackSession = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {
            mCameraCaptureSession = session;
            mPreviewBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_VIDEO);
            //mPreviewBuilder.set(CaptureRequest.JPEG_ORIENTATION, ExifInterface.ORIENTATION_ROTATE_90);
            //mPreviewBuilder.set(CaptureRequest.NOISE_REDUCTION_MODE,CaptureRequest.NOISE_REDUCTION_MODE_FAST);
            mPreviewBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_AUTO);
            try {
                //发送请求
                session.setRepeatingRequest(mPreviewBuilder.build(),
                        null,null);

            }catch (CameraAccessException e){
                e.printStackTrace();
            }

        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
            Log.d(TAG, "onConfigureFailed: ");
        }
    };


}
