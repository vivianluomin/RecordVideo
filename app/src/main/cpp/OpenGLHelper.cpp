//
// Created by asus1 on 2020/2/8.
//

#include "include/OpenGLThread.h"

extern "C"
JNIEXPORT jlong JNICALL
Java_com_example_asus1_videorecoder_OpenGL_OpenGLHelper_nativeInitOpenGL(JNIEnv *env,
                                                                   jobject instance,
                                                                   jobject surface
                                                                    ,jint filter,int width,int height){
    OpenGLThread *handler = new OpenGLThread();
    handler->width = width;
    handler->height = height;
    handler->filter = filter;
    ANativeWindow *window = ANativeWindow_fromSurface(env,surface);
    handler->openglHepler = env->NewGlobalRef(instance);
    jclass openglHelper = env->GetObjectClass(instance);
    jmethodID openglSucssess = env->GetMethodID(openglHelper,"onOpenGLinitSuccess","()V");
    handler->onOpenGLinitSucccess_method = openglSucssess;
    handler->onOpenGLRunning_method = env->GetMethodID(openglHelper,"onOpenGLRunning","()V");
    handler->onEncode_method = env->GetMethodID(openglHelper,"onEncode","(I)V");
    handler->setShareEGLContext_method = env->GetMethodID(openglHelper,"setShareEGLContext","(J)V");
    handler->mvp_filed = env->GetFieldID(openglHelper,"mvp","[F");
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
    delete(mvp);

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




extern "C"
JNIEXPORT void JNICALL
Java_com_example_asus1_videorecoder_OpenGL_OpenGLHelper_nativeStartRecord(JNIEnv *env,
                                                                      jobject instance,
                                                                      jlong handler){

    OpenGLThread *hand = reinterpret_cast<OpenGLThread *>(handler);
    if(hand == NULL){
        return;
    }
    hand->startRecord();
}



extern "C"
JNIEXPORT void JNICALL
Java_com_example_asus1_videorecoder_OpenGL_OpenGLHelper_nativeStopRecord(JNIEnv *env,
                                                                          jobject instance,
                                                                          jlong handler){

    OpenGLThread *hand = reinterpret_cast<OpenGLThread *>(handler);
    if(hand == NULL){
        return;
    }
    hand->stopRecord();
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *jvm, void *reserved) {

    jint result = -1;

    OpenGLThread::JVMInstance = jvm;

    result = JNI_VERSION_1_4;
    return result;
}

JNIEXPORT void JNICALL JNI_OnUnload(JavaVM* vm, void* reserved)
{
    //TODO-
    OpenGLThread::JVMInstance = nullptr;
}

