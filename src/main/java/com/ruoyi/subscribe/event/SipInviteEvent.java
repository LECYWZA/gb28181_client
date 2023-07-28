package com.ruoyi.subscribe.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import javax.sip.RequestEvent;

/**
 * 设备推流事件
 */
@Getter
public class SipInviteEvent extends ApplicationEvent {

    /**
     * 事件内容
     */
    private final RequestEvent evt;

    /**
     * @param evt SIP请求内容对象
     */
    public SipInviteEvent(RequestEvent evt) {
        super(evt);
        this.evt = evt;
    }

}
