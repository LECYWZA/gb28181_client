package com.ruoyi.subscribe.execute_event;

import com.ruoyi.delayed_task.DelayQueueManager;
import com.ruoyi.domain.MyTest;
import com.ruoyi.sip_server.config.SipConfig;
import com.ruoyi.sip_server.tools.SipAuthUtil;
import com.ruoyi.subscribe.EventPublisher;
import com.ruoyi.subscribe.event.SipMessageEvent;
import com.ruoyi.utils.SipCmdUtil;
import com.ruoyi.utils.SipUtil;
import com.ruoyi.utils.XMLUtil;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.sip.RequestEvent;
import javax.sip.SipFactory;
import javax.sip.message.Request;
import javax.sip.message.Response;

/**
 * 收到消息
 */
@Slf4j
@Component
public class SipMessageEventExecute implements ApplicationListener<SipMessageEvent> {



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


    @Override
    @Async("my")
    public void onApplicationEvent(SipMessageEvent evt) {
        RequestEvent event = evt.getEvt();
        Request request = event.getRequest();

        try {
            JSONObject msg = XML.toJSONObject(new String(request.getRawContent(), "GB2312"));
            JSONObject json = XMLUtil.getJSONObject(msg, "Query");
            if (json == null) {
                return;
            }
            String cmd = XMLUtil.getString(json, "CmdType");
            String deviceId = XMLUtil.getString(json, "DeviceID");

            if (!StringUtils.hasText(deviceId)) {
                log.info("\n设备id 为空, 结束执行....");
                return;
            }
            Response response = null;


            // 更新设备信息
            if ("DeviceInfo".equalsIgnoreCase(cmd)) {
                log.info("\n[{}]发送设备信息....", deviceId);
                sipCmdUtil.sendDeviceInfo(event,deviceId);
                new MyTest(deviceId, "发送设备信息");

            }

            // 发送录像
            else if ("RecordInfo".equalsIgnoreCase(cmd)) {
                log.info("\n[{}]发送录像信息....", deviceId);

            }

            //目录响应，保存到redis
            else if ("Catalog".equalsIgnoreCase(cmd)) {
                log.info("\n[{}]发送通道信息....", deviceId);
                sipCmdUtil.sendCatalog(event,deviceId);
                new MyTest(deviceId, "发送通道信息");
            }
            // 响应设备
             sipUtil.response(event, response, Response.OK, null);
        } catch (Exception err) {
            log.error("\nMessageEvent事件处理错误: ", err);
            err.printStackTrace();
        }
    }
}
