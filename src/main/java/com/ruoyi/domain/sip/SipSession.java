package com.ruoyi.domain.sip;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.sip.Dialog;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class SipSession implements Serializable {

    private static final long serialVersionUID = 1L;
    // 全局 key 为 ssrc
    public static final ConcurrentHashMap<String, Dialog> mapDialog = new ConcurrentHashMap<>();

    /**
     * 流id, 可能一个摄像头推两个流,对于实时预览是唯一的,录像回放则是一个ssrc一个回放
     * 此处使用 设备id_通道id
     */
    private String streamId;

    /**
     * SipSession创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime = new Date();

    /**
     * 会话唯一id
     */
    private String callId;
    /**
     * ssrc
     */
    private String ssrc;

    /**
     * ZLM RTP服务器端口[收流端口]
     */
    private String rtpPort;
    /**
     * 是否是回放[0实时预览 | 1录像回放]
     */
    private int type = 0;


    /**
     * 流地址
     */
    private Map<String, String> streamUrls;

    /**
     * 流媒体流详情
     */
    private JSONObject streamDetails;


    /**
     * 点播时的 SDP 数据
     * 可以获取到 ssrc
     */
    private Properties properties;

    /**
     * 获取SSRC
     *
     * @return
     */
    public String getPropertiesSSRC() {
        if (this.properties != null) {
            return this.properties.getProperty("y");
        }
        return null;
    }

    /**
     * 获取流id[实时预览和录像回放的流id不一样]
     * @return
     */
    public String getStreamIdSSRC() {
        if (type != 0) {
            return String.format("%s_%s", streamId, ssrc);
        }
        return streamId;
    }
}
