package com.ail.service.product;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/")
public class ApplicationConfig extends Application
{
    @Override
    public Set<Class<?>> getClasses() {
        return new HashSet<Class<?>>((Collection<? extends Class<?>>)Arrays.asList(ProductService.class));
    }
}
