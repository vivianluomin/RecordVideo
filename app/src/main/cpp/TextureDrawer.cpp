//
// Created by asus1 on 2020/2/9.
//


#include "include/TextureDrawer.h"

const static char vertexShaderSource[] =
        "attribute vec4 aPosition;"
                "varying vec2 vTextureCoord;"
                "uniform mat4 uMvpMatrix;"
                "void main(){"
                "vec4 po = aPosition * uMvpMatrix;"
                "gl_Position =  vec4(po.x,po.y,0,1);"
                "vTextureCoord = (aPosition.xy)*0.5+0.5;"
                "}";

const static char fragmentShaderSource[] =
        "#extension GL_OES_EGL_image_external : require\n"
                "precision highp float;"
                "varying vec2 vTextureCoord;"
                "uniform samplerExternalOES uSampler;"
                "void main(){"
                "gl_FragColor = texture2D(uSampler,vTextureCoord);"  //纹理采样
                "}";



const static  char blackFragmentShaderSource[] =
        "#extension GL_OES_EGL_image_external : require\n"
        "precision mediump float;"
        "varying vec2 vTextureCoord;"
        "uniform samplerExternalOES sTexture;"
        "void main(){"
        "vec4 vTextureColor = texture2D(sTexture,vTextureCoord);"
        "float vFilterColor = (0.3 * vTextureColor.r + 0.59 * vTextureColor.g + 0.11*vTextureColor.b);"
        "gl_FragColor = vec4(vFilterColor, vFilterColor, vFilterColor, 1.0);"
        "}";


const static char fudiaoFragmentShaderSource[] =
        "#extension GL_OES_EGL_image_external : require\n"
        "precision mediump float;"
        "varying vec2 vTextureCoord;"
        "uniform samplerExternalOES sTexture;"
        "const highp vec3 W = vec3(0.2125,0.7154,0.0721);"
        "const vec2 TexSize = vec2(100.0,100.0);"
        "const vec4 bkColor = vec4(0.5,0.5,0.5,1.0);"
        "void main(){"
        "vec2 tex = vTextureCoord;"
        "vec2 upleftUV = vec2(tex.x-1.0/TexSize.x,tex.y-1.0/TexSize.y);"
        "vec4 curColor = texture2D(sTexture,vTextureCoord);"
        "vec4 upleftColor = texture2D(sTexture,upleftUV);"
        "vec4 delColor = curColor - upleftColor;"
        "gl_FragColor = vec4(vec3(dot(delColor.rgb,W)),0.0)+bkColor;"
        "}";


const static char mohuFragmentShader[] =
        "#extension GL_OES_EGL_image_external : require\n"
        "precision mediump float;"
        "varying vec2 vTextureCoord;"
        "uniform samplerExternalOES sTexture;"
        "void main(){"
        "float weight[3];"
        "weight[0] = float(0.4026);"
        "weight[1] = float(0.2442);"
        "weight[2] = float(0.0545);"
        "float offset = 0.01;"
        "vec2 uv[5],uh[5];"
        "uv[0] = vTextureCoord;"
        "uv[1] = vTextureCoord+vec2(0.0,float(-1.0*offset));"
        "uv[2] = vTextureCoord+vec2(0.0,float(-2.0*offset));"
        "uv[3] = vTextureCoord+vec2(0.0,float(1.0*offset));"
        "uv[4] = vTextureCoord+vec2(0.0,float(2.0*offset));"
        "vec3 colorv = texture2D(sTexture,vTextureCoord).rgb*weight[0];"
        "for(int i = 1;i<3;i++){"
            "colorv+=texture2D(sTexture,uv[i]).rgb*weight[i];"
            "colorv+=texture2D(sTexture,uv[5-i]).rgb*weight[i];"
        "}"
        "uh[0] = vTextureCoord;"
        "uh[1] = vTextureCoord+vec2(float(-1.0*offset),0.0);"
        "uh[2] = vTextureCoord+vec2(float(-2.0*offset),0.0);"
        "uh[3] = vTextureCoord+vec2(float(1.0*offset),0.0);"
        "uh[4] = vTextureCoord+vec2(float(2.0*offset),0.0);"
        "vec3 colorh = texture2D(sTexture,vTextureCoord).rgb*weight[0];"
        "for(int j = 1;j<3;j++){"
            "colorh+=texture2D(sTexture,uh[j]).rgb*weight[j];"
            "colorh+=texture2D(sTexture,uh[5-j]).rgb*weight[j];"
        "}"
        "vec3 color = colorh*colorv;"
        "gl_FragColor = vec4(color,1.0);"
        "}";



const static char mopiFragmentShader[] =
        "#extension GL_OES_EGL_image_external : require\n"
        "precision mediump float;"
        "varying vec2 vTextureCoord;"
        "uniform samplerExternalOES sTexture;"
        "void main(){"
        "vec3 gray = vec3(0.5,0.5,0.5);"
        "float weight[3];"
        "weight[0] = float(0.4026);"
        "weight[1] = float(0.2442);"
        "weight[2] = float(0.0545);"
        "float offset = 1.0/150.0;"
        "vec2 uv[5],uh[5];"
        "vec3 centerColor = texture2D(sTexture,vTextureCoord).rgb;"
        "uv[0] = vTextureCoord;"
        "uv[1] = vTextureCoord+vec2(0.0,float(-1.0*offset));"
        "uv[2] = vTextureCoord+vec2(0.0,float(-2.0*offset));"
        "uv[3] = vTextureCoord+vec2(0.0,float(1.0*offset));"
        "uv[4] = vTextureCoord+vec2(0.0,float(2.0*offset));"
        "vec3 colorv = texture2D(sTexture,vTextureCoord).rgb*weight[0];"
        "for(int i = 1;i<3;i++){"
            "colorv+=texture2D(sTexture,uv[i]).rgb*weight[i];"
            "colorv+=texture2D(sTexture,uv[5-i]).rgb*weight[i];"
        "}"
        "uh[0] = vTextureCoord;"
        "uh[1] = vTextureCoord+vec2(float(-1.0*offset),0.0);"
        "uh[2] = vTextureCoord+vec2(float(-2.0*offset),0.0);"
        "uh[3] = vTextureCoord+vec2(float(1.0*offset),0.0);"
        "uh[4] = vTextureCoord+vec2(float(2.0*offset),0.0);"
        "vec3 colorh = texture2D(sTexture,vTextureCoord).rgb*weight[0];"
        "for(int j = 1;j<3;j++){"
            "colorh+=texture2D(sTexture,uh[j]).rgb*weight[j];"
            "colorh+=texture2D(sTexture,uh[5-j]).rgb*weight[j];"
        "}"
        "vec3 color = colorh*colorv;"
        "vec3 high_pass = centerColor-color+gray;"
        "vec3 cha = vec3(1.0,1.0,1.0);"
        "vec3 finalColor = (centerColor+high_pass*2.0-cha);"
        "finalColor.r = clamp(pow(finalColor.r,0.5),0.0,1.0);"
        "finalColor.g = clamp(pow(finalColor.g,0.7),0.0,1.0);"
        "finalColor.b = clamp(pow(finalColor.b,0.5),0.0,1.0);"
        "vec3 rouguang = 2.0*centerColor*finalColor+centerColor*centerColor-2.0*centerColor*centerColor*finalColor;"
        "gl_FragColor = vec4( finalColor,0.5);"
        "vec3 cc = gl_FragColor.rgb*0.2 +rouguang*0.8;"
        "gl_FragColor.rgb = cc;"
        "}";

TextureDrawer::TextureDrawer() {
    initPrograme(vertexShaderSource,fragmentShaderSource);
    initFragmentData();
    initVertexData();
}


void TextureDrawer::initVertexData() {
     float vertexs[] = {
            -1,-1,0,1,
            1,-1,0,1,
            -1,1,0,1,
            1,1,0,1
    };

     GLuint indexs[] = {
            0,1,2,
            1,3,2
    };


    glGenBuffers(1, &mVertexBuffer);
    glBindBuffer(GL_ARRAY_BUFFER, mVertexBuffer);
    glBufferData(GL_ARRAY_BUFFER, sizeof(vertexs),&vertexs[0], GL_STATIC_DRAW);

}

void TextureDrawer::initPrograme(const char* vexterSource, const char *fragmentSource) {

    int vertexShader = loadShader(GL_VERTEX_SHADER,vexterSource);
    if(vertexShader == 0){
        mProgram = 0;
        return;
    }

    int fragmentShader = loadShader(GL_FRAGMENT_SHADER,fragmentSource);

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
    GLuint shader = glCreateShader(shaderType);
    GLint length,result = GL_FALSE;
    if(shader!=0){
        length = strlen(shaderSource);
        glShaderSource(shader,1,&shaderSource,NULL);
        glCompileShader(shader);
        glGetShaderiv(shader,GL_COMPILE_STATUS,&result);
        if(!result){
            LOGE("Could not compile shader %x",shaderType);
            GLchar *log;
            glGetShaderiv(shader,GL_INFO_LOG_LENGTH,&length);
            log=(GLchar *)malloc(length);
            glGetShaderInfoLog(shader,length,NULL,log);
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
    LOGE("maPosition: %x",maPositionHandle);
    muMvpMatrixHandle = glGetUniformLocation(mProgram,"uMvpMatrix");
    LOGE("muMvpMatrixHandle: %x",muMvpMatrixHandle);
}

void TextureDrawer::draw(int textId, float *sTMatrix) {
    glUseProgram(mProgram);
    glBindBuffer(GL_ARRAY_BUFFER,mVertexBuffer);
    glVertexAttribPointer(maPositionHandle,4,GL_FLOAT,
            false,4*4,0);
    glEnableVertexAttribArray(maPositionHandle);

    if(sTMatrix!= NULL){
        glUniformMatrix4fv(muMvpMatrixHandle, 1,false,sTMatrix);
    }

    glActiveTexture(GL_TEXTURE0);
    glBindTexture(GL_TEXTURE_EXTERNAL_OES,textId);

    glDrawArrays(GL_TRIANGLE_STRIP,0,4);
}

int TextureDrawer::checkGLError(std::string info) {
    int error;
    if((error = glGetError())!=GL_NO_ERROR){
        LOGE("glError,the info is %s,the error is %d", info.c_str(),error);
        return -1;
    }

    return 0;

}

BlackTextrueDrawer ::BlackTextrueDrawer() {
    initPrograme(vertexShaderSource,blackFragmentShaderSource);
    initFragmentData();
    initVertexData();
}

FudiaoTextrueDrawer::FudiaoTextrueDrawer() {
    initPrograme(vertexShaderSource,fudiaoFragmentShaderSource);
    initFragmentData();
    initVertexData();
}

MohuTextrueDrawer::MohuTextrueDrawer() {
    initPrograme(vertexShaderSource,mohuFragmentShader);
    initFragmentData();
    initVertexData();
}

MopiTextureDrawer::MopiTextureDrawer() {
    initPrograme(vertexShaderSource,mopiFragmentShader);
    initFragmentData();
    initVertexData();
}

