package com.example.asus1.videorecoder.Controller;


import com.example.asus1.videorecoder.Encode.VideoRecordEncode;

public interface ViewController {

    void startRecording();
    void stopRecording();
    void setVideoEncode(VideoRecordEncode encode);

}
