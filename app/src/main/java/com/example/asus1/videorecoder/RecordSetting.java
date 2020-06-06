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

    public enum FileType{
        MP4,
        AVI,
        FLV,
        MKV
    }


    public Filter mFiler;
    public CameraType mCameraType;
    public CameraOrientation mCameraOri;
    public MuxerType mMuxerType;
    public FileType mFileType;
    public String mime_type;

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

    public RecordSetting setMime_type(String mime_type) {
        this.mime_type = mime_type;
        return this;
    }

    public RecordSetting setFileType(FileType fileType) {
        this.mFileType = fileType;
        return this;
    }
}
