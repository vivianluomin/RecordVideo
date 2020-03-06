package com.example.asus1.videorecoder.videomanager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.asus1.videorecoder.R;

import java.util.ArrayList;
import java.util.List;

public class VideoManagerAdapter extends RecyclerView.Adapter<VideoManagerHolder> {

    private List<VideoModel> mModelList;
    private Context mContext;
    private int mHight = 0;

    public VideoManagerAdapter(Context context, List<VideoModel> models){
        mContext = context;
        mModelList = models;

    }


    @NonNull
    @Override
    public VideoManagerHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_video_manager_item,viewGroup,false);
        return new VideoManagerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoManagerHolder videoManagerHolder, int i) {
//        List<VideoModel> list = new ArrayList<>();
//        if(i<mHight-1){
//            for(int t = i*3;t<i*3+3;t++){
//                list.add(mModelList.get(t));
//            }
//        }else {
//            for(int t = i*3;t<mModelList.size();t++){
//                list.add(mModelList.get(t));
//            }
//        }
//
//        videoManagerHolder.setData(list);
    }

    @Override
    public int getItemCount() {
        mHight = mModelList.size()/3;
        return 13;
    }
}
