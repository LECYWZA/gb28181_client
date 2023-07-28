import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class 获取IP地址 {
    public static void main(String[] args) {
        System.out.println((int)((Math.random()*9+1)*100000));
        // 获得本机的所有网络接口
        Enumeration<NetworkInterface> nifs = null;
        try {
            nifs = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        while (nifs.hasMoreElements()) {
            NetworkInterface nif = nifs.nextElement();

            // 获得与该网络接口绑定的 IP 地址，一般只有一个
            Enumeration<InetAddress> addresses = nif.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress addr = addresses.nextElement();

                if (addr instanceof Inet4Address) { // 只关心 IPv4 地址
                    System.out.println("网卡接口名称：" + nif.getName());
                    System.out.println("网卡接口地址：" + addr.getHostAddress());
                    System.out.println();
                }
            }
        }
    }
}
