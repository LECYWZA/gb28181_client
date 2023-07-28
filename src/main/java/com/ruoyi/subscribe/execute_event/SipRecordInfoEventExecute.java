package com.ruoyi.subscribe.execute_event;

import com.ruoyi.delayed_task.DelayQueueManager;
import com.ruoyi.delayed_task.DelayTask;
import com.ruoyi.domain.base.Prefix;
import com.ruoyi.domain.base.R;
import com.ruoyi.domain.resp.RecordInfos;
import com.ruoyi.sip_server.config.SIPLink;
import com.ruoyi.subscribe.event.SipRecordInfoEvent;
import com.ruoyi.utils.SipUtil;
import com.ruoyi.utils.XMLUtil;
import gov.nist.javax.sip.header.From;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.DeferredResult;

import javax.sip.RequestEvent;
import javax.sip.message.Request;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 收到录像结果
 */
@Slf4j
@Component
public class SipRecordInfoEventExecute implements ApplicationListener<SipRecordInfoEvent> {

    /**
     * 已经同步的录像数
     * key = value
     * 设备_通道:SN=已同步录像数
     * 全部录像接收完成后,响应前端并删除该集合中的录像
     */
    public final static Map<String, RecordInfos> RECORDINFOSIZE = new ConcurrentHashMap<>();

    @Autowired
    private SipUtil sipUtil;
    @Autowired
    private DelayQueueManager delayQueueManager;



    @Override
    // @Async("my")
    public void onApplicationEvent(SipRecordInfoEvent recordInfo) {
        RequestEvent evt = recordInfo.getEvt();
        Request request = evt.getRequest();
        try {
            // 获取设备id
            String to = request.getHeader(From.NAME).toString();
            String deviceId = to.substring(to.indexOf(":", to.indexOf(":") + 1) + 1, to.indexOf("@"));

            // 获取SN
            // 设置转换编码 TODO
            String xmlString = new String(request.getRawContent(), Charset.forName("GB2312"));
            JSONObject response = XML.toJSONObject(xmlString);
            // 获取响应
            JSONObject json = response.getJSONObject("Response");
            // 获取SN
            int sn = json.getInt("SN");
            String channelId = json.getString("DeviceID");
            // 拼接Key 设备id_通道id:SN
            String dcAddrSN = String.format("%s_%s_%s", deviceId, channelId, sn);

            // 不存在则增加延时任务
            if (!delayQueueManager.isExistence(Prefix.recordInfoResult, dcAddrSN)) {
                // 延时执行 5秒
                delayQueueManager.put(new DelayTask(Prefix.recordInfoResult, dcAddrSN, 5000L, () -> {
                    // 删除接收不完整的录像
                    RECORDINFOSIZE.remove(dcAddrSN);
                    log.error("\n延时任务: 接收设备发送的录像结果超时,删除接收不完整的录像[{}]", dcAddrSN);
                }
                ));
            }
            // 同步方法
            RecordInfos newRs = collectRecordInfo(dcAddrSN, deviceId, null, request.getRawContent());
            // 当前数量
            int currentSize = newRs.getRs().size();
            // 总数量
            int countSize = newRs.getSize();
            log.info("收集录像录像数{}: {}/{}", dcAddrSN, currentSize, countSize);
            // 当前数量 == 总数量 [录像接收完成]
            if (currentSize == countSize) {
                // 获取中转站中的MVC异步响应对象
                DeferredResult<R<Object>> result = SIPLink.ASYNCRESPONSE.get(Prefix.recordInfoResult + dcAddrSN);
                log.info("result为{}", result);

                // 删除延时任务
                delayQueueManager.remove(Prefix.recordInfoResult, dcAddrSN);
                // 删除已经响应给前端的录像
                RECORDINFOSIZE.remove(dcAddrSN);
                // 移除中转站中的异步响应对象
                SIPLink.ASYNCRESPONSE.remove(Prefix.recordInfoResult + dcAddrSN);

                // 响应前端 可能出现为null的情况, 所以放到最后执行
                result.setResult(R.success(newRs));
            }
        } catch (Exception e) {
            //noinspection PlaceholderCountMatchesArgumentCount
            log.error("\n接收录像结果处理失败: {}", e);
            e.printStackTrace();
        }
    }


    /**
     * 同步方法
     * @param dcAddrSN   设备id_通道id_SN [唯一标识]
     * @param deviceId   设备id
     * @param enCode     编码方式[传null 默认GB2312]
     * @param recordInfo byte[] XML文本
     * @return
     */
    private synchronized RecordInfos collectRecordInfo(String dcAddrSN, String deviceId, String enCode, byte[] recordInfo) {
        // 加SN
        RecordInfos rs = RECORDINFOSIZE.get(dcAddrSN);
        // XML 转 对象
        RecordInfos newRs = XMLUtil.getXMLToRecordInfo(rs, deviceId, enCode, recordInfo);
        // 更新
        RECORDINFOSIZE.put(dcAddrSN, newRs);
        return newRs;
    }

}
