package com.example.asus1.videorecoder.Camera;

import android.app.Activity;
import android.content.Context;
import android.graphics.SurfaceTexture;

import com.example.asus1.videorecoder.RecordSetting;

import java.nio.channels.AcceptPendingException;

public abstract class BaseCamera {

    protected Activity mActivity;
    protected SurfaceTexture mSurfaceTexture;
    protected RecordSetting.CameraOrientation mFacing;

    abstract void init(RecordSetting.CameraOrientation position);
    abstract void startPreview(SurfaceTexture surfaceTexture);
    abstract void changeCamera(RecordSetting.CameraOrientation position);
    abstract void stopPreview();
    abstract void release();

}
