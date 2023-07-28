package com.ruoyi.sip_server.config;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.ruoyi.domain.Device;
import com.ruoyi.domain.DeviceChannel;
import com.ruoyi.domain.base.R;
import com.ruoyi.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SIPLink {


    /**
     * 异步响应中转站
     * key 和 value 关系
     * [前缀:id] = [DeferredResult]
     */

    public final static Map<String, DeferredResult<R<Object>>> ASYNCRESPONSE = new ConcurrentHashMap<>();

    // 设备信息
    final static String deviceInfo = ResourceUtil.readUtf8Str("sip/模拟设备.xml");
    // 通道信息
    final static String catalog = ResourceUtil.readUtf8Str("sip/模拟通道.xml");
    // 录像查询
    final static String recordInfo = ResourceUtil.readUtf8Str("sip/模拟录像.xml");


    /**
     * 查询录像指令
     *
     * @param channelId 通道id
     * @param date      查询哪一天的录像
     * @param sn        随机码 [传null 方法自动获取]
     * @param secrecy   涉密类型 [默认0不涉密,1涉密]
     * @param queryType 查询录像类型[默认all查询全部]
     * @return
     */
    public static String getRecordInfo(String channelId, Date date, Integer sn, Integer secrecy, String queryType) {
        // 获取SN
        if (sn == null) sn = getSN();
        // 默认0,不涉密
        if (secrecy == null) secrecy = 0;
        // 默认查询全部
        if (!StringUtils.hasText(queryType)) queryType = "all";
        // 20xx-0x-xxT00:00:00
        String sta = DateUtil.getDateSTAEND(date, 0);
        // 20xx-0x-xxT23:59:59
        String end = DateUtil.getDateSTAEND(date, 1);
        return String.format(recordInfo, sn, channelId, sta, end, secrecy, queryType);
    }


    /**
     * 获取随机 SN
     *
     * @return
     */
    public static int getSN() {
        return (int) ((Math.random() * 9 + 1) * 100000);
    }

    /**
     * 查询设备信息指令
     *
     * @param sn       随机码
     * @return xml
     */
    public static String getDeviceInfo(Device d, Integer sn) {
        if (sn == null) {
            sn = getSN();
        }
        return String.format(deviceInfo, d.getCharset(), sn, d.getDeviceName(), d.getDeviceName(),d.getDeviceId(), 1);
    }

    /**
     * 查询通道信息指令
     *
     * @param d   设备对象
     * @param sn       随机码
     * @return xml
     */
    public static String getCatalog(Device d, Integer sn) {
        if (sn == null) {
            sn = getSN();
        }
        DeviceChannel c = d.getChannel();
        return String.format(catalog,
                d.getCharset(), sn, d.getDeviceId(),
                c.getChannelId(),c.getChannelName(),
                d.getDeviceId()

        );
    }


    /**
     * 实时推流指令
     *
     * @param streamMode 流模式
     * @param sessionId  会话ID(暂用通道ID)
     * @param ip         目标ip
     * @param port       目标端口
     * @param ssrc       会话唯一id
     * @return 返回拼接后的字符串
     */
    public static String getPaly(String streamMode, String sessionId, String ip, String port, String ssrc) {
        // 获取Bean
        SipConfig sipConfig = SpringUtil.getBean(SipConfig.class);
        // 是否开启SDP拓展
        boolean sdpEnhance = false;
        // 拼接
        StringBuilder context = new StringBuilder(200);
        context.append("v=0\r\n");
        context.append("o=").append(sessionId).append(" 0 0 IN IP4 ").append(ip).append("\r\n");
        context.append("s=Play\r\n");
        context.append("c=IN IP4 ").append(ip).append("\r\n");
        context.append("t=0 0\r\n");

        String video = "";
        // SDP 拓展
        if (sdpEnhance) {
            if ("TCP-PASSIVE".equalsIgnoreCase(streamMode)) {
                video = String.format("m=video %s TCP/RTP/AVP 96 126 125 99 34 98 97", port);
            } else if ("TCP-ACTIVE".equalsIgnoreCase(streamMode)) {
                video = String.format("m=video %s TCP/RTP/AVP 96 126 125 99 34 98 97", port);
            } else if ("UDP".equalsIgnoreCase(streamMode)) {
                video = String.format("m=video %s RTP/AVP 96 126 125 99 34 98 97", port);
            }
            context.append(video);
            context.append("\r\na=recvonly\r\n");
            context.append("a=rtpmap:96 PS/90000\r\n");
            context.append("a=fmtp:126 profile-level-id=42e01e\r\n");
            context.append("a=rtpmap:126 H264/90000\r\n");
            context.append("a=rtpmap:125 H264S/90000\r\n");
            context.append("a=fmtp:125 profile-level-id=42e01e\r\n");
            context.append("a=rtpmap:99 H265/90000\r\n");
            context.append("a=rtpmap:98 H264/90000\r\n");
            context.append("a=rtpmap:97 MPEG4/90000\r\n");
        } else {
            if ("TCP-PASSIVE".equalsIgnoreCase(streamMode)) {
                video = String.format("m=video %s TCP/RTP/AVP 96 97 98 99", port);
            } else if ("TCP-ACTIVE".equalsIgnoreCase(streamMode)) {
                video = String.format("m=video %s TCP/RTP/AVP 96 97 98 99", port);
            } else if ("UDP".equalsIgnoreCase(streamMode)) {
                video = String.format("m=video %s RTP/AVP 96 97 98 99", port);
            }
            context.append(video);
            context.append("\r\na=recvonly\r\n");
            context.append("a=rtpmap:96 PS/90000\r\n");
            context.append("a=rtpmap:98 H264/90000\r\n");
            context.append("a=rtpmap:97 MPEG4/90000\r\n");
            context.append("a=rtpmap:99 H265/90000\r\n");
        }
        // 流模式拼接
        String type = "";
        // tcp被动模式
        if ("TCP-PASSIVE".equalsIgnoreCase(streamMode)) {
            type = "a=setup:passive\r\na=connection:new\r\n";
            // tcp主动模式
        } else if ("TCP-ACTIVE".equalsIgnoreCase(streamMode)) {
            type = "a=setup:active\r\na=connection:new\r\n";
        }
        context.append(type);
        //ssrc
        context.append("y=").append(ssrc).append("\r\n");

        return context.toString();
    }


    /**
     * 发送心跳包
     *
     * @param device
     * @return
     */
    public static String getKeepalive(Device device) {
        StringBuffer keepaliveXml = new StringBuffer(200);
        keepaliveXml.append("<?xml version=\"1.0\"?>\r\n");
        keepaliveXml.append("<Notify>\r\n");
        keepaliveXml.append("<CmdType>Keepalive</CmdType>\r\n");
        keepaliveXml.append("<SN>" + (int) ((Math.random() * 9 + 1) * 100000) + "</SN>\r\n");
        keepaliveXml.append("<DeviceID>" + device.getDeviceId() + "</DeviceID>\r\n");
        keepaliveXml.append("<Status>OK</Status>\r\n");
        keepaliveXml.append("</Notify>\r\n");
        return keepaliveXml.toString();
    }

}
