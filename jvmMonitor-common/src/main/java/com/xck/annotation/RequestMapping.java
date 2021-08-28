package com.xck.annotation;

import java.lang.annotation.*;

/**
 * 请求映射
 */
//可以用到类和方法上面
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMapping {
    String value() default "/";
}