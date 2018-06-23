package com.ail.core.email;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ail.core.BaseException;
import com.ail.core.Core;
import com.ail.core.PreconditionException;
import com.ail.core.document.Document;
import com.ail.core.document.DocumentContent;
import com.ail.core.email.SendEmailService.SendEmailArgument;
import com.ail.insurance.policy.Policy;
import com.google.gwt.thirdparty.guava.common.collect.Lists;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Transport.class})
public class SendEmailServiceTest {

    private static final String FILE_NAME = "FILE_NAME";

    private static final long ATTACHMENT_SYS_ID = 2L;

    private static final String FROM_ADDRESS = "FROM_ADDRESS";

    SendEmailService sut;

    @Mock
    private Core core;
    @Mock
    private SendEmailArgument args;
    @Mock
    private Policy policy;
    @Mock
    Document attachment;
    @Mock
    Document attachmentToSend;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        PowerMockito.mockStatic(Transport.class);

        sut = spy(new SendEmailService());
        sut.setArgs(args);
        sut.setCore(core);

        doReturn("subject line").when(args).getSubjectArg();
        doReturn("recipient").when(args).getToArg();
        doReturn("host").when(args).getHostArg();
        doReturn("port").when(args).getPortArg();

    }

    @Test
    public void verifyNoAuth() throws Exception {
        doNothing().when(sut).sendNotification();
        sut.invoke();
        verify(args, times(1)).getSubjectArg();
        verify(args, times(1)).getToArg();
        verify(args, times(1)).getPortArg();
        verify(sut, times(1)).sendNotification();

    }

    @Test(expected=PreconditionException.class)
    public void verifyThatNullSubjectIsCaught() throws BaseException {
        doReturn(null).when(args).getSubjectArg();
        sut.invoke();
    }

    @Test(expected=PreconditionException.class)
    public void verifyThatEmptySubjectIsCaught() throws BaseException {
        doReturn("").when(args).getSubjectArg();
        sut.invoke();
    }

    @Test(expected=PreconditionException.class)
    public void verifyThatNullRecipientIsCaught() throws BaseException {
        doReturn(null).when(args).getToArg();
        sut.invoke();
    }


    @Test(expected=PreconditionException.class)
    public void verifyThatEmptyRecipientIsCaught() throws BaseException {
        doReturn("").when(args).getToArg();
        sut.invoke();
    }

    @Test(expected=PreconditionException.class)
    public void verifyThatEmptyHostIsCaught() throws BaseException {
        doReturn("").when(args).getHostArg();
        sut.invoke();
    }

    @Test(expected=PreconditionException.class)
    public void verifyThatEmptyPortIsCaught() throws BaseException {
        doReturn("").when(args).getPortArg();
        sut.invoke();
    }

    @Test
    public void verifyEmailSend() throws BaseException, MessagingException {
        sut.invoke();

        PowerMockito.verifyStatic(times(1));
        Transport.send(any(Message.class));
    }

    @Test
    public void verifyFromAddressSet() throws BaseException {

        doReturn(FROM_ADDRESS).when(args).getFromArg();
        sut.invoke();
        verify(args, times(2)).getFromArg();
        verify(args, never()).getUserArg();
        verify(args, times(2)).getHostArg();
    }

    @Test
    public void verifyFromAddressNotSet() throws BaseException {
        sut.invoke();
        verify(args, times(1)).getFromArg();
        verify(args, times(1)).getUserArg();
        verify(args, times(3)).getHostArg();

    }

    @Test
    public void verifyAttachemntAdded() throws BaseException {
        doReturn(Lists.newArrayList(attachment)).when(args).getAttachmentsArg();
        doReturn(ATTACHMENT_SYS_ID).when(attachment).getSystemId();
        doReturn(attachmentToSend).when(core).queryUnique(eq("get.document.by.systemId"), eq(ATTACHMENT_SYS_ID));
        doReturn(FILE_NAME).when(attachmentToSend).getFileName();
        doReturn(new DocumentContent("product", new byte[]{'a'})).when(attachmentToSend).getDocumentContent();
        sut.invoke();

        verify(attachmentToSend, times(2)).getDocumentContent();
        verify(attachmentToSend, times(3)).getFileName();
        verify(attachment, never()).getTitle();

    }

    @Test
    public void verifyNullAttachemntAddsErrorToEmail() throws BaseException {
        doReturn(Lists.newArrayList(attachment)).when(args).getAttachmentsArg();
        doReturn(ATTACHMENT_SYS_ID).when(attachment).getSystemId();
        doReturn(attachmentToSend).when(core).queryUnique(eq("get.document.by.systemId"), eq(ATTACHMENT_SYS_ID));
        doReturn(FILE_NAME).when(attachmentToSend).getFileName();
        doReturn(null).when(attachmentToSend).getDocumentContent();
        sut.invoke();

        verify(attachmentToSend, times(1)).getDocumentContent();
        verify(attachment, times(1)).getTitle();

    }

}
