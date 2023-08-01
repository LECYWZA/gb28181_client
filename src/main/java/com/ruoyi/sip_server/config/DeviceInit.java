package com.ruoyi.sip_server.config;

import com.ruoyi.config.FileConfig;
import com.ruoyi.domain.Device;
import com.ruoyi.domain.DeviceChannel;
import com.ruoyi.domain.ZLMediaKit;
import com.ruoyi.media.config.ZLMediaKitConfig;
import com.ruoyi.subscribe.EventPublisher;
import com.ruoyi.utils.SipCmdUtil;
import com.ruoyi.utils.SystemUtils;
import com.ruoyi.utils.ZLMediaKitHttpUtil;
import com.ruoyi.web.controller.gb28181.GB28181Controller;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.InputStream;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@EnableScheduling // 开启定时任务
public class DeviceInit {

    @Autowired
    private SipCmdUtil sipCmdUtil;
    @Autowired
    private SipConfig sipConfig;
    @Autowired
    private ZLMediaKitConfig zlm;
    @Autowired
    private ZLMediaKitHttpUtil httpUtil;

    @Autowired
    private GB28181Controller controller;

    @Autowired
    private EventPublisher eventPublisher;

    /**
     * 所有设备
     */
    public static Map<String, Device> ds;
    public static boolean isRegister = true;


    @PostConstruct
    public void construct() {
        log.info("\n=========设备初始化=========");
        ds = new ConcurrentHashMap<>(sipConfig.getDeviceSize());
        // 关闭所有流
        httpUtil.closeStreams(null, null, null, null, null, "1");


        Device d = null;
        int sum = 1;
        int port = 40200;

        // 创建设备
        for (int i = 0; i < sipConfig.getDeviceSize(); i++, sum++, port++) {
            // 前面自动补 0
            String sumSize = null;
            String length = String.valueOf(sum);
            switch (length.length()) {
                case 1:
                    sumSize = "0000" + sum;
                    break;
                case 2:
                    sumSize = "000" + sum;
                    break;
                case 3:
                    sumSize = "00" + sum;
                    break;
                case 4:
                    sumSize = "0" + sum;
                    break;
                default:
                    sumSize = sum + "";
            }

            d = new Device();
            d.setDeviceId("340200000013200" + sumSize);
            d.setDeviceName((i + 1) + "-设备");
            d.setDeviceIp(sipConfig.getSipDeviceIp());
            d.setDevicePort(sipConfig.getSipDevicePort());
            // 设置流媒体推流ip端口
            d.setZlmIp(zlm.getHttpIp());
            d.setZlmPort(port);
            d.setCharset("GB2312");
            d.setRegisterProtocol("UDP");
            d.setStreamProtocol("UDP");

            DeviceChannel c = new DeviceChannel();
            c.setParentId(d.getDeviceId());
            c.setChannelId("38" + d.getDeviceId().substring(2));
            c.setChannelName((i + 1) + "-通道");
            d.setChannel(c);
            ds.put(d.getDeviceId(), d);
        }


        ZLMediaKit mediaKit = zlm.getDefaultZLMediaKit();
        if ("1".equals(mediaKit.getEnabled())) {
            try {
                System.err.println("等待五秒钟,等待流媒体重启完毕,开始拉流");
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            String ffmpegCommand = mediaKit.getFfmpegCommand();
            if (StringUtils.hasText(ffmpegCommand)) {
                ffmpegPush(ffmpegCommand);
            } else {
                boolean b = httpUtil.playPullStrean(null);
                if (!b) {
                    log.error("流媒体拉流异常");
                }else {
                    System.err.println("流媒体拉基础流成功");
                }
            }
        }

        // 注册
        controller.cmd(0);
    }

    /**
     * 推流
     *
     * @param ffmpegCommand ffmege 推流指令
     */
    public static void ffmpegPush(String ffmpegCommand) {

        SystemUtils.SystemType type = SystemUtils.getSystem();
        if (type.equals(SystemUtils.SystemType.Win)) {
            ffmpegCommand = ffmpegCommand.replaceFirst("ffmpeg", FileConfig.win.replace("\\", "/"));
            // ffmpegCommand = ffmpegCommand.replaceFirst("ffmpeg", FileConfig.win);
        } else {
            ffmpegCommand = ffmpegCommand.replaceFirst("ffmpeg", FileConfig.linux.replace("\\", "/"));
        }

        ffmpegCommand = ffmpegCommand.replaceFirst("input.mp4", FileConfig.mp4File.replace("\\", "/"));

        String finalFfmpegCommand = ffmpegCommand;


        new Thread(() -> {

            try {
                System.err.println("开始推流");
                System.err.println("ffmpeg 推流命令: " + finalFfmpegCommand);
                // 创建ProcessBuilder对象，并传入FFmpeg命令
                ProcessBuilder processBuilder = new ProcessBuilder(finalFfmpegCommand.split(" "));

                // 设置输出流和错误流合并
                processBuilder.redirectErrorStream(true);

                // 启动进程
                Process process = processBuilder.start();

                // 读取FFmpeg输出
                InputStream inputStream = process.getInputStream();
                Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
                String output = scanner.hasNext() ? scanner.next() : "";

                // 等待进程执行完成
                // int exitCode = process.waitFor();

                // 打印FFmpeg输出和退出码
                System.out.println("FFmpeg output:\n" + output);
                // System.out.println("Exit code: " + exitCode);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }

    @PreDestroy
    public void destroy() {
        log.info("\n=========设备销毁=========");
        // 关闭所有流
        httpUtil.closeStreams(null, null, null, null, null, "1");
    }


    // 定时的方法
    // @Scheduled(cron = "* */3 * * * *") // 每24小时执行一次
   /* @Async("my") // 设置为异步执行 不设置则默认为单线程,不管多少个方法,都会一个个按照顺序执行,一个线程睡,所有方法停
    public void condition() {
        if (isRegister){
            ds.values().stream().filter(x->!x.isRegister()).forEach(x->eventPublisher.eventPush(new SipRegisterEvent(x)));
        }else {
            ds.values().stream().filter(x->!x.isRegister()).forEach(x->eventPublisher.eventPush(new SipRegisterEvent(x,true)));
        }
    }*/


}
