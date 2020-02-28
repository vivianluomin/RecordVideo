//
// Created by luomin on 2019/4/28.
//

#include "include/FFmpegMuxer.h"

FFmpegMuxer::FFmpegMuxer(const char* path) {
    mPath = path;
}


void FFmpegMuxer::initEGL(ANativeWindow *nativeWindow, OpenGLThread *openglthread, int filter) {
    this->openGLThread = openglthread;
    openglthread->window = nativeWindow;
    openglthread->initOpenGlES(openglthread->shareContext);
    openglthread->drawer = openglthread->createTextureDrawer(filter);
}

void FFmpegMuxer::render(int textId, float *mvp) {
    openGLThread->renderFrame(textId,mvp);
}


void FFmpegMuxer::initFFmpeg(){
    av_register_all();
    AVPacket avPacket;
    int ret,i;
    int videoIndex = 0;
    int audioIndex  = 0;
    //创建AVFormatContext
    avformat_alloc_output_context2(&mFormateContext,NULL,"mp4",mPath);
    mFormateContext->oformat->video_codec = AV_CODEC_ID_H264;
    mFormateContext->oformat->audio_codec = AV_CODEC_ID_AAC;
    av_dump_format(mFormateContext,0,mPath,1);
    if(!(mFormateContext->flags & AVFMT_NOFILE)){
        //打开文件，让文件可写
        avio_open(&mFormateContext->pb,mPath,AVIO_FLAG_WRITE);
    }
    mVideoStream = addVideoStream(mFormateContext,mFormateContext->oformat->video_codec);
    mAudioStream = addAudioStream(mFormateContext,mFormateContext->oformat->audio_codec);
    for(int i = 0;i<mFormateContext->nb_streams;i++){
        if(mFormateContext->streams[i]->codec->codec_type == AVMEDIA_TYPE_VIDEO){
            mVideroIndex = i;
        } else if(mFormateContext->streams[i]->codec->codec_type == AVMEDIA_TYPE_AUDIO){
            mAudioIndex = i;
        }
    }
    avformat_write_header(mFormateContext,NULL);

}

AVStream *FFmpegMuxer::addVideoStream(AVFormatContext *pForContext, AVCodecID codecID) {

    AVCodecContext *context = NULL;
    AVCodec *codec = NULL;
    AVStream *stream = NULL;

    codec = avcodec_find_encoder(pForContext->oformat->video_codec);
    if(codec == NULL){
        return NULL;
    }

    stream = avformat_new_stream(pForContext,codec);
    if(stream == NULL){
        return  NULL;
    }

    stream->codec->width = 720;
    stream->codec->height = 1080;
    stream->id = 0;
    context = stream->codec;
    stream->time_base = (AVRational){1,1000};

    context->codec_id = codecID;
    context->codec_type = AVMEDIA_TYPE_VIDEO;
    if (pForContext->oformat->flags & AVFMT_GLOBALHEADER)
        context->flags |= AV_CODEC_FLAG_GLOBAL_HEADER;

    return stream;

}

AVStream* FFmpegMuxer::addAudioStream(AVFormatContext *pForContext, AVCodecID codecID) {

    AVCodecContext *context = NULL;
    AVCodec *codec = NULL;
    AVStream *stream = NULL;

    codec = avcodec_find_encoder(pForContext->oformat->video_codec);
    if(codec == NULL){
        return NULL;
    }

    stream = avformat_new_stream(pForContext,codec);
    if(stream == NULL){
        return  NULL;
    }

    stream->id = 0;
    stream->codec->sample_fmt = AV_SAMPLE_FMT_S32;
    stream->codec->sample_rate = 44100;
    stream->codec->bit_rate = 44100*2*2;
    stream->codec->channels = 2;
    stream->time_base = (AVRational){1,1000};
    context = stream->codec;
    context->codec_id = codecID;
    context->codec_type = AVMEDIA_TYPE_AUDIO;

    if (pForContext->oformat->flags & AVFMT_GLOBALHEADER)
        context->flags |= AV_CODEC_FLAG_GLOBAL_HEADER;

    return stream;

}


void FFmpegMuxer::writeData(int mediaTrack, uint8_t *data, BufferInfo *info) {

    if(mediaTrack == 0){
        LOGD("FFmpegMuxer mediaTrack-----video");
        mVideoStream->codec->extradata = (uint8_t *)av_mallocz(info->size+3);
        memcpy(mVideoStream->codec->extradata,data,info->size);
        mVideoStream->codec->extradata_size = info->size;
        writeToFile(data,info,mVideoStream,mVideroIndex);
    } else if (mediaTrack == 1){
        LOGD("FFmpegMuxer mediaTrack-----audio");
        mAudioStream->codec->extradata = (uint8_t *)av_mallocz(info->size+3);
        memcpy(mAudioStream->codec->extradata,data,info->size);
        mAudioStream->codec->extradata_size = info->size;
        writeToFile(data,info,mAudioStream,mAudioIndex);
    }

}


void FFmpegMuxer::writeData(int mediaTrack, uint8_t *data, long pts,int size,int flag) {

    BufferInfo *info = new BufferInfo();
    int64_t duration = 0;


    if(mediaTrack == 1){
        if(firstAudioFrame){
            audioDuration = 0;
            audioStartTime = av_gettime();
            firstAudioFrame = false;
        } else{
            audioDuration = av_gettime() - audioStartTime;
        }
        duration = audioDuration;
    }

    if(mediaTrack == 0){
        if(firstVideoFrame){
            videoDuration = 0;
            videoStartTime = av_gettime();
            firstVideoFrame = false;
        } else{
            videoDuration = av_gettime() - videoStartTime;
        }

        duration = videoDuration;
    }


    info->presentationTimeUs = duration;
    info->size = size;
    info->flags = flag;
    writeData(mediaTrack,data,info);
    delete info;

}

void FFmpegMuxer::writeToFile(uint8_t *data,BufferInfo *info,AVStream *avStream,int index) {

    AVPacket avPacket;
    av_init_packet(&avPacket);

    avPacket.stream_index = index;
    avPacket.data = data;
    avPacket.size = info->size;
    LOGD("FFmpegMux  info pts %lld",info->presentationTimeUs/1000 * 1000);
    if(index == mVideroIndex){
        int64_t  dts = info->presentationTimeUs-200;
        avPacket.pts = av_rescale_q((int64_t)(info->presentationTimeUs/1000 * 1000),
                                     (AVRational){1, AV_TIME_BASE}, mVideoStream->time_base);
        avPacket.dts = av_rescale_q((int64_t)dts/1000*1000,(AVRational){1, AV_TIME_BASE}, mVideoStream->time_base);
        LOGD("FFmpegMux  video pts dts %lld,%lld",avPacket.pts,avPacket.dts);
    } else{
        avPacket.pts = av_rescale_q((int64_t)(info->presentationTimeUs/1000 * 1000),
                                     (AVRational){1, AV_TIME_BASE}, mAudioStream->time_base);
        avPacket.dts = avPacket.pts;
        LOGD("FFmpegMux  audio pts %lld",avPacket.pts);
    }


    if(info->flags == BufferInfo::BUFFER_FLAG_KEY_FRAME){
        avPacket.flags |= BufferInfo::BUFFER_FLAG_KEY_FRAME;
    }

    av_interleaved_write_frame(mFormateContext,&avPacket);

}


void FFmpegMuxer::stop() {
    if(mVideoStream){
        avcodec_close(mVideoStream->codec);
        mVideoStream = NULL;
    }
    if(mAudioStream){
        avcodec_close(mAudioStream->codec);
        mAudioStream = NULL;
    }

    if(mFormateContext){
        av_write_trailer(mFormateContext);
        avio_close(mFormateContext->pb);
        avformat_free_context(mFormateContext);
        mFormateContext = NULL;
    }
}

extern "C"
JNIEXPORT jlong JNICALL
Java_com_example_asus1_videorecoder_Encode_FFmpegMuxer_native_1init(JNIEnv *env,
jobject instance,jstring path) {

    const char* p = env->GetStringUTFChars(path,NULL);

    FFmpegMuxer *muxer = new FFmpegMuxer(p);

    LOGD("muxer %d",muxer);

    return (jlong)muxer;

}


extern "C"
JNIEXPORT void JNICALL
Java_com_example_asus1_videorecoder_Encode_FFmpegMuxer_native_1initEGL(JNIEnv *env,
                                                                    jobject instance,
                                                                      jlong handler,
                                                                      jobject surface,
                                                                    jlong openglThread,jint filter) {

    FFmpegMuxer * fFmpegMuxer = reinterpret_cast<FFmpegMuxer *>(handler);
    OpenGLThread* openGL = reinterpret_cast<OpenGLThread *>(openglThread);
    LOGE("shareContext: %d",openGL->shareContext == EGL_NO_CONTEXT);
    ANativeWindow * window = ANativeWindow_fromSurface(env,surface);
    fFmpegMuxer->initEGL(window,openGL,filter);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_asus1_videorecoder_Encode_FFmpegMuxer_native_1render(JNIEnv *env,
                                                                      jobject instance,
                                                                      jlong handler,
                                                                      jint textId,
                                                                      jfloatArray mvp) {
    FFmpegMuxer * fFmpegMuxer = reinterpret_cast<FFmpegMuxer *>(handler);
    jfloat * array = env->GetFloatArrayElements(mvp,0);
    int length = env->GetArrayLength(mvp);
    float *mat = new float[length];

    for(int i = 0;i<length;i++){
        mat[i] = array[i];
    }
    fFmpegMuxer->render(textId,mat);
    env->ReleaseFloatArrayElements(mvp,array,0);

}


extern "C"
JNIEXPORT void JNICALL
Java_com_example_asus1_videorecoder_Encode_FFmpegMuxer_native_1initFFmpeg(JNIEnv *env,
                                                                                  jobject instance,
                                                                                  jlong handler){
    FFmpegMuxer * fFmpegMuxer = reinterpret_cast<FFmpegMuxer *>(handler);
    fFmpegMuxer->initFFmpeg();
}



extern "C"
JNIEXPORT void JNICALL
Java_com_example_asus1_videorecoder_Encode_FFmpegMuxer_writeData(JNIEnv *env,
                                                                              jobject instance,
                                                                              jlong handler,
                                                                              jint mediaTrack,
                                                                              jobject data,
                                                                              jobject info) {

    FFmpegMuxer * fFmpegMuxer = reinterpret_cast<FFmpegMuxer *>(handler);
    if(fFmpegMuxer == NULL){
        return;
    }

    void *by = env->GetDirectBufferAddress(data);
    BufferInfo *bufferInfo = new BufferInfo();

    jclass buffer = env->GetObjectClass(info);
    jfieldID offsetID = env->GetFieldID(buffer,"offset","I");
    jfieldID sizeID = env->GetFieldID(buffer,"size","I");
    jfieldID presentationTimeUsID = env->GetFieldID(buffer,"presentationTimeUs","J");
    jfieldID flagID = env->GetFieldID(buffer,"flags","I");

    jint offset = env->GetIntField(info,offsetID);
    jint size = env->GetIntField(info,sizeID);
    jlong presentationTimeUs = env->GetLongField(info,presentationTimeUsID);
    jint  flags = env->GetIntField(info,flagID);
    jlong test = 12345;

    LOGD("FFmpeg write data pts: %lld,%d,%lld",presentationTimeUs,size,test);

    bufferInfo->offset = offset;
    bufferInfo->size = size;
    bufferInfo->presentationTimeUs = presentationTimeUs;
    bufferInfo->flags = flags;
    fFmpegMuxer->writeData(mediaTrack,(uint8_t *)by,bufferInfo);

}


extern "C"
JNIEXPORT void JNICALL
Java_com_example_asus1_videorecoder_Encode_FFmpegMuxer_native_1stop(JNIEnv *env,
                                                                                 jobject instance,
                                                                                 jlong handler) {

    FFmpegMuxer *ffmpegMuxer = reinterpret_cast<FFmpegMuxer *>(handler);

    ffmpegMuxer->stop();

}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_asus1_videorecoder_Encode_FFmpegMuxer_native_1writeData(JNIEnv *env,
                                                                                      jobject instance,
                                                                                      jlong handler,
                                                                                      jint mediaTrack,
                                                                                      jobject data,
                                                                                      jlong pts,
                                                                                      jint size,
                                                                                      jint flag
                                                                                      ){

    FFmpegMuxer *ffmpegMuxer = reinterpret_cast<FFmpegMuxer *>(handler);
    if(ffmpegMuxer!=NULL){
        void *by = env->GetDirectBufferAddress(data);
        ffmpegMuxer->writeData(mediaTrack,(uint8_t *)by,pts,size,flag);
    }


}