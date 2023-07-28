import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

@Slf4j
public class 设置时分秒 {

    @Test
    public void test() {
        Calendar c = DateUtil.calendar(new Date());
        // 时
        c.set(Calendar.HOUR_OF_DAY, 0);
        // 分
        c.set(Calendar.MINUTE, 0);
        // 秒
        c.set(Calendar.SECOND, 0);
        // 毫秒
        c.set(Calendar.MILLISECOND, 0);
        System.out.println(DateUtil.format(c.getTime(), "yyyy-MM-dd'T'HH:mm:ss"));
        // 当天最后
        // 时
        c.set(Calendar.HOUR_OF_DAY, 23);
        // 分
        c.set(Calendar.MINUTE, 59);
        // 秒
        c.set(Calendar.SECOND, 59);
        // 毫秒
        c.set(Calendar.MILLISECOND, 59);
        System.out.println(DateUtil.format(c.getTime(), "yyyy-MM-dd'T'HH:mm:ss"));

    }
    @Test
    public void test1() {
        System.out.println(UUID.randomUUID(true));
        System.out.println(UUID.randomUUID());
        System.out.println(UUID.randomUUID(true));
    }

}
