package com.ail.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
public @interface ServiceImplementation {
	String serviceName() default DEFAULT_SERVICE_NAME;
	
	static final String DEFAULT_SERVICE_NAME="";
}
