//
// Created by asus1 on 2020/2/8.
//


#ifndef VIDEORECODER_OPENGLTHREAD_H
#define VIDEORECODER_OPENGLTHREAD_H

#include "TextureDrawer.h"
#include "LOG.h"
#include <pthread.h>

//C++的string，默认导入的，会用到的，放着吧
#include <string>
//打log用的，下面会把这里面复杂的函数简化就是#define的那几行
#include <android/log.h>
//这行和下边那行是用来引入ANativeWindow的
#include <android/native_window.h>
//用来引入ANativeWindow的
#include <android/native_window_jni.h>
#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>
#include <EGL/egl.h>
#include <EGL/eglext.h>

#define FILTER_NORMAL 0
#define FILTER_DRAK 1
#define FILTER_FUDIAO 2
#define FILTER_MOHU 3
#define FILTER_MOPI 4


void* start(void *glThread);




class OpenGLThread{
public:
    EGLDisplay  display;
    EGLContext context;
    EGLSurface surface;
    float *MVPMat = NULL;
    int textureId;
    TextureDrawer *drawer;
    pthread_t pid;
    ANativeWindow *window;
    bool threadStart = 0;
    pthread_mutex_t lock;
    bool render = false;
    jmethodID onOpenGLinitSucccess_method;
    jobject openglHepler;

public:
    static JavaVM *JVMInstance;
public:
    TextureDrawer * createTextureDrawer(int textureType);
    void startOpenGLThread(ANativeWindow *nativeWindow);
    bool initOpenGlES();
    bool renderUpdate(int textId, float *mat);
    bool destoryOpenGLES();

};





#endif //VIDEORECODER_OPENGLTHREAD_H