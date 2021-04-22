package com.wbr.model.mem.common;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.util.StringUtil;
import com.wbr.model.mem.utils.StringUtils;
import org.apache.commons.lang3.StringEscapeUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author glf
 */
public class XssAndSqlHttpServletRequestWrapper extends HttpServletRequestWrapper {

    //声明sql注入的关键词key
    private static String key = "and|exec|insert|select|delete|update|count|*|%|chr|mid|master|truncate|char|declare|;|or|-|+";
    private static Set<String> notAllowedKeyWords = new HashSet<String>(0);
    private static String replacedString="INVALID";
    static {
        String keyStr[] = key.split("\\|");
        //将key添加到Set集合中
        for (String str : keyStr) {
            notAllowedKeyWords.add(str);
        }
    }

    /**
     * @return
     * @Author liumingyu
     * @Description //TODO 构造函数，传入参数，执行超类
     * @Date 2020/1/10 2:29 下午
     * @Param [request]
     **/
    public XssAndSqlHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    /**
     * @return java.lang.String
     * @Author liumingyu
     * @Description //TODO 重写getParameter方法 ,getParameter方法是直接通过request获得querystring类型的入参调用的方法
     * @Date 2020/1/10 2:31 下午
     * @Param [name]
     **/
    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        if (!StringUtil.isEmpty(value)) {
            //调用Apache的工具类：StringEscapeUtils.escapeHtml4
            value = StringEscapeUtils.escapeHtml4(value);
            value = cleanSqlKeyWords(value);
        }
        return value;
    }

    /**
     * @return java.lang.String[]
     * @Author liumingyu
     * @Description //TODO 重写getParameterValues
     * @Date 2020/1/10 2:32 下午
     * @Param [name]
     **/
    @Override
    public String[] getParameterValues(String name) {
        String[] parameterValues = super.getParameterValues(name);
        if (parameterValues == null) {
            return null;
        }
        for (int i = 0; i < parameterValues.length; i++) {
            String value = parameterValues[i];
            //调用Apache的工具类：StringEscapeUtils.escapeHtml4
            parameterValues[i] = StringEscapeUtils.escapeHtml4(value);
            parameterValues[i] = cleanSqlKeyWords(parameterValues[i]);
        }
        return parameterValues;
    }

    @Override
    public String getHeader(String name) {
        //过滤xss攻击
        String value = StringEscapeUtils.escapeHtml4(super.getHeader(name));
        if (value == null){
            return null;
        }
        //过滤sql注入
        return cleanSqlKeyWords(value);
    }

    @Override
    public String getQueryString() {
        return StringEscapeUtils.escapeHtml4(super.getQueryString());
    }

    /**
     * @return javax.servlet.ServletInputStream
     * @Author liumingyu
     * @Description //TODO 过滤JSON数据中的XSS攻击
     * @Date 2020/1/10 4:58 下午
     * @Param []
     **/
    @Override
    public ServletInputStream getInputStream() throws IOException {
        //调用方法将流数据return为String
        String str = getRequestBody(super.getInputStream());
        //如果str为""，则返回0
        if ("".equals(str)) {
            return new ServletInputStream() {
                @Override
                public int read() throws IOException {
                    return 0;
                }

                @Override
                public boolean isFinished() {
                    return false;
                }

                @Override
                public boolean isReady() {
                    return false;
                }

                @Override
                public void setReadListener(ReadListener readListener) {

                }
            };
        }
        //将数据存放至map
        Map<String, Object> map = JSON.parseObject(str, Map.class);
        //声明个存放过滤后数据的hashMap
        Map<String, Object> resultMap = new HashMap<>(map.size());
        //开始遍历数据
        for (String key : map.keySet()) {
            Object val = map.get(key);
            //如果key=富文本字段名，就不去过滤
            if ("content".equals(key)) {
                //不过滤
                resultMap.put(key, val);
            } else {
                //不为富文本字段才会过滤
                if (map.get(key) instanceof String) {
                    //通过escapeHtml4去过滤
                    resultMap.put(key, StringEscapeUtils.escapeHtml4(cleanSqlKeyWords(val.toString())));
                } else {
                    //不过滤
                    resultMap.put(key, val);
                }
            }
        }
        str = JSON.toJSONString(resultMap);
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(str.getBytes());
        return new ServletInputStream() {
            @Override
            public int read() throws IOException {
                return byteArrayInputStream.read();
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }

        };
    }

    /**
     * @return java.lang.String
     * @Author liumingyu
     * @Description //TODO 获取JSON数据
     * @Date 2020/1/10 4:58 下午
     * @Param [stream]
     **/
    private String getRequestBody(InputStream stream) {
        String line = "";
        StringBuilder body = new StringBuilder();
        int counter = 0;
        // 读取POST提交的数据内容
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, Charset.forName("UTF-8")));
        try {
            while ((line = reader.readLine()) != null) {
                //拼接读取到的数据
                body.append(line);
                counter++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (body == null) {
            return "";
        }
        //最后返回数据
        return body.toString();
    }


    /**
     * @Author liumingyu
     * @Description //TODO 过滤可能造成sql注入的关键字
     * @Date 2020/1/13 9:11 上午
     * @Param [value]
     * @return java.lang.String
     **/
    private String cleanSqlKeyWords(String value) {
        String paramValue = value;
        for (String keyword : notAllowedKeyWords) {
            if (paramValue.length() > keyword.length() + 4
                    && (paramValue.contains(" "+keyword)||paramValue.contains(keyword+" ")||paramValue.contains(" "+keyword+" "))) {
                paramValue = StringUtils.replace(paramValue, keyword, replacedString);
            }
        }
        return paramValue;
    }

}