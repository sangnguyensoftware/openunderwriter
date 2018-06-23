package com.ail.core.command;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.TextMessage;
import javax.naming.NamingException;

import org.hornetq.api.core.Message;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ail.core.CoreProxy;
import com.ail.core.CoreUser;
import com.ail.core.VersionEffectiveDate;
import com.ail.core.XMLString;


public class JMSAccessorTest {

    private JMSAccessor sut;

    @Mock
    TextMessage message;
    @Mock
    QueueConnection queueConnection;
    @Mock
    private QueueSession session;
    @Mock
    ArgumentImpl argumentImpl;
    @Mock
    CoreUser coreUser;
    @Mock
    VersionEffectiveDate versionEffectiveDate;
    @Mock
    QueueSender queueSender;
    @Mock
    Queue queue;
    @Mock
    CoreProxy coreProxy;
    @Mock 
    XMLString xmlString;
    
    @Before
    public void setup() throws NamingException, JMSException, CloneNotSupportedException {
        MockitoAnnotations.initMocks(this);
        
        sut=spy(new JMSAccessor());
        sut.setArgs(argumentImpl);
        
        doReturn(queueConnection).when(sut).getQueueConnection();
        doReturn(queue).when(sut).getQueueInstance();
        doReturn(2000L).when(sut).deliveryDelay();
        doReturn(coreProxy).when(sut).createCoreProxy(eq(coreUser));

        doReturn(xmlString).when(coreProxy).toXML(eq(argumentImpl));
        doReturn("").when(xmlString).toString();
        doReturn(session).when(queueConnection).createQueueSession(eq(false), eq(QueueSession.AUTO_ACKNOWLEDGE));
        doReturn(queueSender).when(session).createSender(any(Queue.class));
        doReturn(message).when(session).createTextMessage(anyString());
        doReturn(coreUser).when(argumentImpl).getCallersCore();
        doReturn(argumentImpl).when(argumentImpl).clone();
        doReturn(versionEffectiveDate).when(coreUser).getVersionEffectiveDate();
        doReturn(0L).when(versionEffectiveDate).getTime();
    }
    
    @Test
    public void ensureThatMessageDeliveryDelayIsDefines() throws JMSServiceException, JMSException {
        sut.invoke();
        verify(message, times(1)).setLongProperty(eq(Message.HDR_SCHEDULED_DELIVERY_TIME.toString()), eq(2000L));
        verify(queueSender, times(1)).send(eq(message));
    }
    
    @Test(expected=JMSServiceException.class)
    public void ensureExceptionsAreHandled() throws NamingException, JMSException, JMSServiceException {
        doThrow(new NamingException()).when(sut).getQueueConnection();
        sut.invoke();
    }

    @Test(expected=JMSServiceException.class)
    public void ensureExceptionsInFinalAreHandled() throws NamingException, JMSException, JMSServiceException {
        doThrow(new NullPointerException()).when(queueSender).close();
        sut.invoke();
    }
}
