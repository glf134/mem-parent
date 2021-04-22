package com.wbr.model.mem.utils;


import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author glf
 */
public class HttpUtils {
    private static final Logger log = LoggerFactory.getLogger(HttpUtils.class);

    public HttpUtils() {
    }

    public static String doPost(String url, String data, int timeout) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(timeout).setConnectionRequestTimeout(timeout).setSocketTimeout(timeout).setRedirectsEnabled(true).build();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        httpPost.addHeader("Content-Type", "text/html; chartset=UTF-8");
        if (data != null && data instanceof String) {
            StringEntity stringEntity = new StringEntity(data, "UTF-8");
            httpPost.setEntity(stringEntity);
        }

        try {
            CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                String result = EntityUtils.toString(httpEntity);
                String var9 = result;
                return var9;
            }
        } catch (Exception var20) {
            StackTraceElement stackTraceElement = var20.getStackTrace()[0];
            log.error("doPost-" + stackTraceElement.getMethodName() + "--" + stackTraceElement.getLineNumber());
        } finally {
            try {
                httpClient.close();
            } catch (Exception var19) {
                var19.printStackTrace();
            }

        }

        return null;
    }
}
