package com.ruoyi.utils;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.ruoyi.domain.base.FFmpegResult;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 文件操作
 */
public class FileUtils {


    /**
     * 获取指定路径下的文件
     * 结果要么为null,size>0返回字符串默认排序后的名称
     *
     * @param path   路径名称 C://
     * @param suffix 文件后缀 如 .mp4
     * @return NULL or names
     */
    public static List<String> getFilePathName(String path, String suffix) {
        File fs = new File(path);
        File[] files = fs.listFiles();
        ArrayList<String> names = new ArrayList<>();
        if (files != null && files.length > 0) {
            Arrays.stream(files).filter(x -> x.getName().endsWith(suffix)).forEach(x -> names.add(x.getAbsolutePath()));
            // Arrays.stream(files).filter(x -> x.getName().endsWith(".ts")).forEach(File::delete);
        }
        Collections.sort(names);    // 排序
        return names.size() > 0 ? names : null;
    }

    /**
     * 删除指定路径下的文件*
     *
     * @param path   路径名称 C://
     * @param suffix 文件后缀 如 .mp4
     */
    public static void deleteFilePathName(String path, String suffix) {
        File fs = new File(path);
        File[] files = fs.listFiles();
        if (files != null && files.length > 0) {
            Arrays.stream(files).filter(x -> x.getName().endsWith(suffix)).forEach(File::delete);
        }
    }

    /**
     * 删除文件
     *
     * @param pathFile 路径 D:/h264.mp4
     */
    public static boolean delete(String pathFile) {
        return new File(pathFile).delete();
    }


    /**
     * 获取指定路径下的文件大小*
     *
     * @param path   路径名称 C://
     * @param suffix 文件后缀 如 .mp4
     * @return 所有文件大小
     */
    public static long getFilePathSize(String path, String suffix) {
        File fs = new File(path);
        File[] files = fs.listFiles();
        AtomicLong size = new AtomicLong();
        if (files != null && files.length > 0) {
            Arrays.stream(files).filter(x -> x.getName().endsWith(suffix)).forEach(x -> size.set(size.get() + x.length()));
            // Arrays.stream(files).filter(x -> x.getName().endsWith(".ts")).forEach(File::delete);
        }
        return size.get();
    }


    /**
     * 获取磁盘剩余空间 单位 G
     * total 总空间
     * leisure 剩余空间
     * occupy 已用空间
     */
    public static Map<String, Long> getRemainingDiskSpace(String path) {
        File fs = new File(path);
        long totalSpace = fs.getTotalSpace();
        long freeSpace = fs.getFreeSpace();
        long usedSpace = totalSpace - freeSpace;
        HashMap<String, Long> map = new HashMap<>(3);
        map.put("total", totalSpace);
        map.put("leisure", freeSpace);
        map.put("occupy", usedSpace);
        return map;
    }

    /**
     * 删除指定天数前的文件
     *
     * @param path 路径
     * @param day  文件最后修改时间 >= day 则删除
     * @return
     */
    public static boolean deleteDayFront(String path, Integer day) {
        File fs = new File(path);
        File[] files = fs.listFiles();
        if (files != null && files.length > 0) {
            Arrays.stream(files).filter(x -> {
                Date date = new Date(x.lastModified());
                long betweenDay = DateUtil.between(new Date(), date, DateUnit.DAY);
                return betweenDay >= day;
            }).forEach(FileUtils::deleteFile);
        }
        return false;
    }

    /**
     * 递归删除文件
     *
     * @param file 对象
     */
    public static boolean deleteFile(File file) {
        if (file.exists()) {// 判断文件是否存在
            File files[] = file.listFiles();
            for (File newfile : files) { // 遍历文件夹下的目录
                if (newfile.isFile()) { // 如果是文件而不是文件夹==>可直接删除
                    newfile.delete();
                } else {
                    deleteFile(newfile);//是文件夹,递归调用方法
                }
            }
            file.delete();
            return true;
        }
        return false;
    }

    /**
     * 修改目录权限
     *
     * @return
     */
    public static FFmpegResult execLinuxCmd(List<String> cmds) {
        // ffmpeg.exe -y -r 25 -i 1.h264 -i 1.aac -c copy out2.mp4
        SystemUtils.SystemType type = SystemUtils.getSystem();
        if (type.equals(SystemUtils.SystemType.Linux)) {
            // 执行操作
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(cmds);
            builder.redirectErrorStream(true);
            Process rs = null;
            StringBuilder sb = new StringBuilder();
            int i = -1;
            try {
                rs = builder.start();
                InputStream is = rs.getInputStream();
                InputStreamReader inst = new InputStreamReader(is, "GBK");
                BufferedReader br = new BufferedReader(inst);//输入流缓冲区
                String res = null;
                while ((res = br.readLine()) != null) {//循环读取缓冲区中的数据
                    sb.append(res + "\n");
                }
                br.close();
                inst.close();
                is.close();
                rs.destroyForcibly();
                i = rs.waitFor();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            // 返回结果
            return FFmpegResult.rs(i == 0, sb.toString());
        }
        return null;
    }


    /**
     * 设置文件夹权限
     *
     * @param path 路径
     */
    public static void setChmod777(String path) {
        // 给权限
        if (SystemUtils.getSystem().equals(SystemUtils.SystemType.Linux)) {
            List<String> chmod = Arrays.asList("chmod", "-R", "777", path);
            FFmpegResult rs = FileUtils.execLinuxCmd(chmod);
            if (rs!=null){
                System.out.println("执行权限结果: " + rs.isSuccess() + "\r\n" + rs.getMsg());
            }
        }
    }


}
