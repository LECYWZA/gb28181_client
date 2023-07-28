package com.ruoyi.domain;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 设备表
 * @TableName my_device
 */
@Data
public class Device implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 厂家
     */
    private String manufacturer;

    /**
     * 型号
     */
    private String model;

    /**
     * 固件版本
     */
    private String firmware;

    /**
     * 注册模式(UDP|TCP)
     */
    private String registerProtocol;

    /**
     * 推流模式(UDP|TCP)
     */
    private String streamProtocol;

    /**
     * 是否在线(0离线,1在线)
     */
    private String isOnline;

    /**
     * 上次注册时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date registerTime;

    /**
     * 上次心跳时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date keepaliveTime;

    /**
     * 设备ip
     */
    private String deviceIp;

    /**
     * 设备端口
     */
    private int devicePort;
    /**
     * 流媒体推流id
     */
    private String zlmIp;
    /**
     * 流媒体推流端口
     */
    private int zlmPort;

    /**
     * 注册有效期
     */
    private String expires;

    /**
     * 字符集编码
     */
    private String charset;
    /**
     * 是否注册
     */
    private boolean isRegister = false;

    /**
     * 目前只一个设备一个通道
     */
    DeviceChannel channel;

}
