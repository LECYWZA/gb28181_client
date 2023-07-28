package com.ruoyi.utils;

import com.alibaba.fastjson.JSON;
import com.ruoyi.domain.Device;
import com.ruoyi.domain.DeviceChannel;
import com.ruoyi.domain.resp.RecordInfos;
import com.ruoyi.domain.sip.RecordInfo;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class XMLUtil {


    public static JSONObject getJSONObject(JSONObject json, String key) {
        try {
            return json.getJSONObject(key);
        } catch (JSONException e) {
            // log.warn("\njson.getJSONObject({}) 取值时为空,改为取 Notify", key);
            try {
                return json.getJSONObject("Notify");
            } catch (JSONException ex) {
                ex.printStackTrace();
                log.error("\nNotify取值为空", e);
                return null;
            }

        }
    }

    /**
     * 不管什么类型,搞成String
     *
     * @param json 对象
     * @return
     */
    public static String getString(JSONObject json, String key) {
        try {
            return json.get(key).toString();
        } catch (JSONException e) {
            log.info("\n{} 取值时为空", key, e.getMessage());
            return "";
        }

    }

    /**
     * XML 转 Device
     *
     * @param device     设备对象
     * @param deviceInfo byte[] XML文本
     * @return
     */
    public static Device getXMLToDevice(Device device, byte[] deviceInfo) {
        if (device == null) {
            device = new Device();
            device.setCharset("GB2312");
        }
        try {
            // 设置转换编码
            String xmlString = new String(deviceInfo, device.getCharset());
            JSONObject response = XML.toJSONObject(xmlString);
            // 获取响应
            JSONObject json = response.getJSONObject("Response");
            // 设置设备名称
            device.setDeviceId(getString(json, "DeviceID"));
            // 厂家
            device.setManufacturer(getString(json, "Manufacturer"));
            // 型号
            device.setModel(getString(json, "Model"));
            // 固件
            device.setFirmware(getString(json, "Firmware"));
            // 设备名称
            device.setDeviceName(getString(json, "Name"));
            // 设备名称为 null
            if (!StringUtils.hasText(device.getDeviceName())){
                // 重新获取
                device.setDeviceName(getString(json, "Name"));
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            log.error("\n出错: {}", e);
        }
        return device;
    }

    /**
     * XML TO List<DeviceChannel>
     *
     * @param catalog XML byte 数组
     * @return
     */
    public static List<DeviceChannel> getXMLToDeviceChannel(byte[] catalog, String encode) {
        if (!StringUtils.hasText(encode)) {
            encode = "GB2312";
        }

        List<DeviceChannel> list = new ArrayList<>();

        try {
            String xmlString = new String(catalog, encode);
            /*xmlString = xmlString.replaceAll("</Item>", "</Item>\n" +
                    "        <Item>\n" +
                    "            <DeviceID>44010200492000000004</DeviceID>\n" +
                    "            <Name>sydevice</Name>\n" +
                    "            <Manufacturer>shiyuetech</Manufacturer>\n" +
                    "            <Model>RealGBD_V2.1.1</Model>\n" +
                    "            <Owner>RealGBD_V2</Owner>\n" +
                    "            <CivilCode>435200</CivilCode>\n" +
                    "            <Block>435200</Block>\n" +
                    "            <Address>wuhan</Address>\n" +
                    "            <Parental>0</Parental>\n" +
                    "            <ParentID>44010200492000000044</ParentID>\n" +
                    "            <IPAddress/>\n" +
                    "            <Port>0</Port>\n" +
                    "            <Password/>\n" +
                    "            <Status>ON</Status>\n" +
                    "            <Longitude>114.33</Longitude>\n" +
                    "            <Latitude>30.35</Latitude>\n" +
                    "        </Item>");*/
            JSONObject response = XML.toJSONObject(xmlString);
            JSONObject json = response.getJSONObject("Response");
            String deviceId = getString(json, "DeviceID");
            JSONObject deviceList = json.getJSONObject("DeviceList");
            // 获取通道条数
            int num = deviceList.getInt("Num");
            if (num > 0) {
                if (num == 1) {
                    // 单个
                    JSONObject item = deviceList.getJSONObject("Item");
                    DeviceChannel channel = getJSONToDeviceChannel(deviceId, item);
                    list.add(channel);
                } else {
                    JSONArray items = deviceList.getJSONArray("Item");
                    for (int i = 0; i < 2; i++) {
                        DeviceChannel channel = getJSONToDeviceChannel(deviceId, items.getJSONObject(i));
                        list.add(channel);
                    }
                }
            }

            return list;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * JSON 转 DeviceChannel
     *
     * @param json JSON对象
     * @return
     */
    private static DeviceChannel getJSONToDeviceChannel(String deviceId, JSONObject json) {
        DeviceChannel c = new DeviceChannel();
        c.setParentId(deviceId);
        c.setChannelId(getString(json, "DeviceID"));
        c.setStatus(getString(json, "Status").equalsIgnoreCase("ON") ? "1" : "0");
        c.setChannelName(getString(json, "Name"));
        c.setOwner(getString(json, "Owner"));
        c.setManufacturer(getString(json, "Manufacturer"));
        c.setSafetyWay(getString(json, "SafetyWay"));
        c.setRegisterWay(getString(json, "RegisterWay"));
        c.setModel(getString(json, "Model"));
        c.setCivilCode(getString(json, "CivilCode"));
        c.setAddress(getString(json, "Address"));
        c.setPort(getString(json, "Port"));
        c.setLatitude(getString(json, "Latitude"));
        c.setLongitude(getString(json, "Longitude"));
        c.setBlock(getString(json, "Block"));
        c.setIpAddress(getString(json, "IPAddress"));
        c.setPassword(getString(json, "Password"));
        c.setParental(getString(json, "Parental"));
        return c;
    }


    /**
     * XML 转 RecordInfo
     *
     * @param rs         录像集合对象
     * @param deviceId   设备id
     * @param enCode     编码方式[传null 默认GB2312]
     * @param recordInfo byte[] XML文本
     * @return
     */
    public synchronized static RecordInfos getXMLToRecordInfo(RecordInfos rs, String deviceId, String enCode, byte[] recordInfo) {

        if (enCode == null) enCode = "GB2312";
        try {
            // 设置转换编码
            String xmlString = new String(recordInfo, enCode);
            JSONObject response = XML.toJSONObject(xmlString);
            // 获取响应
            JSONObject json = response.getJSONObject("Response");
            if (json != null) {
                int sumNum = json.getInt("SumNum");
                // RecordInfos 为空
                if (rs == null) {
                    log.info("RecordInfos == null");
                    rs = new RecordInfos();
                    rs.setSize(sumNum);
                    rs.setRs(new ArrayList<>(sumNum));
                    // 为空直接返回,预览加上回放达到三个,摄像头无法查出录像
                    if (sumNum == 0) return rs;
                }
                List<RecordInfo> list = rs.getRs();
                // 获取 RecordList 节点对象
                JSONObject recordList = json.getJSONObject("RecordList");
                // 不为空
                if (recordList == null) throw new RuntimeException("RecordList为空");
                try {
                    // 报错就是只有一个转JSON不为数组
                    JSONArray item = recordList.getJSONArray("Item");
                    // 录像集合为空
                    if (item == null || item.length() == 0) throw new RuntimeException(" RecordList-> Item 为空");
                    // 录像XML转成对象
                    item.forEach(x -> {
                        RecordInfo info = JSON.parseObject(x.toString(), RecordInfo.class);
                        info.setDeviceId(deviceId);
                        list.add(info);
                    });
                } catch (JSONException e) {
                    // 单个对象处理
                    JSONObject item = recordList.getJSONObject("Item");
                    RecordInfo info = JSON.parseObject(item.toString(), RecordInfo.class);
                    info.setDeviceId(deviceId);
                    list.add(info);
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            log.error("\nXML转RecordInfo出错: {}", e);
        }
        return rs;
    }

}
