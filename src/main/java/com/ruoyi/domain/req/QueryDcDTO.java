package com.ruoyi.domain.req;

import com.ruoyi.domain.base.Pagination;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 查询设备列表
 */
@Data
@ApiModel(value = "QueryDcDTO", description = "设备通道查询条件")
public class QueryDcDTO extends Pagination implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 在线离线
     */
    @ApiModelProperty(value = "1在线,0离线,不传都查", example = "0")
    private String isOnlie;

    /**
     * 设备id
     */
    @ApiModelProperty(value = "设备id[查询通道列表需要传]", example = "11111111111111111111")
    private String deviceId;

}
