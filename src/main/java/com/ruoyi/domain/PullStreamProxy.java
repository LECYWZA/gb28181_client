package com.ruoyi.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 拉流代理
 * @TableName pull_stream_proxy
 */
@Data
public class PullStreamProxy implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 添加的流的虚拟主机，例如__defaultVhost__
     */
    private String vhost;


    /**
     * 流媒体服务器id
     */
    private String mediaServerId;

    /**
     * 流应用名
     */
    private String app;

    /**
     * 流id
     */
    private String streamId;

    /**
     * 拉流地址
     */
    private String url;

    /**
     * 拉流方式[rtsp拉流时，拉流方式，0：tcp，1：udp，2：组播]
     */
    private String rtpType;

    /**
     * 拉流超时时间，单位秒，float类型
     */
    private Float timeoutSec;

    /**
     * 拉流重试次数,不传此参数或传值<=0时，则无限重试
     */
    private Integer retryCount;

    /**
     * 是否转hls
     */
    private String enableHls;

    /**
     * 是否mp4录制
     */
    private String enableMp4;

    /**
     * 是否转协议为rtsp/webrtc
     */
    private String enableRtsp;

    /**
     * 是否转协议为rtmp/flv
     */
    private String enableRtmp;

    /**
     * 是否转协议为http-ts/ws-ts
     */
    private String enableTs;

    /**
     * 是否转协议为http-fmp4/ws-fmp4
     */
    private String enableFmp4;

    /**
     * 转协议是否开启音频
     */
    private String enableAudio;

    /**
     * 转协议无音频时，是否添加静音aac音频
     */
    private String addMuteAudio;

    /**
     * mp4录制保存根目录，置空使用默认目录
     */
    private String mp4SavePath;

    /**
     * mp4录制切片大小，单位秒
     */
    private String mp4MaxSecond;

    /**
     * hls保存根目录，置空使用默认目录
     */
    private String hlsSavePath;

    /**
     * 无人观看自动关闭流, 0 不关闭,1关闭
     */
    private String autoCloseStream;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;



}