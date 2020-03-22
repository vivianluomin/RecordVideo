package com.example.asus1.videorecoder.OpenGL;

import android.opengl.EGL14;
import android.opengl.EGLContext;
import android.util.Log;
import android.view.Surface;

import com.example.asus1.videorecoder.Camera.OpenGLLifeListener;
import com.example.asus1.videorecoder.RecordSetting;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class OpenGLHelper {

    static {
        System.loadLibrary("OpenGLHelper");
    }

    private OpenGLLifeListener mLifeListener;
    private static final String TAG = "OpenGLHelper";

    public float[] mvp = new float[16];

    public OpenGLHelper(OpenGLLifeListener lifeListener){
        mLifeListener = lifeListener;
    }

    private long mHandler;

    public void initOpenGL(Surface surface, RecordSetting.Filter filter,int width,int height){
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
        mHandler = nativeInitOpenGL(surface,fi,width,height);
    }

    public void changeFilter(RecordSetting.Filter filter){
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
        nativeChangeFilter(mHandler,fi);
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

    public void onOpenGLRunning(){
        if(mLifeListener!=null){
            mLifeListener.onOpenGLRunning();
        }
    }

    public void startRecord(){
        nativeStartRecord(mHandler);
    }

    public void stopRecord(){
        nativeStopRecord(mHandler);
    }

    public void onEncode(int textId){
        if(mLifeListener != null){
            mLifeListener.onEncode(textId,mvp);
        }
    }

    public void setShareEGLContext(long openglThread){
        if(mLifeListener != null){
            Log.d(TAG, "setShareEGLContext: "+openglThread);
                mLifeListener.setShareEGLContext(openglThread);
        }
    }

    private native long nativeInitOpenGL(Surface surface, int filter,int width,int height);

    private native void render(long hander,int textureId,float[] mat);

    private native void destroyOpenGL(long hander);

    private native void nativeStartRecord(long handler);

    private native void nativeStopRecord(long handler);

    private native void nativeChangeFilter(long handler,int filter);
}
