package com.ail.pageflow.render;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URL;
import java.util.Properties;

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
import com.ail.core.CoreContext;
import com.ail.core.CoreProxy;
import com.ail.core.Functions;
import com.ail.core.PreconditionException;
import com.ail.core.configure.Group;
import com.ail.core.document.Document;
import com.ail.core.email.SendEmailService.SendEmailCommand;
import com.ail.insurance.policy.Policy;
import com.ail.pageflow.PageFlowContext;
import com.ail.pageflow.render.NotifyPartyByEmailService.NotifyPartyByEmailArgument;
import com.ail.pageflow.render.RenderService.RenderCommand;
import com.ail.party.Party;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PageFlowContext.class, CoreContext.class, Functions.class})
public class NotifyPartyByEmailServiceTest {

    private static final String FROM_EMAIL_ADDRESS = "FROM_EMAIL_ADDRESS";
    private static final String PORT = "80";
    private static final String HOST = "";
    private static final String PRODUCT_NAME = "PRODUCT_NAME";
    private static final String EMAIL_ADDRESS = "EMAIL_ADDRESS";
    private static final String SUBJECT = "SUBJECT";
    private static final String TEMPLATE_NAME = "TEMPLATE_NAME";

    NotifyPartyByEmailService sut;

    @Mock
    private AddStyleForEmail addStyleForEmail;
    @Mock
    private Core core;
    @Mock
    private CoreProxy coreProxy;
    @Mock
    private NotifyPartyByEmailArgument args;
    @Mock
    private Policy policy;
    @Mock
    private Party party;
    @Mock
    private RenderCommand renderCommand;
    @Mock
    private SendEmailCommand sendCommand;
    @Mock
    private Document emailBody;
    @Mock
    private Group mailPropsGroup;
    @Mock
    private Properties mailProps;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        PowerMockito.mockStatic(PageFlowContext.class);
        PowerMockito.mockStatic(CoreContext.class);
        PowerMockito.mockStatic(Functions.class);

        sut = new NotifyPartyByEmailService();
        sut.setArgs(args);
        sut.setCore(core);

        when(PageFlowContext.getCoreProxy()).thenReturn(coreProxy);
        when(Functions.loadUrlContentAsString(any(URL.class))).thenReturn("CSS_FILE");

        doReturn(policy).when(args).getPolicyArg();
        doReturn(policy).when(args).getModelArg();
        doReturn(1L).when(policy).getSystemId();
        doReturn(party).when(args).getPartyArg();
        doReturn(EMAIL_ADDRESS).when(party).getEmailAddress();

        doReturn(SUBJECT).when(args).getSubjectArg();
        doReturn(TEMPLATE_NAME).when(args).getTemplateNameArg();

        doReturn(PRODUCT_NAME).when(policy).getProductTypeId();
        doReturn(renderCommand).when(core).newCommand(eq(TEMPLATE_NAME), eq("text/html"), eq(RenderCommand.class));
        doReturn(HOST).when(coreProxy).getParameterValue(eq("ProductRepository.Host"));
        doReturn(PORT).when(coreProxy).getParameterValue(eq("ProductRepository.Port"));
        doReturn(emailBody).when(core).create(any(Document.class));
        doReturn(sendCommand).when(core).newCommand(eq("SendEmail"), eq(SendEmailCommand.class));
        doReturn(mailPropsGroup).when(core).getGroup(eq("SMTPServerProperties"));
        doReturn(mailProps).when(mailPropsGroup).getParameterAsProperties();

    }

    @Test(expected=PreconditionException.class)
    public void verifyThatNullModelIsCaught() throws BaseException {
        doReturn(null).when(args).getModelArg();
        sut.invoke();
    }

    @Test(expected=PreconditionException.class)
    public void verifyThatNullPartyIsCaught() throws BaseException {
        doReturn(null).when(args).getPartyArg();
        sut.invoke();
    }

    @Test(expected=PreconditionException.class)
    public void verifyThatNullTemplateNameIsCaught() throws BaseException {
        doReturn(null).when(args).getTemplateNameArg();
        doReturn(null).when(args).getMessageArg();
        sut.invoke();
    }

    @Test(expected=PreconditionException.class)
    public void verifyThatEmptyTemplateNameIsCaught() throws BaseException {
        doReturn("").when(args).getTemplateNameArg();
        doReturn("").when(args).getMessageArg();
        sut.invoke();
    }

    @Test
    public void verifyEmailSends() throws BaseException {
        sut.invoke();
        verify(renderCommand, times(1)).setCoreArg(any(Core.class));
        verify(sendCommand, times(1)).invoke();
    }

    @Test
    public void verifyFromAddressSet() throws BaseException {
        sut.invoke();
        verify(sendCommand, never()).setFromArg(anyString());

        doReturn(FROM_EMAIL_ADDRESS).when(core).getParameterValue(eq("from-address"));
        sut.invoke();
        verify(sendCommand, times(1)).setFromArg(eq(FROM_EMAIL_ADDRESS));
    }

}
