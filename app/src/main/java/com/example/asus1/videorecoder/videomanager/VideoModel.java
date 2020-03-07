package com.example.asus1.videorecoder.videomanager;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;

public class VideoModel implements Parcelable {

    private String mSrc;
    private String mTime;
    private Bitmap bitmap;

    public static Parcelable.Creator<VideoModel> CREATOR = new Parcelable.Creator<VideoModel>(){

        @Override
        public VideoModel createFromParcel(Parcel source) {
            return new VideoModel(source);
        }

        @Override
        public VideoModel[] newArray(int size) {
            return new VideoModel[0];
        }
    };

    private VideoModel(Parcel source){
        this.mSrc = source.readString();
        this.mTime = source.readString();


    }

    public VideoModel(String mSrc, String mTime) {
        this.mSrc = mSrc;
        this.mTime = mTime;
        if(mSrc != null){
            bitmap = getVideoThumb(mSrc);
        }
    }

    public  static Bitmap getVideoThumb(String path) {

        MediaMetadataRetriever media = new MediaMetadataRetriever();

        media.setDataSource(path);

        return  media.getFrameAtTime();

    }



    public String getmSrc() {
        return mSrc;
    }

    public void setmSrc(String mSrc) {
        this.mSrc = mSrc;
    }

    public String getmTime() {
        return mTime;
    }

    public void setmTime(String mTime) {
        this.mTime = mTime;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(mSrc);
        dest.writeString(mTime);
    }
}
