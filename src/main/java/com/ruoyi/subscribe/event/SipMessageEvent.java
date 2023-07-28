package com.ruoyi.subscribe.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import javax.sip.RequestEvent;

/**
 * 设备消息事件 [创建该对象并使用EventPublisher发布]
 */
@Getter
public class SipMessageEvent extends ApplicationEvent {

    /**
     * 事件内容
     */
    private final RequestEvent evt;

    /**
     * @param evt SIP请求内容对象
     */
    public SipMessageEvent(RequestEvent evt) {
        super(evt);
        this.evt = evt;
    }

}
