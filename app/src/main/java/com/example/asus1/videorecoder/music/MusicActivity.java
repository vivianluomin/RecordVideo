package com.example.asus1.videorecoder.music;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.asus1.videorecoder.UI.BaseActivity;
import com.example.asus1.videorecoder.R;

import java.util.ArrayList;
import java.util.List;

public class MusicActivity extends BaseActivity implements Handler.Callback{

    private ImageView mBack;
    private RecyclerView mRecyclerView;
    private ArrayList<Music> mMusicLists = new ArrayList<>();
    private MusicAdapter mAdapter;
    private MusicRunnale mMusicRunnale;
    public static final int MSG_INIT = 100;
    public static final int MSG_UPDATE = 200;
    public static final int MSG_PLAY = 300;
    public static final int MSG_PAUSE = 400;
    public static final int MSG_CON_PLAY = 500;
    public static final int MSG_USE = 600;
    private Handler mMainHanlder;
    private static final String TAG = "MusicActivity";
    private MusicBinder mMusicBinder;
    private MusicHolder mRectMusicHolder;
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
        mAdapter = new MusicAdapter(mMusicLists,this,mMainHanlder);
        mRecyclerView.setAdapter(mAdapter);
        mMusicRunnale = new MusicRunnale(this);
        getData();
        Intent intent = new Intent(this,MusicService.class);
        bindService(intent,mMusicConnection,BIND_AUTO_CREATE);
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
                if(mRectMusicHolder != null){
                    mRectMusicHolder.setPlayBtn();
                }
                Bundle bundle = msg.getData();
                mRectMusicHolder = (MusicHolder) bundle.getSerializable("holder");
                String url = bundle.getString("url");
                int time = bundle.getInt("time",0);
                mMusicBinder.prepareMediaPlayer(url,time);
                break;

            case MSG_PAUSE:
                mMusicBinder.pauseMusic();
                break;
            case MSG_CON_PLAY:
                mMusicBinder.playMusic();
                break;

            case MSG_USE:
                Bundle useBunlde = msg.getData();
                String musicUrl = useBunlde.getString("url");
                int musicTime = useBunlde.getInt("time",0);
                String musicName = useBunlde.getString("name");
                Intent intent = new Intent();
                intent.putExtra("music",musicUrl);
                intent.putExtra("time",musicTime);
                intent.putExtra("name",musicName);
                setResult(RESULT_OK,intent);
                finish();

        }

        return true;
    }

    private ServiceConnection mMusicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected: ");
            mMusicBinder = (MusicBinder)service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected: ");

        }
    };


    @Override
    protected void onDestroy() {
        mMusicBinder.stopMusic();
        unbindService(mMusicConnection);
        stopService(new Intent(this,MusicService.class));
        super.onDestroy();
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
