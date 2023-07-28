package com.ruoyi.subscribe.execute_event;

import com.ruoyi.subscribe.event.TestEventObj;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 事件执行
 */
@Slf4j
@Component
public class TestEvent implements ApplicationListener<TestEventObj> {

    @Override
    @Async("my")
    public void onApplicationEvent(TestEventObj event) {
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("异步执行: {}", event.getSource());
    }
}
