package com.ruoyi.domain.resp;

import com.ruoyi.domain.sip.RecordInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 录像集合
 */
@Data
@ApiModel(value = "RecordInfos", description = "录像集合")
public class RecordInfos {

    /**
     * 结果录像总数
     */
    @ApiModelProperty(value = "结果录像总数",example = "10")
    private int size = 0;
    /**
     * 录像集合
     */
    private List<RecordInfo> rs;
}
