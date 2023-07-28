package com.ruoyi.subscribe.execute_event;

import com.ruoyi.delayed_task.DelayQueueManager;
import com.ruoyi.delayed_task.DelayTask;
import com.ruoyi.domain.Device;
import com.ruoyi.domain.MyTest;
import com.ruoyi.domain.base.Prefix;
import com.ruoyi.sip_server.config.DeviceInit;
import com.ruoyi.sip_server.config.SipConfig;
import com.ruoyi.sip_server.tools.SipAuthUtil;
import com.ruoyi.subscribe.event.SipRegisterRespEvent;
import com.ruoyi.utils.SipCmdUtil;
import com.ruoyi.utils.SipUtil;
import gov.nist.javax.sip.header.From;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.sip.ResponseEvent;
import javax.sip.SipFactory;
import javax.sip.header.CSeqHeader;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ViaHeader;
import javax.sip.header.WWWAuthenticateHeader;
import javax.sip.message.Response;

/**
 * 注册响应事件触发
 */
@Slf4j
@Component
public class SipRegisterEventRespExecute implements ApplicationListener<SipRegisterRespEvent> {


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



    @Override
    @Async("my")
    public void onApplicationEvent(SipRegisterRespEvent evt) {

        ResponseEvent event = evt.getEvt();
        Response response = event.getResponse();
        ViaHeader via = (ViaHeader) response.getHeader("Via");
        int status = response.getStatusCode();
        if (((status >= 200) && (status < 300)) || status == 401) { // Success!
            if (status != 200) {
                log.info("收到{}回复，\n{}", status, via.getHost() + ":" + via.getPort());
            } else {
                log.info("收到{}回复ip {}", status, via.getHost() + ":" + via.getPort());
            }
            CSeqHeader cseqHeader = (CSeqHeader) event.getResponse().getHeader(CSeqHeader.NAME);
            String method = cseqHeader.getMethod();
            // 注册或者注销
            sendMsg(event);

            if (status == 200) {
                log.info("状态码为200");
            }
        } else if ((status >= 100) && (status < 200)) {
            // 增加其它无需回复的响应，如101、180等
            log.info("增加其它无需回复的响应，如101、180等");

        } else {
            log.error("接收到失败的response响应！status：" + status + ",message:" + response.getReasonPhrase()/* .getContent().toString()*/);
            if (response != null) {
                CallIdHeader callIdHeader = (CallIdHeader) response.getHeader(CallIdHeader.NAME);
                if (callIdHeader != null) {
                    log.error("注册失败{}", callIdHeader);
                }
            }
            if (event.getDialog() != null) {
                event.getDialog().delete();
            }
        }

    }


    private void sendMsg(ResponseEvent evt) {
        Response response = evt.getResponse();
        CallIdHeader callIdHeader = (CallIdHeader) response.getHeader(CallIdHeader.NAME);
        String callId = callIdHeader.getCallId();

        // 获取设备id
        String uri = evt.getResponse().getHeader(From.NAME).toString();
        String deviceId = uri.substring(uri.indexOf(":") + 1, uri.indexOf("@")).split(":")[1];
        log.info("设备id: {}", deviceId);
        Device d = DeviceInit.ds.get(deviceId);
        if (d == null) {
            log.info("设备对象为空");
            return;
        }

        int statusCode = response.getStatusCode();
        if (statusCode == 401) {
            //携带验证信息
            WWWAuthenticateHeader authorizationHeader = (WWWAuthenticateHeader) response.getHeader(WWWAuthenticateHeader.NAME);
            if (!d.isRegister()) {
                log.info("向平台:{} 发送带认证信息的注册消息!");
                // 发送注册
                sipCmdUtil.sendRegister(d, authorizationHeader, callId);
                new MyTest(d.getDeviceId(), "发送注册请求-带认证信息");

            } else {
                // 此处还是注册, 注销另外接口, 不然注销一部分注册一部分,在执行会出现相反
                log.info("向平台:{} 发送带认证信息的注销消息!");
                // sipCmdUtil.unRegister(sipPlatform, device, callId, authorizationHeader, null);
                sipCmdUtil.sendRegister(d, authorizationHeader, callId);

            }
        } else if (statusCode == 200) {
            d.setIsOnline("1");
            d.setRegister(true);
            // 存入缓存
            DeviceInit.ds.put(d.getDeviceId(), d);

            // 定时发送心跳
            manager.put(new DelayTask(Prefix.keepalive, d.getDeviceId(), Long.parseLong(sipConfig.getKeepaliveTimeout()), () -> {
                sipCmdUtil.sendKeepalive(d);
                // log.info("发送心跳完成 {} {}",d.getDeviceId(),d.getDeviceName());

            }));
            log.info("设备向平台:{} 注册成功!");

        } else {
            log.info("设备向平台:{} 注册失败!");
            d.setRegister(false);
        }
    }
}
