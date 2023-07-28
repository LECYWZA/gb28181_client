package com.ruoyi.subscribe.execute_event;

import com.ruoyi.domain.MyTest;
import com.ruoyi.sip_server.config.SipConfig;
import com.ruoyi.sip_server.tools.SipAuthUtil;
import com.ruoyi.subscribe.event.SipRegisterEvent;
import com.ruoyi.utils.SipCmdUtil;
import com.ruoyi.utils.SipUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.sip.SipFactory;

/**
 * 注册事件执行
 */
@Slf4j
@Component
public class SipRegisterEventExecute implements ApplicationListener<SipRegisterEvent> {


    @Autowired
    private SipConfig sipConfig;

    @Autowired
    private SipFactory sipFactory;



    @Autowired
    private SipAuthUtil sipAuthUtil;

    @Autowired
    private SipUtil sipUtil;

    @Autowired
    private SipCmdUtil sipCmdUtil;
    @Autowired
    private TaskExecutor my;

    @Override
    @Async("my")
    public void onApplicationEvent(SipRegisterEvent evt) {

        if (evt.isLogOut()){
            sipCmdUtil.logOut(evt.getD());
        }else {
            // 发送注册
            sipCmdUtil.sendRegister(evt.getD(),null,null);
            new MyTest(evt.getD().getDeviceId(), "发送注册请求");

        }

    }
}
