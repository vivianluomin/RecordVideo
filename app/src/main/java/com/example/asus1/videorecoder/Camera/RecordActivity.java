package com.example.asus1.videorecoder.Camera;

import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.media.MediaRecorder;
import android.opengl.EGLContext;
import java.lang.String;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;

import com.example.asus1.videorecoder.Controller.RecordPersenter;
import com.example.asus1.videorecoder.Controller.ViewController;
import com.example.asus1.videorecoder.Encode.VideoRecordEncode;
import com.example.asus1.videorecoder.OpenGL.OpenGLHelper;
import com.example.asus1.videorecoder.R;
import com.example.asus1.videorecoder.RecordSetting;
import com.example.asus1.videorecoder.music.MusicActivity;

public class RecordActivity extends AppCompatActivity
        implements SurfaceHolder.Callback ,
        SurfaceTexture.OnFrameAvailableListener,
        OpenGLLifeListener,View.OnClickListener,ViewController{

    private SurfaceView mSurfaceView;
    private RecordSetting mRecordSetting;
    private BaseCamera mCamera;
    private int mTextId;
    private SurfaceTexture mSurfaceTexture;
    private OpenGLHelper mOpenGLHelper;
    private ImageView mChangeCamera;
    private RecordButtonView mRecordButtom;
    private ImageView mBack;
    private ImageView mMusic;
    private boolean mRecord = false;
    private RecordPersenter mPresenter;
    private VideoRecordEncode mVideoEncode;
    private static int MUSIC_RESULT = 10;
    private String mMusic_Url = "";
    private int mMusicTime = 0;
    private MusicPlayerThread mMusicThread;

    private static final String TAG = "RecordActivity";

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
        mPresenter = RecordPersenter.getPresenterInstantce();
        mPresenter.setViewController(this);
        mSurfaceView = findViewById(R.id.surface_view);
        mChangeCamera = findViewById(R.id.iv_change_camera);
        mChangeCamera.setOnClickListener(this);
        mRecordButtom = findViewById(R.id.view_record);
        mRecordButtom.setOnClickListener(this);
        mBack = findViewById(R.id.iv_back);
        mBack.setOnClickListener(this);
        mMusic = findViewById(R.id.iv_music);
        mMusic.setOnClickListener(this);
        mSurfaceView.getHolder().addCallback(this);
        mOpenGLHelper = new OpenGLHelper(this);
        mMusicThread = new MusicPlayerThread();
        mMusicThread.start();
        mMusicThread.setLinstener(playLinstener);
    }

    private void openCamera(){
        if(mRecordSetting.mCameraType == RecordSetting.CameraType.Camera1){
            mCamera = new Camera1(this);
        }else {
            mCamera = new Camera2(this);
        }
        mCamera.init(mRecordSetting.mCameraOri);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        mOpenGLHelper.initOpenGL(surfaceHolder.getSurface(),
                mRecordSetting.mFiler,i1,i2);

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
    public void onOpenGLinitSuccess() {
        initTextureId();
        mSurfaceTexture = new SurfaceTexture(mTextId);
        mSurfaceTexture.setOnFrameAvailableListener(this);
        mCamera.startPreview(mSurfaceTexture);
    }


    @Override
    public void onOpenGLRunning() {
        mSurfaceTexture.updateTexImage();
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        float[] mvp = new float[16];
        surfaceTexture.getTransformMatrix(mvp);
        mOpenGLHelper.render(mTextId,mvp);
    }

    @Override
    public void onEncode(int textId, float[] mvp) {
        mVideoEncode.onFrameAvaliable(mTextId,mvp);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_change_camera:
                RecordSetting.CameraOrientation po
                        = mRecordSetting.mCameraOri == RecordSetting.CameraOrientation.back?
                        RecordSetting.CameraOrientation.front:RecordSetting.CameraOrientation.back;
                mRecordSetting.mCameraOri = po;
                mCamera.changeCamera(po);
                break;
            case R.id.view_record:
                setRecord();
                break;

            case R.id.iv_back:
                finish();
                break;

            case R.id.iv_music:
                startActivityForResult(new Intent(RecordActivity.this
                        ,MusicActivity.class),MUSIC_RESULT);
                break;

        }
    }


    private void setRecord(){
        Log.d(TAG, "setRecord: ");
        if(mRecord){
            mRecord = false;
            mRecordButtom.setClick(false);
            mRecordButtom.postInvalidate();
            stopRecording();
        }else {
            mRecord = true;
            mRecordButtom.setClick(true);
            startRecording();
        }
    }

    private void startRecordUI(){
        Log.d(TAG, "startRecordUI: ");
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mRecord){
                    RecordActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mRecordButtom.postInvalidate();

                        }
                    });

                    try {
                        Thread.sleep(100);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void startRecording() {
        mPresenter.startRecoding(mRecordSetting);
        startRecordUI();
        if(!mMusic_Url.equals("")){
            mMusicThread.play();
        }
    }

    @Override
    public void stopRecording() {
        mPresenter.stopRecoding();
        mOpenGLHelper.stopRecord();
        if(!mMusic_Url.equals("")){
            mMusicThread.stopMedia();
        }
    }

    @Override
    public void setVideoEncode(VideoRecordEncode encode) {
        mVideoEncode = encode;
        mOpenGLHelper.startRecord();
    }

    @Override
    public void setShareEGLContext(long openglThread) {
        mVideoEncode.setEGLContext(openglThread,mTextId);
    }

    private MusicPlayerThread.MusicPlayLinstener playLinstener = new MusicPlayerThread.MusicPlayLinstener() {
        @Override
        public void compelte() {

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == MUSIC_RESULT && resultCode == RESULT_OK){
            mMusic_Url = data.getStringExtra("music");
            mMusicTime = data.getIntExtra("time",0);
            mMusicThread.setSrouce(mMusic_Url,mMusicTime/2);
        }
    }

    @Override
    protected void onDestroy() {
        mCamera.stopPreview();
        mCamera.release();
        mOpenGLHelper.deatoryOpenGL();
        super.onDestroy();
    }
}
