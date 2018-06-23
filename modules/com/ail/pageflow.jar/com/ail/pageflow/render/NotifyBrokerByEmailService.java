/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.ail.pageflow.render;

import static com.ail.core.Functions.productNameToConfigurationNamespace;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.List;

import javax.mail.MessagingException;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.CoreProxy;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.XMLException;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.core.document.Document;
import com.ail.core.document.DocumentType;
import com.ail.core.email.SendEmailService;
import com.ail.core.email.SendEmailService.SendEmailCommand;
import com.ail.insurance.policy.Policy;
import com.ail.pageflow.BrokerQuotationSummary;
import com.ail.pageflow.PageFlowContext;
import com.ail.pageflow.render.NotifyBrokerByEmailService.NotifyBrokerByEmailArgument;
import com.ail.pageflow.render.RenderService.RenderCommand;
import com.google.common.collect.Lists;

/**
 * Send a notification of an event relating to a quote to the broker associated with the product.
 */
@ServiceImplementation
public class NotifyBrokerByEmailService extends Service<NotifyBrokerByEmailArgument> {

    private String configurationNamespace = null;

    /**
     * Return the product type id of the policy we're assessing the risk for as the
     * configuration namespace. The has the effect of selecting the product's configuration.
     * @return product type id
     */
    @Override
    public String getConfigurationNamespace() {
        if (configurationNamespace==null) {
            return super.getConfigurationNamespace();
        }
        else {
            return configurationNamespace;
        }
    }

    private void setConfigurationNamespace(String configurationNamespace) {
        this.configurationNamespace=configurationNamespace;
    }

    @Override
    public void invoke() throws BaseException {
        setConfigurationNamespace(null);

        // Fail if there's no policyId or policy
        if (args.getPolicyIdArg() == null && args.getPolicyArg() == null) {
            throw new PreconditionException("args.getPolicyIdArg() == null && args.getPolicyArg() == null");
        }

        Policy policy = args.getPolicyArg();
        if (policy == null) {
            policy = (Policy) getCore().queryUnique("get.policy.by.systemId", args.getPolicyIdArg());
        }

        if (policy==null) {
            throw new PreconditionException("core.queryUnique(get.policy.by.systemId, "+args.getPolicyIdArg()+")==null");
        }

        if (policy.getBroker()==null) {
            throw new PreconditionException("savedQuotation.getQuotation().getBroker()==null");
        }

        if (policy.getBroker().getQuoteEmailAddress()==null) {
            throw new PreconditionException("savedQuotation.getQuotation().getBroker().getQuoteEmailAddress()==null");
        }

        setConfigurationNamespace(productNameToConfigurationNamespace(policy.getProductTypeId()));

        try {
            emailNotification(policy);
        }
        catch(Exception e) {
        	throw new PostconditionException("Failed to send notification email for quote: "+policy.getQuotationNumber()+". "+e.toString(), e);
        }
    }

    protected void emailNotification(Policy policy) throws Exception {

        PageFlowContext.setProductName(policy.getProductTypeId());
        PageFlowContext.setCoreProxy(new CoreProxy(getCore()));
    	PageFlowContext.setPolicy(policy);

    	List<Document> attachments = Lists.newArrayList();
    	attachments.add(createBrokerSummaryAttachment(policy));

        AttachQuotationToEmail.invoke(core, policy, attachments);
    	AttachCertificateToEmail.invoke(core, policy, attachments);
    	AttachInvoiceToEmail.invoke(core, policy, attachments);
    	AttachAssessmentSheetToEmail.invoke(core, policy, attachments);
    	AttachRawPolicyXmlToEmail.invoke(core, policy, attachments);

    	SendEmailCommand sendCommand = getCore().newCommand("SendEmail", SendEmailCommand.class);

        sendCommand.setToArg(policy.getBroker().getQuoteEmailAddress());
        sendCommand.setSubjectArg(generateSubject(policy));
        sendCommand.setTextArg(args.getTextArg());
        sendCommand.setAttachmentsArg(attachments);
        sendCommand.setFromArg(getCore().getParameterValue("from-address", null));

        SendEmailService.setMailProperties(sendCommand, getCore().getGroup("SMTPServerProperties").getParameterAsProperties());

        sendCommand.invoke();
    }

    /**
     * Generate the email subject text.
     * Example subject: "Policy: QF0001 - Motor Plus - Mr. Jimbo Clucknasty"
     * @param policy
     * @return
     * @throws XMLException
     */
    private String generateSubject(Policy policy) throws XMLException {
        switch (policy.getStatus()) {
        case ON_RISK:
            // Example subject: "On Risk: POL3214 - Motor Plus - Mr Jim Smith"
            return policy.getStatus().getLongName()+
                    ": "+policy.getPolicyNumber()+
                    " - "+policy.getProductName()+
                    " - "+policy.getClient().getLegalName();

        default:
            // Example subject: "Quotation: QF0001 - Motor Plus - Mr Jim Smith"
            return policy.getStatus().getLongName()+
                    ": "+policy.getQuotationNumber()+
                    " - "+policy.getProductName()+
                    " - "+policy.getClient().getLegalName();
        }
    }

    /**
     * Create a MimeBodyPart containing a summary of the quotation rendered as HTML.
     * @param policy Quote to render the assessment sheet for.
     * @return BodyPart containing rendered output.
     * @throws MessagingException
     * @throws BaseException
     * @throws IOException
     * @throws MalformedURLException
     */
    private Document createBrokerSummaryAttachment(Policy policy) throws MessagingException, BaseException, MalformedURLException, IOException {
        // Use the UI's rendered to create the HTML output - email is a kind of UI after all!
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        PrintWriter writer=new PrintWriter(baos);

        new AddStyleForEmail().invoke(writer);

        RenderCommand rbqs=getCore().newCommand("BrokerQuotationSummary", "text/html", RenderCommand.class);
        rbqs.setWriterArg(writer);
        rbqs.setModelArgRet(policy);
        rbqs.setRenderIdArg("email");
        rbqs.setPageElementArg(new BrokerQuotationSummary());

        rbqs.invoke();

        writer.close();

        return core.create(new Document(
                DocumentType.ATTACHMENT.name(), "inline", baos.toByteArray(), "Summary", "summary.html", "text/html", policy.getProductTypeId()));

    }

    @ServiceArgument
    public interface NotifyBrokerByEmailArgument extends Argument {

        Long getPolicyIdArg();

        void setPolicyIdArg(Long policyIdArg);

        Policy getPolicyArg();

        void setPolicyArg(Policy policyArg);

        String getTextArg();

        void setTextArg(String text);
    }

    @ServiceCommand(defaultServiceClass=NotifyBrokerByEmailService.class)
    public interface NotifyBrokerByEmailCommand extends Command, NotifyBrokerByEmailArgument {
    }
}


