package com.r2yc.dynamic.datasource.spring.boot.autoconfigure.annotation;

import java.lang.annotation.*;

/**
 * 数据源注解
 *
 * @author zhanghaoran
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
public @interface MDS {
    String value();
}
