//
// Created by asus1 on 2020/2/9.
//

#ifndef VIDEORECODER_TEXTUREDRAWER_H
#define VIDEORECODER_TEXTUREDRAWER_H


#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>
#include <EGL/egl.h>
#include <EGL/eglext.h>
#include <string.h>
#include <string>
#include <android/log.h>

#define LOG_TAG "TextureDrawer"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

class TextureDrawer{
public:
    const  char* vertexShaderSource = "attribute vec4 aPosition;"
                                             "varying vec2 vTextureCoord;"
                                             "uniform mat4 uMvpMatrix;"
                                             "void main(){"
                                             "gl_Position = uMvpMatrix * aPosition;"
                                             "vTextureCoord = (aPosition * 0.5+0.5).xy;"
                                             "}";

    const  char* fragmentShaderSource = "#extension GL_OES_EGL_image_external : require\n"
                                               "precision highp float;"
                                               "varying vec2 vTextureCoord;"
                                               "uniform samplerExternalOES uSampler;"
                                               "void main(){"
                                               "gl_FragColor = texture2D(uSampler,vTextureCoord);"  //纹理采样
                                               "}";

    void initVertexData();
    void initPrograme();
    void draw(int textId,float * sTMatrix);
    void initFragmentData();


private: int maPositionHandle;
         int muMvpMatrixHandle;

         GLuint mVertexBuffer;
         GLuint mIndexbuffer;

         int mProgram;

         int mVCount = 4;
         int mIndexCount = 6;

         int mInputWidth;
         int mInputHeight;


         float *mMvpMatrix = new float[16];


    int loadShader(int shaderType, const char * shader);
    int checkGLError(std::string info);

};

#endif //VIDEORECODER_TEXTUREDRAWER_H

