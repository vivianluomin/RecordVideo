package com.example.asus1.videorecoder.videomanager;

import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.example.asus1.videorecoder.R;

import java.util.ArrayList;
import java.util.List;

public class VideoManagerHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private ImageView mVideoOne;
    private ImageView mVideoTwo;
    private ImageView mVideoThree;
    private List<VideoModel> mDatas;
    private List<ImageView> mCovers = new ArrayList<>();

    public VideoManagerHolder(@NonNull View itemView) {
        super(itemView);
        mVideoOne = itemView.findViewById(R.id.iv_video_1);
        mVideoOne.setOnClickListener(this);
        mVideoTwo = itemView.findViewById(R.id.iv_video_2);
        mVideoTwo.setOnClickListener(this);
        mVideoThree = itemView.findViewById(R.id.iv_video_3);
        mVideoThree.setOnClickListener(this);
        mCovers.add(mVideoOne);
        mCovers.add(mVideoTwo);
        mCovers.add(mVideoThree);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_video_1:
                break;
            case R.id.iv_video_2:
                break;
            case R.id.iv_video_3:
                break;
        }
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
            mCovers.get(i).setVisibility(View.GONE);
        }
    }
}
