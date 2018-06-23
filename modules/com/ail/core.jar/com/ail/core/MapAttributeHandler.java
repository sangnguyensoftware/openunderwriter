package com.ail.core;

import java.util.HashMap;
import java.util.Map;

import com.ail.core.context.SessionAdaptor;

public class MapAttributeHandler implements SessionAdaptor {
    Map<String,Object> attributes;

    public MapAttributeHandler() {
        attributes = new HashMap<>();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Object> T get(String name, Class<T> clazz) {
        return (T)attributes.get(name);
    }

    @Override
    public void set(String name, Object value) {
        attributes.put(name, value);
    }
}
