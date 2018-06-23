package com.ail.core.command;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.tools.generic.MathTool;
import org.apache.velocity.tools.generic.NumberTool;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ail.core.Core;
import com.ail.core.configure.Group;
import com.ail.core.configure.Type;

public class VelocityAccessorTest {
    VelocityAccessor sut;

    @Mock
    Argument args;
    @Mock
    private Core core;
    @Mock
    private Type typeSpec;
    @Mock
    private Group group;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        sut = new VelocityAccessor();
        sut.activate(core, typeSpec);
        sut.setArgs(args);

        doReturn(group).when(core).getGroup(eq("VelocityFunctionClasses"));
    }

    @Test
    public void checkThatToolsArePlacedInTheContext() {
        VelocityContext velocityContext = sut.createContext();

        assertThat(velocityContext.get("args"), is(equalTo((Object)args)));
        assertThat(velocityContext.get("date"), is(instanceOf(VelocityDateUtils.class)));
        assertThat(velocityContext.get("math"), is(instanceOf(MathTool.class)));
        assertThat(velocityContext.get("number"), is(instanceOf(NumberTool.class)));
    }
}
