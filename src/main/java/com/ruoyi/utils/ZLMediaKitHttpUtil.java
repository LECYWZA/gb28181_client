package com.ruoyi.utils;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.domain.ZLMediaKit;
import com.ruoyi.media.config.ZLMediaKitConfig;
import com.ruoyi.sip_server.config.SipConfig;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 流媒体服务器配置类
 *
 * @TableName zlmediakit
 */
@Data
@Slf4j
@Component
public class ZLMediaKitHttpUtil {

    @Autowired
    private SipConfig sipConfig;
    @Autowired
    private ZLMediaKitConfig zlMediaKitConfig;


    public String isHttps(boolean isHttps) {
        return isHttps ? "https://" : "http://";
    }

    /**
     * 参数不为空则设置
     *
     * @param map    请求体
     * @param key    ZLM配置key
     * @param config zlm key的value
     */
    private void setZLMConfig(Map<String, Object> map, String key, String config) {
        if (StringUtils.hasText(config)) {
            map.put(key, config);
        }
    }


    /**
     * 获取收流超时实际,此时间为 GeneralMaxStreamWaitMs的80%
     *
     * @param zlm
     * @return
     */
    public long getPlayOvertimeTime(ZLMediaKit zlm) {
        double time = Integer.parseInt(zlm.getGeneralMaxStreamWaitMs()) * 0.8;
        return (long) time;
    }

    /**
     * 获取流地址
     *
     * @param zlm      媒体流服务器
     * @param app      应用名称
     * @param streamId 流id
     * @return
     */
    public Map<String, String> getStreamUrls(ZLMediaKit zlm, String app, String streamId) {
        String streamIp = zlm.getHttpStreamIp();
        Map<String, String> urls = new HashMap<>();
        urls.put("id", zlm.getGeneralMediaServerId());
        urls.put("streamId", streamId);
        urls.put("rtmp", String.format("rtmp://%s:%s/%s/%s", streamIp, zlm.getRtmpPort(), app, streamId));
        if (!"0".equals(zlm.getRtmpSslPort())) {
            urls.put("rtmps", String.format("rtmps://%s:%s/%s/%s", streamIp, zlm.getRtmpSslPort(), app, streamId));
        }
        urls.put("rtsp", String.format("rtsp://%s:%s/%s/%s", streamIp, zlm.getRtspPort(), app, streamId));
        if (!"0".equals(zlm.getRtspSslPort())) {
            urls.put("rtsps", String.format("rtsps://%s:%s/%s/%s", streamIp, zlm.getRtspSslPort(), app, streamId));
        }
        urls.put("http-flv", String.format("http://%s:%s/%s/%s.live.flv", streamIp, zlm.getHttpPort(), app, streamId));
        urls.put("ws-flv", String.format("ws://%s:%s/%s/%s.live.flv", streamIp, zlm.getHttpPort(), app, streamId));
        urls.put("http-m3u8", String.format("http://%s:%s/%s/%s/hls.m3u8", streamIp, zlm.getHttpPort(), app, streamId));
        urls.put("ws-m3u8", String.format("ws://%s:%s/%s/%s/hls.m3u8", streamIp, zlm.getHttpPort(), app, streamId));
        urls.put("http-mp4", String.format("http://%s:%s/%s/%s.live.mp4", streamIp, zlm.getHttpPort(), app, streamId));
        urls.put("ws-mp4", String.format("ws://%s:%s/%s/%s.live.mp4", streamIp, zlm.getHttpPort(), app, streamId));
        urls.put("http-ts", String.format("http://%s:%s/%s/%s.live.ts", streamIp, zlm.getHttpPort(), app, streamId));
        urls.put("ws-ts", String.format("ws://%s:%s/%s/%s.live.ts", streamIp, zlm.getHttpPort(), app, streamId));
        if (!"0".equals(zlm.getHttpSslPort())) {
            urls.put("https-flv", String.format("https://%s:%s/%s/%s.live.flv", streamIp, zlm.getHttpSslPort(), app, streamId));
            urls.put("wss-flv", String.format("wss://%s:%s/%s/%s.live.flv", streamIp, zlm.getHttpSslPort(), app, streamId));
            urls.put("https-m3u8", String.format("https://%s:%s/%s/%s/hls.m3u8", streamIp, zlm.getHttpSslPort(), app, streamId));
            urls.put("wss-m3u8", String.format("wss://%s:%s/%s/%s/hls.m3u8", streamIp, zlm.getHttpSslPort(), app, streamId));
            urls.put("https-mp4", String.format("https://%s:%s/%s/%s.live.mp4", streamIp, zlm.getHttpSslPort(), app, streamId));
            urls.put("wss-mp4", String.format("wss://%s:%s/%s/%s.live.mp4", streamIp, zlm.getHttpSslPort(), app, streamId));
            urls.put("https-ts", String.format("https://%s:%s/%s/%s.live.ts", streamIp, zlm.getHttpSslPort(), app, streamId));
            urls.put("wss-ts", String.format("wss://%s:%s/%s/%s.live.ts", streamIp, zlm.getHttpSslPort(), app, streamId));
            urls.put("https-webrtc", String.format("https://%s:%s/index/api/webrtc?app=%s&streamId=%s&type=play", streamIp, zlm.getHttpSslPort(), app, streamId));
        }
        return urls;
    }

    /**
     * 判断请求结果
     *
     * @param object
     * @return
     */
    public boolean reqResult(JSONObject object) {
        if (object.getIntValue("code") == 0) {
            log.info("\n流媒体服务器响应成功");
            return true;
        }
        log.error("\n流媒体服务器响应失败: {}", object.toJSONString());
        return false;
    }

    /**
     * ZLM是否在线
     *
     * @param zlm zlm对象
     * @return
     */
    public boolean getApiList(ZLMediaKit zlm) {
        String url = String.format("%s%s/index/api/getApiList", isHttps(zlm.isHttps()), zlm.getHttpIp());
        String ret = HttpUtil.get(url);
        JSONObject object = JSONObject.parseObject(ret);
        Integer code = object.getInteger("code");
        return code != null && code.equals(0);
    }

    /**
     * 设置流媒体服务器
     *
     * @param zlm 流媒体服务器对象
     * @return
     */
    public JSONObject setZLMediaKitConfig(ZLMediaKit zlm) {
        try {
            HashMap<String, Object> map = new HashMap<>();
            // 设置认证信息
            map.put("secret", zlm.getApiSecret());
            // [api]
            this.setZLMConfig(map, "api.apiDebug", zlm.getApiDebug());
            this.setZLMConfig(map, "api.secret", zlm.getApiSecret());
            this.setZLMConfig(map, "api.snapRoot", zlm.getApiSnapRoot());
            this.setZLMConfig(map, "api.defaultSnap", zlm.getApiDefaultSnap());
            // [ffmpeg]
            this.setZLMConfig(map, "ffmpeg.bin", zlm.getFfmpegBin());
            this.setZLMConfig(map, "ffmpeg.cmd", zlm.getFfmpegCmd());
            this.setZLMConfig(map, "ffmpeg.snap", zlm.getFfmpegSnap());
            this.setZLMConfig(map, "ffmpeg.log", zlm.getFfmpegLog());
            this.setZLMConfig(map, "ffmpeg.restart_sec", zlm.getFfmpegRestartSec());
            this.setZLMConfig(map, "ffmpeg.restart_sec", zlm.getFfmpegRestartSec());
            // [general]
            this.setZLMConfig(map, "general.enableVhost", zlm.getGeneralEnableVhost());
            this.setZLMConfig(map, "general.flowThreshold", zlm.getGeneralFlowThreshold());
            this.setZLMConfig(map, "general.maxStreamWaitMS", zlm.getGeneralMaxStreamWaitMs());
            this.setZLMConfig(map, "general.streamNoneReaderDelayMS", zlm.getGeneralStreamNoneReaderDelayMs());
            this.setZLMConfig(map, "general.resetWhenRePlay", zlm.getGeneralResetWhenRePlay());
            this.setZLMConfig(map, "general.publishToHls", zlm.getGeneralPublishToHls());
            this.setZLMConfig(map, "general.publishToMP4", zlm.getGeneralPublishToMp4());
            this.setZLMConfig(map, "general.mergeWriteMS", zlm.getGeneralMergeWriteMs());
            this.setZLMConfig(map, "general.wait_track_ready_ms", zlm.getGeneralWaitTrackReadyMs());
            this.setZLMConfig(map, "general.wait_add_track_ms", zlm.getGeneralWaitAddTrackMs());
            this.setZLMConfig(map, "general.unready_frame_cache", zlm.getGeneralUnreadyFrameCache());

            this.setZLMConfig(map, "protocol.modify_stamp", zlm.getGeneralModifyStamp());
            this.setZLMConfig(map, "protocol.add_mute_audio", zlm.getGeneralAddMuteAudio());
            this.setZLMConfig(map, "continue_push_ms.continue_push_ms", zlm.getGeneralContinuePushMs());
            this.setZLMConfig(map, "protocol.enable_audio", zlm.getGeneralEnableAudio());
            this.setZLMConfig(map, "protocol.hls_demand", zlm.getGeneralHlsDemand());
            this.setZLMConfig(map, "protocol.rtsp_demand", zlm.getGeneralRtspDemand());
            this.setZLMConfig(map, "protocol.rtmp_demand", zlm.getGeneralRtmpDemand());
            this.setZLMConfig(map, "protocol.ts_demand", zlm.getGeneralTsDemand());
            this.setZLMConfig(map, "protocol.fmp4_demand", zlm.getGeneralFmp4Demand());

            // 设置服务器ID
            this.setZLMConfig(map, "general.mediaServerId", zlm.getGeneralMediaServerId());

            // [hls]
            this.setZLMConfig(map, "hls.fileBufSize", zlm.getHlsFileBufSize());
            this.setZLMConfig(map, "hls.filePath", zlm.getHlsFilePath());
            this.setZLMConfig(map, "hls.segDur", zlm.getHlsSegDur());
            this.setZLMConfig(map, "hls.segNum", zlm.getHlsSegNum());
            this.setZLMConfig(map, "hls.segRetain", zlm.getHlsSegRetain());
            this.setZLMConfig(map, "hls.broadcastRecordTs", zlm.getHlsBroadcastRecordTs());
            this.setZLMConfig(map, "hls.deleteDelaySec", zlm.getHlsDeleteDelaySec());

            // [hook]
            // 不鉴权
            map.put("hook.admin_params", StringUtils.hasText(zlm.getHookAdminParams()) ? "secret=" + zlm.getHookAdminParams() : "");
            this.setZLMConfig(map, "hook.enable", zlm.getHookEnable());
            this.setZLMConfig(map, "hook.on_flow_report", zlm.getHookOnFlowReport());
            this.setZLMConfig(map, "hook.on_http_access", zlm.getHookOnHttpAccess());
            this.setZLMConfig(map, "hook.on_play", zlm.getHookOnPlay());
            this.setZLMConfig(map, "hook.on_publish", zlm.getHookOnPublish());
            this.setZLMConfig(map, "hook.on_record_mp4", zlm.getHookOnRecordMp4());
            this.setZLMConfig(map, "hook.on_record_ts", zlm.getHookOnRecordTs());
            this.setZLMConfig(map, "hook.on_rtsp_auth", zlm.getHookOnRtspAuth());
            this.setZLMConfig(map, "hook.on_rtsp_realm", zlm.getHookOnRtspRealm());
            this.setZLMConfig(map, "hook.on_shell_login", zlm.getHookOnShellLogin());
            this.setZLMConfig(map, "hook.on_stream_changed", zlm.getHookOnStreamChanged());
            this.setZLMConfig(map, "hook.on_stream_none_reader", zlm.getHookOnStreamNoneReader());
            this.setZLMConfig(map, "hook.on_stream_not_found", zlm.getHookOnStreamNotFound());
            this.setZLMConfig(map, "hook.on_server_started", zlm.getHookOnServerStarted());
            this.setZLMConfig(map, "hook.on_server_keepalive", zlm.getHookOnServerKeepalive());
            this.setZLMConfig(map, "hook.timeoutSec", zlm.getHookTimeoutSec());
            this.setZLMConfig(map, "hook.alive_interval", zlm.getHookAliveInterval());
            // [cluster]
            this.setZLMConfig(map, "cluster.origin_url", zlm.getClusterOriginUrl());
            this.setZLMConfig(map, "cluster.timeout_sec", zlm.getClusterTimeoutSec());
            // [http]
            this.setZLMConfig(map, "http.charSet", zlm.getHttpCharSet());
            this.setZLMConfig(map, "http.maxReqSize", zlm.getHttpMaxReqSize());
            this.setZLMConfig(map, "http.notFound", zlm.getHttpNotFound());
            // this.setZLMConfig(map, "http.port", zlm.getHttpPort());
            this.setZLMConfig(map, "http.rootPath", zlm.getHttpRootPath());
            this.setZLMConfig(map, "http.sendBufSize", zlm.getHttpSendBufSize());
            this.setZLMConfig(map, "http.sslport", zlm.getHttpSslPort());
            this.setZLMConfig(map, "http.dirMenu", zlm.getHttpDirMenu());
            this.setZLMConfig(map, "http.virtualPath", zlm.getHttpVirtualPath());
            this.setZLMConfig(map, "http.forbidCacheSuffix", zlm.getHttpForbidCacheSuffix());
            this.setZLMConfig(map, "http.forbidCacheSuffix", zlm.getHttpForbidCacheSuffix());

            // [multicast]
            this.setZLMConfig(map, "multicast.addrMax", zlm.getMulticastAddrMax());
            this.setZLMConfig(map, "multicast.addrMin", zlm.getMulticastAddrMin());
            this.setZLMConfig(map, "multicast.udpTTL", zlm.getMulticastUdpTtl());
            // [record]
            this.setZLMConfig(map, "record.appName", zlm.getRecordAppName());
            this.setZLMConfig(map, "record.fileBufSize", zlm.getRecordFileBufSize());
            this.setZLMConfig(map, "record.filePath", zlm.getRecordFilePath());
            this.setZLMConfig(map, "record.fileSecond", zlm.getRecordFileSecond());
            this.setZLMConfig(map, "record.sampleMS", zlm.getRecordSampleMs());
            this.setZLMConfig(map, "record.fastStart", zlm.getRecordFastStart());
            this.setZLMConfig(map, "record.fileRepeat", zlm.getRecordFileRepeat());

            // [rtmp]
            this.setZLMConfig(map, "rtmp.handshakeSecond", zlm.getRtmpHandshakeSecond());
            this.setZLMConfig(map, "rtmp.keepAliveSecond", zlm.getRtmpKeepAliveSecond());
            this.setZLMConfig(map, "rtmp.modifyStamp", zlm.getRtmpModifyStamp());
            this.setZLMConfig(map, "rtmp.port", zlm.getRtmpPort());
            this.setZLMConfig(map, "rtmp.sslport", zlm.getRtmpSslPort());
            // [rtp]
            this.setZLMConfig(map, "rtp.audioMtuSize", zlm.getRtpAudioMtuSize());
            this.setZLMConfig(map, "rtp.videoMtuSize", zlm.getRtpVideoMtuSize());
            this.setZLMConfig(map, "rtp.rtpMaxSize", zlm.getRtpRtpMaxSize());
            // [rtp_proxy]
            this.setZLMConfig(map, "rtp_proxy.dumpDir", zlm.getRtpProxyDumpDir());
            this.setZLMConfig(map, "rtp_proxy.port", zlm.getRtpProxyPort());
            this.setZLMConfig(map, "rtp_proxy.timeoutSec", zlm.getRtpProxyTimeoutSec());
            this.setZLMConfig(map, "rtp_proxy.port_range", zlm.getRtpProxyPortRange());
            // [rtc]
            this.setZLMConfig(map, "rtc.timeoutSec", zlm.getRtcTimeoutSec());
            this.setZLMConfig(map, "rtc.externIP", zlm.getRtcExternIp());
            this.setZLMConfig(map, "rtc.port", zlm.getRtcPort());
            this.setZLMConfig(map, "rtc.rembBitRate", zlm.getRtcRembBitRate());
            this.setZLMConfig(map, "rtc.preferredCodecA", zlm.getRtcPreferredCodecA());
            this.setZLMConfig(map, "rtc.preferredCodecV", zlm.getRtcPreferredCodecV());
            // [rtsp]
            this.setZLMConfig(map, "rtsp.authBasic", zlm.getRtspAuthBasic());
            this.setZLMConfig(map, "rtsp.directProxy", zlm.getRtspDirectProxy());
            this.setZLMConfig(map, "rtsp.handshakeSecond", zlm.getRtspHandshakeSecond());
            this.setZLMConfig(map, "rtsp.keepAliveSecond", zlm.getRtspKeepAliveSecond());
            this.setZLMConfig(map, "rtsp.port", zlm.getRtspPort());
            this.setZLMConfig(map, "rtsp.sslport", zlm.getRtspSslPort());
            // [shell]
            this.setZLMConfig(map, "shell.maxReqSize", zlm.getShellMaxReqSize());
            this.setZLMConfig(map, "shell.port", zlm.getShellPort());
            // 发送请求
            String url = String.format("%s%s:%s/index/api/setServerConfig", isHttps(zlm.isHttps()), zlm.getHttpIp(), zlm.getHttpPort());
            String result = HttpUtil.post(url, map);
            return JSONObject.parseObject(result);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("\n{} 设置ZLM配置失败: {}", zlm.getGeneralMediaServerId(), e);
        }
        return null;
    }

    /**
     * 获取ZLM配置
     *
     * @param zlm
     * @return
     */
    public JSONObject getZLMediaKitConfig(ZLMediaKit zlm) {
        try {
            HashMap<String, Object> map = new HashMap<>();
            // 设置认证信息
            map.put("secret", zlm.getApiSecret());
            String url = String.format("%s%s:%s/index/api/getServerConfig", isHttps(zlm.isHttps()), zlm.getHttpIp(), zlm.getHttpPort());
            String result = HttpUtil.post(url, map);
            return JSONObject.parseObject(result);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("\n{} 获取ZLM配置失败: {}", zlm.getGeneralMediaServerId(), e);
        }
        return null;
    }

    /**
     * 关闭所有流
     *
     * @param zlm    为空使用默认 可选
     * @param schema 协议，例如 rtsp或rtmp 可选
     * @param vhost  虚拟主机，例如__defaultVhost__ 可选
     * @param app    应用名，例如 live    可选
     * @param stream 流id，例如 test    可选
     * @param force  是否强制关闭(有人在观看是否还关闭) 1 OR 0
     * @return
     */
    public void closeStreams(ZLMediaKit zlm, String schema, String vhost, String app, String stream, String force) {
        if (zlm==null){
            zlm = zlMediaKitConfig.getDefaultZLMediaKit();
        }
        try {
            HashMap<String, Object> map = new HashMap<>();
            // 设置认证信息
            map.put("secret", zlm.getApiSecret());
            this.setZLMConfig(map, "schema", schema);
            this.setZLMConfig(map, "vhost", vhost);
            this.setZLMConfig(map, "app", app);
            this.setZLMConfig(map, "stream", stream);
            this.setZLMConfig(map, "force", force);
            String url = String.format("%s%s:%s/index/api/close_streams", isHttps(zlm.isHttps()), zlm.getHttpIp(), zlm.getHttpPort());
            String result = HttpUtil.post(url, map);
            JSONObject object = JSONObject.parseObject(result);
            int code = object.getIntValue("code");
            if (code == 0) {
                // 命中的流个数
                int count_hit = object.getIntValue("count_hit");
                // 关闭的流个数
                int count_closed = object.getIntValue("count_closed");
                log.info("\n流媒体关闭流[命中的流个数{}][关闭的流个数{}]", count_hit, count_closed);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("\n{} 流媒体关闭流失败: {}", zlm.getGeneralMediaServerId(), e);
        }
    }

    /**
     * 创建rtpServer收流端口
     *
     * @param zlm      流媒体配置
     * @param streamId 流id
     * @param port     指定端口,传null或者0为随机端口
     * @return 随机端口 [-1为获取错误]
     */
    public String createRtpServer(ZLMediaKit zlm, String streamId, Integer port) {
        try {
            HashMap<String, Object> map = new HashMap<>();
            // 设置认证信息
            map.put("secret", zlm.getApiSecret());
            if (port == null || port >= 0) {
                // 使用随机端口
                map.put("port", 0);
            } else {
                map.put("port", port);
            }
            // 创建 udp端口时是否同时监听tcp端口
            map.put("enable_tcp", 1);
            map.put("stream_id", streamId);
            // 请求地址
            String url = String.format("%s%s:%s/index/api/openRtpServer", isHttps(zlm.isHttps()), zlm.getHttpIp(), zlm.getHttpPort());
            String result = HttpUtil.post(url, map);
            JSONObject object = JSONObject.parseObject(result);
            int code = object.getIntValue("code");
            String msg = object.getString("msg");
            if (code == 0) {
                return object.getInteger("port").toString();
                // 存在的情况
            } else if (msg.equals("该stream_id已存在")) {
                return "-2";
            }
            log.error("\n{} 创建rtpServer失败: {}", zlm.getGeneralMediaServerId(), msg);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("\n{} 创建rtpServer失败: {}", zlm.getGeneralMediaServerId(), e);
        }
        return "-1";
    }


    /**
     * 重启流媒体服务器
     *
     * @param zlm
     * @return
     */
    public boolean restart(ZLMediaKit zlm) {
        if (zlm==null){
            zlm = zlMediaKitConfig.getDefaultZLMediaKit();
        }
        String msg = "";
        try {
            HashMap<String, Object> map = new HashMap<>();
            // 设置认证信息
            map.put("secret", zlm.getApiSecret());
            // 请求地址
            String url = String.format("%s%s:%s/index/api/restartServer", isHttps(zlm.isHttps()), zlm.getHttpIp(), zlm.getHttpPort());
            String result = HttpUtil.post(url, map);
            JSONObject object = JSONObject.parseObject(result);
            int code = object.getIntValue("code");
            msg = object.getString("msg");
            if (code == 0) {
                log.info("\n流媒体服务器[{}]重启成功:{}", zlm.getGeneralMediaServerId(), msg);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("\n请求流媒体服务器[{}]失败:{}", zlm.getGeneralMediaServerId(), e);
        }
        log.error("\n流媒体服务器[{}]重启失败:{}", zlm.getGeneralMediaServerId(), msg);
        return false;
    }

    /**
     * 发送RTP流
     * @param zlm 流媒体对象
     * @param ssrc ssrc
     * @param ip 目标ip
     * @param port 本地端口
     * @param toPort 目标端口
     * @param isTcp 是否是TCP
     * @return
     */
    public boolean pushStreamGB(ZLMediaKit zlm, String ssrc, String ip, String port, String toPort, boolean isTcp) {
        String msg = "";
        try {
            HashMap<String, Object> map = new HashMap<>();
            // 设置认证信息
            map.put("secret", zlm.getApiSecret());
            map.put("vhost", "__defaultVhost__");
            // 应用名
            // map.put("app", "rtp");
            map.put("app", zlm.getPullStreamApp());
            // 流名称
            // map.put("stream", String.format("%s_%s", deviceId, channelId));
            map.put("stream", zlm.getPullStreamId());
            // rtp推流的ssrc，ssrc不同时，可以推流到多个上级服务器
            map.put("ssrc", ssrc);
            // 目标流媒体ip
            map.put("dst_url", ip);
            // 目标端口
            map.put("dst_port", toPort);
            // 是否UDP
            map.put("is_udp", isTcp ? 1 : 0);
            // 本机推流端口
            map.put("src_port", port);
            // 是否推送本地MP4录像，该参数非必选参数
            // map.put("from_mp4", "");



            // 请求地址
            String url = String.format("%s%s:%s/index/api/startSendRtp", isHttps(zlm.isHttps()), zlm.getHttpIp(), zlm.getHttpPort());
            String result = HttpUtil.post(url, map);
            JSONObject object = JSONObject.parseObject(result);
            int code = object.getIntValue("code");
            msg = object.getString("msg");
            if (code == 0) {
                log.info("\n国标推流: 流媒体: {}, 流id:{}", zlm.getGeneralMediaServerId());
                return true;
            }
            log.error("\n国标推流执行错误:[{}][{}][{}]", code,zlm.getGeneralMediaServerId(), msg);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("\n国标推流执行异常:[{}][{}]", zlm.getGeneralMediaServerId(), e);
        }
        return false;
    }

    /**
     * 关闭推流
     * @param zlm 流媒体对象
     * @param ssrc ssrc
     * @return
     */
    public boolean stopStreamGB(ZLMediaKit zlm, String ssrc) {
        String msg = "";
        try {
            HashMap<String, Object> map = new HashMap<>();
            // 设置认证信息
            map.put("secret", zlm.getApiSecret());
            map.put("vhost", "__defaultVhost__");
            // 应用名
            // map.put("app", "rtp");
            map.put("app", zlm.getPullStreamApp());
            // 流名称
            // map.put("stream", String.format("%s_%s", deviceId, channelId));
            map.put("stream", zlm.getPullStreamId());
            // rtp推流的ssrc，ssrc不同时，可以推流到多个上级服务器
            map.put("ssrc", ssrc);


            // 请求地址
            String url = String.format("%s%s:%s/index/api/stopSendRtp", isHttps(zlm.isHttps()), zlm.getHttpIp(), zlm.getHttpPort());
            String result = HttpUtil.post(url, map);
            JSONObject object = JSONObject.parseObject(result);
            int code = object.getIntValue("code");
            msg = object.getString("msg");
            if (code == 0) {
                log.info("\n停止推流: 流媒体: {}, 流id:{}", zlm.getGeneralMediaServerId());
                return true;
            }
            log.error("\n停止推流执行错误:[{}][{}][{}]", zlm.getGeneralMediaServerId(), msg);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("\n停止推流执行异常:[{}][{}][{}]", zlm.getGeneralMediaServerId(), e);
        }
        return false;
    }



    /**
     * 添加拉流代理
     *
     * @param zlm   流媒体服务器对象
     * @return
     */
    public boolean playPullStrean(ZLMediaKit zlm) {
        if (zlm==null){
            zlm = zlMediaKitConfig.getDefaultZLMediaKit();
        }
        String msg = "";
        try {
            HashMap<String, Object> map = new HashMap<>();
            // 设置认证信息
            map.put("secret", zlm.getApiSecret());
            map.put("vhost", "__defaultVhost__");
            // 设置拉流超时时间
            map.put("timeout_sec", "15");
            // 拉流重试次数,不传此参数或传值<=0时，则无限重试
            map.put("retry_count", "0");
            // 流应用名
            setZLMConfig(map, "app", zlm.getPullStreamApp());
            // 流id
            setZLMConfig(map, "stream",  zlm.getPullStreamId());
            // 拉流地址
            setZLMConfig(map, "url", zlm.getPullStreamUrl());
            // 拉流方式[rtsp拉流时，拉流方式，0：tcp，1：udp，2：组播]
            setZLMConfig(map, "rtp_type", "1");
            // 是否转hls
            setZLMConfig(map, "enable_hls", "1");
            // 是否mp4录制
            setZLMConfig(map, "enable_mp4", "1");
            // 是否转协议为rtsp/webrtc
            setZLMConfig(map, "enable_rtsp", "1");
            // 是否转协议为rtmp/flv
            setZLMConfig(map, "enable_rtmp", "1");
            // 是否转协议为http-ts/ws-ts
            setZLMConfig(map, "enable_ts", "1");
            // 是否转协议为http-fmp4/ws-fmp4
            setZLMConfig(map, "enable_fmp4", "1");
            // 转协议是否开启音频
            setZLMConfig(map, "enable_audio", "1");
            // 转协议无音频时，是否添加静音aac音频
            setZLMConfig(map, "add_mute_audio", "1");
            // mp4录制保存根目录，置空使用默认目录
            // setZLMConfig(map, "mp4_save_path", proxy.getMp4SavePath());
            // mp4录制切片大小，单位秒
            // setZLMConfig(map, "mp4_max_second", proxy.getMp4MaxSecond());
            // hls保存根目录，置空使用默认目录
            // setZLMConfig(map, "hls_save_path", proxy.getHlsSavePath());

            // 请求地址
            String url = String.format("%s%s:%s/index/api/addStreamProxy", isHttps(zlm.isHttps()), zlm.getHttpIp(), zlm.getHttpPort());
            String result = HttpUtil.post(url, map);
            JSONObject object = JSONObject.parseObject(result);
            int code = object.getIntValue("code");
            msg = object.getString("msg");
            if (code == 0) {
                log.info("\n拉流成功: 流媒体: {}, 流id:{}", zlm.getGeneralMediaServerId(), zlm.getPullStreamId());
                return true;
            }
            log.error("\n拉流执行错误:[{}][{}][{}]", zlm.getGeneralMediaServerId(), zlm.getPullStreamId(), msg);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("\n拉流执行异常:[{}][{}][{}]", zlm.getGeneralMediaServerId(), zlm.getPullStreamId(), e);
        }
        return false;
    }
}
