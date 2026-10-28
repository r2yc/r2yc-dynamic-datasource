package com.r2yc.dynamic.datasource.spring.boot.autoconfigure.annotation;

import java.lang.annotation.*;

/**
 * 自定义事务
 *
 * @author zhanghaoran
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LocalTransactional {
    String[] dbs();
}
