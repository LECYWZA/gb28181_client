package com.ruoyi.domain.sip;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 对应一个XML一个Item
 */
@Data
@ApiModel(value = "RecordInfo", description = "一个对象对应一个录像")
public class RecordInfo {

    /**
     * 设备id
     */
    @ApiModelProperty(value = "设备id",example = "10000000000000000001")
    private String deviceId;

    /**
     * 通道id
     */
    @JSONField(name = "DeviceID")
    @ApiModelProperty(value = "通道id",example = "10000000000000000001")
    private String channelId;
    /**
     * 文件路径
     */
    @JSONField(name = "FilePath")
    @ApiModelProperty(value = "文件路径",example = "file_path")
    private String filePath;

    /**
     * 通道[摄像头]名称
     */
    @JSONField(name = "Name")
    @ApiModelProperty(value = "通道[摄像头]名称",example = "IPCamera 01")
    private String name;

    /**
     * 录像地址，支持不完全查询
     */
    @JSONField(name = "Address")
    @ApiModelProperty(value = "录像地址",example = "Address 1")
    private String address;

    /**
     * 开始时间
     */
    @JSONField(name = "StartTime")
    @ApiModelProperty(value = "开始时间",example = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /**
     * 结束时间
     */
    @JSONField(name = "EndTime")
    @ApiModelProperty(value = "结束时间",example = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    /**
     * 默认为0代表，不涉密
     */
    @JSONField(name = "Secrecy")
    @ApiModelProperty(value = "默认为0，不涉密",example = "0")
    private Integer secrecy;

    /**
     * 录像产生类型(可选)time 或alarm 或 manual 或all
     */
    @JSONField(name = "Type")
    @ApiModelProperty(value = "录像产生类型[time,alarm,manual,all]",example = "all")
    private String type;

}
