package com.ruoyi.domain.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 云台控制
 */
@Data
@ApiModel(value = "PtzDTO", description = "云台控制请求对象")
public class PtzDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 设备id
     */
    @ApiModelProperty(value = "设备id", required = true, example = "44010200492000000001")
    private String deviceId;
    /**
     * 通道id
     */
    @ApiModelProperty(value = "设备id", required = true, example = "44010200492000000002")
    private String channelId;
    /**
     * 控制指令,允许值: left, right, up, down, upleft, upright, downleft, downright, zoomin, zoomout, stop
     */
    @ApiModelProperty(value = "制指令,允许值: left, right, up, down, upleft, upright, downleft, downright, zoomin, zoomout, stop", required = true, example = "right")
    private String command;
    /**
     * 水平速度
     */
    @ApiModelProperty(value = "水平速度[不传默认255]",example = "255")
    private int horizonSpeed = 255;
    /**
     * 垂直速度
     */
    @ApiModelProperty(value = "垂直速度[不传默认255]",  example = "255")
    private int verticalSpeed = 255;
    /**
     * 缩放速度
     */
    @ApiModelProperty(value = "缩放速度[不传默认255]", example = "255")
    private int zoomSpeed = 255;


}
