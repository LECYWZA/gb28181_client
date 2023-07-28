package com.ruoyi.domain.base;


import cn.hutool.core.lang.UUID;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 成功
     */
    public static final int SUCCESS = 200;
    /**
     * 操作成功
     */
    public static final String SUCCESSMSG = "操作成功";
    /**
     * 失败
     */
    public static final int FAIL = 500;
    /**
     * 操作失败
     */
    public static final String FAILMSG = "操作失败";


    public R(int code, Boolean close, String msg, T data) {
        this.code = code;
        this.close = close;
        this.msg = msg;
        this.data = data;
    }


    /**
     * 状态码
     */
    @ApiModelProperty(value = "状态码", example = "200")
    private int code;

    /**
     * 流媒体无人观看是否关流
     */
    @ApiModelProperty(hidden = true)
    private Boolean close;

    @ApiModelProperty(value = "时间", example = "2021-11-12 19:35:45")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dateTime = new Date();

    @ApiModelProperty(value = "响应UUID[区分请求,无其他用处]")
    private String id = UUID.randomUUID().toString();

    /**
     * 消息
     */
    @ApiModelProperty(value = "消息", example = "操作成功")
    private String msg;

    /**
     * 数据
     */
    private T data;

    public R(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }


    /**
     * 操作成功
     *
     * @param <T>
     * @return
     */
    public static <T> R<T> success() {
        return getJsonResult(SUCCESS, SUCCESSMSG, null);
    }


    /**
     * ZLM 专属
     *
     * @param <T>
     * @return
     */
    public static <T> R<T> respZLMediaKit() {
        return getJsonResult(0, "success", null);
    }

    /**
     * 操作成功
     *
     * @param msg 消息
     * @return
     */
    public static <T> R<T> success(String msg) {
        return getJsonResult(SUCCESS, msg, null);
    }

    /**
     * 操作成功
     *
     * @param data 数据
     * @return
     */
    public static <T> R<T> success(T data) {
        return getJsonResult(SUCCESS, SUCCESSMSG, data);
    }

    /**
     * 操作成功
     *
     * @param msg  消息
     * @param data 数据
     * @return
     */
    public static <T> R<T> success(String msg, T data) {
        return getJsonResult(SUCCESS, msg, data);
    }


    /**
     * 操作失败
     *
     * @param <T>
     * @return
     */
    public static <T> R<T> fail() {
        return getJsonResult(FAIL, FAILMSG, null);
    }

    /**
     * 操作失败
     *
     * @param msg 消息
     * @return
     */
    public static <T> R<T> fail(String msg) {
        return getJsonResult(FAIL, msg, null);
    }

    /**
     * 操作失败
     *
     * @param data 数据
     * @return
     */
    public static <T> R<T> fail(T data) {
        return getJsonResult(FAIL, FAILMSG, data);
    }

    /**
     * 操作失败
     *
     * @param msg  消息
     * @param data 数据
     * @return
     */
    public static <T> R<T> fail(String msg, T data) {
        return getJsonResult(FAIL, msg, data);
    }


    /**
     * 获取JSONResult
     *
     * @param code 状态码
     * @param msg  消息
     * @param data 数据
     * @param <T>
     * @return JsonResult对象
     */
    private static <T> R<T> getJsonResult(int code, String msg, T data) {
        return new R<>(code, msg, data);
    }


}
