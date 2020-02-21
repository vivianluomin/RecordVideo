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
#include "LOG.h"
#include <malloc.h>

#ifdef __cplusplus
//extern "C" {
//#endif


class TextureDrawer{


public:
    TextureDrawer();
    void initVertexData();
    void initPrograme(const char *vexterSource, const char *fragmentSource);
    void draw(int textId,float * sTMatrix);
    virtual void initFragmentData();


protected: GLint maPositionHandle;
    GLint muMvpMatrixHandle;

    GLuint mVertexBuffer;
    GLuint mIndexbuffer;

    int mProgram;

    int mVCount = 4;
    int mIndexCount = 6;

    int mInputWidth;
    int mInputHeight;


    float *mMvpMatrix = new float[16];


protected:
    int loadShader(int shaderType, const char * shader);
    int checkGLError(std::string info);

};

class BlackTextrueDrawer:public TextureDrawer{
public:
    BlackTextrueDrawer();
};


class FudiaoTextrueDrawer:public TextureDrawer{
public:
    FudiaoTextrueDrawer();
};


class MohuTextrueDrawer:public TextureDrawer{
public:
    MohuTextrueDrawer();
};

class MopiTextureDrawer:public TextureDrawer{
public:
    MopiTextureDrawer();
};

//#ifdef __cplusplus
//}
#endif




#endif //VIDEORECODER_TEXTUREDRAWER_H

