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

    this->width = width;
    this->height = height;
    this->fps = fps;
    this->bitrate = bitrate;

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
        x264_encoder_close(videoCodec);
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
                LOGI("解码 sps");
                sps_len = pp_nals[i].i_payload - 4;
                memcpy(sps,pp_nals[i].p_payload + 4,sps_len);
                LOGI("解码完 sps");
            }else if(pp_nals[i].i_type == NAL_PPS) {
                LOGI("解码 pps");
                pps_len = pp_nals[i].i_payload - 4;
                memcpy(pps,pp_nals[i].p_payload+4,pps_len);
                LOGI("解码完 pps");
                sendSpsPps(sps,pps,sps_len,pps_len);
            } else {
                LOGI("解码完 帧");
                sendFrame(pp_nals[i].i_type,pp_nals[i].i_payload,pp_nals[i].p_payload);
            }
        }


    }

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
    data += 4;
    len -= 4;

    int body_size = len + 9;
    RTMPPacket *packet = static_cast<RTMPPacket *>(malloc(sizeof(RTMPPacket)));
    RTMPPacket_Alloc(packet, body_size);
    int i = 0;
    if ((data[0] & 0x1f) == 5) {
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
    videoCallback(packet);

    LOGI("x264 发送 帧");
}

