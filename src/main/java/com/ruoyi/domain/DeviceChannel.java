package com.ruoyi.domain;


import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

/**
 * 设备通道表
 * @TableName device_channel
 */

@Data
public class DeviceChannel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 通道名称
     */
    private String channelName;

    /**
     * 父级设备ID
     */
    private String parentId;

    /**
     * 通道id
     */
    private String channelId;

    /**
     * 厂家
     */
    private String manufacturer;

    /**
     * 型号
     */
    private String model;

    /**
     * 设备归属
     */
    private String owner;

    /**
     * 是否在线(0开启,1关闭)
     */
    private String status;

    /**
     * 音频(0开启,1关闭)
     */
    private String isAudio;

    /**
     * 设备ip
     */
    private String ipAddress;

    /**
     * 设备端口
     */
    private String port;

    /**
     * 设备经度
     */
    private String longitude;

    /**
     * 设备纬度
     */
    private String latitude;

    /**
     * 行政区域代码
     */
    private String civilCode;

    /**
     * 密码
     */
    private String password;

    /**
     * 警区
     */
    private String block;

    /**
     * 信令安全模式(可选)缺省为0; 0:不采用;2:S/MIME 签名方式;3:S/ MIME加密签名同时采用方式;4:数字摘要方式
     */
    private String safetyWay;

    /**
     * 注册方式(必选)缺省为1;1:符合IETFRFC3261标准的认证注册模 式;2:基于口令的双向认证注册模式;3:基于数字证书的双向认证注册模式
     */
    private String registerWay;

    /**
     * 地址
     */
    private String address;
    /**
     * 家长
     */
    private String parental;


    /*通道ID和设备ID一致就是同一个对象*/
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceChannel that = (DeviceChannel) o;
        return Objects.equals(parentId, that.parentId) && Objects.equals(channelId, that.channelId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parentId, channelId);
    }
}
