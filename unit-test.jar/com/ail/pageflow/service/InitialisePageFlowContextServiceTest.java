package com.ail.pageflow.service;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import javax.portlet.PortletRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ail.core.BaseException;
import com.ail.core.Core;
import com.ail.core.PreconditionException;
import com.ail.core.configure.Configuration;
import com.ail.core.configure.Group;
import com.ail.core.configure.Parameter;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionCommand;

@RunWith(MockitoJUnitRunner.class)
public class InitialisePageFlowContextServiceTest {
    InitialisePageFlowContextService sut;

    @Mock
    InitialisePageFlowContextService.InitialisePageFlowContextArgument argument;
    @Mock
    PortletRequest portletRequest;
    @Mock
    Core core;
    @Mock
    ExecutePageActionCommand executePageActionCommand;

    @Before
    public void setupSut() {
        sut = spy(new InitialisePageFlowContextService());
        sut.setArgs(argument);

        doReturn(core).when(sut).getCore();
        doReturn(testableConfiguraitonWithGroup()).when(core).getConfiguration();
    }

    @Test(expected = PreconditionException.class)
    public void testNullConfigurationPrecondition() throws BaseException {
        doReturn(null).when(core).getConfiguration();
        sut.invoke();
    }

    @Test(expected = PreconditionException.class)
    public void testInitialisationActionListExistsPrecondition() throws BaseException {
        doReturn(testableConfiguraitonWithoutGroup()).when(core).getConfiguration();
        sut.invoke();
    }

    @Test
    public void ensureThatPageFlowInitialisationActionsAreExecuted() throws BaseException {
        doReturn(executePageActionCommand).when(core).newCommand(eq("ActionCommandOne"), eq(ExecutePageActionCommand.class));
        doReturn(executePageActionCommand).when(core).newCommand(eq("ActionCommandTwo"), eq(ExecutePageActionCommand.class));
        sut.invoke();
        verify(core, times(1)).newCommand(eq("ActionCommandOne"), eq(ExecutePageActionCommand.class));
        verify(core, times(1)).newCommand(eq("ActionCommandTwo"), eq(ExecutePageActionCommand.class));
    }

    private Configuration testableConfiguraitonWithGroup() {
        Group group;
        Parameter parameter;
        Configuration configuration;

        group=new Group();
        group.setName("PageFlowInitialisationActions");

        parameter=new Parameter();
        parameter.setName("ActionCommandOne");
        group.addParameter(parameter);

        parameter=new Parameter();
        parameter.setName("ActionCommandTwo");
        group.addParameter(parameter);

        configuration = new Configuration();
        configuration.addGroup(group);

        return configuration;
    }

    private Configuration testableConfiguraitonWithoutGroup() {
        return new Configuration();
    }
}
