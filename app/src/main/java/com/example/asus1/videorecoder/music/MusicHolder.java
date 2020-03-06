package com.example.asus1.videorecoder.music;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.asus1.videorecoder.R;

import java.io.Serializable;


public class MusicHolder extends RecyclerView.ViewHolder implements View.OnClickListener,Serializable {

    private Context mContext;
    private ImageView mMusicCover;
    private ImageView mPlayBtn;
    private TextView mMusicName;
    private TextView mMusicAuthor;
    private TextView mUseText;
    private Handler mMusicHandler;
    private Music mData;

    private boolean mPlaying = false;


    public MusicHolder(@NonNull View itemView, Context context, Handler musicHandler) {
        super(itemView);
        mContext = context;
        mMusicHandler = musicHandler;
        init(itemView);
    }

    private void init(View itemView){
        mMusicCover = itemView.findViewById(R.id.iv_music_album);
        mPlayBtn = itemView.findViewById(R.id.iv_play);
        mPlayBtn.setOnClickListener(this);
        mMusicName = itemView.findViewById(R.id.tv_music_name);
        mMusicAuthor = itemView.findViewById(R.id.tv_music_author);
        mUseText = itemView.findViewById(R.id.tv_use);
        mUseText.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_play:
                if(mPlaying){
                    mMusicHandler.obtainMessage(MusicActivity.MSG_PAUSE).sendToTarget();
                    mPlaying = false;
                    mPlayBtn.setImageResource(R.mipmap.ic_play);
                }else {
                    Bundle bundle = new Bundle();
                    bundle.putString("url",mData.path);
                    bundle.putInt("time",mData.duration);
                    bundle.putSerializable("holder",MusicHolder.this);
                    Message message = new Message();
                    message.what = MusicActivity.MSG_PLAY;
                    message.setData(bundle);
                    mMusicHandler.sendMessage(message);
                    mPlaying = true;
                    mPlayBtn.setImageResource(R.mipmap.ic_pause);
                }
                break;

            case R.id.tv_use:
                Bundle bundle = new Bundle();
                bundle.putString("url",mData.path);
                bundle.putInt("time",mData.duration);
                bundle.putString("name",mData.name);
                Message message = new Message();
                message.what = MusicActivity.MSG_USE;
                message.setData(bundle);
                mMusicHandler.sendMessage(message);
                break;

        }

    }

    public void setData(Music music){
        mData = music;
        mMusicName.setText(music.name.trim().split(".mp")[0]);
        mMusicAuthor.setText(music.singer);
        String path = LocalMusicUtils.getAlbumArt(mContext,music.albumId);
        Glide.with(mContext)
                .load(path)
                .error(R.mipmap.bg_music)
                .into(mMusicCover);
    }

    public void setPlayBtn(){
        mPlayBtn.setImageResource(R.mipmap.ic_play);
        mPlaying = false;
    }
}
