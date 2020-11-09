package com.thinksee.annotation;

import java.lang.annotation.*;

/**
 * 与RpcReference相同
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface RpcService {
    // Service version, default value is empty string
    String version() default "";
    // Service group, default value is empty string
    String group() default "";
}
