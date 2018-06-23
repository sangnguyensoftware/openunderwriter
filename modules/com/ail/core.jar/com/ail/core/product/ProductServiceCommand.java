package com.ail.core.product;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(TYPE)
public @interface ProductServiceCommand {

    static final String EMPTY_STRING = "";

    /** Name to set for the dynamic Type service entry. Optional. If blank, the FQN of the containing class will be used. */
    public String serviceName() default EMPTY_STRING;

    /** Name to set for the dynamic Type command entry. Optional. If blank, a command will not be created. */
    public String commandName() default EMPTY_STRING;
}
