package com.cai.blog.common.aop;

import java.lang.annotation.*;

/**
 * 日志注解开发
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogAnnotation {

    //两个参数

    String module() default "";

    String operation() default "";

}
