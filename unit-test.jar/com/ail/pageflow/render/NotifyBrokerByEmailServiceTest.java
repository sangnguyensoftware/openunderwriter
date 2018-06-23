package com.ail.pageflow.render;

import static com.ail.insurance.policy.PolicyStatus.ON_RISK;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ail.core.Core;
import com.ail.core.CoreContext;
import com.ail.core.CoreProxy;
import com.ail.core.configure.Group;
import com.ail.core.email.SendEmailService;
import com.ail.core.email.SendEmailService.SendEmailCommand;
import com.ail.insurance.policy.Broker;
import com.ail.insurance.policy.Policy;
import com.ail.pageflow.PageFlowContext;
import com.ail.pageflow.render.NotifyBrokerByEmailService.NotifyBrokerByEmailArgument;
import com.ail.pageflow.render.RenderService.RenderCommand;
import com.ail.party.Party;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SendEmailService.class, NotifyBrokerByEmailService.class, PageFlowContext.class, CoreContext.class, AttachQuotationToEmail.class, AttachCertificateToEmail.class, AttachInvoiceToEmail.class, AttachAssessmentSheetToEmail.class,
        AttachRawPolicyXmlToEmail.class })
public class NotifyBrokerByEmailServiceTest {

    private static final String FROM_EMAIL_ADDRESS = "FROM_ADDRESS";

    private static final String PRODUCT_NAME = "PRODUCT_NAME";

    private static final String QUOTE_EMAIL_ADDRESS = "EMAIL_ADDRESS";

    private static final long POLICY_ID = 1L;

    NotifyBrokerByEmailService sut;

    @Mock
    private NotifyBrokerByEmailArgument args;
    @Mock
    private Policy policy;
    @Mock
    private Broker broker;
    @Mock
    private Core core;
    @Mock
    private CoreProxy coreProxy;
    @Mock
    private SendEmailCommand sendEmailCommand;
    @Mock
    private Group smtpPropertyGroup;
    @Mock
    private Properties smtpProperties;
    @Mock
    private AddStyleForEmail addStyleForEmail;
    @Mock
    private RenderCommand renderCommand;
    @Mock
    private Party client;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        PowerMockito.mockStatic(PageFlowContext.class);
        PowerMockito.mockStatic(CoreContext.class);
        PowerMockito.mockStatic(AttachQuotationToEmail.class);
        PowerMockito.mockStatic(AttachCertificateToEmail.class);
        PowerMockito.mockStatic(AttachInvoiceToEmail.class);
        PowerMockito.mockStatic(AttachAssessmentSheetToEmail.class);
        PowerMockito.mockStatic(AttachRawPolicyXmlToEmail.class);
        PowerMockito.mockStatic(SendEmailService.class);

        whenNew(AddStyleForEmail.class).withNoArguments().thenReturn(addStyleForEmail);

        when(CoreContext.getCoreProxy()).thenReturn(coreProxy);

        sut = new NotifyBrokerByEmailService();
        sut.setArgs(args);
        sut.setCore(core);

        doReturn(POLICY_ID).when(args).getPolicyIdArg();
        doReturn(policy).when(core).queryUnique(eq("get.policy.by.systemId"), eq(POLICY_ID));
        doReturn(ON_RISK).when(policy).getStatus();
        doReturn(broker).when(policy).getBroker();
        doReturn(client).when(policy).getClient();
        doReturn(renderCommand).when(core).newCommand(eq("BrokerQuotationSummary"), eq("text/html"), eq(RenderCommand.class));
        doReturn(QUOTE_EMAIL_ADDRESS).when(broker).getQuoteEmailAddress();
        doReturn(PRODUCT_NAME).when(policy).getProductTypeId();
        doReturn(sendEmailCommand).when(core).newCommand(eq("SendEmail"), eq(SendEmailCommand.class));
        doReturn(smtpPropertyGroup).when(core).getGroup(eq("SMTPServerProperties"));
        doReturn(smtpProperties).when(smtpPropertyGroup).getParameterAsProperties();
        doReturn(FROM_EMAIL_ADDRESS).when(core).getParameterValue(eq("from-address"), (String)isNull());
    }

    @Test
    public void checkThatFromAddressFromConfigurationIsUsed() throws Exception {
        sut.emailNotification(policy);

        verify(sendEmailCommand).setFromArg(eq(FROM_EMAIL_ADDRESS));
    }

}
