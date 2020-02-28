package com.example.asus1.videorecoder.Camera;

import android.opengl.EGL14;
import android.opengl.EGLContext;

public interface OpenGLLifeListener {

    void onOpenGLinitSuccess();

    void onOpenGLRunning();

    void onEncode(int textId,float[] mvp);

    void setShareEGLContext(long openglHelper);
}
