package com.example.asus1.videorecoder.videomanager;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.asus1.videorecoder.UI.BaseActivity;
import com.example.asus1.videorecoder.Encode.FFmpegMuxer;
import com.example.asus1.videorecoder.Encode.VideoMediaMuxer;
import com.example.asus1.videorecoder.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VideoManagerActivity extends BaseActivity implements Handler.Callback {

    private ImageView mBack;
    private RecyclerView mRecyclerView;
    private VideoManagerAdapter mAdapter;
    private List<VideoModel> mVideoModelLists = new ArrayList<>();
    private Handler mHandler;
    public static final int RESPONSE_DATA = 11;
    public static final int MSG_DELETE = 12;

    private static final String TAG = "VideoManagerActivity";

    private FFmpegMuxer fFmpegMuxer = new FFmpegMuxer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_manager);
        init();
    }
    private void init(){
        mHandler = new Handler(Looper.getMainLooper(),this);
        mBack = findViewById(R.id.iv_back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mRecyclerView = findViewById(R.id.recycler_view);
        mAdapter = new VideoManagerAdapter(this,mVideoModelLists,mHandler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
        getData();
    }

    private void getData(){
        new Thread(new ReadFileRunnable()).start();
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what){
            case RESPONSE_DATA:
                Log.d(TAG, "RESPONSE_DATA: ");
                mAdapter.notifyDataSetChanged();
                break;

            case MSG_DELETE:
                Bundle bundle = msg.getData();
                VideoModel model = bundle.getParcelable("video");
                if(model.getmSrc() != null){
                    mVideoModelLists.remove(model);
                    File file = new File(model.getmSrc());
                    file.delete();
                    mAdapter.notifyDataSetChanged();
                }
                break;
        }
        return true;
    }

    private class ReadFileRunnable implements Runnable{
        private static final String TAG = "ReadFileRunnable";
        @Override
        public void run() {
            File file = new File(Environment.
                    getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), VideoMediaMuxer.DIR_NAME);
            file.mkdirs();
            String[] files = file.list();
            Log.d(TAG, "run: "+files.length);
            ArrayList<VideoModel> models = new ArrayList<>();
            for(int i = 0;i<files.length;i++){
                String src = new File(file,files[i]).toString();
                if (!(src.endsWith("mp4")||src.endsWith(".avi")||src.endsWith(".flv")||src.endsWith(".mkv"))){
                    continue;
                }
                Log.d(TAG, "run: "+src);
                String time = files[i];
                String ss = src.substring(0,src.length()-4);
                Log.d(TAG, "run:ss-0  "+ss);
                String picPath = ss+".jpg";
                fFmpegMuxer.getFrame(src,picPath);
                models.add(new VideoModel(src,time,picPath));
            }

            mVideoModelLists.clear();
            mVideoModelLists.addAll(models);
            mHandler.obtainMessage(RESPONSE_DATA).sendToTarget();
        }
    }
}
