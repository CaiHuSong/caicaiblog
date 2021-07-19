package com.cai.blog.vo.Params;

import lombok.Data;

@Data
public class LoginParam {

    private String account;

    private String password;

    //注册昵称参数
    private String nickname;


}
