//
// Created by asus1 on 2020/2/9.
//

#include "TextureDrawer.h"


void TextureDrawer::initVertexData() {
     float vertexs[] = {
            -1,1,0,
            -1,-1,0,
            1,1,0,
            1,-1,0
    };

     GLuint indexs[] = {
            0,1,2,
            1,3,2
    };


    glGenBuffers(1, &mIndexbuffer);
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, mIndexbuffer);
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, sizeof(indexs), indexs, GL_STATIC_DRAW);


    glGenBuffers(1, &mVertexBuffer);
    glBindBuffer(GL_ARRAY_BUFFER, mVertexBuffer);
    glBufferData(GL_ARRAY_BUFFER, sizeof(vertexs), NULL, GL_STATIC_DRAW);
    glBufferSubData(GL_ARRAY_BUFFER, 0, sizeof(vertexs), vertexs);


}

void TextureDrawer::initPrograme() {

    int vertexShader = loadShader(GL_VERTEX_SHADER,vertexShaderSource);
    if(vertexShader == 0){
        mProgram = 0;
        return;
    }

    int fragmentShader = loadShader(GL_FRAGMENT_SHADER,fragmentShaderSource);

    if(fragmentShader == 0){
        mProgram = 0;
        return;
    }

    int program = glCreateProgram();
    int ret = 0;
    if(program!=0){
        glAttachShader(program,vertexShader);
        ret = checkGLError("glAttachShader");
        if(ret !=0 ){
            return;
        }
        glAttachShader(program,fragmentShader);
        ret = checkGLError("glAttachShader");
        if(ret != 0 ){
            return;
        }
        glLinkProgram(program);//链接程序

        int *linkStatus = new int[1];
        glGetProgramiv(program,GL_LINK_STATUS,linkStatus);
        if(linkStatus[0] != GL_TRUE){
            LOGE( "Could not link program" );
            glDeleteProgram(program);
            program = 0;
        }

    }

    mProgram = program;

}


int TextureDrawer::loadShader(int shaderType,const char* shaderSource) {
    int shader = glCreateShader(shaderType);
    GLint length,result;
    if(shader!=0){
        length = strlen(shaderSource);
        glShaderSource(shader,1,&shaderSource,&length);
        glCompileShader(shader);
        glGetShaderiv(shader,GL_COMPILE_STATUS,&result);
        if(result != GL_NO_ERROR){
            LOGE("Could not compile shader "+shaderType);
            char *log;
            glGetShaderiv(shader,GL_INFO_LOG_LENGTH,&length);
            log=(char *)malloc(length);
            glGetShaderInfoLog(shader,length,&result,log);
            LOGE("get error %s: \n",log);
            free(log);
            glDeleteShader(shader);
            shader = 0;
        }
    }

    return shader;
}

void TextureDrawer::initFragmentData() {

    maPositionHandle = glGetAttribLocation(mProgram,"aPosition");
    muMvpMatrixHandle = glGetUniformLocation(mProgram,"uMvpMatrix");
}

void TextureDrawer::draw(int textId, float *sTMatrix) {
    glUseProgram(mProgram);
    glVertexAttribPointer(maPositionHandle,3,GL_FLOAT,
            false,3*4,&mVertexBuffer);
    glEnableVertexAttribArray(maPositionHandle);

    if(sTMatrix!= NULL){
        glUniformMatrix4fv(muMvpMatrixHandle, 1,false,sTMatrix);
    }

    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_EXTERNAL_OES,textId);

    glDrawElements(GL_TRIANGLES,mIndexCount,
            GL_UNSIGNED_BYTE,&mIndexbuffer);
}

int TextureDrawer::checkGLError(std::string info) {
    int error;
    if((error = glGetError())!=GL_NO_ERROR){
        LOGE("glError,the info is %s,the error is %d", info,error);
        return -1;
    }

    return 0;

}