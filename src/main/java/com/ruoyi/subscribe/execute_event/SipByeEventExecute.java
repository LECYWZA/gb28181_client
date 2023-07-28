package com.ruoyi.subscribe.execute_event;

import com.ruoyi.domain.ZLMediaKit;
import com.ruoyi.media.config.ZLMediaKitConfig;
import com.ruoyi.subscribe.event.SipByeEvent;
import com.ruoyi.utils.SipUtil;
import com.ruoyi.utils.ZLMediaKitHttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.sip.RequestEvent;
import javax.sip.SipFactory;
import javax.sip.header.CallIdHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;

/**
 * 结束推流
 */
@Slf4j
@Component
public class SipByeEventExecute implements ApplicationListener<SipByeEvent> {
    @Autowired
    private SipFactory sipFactory;
    @Autowired
    private SipUtil sipUtil;
    @Autowired
    private ZLMediaKitHttpUtil httpUtil;
    @Autowired
    private ZLMediaKitConfig zlMediaKitConfig;


    @Override
    // @Async("my")
    public void onApplicationEvent(SipByeEvent byeEvent) {
        RequestEvent evt = byeEvent.getEvt();
        Request request = evt.getRequest();
        CallIdHeader callIdHeader = (CallIdHeader) request.getHeader(CallIdHeader.NAME);
        String callId =  callIdHeader.getCallId();
        try {
            // 响应设备
            sipUtil.response(evt, null, Response.OK, null);
            // 存入正在推流缓存
            String ssrc = SipInviteEventExecute.pushStream.get(callId);

            if (StringUtils.hasText(ssrc)){
                ZLMediaKit zlm = zlMediaKitConfig.getDefaultZLMediaKit();
                boolean b = httpUtil.stopStreamGB(zlm, ssrc);
                if (b) {
                    log.info("停止推流成功");
                }else {
                    log.error("流媒体处理发送错误");
                }
            }else {
                log.error("ssrc不存在");
            }

        } catch (Exception err) {
            log.error("\n回复Bye事件处理失败", err);
            err.printStackTrace();
        }

    }
}
