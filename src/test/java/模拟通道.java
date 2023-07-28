import cn.hutool.core.io.resource.ResourceUtil;
import lombok.SneakyThrows;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

public class 模拟通道 {

    @SneakyThrows
    public static void main(String[] args) throws JSONException {
        byte[] bytes = ResourceUtil.readBytes("sip/模拟通道.xml");

        String gb2312 = new String(bytes, "GB2312");
        JSONObject response = XML.toJSONObject(gb2312);
        JSONObject json = response.getJSONObject("Response");
        JSONObject deviceList = json.getJSONObject("DeviceList");
        JSONArray deviceID = json.getJSONArray("DeviceList");
        System.out.println(deviceID);
        System.out.println(deviceID);
    }
}
