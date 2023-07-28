package com.ruoyi.sip_server;


import com.ruoyi.subscribe.EventPublisher;
import com.ruoyi.subscribe.event.*;
import gov.nist.javax.sip.header.From;
import gov.nist.javax.sip.header.Via;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sip.*;
import javax.sip.header.CSeqHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;
@Slf4j
// 已通过Bean方法创建对象,已在Spring环境下
public class SipServer implements SipListener {


    @Autowired
    private EventPublisher eventPublisher;

    @Override
    public void processRequest(RequestEvent evt) {
        String method = evt.getRequest().getMethod();
        //log.info("请求: \n{}",evt.getRequest());

        // 类型判断
        switch (method){
            case Request.MESSAGE : eventPublisher.eventPush(new SipMessageEvent(evt));break;
            case Request.INVITE: eventPublisher.eventPush(new SipInviteEvent(evt));break;
            case Request.BYE :  eventPublisher.eventPush(new SipByeEvent(evt));break;
            default: /*log.info("其他请求类型:[{}]",method)*/;
        }
    }

    @Override
    public void processResponse(ResponseEvent evt) {
        Response response = evt.getResponse();
        CSeqHeader cseqHeader = (CSeqHeader) response.getHeader(CSeqHeader.NAME);
        String method = cseqHeader.getMethod();
        //log.info("响应: \n{}",response);
        // 内容
         String from = response.getHeader(From.NAME).toString();
        String viaString = from + response.getHeader(Via.NAME).toString();
        viaString = viaString.toLowerCase();
        // 心跳响应
       if (viaString.contains("keepalive")){
           // 响应
           eventPublisher.eventPush(new SipKeepaliveEvent(evt));
       }
       // 注册响应
       else if (viaString.contains("register")){
           // 响应
           eventPublisher.eventPush(new SipRegisterRespEvent(evt));
       }

       // 其他
       else if (viaString.contains("xxxx")){
           // 响应
           eventPublisher.eventPush(new SipRegisterRespEvent(evt));
       }

    }

    @Override
    public void processTimeout(TimeoutEvent timeoutEvent) {

    }

    @Override
    public void processIOException(IOExceptionEvent exceptionEvent) {

    }

    @Override
    public void processTransactionTerminated(TransactionTerminatedEvent transactionTerminatedEvent) {

    }

    @Override
    public void processDialogTerminated(DialogTerminatedEvent dialogTerminatedEvent) {

    }
}
