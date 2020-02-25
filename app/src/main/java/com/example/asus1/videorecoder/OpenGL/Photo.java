package com.example.asus1.videorecoder.OpenGL;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Photo {

    protected String mVertexShder =
            "attribute vec4 aPosition;" +
            "attribute vec4 aTexCoord;" +
            "varying vec2 vTextureCoord;" +
            "uniform mat4 uMvpMatrix;" +
            "void main(){" +
            "gl_Position = aPosition;" +
            "vTextureCoord = (aPosition * 0.5)+0.5;" +
            "}";

    private String mFragmentShader =
            "#extension GL_OES_EGL_image_external : require\n" +
            "precision highp float;" +
            "varying vec2 vTextureCoord;" +
            "uniform samplerExternalOES uSampler;" +
            "void main(){" +
            "gl_FragColor = texture2D(uSampler,vTextureCoord);" + //纹理采样
            "}";



    private int maPositionHandle;
    private int maTextureHandle;
    private int muMvpMatrixHandle;

    private FloatBuffer mVertexBuffer;
    private FloatBuffer mTextureBuffer;
    private ByteBuffer mIndexbuffer;

    protected int mProgram;

    private int mVCount = 4;
    private int mIndexCount = 6;

    protected int mInputWidth;
    protected int mInputHeight;


    private float[] mMvpMatrix = new float[16];
    private float[] mMMatrix = new float[16];

    public Photo(){
        initVertexData();
        initProgram();
    }

    public void setSize(int width,int height){
        mInputWidth = width;
        mInputHeight = height;
    }

    private void initVertexData(){

        float[] vertexs = new float[]{
                -1,1,0,
                -1,-1,0,
                1,1,0,
                1,-1,0
        };

        ByteBuffer vbb = ByteBuffer.allocateDirect(vertexs.length*4);
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asFloatBuffer();
        mVertexBuffer.put(vertexs);
        mVertexBuffer.position(0);

        float[] textures = new float[]{
                0,0,
                0,1,
                1,0,
                1,1
        };//纹理坐标

        ByteBuffer cbb = ByteBuffer.allocateDirect(textures.length*4);
        cbb.order(ByteOrder.nativeOrder());
        mTextureBuffer = cbb.asFloatBuffer();
        mTextureBuffer.put(textures);
        mTextureBuffer.position(0);

        byte[] indexs = new byte[]{
          0,1,2,
          1,3,2
        };

        mIndexbuffer = ByteBuffer.allocateDirect(indexs.length);
        mIndexbuffer.order(ByteOrder.nativeOrder());
        mIndexbuffer.put(indexs);
        mIndexbuffer.position(0);

    }
    protected void initProgram(){
        mProgram = ShaderUtil.loadProgram(mVertexShder, mFragmentShader);
        initFragmentData();
    }

    protected void initFragmentData(){

        maPositionHandle = GLES20.glGetAttribLocation(mProgram,"aPosition");
        maTextureHandle = GLES20.glGetAttribLocation(mProgram,"aTexCoord");
        muMvpMatrixHandle = GLES20.glGetUniformLocation(mProgram,"uMvpMatrix");

    }

    public void draw(int textId,float[] sTMatrix){
        GLES20.glUseProgram(mProgram);

        GLES20.glVertexAttribPointer(maPositionHandle,3,GLES20.GL_FLOAT,
                false,3*4,mVertexBuffer);
        GLES20.glVertexAttribPointer(maTextureHandle,2,GLES20.GL_FLOAT,
                false,2*4,mTextureBuffer);
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GLES20.glEnableVertexAttribArray(maTextureHandle);

        //GLES20.glUniformMatrix4fv(muMvpMatrixHandle,
              //  1,false,mMvpMatrix,0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,textId);

        GLES20.glDrawElements(GLES20.GL_TRIANGLES,mIndexCount,
                GLES20.GL_UNSIGNED_BYTE,mIndexbuffer);
    }

}
