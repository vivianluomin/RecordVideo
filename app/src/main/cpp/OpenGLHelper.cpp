//
// Created by asus1 on 2020/2/8.
//

#include <jni.h>
#include "include/OpenGLThread.h"

extern "C"
JNIEXPORT jlong JNICALL
Java_com_example_asus1_videorecoder_OpenGL_OpenGLHelper_initOpenGL(JNIEnv *env,
                                                                   jobject instance,
                                                                   jobject surface,jint filter){
    OpenGLThread *handler = new OpenGLThread();

    ANativeWindow *window = ANativeWindow_fromSurface(env,surface);
    handler->startOpenGLThread(window);
    return (jlong)handler;

}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_asus1_videorecoder_OpenGL_OpenGLHelper_render(JNIEnv *env,
                                                               jobject instance,jlong handler,
                                                                jint textureId,
                                                               jfloatArray mat){

    OpenGLThread *hand = reinterpret_cast<OpenGLThread *>(handler);
    if(hand == NULL){
        return;
    }

    jfloat * array = env->GetFloatArrayElements(mat,0);
    int length = env->GetArrayLength(mat);

    float *mvp = new float[length];

    for(int i = 0;i<length;i++){
        mvp[i] = array[i];
    }

    hand->renderUpdate(textureId,mvp);
    env->ReleaseFloatArrayElements(mat,array,0);

}




extern "C"
JNIEXPORT void JNICALL
Java_com_example_asus1_videorecoder_OpenGL_OpenGLHelper_destroyOpenGL(JNIEnv *env,
                                                                      jobject instance,
                                                                      jlong handler){

    OpenGLThread *hand = reinterpret_cast<OpenGLThread *>(handler);
    if(hand == NULL){
        return;
    }
    hand->destoryOpenGLES();
}

