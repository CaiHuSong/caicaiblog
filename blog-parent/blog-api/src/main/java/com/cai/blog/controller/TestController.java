package com.cai.blog.controller;

import com.cai.blog.utils.UserThreadLocal;
import com.cai.blog.vo.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test")
public class TestController {

    @RequestMapping
    public Result test(){

        UserThreadLocal.get();
        return Result.success(null);
    }
}
