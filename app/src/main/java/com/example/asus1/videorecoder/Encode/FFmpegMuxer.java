package com.example.asus1.videorecoder.Encode;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaCodec;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;

import com.example.asus1.videorecoder.RecordSetting;
import com.example.asus1.videorecoder.Utils;

import java.io.File;
import java.nio.ByteBuffer;

public class FFmpegMuxer {

    static {

        System.loadLibrary("FFmpegMuxer");
    }

    private long mHandler;
    private String mPath;
    private RecordSetting mRecordingSetting;

    private static final String TAG = "FFmpegMuxer";

    public FFmpegMuxer(RecordSetting recordSetting){
        mRecordingSetting = recordSetting;
        init();
    }

    public FFmpegMuxer(){
        init();
    }

    private void init(){
        String ext = ".mp4";
        if(mRecordingSetting != null){
            switch (mRecordingSetting.mFileType){
                case FLV:
                    ext = ".flv";
                    break;
                case MKV:
                    ext = ".mkv";
                    break;
                case AVI:
                    ext = ".avi";
                    break;
            }
        }
        Log.d(TAG, "init: "+ext);
        mPath  = VideoMediaMuxer.getCaptureFile(Environment.DIRECTORY_MOVIES,ext).toString();
        Log.d(TAG, "init: "+mPath);
        mHandler = native_init(mPath,ext.substring(1));
    }

    public void write(int mediaTrack, ByteBuffer data, MediaCodec.BufferInfo info){
        writeData(mHandler,mediaTrack,data,info);
    }

    public void stop(){

        native_stop(mHandler);
    }

    public void writeData(int mediaTrack, ByteBuffer data,long pts,int size,int flag){
        native_writeData(mHandler,mediaTrack,data,pts,size,flag);
    }

    public void initFFmpeg(){
        native_initFFmpeg(mHandler);
    }

    public void initEGL(Surface surface, long openglThread, RecordSetting.Filter filter){
        int fi = 0;
        if(filter == RecordSetting.Filter.normal){
            fi = 0;
        }else if(filter == RecordSetting.Filter.dark){
            fi = 1;
        }else if(filter == RecordSetting.Filter.fudiao){
            fi = 2;
        }else if(filter == RecordSetting.Filter.mohu){
            fi = 3;
        }else if(filter == RecordSetting.Filter.mopi){
            fi = 4;
        }
        native_initEGL(mHandler,surface,openglThread,fi);
    }

    public void getFrame(String path,String picPath){
        File file = new File(picPath);
        if(!file.exists()){
            new File(picPath);
            native_getFrame(mHandler,path,picPath);
        }
    }

    public void render(int textId,float[] mvp){
        native_render(mHandler,textId,mvp);
    }

    private native long native_init(String path,String mime);

    private native void native_initFFmpeg(long handler);

    private native void native_initEGL(long handler,Surface surface,long openglThread,int filter);

    private native void native_render(long handler,int textId,float[] mvp);

    private native void writeData(long handler,int mediaTrack, ByteBuffer data, MediaCodec.BufferInfo info);

    private native void native_stop(long handler);

    private native void native_writeData(long handler,int mediaTrack, ByteBuffer data, long pts,int size,int flag);

    private native void native_getFrame(long handler,String path,String picPath);
}
