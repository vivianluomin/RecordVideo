package com.example.asus1.videorecoder.Camera;

import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.asus1.videorecoder.OpenGL.OpenGLHelper;
import com.example.asus1.videorecoder.R;
import com.example.asus1.videorecoder.RecordSetting;

public class RecordActivity extends AppCompatActivity implements SurfaceHolder.Callback ,SurfaceTexture.OnFrameAvailableListener{

    private SurfaceView mSurfaceView;
    private RecordSetting mRecordSetting;
    private Camera1 mCamera;
    private int mTextId;
    private SurfaceTexture mSurfaceTexture;
    private OpenGLHelper mOpenGLHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        Intent intent = getIntent();
        mRecordSetting = (RecordSetting) intent.getSerializableExtra("setting");
        init();
        openCamera();
    }

    private void init(){
        mSurfaceView = findViewById(R.id.surface_view);
        mSurfaceView.getHolder().addCallback(this);
        mOpenGLHelper = new OpenGLHelper();

    }

    private void openCamera(){
        mCamera = new Camera1();
        mCamera.init(RecordSetting.CameraOrientation.front);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        initTextureId();
        mOpenGLHelper.initOpenGL(surfaceHolder.getSurface(), RecordSetting.Filter.normal);
        mSurfaceTexture = new SurfaceTexture(mTextId);
        mSurfaceTexture.setOnFrameAvailableListener(this);
        mCamera.startPreview(mSurfaceTexture);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    private void initTextureId(){

        int[] texutes = new int[1];
        GLES20.glGenTextures(1,texutes,0);
        mTextId = texutes[0];
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,mTextId);

        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);//设置MIN 采样方式
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);//设置MAG采样方式
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_CLAMP_TO_EDGE);//设置S轴拉伸方式
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_CLAMP_TO_EDGE);//设置T轴拉伸方式

    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        float[] mvp = new float[16];
        surfaceTexture.updateTexImage();
        surfaceTexture.getTransformMatrix(mvp);
        mOpenGLHelper.render(mTextId,mvp);
    }
}
