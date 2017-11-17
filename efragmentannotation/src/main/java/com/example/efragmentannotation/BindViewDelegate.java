package com.example.efragmentannotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by nazmuddinmavliwala on 08/11/16.
 */
@Retention(RUNTIME)
@Target(FIELD)
@Documented
public @interface BindViewDelegate {
}
