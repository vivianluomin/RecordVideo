//
// Created by luomin on 2019/4/28.
//

#ifndef FUNCAMERA_FFMPEGMUXER_H
#define FUNCAMERA_FFMPEGMUXER_H

#include <iostream>

extern "C"
{
#include <libavfilter/avfilter.h>
#include <libavcodec/avcodec.h>
#include <libavutil/common.h>
#include <libavutil/opt.h>
#include <libavutil/samplefmt.h>
//封装格式处理
#include <libavformat/avformat.h>
#include <libavutil/avconfig.h>
#include <libavutil/time.h>
//像素处理
#include <libswscale/swscale.h>
#include <libavutil/imgutils.h>
#include <unistd.h>
}

#include "OpenGLThread.h"

#include <stdio.h>



using namespace std;

class BufferInfo {
public:
    BufferInfo(){

    }
    int offset;
    int size;
    int64_t presentationTimeUs;
    int flags;
    int64_t duration = 0;
    static const int BUFFER_FLAG_END_OF_STREAM = 4;
    static const int BUFFER_FLAG_KEY_FRAME = 1;
};

class FFmpegMuxer {

public:
    FFmpegMuxer(const char *path, const char* mime);
    void writeData(int mediaTrack,uint8_t *data,BufferInfo *info);
    void writeData(int mediaTrack,uint8_t *data, long pts,int size,int flag);
    void stop();
    void initFFmpeg();
    void initEGL(ANativeWindow *nativeWindow,OpenGLThread * openglthread, int filter);
    void render(int textId, float *mvp);
    void getFirstFrame(const char *filePath, const char *picPath);
    void SaveFrame(AVPacket *packet,AVFrame* pFrame,int width,int height, const char *name);

private:
    const char* mPath;
    const char* mime;
    AVStream *mVideoStream;
    AVStream *mAudioStream;
    AVFormatContext *mFormateContext = NULL;
    AVFormatContext *mFormateContext_audio = NULL;
    AVFormatContext *mFormateContext_video = NULL;
    int64_t videoDuration = 0;
    int64_t videoStartTime = 0;
    int64_t audioDuration = 0;
    int64_t audioStartTime = 0;
    bool firstVideoFrame = true;
    bool firstAudioFrame = true;
    int mVideroIndex = 0;
    int mAudioIndex = 0;
    OpenGLThread *openGLThread;
    void writeToFile(uint8_t *data,BufferInfo *info,AVStream *avStreamm,int index);
    AVStream *addVideoStream(AVFormatContext *pForContext,AVCodecID codecID);
    AVStream *addAudioStream(AVFormatContext *pForContext,AVCodecID codecID);


};



#endif //FUNCAMERA_FFMPEGMUXER_H
