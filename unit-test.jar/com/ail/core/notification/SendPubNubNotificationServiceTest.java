package com.ail.core.notification;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ail.core.BaseException;
import com.ail.core.Core;
import com.ail.core.PreconditionException;
import com.ail.core.notification.SendPubNubNotificationService.SendPubNubNotificationArgument;

public class SendPubNubNotificationServiceTest {
    private static final String PUB_KEY = "PUB_KEY";
    private static final String SUB_KEY = "SUB_KEY";
    private static final String CHANNEL = "CHANNEL";

    private SendPubNubNotificationService sut;

    @Mock
    private SendPubNubNotificationArgument args;
    @Mock
    private Core core;
    @Mock
    private JSONObject message;

    @Before
    public void setup() throws PreconditionException {
        MockitoAnnotations.initMocks(this);

        sut = spy(new SendPubNubNotificationService());
        sut.setArgs(args);

        doReturn(message).when(args).getMessageArg();
        doReturn(CHANNEL).when(args).getChannelArg();
        doNothing().when(sut).invokePubNub(eq(SUB_KEY), eq(PUB_KEY), eq(CHANNEL), eq(message));
        doNothing().when(sut).setConfigurationNamespace();
        doReturn(core).when(sut).getCore();
        doReturn(SUB_KEY).when(core).getParameterValue(eq("PubNubProperties.SubscribeKey"));
        doReturn(PUB_KEY).when(core).getParameterValue(eq("PubNubProperties.PublishKey"));
    }

    @Test(expected=PreconditionException.class)
    public void testNullMessage() throws BaseException {
        doReturn(null).when(args).getMessageArg();
        sut.invoke();
    }

    @Test(expected=PreconditionException.class)
    public void testNullChannel() throws BaseException {
        doReturn(null).when(args).getChannelArg();
        sut.invoke();
    }

    @Test(expected=PreconditionException.class)
    public void testEmptyChannel() throws BaseException {
        doReturn("").when(args).getChannelArg();
        sut.invoke();
    }

    @Test(expected=PreconditionException.class)
    public void testNullSubKey() throws BaseException {
        doReturn(null).when(core).getParameterValue(eq("PubNubProperties.SubscribeKey"));
        sut.invoke();
    }

    @Test(expected=PreconditionException.class)
    public void testNullPubKey() throws BaseException {
        doReturn(null).when(core).getParameterValue(eq("PubNubProperties.PublishKey"));
        sut.invoke();
    }

    @Test
    public void testHappyPath() throws BaseException {
        sut.invoke();
    }

    @Test(expected=PubNubIntegrationError.class)
    public void testPubNubIntegrationError() throws BaseException {
        doThrow(new PubNubIntegrationError("ERROR")).when(sut).invokePubNub(eq(SUB_KEY), eq(PUB_KEY), eq(CHANNEL), eq(message));
        sut.invoke();
    }
}
