package com.ruoyi.subscribe.event;

import com.ruoyi.domain.Device;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 设备消息事件 [创建该对象并使用EventPublisher发布]
 */
@Getter
public class SipRegisterEvent extends ApplicationEvent {

    /**
     * 事件内容
     */
    private Device d;
    /**
     * 是否注销
     */
    private boolean logOut;


    /**
     * @param d 设备对象
     */
    public SipRegisterEvent(Device d) {
        this(d, false);
    }

    public SipRegisterEvent(Device d, boolean logOut) {
        super(d);
        this.d = d;
        this.logOut = logOut;
    }
}
