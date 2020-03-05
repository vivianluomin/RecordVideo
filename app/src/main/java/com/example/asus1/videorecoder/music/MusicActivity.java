package com.example.asus1.videorecoder.music;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.asus1.videorecoder.BaseActivity;
import com.example.asus1.videorecoder.R;

import java.util.ArrayList;
import java.util.List;

public class MusicActivity extends BaseActivity implements Handler.Callback{

    private ImageView mBack;
    private RecyclerView mRecyclerView;
    private ArrayList<Music> mMusicLists = new ArrayList<>();
    private MusicAdapter mAdapter;
    private MusicRunnale mMusicRunnale;
    private final int MSG_INIT = 100;
    private final int MSG_UPDATE = 200;
    private final int MSG_PLAY = 300;
    private Handler mMainHanlder;
    private static final String TAG = "MusicActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        init();
    }

    private void init(){
        mMainHanlder = new Handler(Looper.getMainLooper(),this);
        mBack = findViewById(R.id.iv_back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new MusicAdapter(mMusicLists,this);
        mRecyclerView.setAdapter(mAdapter);
        mMusicRunnale = new MusicRunnale(this);
        getData();
    }

    private void getData(){
        new Thread(mMusicRunnale).start();
    }

    @Override
    public boolean handleMessage(Message msg) {
        Log.d(TAG, "handleMessage: ");

        switch (msg.what){
            case MSG_INIT:
                mAdapter.notifyDataSetChanged();
                break;
            case MSG_PLAY:
                Bundle bundle = msg.getData();
                String ulr = bundle.getString("src");
                String time = bundle.getString("time");
                break;

        }

        return true;
    }

    private class MusicRunnale implements Runnable{

        private Context mContext;

        public MusicRunnale(Context context){
            mContext = context;
        }
        @Override
        public void run() {
            List<Music> musicArrayList = LocalMusicUtils.getmusic(mContext);
            mMusicLists.clear();
            mMusicLists.addAll(musicArrayList);
            mMainHanlder.obtainMessage(MSG_INIT).sendToTarget();
        }
    }
}
