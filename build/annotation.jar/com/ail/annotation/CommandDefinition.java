package com.ail.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Deprecated
public @interface CommandDefinition {
    static final class DEFAULT_SERVICE_CLASS {};

	Class<?> defaultServiceClass() default DEFAULT_SERVICE_CLASS.class;
}
