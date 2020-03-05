package com.example.asus1.videorecoder.music;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.asus1.videorecoder.R;

public class MusicHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private Context mContext;
    private ImageView mMusicCover;
    private ImageView mPlayBtn;
    private TextView mMusicName;
    private TextView mMusicAuthor;
    private TextView mUseText;


    public MusicHolder(@NonNull View itemView, Context context) {
        super(itemView);
        mContext = context;
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

    }

    public void setData(Music music){
        mMusicName.setText(music.name.trim().split(".mp")[0]);
        mMusicAuthor.setText(music.singer);
        String path = LocalMusicUtils.getAlbumArt(mContext,music.albumId);
        Glide.with(mContext)
                .load(path)
                .error(R.mipmap.bg_music)
                .into(mMusicCover);
    }
}
