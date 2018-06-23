package com.ail.core.email;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.ail.core.CoreProxy;
import com.ail.core.XMLException;
import com.ail.core.XMLString;
import com.ail.core.configure.ConfigurationHandler;
import com.ail.core.email.SendEmailService.SendEmailArgument;

public class TestSendEmailArgument {

    private CoreProxy coreProxy;

    @Before
    public void setup() {
        coreProxy = new CoreProxy();
        coreProxy.resetConfigurations();
        coreProxy.setVersionEffectiveDateToNow();
        ConfigurationHandler.resetCache();
    }

    @Test
    public void testHandlingOfAmpersandInSubject() throws XMLException {
        SendEmailArgument sendEmailArgument = coreProxy.newType(SendEmailArgument.class);

        sendEmailArgument.setSubjectArg("NAME & NAME");

        XMLString xml = coreProxy.toXML(sendEmailArgument);

        sendEmailArgument = coreProxy.fromXML(SendEmailArgument.class, xml);

        assertThat(sendEmailArgument.getSubjectArg(), is("NAME & NAME"));
    }
}
