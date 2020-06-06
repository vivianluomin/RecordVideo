package com.example.asus1.videorecoder.UI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus1.videorecoder.Encode.VideoRecordEncode;
import com.example.asus1.videorecoder.R;
import com.example.asus1.videorecoder.RecordSetting;
import com.example.asus1.videorecoder.UI.BaseActivity;

public class SettingActivity extends BaseActivity {

    private ImageView mBack;
    private Switch mCamera1;
    private Switch mCamera2;
    private Switch mGPUMuxer;
    private Switch mCPUMuxer;
    private Switch mMP4;
    private Switch mAVI;
    private Switch mFLV;
    private Switch mMKV;
    private Switch m264;
    private Switch m265;
    private TextView mSave;
    private RecordSetting.CameraType mCameraType = RecordSetting.CameraType.Camera1;
    private RecordSetting.MuxerType mMuxerType = RecordSetting.MuxerType.GPU;
    private RecordSetting.FileType mFileTyppe = RecordSetting.FileType.MP4;
    private String mMimeType = VideoRecordEncode.MIME_TYPE_264;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        |View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        |View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        |View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        |View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                        |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

         Intent intent = getIntent();

        mCameraType = (RecordSetting.CameraType)
                intent.getSerializableExtra("camera");
        mMuxerType = (RecordSetting.MuxerType)
                intent.getSerializableExtra("muxer");
        mFileTyppe = (RecordSetting.FileType)
                intent.getSerializableExtra("file");

        mMimeType = intent.getStringExtra("mime");

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

        mSave = findViewById(R.id.tv_save);
        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("camera",mCameraType);
                intent.putExtra("muxer",mMuxerType);
                intent.putExtra("file",mFileTyppe);
                intent.putExtra("mime",mMimeType);
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        mCamera1 = findViewById(R.id.switch_camera_1);
        mCamera1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mCameraType = RecordSetting.CameraType.Camera1;
                    mCamera2.setChecked(false);
                }else {
                    mCameraType = RecordSetting.CameraType.Camera2;
                    mCamera2.setChecked(true);
                }
            }
        });
        mCamera2 = findViewById(R.id.switch_camera_2);
        mCamera2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mCameraType = RecordSetting.CameraType.Camera2;
                    mCamera1.setChecked(false);
                }else {
                    mCameraType = RecordSetting.CameraType.Camera1;
                    mCamera1.setChecked(true);
                }
            }
        });
        mGPUMuxer = findViewById(R.id.switch_muxer_gpu);
        mGPUMuxer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               if(isChecked){
                   mMuxerType = RecordSetting.MuxerType.GPU;
                   mCPUMuxer.setChecked(false);
               }else {
                   mMuxerType = RecordSetting.MuxerType.CPU;
                   mCPUMuxer.setChecked(true);
               }
            }
        });

        mCPUMuxer = findViewById(R.id.switch_muxer_cpu);
        mCPUMuxer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mMuxerType = RecordSetting.MuxerType.CPU;
                    mGPUMuxer.setChecked(false);
                    mMimeType = VideoRecordEncode.MIME_TYPE_264;
                    m264.setChecked(true);
                    m265.setChecked(false);
                }else {
                    mMuxerType = RecordSetting.MuxerType.GPU;
                    mGPUMuxer.setChecked(true);
                }
            }
        });


        m264 = findViewById(R.id.switch_264);
        m264.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mMimeType = VideoRecordEncode.MIME_TYPE_264;
                    m265.setChecked(false);
                }else {
                    if(mMuxerType == RecordSetting.MuxerType.CPU){
                        Toast.makeText(SettingActivity.this,
                                "使用FFmpegMuxer时不可改变编码",Toast.LENGTH_SHORT).show();
                        m265.setChecked(false);
                        m264.setChecked(true);
                    }else {
                        mMimeType = VideoRecordEncode.MIME_TYPE_265;
                        m265.setChecked(true);
                    }

                }
            }
        });

        m265 = findViewById(R.id.switch_265);
        m265.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(mMuxerType == RecordSetting.MuxerType.CPU){
                        Toast.makeText(SettingActivity.this,
                                "使用FFmpegMuxer时不可使用该编码",Toast.LENGTH_SHORT).show();
                        m265.setChecked(false);
                        m264.setChecked(true);
                    }else {
                        mMimeType = VideoRecordEncode.MIME_TYPE_265;
                        m264.setChecked(false);
                    }

                }else {
                    mMimeType = VideoRecordEncode.MIME_TYPE_264;
                    m264.setChecked(true);
                }
            }
        });


        mMP4 = findViewById(R.id.switch_mp4);
        mMP4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mFileTyppe = RecordSetting.FileType.MP4;
                    mAVI.setChecked(false);
                    mFLV.setChecked(false);
                    mMKV.setChecked(false);
                }else {
                    if(mMuxerType == RecordSetting.MuxerType.GPU){
                        Toast.makeText(SettingActivity.this,
                                "使用MediaMuxer时不可改变文件格式",Toast.LENGTH_SHORT).show();
                        mMP4.setChecked(true);
                    }else {
                        if(mFileTyppe == RecordSetting.FileType.MP4){
                            Toast.makeText(SettingActivity.this,
                                    "至少使用一个",Toast.LENGTH_SHORT).show();
                            mMP4.setChecked(true);
                        }
                    }
                }

            }
        });


        mAVI = findViewById(R.id.switch_avi);
        mAVI.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(mMuxerType == RecordSetting.MuxerType.GPU){
                        Toast.makeText(SettingActivity.this,
                                "使用MediaMuxer时不可改变使用该格式",Toast.LENGTH_SHORT).show();
                        mMP4.setChecked(true);
                        mAVI.setChecked(false);
                        mFLV.setChecked(false);
                        mMKV.setChecked(false);
                    }else {
                        mFileTyppe = RecordSetting.FileType.AVI;
                        mMP4.setChecked(false);
                        mFLV.setChecked(false);
                        mMKV.setChecked(false);
                    }
                }else {
                    //mFileTyppe = RecordSetting.FileType.MP4;
                    if(mFileTyppe == RecordSetting.FileType.AVI){
                        mFileTyppe = RecordSetting.FileType.MP4;
                        mMP4.setChecked(true);
                    }

                }

            }
        });

        mFLV = findViewById(R.id.switch_flv);
        mFLV.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(mMuxerType == RecordSetting.MuxerType.GPU){
                        Toast.makeText(SettingActivity.this,
                                "使用MediaMuxer时不可改变使用该格式",Toast.LENGTH_SHORT).show();
                        mMP4.setChecked(true);
                        mAVI.setChecked(false);
                        mFLV.setChecked(false);
                        mMKV.setChecked(false);
                    }else {
                        mFileTyppe = RecordSetting.FileType.FLV;
                        mMP4.setChecked(false);
                        mAVI.setChecked(false);
                        mMKV.setChecked(false);
                    }
                }else {
                    if(mFileTyppe == RecordSetting.FileType.FLV) {
                        mFileTyppe = RecordSetting.FileType.MP4;
                        mMP4.setChecked(true);
                    }
                }
            }
        });

        mMKV = findViewById(R.id.switch_mkv);
        mMKV.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(mMuxerType == RecordSetting.MuxerType.GPU){
                        Toast.makeText(SettingActivity.this,
                                "使用MediaMuxer时不可改变使用该格式",Toast.LENGTH_SHORT).show();
                        mMP4.setChecked(true);
                        mAVI.setChecked(false);
                        mFLV.setChecked(false);
                        mMKV.setChecked(false);
                    }else {
                        mFileTyppe = RecordSetting.FileType.MKV;
                        mMP4.setChecked(false);
                        mFLV.setChecked(false);
                        mAVI.setChecked(false);
                    }
                }else {
                    if(mFileTyppe == RecordSetting.FileType.MKV) {
                        mFileTyppe = RecordSetting.FileType.MP4;
                        mMP4.setChecked(true);
                    }
                }

            }
        });


        if(mCameraType == RecordSetting.CameraType.Camera1){
            mCamera1.setChecked(true);
        }else {
            mCamera2.setChecked(true);
        }

        if(mMuxerType == RecordSetting.MuxerType.GPU){
            mGPUMuxer.setChecked(true);
        }else {
            mCPUMuxer.setChecked(true);
        }

        switch (mFileTyppe){
            case AVI:
                mAVI.setChecked(true);
                break;
            case MKV:
                mMKV.setChecked(true);
                break;
            case MP4:
                mMP4.setChecked(true);
                break;
            case FLV:
                mFLV.setChecked(true);
                break;
        }

        if(mMimeType.equals(VideoRecordEncode.MIME_TYPE_264)){
            m264.setChecked(true);
        }else {
            m265.setChecked(true);
        }

    }

}
