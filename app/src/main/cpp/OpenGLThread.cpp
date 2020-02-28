//
// Created by asus1 on 2020/2/8.
//

#include "include/OpenGLThread.h"

JavaVM* OpenGLThread::JVMInstance = nullptr;

void*  start(void *gl) {

    OpenGLThread *glThread = static_cast<OpenGLThread *>(gl);

    bool ret = glThread->initOpenGlES(EGL_NO_CONTEXT);
    LOGE("ret = %d",ret);
    if(!ret){
        LOGE("initOpenGL failed");
        return NULL;
    }

    LOGE("init OpenGL Success");

    glThread->drawer = glThread->createTextureDrawer(glThread->filter);
    pthread_mutex_init(&glThread->lock,NULL);
    glThread->threadStart = true;
    JNIEnv * env;
    glThread->JVMInstance->AttachCurrentThread(&env,NULL);
    LOGE("get env ");
    env->CallVoidMethod(glThread->openglHepler,glThread->onOpenGLinitSucccess_method);
    LOGE("class method");
    glViewport(0,0,glThread->width,glThread->height);
    while (glThread->threadStart){
        if(!glThread->render){
            pthread_mutex_lock(&glThread->lock);
        }

        env->CallVoidMethod(glThread->openglHepler,glThread->onOpenGLRunning_method);

        glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
#ifdef __cplusplus
        glThread->drawer->draw(glThread->textureId,glThread->MVPMat);
#endif
        if(glThread->record){
            jfloatArray  mvp = (jfloatArray)(env->GetObjectField(glThread->openglHepler,glThread->mvp_filed));
            env->SetFloatArrayRegion(mvp,0,16,glThread->MVPMat);
            env->CallVoidMethod(glThread->openglHepler,glThread->onEncode_method,glThread->textureId);
        }

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
            drawer = new BlackTextrueDrawer();
            break;
        case FILTER_FUDIAO:
            drawer = new FudiaoTextrueDrawer();
            break;
        case FILTER_MOHU:
            drawer = new MohuTextrueDrawer();
            break;
        case FILTER_MOPI:
            drawer = new MopiTextureDrawer();
            break;
    }
    return drawer;
}


bool OpenGLThread::initOpenGlES(EGLContext shareContext) {
    display = eglGetDisplay(EGL_DEFAULT_DISPLAY);
    if(display == EGL_NO_DISPLAY){
        LOGE("eglGetDisplay error");
        return false;
    }

    LOGE("display create success");

    EGLint *version = new EGLint[2];
    if(!eglInitialize(display,&version[0],&version[1])){
        LOGE("eglInitialize error");
        return false;
    }

    LOGE("eglInit create success");


    //配置选项
    EGLint configAttribs[] = {
            EGL_BUFFER_SIZE,32,
            EGL_ALPHA_SIZE,8,
            EGL_BLUE_SIZE,8,
            EGL_GREEN_SIZE,8,
            EGL_RED_SIZE,8,
            EGL_RENDERABLE_TYPE,EGL_OPENGL_ES2_BIT,
            EGL_SURFACE_TYPE,EGL_WINDOW_BIT,
            EGL_NONE
    };

    // 根据所需的参数获取符合该参数的config_size，主要是解决有些手机eglChooseConfig失败的兼容性问题
    EGLint num_config;

    //根据获取到的config_size得到eglConfig
    EGLConfig  eglConfig;
    if(!eglChooseConfig(display,configAttribs,&eglConfig,1,&num_config)){
        LOGE("eglChooseConfig error");
        return -1;
    }

    LOGE("eglChooseConfig success ");

    //4. 创建egl上下文 eglCreateContext
    const EGLint attrib_ctx_list[] = {
            EGL_CONTEXT_CLIENT_VERSION, 2,
            EGL_NONE
    };

    if (shareContext == EGL_NO_CONTEXT){
        context = eglCreateContext(display,eglConfig,NULL,attrib_ctx_list);
    } else{
        context = eglCreateContext(display,eglConfig,shareContext,attrib_ctx_list);
    }
    if(context == EGL_NO_CONTEXT){
        LOGE("elgCreateContext error");
        return false;
    }

    LOGE("context create success ");

    surface = eglCreateWindowSurface(display,eglConfig,window,NULL);
    if(surface == EGL_NO_SURFACE){
        LOGE("eglCreateWindowSurface error");
        return false;
    }

    eglQuerySurface(display,surface,NULL,&width);
    eglQuerySurface(display,surface,NULL,&height);

    LOGE("window surface create success ");

    //绑定eglContext和surface到display
    if(eglMakeCurrent(display,surface,surface,context)== EGL_FALSE){
        LOGE("eglMakeCurrent error");
        return false;
    }

    LOGE("make current  success ");
    return true;
}

bool OpenGLThread::renderUpdate(int textId,float *mat){
    textureId = textId;
    for(int i = 0;i<16;i++){
        MVPMat[i] = mat[i];
    }
    render = true;
    pthread_mutex_unlock(&lock);

    return true;
}

void OpenGLThread::renderFrame(int textId, float *mat) {
    eglMakeCurrent(display,surface,surface,context);
    glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
    glClear(GL_COLOR_BUFFER_BIT);
    //glViewport(0,0,width,height);
    drawer->draw(textId,mat);
    eglSwapBuffers(display,surface);
}

void OpenGLThread::startRecord() {
    record = true;
    JNIEnv * env;
    JVMInstance->AttachCurrentThread(&env,NULL);
    OpenGLThread *openGLThread = new OpenGLThread();
    openGLThread->shareContext = context;
    env->CallVoidMethod(openglHepler,setShareEGLContext_method,(jlong)openGLThread);
}

void OpenGLThread::stopRecord() {
    record = false;
}

bool OpenGLThread::destoryOpenGLES() {
    threadStart = false;
    pthread_mutex_unlock(&lock);
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
    }

    return true;
}
