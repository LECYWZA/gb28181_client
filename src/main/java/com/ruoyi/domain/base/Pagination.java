package com.ruoyi.domain.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 分页参数基类
 */
@Data
public class Pagination {

    /**
     * 当前页
     */
    @ApiModelProperty(value = "当前页",example = "1")
    private Integer currentPage;

    /**
     * 当前页
     */
    @ApiModelProperty(value = "每页条数",example = "10")
    private Integer pageSize;

}
