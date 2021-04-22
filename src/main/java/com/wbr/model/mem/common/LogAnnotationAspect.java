package com.wbr.model.mem.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wbr.model.mem.annotation.LogAnnotation;
import com.wbr.model.mem.service.SysLogService;
import com.wbr.model.mem.utils.JwtTokenUtils;
import com.wbr.model.mem.utils.StringUtils;
import com.wbr.model.mem.utils.UUIDUtils;
import com.wbr.model.mem.vo.SysLog;
import io.jsonwebtoken.impl.DefaultClaims;
import io.swagger.annotations.ApiOperation;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 切面处理类，操作日志异常日志记录处理
 * @author glf
 * @date 2020/01/08
 */
@Aspect
@Component
public class LogAnnotationAspect {

    @Autowired
    private SysLogService sysLogService;

    /**
     * 正常返回通知，拦截用户操作日志，连接点正常执行完成后执行， 如果连接点抛出异常，则不会执行
     * @param joinPoint 切入点
     * @param keys      返回结果
     */
    @AfterReturning(value = "@annotation(com.wbr.model.mem.annotation.LogAnnotation)", returning = "keys")
    public void saveOperLog(JoinPoint joinPoint, Object keys) {
        // 获取RequestAttributes
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        // 从获取RequestAttributes中获取HttpServletRequest的信息
        HttpServletRequest request = (HttpServletRequest) requestAttributes
                .resolveReference(RequestAttributes.REFERENCE_REQUEST);
        SysLog log = new SysLog();
        try {
            log.setId(UUIDUtils.getGUID32());
            // 从切面织入点处通过反射机制获取织入点处的方法
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            // 获取切入点所在的方法
            Method method = signature.getMethod();
            // 获取操作
            LogAnnotation opLog = method.getAnnotation(LogAnnotation.class);
            if (opLog != null) {
                if(!opLog.recordRequestParam()){
                    return ;
                }
                String module = opLog.module();
                log.setModule(module); // 操作模块
                log.setFlag(true);
                log.setCreateTime(new Date());
                ApiOperation api = method.getAnnotation(ApiOperation.class);
                if(api != null){
                    log.setRemark(api.value());
                }
                String token = JwtTokenUtils.getToken(request);// 从 http 请求头中取出 token
                String username = null;
                if(!StringUtils.isBlank(token)){
                    DefaultClaims defaultClaims= (DefaultClaims) JwtTokenUtils.getClaimsFromToken(token);
                    username=(String) defaultClaims.get("username");
                }
                //request 中获取
                if(StringUtils.isBlank(username)){
                    username = request.getParameter("username");
                }
                if(StringUtils.isBlank(username)){
                    XssHttpServletRequestWrapper3 xssWrapper=new XssHttpServletRequestWrapper3(request);
                    String body =  xssWrapper.getBody();
                    if(!StringUtils.isBlank(body)){
                        JSONObject jsonObject = JSON.parseObject(body);
                        username = jsonObject.getString("username");
                    }
                }
                log.setUsername(username);
            }
            // 获取请求的类名
            String className = joinPoint.getTarget().getClass().getName();
            // 获取请求的方法名
            String methodName = method.getName();
            methodName = className + "." + methodName; // 请求方法
            log.setParams(methodName);
            //兼容 中法版本  暂时不做详细日志记录
            // 请求的参数
//            Map<String, String> rtnMap = converMap(request.getParameterMap());
//            将参数所在的数组转换成json
//            String params = JSON.toJSONString(rtnMap);
//            log.setOperRequParam(params); // 请求参数
//            log.setOperRespParam(JSON.toJSONString(keys)); // 返回结果
//            log.setOperUserId(UserShiroUtil.getCurrentUserLoginName()); // 请求用户ID
//            log.setOperUserName(UserShiroUtil.getCurrentUserName()); // 请求用户名称
//            log.setOperIp(IPUtil.getRemortIP(request)); // 请求IP
//            log.setOperUri(request.getRequestURI()); // 请求URI
//            log.setOperCreateTime(new Date()); // 创建时间
//            log.setOperVer(operVer); // 操作版本
            sysLogService.insert(log);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 异常返回通知，用于拦截异常日志信息 连接点抛出异常后执行
     * @param joinPoint 切入点
     * @param e         异常信息
     */
    @AfterThrowing(pointcut = "execution(* com.wbr..*.controller..*.*(..))", throwing = "e")
    public void saveExceptionLog(JoinPoint joinPoint, Throwable e) {
        // 获取RequestAttributes
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        // 从获取RequestAttributes中获取HttpServletRequest的信息
        HttpServletRequest request = (HttpServletRequest) requestAttributes
                .resolveReference(RequestAttributes.REFERENCE_REQUEST);

        SysLog excepLog = new SysLog();
        try {
            // 从切面织入点处通过反射机制获取织入点处的方法
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            // 获取切入点所在的方法
            Method method = signature.getMethod();
            excepLog.setId(UUIDUtils.getGUID32());
            // 获取请求的类名
            String className = joinPoint.getTarget().getClass().getName();
            // 获取请求的方法名
            String methodName = method.getName();
            methodName = className + "." + methodName;
            // 请求的参数
            Map<String, String> rtnMap = converMap(request.getParameterMap());
            // 将参数所在的数组转换成json
            String params = JSON.toJSONString(methodName);
            excepLog.setParams(methodName); // 请求参数
            if(e != null){
                excepLog.setRemark("异常信息==="+e.getClass().getName() +":"+ e.getMessage());
            }
//          excepLog.setRemark("异常信息==="+stackTraceToString(e.getClass().getName(), e.getMessage(), e.getStackTrace())); // 异常信息
            LogAnnotation opLog = method.getAnnotation(LogAnnotation.class);
            if (opLog != null) {
                if(!opLog.recordRequestParam()){
                    return ;
                }
                String module = opLog.module();
                excepLog.setModule(module); // 操作模块
                excepLog.setFlag(true);
                excepLog.setCreateTime(new Date());
                String username = null;
                String token = JwtTokenUtils.getToken(request);// 从 http 请求头中取出 token
                if(!StringUtils.isBlank(token)){
                    DefaultClaims defaultClaims= (DefaultClaims) JwtTokenUtils.getClaimsFromToken(token);
                    username=(String) defaultClaims.get("username");
                }
                //request 中获取
                if(StringUtils.isBlank(username)){
                    username = request.getParameter("username");
                }
                if(StringUtils.isBlank(username)){
                    XssHttpServletRequestWrapper3 xssWrapper=new XssHttpServletRequestWrapper3(request);
                    String body =  xssWrapper.getBody();
                    if(!StringUtils.isBlank(body)){
                        JSONObject jsonObject = JSON.parseObject(body);
                        username = jsonObject.getString("username");
                    }
                }
                excepLog.setUsername(username);
            }
            sysLogService.insert(excepLog);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    /**
     * 转换request 请求参数
     *
     * @param paramMap request获取的参数数组
     */
    public Map<String, String> converMap(Map<String, String[]> paramMap) {
        Map<String, String> rtnMap = new HashMap<String, String>();
        for (String key : paramMap.keySet()) {
            rtnMap.put(key, paramMap.get(key)[0]);
        }
        return rtnMap;
    }

    /**
     * 转换异常信息为字符串
     *
     * @param exceptionName    异常名称
     * @param exceptionMessage 异常信息
     * @param elements         堆栈信息
     */
    public String stackTraceToString(String exceptionName, String exceptionMessage, StackTraceElement[] elements) {
        StringBuffer strbuff = new StringBuffer();
        for (StackTraceElement stet : elements) {
            strbuff.append(stet + "\n");
        }
        String message = exceptionName + ":" + exceptionMessage + "\n\t" + strbuff.toString();
        return message;
    }
}