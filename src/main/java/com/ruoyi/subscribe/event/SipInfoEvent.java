package com.ruoyi.subscribe.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import javax.sip.ResponseEvent;

/**
 * 暂时只是录像跳进度等消息 [创建该对象并使用EventPublisher发布]
 */
@Getter
public class SipInfoEvent extends ApplicationEvent {

    /**
     * 事件内容
     */
    private final ResponseEvent evt;

    /**
     * @param evt SIP请求内容对象
     */
    public SipInfoEvent(ResponseEvent evt) {
        super(evt);
        this.evt = evt;
    }

}
