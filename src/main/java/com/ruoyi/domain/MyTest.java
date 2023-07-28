package com.ruoyi.domain;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @TableName 记录心跳等
 */
@Data
@ToString
public class MyTest implements Serializable {
    /**
     * 设备id
     */
    private String deviceId;

    /**
     * 发送请求
     *
     * @param deviceId 设备id
     * @param type     类型
     */
    public MyTest(String deviceId, String type) {
        this.deviceId = deviceId;
        this.type = type;
        this.http();
    }

    /**
     * 类型[1第一次注册,2注册成功,3设备心跳,4设备掉线]
     */
    private String type;

    private static final long serialVersionUID = 1L;

    public boolean http() {
        try {
            String post = HttpUtil.post("http://127.0.0.1:258/t1", JSON.toJSONString(this));
            String rs = new String(post.getBytes());
            return Boolean.parseBoolean(rs);
        } catch (Exception e) {
            return false;
        }
    }
}
