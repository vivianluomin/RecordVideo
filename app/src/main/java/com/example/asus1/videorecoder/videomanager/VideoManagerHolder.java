package com.example.asus1.videorecoder.videomanager;

import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.asus1.videorecoder.R;

import java.util.ArrayList;
import java.util.List;

public class VideoManagerHolder extends RecyclerView.ViewHolder implements
        View.OnClickListener,View.OnLongClickListener{

    private ImageView mVideoOne;
    private ImageView mVideoTwo;
    private ImageView mVideoThree;
    private List<VideoModel> mDatas;
    private List<ImageView> mCovers = new ArrayList<>();
    private static final String TAG = "VideoManagerHolder";
    private Context mContext;
    private Handler mHandler;
    public VideoManagerHolder(@NonNull View itemView, Context context, Handler handler) {
        super(itemView);
        mContext = context;
        mHandler = handler;
        mVideoOne = itemView.findViewById(R.id.iv_video_1);
        mVideoOne.setOnClickListener(this);
        mVideoOne.setOnLongClickListener(this);
        mVideoTwo = itemView.findViewById(R.id.iv_video_2);
        mVideoTwo.setOnClickListener(this);
        mVideoTwo.setOnLongClickListener(this);
        mVideoThree = itemView.findViewById(R.id.iv_video_3);
        mVideoThree.setOnClickListener(this);
        mVideoThree.setOnLongClickListener(this);
        mCovers.add(mVideoOne);
        mCovers.add(mVideoTwo);
        mCovers.add(mVideoThree);
    }

    @Override
    public void onClick(View v) {
        String src = "";
        switch (v.getId()){
            case R.id.iv_video_1:
                Log.d(TAG, "onClick: 1");
                src = mDatas.get(0).getmSrc();
                break;
            case R.id.iv_video_2:
                Log.d(TAG, "onClick: 2");
                src = mDatas.get(1).getmSrc();
                break;
            case R.id.iv_video_3:
                Log.d(TAG, "onClick: 3");
                src = mDatas.get(2).getmSrc();
                break;
        }

        Intent intent = new Intent(mContext,PlayVideoActivity.class);
        intent.putExtra("src",src);
        mContext.startActivity(intent);
    }

    @Override
    public boolean onLongClick(View v) {
        VideoModel model = new VideoModel(null,null);
        switch (v.getId()){
            case R.id.iv_video_1:
                Log.d(TAG, "onClick: 1");
                model = mDatas.get(0);
                break;
            case R.id.iv_video_2:
                Log.d(TAG, "onClick: 2");
                model = mDatas.get(1);
                break;
            case R.id.iv_video_3:
                Log.d(TAG, "onClick: 3");
                model = mDatas.get(2);
                break;
        }

        Bundle bundle = new Bundle();
        bundle.putParcelable("video",model);
        Message message = new Message();
        message.what = VideoManagerActivity.MSG_DELETE;
        message.setData(bundle);
        mHandler.sendMessage(message);
        return true;
    }

    public void setData(List<VideoModel> data){
        mDatas = data;
        int i = 0;
        for(;i<mDatas.size();i++){
            ImageView view = mCovers.get(i);
            VideoModel model = mDatas.get(i);
            view.setImageBitmap(model.getBitmap());
        }

        for(;i<mCovers.size();i++){
            mCovers.get(i).setVisibility(View.INVISIBLE);
            mCovers.get(i).setClickable(false);
        }
    }
}
