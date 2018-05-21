package com.csh.http;


import com.csh.model.BaiduFile;
import com.csh.utils.Constant;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.*;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class RequestProxy {

    private static Logger logger = Logger.getLogger(RequestProxy.class);

    public static JSONObject yunData = new JSONObject();

    // 连接管理器
    private static PoolingHttpClientConnectionManager pool;

    private static CloseableHttpClient httpClient = null;

    private static HttpClientContext context = null;

    // 请求配置
    private static RequestConfig requestConfig;

    private static final String TOKEN_KEY = "MYBDSTOKEN";

    private static ObjectMapper mapper = new ObjectMapper();

    private static final File jsonFile = new File(System.getProperty("user.dir") + "/config/yunData.json");

    static {
        try {
            context = HttpClientContext.create();

            // 配置超时时间（连接服务端超时1秒，请求数据返回超时60秒）
            requestConfig = RequestConfig.custom()
                    .setSocketTimeout(60000)
                    .setConnectTimeout(120000)
                    .setConnectionRequestTimeout(60000).build();

            // 设置默认跳转以及存储cookie
            httpClient = HttpClientBuilder.create()
                    .setDefaultRequestConfig(requestConfig)
                    .setDefaultHeaders(Constant.DEFAULT_HEADERS)
                    .setRedirectStrategy(new DefaultRedirectStrategy())
                    .setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy()).build();

            if (!jsonFile.exists()) {
                jsonFile.getParentFile().mkdir();
                jsonFile.createNewFile();
            }

            RequestProxy.yunData = mapper.readValue(jsonFile, JSONObject.class);

        } catch (IOException e) {
            logger.error(e);
        }
    }

    public static void setYunData(JSONObject yunData) {
        try {
            RequestProxy.yunData = yunData;
            mapper.writeValue(jsonFile, yunData);
        } catch (IOException e) {
            logger.error(e);
        }
    }

    /**
     * 获取网盘容量使用信息
     *
     * @return
     * @throws Exception
     */
    public static JSONObject getQuotaInfos() throws Exception {

        List<NameValuePair> params = new ArrayList<>();

        params.add(new BasicNameValuePair("checkexpire", "1"));
        params.add(new BasicNameValuePair("checkfree", "1"));
        params.add(new BasicNameValuePair("channel", "chunlei"));
        params.add(new BasicNameValuePair("web", "1"));
        params.add(new BasicNameValuePair("appid", "250528"));
        params.add(new BasicNameValuePair("clienttype", "0"));
        params.add(new BasicNameValuePair("bdstoken", yunData.getString(TOKEN_KEY)));
        params.add(new BasicNameValuePair("logid", "MTUwNTY2MDg0OTA1MTAuMzExNTk1MDY4OTUxODMyMw=="));

        HttpGet httpGet = new HttpGet(Constant.QUOTA_URL + URLEncodedUtils.format(params, Constant.CHARSET_UTF_8));

        CloseableHttpResponse response = httpClient.execute(httpGet, context);

        try {
            return JSONObject.fromObject(EntityUtils.toString(response.getEntity()));
        } finally {
            if (response != null) response.close();
        }
    }

    public static List<BaiduFile> getFileList(String path) throws Exception {
        List<NameValuePair> params = new ArrayList<>();

        params.add(new BasicNameValuePair("dir", path));
        params.add(new BasicNameValuePair("order", "name"));
        params.add(new BasicNameValuePair("desc", "0"));
        params.add(new BasicNameValuePair("clienttype", "0"));
        params.add(new BasicNameValuePair("showempty", "0"));
        params.add(new BasicNameValuePair("web", "1"));
        params.add(new BasicNameValuePair("channel", "chunlei"));
        params.add(new BasicNameValuePair("appid", "250528"));
        params.add(new BasicNameValuePair("bdstoken", yunData.getString(TOKEN_KEY)));
        params.add(new BasicNameValuePair("logid", "MTUwNTY2MDg0OTA1MTAuMzExNTk1MDY4OTUxODMyMw=="));

        HttpGet httpGet = new HttpGet(Constant.LIST_URL + URLEncodedUtils.format(params, Constant.CHARSET_UTF_8));

        CloseableHttpResponse response = httpClient.execute(httpGet, context);

        try {
            JSONObject json = JSONObject.fromObject(EntityUtils.toString(response.getEntity()));

            System.out.println(json.toString(1));

            if (json.has("list")) {

                JavaType javaType = mapper.getTypeFactory().constructParametricType(List.class, BaiduFile.class);

                List<BaiduFile> baiduFiles = mapper.readValue(json.getJSONArray("list").toString(), javaType);

                return baiduFiles;
            } else {
                throw new RuntimeException("文件列表获取失败！");
            }

        } finally {
            if (response != null) response.close();
        }

    }

    public static List<BaiduFile> searchFileList(String keyword) throws Exception {
        List<NameValuePair> params = new ArrayList<>();

        params.add(new BasicNameValuePair("recursion", "1"));
        params.add(new BasicNameValuePair("order", "name"));
        params.add(new BasicNameValuePair("desc", "0"));
        params.add(new BasicNameValuePair("showempty", "0"));
        params.add(new BasicNameValuePair("web", "1"));
//        params.add(new BasicNameValuePair("page", "1"));
//        params.add(new BasicNameValuePair("num", "100"));
        params.add(new BasicNameValuePair("key", keyword));
        params.add(new BasicNameValuePair("channel", "chunlei"));
        params.add(new BasicNameValuePair("appid", "250528"));
        params.add(new BasicNameValuePair("bdstoken", yunData.getString(TOKEN_KEY)));
        params.add(new BasicNameValuePair("logid", "MTUwNTY2MDg0OTA1MTAuMzExNTk1MDY4OTUxODMyMw=="));
        params.add(new BasicNameValuePair("clienttype", "0"));

        HttpGet httpGet = new HttpGet(Constant.SEARCH_URL + URLEncodedUtils.format(params, Constant.CHARSET_UTF_8));

        CloseableHttpResponse response = httpClient.execute(httpGet, context);

        try {
            JSONObject json = JSONObject.fromObject(EntityUtils.toString(response.getEntity()));

            System.out.println(json.toString(1));

            if (json.has("list")) {

                JavaType javaType = mapper.getTypeFactory().constructParametricType(List.class, BaiduFile.class);

                List<BaiduFile> baiduFiles = mapper.readValue(json.getJSONArray("list").toString(), javaType);

                return baiduFiles;
            } else {
                throw new RuntimeException("文件列表获取失败！");
            }

        } finally {
            if (response != null) response.close();
        }

    }
}
