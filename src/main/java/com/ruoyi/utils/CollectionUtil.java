package com.ruoyi.utils;

import java.util.Collection;

public class CollectionUtil {

    /**
     * 集合不为空,Size 大于 0
     *
     * @param list
     * @param <T>
     * @return
     */
    public static <T> boolean isNotNull(Collection<T> list) {
        return list != null && list.size() > 0;
    }

    /**
     * 集合等于null 或者 size ==0
     *
     * @param list
     * @param <T>
     * @return
     */
    public static <T> boolean isNull(Collection<T> list) {
        return list == null || list.size() == 0;
    }
}
