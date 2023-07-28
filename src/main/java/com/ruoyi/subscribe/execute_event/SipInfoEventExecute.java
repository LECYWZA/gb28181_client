package com.ruoyi.subscribe.execute_event;

import com.ruoyi.domain.base.Prefix;
import com.ruoyi.domain.base.R;
import com.ruoyi.sip_server.config.SIPLink;
import com.ruoyi.subscribe.event.SipInfoEvent;
import com.ruoyi.utils.SipUtil;
import com.ruoyi.utils.ZLMediaKitHttpUtil;
import gov.nist.javax.sip.header.From;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.DeferredResult;

import javax.sip.ResponseEvent;
import javax.sip.header.CSeqHeader;
import javax.sip.message.Response;

/**
 * 目前: 控制录像[进度,倍数]
 */
@Slf4j
@Component
public class SipInfoEventExecute implements ApplicationListener<SipInfoEvent> {
    @Autowired
    private SipUtil sipUtil;

    

    @Autowired
    private ZLMediaKitHttpUtil httpUtil;

    @Override
    @Async("my")
    public void onApplicationEvent(SipInfoEvent InfoEvent) {
        ResponseEvent evt = InfoEvent.getEvt();
        Response response = evt.getResponse();
        try {
            int statusCode = response.getStatusCode();
            if (statusCode == Response.OK) {
                CSeqHeader clientCSeqHeader = (CSeqHeader) response.getHeader(CSeqHeader.NAME);
                long cseq = clientCSeqHeader.getSeqNumber();
                String callId = evt.getDialog().getCallId().getCallId();
                // 是否是录像回放的响应
                boolean isContains = response.getHeader(From.NAME).toString().contains("From-Payback");
                // 是
                if (isContains) {
                    // 获取异步响应对象
                    DeferredResult<R<Object>> result = SIPLink.ASYNCRESPONSE.get(Prefix.playbackCmd + callId + cseq);
                    if (result != null) {
                        // 响应前端
                        result.setResult(R.success("操作成功,但摄像头不一定支持该指令"));
                        // 删除异步响应中转站的Key
                        SIPLink.ASYNCRESPONSE.remove(Prefix.playbackCmd + callId + cseq);
                    }
                }
            }
        } catch (Exception err) {
            err.printStackTrace();
        }


    }

}
