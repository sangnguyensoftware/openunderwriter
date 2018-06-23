package com.ail.insurance.quotation;

import static com.ail.insurance.quotation.NotifyBrokerByPubNubService.PARTY_PUBNUB_CHANNEL_XPATH;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ail.core.BaseException;
import com.ail.core.Core;
import com.ail.core.PreconditionException;
import com.ail.core.notification.SendPubNubNotificationService.SendPubNubNotificationCommand;
import com.ail.insurance.policy.Broker;
import com.ail.insurance.policy.Policy;
import com.ail.insurance.quotation.NotifyBrokerByPubNubService.NotifyBrokerByPubNubArgument;

public class NotifyBrokerByPubNubServiceTest {
    private static final String BROKER_PUBNUB_CHANNEL = "BROKER_PUBNUB_CHANNEL";
    private static final String PRODUCT_ID = "PRODUCT_ID";
    private static final String SUBSCRIBE_KEY = "DUMMY_SUBSCRIBE_KEY";
    private static final String PUBLISH_KEY = "DUMMY_SUBSCRIBE_KEY";
    private static final String MESSAGE = "dummy message";

    NotifyBrokerByPubNubService sut;

    @Mock
    NotifyBrokerByPubNubArgument args;
    @Mock
    Policy policy;
    @Mock
    private Broker broker;
    @Mock
    private SendPubNubNotificationCommand sendPubNubNotificationCommand;
    @Mock
    private Core core;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        sut = spy(new NotifyBrokerByPubNubService());
        sut.setArgs(args);

        doReturn(MESSAGE).when(args).getMessageArg();
        doReturn(policy).when(args).getPolicyArg();
        doReturn(broker).when(policy).getBroker();
        doReturn(PRODUCT_ID).when(policy).getProductTypeId();
        doReturn(BROKER_PUBNUB_CHANNEL).when(broker).xpathGet(eq(PARTY_PUBNUB_CHANNEL_XPATH));
        doReturn(core).when(sut).getCore();
        doReturn(SUBSCRIBE_KEY).when(core).getParameterValue(eq("PubNubProperties.SubscribeKey"));
        doReturn(PUBLISH_KEY).when(core).getParameterValue(eq("PubNubProperties.PublishKey"));
        doReturn(sendPubNubNotificationCommand).when(core).newCommand(eq(SendPubNubNotificationCommand.class));
    }

    @Test(expected = PreconditionException.class)
    public void testThatPolicyAndPolicyIdCannotBeNull() throws BaseException {
        doReturn(null).when(args).getPolicyArg();
        doReturn(null).when(args).getPolicyIdArg();
        sut.invoke();
    }

    @Test(expected = PreconditionException.class)
    public void testThatNullForBrokerIsCaught() throws BaseException {
        doReturn(null).when(policy).getBroker();
        sut.invoke();
    }

    @Test
    public void testThatPolicyArgIsUsedInPreferenceToPolicyIdArg() throws BaseException {
        doNothing().when(sut).sendNotification(any(Policy.class), any(Broker.class));

        doReturn(policy).when(args).getPolicyArg();
        doReturn(null).when(args).getPolicyIdArg();
        sut.invoke();
        verify(core, never()).queryUnique(anyString(), anyObject());

        doReturn(null).when(args).getPolicyArg();
        doReturn(1234L).when(args).getPolicyIdArg();
        doReturn(policy).when(core).queryUnique(anyString(), eq(1234L));
        sut.invoke();
        verify(core, times(1)).queryUnique(anyString(), eq(1234L));
    }

    @Test(expected = PreconditionException.class)
    public void testThatNullChannelIsCaught() throws BaseException {
        Broker broker = mock(Broker.class);
        doReturn(broker).when(sut).determineBrokerParty(policy);
        doReturn(null).when(broker).xpathGet(eq(PARTY_PUBNUB_CHANNEL_XPATH));
        sut.invoke();
    }

    @Test(expected = PreconditionException.class)
    public void testThatNullMessageIsCaught() throws BaseException {
        doReturn(null).when(args).getMessageArg();
        sut.invoke();
    }

    @Test(expected = PreconditionException.class)
    public void testThatEmptyMessageIsCaught() throws BaseException {
        doReturn("").when(args).getMessageArg();
        sut.invoke();
    }

    @Test
    public void verifyThatMessageGoesToClientIfDefined() throws BaseException {
        sut.invoke();
        verify(sendPubNubNotificationCommand).setChannelArg(eq(BROKER_PUBNUB_CHANNEL));
    }

    @Test
    public void checkThatCharsAreEscappedInMessage() throws PreconditionException {
        String res = sut.stringMessage2PushNotification("hello \"world").toString();
        assertThat(res, containsString("\\\""));
    }
}
