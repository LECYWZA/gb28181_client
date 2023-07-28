package com.ruoyi.delayed_task;

import org.springframework.scheduling.annotation.Async;

/**
 * 自定义逻辑
 */
public interface DelayExecute {

    /**
     * 执行 TODO 异步
     */
    @Async("my")
    void execute() ;
}
