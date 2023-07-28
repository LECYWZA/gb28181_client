package com.ruoyi.sip_server.config;

import com.ruoyi.sip_server.SipServer;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import javax.sip.*;
import java.util.Properties;
import java.util.TooManyListenersException;

// 创建一对 ListeningPoint 和 SipProvider 对象。这两个对象提供发送和接收消息的通信功能。一组对象用于 TCP，一组对象用于 UDP

@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "sip")
public class SipConfig {


    /**
     * 监听ip
     */
    private String listenIp = "0.0.0.0";

    /**
     * 启动端口
     */
    @Value("${server.port}")
    private Integer httpPort;

    /**
     * 本地ip
     */
    private String ip;
    /**
     * 本地端口
     */
    private Integer port;
    /**
     * 编码
     */
    private String id;
    /**
     * 域编码
     */
    private String domain;
    /**
     * sip服务器密码
     */
    private String password = "";
    /**
     * 是否是Https
     */
    private boolean isHttps;
    /**
     * 模拟设备数量,端口默认50000开始
     */
    private Integer deviceSize;


    /**
     * 本机sip ip
     */
    @Value("${sip-device.ip}")
    private String sipDeviceIp;
    /**
     * 本机sip端口
     */
    @Value("${sip-device.port}")
    private Integer sipDevicePort;


    /**
     * 发送心跳间隔
     */
    private String keepaliveTimeout = "60000";


    private SipFactory sipFactory;

    private SipStack sipStack;

    private ListeningPoint tcpListeningPoint;

    private ListeningPoint udpListeningPoint;

    private SipServer sipServer;


    @Bean
    public SipServer sipLayer() {
        sipServer = new SipServer();
        log.info("\n创建 sipLayer");
        return sipServer;
    }

    @Bean
    public SipFactory sipFactory() {
        sipFactory = SipFactory.getInstance();
        sipFactory.setPathName("gov.nist");
        log.info("\n创建 sipFactory");
        return sipFactory;
    }

    @Bean
    @DependsOn("sipFactory")
    public SipStack sipStack() {
        Properties properties = new Properties();
        properties.setProperty("javax.sip.STACK_NAME", "GB28181_SIP");
        properties.setProperty("javax.sip.IP_ADDRESS", listenIp);
        properties.setProperty("gov.nist.javax.sip.LOG_MESSAGE_CONTENT", "true");
        properties.setProperty("gov.nist.javax.sip.TRACE_LEVEL", "0");
        properties.setProperty("gov.nist.javax.sip.SERVER_LOG", "sip_server_log");
        properties.setProperty("gov.nist.javax.sip.DEBUG_LOG", "sip_debug_log");
        try {
            sipStack = sipFactory.createSipStack(properties);
            log.info("\n创建 SipStack");
            return sipStack;
        } catch (PeerUnavailableException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Bean
    @DependsOn("sipStack")
    public ListeningPoint tcpListeningPoint() {
        try {
            tcpListeningPoint = sipStack.createListeningPoint(listenIp, sipDevicePort, "tcp");
            log.info("\n创建 tcpListeningPoint");
            return tcpListeningPoint;
        } catch (TransportNotSupportedException | InvalidArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Bean
    @DependsOn("sipStack")
    public ListeningPoint udpListeningPoint() {
        try {
            udpListeningPoint = sipStack.createListeningPoint(listenIp, sipDevicePort, "udp");
            log.info("\n创建 udpListeningPoint");
            return udpListeningPoint;
        } catch (TransportNotSupportedException | InvalidArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Bean("tcpSipProvider")
    @DependsOn({"sipLayer", "tcpListeningPoint"})
    public SipProvider tcpSipProvider() {
        try {
            SipProvider tcpSipProvider = sipStack.createSipProvider(tcpListeningPoint);
            tcpSipProvider.addSipListener(sipServer);
            log.info("\n创建 tcpSipProvider");
            return tcpSipProvider;
        } catch (ObjectInUseException | TooManyListenersException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Bean("udpSipProvider")
    @DependsOn({"sipLayer", "udpListeningPoint"})
    public SipProvider udpSipProvider() {
        try {
            SipProvider udpSipProvider = sipStack.createSipProvider(udpListeningPoint);
            udpSipProvider.addSipListener(sipServer);
            log.info("\n创建 udpSipProvider");
            return udpSipProvider;
        } catch (ObjectInUseException | TooManyListenersException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Bean("ssrcConfig")
    public SSRCConfig ssrcConfig() {
        return new SSRCConfig(domain);
    }

}
