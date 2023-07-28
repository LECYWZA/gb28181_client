package com.ruoyi.domain.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "PreviewDTO", description = "实时预览/录像查看,请求体")
public class PreviewDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 设备id
     */
    @ApiModelProperty(value = "设备id", required = true, example = "44010200492000000009")
    private String deviceId;

    /**
     * 通道id
     */
    @ApiModelProperty(value = "通道id", required = true, example = "44010200492000000008")
    private String channelId;

    /**
     * 查看类型
     */
    @ApiModelProperty(required = true,value = "类型 [0实时预览 | 1录像回放]")
    private int type = 0;

    /**
     * 录像回放时拼接
     */
    @ApiModelProperty(hidden = true)
    private String ssrc;

    /**
     * 录像播放开始时间
     */
    @ApiModelProperty(value = "开始时间[type=1时必填]", example = "yyyy-MM-dd HH:mm:ss")
    private String startTime;

    /**
     * 录像播放结束时间
     */
    @ApiModelProperty(value = "结束时间[type=1时必填]", example = "yyyy-MM-dd HH:mm:ss")
    private String endTime;

    /**
     * 获取拼接后的地址[如果是录像回放会获取SSRC作为结尾]
     * 设备id_通道id
     *
     * @return
     */
    @ApiModelProperty(hidden = true)
    public String getDcAddr() {
        if (type == 1) {
            return String.format("%s_%s_%s", deviceId, channelId, ssrc);
        }
        return String.format("%s_%s", deviceId, channelId);
    }
}
