package com.ruoyi.domain.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 添加拉流代理
 * @TableName pull_stream_proxy
 */
@Data
public class AddPullStreamProxy implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键[新增不用传]", example = "1")
    private Long id;

    /**
     * 流媒体服务器id
     */
    @ApiModelProperty(value = "流媒体服务器id", example = "不传使用默认流媒体")
    private String mediaServerId = "auto";

    /**
     * 流应用名
     */
    @ApiModelProperty(value = "流应用名",required = true, example = "live")
    private String app;

    /**
     * 流id
     */
    @ApiModelProperty(value = "流id",required = true, example = "111_111")
    private String streamId;

    /**
     * 拉流地址
     */
    @ApiModelProperty(value = "拉流地址",required = true, example = "rtsp://admin:@192.168.10.12:554/stream1")
    private String url;

    /**
     * 拉流方式[rtsp拉流时，拉流方式，0：tcp，1：udp，2：组播]
     */
    @ApiModelProperty(value = "拉流方式[rtsp拉流时，拉流方式，0：tcp，1：udp]", example = "1")
    private String rtpType;

    /**
     * 拉流超时时间，单位秒，float类型
     */
    @ApiModelProperty(value = "拉流超时时间，单位秒，float类型", example = "10.0")
    private Float timeoutSec = 10F;

    /**
     * 拉流重试次数,次数<=0时，则无限重试
     */
    @ApiModelProperty(value = "拉流重试次数,不传此参数或传值<=0时，则无限重试", example = "3")
    private Integer retryCount = 3;

    /**
     * 是否转hls
     */
    @ApiModelProperty(value = "是否转hls", example = "0")
    private String enableHls;

    /**
     * 是否mp4录制
     */
    @ApiModelProperty(value = "是否mp4录制", example = "0")
    private String enableMp4;

    /**
     * 是否转协议为rtsp/webrtc
     */
    @ApiModelProperty(value = "是否转协议为rtsp/webrtc", example = "1")
    private String enableRtsp;

    /**
     * 是否转协议为rtmp/flv
     */
    @ApiModelProperty(value = "是否转协议为rtmp/flv", example = "1")
    private String enableRtmp;

    /**
     * 是否转协议为http-ts/ws-ts
     */
    @ApiModelProperty(value = "是否转协议为http-ts/ws-ts", example = "1")
    private String enableTs;

    /**
     * 是否转协议为http-fmp4/ws-fmp4
     */
    @ApiModelProperty(value = "是否转协议为http-fmp4/ws-fmp4", example = "1")
    private String enableFmp4;

    /**
     * 转协议是否开启音频
     */
    @ApiModelProperty(value = "转协议是否开启音频", example = "1")
    private String enableAudio;

    /**
     * 转协议无音频时，是否添加静音aac音频
     */
    @ApiModelProperty(value = "转协议无音频时，是否添加静音aac音频", example = "1")
    private String addMuteAudio;

    /**
     * mp4录制保存根目录，置空使用默认目录
     */
    @ApiModelProperty(hidden = true)
    private String mp4SavePath;

    /**
     * mp4录制切片大小，单位秒
     */
    @ApiModelProperty(hidden = true)
    private String mp4MaxSecond;

    /**
     * hls保存根目录，置空使用默认目录
     */
    @ApiModelProperty(hidden = true)
    private String hlsSavePath;

    /**
     * 无人观看自动关闭流, 0 不关闭,1关闭
     */
    private String autoCloseStream = "1";

}