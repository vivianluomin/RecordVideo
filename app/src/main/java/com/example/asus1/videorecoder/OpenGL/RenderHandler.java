package com.example.asus1.videorecoder.OpenGL;

import android.opengl.EGL14;
import android.opengl.EGLContext;
import android.opengl.Matrix;
import android.util.Log;
import android.view.Surface;

import com.example.asus1.videorecoder.Encode.FFmpegMuxer;
import com.example.asus1.videorecoder.Encode.VideoMediaMuxer;
import com.example.asus1.videorecoder.RecordSetting;

public class RenderHandler implements Runnable {

    private boolean mRequestEGLContext = false;
    private int mRequestDraw = 0;

    private long mShareContext;
    private int mTextId;
    private Surface mLinkSurface;
    private Object mSyn = new Object();
    private float[] mStMatrix = new float[16];
    private boolean mRequestRelease = false;
    private boolean mSetContext = false;

    private static final String TAG = "RenderHandler";
    private FFmpegMuxer mDrawer;
    private RecordSetting.Filter mFilter;


    private RenderHandler(FFmpegMuxer drawer,RecordSetting.Filter filter){
        mDrawer = drawer;
        mFilter = filter;
    }

    public static RenderHandler createRenderHandler(FFmpegMuxer drawer, RecordSetting.Filter filter){
        RenderHandler handler = new RenderHandler(drawer,filter);
        return handler;
    }

    private void prepare(){
        mDrawer.initEGL(mLinkSurface,mShareContext,mFilter);
    }

    public void setEGLContext(long context,Surface surface,int textId){
        mShareContext = context;
        mLinkSurface = surface;
        mTextId = textId;
        Matrix.setIdentityM(mStMatrix,0);
        mRequestEGLContext = true;
        mSetContext = true;

    }

    public void draw(int textId,float[] stMatrix){
        if(mRequestRelease) return;
        synchronized (mSyn){
            mTextId = textId;
            Log.d(TAG, "draw: ");
           // System.arraycopy(stMatrix,0,mStMatrix,0,16);
            mStMatrix = stMatrix;
            mRequestDraw ++;
            mSyn.notifyAll();
        }

    }

    public void stop(){
        synchronized (mSyn){
            if(mRequestRelease) return;
            mRequestRelease = true;
        }

    }

    @Override
    public void run() {
        boolean localRequestDraw = false;
        Log.d(TAG, "run: ");
        for(;;){
            if(!mSetContext){
                continue;
            }
            if (mRequestRelease) break;
            if (mRequestEGLContext) {
                mRequestEGLContext = false;
                prepare();
            }

            localRequestDraw = mRequestDraw>0 ;
            if(localRequestDraw){
                if(mTextId>=0){
                    mRequestDraw --;
                    mDrawer.render(mTextId,mStMatrix);
                    Log.d(TAG, "render: end");
                }

            }else {
                synchronized (mSyn){
                    try {
                        if(mRequestDraw<=0){
                            Log.d(TAG, "wait: ");
                            mSyn.wait();
                        }
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }

            }
        }
    }
}
