package com.ruoyi.web.controller.zlmediakit;

import com.ruoyi.domain.base.R;
import com.ruoyi.media.config.ZLMediaKitConfig;
import com.ruoyi.subscribe.EventPublisher;
import com.ruoyi.utils.SipCmdUtil;
import com.ruoyi.utils.ZLMediaKitHttpUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 获取操作流媒体,查询流媒体信息
 */
@Slf4j
@RestController
@RequestMapping("zlm")
@Api(tags = "操作流媒体")
public class ZLMController {

    @Autowired
    private SipCmdUtil sipCmdUtil;

    
    
    @Autowired
    private ZLMediaKitConfig kitConfig;
    @Autowired
    private ZLMediaKitHttpUtil kitHttpUtil;
    
    @Autowired
    private EventPublisher eventPublisher;



    /**
     * 重启流媒体
     */
    @ApiOperation("重启流媒体")
    @ApiImplicitParams(@ApiImplicitParam(
            name = "id", required = true, value = "流媒体id[不传重启默认流媒体]",
            example = "1", dataTypeClass = long.class))
    @GetMapping("restart")
    public R<Boolean> deleteChannel(String id) {
        boolean restart;
        if (StringUtils.hasText(id)) {
            // TODO 流媒体如果做集群,根据id重启哪一台[目前没做]
            restart = kitHttpUtil.restart(kitConfig.getDefaultZLMediaKit());
        } else {
            restart = kitHttpUtil.restart(kitConfig.getDefaultZLMediaKit());
        }
        return R.success(restart);
    }




}
