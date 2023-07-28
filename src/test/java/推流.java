public class 推流 {
    public static void main(String[] args) throws Exception {
        // F:/dev-works/WorkWVP/sip_server整合若依/ffmpeg/ffmpeg.exe -stream_loop -1 -re -i I:/1080P.mp4 -c:v copy -c:a copy -f flv rtmp://192.168.11.180:1935/pull-stream/pull-to-push
//        String push = "F:/dev-works/WorkWVP/sip_server整合若依/ffmpeg/ffmpeg.exe -re -i  F:/dev-works/WorkWVP/sip_server整合若依/ffmpeg/input.mp4 -vcodec h264 -acodec aac -f flv rtmp://192.168.11.180/pull-stream/pull-to-push";
//        String push = "F:/dev-works/WorkWVP/sip_server整合若依/ffmpeg/ffmpeg.exe -stream_loop -1 -re -i  I:/1080P.mp4 -vcodec h264 -acodec aac -f flv rtmp://192.168.11.180/pull-stream/pull-to-push";
        String push = "F:/dev-works/WorkWVP/sip_server整合若依/ffmpeg/ffmpeg.exe -stream_loop -1 -re -i F:/dev-works/WorkWVP/sip_server整合若依/sip_client/src/main/resources/ffmpeg/input.mp4 -vcodec h264 -acodec aac -f flv rtmp://192.168.11.180/pull-stream/pull-to-push";
        // 创建ProcessBuilder对象，并传入FFmpeg命令
        ProcessBuilder processBuilder = new ProcessBuilder(push.split(" "));

        // 设置输出流和错误流合并
        processBuilder.redirectErrorStream(true);

        // 启动进程
        Process process = processBuilder.start();

        // 读取FFmpeg输出
        java.io.InputStream inputStream = process.getInputStream();
        java.util.Scanner scanner = new java.util.Scanner(inputStream).useDelimiter("\\A");
        String output = scanner.hasNext() ? scanner.next() : "";

        // 等待进程执行完成
        int exitCode = process.waitFor();
        // 打印FFmpeg输出和退出码
        System.out.println("FFmpeg output:\n" + output);
        System.out.println("Exit code: " + exitCode);

    }
}
