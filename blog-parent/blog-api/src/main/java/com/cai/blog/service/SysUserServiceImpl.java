package com.cai.blog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cai.blog.dao.mapper.SysUserMapper;
import com.cai.blog.dao.pojo.SysUser;
import com.cai.blog.vo.ErrorCode;
import com.cai.blog.vo.LoginUserVo;
import com.cai.blog.vo.Result;
import com.cai.blog.vo.UserVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl implements SysUserService{

    @Autowired
    private LoginService loginService;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public UserVo findUserVoById(Long id) {
        SysUser sysUser = sysUserMapper.selectById(id);
        if (sysUser==null){
            sysUser = new SysUser();
            sysUser.setId(1L);
            sysUser.setAvatar("/static/img/logo.b3a48c0.png");
            sysUser.setNickname("小菜");
        }
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(sysUser,userVo);
        return userVo;
    }

    @Override
    public SysUser findUserById(Long id) {
        SysUser sysUser = sysUserMapper.selectById(id);
        if (sysUser==null){
           sysUser = new SysUser();
            sysUser.setNickname("小菜");
        }

        return sysUser;
    }

    @Override
    public SysUser findUser(String account, String password) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getAccount,account);
        queryWrapper.eq(SysUser::getPassword,password);
        queryWrapper.select(SysUser::getId,SysUser::getAccount,SysUser::getAvatar,SysUser::getNickname);
        queryWrapper.last("limit 1");

        return sysUserMapper.selectOne(queryWrapper);
    }

    @Override
    public Result findUserByToken(String token) {

        /**
         * 1.token合法性校验
         *   是否为空  解析是否成功  redis是否存在
         *   2.如果校验失败，返回错误
         *   3.如果成功 返回对应结果LoginUserVo
         */
       SysUser sysUser = loginService.checkToken(token);
       if (sysUser==null){
           return Result.fail(ErrorCode.TOKEN_ERROR.getCode(),ErrorCode.TOKEN_ERROR.getMsg());
       }
        LoginUserVo userVo = new LoginUserVo();
       userVo.setAccount(sysUser.getAccount());
       userVo.setAvatar(sysUser.getAvatar());
       userVo.setId(sysUser.getId());
       userVo.setNickname(sysUser.getNickname());

        return Result.success(userVo);
    }

    @Override
    public SysUser findUserByAccount(String account) {
        LambdaQueryWrapper<SysUser> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getAccount,account);
        queryWrapper.last("limit 1");

        return this.sysUserMapper.selectOne(queryWrapper);
    }

    @Override
    public void save(SysUser sysUser) {
        //插入id为分布式id  雪花算法
        this.sysUserMapper.insert(sysUser);
    }
}
