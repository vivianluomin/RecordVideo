//
// Created by luomin on 2019/4/28.
//

#include "include/FFmpegMuxer.h"
using namespace std;

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
    LOGD("FFmpegMux  info pts %lld",info->presentationTimeUs);
    if(index == mVideroIndex){
        avPacket.pts = av_rescale_q((int64_t)(info->presentationTimeUs),
                                     (AVRational){1, AV_TIME_BASE}, mVideoStream->time_base);
        avPacket.dts = avPacket.pts;
        LOGD("FFmpegMux  video pts dts %lld,%lld",avPacket.pts,avPacket.dts);
    } else{
        avPacket.pts = av_rescale_q((int64_t)(info->presentationTimeUs),
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

void FFmpegMuxer::getFirstFrame(const char *filePath, const char *picPath) {
    av_register_all();

    AVFormatContext *pFormatCtx = NULL;
    int videoStream;	//视频流标记

    if (avformat_open_input(&pFormatCtx,filePath,NULL,NULL) !=0) {
        return ;
    }

    if (avformat_find_stream_info(pFormatCtx, NULL) < 0) {
        return ;
    }

    videoStream = -1;
    for (int i = 0; i < pFormatCtx->nb_streams;i++) {
        if (pFormatCtx->streams[i]->codec->codec_type == AVMEDIA_TYPE_VIDEO) {
            videoStream = i;
            break;
        }
    }
    if (videoStream == -1) {
        //读取失败
        return ;
    }

    AVCodecContext *pCodecCtxOrig;
    AVCodec *pCodec;
    AVCodecContext *pCodecCtx;

    pCodecCtxOrig = pFormatCtx->streams[videoStream]->codec;

    pCodec = avcodec_find_decoder(pCodecCtxOrig->codec_id);
    if(pCodec == NULL){
        return;
    }


    pCodecCtx = avcodec_alloc_context3(pCodec);
    if(avcodec_copy_context(pCodecCtx,pCodecCtxOrig)!= 0){
        return;
    }

    if(avcodec_open2(pCodecCtx,pCodec,NULL) < 0){
        return;
    }

    AVFrame *pFrame = av_frame_alloc();
    if(pFrame == NULL){
        return;
    }

    AVFrame *pFrameRGB = av_frame_alloc();
    if(pFrameRGB == NULL){
        return;
    }


    int numBytes = avpicture_get_size(AV_PIX_FMT_RGB24
            ,pCodecCtx->width,pCodecCtx->height);
    uint8_t *buffer = (uint8_t *)av_malloc(numBytes * sizeof(uint8_t));
    //进行关联
    avpicture_fill((AVPicture *)pFrameRGB,buffer,
                         AV_PIX_FMT_RGB24,pCodecCtx->width
            ,pCodecCtx->height);

    int y_size = pCodecCtx->width * pCodecCtx->height;

    AVPacket *packet = (AVPacket *)malloc(sizeof(AVPacket));
    av_new_packet(packet,y_size);
    int frameFinished;
    struct SwsContext *sws_ctx = NULL;

    sws_ctx = sws_getContext(pCodecCtx->width,
                             pCodecCtx->height,pCodecCtx->pix_fmt, pCodecCtx->width, pCodecCtx->height, AV_PIX_FMT_RGB24,
                             SWS_BICUBIC, NULL, NULL, NULL);

    int i = 0;

    while (int x = av_read_frame(pFormatCtx,packet)>=0){
        if(packet->stream_index == videoStream){
            avcodec_decode_video2(pCodecCtx,pFrame,
                                  &frameFinished,packet);
            if(frameFinished){
//                sws_scale(sws_ctx,(uint8_t const * const *)pFrame->data, pFrame->linesize, 0,
//                          pCodecCtx->height, pFrameRGB->data,
//                          pFrameRGB->linesize);
                SaveFrame(packet,pFrame, pCodecCtx->width, pCodecCtx->height,picPath);

                break;

            }
        }

        av_packet_unref(packet);
    }

    av_free(buffer);
    av_frame_free(&pFrameRGB);

    av_frame_free(&pFrame);

    avcodec_close(pCodecCtx);
    avcodec_close(pCodecCtxOrig);

    avformat_close_input(&pFormatCtx);


}



void FFmpegMuxer::SaveFrame(AVPacket *packet,AVFrame *pFrame, int width, int height, const char *name) {

        // 分配AVFormatContext对象
        AVFormatContext* pFormatCtx = avformat_alloc_context();

        // 设置输出文件格式
        pFormatCtx->oformat = av_guess_format("mjpeg", NULL, NULL);

        // 创建并初始化一个和该url相关的AVIOContext
        if (avio_open(&pFormatCtx->pb, name, AVIO_FLAG_READ_WRITE) < 0)
        {
            LOGE("Couldn't open output file");
            return ;
        }

        // 构建一个新stream
        AVStream* pAVStream = avformat_new_stream(pFormatCtx, 0);
        if (pAVStream == NULL)
        {
            LOGE("Frame2JPG::avformat_new_stream error.");
            return ;
        }

        // 设置该stream的信息
        AVCodecContext* pCodecCtx = pAVStream->codec;

        pCodecCtx->codec_id = pFormatCtx->oformat->video_codec;
        pCodecCtx->codec_type = AVMEDIA_TYPE_VIDEO;
        pCodecCtx->pix_fmt = AV_PIX_FMT_YUVJ420P;
        pCodecCtx->width = width;
        pCodecCtx->height = height;
        pCodecCtx->time_base.num = 1;
        pCodecCtx->time_base.den = 25;

        // 查找解码器
        AVCodec* pCodec = avcodec_find_encoder(pCodecCtx->codec_id);
        if (!pCodec)
        {
            LOGE("avcodec_find_encoder() error.");
            return ;
        }
        // 设置pCodecCtx的解码器为pCodec
        if (avcodec_open2(pCodecCtx, pCodec, NULL) < 0)
        {
            LOGE("Could not open codec.");
            return ;
        }

        //Write Header
        int ret = avformat_write_header(pFormatCtx, NULL);
        if (ret < 0)
        {
            LOGE("avformat_write_header() error.\n");
            return ;
        }

        int y_size = pCodecCtx->width * pCodecCtx->height;

        //Encode
        // 给AVPacket分配足够大的空间
        AVPacket pkt;
        av_new_packet(&pkt, y_size * 3);

        int got_picture = 0;
        ret = avcodec_encode_video2(pCodecCtx, &pkt, pFrame, &got_picture);
        if (ret < 0)
        {
            LOGE("avcodec_encode_video2() error.\n");
            return ;
        }

        if (got_picture == 1)
        {
            ret = av_write_frame(pFormatCtx, &pkt);
        }

        av_free_packet(&pkt);

        //Write Trailer
        av_write_trailer(pFormatCtx);

        if (pAVStream)
        {
            avcodec_close(pAVStream->codec);
        }

        avio_close(pFormatCtx->pb);
        avformat_free_context(pFormatCtx);
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


extern "C"
JNIEXPORT void JNICALL
Java_com_example_asus1_videorecoder_Encode_FFmpegMuxer_native_1getFrame(JNIEnv *env,
                                                                         jobject instance,
                                                                         jlong handler,
                                                                        jstring  path,jstring picPath){
    FFmpegMuxer *ffmpegMuxer = reinterpret_cast<FFmpegMuxer *>(handler);
    if(ffmpegMuxer != NULL){
        const char* p = env->GetStringUTFChars(path,NULL);
        const char *pic = env->GetStringUTFChars(picPath,NULL);
        ffmpegMuxer->getFirstFrame(p,pic);
    }

}
