package com.ruoyi.config;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import com.ruoyi.utils.FileUtils;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

@Configuration
public class FileConfig {


    /* 项目同级的 servFile 目录  E:\myJava-workspace\servFile   */
    public final static File servFile = new File(new File(FileUtil.getAbsolutePath("classpath:"))
            .getParentFile().getParentFile().getParentFile().toString() + "/ffmpeg/");
    /* 带斜杠,直接拼接即可  E:\myJava-workspace\servFile/ */
    public final static String win = servFile + "/ffmpeg.exe";
    public final static String linux = servFile + "/ffmpeg";
    public final static String mp4File = servFile + "/input.mp4";


    static {
        System.out.println("序列化存放路径: " + servFile);
        System.out.println("路径是否存在: " + servFile.exists());
        /* 不存在则创建目录 */
        if (!servFile.exists()) {
            System.out.println("创建目录: " + servFile);
            servFile.mkdirs();
            servFile.setReadable(true);
            servFile.setWritable(true);
        }

        if (!new File(win).exists()) {
            InputStream in = ResourceUtil.getStream("ffmpeg/ffmpeg.exe");
            try {
                FileOutputStream out = new FileOutputStream(win);
                IoUtil.copy(in, out);
                in.close();
                out.close();
                System.out.println("拷贝: ffmpeg/ffmpeg.exe");
                File file = new File(win);
                file.setReadable(true);
                file.setWritable(true);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        if (!new File(linux).exists()) {
            InputStream in = ResourceUtil.getStream("ffmpeg/ffmpeg");
            try {
                FileOutputStream out = new FileOutputStream(linux);
                IoUtil.copy(in, out);
                in.close();
                out.close();
                System.out.println("拷贝: ffmpeg/ffmpeg");
                File file = new File(linux);
                file.setReadable(true);
                file.setWritable(true);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        InputStream in = ResourceUtil.getStream("ffmpeg/input.mp4");
        try {
            FileOutputStream out = new FileOutputStream(mp4File);
            IoUtil.copy(in, out);
            in.close();
            out.close();
            System.out.println("拷贝: ffmpeg/input.mp4");
            File file = new File(mp4File);
            file.setReadable(true);
            file.setWritable(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        FileUtils.setChmod777(servFile.toString());
        System.out.println("设置FFmpeg目录执行权限");

    }
}
