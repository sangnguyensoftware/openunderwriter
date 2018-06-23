package com.ail.core.command;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ail.core.BaseError;
import com.ail.core.Core;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.XMLException;
import com.ail.core.XMLString;
import com.ail.core.command.OnMessageService.OnMessageArgument;

public class OnMessageServiceTest {

    private OnMessageService sut;
    private String dummyXMLString="<version xsi:type='java:com.ail.core.Version'/>";
    private String expectedCommandName;
    @Mock private Core core;
    @Mock private OnMessageArgument args;
    @Mock private TextMessage message;
    @Mock private Argument messageArgument;
    @Mock private Command messageCommand;
    @Mock private BaseError exception;
    
    @Before
    @SuppressWarnings("unchecked")
    public void setupSut() throws JMSException, XMLException {
        MockitoAnnotations.initMocks(this);
        
        sut=new OnMessageService();
        sut.setCore(core);
        sut.setArgs(args);
        doReturn(message).when(args).getMessageArg();
        doReturn(true).when(message).propertyExists(eq("VersionEffectiveDate"));
        doReturn(dummyXMLString).when(message).getText();
        doReturn(messageArgument).when(core).fromXML(any(Class.class), any(XMLString.class));

        expectedCommandName = messageArgument.getClass().getName();
        expectedCommandName = expectedCommandName.substring(expectedCommandName.lastIndexOf('.') + 1);
        doReturn(messageCommand).when(core).newCommand(eq(expectedCommandName), any(Class.class));
        
    }
    
    @Test(expected=PreconditionException.class)
    public void ensureMessageArgPreconditionIsChecked() throws PreconditionException, PostconditionException {
        doReturn(null).when(args).getMessageArg();
        sut.invoke();
    }

    @Test(expected=PreconditionException.class)
    public void ensureMessageVersionEffectiveDateIsChecked() throws PreconditionException, PostconditionException, JMSException {
        doReturn(false).when(message).propertyExists(eq("VersionEffectiveDate"));
        sut.invoke();
    }
    
    @Test
    public void testCommandNameGeneration() throws Throwable, PostconditionException {
        sut.invoke();
        verify(core, times(1)).newCommand(eq(expectedCommandName), eq(Command.class));
    }
    
    @Test
    public void testExceptionTranslation() throws PreconditionException {
        try {
            doThrow(exception).when(core).newCommand(eq(expectedCommandName), eq(Command.class));
            sut.invoke();
            fail("Expected exception not caught");
        } catch (PostconditionException e) {
            assertTrue("!e.getTarget().equals(exception)", e.getTarget().equals(exception));
        }
    }
}
