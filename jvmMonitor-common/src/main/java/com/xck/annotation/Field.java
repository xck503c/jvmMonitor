package com.xck.annotation;

import com.sun.corba.se.impl.orbutil.closure.Future;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Field {
    /*字段名*/
    String name() default "";

    /*字段说明*/
    String desc() default "";

    /*js执行脚本，可用于根据值给出解释*/
    String jsScript() default "";
}