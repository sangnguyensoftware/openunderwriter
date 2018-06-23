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
import static com.ail.core.document.DocumentType.ATTACHMENT;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.List;

import javax.mail.MessagingException;

import org.apache.commons.lang.StringUtils;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.CoreProxy;
import com.ail.core.HasDocuments;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.Type;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.core.document.Document;
import com.ail.core.email.SendEmailService;
import com.ail.core.email.SendEmailService.SendEmailCommand;
import com.ail.insurance.policy.Policy;
import com.ail.pageflow.Blank;
import com.ail.pageflow.PageFlowContext;
import com.ail.pageflow.render.NotifyPartyByEmailService.NotifyPartyByEmailArgument;
import com.ail.pageflow.render.RenderService.RenderCommand;
import com.ail.party.Party;
import com.google.common.collect.Lists;

/**
 * Send a simple notification to a party. Attachments are supported.
 */
@ServiceImplementation
public class NotifyPartyByEmailService extends Service<NotifyPartyByEmailArgument> {

    private static final long serialVersionUID = -4915889686192216902L;

    private String configurationNamespace = null;

    /**
     * Return the product type id of the policy we're assessing the risk for as
     * the configuration namespace. The has the effect of selecting the
     * product's configuration.
     *
     * @return product type id
     */
    @Override
    public String getConfigurationNamespace() {
        if (configurationNamespace == null) {
            return super.getConfigurationNamespace();
        } else {
            return configurationNamespace;
        }
    }

    private void setConfigurationNamespace(String configurationNamespace) {
        this.configurationNamespace = configurationNamespace;
    }

    @Override
    public void invoke() throws BaseException {
        setConfigurationNamespace(null);

        if (args.getModelArg() == null && args.getPolicyArg() != null) {
            args.setModelArg(args.getPolicyArg());
            args.setProductTypeArg(args.getPolicyArg().getProductTypeId());
        }

        if (args.getModelArg() == null) {
            throw new PreconditionException("args.getModelArg()");
        }

        if (args.getPartyArg() == null || args.getPartyArg().getEmailAddress() == null || args.getPartyArg().getEmailAddress().isEmpty()) {
            throw new PreconditionException("args.getPartyArg() == null || args.getPartyArg().getEmailAddress() == null || args.getPartyArg().getEmailAddress().isEmpty()");
        }

        if (StringUtils.isBlank(args.getTemplateNameArg()) && StringUtils.isBlank(args.getMessageArg())) {
            throw new PreconditionException("args.getTemplateNameArg() == null && args.getMessageArg() == null");
        }

        setConfigurationNamespace(productNameToConfigurationNamespace(args.getProductTypeArg()));

        try {
            emailNotification();
        } catch (Exception e) {
            throw new PostconditionException("Failed to send notification email for policy: " + args.getPolicyArg().getSystemId() + ". " + e.toString(), e);
        }
    }

    protected void emailNotification() throws Exception {

        PageFlowContext.setProductName(args.getProductTypeArg());
        PageFlowContext.setCoreProxy(new CoreProxy(getCore()));
        if (args.getPolicyArg() != null) {
            PageFlowContext.setPolicy(args.getPolicyArg());
        }

        List<Document> attachments = Lists.newArrayList();
        attachments.add(createMainBody(args.getModelArg(), args.getProductTypeArg(), args.getTemplateNameArg(), args.getMessageArg()));

        if (args.getPolicyDocumentNamesArg() != null) {
            for (String policyDocumentName : args.getPolicyDocumentNamesArg()) {
                AttachPolicyDocumentToEmail.invoke(core, args.getPolicyArg(), attachments, policyDocumentName);
            }
        }

        // Use the document references arg to try and find documents to attach first by their external id, if the reference is an external id,
        // or by title otherwise.
        if (args.getDocumentReferencesArg() != null) {
            for (String documentReference : args.getDocumentReferencesArg()) {
                if (!AttachDocumentToEmailByExternalId.invoke(core, attachments, documentReference)) {
                    if (HasDocuments.class.isInstance(args.getModelArg())) {
                        AttachDocumentToEmailByTitle.invoke(core, attachments, (HasDocuments) args.getModelArg(), documentReference);
                    }
                }
            }
        }

        SendEmailCommand sendCommand = getCore().newCommand("SendEmail", SendEmailCommand.class);

        sendCommand.setToArg(args.getPartyArg().getEmailAddress());

        String fromParam = getCore().getParameterValue("from-address");
        if (fromParam != null) {
            sendCommand.setFromArg(fromParam);
        }
        sendCommand.setSubjectArg(args.getSubjectArg());
        sendCommand.setAttachmentsArg(attachments);
        SendEmailService.setMailProperties(sendCommand, getCore().getGroup("SMTPServerProperties").getParameterAsProperties());

        sendCommand.invoke();
    }

    private Document createMainBody(Type model, String productType, String templateName, String message) throws MessagingException, BaseException, MalformedURLException, IOException {
        if (StringUtils.isNotBlank(templateName)) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintWriter writer = new PrintWriter(baos);

            new AddStyleForEmail().invoke(writer);

            RenderCommand renderCommand = getCore().newCommand(templateName, "text/html", RenderCommand.class);
            renderCommand.setWriterArg(writer);
            renderCommand.setModelArgRet(model);
            renderCommand.setRenderIdArg("email");
            renderCommand.setPageElementArg(new Blank());
            renderCommand.setCoreArg(getCore());

            renderCommand.invoke();

            writer.close();

            return getCore().create(new Document(ATTACHMENT.name(), "inline", baos.toByteArray(), "Summary", "summary.html", "text/html", productType));
        } else {
            return getCore().create(new Document(ATTACHMENT.name(), "inline", message.getBytes(), "Summary", "summary.html", "text/html", productType));
        }
    }

    @ServiceArgument
    public interface NotifyPartyByEmailArgument extends Argument {
        Type getModelArg();

        void setModelArg(Type modelArg);

        String getProductTypeArg();

        void setProductTypeArg(String productTypeArg);

        Policy getPolicyArg();

        void setPolicyArg(Policy policyArg);

        String getTemplateNameArg();

        void setTemplateNameArg(String templateNameArg);

        String getSubjectArg();

        void setSubjectArg(String subjectArg);

        String getMessageArg();

        void setMessageArg(String messageArg);

        Party getPartyArg();

        void setPartyArg(Party partyArg);

        String[] getPolicyDocumentNamesArg();

        void setPolicyDocumentNamesArg(String[] policyDocumentNamesArg);

        String[] getDocumentReferencesArg();

        void setDocumentReferencesArg(String[] documentReferencesArg);
    }

    @ServiceCommand(defaultServiceClass=NotifyPartyByEmailService.class)
    public interface NotifyPartyByEmailCommand extends Command, NotifyPartyByEmailArgument {
    }
}
