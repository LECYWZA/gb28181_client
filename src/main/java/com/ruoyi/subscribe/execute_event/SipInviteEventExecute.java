package com.ruoyi.subscribe.execute_event;

import com.ruoyi.delayed_task.DelayQueueManager;
import com.ruoyi.domain.Device;
import com.ruoyi.media.config.ZLMediaKitConfig;
import com.ruoyi.sip_server.config.DeviceInit;
import com.ruoyi.sip_server.config.SipConfig;
import com.ruoyi.sip_server.tools.SipAuthUtil;
import com.ruoyi.subscribe.EventPublisher;
import com.ruoyi.subscribe.event.SipInviteEvent;
import com.ruoyi.utils.SipCmdUtil;
import com.ruoyi.utils.SipUtil;
import com.ruoyi.utils.ZLMediaKitHttpUtil;
import gov.nist.javax.sip.header.To;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.sip.RequestEvent;
import javax.sip.SipFactory;
import javax.sip.header.CallIdHeader;
import javax.sip.message.Request;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 设备推流事件执行
 */
@Slf4j
@Component
public class SipInviteEventExecute implements ApplicationListener<SipInviteEvent> {


    @Autowired
    private SipConfig sipConfig;

    @Autowired
    private SipFactory sipFactory;


    @Autowired
    private SipAuthUtil sipAuthUtil;

    @Autowired
    private SipUtil sipUtil;

    @Autowired
    private DelayQueueManager delayQueueManager;

    @Autowired
    private EventPublisher eventPublisher;

    @Autowired
    private SipCmdUtil sipCmdUtil;
    @Autowired
    private ZLMediaKitHttpUtil httpUtil;
    @Autowired
    private ZLMediaKitConfig zlMediaKitConfig;

    /**
     * 正在推的流 键值对关系: callId = ssrc
     */
    public final static Map<String,String> pushStream  = new ConcurrentHashMap<>();


    @Override
    // @Async("my")
    public void onApplicationEvent(SipInviteEvent evt) {
        try {
            RequestEvent event = evt.getEvt();
            Request request = event.getRequest();
            CallIdHeader callIdHeader = (CallIdHeader) request.getHeader(CallIdHeader.NAME);
            String callId =  callIdHeader.getCallId();

            log.info("目标SDP内容: \r\n{}", request);
            // 获取设备id [form也是通道id,我直接改34了]
            String fromUri = event.getRequest().getHeader(To.NAME).toString();
            String channelId = fromUri.substring(fromUri.indexOf(":") + 1, fromUri.indexOf("@")).split(":")[1];
            String deviceId = "34" + channelId.substring(2);
            log.info("设备id: {}", deviceId);
            Device d = DeviceInit.ds.get(deviceId);
            if (d == null) {
                log.info("设备对象为空");
                return;
            }

            // 获取SDP
            Properties p = sipUtil.getProperties(request.getRawContent());
            String ssrc = p.getProperty("y");
            String spdId = p.getProperty("o").split(" ")[0];
            String ip = p.getProperty("c").split(" ")[2];
            String port = p.getProperty("m").split(" ")[1];
            boolean isTcp = p.getProperty("m").contains("TCP");


            // 流媒体推流
            boolean b = httpUtil.pushStreamGB(
                    zlMediaKitConfig.getDefaultZLMediaKit(),
                    ssrc,
                    ip,
                    null,
                    port,
                    !isTcp
            );

            if (!b){
                log.error("推流失败: [{}][{}][{}]", deviceId,channelId,ssrc);
                return;
            }
            // 存入正在推流缓存
            pushStream.put(callId, ssrc);

            log.info("推流结果: {}", b);



            StringBuffer content = new StringBuffer(200);
            content.append("v=0\r\n");
            // content.append("o=" + channelId + " 0 0 IN IP4 " + d.getZlmIp() + "\r\n");
            content.append("o=" + spdId + " 0 0 IN IP4 " + d.getZlmIp() + "\r\n");
            content.append("s=" + "Play" + "\r\n");
            content.append("c=IN IP4 " + d.getZlmIp() + "\r\n");
            content.append("t=0 0\r\n");
            if (isTcp) {
                content.append("m=video " + "30000" + " TCP/RTP/AVP 96\r\n");
            } else {
                content.append("m=video " + "30000" + " RTP/AVP 96\r\n");
            }
            content.append("a=sendonly\r\n");
            content.append("a=rtpmap:96 PS/90000\r\n");
            content.append("y=" + ssrc + "\r\n");

            System.out.println("本级推流SDP============================================");
            System.out.println(content.toString());
            // 先响应ACK
            sipUtil.responseAck(event, content.toString());
            // sipUtil.responseAck(event,"");



        } catch (Exception e) {
            log.info("sdp解析错误");
            e.printStackTrace();
        }
    }
}
