//
// Created by asus1 on 2020/2/8.
//

#include "include/OpenGLThread.h"

void*  start(void *gl) {

    OpenGLThread *glThread = static_cast<OpenGLThread *>(gl);

    int ret = glThread->initOpenGlES();
    if(ret != 0){
        LOGE("initOpenGL failed");
        return NULL;
    }

    glThread->drawer = new TextureDrawer();
    pthread_mutex_init(&glThread->lock,NULL);
    glThread->threadStart = true;
    while (glThread->threadStart){
        if(!glThread->render){
            pthread_mutex_lock(&glThread->lock);
        }
        glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT);
        glViewport(0,0,720,1280);
#ifdef __cplusplus
        glThread->drawer->draw(glThread->textureId,glThread->MVPMat);
#endif
        eglSwapBuffers(glThread->display,glThread->surface);
        glThread->render = false;
    }


    return NULL;

}


void OpenGLThread::startOpenGLThread(ANativeWindow *nativeWindow) {

    window = nativeWindow;
    pthread_create(&pid,NULL,start,this);
}

TextureDrawer* OpenGLThread::createTextureDrawer(int textureType) {
    TextureDrawer *drawer;
    switch (textureType){
        case FILTER_NORMAL:
            drawer = new TextureDrawer();
            break;

        case FILTER_DRAK:
            break;

        case FILTER_FUDIAO:
            break;
        case FILTER_MOHU:
            break;
        case FILTER_MOPI:
            break;
    }

    return drawer;
}


bool OpenGLThread::initOpenGlES() {
    display = eglGetDisplay(EGL_DEFAULT_DISPLAY);
    if(display == EGL_NO_DISPLAY){
        LOGE("eglGetDisplay error");
        return -1;
    }

    EGLint *version = new EGLint[2];
    if(!eglInitialize(display,&version[0],&version[1])){
        LOGE("eglInitialize error");
        return -1;
    }

    const EGLint attrib_config_list[] = {
            EGL_RED_SIZE, 8,
            EGL_GREEN_SIZE, 8,
            EGL_BLUE_SIZE, 8,
            EGL_ALPHA_SIZE, 8,
            EGL_DEPTH_SIZE, 8,
            EGL_STENCIL_SIZE, 8,// 眼睛屏幕的距离
            EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT,//版本号
            EGL_NONE
    };

    // 根据所需的参数获取符合该参数的config_size，主要是解决有些手机eglChooseConfig失败的兼容性问题
    EGLint num_config;
    if(!eglChooseConfig(display,attrib_config_list,NULL,1,&num_config)){
        LOGE("eglChooseConfig error");
        return -1;
    }

    //根据获取到的config_size得到eglConfig
    EGLConfig  eglConfig;
    if(!eglChooseConfig(display,attrib_config_list,&eglConfig,num_config,&num_config)){
        LOGE("eglChooseConfig error");
        return -1;
    }

    //4. 创建egl上下文 eglCreateContext
    const EGLint attrib_ctx_list[] = {
            EGL_CONTEXT_CLIENT_VERSION, 2,
            EGL_NONE
    };

    context = eglCreateContext(display,eglConfig,NULL,attrib_ctx_list);
    if(context == EGL_NO_CONTEXT){
        LOGE("elgCreateContext error");
        return -1;
    }

    surface = eglCreateWindowSurface(display,eglConfig,window,NULL);
    if(surface == EGL_NO_SURFACE){
        LOGE("eglCreateWindowSurface error");
        return -1;
    }

    //绑定eglContext和surface到display
    if(!eglMakeCurrent(display,surface,surface,context)){
        LOGE("eglMakeCurrent error");
        return -1;
    }

    return 0;


}

bool OpenGLThread::renderUpdate(int textId,float *mat){
    textureId = textId;
    if(MVPMat != NULL){
        delete(MVPMat);
    }
    MVPMat = mat;
    render = true;
    pthread_mutex_unlock(&lock);

    return true;
}

bool OpenGLThread::destoryOpenGLES() {
    pthread_mutex_unlock(&lock);
    threadStart = false;
    if (display != EGL_NO_DISPLAY) {
        //解绑display上的eglContext和surface
        eglMakeCurrent(display, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT);

        //销毁surface 和 eglContext
        if (surface != EGL_NO_SURFACE) {
            eglDestroySurface(display, surface);
            surface = EGL_NO_SURFACE;
        }

        if (context != EGL_NO_CONTEXT) {
            eglDestroyContext(display, context);
            context = EGL_NO_CONTEXT;
        }

        if (display != EGL_NO_DISPLAY) {
            eglTerminate(display);
            display = EGL_NO_DISPLAY;
        }

        delete(drawer);
        delete(MVPMat);
    }

    return true;
}
