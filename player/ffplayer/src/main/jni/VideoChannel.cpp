//
// Created by Luffy on 17/5/2021.
//

#include <cstring>
#include <cstdlib>
#include "VideoChannel.h"
#include "x264.h"
//#include "log.h"

VideoChannel::~VideoChannel() {
    LOGI("VideoChannel release");
    if (videoCodec) {
        x264_encoder_close(videoCodec);
        videoCodec = 0;
    }
}

void VideoChannel::setVideoEncodeInfo(int width, int height, int fps, int bitrate) {

    this->mWidth = width;
    this->mHeight = height;
    this->mFps = fps;
    this->mBitrate = bitrate;

    ySize = width*height;
    uvSize = ySize/4;

    LOGI("初始化x264解码器  width = %d",width);
    LOGI("初始化x264解码器  height = %d",height);
    LOGI("初始化x264解码器  fps = %d",fps);
    LOGI("初始化x264解码器  bitrate = %d",bitrate);
    LOGI("初始化x264解码器  ySize = %d",ySize);
    LOGI("初始化x264解码器  uvSize = %d",uvSize);

    // 初始化之前先销毁
    if (videoCodec) {
//        x264_encoder_close(videoCodec);
        videoCodec = NULL;
    }

    x264_param_t param;
    // 编码等级
    x264_param_default_preset(&param,"ultrafast","zerolatency");
    // 编码等级
    param.i_level_idc = 32;
    // nv12
    param.i_csp = X264_CSP_I420;
    //
    param.i_width = width;
    param.i_height = height;
    // 没有B帧
    param.i_bframe = 0;
    // 编码码率控制
    param.rc.i_rc_method = X264_RC_ABR;
    //码率 k 为单位
    param.rc.i_bitrate = bitrate/1000;

    //帧率分母
    param.i_fps_num = fps;
    // 分子
    param.i_fps_den = 1;
    param.i_timebase_num = param.i_fps_num;
    param.i_timebase_den = param.i_fps_den;
    // 用fps而不是时间戳计算帧间距
    param.b_vfr_input = 0;
    // I帧间隔 ,2秒一个I帧
    param.i_keyint_max = fps * 2;
    // 是否每次发送I帧前复制sps/pps到I帧前面
    param.b_repeat_headers = 1;
    // 多线程
    param.i_threads = 1;
    x264_param_apply_profile(&param,"baseline");
    videoCodec = x264_encoder_open(&param);
    pic_in = new x264_picture_t;
    x264_picture_alloc(pic_in,X264_CSP_I420,width,height);

    // ------- 备份代码
////    实例化X264
//    mWidth = width;
//    mHeight = height;
//    mFps = fps;
//    mBitrate = bitrate;
////
//    ySize = width * height;
//    uvSize = ySize / 4;
//    if (videoCodec) {
////        x264_encoder_close(videoCodec);
//        videoCodec = 0;
//    }
////    定义参数
//    x264_param_t param;
////    参数赋值   x264  麻烦  编码器 速度   直播  越快 1  越慢2
//    x264_param_default_preset(&param, "ultrafast", "zerolatency");
////编码等级
//    param.i_level_idc = 32;
////    选取显示格式
//    param.i_csp = X264_CSP_I420;
//    param.i_width = width;
//    param.i_height = height;
////    B帧
//    param.i_bframe = 0;
////折中    cpu   突发情况   ABR 平均
//    param.rc.i_rc_method = X264_RC_ABR;
////k为单位
//    param.rc.i_bitrate = bitrate / 1024;
////帧率   1s/25帧     40ms  视频 编码      帧时间 ms存储  us   s
//    param.i_fps_num = fps;
////    帧率 时间  分子  分母
//    param.i_fps_den = 1;
////    分母
//    param.i_timebase_den = param.i_fps_num;
////    分子
//    param.i_timebase_num = param.i_fps_den;
//
////单位 分子/分母    发热  --
//    //用fps而不是时间戳来计算帧间距离
//    param.b_vfr_input = 0;
////I帧间隔     2s  15*2
//    param.i_keyint_max = fps * 2;
//
//    // 是否复制sps和pps放在每个关键帧的前面 该参数设置是让每个关键帧(I帧)都附带sps/pps。
//    param.b_repeat_headers = 1;
////    sps  pps  赋值及裙楼
//    //多线程
//    param.i_threads = 1;
//    x264_param_apply_profile(&param, "baseline");
////    打开编码器  宽 高一定是交换的
//    videoCodec = x264_encoder_open(&param);
////容器
//    pic_in = new x264_picture_t;
////设置初始化大小  容器大小就确定的
//    x264_picture_alloc(pic_in, X264_CSP_I420, width, height);
//    大公司 Camera2 plan[0] y
    LOGI("初始化x264成功");
}

void VideoChannel::setVideoCallback(VideoChannel::VideoCallback videoCallback) {
    this->videoCallback = videoCallback;
}

/**
 * 编码
 * @param data nv12数据
 */
void VideoChannel::encodeData(uint8_t *data) {
//    LOGI("encodeData...");
    memcpy(pic_in->img.plane[0],data,ySize);
    for (int i = 0; i < uvSize; i++) {
        // u
        *(pic_in->img.plane[1] + i) = *(data + ySize + i*2+1);
        // v
        *(pic_in->img.plane[2] + i) = *(data + ySize + i*2);
    }

    // 编码nal 的个数 一般都是1
    int pi_nal;
    // 编码出的h264的数据
    x264_nal_t *pp_nals;
    // 编码出参，编码之后的数据存放
    x264_picture_t pic_out;
    // 编码
    x264_encoder_encode(videoCodec,&pp_nals,&pi_nal,pic_in,&pic_out);

    uint8_t sps[100];
    uint8_t pps[100];

    int sps_len,pps_len;
    if (pi_nal > 0) {
        for (int i = 0; i < pi_nal; ++i) {
            if (pp_nals[i].i_type == NAL_SPS) {
//                LOGI("解码 sps");
                sps_len = pp_nals[i].i_payload - 4;
                memcpy(sps,pp_nals[i].p_payload + 4,sps_len);
//                LOGI("解码完 sps");
            }else if(pp_nals[i].i_type == NAL_PPS) {
//                LOGI("解码 pps");
                pps_len = pp_nals[i].i_payload - 4;
                memcpy(pps,pp_nals[i].p_payload+4,pps_len);
//                LOGI("解码完 pps");
                sendSpsPps(sps,pps,sps_len,pps_len);
            } else {
//                LOGI("解码完 帧");
                sendFrame(pp_nals[i].i_type,pp_nals[i].i_payload,pp_nals[i].p_payload);
            }
        }


    }

    // ---- 备份代码

////    容器   y的数据
//    memcpy(pic_in->img.plane[0], data, ySize);
//    for (int i = 0; i < uvSize; ++i) {
//        //v数据
//        *(pic_in->img.plane[2] + i) = *(data + ySize + i * 2);
//        //间隔1个字节取一个数据
//        //u数据
//        *(pic_in->img.plane[1] + i) = *(data + ySize + i * 2+1 );
//    }
////编码成H264码流
//
//    //编码出了几个 nalu （暂时理解为帧）  1   pi_nal  1  永远是1
//    int pi_nal;
////     编码出了几帧
//    //编码出的数据 H264
//    x264_nal_t *pp_nals;
////    pp_nal[]
////编码出的参数  BufferInfo
//    x264_picture_t pic_out;
////关键的一句话   yuv  数据 转化 H  sendPacket
//    x264_encoder_encode(videoCodec, &pp_nals, &pi_nal, pic_in, &pic_out);
//    LOGE("videoCodec value  %d",videoCodec);
////sps数据   30
//    uint8_t sps[100];
//    uint8_t pps[100];
//
//
//    int sps_len, pps_len;
//    LOGE("编码出的帧数  %d",pi_nal);
//    if (pi_nal > 0) {
//        for (int i = 0; i < pi_nal; ++i) {
//            LOGE("输出索引:  %d  输出长度 %d",i,pi_nal);
////                javaCallHelper->postH264(reinterpret_cast<char *>(pp_nals[i].p_payload), pp_nals[i].i_payload);
//            if (pp_nals[i].i_type == NAL_SPS) {
////        sps    发送  1   一起发送
//                sps_len = pp_nals[i].i_payload - 4;
//                memcpy(sps, pp_nals[i].p_payload + 4, sps_len);
//            }  else if (pp_nals[i].i_type == NAL_PPS) {
////        到了pps   需要 1  不需要 2
//                pps_len = pp_nals[i].i_payload - 4;
//                memcpy(pps, pp_nals[i].p_payload + 4, pps_len);
////                发送出去
//                sendSpsPps(sps, pps, sps_len, pps_len);
//            } else{
//                //关键帧、非关键帧
//                sendFrame(pp_nals[i].i_type,pp_nals[i].i_payload,pp_nals[i].p_payload);
//            }
//        }
//    }
//    LOGE("pi_nal  %d",pi_nal);
////pp_nal  输出来
////    H264码流
//    return;

}

/**
 * 发送sps/pps
 * @param sps
 * @param pps
 * @param sps_len
 * @param pps_len
 */
void VideoChannel::sendSpsPps(uint8_t *sps, uint8_t *pps, int sps_len, int pps_len) {
    int body_size = 13 + sps_len + 3 + pps_len;
    RTMPPacket *packet = static_cast<RTMPPacket *>(malloc(sizeof(RTMPPacket)));
    RTMPPacket_Alloc(packet, body_size);

    int i = 0;
    packet->m_body[i++] = 0x17;
    packet->m_body[i++] = 0x00;
    packet->m_body[i++] = 0x00;
    packet->m_body[i++] = 0x00;
    packet->m_body[i++] = 0x00;
    packet->m_body[i++] = 0x01; // 版本

    packet->m_body[i++] = sps[1]; // 编码规格 sps[1]+sps[2]+sps[3]
    packet->m_body[i++] = sps[2];
    packet->m_body[i++] = sps[3];

    packet->m_body[i++] = 0xFF;
    packet->m_body[i++] = 0xE1; // sps个数： 1 ，0xE1 & 0x1F

    packet->m_body[i++] = (sps_len >> 8) & 0xFF; // sps 长度 2个字节  , 高八位
    packet->m_body[i++] = sps_len & 0xFF; // sps 长度  低八位

    // sps 内容
    memcpy(&packet->m_body[i], sps, sps_len);
    i += sps_len;

    packet->m_body[i++] = 0x01; // pps 个数
    packet->m_body[i++] = (pps_len >> 8) & 0xFF; // pps 长度 高八位
    packet->m_body[i++] = pps_len & 0xFF; // pps 长度 低八位

    // pps 内容
    memcpy(&packet->m_body[i], pps, pps_len);
    // ----- 数据填充完毕

    // 设置属性
    packet->m_packetType = RTMP_PACKET_TYPE_VIDEO;
    packet->m_nBodySize = body_size; // 数据总长度
    packet->m_nChannel = 0x04; // 视频04
    packet->m_nTimeStamp = 0;
    packet->m_hasAbsTimestamp = 0;
    packet->m_headerType = RTMP_PACKET_SIZE_LARGE;
//    packet->m_nInfoField2 = live->rtmp->m_stream_id;

//  ---------备份代码
//    RTMPPacket *packet = new RTMPPacket;
//    int bodysize = 13 + sps_len + 3 + pps_len;
//    RTMPPacket_Alloc(packet, bodysize);
//    int i = 0;
//    //固定头
//    packet->m_body[i++] = 0x17;
//    //类型
//    packet->m_body[i++] = 0x00;
//    //composition time 0x000000
//    packet->m_body[i++] = 0x00;
//    packet->m_body[i++] = 0x00;
//    packet->m_body[i++] = 0x00;
//
//    //版本
//    packet->m_body[i++] = 0x01;
//    //编码规格
//    packet->m_body[i++] = sps[1];
//    packet->m_body[i++] = sps[2];
//    packet->m_body[i++] = sps[3];
//    packet->m_body[i++] = 0xFF;
//
//    //整个sps
//    packet->m_body[i++] = 0xE1;
//    //sps长度
//    packet->m_body[i++] = (sps_len >> 8) & 0xff;
//    packet->m_body[i++] = sps_len & 0xff;
//    memcpy(&packet->m_body[i], sps, sps_len);
//    i += sps_len;
//
//    //pps
//    packet->m_body[i++] = 0x01;
//    packet->m_body[i++] = (pps_len >> 8) & 0xff;
//    packet->m_body[i++] = (pps_len) & 0xff;
//    memcpy(&packet->m_body[i], pps, pps_len);
//
//
//    //视频
//    packet->m_packetType = RTMP_PACKET_TYPE_VIDEO;
//    packet->m_nBodySize = bodysize;
//    //随意分配一个管道（尽量避开rtmp.c中使用的）
//    packet->m_nChannel = 10;
//    //sps pps没有时间戳
//    packet->m_nTimeStamp = 0;
//    //不使用绝对时间
//    packet->m_hasAbsTimestamp = 0;
//    packet->m_headerType = RTMP_PACKET_SIZE_MEDIUM;

    videoCallback(packet);

    LOGI("x264 发送 sps  pps");
}

/**
 * 发送帧
 * @param type
 * @param len
 * @param data
 */
void VideoChannel::sendFrame(int type, int len, uint8_t *data) {

    // 这里注意去掉分隔符
    if (data[2] == 0x00){
        len -= 4;
        data += 4;
    } else if(data[2] == 0x01){
        len -= 3;
        data += 3;
    }

    int body_size = len + 9;
    RTMPPacket *packet = static_cast<RTMPPacket *>(malloc(sizeof(RTMPPacket)));
    RTMPPacket_Alloc(packet, body_size);
    int i = 0;
    if ((data[0] & 0x1f) == 5) {
//    if (type == NAL_SLICE_IDR) {
        // I帧
        packet->m_body[i++] = 0x17;
    } else {
        packet->m_body[i++] = 0x27;
    }
    packet->m_body[i++] = 0x01;
    packet->m_body[i++] = 0x00;
    packet->m_body[i++] = 0x00;
    packet->m_body[i++] = 0x00;

    // 长度  4字节
    packet->m_body[i++] = len >> 24 & 0xFF;
    packet->m_body[i++] = len >> 16 & 0xFF;
    packet->m_body[i++] = len >> 8 & 0xFF;
    packet->m_body[i++] = len & 0xFF;

    // 数据
    memcpy(&packet->m_body[i], data, len);

    // 设置其他属性
    packet->m_nBodySize = body_size; // 数据总长度
    packet->m_packetType = RTMP_PACKET_TYPE_VIDEO;
    packet->m_nChannel = 0x04; // 视频04
    packet->m_hasAbsTimestamp = 0;
//    packet->m_nTimeStamp = stamp;
    packet->m_headerType = RTMP_PACKET_SIZE_LARGE;
//    packet->m_nInfoField2 = live->rtmp->m_stream_id;

//    videoCallback(packet);

//去掉 00 00 00 01 / 00 00 01
//    if (data[2] == 0x00){
//        len -= 4;
//        data += 4;
//    } else if(data[2] == 0x01){
//        len -= 3;
//        data += 3;
//    }
//    RTMPPacket *packet = new RTMPPacket;
//    int bodysize = 9 + len;
//    RTMPPacket_Alloc(packet, bodysize);
//    RTMPPacket_Reset(packet);
////    int type = len[0] & 0x1f;
//    packet->m_body[0] = 0x27;
//    //关键帧
//    if (type == NAL_SLICE_IDR) {
//        LOGE("关键帧");
//        packet->m_body[0] = 0x17;
//    }
//    //类型
//    packet->m_body[1] = 0x01;
//    //时间戳
//    packet->m_body[2] = 0x00;
//    packet->m_body[3] = 0x00;
//    packet->m_body[4] = 0x00;
//    //数据长度 int 4个字节 相当于把int转成4个字节的byte数组
//    packet->m_body[5] = (len >> 24) & 0xff;
//    packet->m_body[6] = (len >> 16) & 0xff;
//    packet->m_body[7] = (len >> 8) & 0xff;
//    packet->m_body[8] = (len) & 0xff;
//
//    //图片数据
//    memcpy(&packet->m_body[9],data,  len);
//
//    packet->m_hasAbsTimestamp = 0;
//    packet->m_nBodySize = bodysize;
//    packet->m_packetType = RTMP_PACKET_TYPE_VIDEO;
//    packet->m_nChannel = 0x10;
//    packet->m_headerType = RTMP_PACKET_SIZE_LARGE;

    videoCallback(packet);

    LOGI("x264 发送 帧");
}

