package com.ruoyi.web.controller;

import com.ruoyi.domain.base.R;
import com.ruoyi.media.config.ZLMediaKitConfig;
import com.ruoyi.subscribe.EventPublisher;
import com.ruoyi.subscribe.event.TestEventObj;
import com.ruoyi.utils.SipCmdUtil;
import com.ruoyi.utils.ZLMediaKitHttpUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试接口
 */
@Slf4j
@RestController
@RequestMapping("test")
@Api(tags = "测试接口")
public class TestController {

    @Autowired
    private SipCmdUtil sipCmdUtil;


    @Autowired
    private ZLMediaKitConfig kitConfig;
    @Autowired
    private ZLMediaKitHttpUtil kitHttpUtil;
    
    @Autowired
    private EventPublisher eventPublisher;


    /**
     * 测试事件发布
     */
    @ApiOperation("测试事件发布")
    @GetMapping("event-push")
    public R<String> eventPush(String dcAddress) {
        for (int i = 0; i < 1000; i++) {
            eventPublisher.eventPush(new TestEventObj(dcAddress + ": " + i));
        }
        return R.success();
    }
}
