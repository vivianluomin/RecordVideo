package com.example.asus1.videorecoder.Encode;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.opengl.EGLContext;
import android.util.Log;
import android.view.Surface;
import com.example.asus1.videorecoder.OpenGL.Photo;
import com.example.asus1.videorecoder.OpenGL.RenderHandler;

import java.io.IOException;
import java.nio.ByteBuffer;


public class VideoRecordEncode implements Runnable {

    private MediaCodec mViedeoEncode;
    public static final  String MIME_TYPE = "video/avc";
    public static final int FRAME_RATE = 25; //帧率
    public static final float BPP = 0.25f; //位深度
    private Surface mSurface;

    private static final String TAG = "VideoRecordEncode";

    public boolean mLocalRquestStop = false;
    public boolean mIsRunning = true;
    public boolean mIsCaturing = false;
    private int mRequestDrain = 0;
    private Object mSync = new Object();

    private int mWidth;
    private int mHeight;
    private int mTexId;

    private MediaCodec.BufferInfo mBfferInfo;
    private boolean mEnOS = false;

    private int mTrackIndex;
    private RenderHandler mHandler;
    private VideoMediaMuxer mMuxer;

    private boolean mMuxerStart = false;

    private onFramPrepareLisnter mPrepareLisnter;

    public VideoRecordEncode(VideoMediaMuxer muxer, onFramPrepareLisnter prepareLisnter, int width, int height) {
        mWidth = width;
        mHeight = height;
        mPrepareLisnter = prepareLisnter;
        mMuxer = muxer;
        mBfferInfo = new MediaCodec.BufferInfo();
        mHandler = RenderHandler.createRenderHandler(mMuxer.mFFmepgMuxer,mMuxer.mRecordSetting.mFiler);
        synchronized (mSync){
            new Thread(this).start();
            try {
                mSync.wait();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    public void prepare(){

        Log.d(TAG, "prepare: "+Thread.currentThread().getName());
        try {
            mEnOS = false;
            mViedeoEncode = MediaCodec.createEncoderByType(MIME_TYPE);
            MediaFormat format =  MediaFormat.createVideoFormat(MIME_TYPE,mHeight,mWidth);
            format.setInteger(MediaFormat.KEY_BIT_RATE,calcBitRate());//码率
            format.setInteger(MediaFormat.KEY_FRAME_RATE,FRAME_RATE);//帧率
            format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL,10);
            format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                    MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
            MediaCodecInfo.CodecProfileLevel dstProfileLevel = null;
            MediaCodecInfo.CodecCapabilities codecCapabilities = mViedeoEncode.getCodecInfo().getCapabilitiesForType(MIME_TYPE);
            for(MediaCodecInfo.CodecProfileLevel profileLevel: codecCapabilities.profileLevels){
                if(profileLevel.profile == MediaCodecInfo.CodecProfileLevel.AVCProfileBaseline){
                    dstProfileLevel = profileLevel;
                }else if(profileLevel.profile == MediaCodecInfo.CodecProfileLevel.AVCProfileMain){
                    if (dstProfileLevel.profile < MediaCodecInfo.CodecProfileLevel.AVCProfileMain) {
                        dstProfileLevel = profileLevel;
                    }
                }else if (profileLevel.profile == MediaCodecInfo.CodecProfileLevel.AVCProfileHigh) {
                    if (dstProfileLevel.profile < MediaCodecInfo.CodecProfileLevel.AVCProfileHigh) {
                        dstProfileLevel = profileLevel;
                    }
                }
                if(dstProfileLevel.profile == MediaCodecInfo.CodecProfileLevel.AVCProfileBaseline){
                    break;
                }
            }

            Log.d(TAG, "prepare: "+dstProfileLevel.profile+"----"+dstProfileLevel.level);
            format.setInteger(MediaFormat.KEY_PROFILE,dstProfileLevel.profile);
            format.setInteger(MediaFormat.KEY_LEVEL,dstProfileLevel.level);
            mViedeoEncode.configure(format,null,null,MediaCodec.CONFIGURE_FLAG_ENCODE);

            //得到Surface用于编码
            mSurface = mViedeoEncode.createInputSurface();
            new Thread(mHandler).start();
            mViedeoEncode.start();
            mPrepareLisnter.onPrepare(this);

        }catch (IOException e){
            e.printStackTrace();
        }

    }

    public void startRecord(){
        mIsRunning = true;
        mIsCaturing = true;
        //mSync.notifyAll();

    }

    private int calcBitRate() {
        final int bitrate = (int)(BPP * FRAME_RATE * mWidth * mHeight);
        Log.i(TAG, String.format("bitrate=%5.2f[Mbps]", bitrate / 1024f / 1024f));
        return bitrate;
    }

    public boolean onFrameAvaliable(int textId,float[] stMatrix){
        Log.d(TAG, "onFrameAvaliable: ");

        synchronized (mSync){
            if(!mIsCaturing||mLocalRquestStop){
                return false;
            }
            mRequestDrain++;
            mSync.notifyAll();
        }
        mTexId = textId;
        mHandler.draw(mTexId,stMatrix);
        return true;
    }

    public void setEGLContext(long openglThread,int texId){
        //mShare_Context = context;
        mTexId = texId;
        mHandler.setEGLContext(openglThread,mSurface,mTexId);
        //mMuxer.mFFmepgMuxer.initEGL(mSurface,openglThread);
    }


    @Override
    public void run() {
        Log.d(TAG, "run: "+Thread.currentThread().getName());
        synchronized (mSync){
            mLocalRquestStop = false;
            mRequestDrain = 0;
            mSync.notifyAll();
        }
        boolean localRuqestDrain;
        boolean localRequestStop;
        while (mIsRunning){

            synchronized (mSync){
                localRequestStop = mLocalRquestStop;
                localRuqestDrain = (mRequestDrain>0);
                if(localRuqestDrain){
                    mRequestDrain --;
                }
            }

            if(localRequestStop){
                drain();
                mViedeoEncode.signalEndOfInputStream();
                mEnOS = true;
                drain();
                release();
                break;
            }

            if(localRuqestDrain){

                drain();
            }else {
                synchronized (mSync){

                    try {
                        Log.d(TAG, "run: wait");
                        mSync.wait();
                    }catch (InterruptedException e){
                        break;
                    }
                }
            }
        }

        synchronized (mSync){
            mLocalRquestStop = true;
            mIsCaturing = false;
        }
    }

    private void  drain(){
        int count = 0;
 LOOP:       while (mIsCaturing){
            int encodeStatue = mViedeoEncode.
                    dequeueOutputBuffer(mBfferInfo,10000);
           if(encodeStatue == MediaCodec.INFO_TRY_AGAIN_LATER){
               if(!mEnOS){
                   if(++count >5){
                       break LOOP;
                   }
               }
           }else if(encodeStatue == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED){
               Log.d(TAG, "drain: "+encodeStatue);

               MediaFormat format = mViedeoEncode.getOutputFormat();
               mTrackIndex = mMuxer.addTrack(format);
               mMuxerStart = true;
               if(!mMuxer.start()){
                   synchronized (mMuxer){
                       while (!mMuxer.isStarted()){
                           try {
                               mMuxer.wait(100);
                           }catch (InterruptedException e){
                               break LOOP;
                           }
                       }
                   }
               }

           }else if(encodeStatue <0){
               Log.d(TAG, "drain:unexpected result " +
                       "from encoder#dequeueOutputBuffer: " + encodeStatue);
           }else {
               ByteBuffer byteBuffer = mViedeoEncode.getOutputBuffer(encodeStatue);
               mBfferInfo.presentationTimeUs = getPTSUs();
               prevOutputPTSUs = mBfferInfo.presentationTimeUs;
               mMuxer.writeSampleData(0,byteBuffer,mBfferInfo);
               mViedeoEncode.releaseOutputBuffer(encodeStatue,false);
               if ((mBfferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                   // when EOS come.
                   Log.d(TAG, "drain: EOS");
                   mIsCaturing = false;
                   break;      // out of while
               }
           }

        }
    }

    private void release(){
        if(mViedeoEncode != null){
            mViedeoEncode.stop();
            mViedeoEncode.release();
            mViedeoEncode = null;
        }

        if (mMuxerStart) {
            if (mMuxer != null) {
                try {
                    mMuxer.stop();
                } catch (final Exception e) {
                    Log.e(TAG, "failed stopping muxer", e);
                }
            }
        }
        mBfferInfo = null;
    }

    public void onStopRecording(){
        synchronized (mSync){
            if(!mIsCaturing||mLocalRquestStop){
                return;
            }
            mIsCaturing = false;
            mLocalRquestStop = true;
            mHandler.stop();
            mSync.notifyAll();
        }

    }


    private long prevOutputPTSUs = 0;
    protected long getPTSUs() {
        long result = System.nanoTime() / 1000L;
        // presentationTimeUs should be monotonic
        // otherwise muxer fail to write
        if (result < prevOutputPTSUs)
            result = (prevOutputPTSUs - result) + result;
        return result;
    }
}
