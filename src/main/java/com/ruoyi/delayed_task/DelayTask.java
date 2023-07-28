package com.ruoyi.delayed_task;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import com.ruoyi.domain.base.Prefix;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 延时任务
 */
@Data
@Slf4j
public class DelayTask implements Delayed {


    /**
     * 任务的延时时间，单位毫秒
     */
    private long expire;
    /**
     * 实现类
     */
    private DelayExecute execute;
    /**
     * 任务标识
     */
    private String id;

    /**
     * 构造延时任务
     *
     * @param expire 任务延时时间毫秒（ms）
     * @param exec   要执行的业务逻辑
     */
    public DelayTask(Prefix prefix, String id, long expire, DelayExecute exec) {
        // log.info("\n创建延时任务 [{}] [{}]", prefix + id, exec);
        this.id = prefix + id;
        this.expire = expire + System.currentTimeMillis();
        this.execute = exec;
    }

    /**
     * 构造延时任务 id 默认为当前时间
     *
     * @param expire 任务延时时间毫秒（ms）
     * @param exec   要执行的业务逻辑
     */
    public DelayTask(long expire, DelayExecute exec) {
        this(Prefix.uuid,String.format("%s-%s", UUID.fastUUID(), DateUtil.formatDateTime(new Date())), expire, exec);
    }

    /**
     * TimeUnit.DAYS    天
     * TimeUnit.HOURS   小时
     * TimeUnit.MINUTES 分钟
     * TimeUnit.SECONDS 秒
     * TimeUnit.MILLISECONDS 毫秒
     * TimeUnit.NANOSECONDS  毫微秒
     * TimeUnit.MICROSECONDS 微秒
     * 剩余触发时间
     *
     * @param unit
     * @return
     */
    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(this.expire - System.currentTimeMillis(), unit);
    }

    @Override
    public int compareTo(Delayed o) {
        long delta = getDelay(TimeUnit.NANOSECONDS) - o.getDelay(TimeUnit.NANOSECONDS);
        return (int) delta;
    }
}
