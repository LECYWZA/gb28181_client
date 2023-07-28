package com.ruoyi.sip_server.config;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Data
public class SSRCConfig {


    /**
     * 最大并发数
     */
    private final String MAXIMUM_CONCURRENCY = "10000";

    /**
     * 前缀
     */
    private String ssrcPrefix;
    /**
     * 已使用SSRC
     */
    private List<String> isUsed;
    /**
     * 未使用SSRC
     */
    private List<String> notUsed;


    /**
     * TODO SSRC组成
     * 初始化对象
     */
    public SSRCConfig(String domain) {
        this.isUsed = new ArrayList<>();
//        this.ssrcPrefix = domain.substring(3, 7);
        this.ssrcPrefix = "1998";
        this.notUsed = new ArrayList<>();
        int maxLength = MAXIMUM_CONCURRENCY.length();
        for (int i = 1; i <= Integer.parseInt(MAXIMUM_CONCURRENCY); i++) {
            StringBuilder ssrc = new StringBuilder();

            int iLength = String.valueOf(i).length();
            for (int k = 1; k <= maxLength - iLength; k++) {
                ssrc.append("0");
            }
            ssrc = ssrc.append(i);
            this.notUsed.add(ssrc.toString());
        }
        log.info("\n{} 个SSRC初始化完成!", notUsed.size());
    }


    /**
     * 获取视频预览的SSRC值
     *
     * @return ssrc
     */
    public String getPlaySsrc() {
        return String.format("0%s%s", getSsrcPrefix(), getSN());
    }

    /**
     * 获取录像回放的SSRC值
     */
    public String getPlayBackSsrc() {
        return String.format("1%s%s", getSsrcPrefix(), getSN());
    }

    /**
     * 释放ssrc
     *
     * @param ssrc
     */
    public synchronized void releaseSsrc(String ssrc) {
        if (!StringUtils.hasText(ssrc)) {
            return;
        }
        String sn = ssrc.substring(5);
        try {
            isUsed.remove(sn);
            if (!notUsed.contains(sn)) {
                notUsed.add(sn);
            }
            log.info("\n释放ssrc: {}, ssrc使用情况: {}/{}", ssrc, isUsed.size(), notUsed.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取后四位数SN,随机数
     */
    private synchronized String getSN() {
        String sn = null;
        int index = 0;
        if (notUsed.size() == 0) {
            throw new RuntimeException("ssrc已耗尽");
        } else if (notUsed.size() == 1) {
            sn = notUsed.get(0);
        } else {
            index = new Random().nextInt(notUsed.size() - 1);
            sn = notUsed.get(index);
        }
        notUsed.remove(index);
        isUsed.add(sn);
        return sn;
    }


}
