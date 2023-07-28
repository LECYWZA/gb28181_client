package com.ruoyi.subscribe.event;

import org.springframework.context.ApplicationEvent;

/**
 * 事件类[创建该对象并使用EventPublisher发布]
 */
public class TestEventObj extends ApplicationEvent {

    private Object source;

    // 创建事件对象要执行用到的业务数据数据
    public TestEventObj(Object source) {
        super(source);
        this.source = source;
    }

}
