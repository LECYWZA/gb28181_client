package com.ruoyi.web.controller.gb28181;

import com.ruoyi.domain.base.R;
import com.ruoyi.media.config.ZLMediaKitConfig;
import com.ruoyi.sip_server.config.DeviceInit;
import com.ruoyi.sip_server.config.SSRCConfig;
import com.ruoyi.subscribe.EventPublisher;
import com.ruoyi.subscribe.event.SipRegisterEvent;
import com.ruoyi.utils.SipCmdUtil;
import com.ruoyi.utils.ZLMediaKitHttpUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 模拟国标
 */
@Slf4j
@RestController
@RequestMapping("gb-client")
@Api(tags = "模拟国标")
public class GB28181Controller {

    @Autowired
    private SipCmdUtil sipCmdUtil;

    @Autowired
    private ZLMediaKitConfig kitConfig;
    @Autowired
    private ZLMediaKitHttpUtil kitHttpUtil;

    @Autowired
    private SSRCConfig ssrcConfig;

    @Autowired
    private EventPublisher eventPublisher;


    /**
     * 设备注册/注销
     */
    @ApiOperation("0注册/1注销")
    @PostMapping("cmd")
    public R<String> cmd(int type) {
        if (type == 0) {
            // 注册
            DeviceInit.ds.values().forEach(x -> {
                        log.info("设备数: {}", DeviceInit.ds.keySet().size());
                        eventPublisher.eventPush(new SipRegisterEvent(x));
                        log.info("{} 发起注册", x.getDeviceName());
                    }
            );
            DeviceInit.isRegister = true;
            return R.success("注册中");
        } else {
            // 注销
            DeviceInit.ds.values().forEach(x -> eventPublisher.eventPush(new SipRegisterEvent(x, true)));
            DeviceInit.isRegister = false;
            return R.success("注销中");
        }

    }


}
