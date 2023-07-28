package com.ruoyi.domain.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

/**
 * 对录像回放的一系列操作,倍数播放,跳进度
 */
@Data
@ApiModel(value = "PlaybackCmd", description = "对录像回放的一系列操作,倍数播放,跳进度")
public class PlaybackCmd {

    /**
     * 设备id
     */
    @ApiModelProperty(value = "设备id_通道id_ssrc", example = "1111_2222_2345")
    private String dcAddr;

    /**
     * 操作[0操作进度,1调节倍数,2停止回放,3开始回放,4关闭回放]
     */
    @ApiModelProperty(value = "操作[0操作进度,1调节倍数,2停止回放,3开始回放,4关闭回放]", required = true, example = "0")
    private int type = 0;
    /**
     * [type=0]时必填,录像开始时间为原点,跳多少秒的录像
     */
    @ApiModelProperty(value = "[type=0]时必填,录像开始时间为原点,跳多少秒的录像", example = "0")
    private long time;
    /**
     * [type=1]时必填,要设置录像倍数
     */
    @ApiModelProperty(value = "[type=1]时必填,要设置录像倍数", example = "2.0")
    private double speed;


    /**
     * 切割[设备id_通道id_ssrc]
     */
    @ApiModelProperty(hidden = true)
    private List<String> data;
    @ApiModelProperty(hidden = true)
    private void init() {
        if (data == null) data = Arrays.asList(this.dcAddr.split("_"));
    }
    @ApiModelProperty(hidden = true)
    public String getDeviceId() {
        init();
        return data.get(0);
    }
    @ApiModelProperty(hidden = true)
    public String getChannelId() {
        init();
        return data.get(1);
    }
    @ApiModelProperty(hidden = true)
    public String getSsrc() {
        init();
        return data.get(2);
    }
}
