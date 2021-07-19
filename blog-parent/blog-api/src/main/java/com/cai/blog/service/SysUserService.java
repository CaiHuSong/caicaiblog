package com.cai.blog.service;

import com.cai.blog.dao.pojo.SysUser;
import com.cai.blog.vo.Result;
import com.cai.blog.vo.UserVo;

public interface SysUserService {

    UserVo findUserVoById(Long id);

    SysUser findUserById(Long id);

    SysUser findUser(String account, String password);

    Result findUserByToken(String token);

    SysUser findUserByAccount(String account);

    void save(SysUser sysUser);
}
