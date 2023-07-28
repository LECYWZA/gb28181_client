package com.ruoyi.utils;


public class SystemUtils {

    /**
     * 系统类型
     */
    public enum SystemType {
        Win,Linux,Else
    }

    /**
     * 获取操作系统环境
     *
     * @return win|linux|else
     */
    public static SystemType getSystem() {
        String os = System.getProperty("os.name");
        //Windows操作系统
        if (os != null && os.toLowerCase().startsWith("windows")) {
            return SystemType.Win;
        } else if (os != null && os.toLowerCase().startsWith("linux")) {//Linux操作系统
            return SystemType.Linux;
        }
        //其它操作系统
        return SystemType.Else;

    }


}
