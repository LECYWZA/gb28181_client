package com.ruoyi.utils;

import cn.hutool.core.lang.UUID;
import com.ruoyi.delayed_task.DelayQueueManager;
import com.ruoyi.delayed_task.DelayTask;
import com.ruoyi.domain.Device;
import com.ruoyi.sip_server.config.DeviceInit;
import com.ruoyi.sip_server.config.SIPLink;
import com.ruoyi.sip_server.config.SSRCConfig;
import com.ruoyi.sip_server.config.SipConfig;
import com.ruoyi.sip_server.tools.SipAuthUtil;
import com.ruoyi.subscribe.execute_event.SipKeepaliveEventExecute;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.sip.*;
import javax.sip.header.CallIdHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.WWWAuthenticateHeader;
import javax.sip.message.Request;
import java.text.ParseException;
import java.util.Map;

@Slf4j
@Component
public class SipCmdUtil {

    @Autowired
    private SipConfig sipConfig;

    @Autowired
    private SipFactory sipFactory;


    @Autowired
    private SipAuthUtil sipAuthUtil;

    @Autowired
    private SipUtil sipUtil;

    @Autowired
    private SipProvider tcpSipProvider;

    @Autowired
    private SipProvider udpSipProvider;

    @Autowired
    private SSRCConfig ssrcConfig;

    @Autowired
    private DelayQueueManager delayQueueManager;


    /**
     * 根据协议获取 CallIdHeader
     *
     * @param protocol UDP OR TCP
     * @return
     */
    private CallIdHeader getCallIdHeader(String protocol) {
        return (protocol.equalsIgnoreCase("TCP")) ?
                tcpSipProvider.getNewCallId() : udpSipProvider.getNewCallId();
    }


    /**
     * 停止推流
     *
     * @param dcAddress 流id, 不传停止所有推流
     * @return
     * @throws SipException
     */
    public Map<String, Boolean> sendBye(String dcAddress) {
        return null;
    }


    /**
     * 发送注册请求
     *
     * @param d      设备对象
     * @param www    认证信息
     * @param callId 会议id
     * @throws ParseException
     * @throws InvalidArgumentException
     * @throws SipException
     */
    public void sendRegister(Device d, WWWAuthenticateHeader www, String callId) {

        try {
            String tm = UUID.fastUUID().toString(true);
            Request request = null;
            CallIdHeader callIdHeader = udpSipProvider.getNewCallId();
            if (www == null) {
                request = sipUtil.createRegisterRequest(d, 1L, "From-Register-" + tm, null, callIdHeader, 3600);
            } else {
                callIdHeader.setCallId(callId);
                request = sipUtil.createRegisterRequest(www, d, 2L, "From-Register-" + tm, null, callIdHeader);
            }
            if (request != null) {
                udpSipProvider.sendRequest(request);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 发送心跳包
     */
    public void sendKeepalive(Device d) {
        try {
            // 心跳内容
            String context = SIPLink.getKeepalive(d);
            CallIdHeader callIdHeader = udpSipProvider.getNewCallId();
            String tm1 = UUID.fastUUID().toString(true);
            String tm2 = UUID.fastUUID().toString(true);
            Request request = sipUtil.createKeetpaliveMessageRequest(d, context, "z9hG4bK-Keepalive-" + tm1, tm2, null, callIdHeader);
            udpSipProvider.sendRequest(request);
            // log.info("发送心跳: \n{}", request);
            // 存储心跳请求
            String callId = callIdHeader.getCallId();

            // 不为空就行
            SipKeepaliveEventExecute.keepaliveCallId.put(callId, "xxx");

            // 没收到响应移除 callId
            delayQueueManager.put(new DelayTask(5000L, () -> {
                String cId = SipKeepaliveEventExecute.keepaliveCallId.get(callId);
                if (StringUtils.hasText(cId)) {
                    // 移除
                    SipKeepaliveEventExecute.keepaliveCallId.remove(callId);
                    // 同时增加一次
                    Integer sum = SipKeepaliveEventExecute.keepalive.get(d.getDeviceId());
                    SipKeepaliveEventExecute.keepalive.put(d.getDeviceId(), sum != null ? ++sum : 1);
                    // 超过3次设置为离线
                    if (d.isRegister() && sum != null && sum >= 3) {
                        Device device = DeviceInit.ds.get(d.getDeviceId());
                        device.setRegister(false);
                        DeviceInit.ds.put(d.getDeviceId(), device);
                    }

                }
            }));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送设备信息
     *
     * @param deviceId 设备id
     */
    public void sendDeviceInfo(RequestEvent event, String deviceId) {
        Device device = DeviceInit.ds.get(deviceId);
        Request request = event.getRequest();
        if (device != null && request != null) {
            try {
                String deviceInfo = SIPLink.getDeviceInfo(device, null);
                String tm1 = UUID.fastUUID().toString(true);
                String tm2 = UUID.fastUUID().toString(true);

                FromHeader fromHeader = (FromHeader) request.getHeader(FromHeader.NAME);
                CallIdHeader callIdHeader = udpSipProvider.getNewCallId();
                Request req = sipUtil.createMessageRequest(device, deviceInfo, tm1/*fromHeader.getTag()*/,"z9hG4bK-"+tm2, callIdHeader);
                udpSipProvider.sendRequest(req);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            log.error("发送设备信息错误-设备对象: [{}], 请求对象: [{}]", device, request);
        }

    }

    /**
     * 发送通道信息
     *
     * @param event
     * @param deviceId
     */
    public void sendCatalog(RequestEvent event, String deviceId) {
        Device device = DeviceInit.ds.get(deviceId);
        Request request = event.getRequest();
        if (device != null && request != null) {
            try {
                String catalog = SIPLink.getCatalog(device, null);

                String tm1 = UUID.fastUUID().toString(true);
                String tm2 = UUID.fastUUID().toString(true);

                FromHeader fromHeader = (FromHeader) request.getHeader(FromHeader.NAME);
                CallIdHeader callIdHeader = udpSipProvider.getNewCallId();
                Request req = sipUtil.createMessageRequest(device, catalog, tm1/*fromHeader.getTag()*/,"z9hG4bK-"+tm2, callIdHeader);
                udpSipProvider.sendRequest(req);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            log.error("发送通道信息错误-设备对象: [{}], 请求对象: [{}]", device, request);
        }

    }

    public void logOut(Device d) {
        try {
            String tm = UUID.fastUUID().toString(true);
            CallIdHeader callIdHeader = udpSipProvider.getNewCallId();
            //携带验证信息
            Request request = sipUtil.createRegisterRequest(d, 1L, "From-Register" + tm, null, callIdHeader, 0);
            udpSipProvider.sendRequest(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
