package com.coku.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author liuwaiping
 * @desc
 * @date 2020/11/16.
 * @email coku_lwp@126.com
 */

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface AutoRequest {
}
