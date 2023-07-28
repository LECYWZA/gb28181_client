package com.ruoyi.subscribe.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import javax.sip.RequestEvent;

/**
 * 设备响应录像事件类 [创建该对象并使用EventPublisher发布]
 */
@Getter
public class SipRecordInfoEvent extends ApplicationEvent {

    /**
     * 事件内容
     */
    private final RequestEvent evt;

    /**
     * @param evt SIP请求内容对象
     */
    public SipRecordInfoEvent(RequestEvent evt) {
        super(evt);
        this.evt = evt;
    }

}
