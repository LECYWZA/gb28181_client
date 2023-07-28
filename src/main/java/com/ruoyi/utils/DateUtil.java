package com.ruoyi.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

    // 带T时间格式
    public final static String dateT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";

    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(yyyy_MM_dd_HH_mm_ss, Locale.getDefault()).withZone(ZoneId.systemDefault());


    /**
     * 转换为时间戳[用于录像回放]
     * @param data yyyy-MM-dd HH:mm:ss 字符串
     * @return
     */
    public static long getDateTimestamp(String data) {
        TemporalAccessor temporalAccessor = formatter.parse(data);
        Instant instant = Instant.from(temporalAccessor);
        return instant.getEpochSecond();
    }


    /**
     * 获取指定日期的起始时间[用于查询录像]
     *
     * @param data 不传为当天
     * @param i    传0时分秒都为0,传其它均为为23:59:59
     * @return
     */
    public static String getDateSTAEND(Date data, int i) {
        Calendar c = cn.hutool.core.date.DateUtil.calendar(data == null ? new Date() : data);
        if (i == 0) {
            // 时
            c.set(Calendar.HOUR_OF_DAY, 0);
            // 分
            c.set(Calendar.MINUTE, 0);
            // 秒
            c.set(Calendar.SECOND, 0);
            // 毫秒
            c.set(Calendar.MILLISECOND, 0);
            return cn.hutool.core.date.DateUtil.format(c.getTime(), dateT);
        } else {
            // 当天最后
            // 时
            c.set(Calendar.HOUR_OF_DAY, 23);
            // 分
            c.set(Calendar.MINUTE, 59);
            // 秒
            c.set(Calendar.SECOND, 59);
            // 毫秒
            c.set(Calendar.MILLISECOND, 59);
            return cn.hutool.core.date.DateUtil.format(c.getTime(), dateT);
        }
    }

    /**
     * 获取带T的时间
     *
     * @param data 时间
     * @return
     */
    public static String getDate(Date data) {
        return cn.hutool.core.date.DateUtil.format(data, dateT);
    }
}
