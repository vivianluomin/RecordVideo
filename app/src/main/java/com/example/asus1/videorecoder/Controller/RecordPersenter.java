package com.example.asus1.videorecoder.Controller;

import android.media.MediaRecorder;
import android.opengl.EGLContext;

import com.example.asus1.videorecoder.Encode.VideoMediaMuxer;
import com.example.asus1.videorecoder.Encode.VideoRecordEncode;


public class RecordPersenter {
    private ViewController mViewController;
    private ModelController mModelController;
    private static RecordPersenter mPersenter = new RecordPersenter();

    private RecordPersenter(){

    }

    public static RecordPersenter getPresenterInstantce(){
        return mPersenter;
    }

    public void setViewController(ViewController viewController){
        mViewController = viewController;
    }

    public void setModeController(ModelController modeController){
        mModelController = modeController;
    }

    public void startRecoding() {
        mModelController.startRecording();

    }

//    public MediaRecorder.VideoEncoder startRecoding(EGLContext context, int textId){
//        VideoMediaMuxer muxer = new VideoMediaMuxer(context,textId);
////        mModelController.startRecording(0);
////        return muxer.mVideoEncoder;
//    }

    public void stopRecoding(){
        if(mModelController!=null)
        mModelController.stopRecording();
    }

    public String getVideoPath(){
        return mModelController.getVideoPath();
    }

    public void setVideoEncode(VideoRecordEncode encode){
        mViewController.setVideoEncode(encode);
    }
}
