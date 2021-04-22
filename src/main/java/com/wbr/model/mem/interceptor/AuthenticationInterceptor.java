package com.wbr.model.mem.interceptor;

import com.wbr.model.mem.annotation.PassTokenAnnotation;
import com.wbr.model.mem.utils.JwtTokenUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @author glf
 */
@Component
public class AuthenticationInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = JwtTokenUtils.getToken(request);// 从 http 请求头中取出 token
        // 如果不是映射到方法直接通过
        if(!(handler instanceof HandlerMethod)){
            return true;
        }

        String path = request.getRequestURI();
        HandlerMethod handlerMethod=(HandlerMethod)handler;
        Method method=handlerMethod.getMethod();
        //检查是否有passtoken注释，有则跳过认证
        if (method.isAnnotationPresent(PassTokenAnnotation.class)) {
            PassTokenAnnotation passToken = method.getAnnotation(PassTokenAnnotation.class);
            if (passToken.required()) {
                return true;
            }
        }
//        if(!StringUtils.isBlank(token)){
//            DefaultClaims defaultClaims= (DefaultClaims) JwtTokenUtils.getClaimsFromToken(token);
//            if(defaultClaims != null && !JwtTokenUtils.isTokenExpired(token)){
//                List perms = (List) defaultClaims.get("permission");
//                if(false){ //perms != null && !perms.contains(path
//                    throw new RuntimeException("权限认证失败,请重新登录！");
//                }
//            }else{
//                throw new RuntimeException("token错误！");
//            }
//        }else{
//            throw new RuntimeException("权限认证失败,请重新登录！");
//        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        //在业务处理器处理请求执行完成后，生成视图之前执行
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 在DispatcherServlet完全处理完请求后被调用，可用于清理资源等。返回处理（已经渲染了页面）
    }
}
