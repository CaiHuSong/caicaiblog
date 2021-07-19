package com.cai.blog.service;

import com.alibaba.fastjson.JSON;
import com.cai.blog.dao.pojo.SysUser;
import com.cai.blog.utils.JWTUtils;
import com.cai.blog.vo.ErrorCode;
import com.cai.blog.vo.Params.LoginParam;
import com.cai.blog.vo.Result;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Transactional//事务注解，加在接口上对所有实现类生效
public class LoginServiceImpl implements LoginService{

    private static final String slat = "caicaizi!@#";

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Override
    public Result login(LoginParam loginParam) {
        /**
         * 1.检查参数是否合法
         * 2.根据用户名和密码取user表中查询是否存在
         * 3.如果不存在 登录失败
         * 4.如果存在 使用jwt生成token 返回给前端
         * 5.token放入redis中  redis token user信息  设置过期时间
         * 6.登录认证的时候，先认证token字符串是否合法，去redis中认证是否存在
         */
        String account = loginParam.getAccount();
        String password = loginParam.getPassword();
        if (StringUtils.isBlank(account) || StringUtils.isBlank(password)){
            return Result.fail(ErrorCode.PARAMS_ERROR.getCode(),ErrorCode.PARAMS_ERROR.getMsg());
        }
        //密码加密
        password = DigestUtils.md5Hex(password + slat);
       SysUser sysUser = sysUserService.findUser(account,password);
        if (sysUser==null){
            return Result.fail(ErrorCode.ACCOUNT_PWD_NOT_EXIST.getCode(),ErrorCode.ACCOUNT_PWD_NOT_EXIST.getMsg());
        }
        String token = JWTUtils.createToken(sysUser.getId());
        redisTemplate.opsForValue().set("TOKEN_"+token, JSON.toJSONString(sysUser),1, TimeUnit.DAYS);

        return Result.success(token);
    }

    @Override
    public SysUser checkToken(String token) {
        if (StringUtils.isBlank(token)){
            return null;
        }

        Map<String, Object> map = JWTUtils.checkToken(token);
        if (map==null){
            return null;
        }
        String s = redisTemplate.opsForValue().get("TOKEN_" + token);
        if (StringUtils.isBlank(s)){
            return null;
        }
        SysUser sysUser = JSON.parseObject(s, SysUser.class);

        return sysUser;
    }

    @Override
    public Result logout(String token) {
        redisTemplate.delete("TOKEN_"+token);
        return Result.success(null);
    }

    @Override
    public Result register(LoginParam loginParam) {
        /**
         * 1.判断参数合法
         * 2.判断账号是否存在，是，返回账号已存在
         * 3.不存在，注册用户
         * 4.生成token
         * 5.存入redis，返回
         * 6.加上事务，一但中间任何过程出问题，回滚
         */
        String account = loginParam.getAccount();
        String password = loginParam.getPassword();
        String nickname = loginParam.getNickname();
        if (StringUtils.isBlank(account) || StringUtils.isBlank(password) || StringUtils.isBlank(nickname)){
            return Result.fail(ErrorCode.PARAMS_ERROR.getCode(),ErrorCode.PARAMS_ERROR.getMsg());

        }

       SysUser sysUser = sysUserService.findUserByAccount(account);
        if (sysUser!=null){
            return Result.fail(ErrorCode.ACCOUNT_EXIST.getCode(),ErrorCode.ACCOUNT_EXIST.getMsg());

        }
        sysUser = new SysUser();
        sysUser.setNickname(nickname);
        sysUser.setAccount(account);
        sysUser.setPassword(DigestUtils.md5Hex(password+slat));
        sysUser.setCreateDate(System.currentTimeMillis());
        sysUser.setLastLogin(System.currentTimeMillis());
        sysUser.setAvatar("/static/img/logo.b3a48c0.png");
        sysUser.setAdmin(1); //1 为true
        sysUser.setDeleted(0); // 0 为false
        sysUser.setSalt("");
        sysUser.setStatus("");
        sysUser.setEmail("");
        this.sysUserService.save(sysUser);

        String token = JWTUtils.createToken(sysUser.getId());
        redisTemplate.opsForValue().set("TOKEN_"+token, JSON.toJSONString(sysUser),1, TimeUnit.DAYS);
        return Result.success(token);
    }
}
