package com.thinksee.annotation;

import com.thinksee.spring.CustomScanner;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 此注解作用于class,interface以及method上
 * @Import注解把用到的bean导入到了当前容器中。
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Import(CustomScanner.class)
public @interface RpcScan {
    String[] basePackage();
}