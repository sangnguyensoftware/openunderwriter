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

import org.springframework.util.StringUtils;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.Core;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.core.document.Document;
import com.ail.core.document.DocumentType;
import com.ail.core.document.FetchDocumentService.FetchDocumentCommand;
import com.ail.core.email.SendEmailService;
import com.ail.core.email.SendEmailService.SendEmailCommand;
import com.ail.insurance.policy.Policy;
import com.ail.pageflow.Blank;
import com.ail.pageflow.PageElement;
import com.ail.pageflow.render.NotifyEmailService.NotifyEmailArgument;
import com.ail.pageflow.render.RenderService.RenderCommand;
import com.google.common.collect.Lists;

/**
 * A simple but comprehensive generic email service.
 * Body text can be expressed with a template or by simple text.
 * Attachments are added using document retrieval commands.
 */
@ServiceImplementation
public class NotifyEmailService extends Service<NotifyEmailArgument> {

    private String configurationNamespace = null;

    @Override
    public void invoke() throws BaseException {

        if (!StringUtils.hasLength(args.getToArg())) {
            throw new PreconditionException("args.getToArg() == null");
        }

        if (!StringUtils.hasLength(args.getSubjectArg())) {
            throw new PreconditionException("args.getSubjectArg() == null");
        }

        if (StringUtils.hasLength(args.getTemplateNameArg()) && StringUtils.hasLength(args.getTextArg())) {
            throw new PreconditionException(
                    "StringUtils.hasLength(args.getTemplateNameArg()) && StringUtils.hasLength(args.getTextArg())");
        }

        if (args.getPolicyArg() != null) {
            setConfigurationNamespace(
                    productNameToConfigurationNamespace(args.getPolicyArg().getProductTypeId()));
        }

        try {
            sendNotification();
        } catch (Exception e) {
            throw new PostconditionException("Failed to send notification email: " +
                    (args.getPolicyArg() != null ? args.getPolicyArg().getSystemId() : args.getToArg()) + ". " + e.toString(), e);
        }
    }

    protected void sendNotification() throws Exception {

        SendEmailCommand sendCommand = getCore().newCommand("SendEmail", SendEmailCommand.class);

        sendCommand.setToArg(args.getToArg());
        sendCommand.setFromArg(args.getFromArg());
        sendCommand.setSubjectArg(args.getSubjectArg());
        sendCommand.setTextArg(args.getTextArg());
        sendCommand.setCcArg(args.getCcArg());
        sendCommand.setAttachmentsArg(getAttachments());
        String fromParam = getCore().getParameterValue("from-address");
        if (fromParam != null) {
            sendCommand.setFromArg(fromParam);
        }
        SendEmailService.setMailProperties(
                sendCommand, getCore().getGroup("SMTPServerProperties").getParameterAsProperties());

        sendCommand.invoke();

    }


    private List<Document> getAttachments() throws Exception {

        List<Document> attachments = Lists.newArrayList();
        String[] docTypes = args.getDocumentsToAttachArg().split(",");

        for(int i = 0; i < docTypes.length; i++) {

            FetchDocumentCommand cmd = core.newCommand(docTypes[i].trim(), FetchDocumentCommand.class);
            cmd.setModelIDArg(args.getPolicyArg().getSystemId());
            cmd.invoke();

            attachments.add(cmd.getDocumentRet());
        }

        if (StringUtils.hasLength(args.getTemplateNameArg())) {
                attachments.add(getRenderedTemplate(
                    getCore(), args.getPolicyArg(), args.getTemplateNameArg(),
                        (args.getTemplateRenderArg() == null ? new Blank() : args.getTemplateRenderArg())));
        }

        return attachments;
    }

    private Document getRenderedTemplate(Core core, Policy policy, String templateName, PageElement renderElement) throws BaseException, MessagingException, MalformedURLException, IOException {

        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        PrintWriter writer=new PrintWriter(baos);

        new AddStyleForEmail().invoke(writer);

        RenderCommand renderCommand = core.newCommand(templateName, "text/html", RenderCommand.class);
        renderCommand.setWriterArg(writer);

        if (policy != null) {
            renderCommand.setModelArgRet(policy);
        }

        renderCommand.setPageElementArg(renderElement);
        renderCommand.setRenderIdArg("email");
        renderCommand.invoke();

        writer.close();

        return core.create(new Document(
                DocumentType.ATTACHMENT.name(), "inline", baos.toByteArray(), "Summary", "summary.html", "text/html", policy.getProductTypeId()));
    }

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

    @ServiceArgument
    public interface NotifyEmailArgument extends Argument {


        String getTemplateNameArg();

        /**
         * Set main body summary template name
         * @param templateNameArg
         */
        void setTemplateNameArg(String templateNameArg);

        PageElement getTemplateRenderArg();

        /**
         * Template render helper class
         * @param element
         */
        void setTemplateRenderArg(PageElement element);

        Policy getPolicyArg();

        /**
         * Set policy information if required
         * Note: productNameToConfigurationNamespace used to retrieve mail properties require policy to be set
         * @param policyArg
         */
        void setPolicyArg(Policy policyArg);

        String getSubjectArg();

        /**
         * Email subject line
         * @param subjectArg
         */
        void setSubjectArg(String subjectArg);

        String getToArg();

        /**
         * Recipient
         * @param to
         */
        void setToArg(String to);

        String getCcArg();

        /**
         * CC recipient
         * @param cc
         */
        void setCcArg(String cc);

        String getFromArg();

        /**
         * From email address - defaults to mail properties if not set
         * @param from
         */
        void setFromArg(String from);

        String getTextArg();

        /**
         * A simple way to set body summary text without using a template
         * @param text
         */
        void setTextArg(String text);

        String getDocumentsToAttachArg();

        /**
         * Comma separated list of commands for documents to attach (e.g. 'FetchQuotationDocument, FetchWordingDocument')
         * @param attachments
         */
        void setDocumentsToAttachArg(String attachments);

    }

    @ServiceCommand(defaultServiceClass=NotifyEmailService.class)
    public interface NotifyEmailCommand extends Command, NotifyEmailArgument {
    }

}
