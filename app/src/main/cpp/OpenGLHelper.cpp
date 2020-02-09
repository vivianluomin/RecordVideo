//
// Created by asus1 on 2020/2/8.
//

#include <jni.h>
#include "OpenGLThread.h"
#include <pthread.h>

extern "C"
JNIEXPORT jlong JNICALL
Java_com_example_asus1_videorecoder_OpenGL_OpenGLHelper_initOpenGL(JNIEnv *env,
                                                                   jobject instance){
    OpenGLThread *handler = new OpenGLThread();


    return (jlong)handler;

}
