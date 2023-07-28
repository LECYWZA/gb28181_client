package com.ruoyi.subscribe.execute_event;

import com.ruoyi.delayed_task.DelayQueueManager;
import com.ruoyi.delayed_task.DelayTask;
import com.ruoyi.domain.Device;
import com.ruoyi.domain.base.Prefix;
import com.ruoyi.sip_server.config.DeviceInit;
import com.ruoyi.sip_server.config.SipConfig;
import com.ruoyi.sip_server.tools.SipAuthUtil;
import com.ruoyi.subscribe.event.SipKeepaliveEvent;
import com.ruoyi.utils.SipCmdUtil;
import com.ruoyi.utils.SipUtil;
import gov.nist.javax.sip.header.From;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.sip.ResponseEvent;
import javax.sip.SipFactory;
import javax.sip.header.CallIdHeader;
import javax.sip.message.Response;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 发送心跳包
 */
@Slf4j
@Component
public class SipKeepaliveEventExecute implements ApplicationListener<SipKeepaliveEvent> {


    @Autowired
    private SipConfig sipConfig;

    @Autowired
    private SipFactory sipFactory;


    @Autowired
    private SipAuthUtil sipAuthUtil;

    @Autowired
    private SipUtil sipUtil;

    @Autowired
    private SipCmdUtil sipCmdUtil;


    @Autowired
    private DelayQueueManager manager;

    /**
     * 心跳未响应次数
     */
    public static Map<String, Integer> keepalive = new ConcurrentHashMap<>();
    /**
     * 判断是否是心跳会议的响应
     */
    public static Map<String, String> keepaliveCallId = new ConcurrentHashMap<>();


    @Override
    // @Async("my")
    public void onApplicationEvent(SipKeepaliveEvent evt) {
        ResponseEvent event = evt.getEvt();
        Response response = event.getResponse();
        CallIdHeader callIdHeader = (CallIdHeader) response.getHeader(CallIdHeader.NAME);
        String callId = callIdHeader.getCallId();

        // 获取设备id
        String uri = response.getHeader(From.NAME).toString();
        String deviceId = uri.substring(uri.indexOf(":") + 1, uri.indexOf("@")).split(":")[1];
        log.info("设备id: {}", deviceId);
        Device d = DeviceInit.ds.get(deviceId);
        if (d == null) {
            log.info("设备对象为空");
            return;
        }

        int statusCode = response.getStatusCode();

        if (statusCode == 200) {
            // 删除次数累计
            keepalive.remove(deviceId);
            // 删除id
            keepaliveCallId.remove(callId);
        }

        // 注册状态继续发送心跳
        if (d.isRegister()) {
            log.info("继续心跳延时任务");
            // 定时发送心跳
            manager.put(new DelayTask(Prefix.keepalive, d.getDeviceId(), Long.parseLong(sipConfig.getKeepaliveTimeout()), () -> {
                sipCmdUtil.sendKeepalive(d);
                log.info("注册状态发送心跳完成 {} {}",d.getDeviceId(),d.getDeviceName());
                log.info("{} 未响应次数: {}", deviceId,keepalive.get(deviceId));
            }));
        }

    }
}
