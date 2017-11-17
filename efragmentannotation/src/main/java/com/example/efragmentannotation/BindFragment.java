package com.example.efragmentannotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by nazmuddinmavliwala on 03/11/16.
 */
@Retention(RUNTIME)
@Target(TYPE)
@Documented
public @interface BindFragment {
    int value() default -1;
}
