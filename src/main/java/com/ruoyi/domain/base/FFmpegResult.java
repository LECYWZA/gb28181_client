package com.ruoyi.domain.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * FFmpeg 执行命令结果
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FFmpegResult {

    /**
     * 是否成功
     */
    private boolean success;
    /**
     * 返回文件名称
     */
    private String pathFileName;

    /**
     * 结果
     */
    private String msg;

    /**
     * 创建结果集对象
     *
     * @param success 执行结果
     * @param msg     控制台消息
     */
    public static FFmpegResult rs(boolean success, String msg) {
        return new FFmpegResult(success, null,msg);
    }
}
