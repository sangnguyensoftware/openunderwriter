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
import static com.ail.insurance.policy.PolicyStatus.ON_RISK;

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
import com.ail.insurance.policy.Broker;
import com.ail.insurance.policy.Policy;
import com.ail.insurance.policy.Proposer;
import com.ail.pageflow.PageFlowContext;
import com.ail.pageflow.ProposerQuotationSummary;
import com.ail.pageflow.render.NotifyProposerByEmailService.NotifyProposerByEmailArgument;
import com.ail.pageflow.render.RenderService.RenderCommand;
import com.google.common.collect.Lists;
/**
 * A standard notification to the proposer relating to QUOTATION / ON_RISK
 * including associated documents as attachments.
 */
@ServiceImplementation
public class NotifyProposerByEmailService extends Service<NotifyProposerByEmailArgument> {

    private static final long serialVersionUID = -4915889686192216902L;

    private String configurationNamespace = null;

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

    /** The 'business logic' of the entry point. */
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

        if (policy.getClient() == null) {
            throw new PreconditionException("policy.getClient() == null");
        }

        if (policy.getClient().getEmailAddress() == null) {
            throw new PreconditionException("policy.getClient().getEmailAddress() == null");
        }

        setConfigurationNamespace(productNameToConfigurationNamespace(policy.getProductTypeId()));

        try {
            emailNotification(policy);
        }
        catch(Exception e) {
        	throw new PostconditionException("Failed to send notification email for policy: "+policy.getQuotationNumber()+". "+e.toString(), e);
        }
    }

    private void emailNotification(Policy policy) throws Exception {

        PageFlowContext.setProductName(policy.getProductTypeId());
        PageFlowContext.setCoreProxy(new CoreProxy(getCore()));
        PageFlowContext.setPolicy(policy);

        Proposer proposer = (Proposer) policy.getClient();
        Broker broker = policy.getBroker();
        String fromAddress = broker.getEmailAddress();
        String toAddress = proposer.getEmailAddress();

        List<Document> attachments = Lists.newArrayList();
        attachments.add(createSummaryAttachment(policy));

        if (ON_RISK.equals(policy.getStatus())) {
            AttachCertificateToEmail.invoke(core, policy, attachments);
        }
        else {
            AttachQuotationToEmail.invoke(core, policy, attachments);
        }
        AttachInvoiceToEmail.invoke(core, policy, attachments);

        SendEmailCommand sendCommand = getCore().newCommand("SendEmail", SendEmailCommand.class);

        sendCommand.setToArg(toAddress);
        sendCommand.setFromArg(fromAddress);
        sendCommand.setSubjectArg(generateSubject(policy));
        sendCommand.setTextArg(args.getTextArg());
        sendCommand.setAttachmentsArg(attachments);
        SendEmailService.setMailProperties(
                sendCommand, getCore().getGroup("SMTPServerProperties").getParameterAsProperties());

        sendCommand.invoke();

    }

    /**
     * Generate a subject for the email that is appropriate to the status
     * of the policy
     * @param policy
     * @return subject string
     * @throws XMLException
     */
    private String generateSubject(Policy policy) throws XMLException {
        switch (policy.getStatus()) {
        case ON_RISK:
            // Example subject: "Motor Plus Policy: PF0001"
            return policy.getProductName() + " " +
                   "Policy: " + policy.getPolicyNumber();

        default:
            // Example subject: "Motor Plus Quotation: QF0001"
            return policy.getProductName() + " " +
                   policy.getStatus().getLongName() + ": " +
                   policy.getQuotationNumber();
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
    private Document createSummaryAttachment(Policy policy) throws MessagingException, BaseException, MalformedURLException, IOException {
        // Use the UI's rendered to create the HTML output - email is a kind of UI after all!
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        PrintWriter writer=new PrintWriter(baos);

        new AddStyleForEmail().invoke(writer);

        RenderCommand rbqs=getCore().newCommand("ProposerQuotationSummary", "text/html", RenderCommand.class);
        rbqs.setWriterArg(writer);
        rbqs.setModelArgRet(policy);
        rbqs.setRenderIdArg("email");
        rbqs.setPageElementArg(new ProposerQuotationSummary());
        rbqs.invoke();

        writer.close();

        return core.create(new Document(
                DocumentType.ATTACHMENT.name(), "inline", baos.toByteArray(), "Summary", "summary.html", "text/html", policy.getProductTypeId()));

    }

    @ServiceArgument
    public interface NotifyProposerByEmailArgument extends Argument {
        Long getPolicyIdArg();

        void setPolicyIdArg(Long policyIdArg);

        Policy getPolicyArg();

        void setPolicyArg(Policy policyArg);

        String getTextArg();

        void setTextArg(String text);
    }

    @ServiceCommand(defaultServiceClass=NotifyProposerByEmailService.class)
    public interface NotifyProposerByEmailCommand extends Command, NotifyProposerByEmailArgument {
    }

}


