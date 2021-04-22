package com.wbr.model.mem.filter;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wbr.model.mem.constant.SystemCon;
import com.wbr.model.mem.utils.RedisUtil;
import com.wbr.model.mem.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

@Component
public class SessionListener implements HttpSessionListener {

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public void sessionCreated(HttpSessionEvent arg0) {

    }
    @Override
    public synchronized void sessionDestroyed(HttpSessionEvent arg0) {//监听session的撤销
        String username= (String) arg0.getSession().getAttribute(SystemCon.SESSIONUSERDATA);
        if(!StringUtils.isBlank(username)){
            if(redisUtil.get(SystemCon.SESSIONUSERDATA) != null){
                JSONObject jsonObject= (JSONObject) redisUtil.get(SystemCon.SESSIONUSERDATA);
                if(jsonObject != null){
                    JSONArray jsonArray= (JSONArray) jsonObject.get("onLines");
                    if(jsonArray != null && jsonArray.contains(username)){
                        int redisCount= (int) jsonObject.get("count");
                        jsonObject.put("count",--redisCount);
                        jsonArray.remove(username);
                        redisUtil.set(SystemCon.SESSIONUSERDATA,jsonObject);
                    }
                }
            }
        }
    }
}