package com.ail.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target(TYPE)
@Retention(RUNTIME)
public @interface TypeDefinition {
    static final String DEFAULT_NAME = "TypeDefinitionDefaultName";
    static final String UNDEFINED_PREFIX = "undefined";

    String name() default DEFAULT_NAME;

    String prefix() default UNDEFINED_PREFIX;
}
