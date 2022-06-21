package com.gotokeep.jmeter.util;

import com.google.common.base.Joiner;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * Created by zhaoqian on 19/2/14.
 */
public class POSTSignUtil {
    private int MAGIC_NUMBER(String env){
        if(env.equals("pre")){
            return 929;
        }else if(env.equals("online")){
            return 929;
        }
        return 0;
    }

    private String key(String env){
        if(env.equals("pre")){
            return "V1QiLCJhbGciOiJIUzI1NiJ9";
        }else if(env.equals("online")){
            return "V1QiLCJhbGciOiJIUzI1NiJ9";
        }
        return "";
    }

    //32位MD5加密＋后8位生成
    public String DeviceIdSign(String domaintype,Map<String, String[]> param,String body,String uri) throws IOException {
        String deviceId=Sign32(domaintype,param, body, uri);
        int over = 0;
        String sr="";
        for(int i=7;i>=0;i--){
            int first = getInt(deviceId.charAt(i));
            int second = getInt(deviceId.charAt(i + 8));
            int third = getInt(deviceId.charAt(i + 16));
            int fourth = getInt(deviceId.charAt(i + 24));
            int sum = first + second + third + fourth + over + MAGIC_NUMBER(domaintype);
            over = sum / 16;
            sr=getChar(sum % 16)+sr;
        }
        return deviceId+sr;
    }

    //MD5加密
    private String Sign32(String domaintype,Map<String, String[]> param,String body,String uri) throws IOException {
        String params = formatUrlMap(param);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(params);
        if (body != null) {
            stringBuilder.append(body);
        }
        stringBuilder.append(uri);
        stringBuilder.append(key(domaintype));
        return DigestUtils.md5Hex(stringBuilder.toString());
    }

    //将map转成String
    private String formatUrlMap(Map<String, String[]> paramMap) {
        Map<String, String> treeMap = new TreeMap<>();
        try {
            Set<String> keySet = paramMap.keySet();

            for (String key : keySet) {
                String[] valueArray = paramMap.get(key);
                String valueStr =
                        valueArray.length == 1
                                ? valueArray[0]
                                : JsonSerializer.INSTANCE.serialize(valueArray);
                treeMap.put(key.toLowerCase(), URLEncoder.encode(valueStr, "utf-8"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Joiner.on("&").withKeyValueSeparator("=").join(treeMap);
    }

    private int getInt(char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        } else if (c >= 'a' && c <= 'f') {
            return c - 'a' + 10;
        } else if (c >= 'A' && c <= 'F') {
            return c - 'A' + 10;
        } else {
            return 0;
        }
    }

    private char getChar(int i){
        if(i<10){
            return (char)('0'+i);
        }else{
            return (char)('a'+i-10);
        }
    }
}
