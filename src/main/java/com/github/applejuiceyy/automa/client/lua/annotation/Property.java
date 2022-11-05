package com.github.applejuiceyy.automa.client.lua.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface Property {
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.METHOD })
    @interface Setter { }
}