package com.ruoyi.domain.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data // TODO 未使用
@ApiModel(value = "SyncDTO", description = "查询录像信息")
public class SyncDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 设备id_通道id
     */
    @ApiModelProperty(value = "设备id_通道id",example = "10000000000000000001")
    private String dcAddress;

    /**
     * 录像查询筛选开始时间
     */
    @ApiModelProperty(value = "结束时间",example = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /**
     * 录像查询筛选结束时间
     */
    @ApiModelProperty(value = "结束时间",example = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

}
