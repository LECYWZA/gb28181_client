package com.ruoyi.subscribe.execute_event;

import com.ruoyi.domain.sip.SipSession;
import com.ruoyi.subscribe.event.SipAckEvent;
import com.ruoyi.utils.SipUtil;
import com.ruoyi.utils.ZLMediaKitHttpUtil;
import gov.nist.javax.sip.header.To;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.sip.ClientTransaction;
import javax.sip.Dialog;
import javax.sip.ResponseEvent;
import javax.sip.header.CSeqHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;
import java.util.Properties;

/**
 * 通知设备推流
 * [普联 TP-Link 就算没通知设备也会开始推流]
 */
@Slf4j
@Component
public class SipAckEventExecute implements ApplicationListener<SipAckEvent> {
    @Autowired
    private SipUtil sipUtil;



    @Autowired
    private ZLMediaKitHttpUtil httpUtil;

    @Override
    // @Async("my")
    public void onApplicationEvent(SipAckEvent ackEvent) {
        ResponseEvent evt = ackEvent.getEvt();
        Response response = evt.getResponse();

        try {
            int statusCode = response.getStatusCode();
            //100trying不会回复
            if (statusCode == Response.TRYING) {
                log.info("100trying不会回复");
            }
            //成功响应
            //下发ack
            if (statusCode == Response.OK) {
                ClientTransaction clientTransaction = evt.getClientTransaction();
                if (clientTransaction == null) {
                    log.error("回复ACK时，clientTransaction为null");
                    return;
                }
                Dialog clientDialog = clientTransaction.getDialog();

                CSeqHeader clientCSeqHeader = (CSeqHeader) response.getHeader(CSeqHeader.NAME);
                long cseqId = clientCSeqHeader.getSeqNumber();
                /*
                    createAck函数，创建的ackRequest，会采用Invite响应的200OK，中的contact字段中的地址，作为目标地址。
                    有的终端传上来的可能还是内网地址，会造成ack发送不出去。接受不到音视频流
                    所以在此处统一替换地址。和响应消息的Via头中的地址保持一致。
                 */
                Request ackRequest = clientDialog.createAck(cseqId);
                // SipURI requestURI = (SipURI) ackRequest.getRequestURI();
                // ViaHeader viaHeader = (ViaHeader) response.getHeader(ViaHeader.NAME);
                // 大坑
                /*requestURI.setHost(viaHeader.getHost());
                requestURI.setPort(viaHeader.getPort());*/
                clientDialog.sendAck(ackRequest);

                // 需要保存起来
                // 获取设备id
                String uri = ackRequest.getRequestURI().toString();
                String deviceId = uri.substring(uri.indexOf(":") + 1, uri.indexOf("@"));
                // 获取通道id
                String to = response.getHeader(To.NAME).toString();
                String channelId = to.substring(to.indexOf(":", to.indexOf(":") + 1) + 1, to.indexOf("@"));
                // 拼接流地址
                String streamId = String.format("%s_%s", deviceId, channelId);

                // 获取SDP
                Properties p = sipUtil.getProperties(response.getRawContent());
                if (p != null) {
                    String ssrc = p.getProperty("y");
                    if (StringUtils.hasText(ssrc)) {
                        // 存储 Dialog
                        SipSession.mapDialog.put(ssrc, clientDialog);
                        // 获取点播时的SipSession对象
                        // 判断是否是录像查看
                        if (ssrc.startsWith("1")) streamId = String.format("%s_%s", streamId, ssrc);
                       /* String json = redisUtil.getString(Prefix.sipStream, streamId);
                        if (StringUtils.hasText(json)) {
                            SipSession session = JSON.parseObject(json, SipSession.class);
                            if (session != null) {
                                session.setProperties(p);
                                String updateSession = JSON.toJSONStringWithDateFormat(session, "yyyy-MM-dd HH:mm:ss");
                                // 存储进 Redis
                                redisUtil.setString(Prefix.sipStream, streamId, updateSession, 10);
                            }
                        }*/
                    }
                }
            }
        } catch (Exception err) {
            err.printStackTrace();
        }


    }
}
