package com.bridge.utils.rpc.http;

import com.bridge.exception.BridgeProcessFailException;
import com.bridge.enums.BridgeErrorEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author Jay
 * @version v1.0
 * @description http工具类
 * @date 2019-01-03 17:56
 */
@Slf4j
public class HttpClientUtils {

    /**
     * 连接池对象
     */
    private static PoolingHttpClientConnectionManager cm;

    /**
     * 编码为UTF-8
     */
    private static final String UTF_8 = "UTF-8";

    /**
     * http请求成功的返回值
     */
    private static final int SUCCESS_CODE = 200;

    /**
     * 整个连接池的最大连接数
     */
    private static final int MAX_TOTAL = 500;

    /**
     * 每个route默认的最大连接数
     */
    private static final int DEFAULT_MAX_PER_ROUTE = 50;

    /**
     * 获取CloseableHttpClient对象
     */
    private static CloseableHttpClient httpClient = getHttpClient();

    /**
     * 不带参数的get请求
     *
     * @param url
     * @return
     */
    public static String httpGetRequest(String url) {
        HttpGet httpGet = new HttpGet(url);
        return getResult(httpGet);
    }

    /**
     * 带参数的get请求
     *
     * @param url
     * @param params
     * @return
     */
    public static String httpGetRequest(String url, Map<String, Object> params) {
        return httpGetRequest(url, null, params);
    }


    /**
     * 不带参数的post请求
     *
     * @param url
     * @return
     */
    public static String httpPostRequest(String url) {
        HttpPost httpPost = new HttpPost(url);
        return getResult(httpPost);
    }


    /**
     * 带参数的post请求
     *
     * @param url
     * @param params
     * @return
     */
    public static String httpPostRequest(String url, Map<String, Object> params) {
        return httpPostRequest(url, null, params);
    }

    //--------------------------------------------private method---------------------------------------------

    /**
     * 初始化连接池
     */
    private static void init() {
        if (cm == null) {
            cm = new PoolingHttpClientConnectionManager();
            cm.setMaxTotal(MAX_TOTAL);
            cm.setDefaultMaxPerRoute(DEFAULT_MAX_PER_ROUTE);
        }
    }

    /**
     * 初始化对象
     *
     * @return
     */
    private static CloseableHttpClient getHttpClient() {
        init();
        return HttpClients.custom().setConnectionManager(cm).build();
    }


    /**
     * get请求
     *
     * @param url
     * @param headers
     * @param params
     * @return
     */
    private static String httpGetRequest(String url, Map<String, Object> headers, Map<String, Object> params) {
        URIBuilder ub = new URIBuilder();
        ub.setPath(url);

        ArrayList<NameValuePair> pairs = covertParams(params);
        ub.setParameters(pairs);

        HttpGet httpGet;
        try {
            httpGet = new HttpGet(ub.build());
        } catch (URISyntaxException e) {
            log.error("构建连接错误,url -> {},异常信息为:", url, e);
            throw new BridgeProcessFailException(BridgeErrorEnum.RPC_ERROR);
        }
        if (headers != null) {
            for (Map.Entry<String, Object> param : headers.entrySet()) {
                httpGet.addHeader(param.getKey(), String.valueOf(param.getValue()));
            }
        }
        return getResult(httpGet);
    }


    /**
     * 发送post请求
     *
     * @param url
     * @param headers
     * @param params
     * @return
     */
    private static String httpPostRequest(String url, Map<String, Object> headers, Map<String, Object> params) {
        HttpPost httpPost = new HttpPost(url);
        if (headers != null) {
            for (Map.Entry<String, Object> param : headers.entrySet()) {
                httpPost.addHeader(param.getKey(), String.valueOf(param.getValue()));
            }
        }
        ArrayList<NameValuePair> pairs = covertParams(params);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(pairs, UTF_8));
        } catch (UnsupportedEncodingException e) {
            log.error("设置编码错误,url - > {}, 异常信息为:", url, e);
            throw new BridgeProcessFailException(BridgeErrorEnum.RPC_ERROR.getCode(), e.getMessage());
        }
        return getResult(httpPost);
    }


    /**
     * map内参数转list
     *
     * @param params
     * @return
     */
    private static ArrayList<NameValuePair> covertParams(Map<String, Object> params) {
        ArrayList<NameValuePair> pairs = new ArrayList<>();
        for (Map.Entry<String, Object> param : params.entrySet()) {
            pairs.add(new BasicNameValuePair(param.getKey(), String.valueOf(param.getValue())));
        }
        return pairs;
    }


    /**
     * 处理返回的结果集
     *
     * @param request
     * @return
     */
    private static String getResult(HttpRequestBase request) {
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(request);
            // 成功的时候返回结果集，失败直接抛异常
            if (response.getStatusLine().getStatusCode() == SUCCESS_CODE) {
                HttpEntity entity = response.getEntity();
                if (entity == null) {
                    throw new BridgeProcessFailException(BridgeErrorEnum.RPC_ERROR);
                }
                return EntityUtils.toString(entity, "utf-8");
            }
            throw new BridgeProcessFailException(BridgeErrorEnum.RPC_ERROR);
        } catch (Exception e) {
            log.error("远程调用异常, url -> {}, 异常信息为:", request.getURI().toString(), e);
            throw new BridgeProcessFailException(BridgeErrorEnum.RPC_ERROR);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                log.error("释放连接异常，异常信息为:", e);
            }
        }
    }
}

