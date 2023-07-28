package com.ruoyi.subscribe.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import javax.sip.ResponseEvent;

/**
 * 设备消息事件 [创建该对象并使用EventPublisher发布]
 */
@Getter
public class SipAckEvent extends ApplicationEvent {

    /**
     * 事件内容
     */
    private final ResponseEvent evt;

    /**
     * @param evt SIP请求内容对象
     */
    public SipAckEvent(ResponseEvent evt) {
        super(evt);
        this.evt = evt;
    }

}
