package com.ruoyi.subscribe.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import javax.sip.ResponseEvent;

/**
 * 注册响应
 */
@Getter
public class SipRegisterRespEvent extends ApplicationEvent {

    /**
     * 事件内容
     */
    private ResponseEvent evt;

    /**
     * @param evt 设备对象
     */
    public SipRegisterRespEvent(ResponseEvent evt) {
        super(evt);
        this.evt = evt;
    }

}
