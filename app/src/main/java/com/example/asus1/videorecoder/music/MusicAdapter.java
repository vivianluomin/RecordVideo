package com.example.asus1.videorecoder.music;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.asus1.videorecoder.R;

import java.util.ArrayList;
import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<MusicHolder> {

    private List<Music> mMusicLists;
    private Context mContext;
    private Handler mMusicHandler;

    public MusicAdapter(List<Music> list, Context context, Handler musicHandler){
        mMusicLists = list;
        mContext = context;
        mMusicHandler = musicHandler;
    }

    @NonNull
    @Override
    public MusicHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_music_item,viewGroup,false);
        return new MusicHolder(view,mContext,mMusicHandler);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicHolder musicHolder, int i) {
        musicHolder.setData(mMusicLists.get(i));
    }

    @Override
    public int getItemCount() {
        return mMusicLists.size();
    }
}
