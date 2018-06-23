package com.ail.core;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import com.ail.core.configure.Builder;
import com.ail.core.configure.Builders;
import com.ail.core.configure.Configuration;
import com.ail.core.configure.Group;
import com.ail.core.configure.Parameter;

public class AnnotatedConfigMergeTest {
    private Core mockCore;

    @Before
    public void setup() {
        mockCore=mock(Core.class);
    }

    @Test
    public void testConfigGroupMerge() {
        Configuration config1=buildConfigOne();
        Configuration config2=buildConfigTwo();

        config1.mergeWithDataFrom(config2, mockCore);

        assertEquals(2, config1.getBuilders().getBuilderCount());
        assertEquals("builder_1_namespace", config1.getBuilders().getBuilder(0).getNamespace());
        assertEquals("builder_1_name", config1.getBuilders().getBuilder(0).getName());
        assertEquals("builder_2_namespace", config1.getBuilders().getBuilder(1).getNamespace());
        assertEquals("builder_2_name", config1.getBuilders().getBuilder(1).getName());
    }

    @Test
    public void testConfigParameterMerge() {
        Configuration config1=buildConfigOne();
        Configuration config2=buildConfigTwo();

        config1.mergeWithDataFrom(config2, mockCore);

        assertEquals(2, config1.findGroup("paths").getParameterCount());
        assertEquals("param_1_value", config1.findGroup("paths").findParameter("param_1_name").getValue());
        assertEquals("param_2_value", config1.findGroup("paths").findParameter("param_2_name").getValue());
    }

    Configuration buildConfigOne() {
        Builder builder;
        Group group;
        Parameter param;
        Configuration config=new Configuration();
        config.setBuilders(new Builders());

        builder=new Builder();
        builder.setNamespace("builder_1_namespace");
        builder.setName("builder_1_name");
        config.getBuilders().addBuilder(builder);

        group=new Group();
        group.setName("paths");
        param=new Parameter();
        param.setName("param_1_name");
        param.setValue("param_1_value");
        group.addParameter(param);
        config.addGroup(group);

        return config;
    }

    Configuration buildConfigTwo() {
        Builder builder;
        Configuration config=new Configuration();
        config.setBuilders(new Builders());

        builder=new Builder();
        builder.setNamespace("builder_2_namespace");
        builder.setName("builder_2_name");
        config.getBuilders().addBuilder(builder);

        Group group = new Group();
        group.setName("paths");
        Parameter param = new Parameter();
        param.setName("param_2_name");
        param.setValue("param_2_value");
        group.addParameter(param);
        config.addGroup(group);


        return config;
    }
}
