package com.example.asus1.videorecoder.videomanager;

import android.arch.lifecycle.ViewModel;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.example.asus1.videorecoder.BaseActivity;
import com.example.asus1.videorecoder.R;

import java.util.ArrayList;
import java.util.List;

public class VideoManagerActivity extends BaseActivity {

    private ImageView mBack;
    private RecyclerView mRecyclerView;
    private VideoManagerAdapter mAdapter;
    private List<VideoModel> mVideoModelLists = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_manager);
        init();
    }
    private void init(){
        mBack = findViewById(R.id.iv_back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mRecyclerView = findViewById(R.id.recycler_view);
        mAdapter = new VideoManagerAdapter(this,mVideoModelLists);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
    }
}
