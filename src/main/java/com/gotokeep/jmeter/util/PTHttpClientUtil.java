package com.gotokeep.jmeter.util;

import com.gotokeep.jmeter.dto.HttpClientParamDTO;
import com.gotokeep.jmeter.dto.JmeterResponseDTO;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 *
 * Created by zhaoqian on 19/4/12.
 */
public class PTHttpClientUtil {
    private static HttpClient client = null;
    private static final String UTF8="UTF-8";
    private static final String APPLICATIONJSON="application/json";
    //Header constant
    private static final String AUTHORIZATION="Authorization";
    private static final String ACCEPT="Accept";
    private static final String CONTENTTYPE="Content-Type";
    private static final String USERAGENT="User-Agent";
    private static final String XCHANNEL="X-Channel";
    private static final String XDEVICEID="X-Device-Id";
    private static final String XISNEWDEVICE="X-Is-New-Device";
    private static final String XKEEPTIMEZONE="X-Keep-Timezone";
    private static final String XLOCALE="X-Locale";
    private static final String XMANUFACTURER="X-Manufacturer";
    private static final String XMODE="X-Mode";
    private static final String XOS="X-Os";
    private static final String XOSVERSION="X-Os-Version";
    private static final String XVERSIONNAME="X-Version-Name";
    private static final String SIGN="Sign";
    private static final String XPTS="X-Pts";
    private static final String XTOKENID="x-token-id";

    private static final String XUSERID="X-User-Id";
    private static final String BEARER="Bearer ";
    private static final String GENDER="M";
    private static final String HTTP11="HTTP/1.1 ";
    private static final String NOCONTENT=" No Content";
    private static final String URLPATH="/";
    private static final String BASIC="Basic ";

    private static final String ACCEPT_ENCODING="Accept-Encoding";
    private static final String CONNECTION="Connection";
    private static final String X_ABTEST_TAGS="X-Abtest-Tags";
    private static final String X_ADS="X-Ads";
    private static final String X_APP_PLATFORM="X-App-Platform";
    private static final String X_CARRIER="X-Carrier";
    private static final String X_CONNECTION_TYPE="X-Connection-Type";
    private static final String X_GEO="X-Geo";
    private static final String X_SCREEN_HEIGHT="X-Screen-Height";
    private static final String X_SCREEN_WIDTH="X-Screen-Width";
    private static final String X_TIMESTAMP="X-Timestamp";
    private static final String X_VERSION_CODE="X-Version-Code";

    //全链路压测标识
    private static final String X_TRAFFIC_FLAG="x-traffic-flag";

    //泳道标识
    private static final String X_ROUTE_KEY="x-route-key";
    private PTHttpClientUtil() {

    }

    static {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(128);
        cm.setDefaultMaxPerRoute(128);
        client = HttpClients.custom().setConnectionManager(cm).build();
    }

    public static JmeterResponseDTO jmeterdoPOSTJson(HttpClientParamDTO httpClientParamDTO){
        return jmeterPOST(httpClientParamDTO, APPLICATIONJSON, UTF8, null, null);
    }

    public static String doPostJson(HttpClientParamDTO httpClientParamDTO){
        return post(httpClientParamDTO, APPLICATIONJSON, UTF8, null, null);
    }

    public static String doPostForm(HttpClientParamDTO httpClientParamDTO){
        return postForm(httpClientParamDTO, null, null);
    }

    private static JmeterResponseDTO jmeterPOST(HttpClientParamDTO httpClientParamDTO, String mimeType,
                               String charset, Integer connTimeout, Integer readTimeout)
    {
        HttpClient client = null;
        HttpPost post = new HttpPost(httpClientParamDTO.getDomain()+httpClientParamDTO.getUrl());
        String result = "";
        JmeterResponseDTO jmeterResponseDTO=new JmeterResponseDTO();
        try {
            if (StringUtils.isNotBlank(httpClientParamDTO.getPostBodyJson())) {
                HttpEntity entity = new StringEntity(httpClientParamDTO.getPostBodyJson(), ContentType.create(
                        mimeType, charset));
                post.setEntity(entity);
            }
            // 设置参数
            RequestConfig.Builder customReqConf = RequestConfig.custom();
            if (connTimeout != null) {
                customReqConf.setConnectTimeout(connTimeout);
            }
            if (readTimeout != null) {
                customReqConf.setSocketTimeout(readTimeout);
            }
            if(StringUtils.isNotBlank(httpClientParamDTO.getUserId())&&StringUtils.isNotBlank(httpClientParamDTO.getEnv())){
                JWTUtil jwtUtil=new JWTUtil();
                post.setHeader(AUTHORIZATION, BEARER + jwtUtil.creatToken(httpClientParamDTO.getEnv(), httpClientParamDTO.getUserId(), "", GENDER));
                post.setHeader(XUSERID,httpClientParamDTO.getUserId());
            }
//            post.setHeader(ACCEPT,APPLICATIONJSON);
            post.setHeader(CONTENTTYPE,APPLICATIONJSON);
            if(httpClientParamDTO.getHeader()!=null) {
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getUa())) {
                    post.setHeader(USERAGENT, httpClientParamDTO.getHeader().getUa());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getChannel())) {
                    post.setHeader(XCHANNEL, httpClientParamDTO.getHeader().getChannel());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getDeviceId())) {
                    post.setHeader(XDEVICEID, httpClientParamDTO.getHeader().getDeviceId());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getIsnewdevice())) {
                    post.setHeader(XISNEWDEVICE, httpClientParamDTO.getHeader().getIsnewdevice());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getTimezone())) {
                    post.setHeader(XKEEPTIMEZONE, httpClientParamDTO.getHeader().getTimezone());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getLocale())) {
                    post.setHeader(XLOCALE, httpClientParamDTO.getHeader().getLocale());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getManufacturer())) {
                    post.setHeader(XMANUFACTURER, httpClientParamDTO.getHeader().getManufacturer());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getModel())) {
                    post.setHeader(XMODE, httpClientParamDTO.getHeader().getModel());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getOs())) {
                    post.setHeader(XOS, httpClientParamDTO.getHeader().getOs());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getOsversion())) {
                    post.setHeader(XOSVERSION, httpClientParamDTO.getHeader().getUa());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getKeepversion())) {
                    post.setHeader(XVERSIONNAME, httpClientParamDTO.getHeader().getKeepversion());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getRoutekey())) {
                    post.setHeader(X_ROUTE_KEY, httpClientParamDTO.getHeader().getRoutekey());
                }
            }
            if(StringUtils.isNotBlank(httpClientParamDTO.getEnv())){
                post.setHeader(XPTS, httpClientParamDTO.getEnv());
                //默认走全链路压测
                //post.setHeader(X_TRAFFIC_FLAG, "true");
            }
            if(httpClientParamDTO.getEnv()!=null&&httpClientParamDTO.getUrl()!=null&&httpClientParamDTO.getPostBodyJson()!=null) {
                POSTSignUtil postSignUtils = new POSTSignUtil();
                String uri = "";
                for (int i = 0; i < (httpClientParamDTO.getDomain()+httpClientParamDTO.getUrl()).split(URLPATH).length; i++) {
                    if (i >= 3)
                        uri += URLPATH + (httpClientParamDTO.getDomain()+httpClientParamDTO.getUrl()).split(URLPATH)[i];
                }
                try {
                    post.setHeader(SIGN, postSignUtils.DeviceIdSign(httpClientParamDTO.getEnv(), new HashMap<>(), httpClientParamDTO.getPostBodyJson(), uri));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            post.setConfig(customReqConf.build());

            HttpResponse res = null;
            if (httpClientParamDTO.getUrl().startsWith("https")) {
                // 执行 Https 请求.
                client = createSSLInsecureClient();
                try {
                    res = client.execute(post);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                // 执行 Http 请求.
                client = PTHttpClientUtil.client;
                try {
                    res = client.execute(post);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(res==null){
                return null;
            }
            if(res.getStatusLine().getStatusCode()==204){
                result=HTTP11+res.getStatusLine().getStatusCode()+NOCONTENT;
                jmeterResponseDTO.setResponse(result);
                jmeterResponseDTO.setUrl(httpClientParamDTO.getDomain() + httpClientParamDTO.getUrl());
                jmeterResponseDTO.setSuccess(true);
            }else {
                try {
                    result = IOUtils.toString(res.getEntity().getContent(), charset);
                    jmeterResponseDTO.setResponse(result);
                    jmeterResponseDTO.setUrl(httpClientParamDTO.getDomain()+httpClientParamDTO.getUrl());
                    jmeterResponseDTO.setSuccess(res.getStatusLine().getStatusCode() == 200 || res.getStatusLine().getStatusCode() == 400);
                } catch (IOException e) {
                    System.out.println(e);
                }
            }
        } finally {
            post.releaseConnection();
            if (httpClientParamDTO.getUrl().startsWith("https") && client != null
                    && client instanceof CloseableHttpClient) {
                try {
                    ((CloseableHttpClient) client).close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return jmeterResponseDTO;
    }


    private static String post(HttpClientParamDTO httpClientParamDTO, String mimeType,
                               String charset, Integer connTimeout, Integer readTimeout)
    {
        HttpClient client = null;
        HttpPost post = new HttpPost(httpClientParamDTO.getDomain()+httpClientParamDTO.getUrl());
        String result = "";
        try {
            if (StringUtils.isNotBlank(httpClientParamDTO.getPostBodyJson())) {
                HttpEntity entity = new StringEntity(httpClientParamDTO.getPostBodyJson(), ContentType.create(
                        mimeType, charset));
                post.setEntity(entity);
            }
            // 设置参数
            RequestConfig.Builder customReqConf = RequestConfig.custom();
            if (connTimeout != null) {
                customReqConf.setConnectTimeout(connTimeout);
            }
            if (readTimeout != null) {
                customReqConf.setSocketTimeout(readTimeout);
            }
            if(StringUtils.isNotBlank(httpClientParamDTO.getUserId())&&StringUtils.isNotBlank(httpClientParamDTO.getEnv())){
                JWTUtil jwtUtil=new JWTUtil();
                post.setHeader(AUTHORIZATION, BEARER + jwtUtil.creatToken(httpClientParamDTO.getEnv(), httpClientParamDTO.getUserId(), "", GENDER));
                post.setHeader(XUSERID,httpClientParamDTO.getUserId());
            }
            post.setHeader(ACCEPT,APPLICATIONJSON);
            post.setHeader(CONTENTTYPE,APPLICATIONJSON);
            if(httpClientParamDTO.getHeader()!=null) {
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getUa())) {
                    post.setHeader(USERAGENT, httpClientParamDTO.getHeader().getUa());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getChannel())) {
                    post.setHeader(XCHANNEL, httpClientParamDTO.getHeader().getChannel());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getDeviceId())) {
                    post.setHeader(XDEVICEID, httpClientParamDTO.getHeader().getDeviceId());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getIsnewdevice())) {
                    post.setHeader(XISNEWDEVICE, httpClientParamDTO.getHeader().getIsnewdevice());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getTimezone())) {
                    post.setHeader(XKEEPTIMEZONE, httpClientParamDTO.getHeader().getTimezone());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getLocale())) {
                    post.setHeader(XLOCALE, httpClientParamDTO.getHeader().getLocale());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getManufacturer())) {
                    post.setHeader(XMANUFACTURER, httpClientParamDTO.getHeader().getManufacturer());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getModel())) {
                    post.setHeader(XMODE, httpClientParamDTO.getHeader().getModel());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getOs())) {
                    post.setHeader(XOS, httpClientParamDTO.getHeader().getOs());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getOsversion())) {
                    post.setHeader(XOSVERSION, httpClientParamDTO.getHeader().getUa());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getKeepversion())) {
                    post.setHeader(XVERSIONNAME, httpClientParamDTO.getHeader().getKeepversion());
                }
            }
            if(StringUtils.isNotBlank(httpClientParamDTO.getEnv())){
                post.setHeader(XPTS, httpClientParamDTO.getEnv());
            }
//            System.out.println("url:"+httpClientParamDTO.getUrl());
            if(httpClientParamDTO.getEnv()!=null&&httpClientParamDTO.getUrl()!=null&&httpClientParamDTO.getPostBodyJson()!=null) {
                POSTSignUtil postSignUtils = new POSTSignUtil();
                String uri = "";
                for (int i = 0; i < (httpClientParamDTO.getDomain()+httpClientParamDTO.getUrl()).split(URLPATH).length; i++) {
                    if (i >= 3)
                        uri += URLPATH + (httpClientParamDTO.getDomain()+httpClientParamDTO.getUrl()).split(URLPATH)[i];
                }
//                System.out.println("uri:"+uri);
                try {
                    post.setHeader(SIGN, postSignUtils.DeviceIdSign(httpClientParamDTO.getEnv(), new HashMap<>(), httpClientParamDTO.getPostBodyJson(), uri));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            post.setConfig(customReqConf.build());

            HttpResponse res = null;
            if (httpClientParamDTO.getUrl().startsWith("https")) {
                // 执行 Https 请求.
                client = createSSLInsecureClient();
                try {
                    res = client.execute(post);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                // 执行 Http 请求.
                client = PTHttpClientUtil.client;
                try {
                    res = client.execute(post);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(res==null){
                return null;
            }
            if(res.getStatusLine().getStatusCode()==204){
                result=HTTP11+res.getStatusLine().getStatusCode()+NOCONTENT;
            }else {
                try {
                    result = IOUtils.toString(res.getEntity().getContent(), charset);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } finally {
            post.releaseConnection();
            if (httpClientParamDTO.getUrl().startsWith("https") && client != null
                    && client instanceof CloseableHttpClient) {
                try {
                    ((CloseableHttpClient) client).close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    private static String postForm(HttpClientParamDTO httpClientParamDTO,
                                   Integer connTimeout,
                                   Integer readTimeout){
        HttpClient client = null;

        HttpPost post = new HttpPost(httpClientParamDTO.getDomain()+httpClientParamDTO.getUrl());
        String result = "";
        try {
            if (httpClientParamDTO.getPostBodyForm() != null && !httpClientParamDTO.getPostBodyForm().isEmpty()) {
                List<NameValuePair> formParams = new ArrayList<>();
                Set<Map.Entry<String, String>> entrySet = httpClientParamDTO.getPostBodyForm().entrySet();
                for (Map.Entry<String, String> entry : entrySet) {
                    formParams.add(new BasicNameValuePair(entry.getKey(), entry
                            .getValue()));
                }
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
                        formParams, Consts.UTF_8);
                post.setEntity(entity);
            }
            // 设置参数
            RequestConfig.Builder customReqConf = RequestConfig.custom();
            if (connTimeout != null) {
                customReqConf.setConnectTimeout(connTimeout);
            }
            if (readTimeout != null) {
                customReqConf.setSocketTimeout(readTimeout);
            }
            if(StringUtils.isNotBlank(httpClientParamDTO.getUserId())&&StringUtils.isNotBlank(httpClientParamDTO.getEnv())){
                JWTUtil jwtUtil=new JWTUtil();
                post.setHeader(AUTHORIZATION, BEARER + jwtUtil.creatToken(httpClientParamDTO.getEnv(), httpClientParamDTO.getUserId(), "", GENDER));
                post.setHeader(XUSERID,httpClientParamDTO.getUserId());
            }
            if(httpClientParamDTO.getHeader()!=null) {
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getUa())) {
                    post.setHeader(USERAGENT, httpClientParamDTO.getHeader().getUa());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getChannel())) {
                    post.setHeader(XCHANNEL, httpClientParamDTO.getHeader().getChannel());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getDeviceId())) {
                    post.setHeader(XDEVICEID, httpClientParamDTO.getHeader().getDeviceId());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getIsnewdevice())) {
                    post.setHeader(XISNEWDEVICE, httpClientParamDTO.getHeader().getIsnewdevice());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getTimezone())) {
                    post.setHeader(XKEEPTIMEZONE, httpClientParamDTO.getHeader().getTimezone());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getLocale())) {
                    post.setHeader(XLOCALE, httpClientParamDTO.getHeader().getLocale());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getManufacturer())) {
                    post.setHeader(XMANUFACTURER, httpClientParamDTO.getHeader().getManufacturer());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getModel())) {
                    post.setHeader(XMODE, httpClientParamDTO.getHeader().getModel());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getOs())) {
                    post.setHeader(XOS, httpClientParamDTO.getHeader().getOs());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getOsversion())) {
                    post.setHeader(XOSVERSION, httpClientParamDTO.getHeader().getUa());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getKeepversion())) {
                    post.setHeader(XVERSIONNAME, httpClientParamDTO.getHeader().getKeepversion());
                }
            }
            if(StringUtils.isNotBlank(httpClientParamDTO.getEnv())){
                post.setHeader(XPTS, httpClientParamDTO.getEnv());
            }
            post.setConfig(customReqConf.build());
            HttpResponse res = null;
            if (httpClientParamDTO.getUrl().startsWith("https")) {
                // 执行 Https 请求.
                client = createSSLInsecureClient();
                try {
                    res = client.execute(post);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                // 执行 Http 请求.
                client = PTHttpClientUtil.client;
                try {
                    res = client.execute(post);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(res==null){
                return null;
            }
            if(res.getStatusLine().getStatusCode()==204){
                result=HTTP11+res.getStatusLine().getStatusCode()+NOCONTENT;
            }else {
                try {
                    result = IOUtils.toString(res.getEntity().getContent(), UTF8);
                } catch (IOException e) {
                    System.out.println(e);
                }
            }
        } finally {
            post.releaseConnection();
            if (httpClientParamDTO.getUrl().startsWith("https") && client != null
                    && client instanceof CloseableHttpClient) {
                try {
                    ((CloseableHttpClient) client).close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public static String doPut(HttpClientParamDTO httpClientParamDTO){
        return put(httpClientParamDTO,APPLICATIONJSON, UTF8, null, null);
    }

    private static String put(HttpClientParamDTO httpClientParamDTO,String mimeType,
                              String charset, Integer connTimeout, Integer readTimeout){
        HttpClient client = null;
        HttpPut put = new HttpPut(httpClientParamDTO.getDomain()+httpClientParamDTO.getUrl());
        String result = "";
        try {
            if (StringUtils.isNotBlank(httpClientParamDTO.getPostBodyJson())) {
                HttpEntity entity = new StringEntity(httpClientParamDTO.getPostBodyJson(), ContentType.create(
                        mimeType, charset));
                put.setEntity(entity);
            }
            // 设置参数
            RequestConfig.Builder customReqConf = RequestConfig.custom();
            if (connTimeout != null) {
                customReqConf.setConnectTimeout(connTimeout);
            }
            if (readTimeout != null) {
                customReqConf.setSocketTimeout(readTimeout);
            }
            if(StringUtils.isNotBlank(httpClientParamDTO.getUserId())&&StringUtils.isNotBlank(httpClientParamDTO.getEnv())){
                JWTUtil jwtUtil=new JWTUtil();
                put.setHeader(AUTHORIZATION, BEARER + jwtUtil.creatToken(httpClientParamDTO.getEnv(), httpClientParamDTO.getUserId(), "", GENDER));
                put.setHeader(XUSERID,httpClientParamDTO.getUserId());
            }
            if(httpClientParamDTO.getHeader()!=null) {
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getUa())) {
                    put.setHeader(USERAGENT, httpClientParamDTO.getHeader().getUa());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getChannel())) {
                    put.setHeader(XCHANNEL, httpClientParamDTO.getHeader().getChannel());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getDeviceId())) {
                    put.setHeader(XDEVICEID, httpClientParamDTO.getHeader().getDeviceId());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getIsnewdevice())) {
                    put.setHeader(XISNEWDEVICE, httpClientParamDTO.getHeader().getIsnewdevice());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getTimezone())) {
                    put.setHeader(XKEEPTIMEZONE, httpClientParamDTO.getHeader().getTimezone());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getLocale())) {
                    put.setHeader(XLOCALE, httpClientParamDTO.getHeader().getLocale());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getManufacturer())) {
                    put.setHeader(XMANUFACTURER, httpClientParamDTO.getHeader().getManufacturer());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getModel())) {
                    put.setHeader(XMODE, httpClientParamDTO.getHeader().getModel());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getOs())) {
                    put.setHeader(XOS, httpClientParamDTO.getHeader().getOs());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getOsversion())) {
                    put.setHeader(XOSVERSION, httpClientParamDTO.getHeader().getUa());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getKeepversion())) {
                    put.setHeader(XVERSIONNAME, httpClientParamDTO.getHeader().getKeepversion());
                }
            }
            if(StringUtils.isNotBlank(httpClientParamDTO.getEnv())){
                put.setHeader(XPTS, httpClientParamDTO.getEnv());
            }
            put.setConfig(customReqConf.build());

            HttpResponse res = null;
                // 执行 Http 请求.
                client = PTHttpClientUtil.client;
                try {
                    res = client.execute(put);
                } catch (IOException e) {
                    System.out.println(e);
                }
            if(res==null){
                return null;
            }
            try {
                result = IOUtils.toString(res.getEntity().getContent(), charset);
            } catch (IOException e) {
                System.out.println(e);
            }
        } finally {
            put.releaseConnection();
        }
        return result;
    }

    public static String doDelete(HttpClientParamDTO httpClientParamDTO){
        return delete(httpClientParamDTO, UTF8, null, null);
    }

    private static String delete(HttpClientParamDTO httpClientParamDTO,
                                 String charset, Integer connTimeout, Integer readTimeout){
        HttpClient client = null;
        HttpDelete delete = new HttpDelete(httpClientParamDTO.getDomain()+httpClientParamDTO.getUrl());
        String result = "";
        try {

            // 设置参数
            RequestConfig.Builder customReqConf = RequestConfig.custom();
            if (connTimeout != null) {
                customReqConf.setConnectTimeout(connTimeout);
            }
            if (readTimeout != null) {
                customReqConf.setSocketTimeout(readTimeout);
            }
            if(StringUtils.isNotBlank(httpClientParamDTO.getUserId())&&StringUtils.isNotBlank(httpClientParamDTO.getEnv())){
                JWTUtil jwtUtil=new JWTUtil();
                delete.setHeader(AUTHORIZATION, BEARER + jwtUtil.creatToken(httpClientParamDTO.getEnv(), httpClientParamDTO.getUserId(), "", GENDER));
                delete.setHeader(XUSERID,httpClientParamDTO.getUserId());
            }
            if(httpClientParamDTO.getHeader()!=null) {
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getUa())) {
                    delete.setHeader(USERAGENT, httpClientParamDTO.getHeader().getUa());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getChannel())) {
                    delete.setHeader(XCHANNEL, httpClientParamDTO.getHeader().getChannel());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getDeviceId())) {
                    delete.setHeader(XDEVICEID, httpClientParamDTO.getHeader().getDeviceId());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getIsnewdevice())) {
                    delete.setHeader(XISNEWDEVICE, httpClientParamDTO.getHeader().getIsnewdevice());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getTimezone())) {
                    delete.setHeader(XKEEPTIMEZONE, httpClientParamDTO.getHeader().getTimezone());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getLocale())) {
                    delete.setHeader(XLOCALE, httpClientParamDTO.getHeader().getLocale());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getManufacturer())) {
                    delete.setHeader(XMANUFACTURER, httpClientParamDTO.getHeader().getManufacturer());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getModel())) {
                    delete.setHeader(XMODE, httpClientParamDTO.getHeader().getModel());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getOs())) {
                    delete.setHeader(XOS, httpClientParamDTO.getHeader().getOs());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getOsversion())) {
                    delete.setHeader(XOSVERSION, httpClientParamDTO.getHeader().getUa());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getKeepversion())) {
                    delete.setHeader(XVERSIONNAME, httpClientParamDTO.getHeader().getKeepversion());
                }
            }
            if(StringUtils.isNotBlank(httpClientParamDTO.getEnv())){
                delete.setHeader(XPTS, httpClientParamDTO.getEnv());
            }
            delete.setConfig(customReqConf.build());

            HttpResponse res = null;
                // 执行 Http 请求.
                client = PTHttpClientUtil.client;
                try {
                    res = client.execute(delete);
                } catch (IOException e) {
                    System.out.println(e);
                }

            if(res==null){
                return null;
            }
            try {
                result = IOUtils.toString(res.getEntity().getContent(), charset);
            } catch (IOException e) {
                System.out.println(e);
            }
        } finally {
            delete.releaseConnection();
        }
        return result;
    }

    public static String doGet(HttpClientParamDTO httpClientParamDTO){
        return get(httpClientParamDTO, UTF8, null, null);
    }

    private static String get(HttpClientParamDTO httpClientParamDTO,String charset, Integer connTimeout,
                              Integer readTimeout){
        HttpClient client = null;

        HttpGet get = new HttpGet(httpClientParamDTO.getDomain()+httpClientParamDTO.getUrl());
        String result = "";
        try {
            // 设置参数
            RequestConfig.Builder customReqConf = RequestConfig.custom();
            if (connTimeout != null) {
                customReqConf.setConnectTimeout(connTimeout);
            }
            if (readTimeout != null) {
                customReqConf.setSocketTimeout(readTimeout);
            }

            get.setConfig(customReqConf.build());
            if(StringUtils.isNotBlank(httpClientParamDTO.getUserId())&&StringUtils.isNotBlank(httpClientParamDTO.getEnv())&&StringUtils.isNotBlank(httpClientParamDTO.getType())) {
                JWTUtil jwtUtil = new JWTUtil();
                get.setHeader(AUTHORIZATION, BEARER + jwtUtil.creatToken(httpClientParamDTO.getEnv(), httpClientParamDTO.getUserId(), "", GENDER));
                get.setHeader(XUSERID,httpClientParamDTO.getUserId());
            }
            if(httpClientParamDTO.getHeader()!=null) {
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getUa())) {
                    get.setHeader(USERAGENT, httpClientParamDTO.getHeader().getUa());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getChannel())) {
                    get.setHeader(XCHANNEL, httpClientParamDTO.getHeader().getChannel());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getDeviceId())) {
                    get.setHeader(XDEVICEID, httpClientParamDTO.getHeader().getDeviceId());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getIsnewdevice())) {
                    get.setHeader(XISNEWDEVICE, httpClientParamDTO.getHeader().getIsnewdevice());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getTimezone())) {
                    get.setHeader(XKEEPTIMEZONE, httpClientParamDTO.getHeader().getTimezone());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getLocale())) {
                    get.setHeader(XLOCALE, httpClientParamDTO.getHeader().getLocale());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getManufacturer())) {
                    get.setHeader(XMANUFACTURER, httpClientParamDTO.getHeader().getManufacturer());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getModel())) {
                    get.setHeader(XMODE, httpClientParamDTO.getHeader().getModel());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getOs())) {
                    get.setHeader(XOS, httpClientParamDTO.getHeader().getOs());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getOsversion())) {
                    get.setHeader(XOSVERSION, httpClientParamDTO.getHeader().getUa());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getKeepversion())) {
                    get.setHeader(XVERSIONNAME, httpClientParamDTO.getHeader().getKeepversion());
                }
            }
            if(StringUtils.isNotBlank(httpClientParamDTO.getXTOKENID())&&!httpClientParamDTO.getXTOKENID().equals("0")){
                get.setHeader(XTOKENID, httpClientParamDTO.getXTOKENID());
            }
            if(StringUtils.isNotBlank(httpClientParamDTO.getEnv())){
                get.setHeader(XPTS, httpClientParamDTO.getEnv());
            }
            HttpResponse res = null;
            if (httpClientParamDTO.getUrl().startsWith("https")) {
                // 执行 Https 请求.
                client = createSSLInsecureClient();
                try {
                    res = client.execute(get);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                // 执行 Http 请求.
                client = PTHttpClientUtil.client;
                try {
                    res = client.execute(get);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(res==null){
                return null;
            }
            if(res.getStatusLine().getStatusCode()!=200&&res.getStatusLine().getStatusCode()!=204&&res.getStatusLine().getStatusCode()!=400){
                System.out.println("url:"+httpClientParamDTO.getUrl()+" http_status:"+res.getStatusLine().getStatusCode());
            }else {
                try {
                    result = IOUtils.toString(res.getEntity().getContent(), charset);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
//            System.out.println("http_status:"+res.getStatusLine().getStatusCode());
        } finally {
            get.releaseConnection();
            if (httpClientParamDTO.getUrl().startsWith("https") && client != null
                    && client instanceof CloseableHttpClient) {
                try {
                    ((CloseableHttpClient) client).close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public static JmeterResponseDTO jmeterdoGet(HttpClientParamDTO httpClientParamDTO){
        return jmeterget(httpClientParamDTO, UTF8, null, null);
    }

    private static JmeterResponseDTO jmeterget(HttpClientParamDTO httpClientParamDTO,String charset, Integer connTimeout,
                              Integer readTimeout){
        HttpClient client = null;
        JmeterResponseDTO jmeterResponseDTO=new JmeterResponseDTO();
        HttpGet get = new HttpGet(httpClientParamDTO.getDomain()+httpClientParamDTO.getUrl());
        try {
            // 设置参数
            RequestConfig.Builder customReqConf = RequestConfig.custom();
            if (connTimeout != null) {
                customReqConf.setConnectTimeout(connTimeout);
            }
            if (readTimeout != null) {
                customReqConf.setSocketTimeout(readTimeout);
            }
            if(StringUtils.isNotBlank(httpClientParamDTO.getUserId())&&StringUtils.isNotBlank(httpClientParamDTO.getEnv())&&StringUtils.isNotBlank(httpClientParamDTO.getType())) {
                JWTUtil jwtUtil = new JWTUtil();
                get.setHeader(AUTHORIZATION, BEARER + jwtUtil.creatToken(httpClientParamDTO.getEnv(), httpClientParamDTO.getUserId(), "", GENDER));
                get.setHeader(XUSERID,httpClientParamDTO.getUserId());
                get.setHeader(XDEVICEID, httpClientParamDTO.getUserId());
            }
            if(httpClientParamDTO.getHeader()!=null) {
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getAccept_encoding())) {
                    get.setHeader(ACCEPT_ENCODING, httpClientParamDTO.getHeader().getAccept_encoding());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getConnection())) {
                    get.setHeader(CONNECTION, httpClientParamDTO.getHeader().getConnection());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getUa())) {
                    get.setHeader(USERAGENT, httpClientParamDTO.getHeader().getUa());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getX_abtest_tags())) {
                    get.setHeader(X_ABTEST_TAGS, httpClientParamDTO.getHeader().getX_abtest_tags());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getX_ads())) {
                    get.setHeader(X_ADS, httpClientParamDTO.getHeader().getX_ads());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getX_app_platform())) {
                    get.setHeader(X_APP_PLATFORM, httpClientParamDTO.getHeader().getX_app_platform());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getX_carrier())) {
                    get.setHeader(X_CARRIER, httpClientParamDTO.getHeader().getX_carrier());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getChannel())) {
                    get.setHeader(XCHANNEL, httpClientParamDTO.getHeader().getChannel());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getX_connection_type())) {
                    get.setHeader(X_CONNECTION_TYPE, httpClientParamDTO.getHeader().getX_connection_type());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getDeviceId())) {
                    get.setHeader(XDEVICEID, httpClientParamDTO.getHeader().getDeviceId());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getX_geo())) {
                    get.setHeader(X_GEO, httpClientParamDTO.getHeader().getX_geo());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getIsnewdevice())) {
                    get.setHeader(XISNEWDEVICE, httpClientParamDTO.getHeader().getIsnewdevice());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getTimezone())) {
                    get.setHeader(XKEEPTIMEZONE, httpClientParamDTO.getHeader().getTimezone());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getLocale())) {
                    get.setHeader(XLOCALE, httpClientParamDTO.getHeader().getLocale());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getManufacturer())) {
                    get.setHeader(XMANUFACTURER, httpClientParamDTO.getHeader().getManufacturer());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getModel())) {
                    get.setHeader(XMODE, httpClientParamDTO.getHeader().getModel());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getOs())) {
                    get.setHeader(XOS, httpClientParamDTO.getHeader().getOs());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getOsversion())) {
                    get.setHeader(XOSVERSION, httpClientParamDTO.getHeader().getUa());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getX_screen_height())) {
                    get.setHeader(X_SCREEN_HEIGHT, httpClientParamDTO.getHeader().getX_screen_height());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getX_screen_width())) {
                    get.setHeader(X_SCREEN_WIDTH, httpClientParamDTO.getHeader().getX_screen_width());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getTimestamp())) {
                    get.setHeader(X_TIMESTAMP, httpClientParamDTO.getHeader().getTimestamp());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getX_version_code())) {
                    get.setHeader(X_VERSION_CODE, httpClientParamDTO.getHeader().getX_version_code());
                }
                if (StringUtils.isNotBlank(httpClientParamDTO.getHeader().getKeepversion())) {
                    get.setHeader(XVERSIONNAME, httpClientParamDTO.getHeader().getKeepversion());
                }
            }
            if(StringUtils.isNotBlank(httpClientParamDTO.getXTOKENID())&&!httpClientParamDTO.getXTOKENID().equals("0")){
                get.setHeader(XTOKENID, httpClientParamDTO.getXTOKENID());
            }
            if(StringUtils.isNotBlank(httpClientParamDTO.getEnv())){
                get.setHeader(XPTS, httpClientParamDTO.getEnv());
            }
            get.setConfig(customReqConf.build());
            HttpResponse res = null;

            // 执行 Http 请求.
            client = PTHttpClientUtil.client;
            try {
                res = client.execute(get);
                jmeterResponseDTO.setResponseCode(Integer.toString(res.getStatusLine().getStatusCode()));
                jmeterResponseDTO.setUrl(httpClientParamDTO.getDomain()+httpClientParamDTO.getUrl());
                jmeterResponseDTO.setSuccess(res.getStatusLine().getStatusCode() == 200 || res.getStatusLine().getStatusCode() == 204 || res.getStatusLine().getStatusCode() == 400);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(res==null){
                return null;
            }
            try {
                String result = IOUtils.toString(res.getEntity().getContent(), charset);
                jmeterResponseDTO.setResponse(result);
            } catch (IOException e) {
                e.printStackTrace();
            }

//            System.out.println("http_status:"+res.getStatusLine().getStatusCode());
        } finally {
            get.releaseConnection();
        }
        return jmeterResponseDTO;
    }

    private static CloseableHttpClient createSSLInsecureClient()
    {
        SSLContext sslContext = null;
        try {
            sslContext = new SSLContextBuilder().loadTrustMaterial(
                    null, (chain, authType) -> true).build();
        } catch (NoSuchAlgorithmException | KeyManagementException | KeyStoreException e) {
            e.printStackTrace();
        }
        assert sslContext != null;
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                sslContext, new X509HostnameVerifier() {

            @Override
            public boolean verify(String arg0, SSLSession arg1) {
                return arg0.equalsIgnoreCase(arg1.getPeerHost());
            }

            //do noting
            @Override
            public void verify(String host, SSLSocket ssl) throws IOException {
            }

            //do noting
            @Override
            public void verify(String host, X509Certificate cert) throws SSLException {
            }

            //do noting
            @Override
            public void verify(String host, String[] cns,
                               String[] subjectAlts) throws SSLException {
            }

        });
        return HttpClients.custom().setSSLSocketFactory(sslsf).build();
    }
}
