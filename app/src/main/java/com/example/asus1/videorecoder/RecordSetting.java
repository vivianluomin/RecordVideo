package com.example.asus1.videorecoder;

import java.io.Serializable;

public class RecordSetting implements Serializable{

    public  enum Filter{
        normal,
        dark,
        fudiao,
        mohu,
        mopi
    }


    public  enum CameraType{
        Camera1,
        Camera2
    }


    public  enum CameraOrientation{
        front,
        back
    }

    public  enum MuxerType{
        GPU,
        CPU
    }


    public Filter mFiler;
    public CameraType mCameraType;
    public CameraOrientation mCameraOri;
    public MuxerType mMuxerType;

    public static RecordSetting BUILD(){
        return new RecordSetting();
    }

    public RecordSetting setFiler(Filter filer){
        mFiler = filer;
        return this;
    }


   public RecordSetting setCameraType(CameraType cameraType){
        mCameraType = cameraType;
        return this;
   }

   public RecordSetting setCameraOri(CameraOrientation cameraOri){
        mCameraOri = cameraOri;
        return this;
   }

   public RecordSetting setMuxerType(MuxerType muxerType){
        mMuxerType = muxerType;
        return this;
   }


}
