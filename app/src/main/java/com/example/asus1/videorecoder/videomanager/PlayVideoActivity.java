package com.example.asus1.videorecoder.videomanager;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.example.asus1.videorecoder.BaseActivity;
import com.example.asus1.videorecoder.BuildConfig;
import com.example.asus1.videorecoder.R;

import java.io.File;

public class PlayVideoActivity extends BaseActivity implements View.OnClickListener{

    private VideoView mVideoView;
    private ImageView mClose;
    private String mSrc;
    private ImageView mPause;
    private boolean mPlaying = true;
    private TextView mShare;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);
        Intent intent = getIntent();
        mSrc = intent.getStringExtra("src");
        init();
        if(mSrc!=null&&!mSrc.equals("")){
            setData();
        }



    }

    private void init(){
        mVideoView = (VideoView)findViewById(R.id.video_view);
        mVideoView.setOnClickListener(this);
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mPause.setVisibility(View.VISIBLE);
                mPlaying = false;
            }
        });

        mPause = (ImageView)findViewById(R.id.iv_pause);
        mPause.setVisibility(View.GONE);
        mClose = (ImageView)findViewById(R.id.iv_back);
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mShare = findViewById(R.id.tv_share);
        mShare.setOnClickListener(this);


    }

    private void setData(){
        mVideoView.setVideoPath(mSrc);
        mVideoView.start();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mVideoView.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.video_view:
                if(mPlaying){
                    mVideoView.pause();
                    mPause.setVisibility(View.VISIBLE);
                    mPlaying = false;
                }else {
                    mVideoView.start();
                    mPause.setVisibility(View.GONE);
                    mPlaying = true;
                }

                break;

            case R.id.tv_share:
                share();
                break;

        }
    }

    private void share(){
        //调用android分享窗口
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("*/*");
        intent.setPackage("com.android.bluetooth");
        Uri uri = FileProvider.getUriForFile(
                getApplicationContext(),
                "com.example.asus1.videorecoder",
                new File(mSrc));
        intent.putExtra(Intent.EXTRA_STREAM, uri);//path为文件的路径
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Intent chooser = Intent.createChooser(intent, "Share app");
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(chooser);

    }
}
