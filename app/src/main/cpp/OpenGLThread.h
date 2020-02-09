//
// Created by asus1 on 2020/2/8.
//

#ifndef VIDEORECODER_OPENGLTHREAD_H
#define VIDEORECODER_OPENGLTHREAD_H

//C++与java交互的实现
#include <jni.h>
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
#include "TextureDrawer.h"

//这三行主要是用来定义LOGI和LOGE的，看到原函数多复杂了吧，用这个会疯掉的
#define LOG_GL "OpenGLThread"
//#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_GL, __VA_ARGS__)
//#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_GL, __VA_ARGS__)

class OpenGLThread{
private:EGLDisplay  display;
        EGLContext context;
        EGLSurface surface;
        float *mat;
        TextureDrawer drawer;
public:
    bool initOpenGlES(ANativeWindow *nativeWindow);
    bool renderUpdate(int textId, float *mat);
    bool destoryOpenGLES();

};



#endif //VIDEORECODER_OPENGLTHREAD_H
