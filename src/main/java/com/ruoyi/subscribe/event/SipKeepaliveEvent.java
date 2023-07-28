package com.ruoyi.subscribe.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import javax.sip.ResponseEvent;

/**
 * 心跳响应
 */
@Getter
public class SipKeepaliveEvent extends ApplicationEvent {


private ResponseEvent evt;
    /**
     * @param evt SIP请求内容对象
     */
    public SipKeepaliveEvent(ResponseEvent evt) {
        super(evt);
        this.evt = evt;
    }

}
