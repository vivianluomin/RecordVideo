package com.example.asus1.videorecoder.OpenGL;

import android.util.Log;
import android.view.Surface;

import com.example.asus1.videorecoder.Camera.OpenGLLifeListener;
import com.example.asus1.videorecoder.RecordSetting;

public class OpenGLHelper {

    static {
        System.loadLibrary("OpenGLHelper");
    }

    private OpenGLLifeListener mLifeListener;
    private static final String TAG = "OpenGLHelper";

    public OpenGLHelper(OpenGLLifeListener lifeListener){
        mLifeListener = lifeListener;
    }

    private long mHandler;

    public void initOpenGL(Surface surface, RecordSetting.Filter filter){
        int fi = 0;
        if(filter == RecordSetting.Filter.normal){
            fi = 0;
        }else if(filter == RecordSetting.Filter.dark){
            fi = 1;
        }else if(filter == RecordSetting.Filter.fudiao){
            fi = 2;
        }else if(filter == RecordSetting.Filter.mohu){
            fi = 3;
        }else if(filter == RecordSetting.Filter.mopi){
            fi = 4;
        }
        mHandler = initOpenGL(surface,fi);
    }

    public void render(int textureId,float[] mat){
        if(mHandler != 0){
            render(mHandler,textureId,mat);
        }
    }

    public void deatoryOpenGL(){
        if(mHandler != 0){
            destroyOpenGL(mHandler);
        }
    }

    public void onOpenGLinitSuccess(){
        Log.d(TAG, "onOpenGLinitSuccess: ");
        if(mLifeListener != null){
            mLifeListener.onOpenGLinitSuccess();
        }
    }

    private native long initOpenGL(Surface surface, int filter);

    private native void render(long hander,int textureId,float[] mat);

    private native void destroyOpenGL(long hander);
}
