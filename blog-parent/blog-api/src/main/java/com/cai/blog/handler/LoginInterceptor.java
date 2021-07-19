package com.cai.blog.handler;

import com.alibaba.fastjson.JSON;
import com.cai.blog.dao.pojo.SysUser;
import com.cai.blog.service.LoginService;
import com.cai.blog.utils.UserThreadLocal;
import com.cai.blog.vo.ErrorCode;
import com.cai.blog.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private LoginService loginService;

    //执行controller之前拦截
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        /**
         * 1.判断是否是controller路径
         * 2.验证token是否存在和正确
         * 3.存在放行，不存在返回“未登录”
         *
         */
        if (!(handler instanceof HandlerMethod)){
            //不是，放行
            return true;
        }

        String token = request.getHeader("Authorization");
        log.info("=================request start===========================");
        String requestURI = request.getRequestURI();
        log.info("request uri:{}",requestURI);
        log.info("request method:{}",request.getMethod());
        log.info("token:{}", token);
        log.info("=================request end===========================");
        if (token==null){
            Result fail = Result.fail(ErrorCode.NO_LOGIN.getCode(), ErrorCode.NO_LOGIN.getMsg());

            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(JSON.toJSONString(fail));
            return false;
        }

        SysUser sysUser = loginService.checkToken(token);
        if (sysUser==null){
            Result fail = Result.fail(ErrorCode.NO_LOGIN.getCode(), ErrorCode.NO_LOGIN.getMsg());

            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(JSON.toJSONString(fail));
            return false;
        }

        //验证通过，登录状态放行,存入threadlocal
        UserThreadLocal.put(sysUser);

        return true;
    }

    //释放threadlocal资源，以免造成内存泄漏
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        UserThreadLocal.remove();
    }
}
