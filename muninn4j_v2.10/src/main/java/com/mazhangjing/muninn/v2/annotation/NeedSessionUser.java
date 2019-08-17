package com.mazhangjing.muninn.v2.annotation;


import com.mazhangjing.muninn.v2.entry.UserType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface NeedSessionUser {
    UserType value() default UserType.UNKNOWN;
}
