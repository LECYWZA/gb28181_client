package com.ruoyi.domain.base;

import lombok.Getter;

/**
 * 前缀类
 */
@Getter
public enum Prefix {

    /**
     * 定时发送心跳
     */
    keepalive("keepalive"),

    /**
     * 其他前缀[无所谓]
     */
    uuid("UUID"),
    /**
     * key有前缀是,传入空字符串
     */
    NULL(""),

    /**
     * 设备在线检测前缀[延时任务,处理心跳,处理设备离线]
     */
    deviceOnlineCheck("deviceOnlineCheck"),
    /**
     * 录像结果集前缀
     */
    recordInfoResult("recordInfoResult"),
    /**
     * redis设备保活前缀
     */
    deviceKeepalive("deviceKeepalive"),
    /**
     * redis流媒体设备保活前缀
     */
    zlmKeepalive("zlmKeepalive"),
    /**
     * 服务器重启key 防止一直出发上线事件导致死循环
     */
    zlmRestart("zlmRestart"),
    /**
     * redis实时流
     */
    sipStream("sipStream"),

    /**
     * 拉流代理对象的流id前缀, 用于区分是国标流还是拉流
     */
    pullStream("pull"),

    /**
     * redis cseq前缀[sip区分请求,是新请求还是老请求]
     */
    cseq("cseq"),

    /**
     * 异步响应中转站[实时预览|录像回放]
     */
    palys("palys"),

    /**
     * 存储异步响应中转站-对录像的一系列操作的前缀
     */
    playbackCmd("playbackCmd");


    private final String prefix;

    Prefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String toString() {
        if ("".equals(this.prefix)) {
            return this.prefix;
        }
        return this.prefix + ":";
    }
}
