package com.ail.pageflow.render;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ail.core.BaseException;
import com.ail.core.PreconditionException;
import com.ail.insurance.policy.Policy;
import com.ail.pageflow.render.NotifyEmailService.NotifyEmailArgument;

public class NotifyEmailServiceTest {

    NotifyEmailService sut;

    @Mock
    private NotifyEmailArgument args;
    @Mock
    private Policy policy;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        sut = spy(new NotifyEmailService());
        sut.setArgs(args);

        doNothing().when(sut).sendNotification();

        doReturn("subject line").when(args).getSubjectArg();
        doReturn("recipient").when(args).getToArg();

    }

    @Test
    public void verifyNoPreconditionException() throws BaseException {
        sut.invoke();
        verify(args, times(1)).getSubjectArg();
        verify(args, times(1)).getToArg();

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
    public void verifyTemplateAndTextSetIsCaught() throws BaseException {
        doReturn("body template name").when(args).getTemplateNameArg();
        doReturn("body text").when(args).getTextArg();
        sut.invoke();
    }


}
