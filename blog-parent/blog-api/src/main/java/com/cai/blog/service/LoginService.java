package com.cai.blog.service;

import com.cai.blog.dao.pojo.SysUser;
import com.cai.blog.vo.Params.LoginParam;
import com.cai.blog.vo.Result;

public interface LoginService {
    /**
     * 登录功能
     * @param loginParam
     * @return
     */
    Result login(LoginParam loginParam);

    SysUser checkToken(String token);

    Result logout(String token);

    Result register(LoginParam loginParam);
}
