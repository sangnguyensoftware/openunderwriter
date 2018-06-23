package com.ail.core.email;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.core.document.Document;
import com.ail.core.email.SendEmailService.SendEmailArgument;

/**
 * Low level email transport service
 */
@ServiceImplementation
@Component
public class SendEmailService extends Service<SendEmailArgument> {

    private static final String MAIL_SMTP_STARTTLS_ENABLE = "mail.smtp.starttls.enable";
    private static final String MAIL_SMTP_PASSWORD = "mail.smtp.password";
    private static final String MAIL_SMTP_USER = "mail.smtp.user";
    private static final String MAIL_SMTP_AUTH = "mail.smtp.auth";
    private static final String MAIL_SMTP_PORT = "mail.smtp.port";
    private static final String MAIL_SMTP_HOST = "mail.smtp.host";

    @Override
    public void invoke() throws BaseException {

        if (!StringUtils.hasLength(args.getToArg())) {
            throw new PreconditionException("args.getToArg() == null");
        }

        if (!StringUtils.hasLength(args.getSubjectArg())) {
            throw new PreconditionException("args.getSubjectArg() == null");
        }

        if (!StringUtils.hasLength(args.getHostArg())) {
            throw new PreconditionException("args.getHostArg() == null");
        }

        if (!StringUtils.hasLength(args.getPortArg())) {
            throw new PreconditionException("args.getPortArg() == null");
        }

        try {
            sendNotification();
        } catch (Exception e) {
            throw new PostconditionException("Failed to send email to " + args.getToArg() + ". " + e.toString(), e);
        }
    }

    protected void sendNotification() throws Exception {

        MimeMultipart multipart = new MimeMultipart("mixed");

        MimeMessage message = new MimeMessage(
                Session.getInstance(getMailProperties(), getSmtpAuthenticator()));

        message.addRecipients(Message.RecipientType.TO, args.getToArg());
        message.setSubject(args.getSubjectArg());

        if (StringUtils.hasLength(args.getCcArg())) {
            message.addRecipients(Message.RecipientType.CC, args.getCcArg());
        }

        InternetAddress fromAddress = getFromAndReplyToAddress();
        message.setFrom(fromAddress);
        message.setReplyTo(new InternetAddress[]{fromAddress});


        if (StringUtils.hasLength(args.getTextArg())) {
            BodyPart part=new MimeBodyPart();
            part.setHeader("Content-ID", "<summary.html>");
            part.setContent(args.getTextArg(), "text/html; charset=UTF-8;");

            multipart.addBodyPart(part);
        }

        attachDocuments(multipart);

        message.setContent(multipart);

        Transport.send(message);
    }

    private InternetAddress getFromAndReplyToAddress() throws UnsupportedEncodingException {
        String from = null;
        if (StringUtils.hasLength(args.getFromArg())) {
            from = args.getFromArg();
        } else {
            from = getDefaultFramAddress();
        }
        InternetAddress fromAddress = new InternetAddress(from, from);
        return fromAddress;
    }

    private String getDefaultFramAddress() {
        return args.getUserArg() + "@" + args.getHostArg();
    }

    private Authenticator getSmtpAuthenticator() {
        if ("true".equals(args.getAuthArg())) {
            Authenticator authenticator=new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    String username = args.getUserArg();
                    String password = args.getPasswordArg();
                    return new PasswordAuthentication(username, password);
                }
            };
            return authenticator;
        }
        return null;
    }

    private void attachDocuments(MimeMultipart multipart) throws Exception {

        if (args.getAttachmentsArg() != null) {
            for (Document attachment : args.getAttachmentsArg()) {
                Document attachmentToSend = null;
                if (attachment.getSystemId() == NOT_PERSISTED) {
                    attachmentToSend = attachment;
                } else {
                    attachmentToSend = (Document) getCore().queryUnique("get.document.by.systemId", attachment.getSystemId());
                }

                if (attachmentToSend != null) {
                    BodyPart part = new MimeBodyPart();
                    part.setHeader("Content-ID", "<" +
                            (attachmentToSend.getFileName() == null ? "attachment" : attachmentToSend.getFileName().replace(" ", "")) + ">");
                    if (attachment.getOtherType() != null) {
                        part.setHeader("Content-Disposition", attachment.getOtherType());
                    } else {
                        part.setHeader("Content-Disposition", "attachment");
                    }
                    if (attachmentToSend.getDocumentContent() != null) {
                        part.setDataHandler(
                                new DataHandler(
                                        new ByteArrayDataSource(attachmentToSend.getDocumentContent().getContent(), attachmentToSend.getMimeType())));
                    } else {
                        part.setContent("ERROR ADDING ATTACHMENT: " + attachment.getTitle(), "text/html; charset=UTF-8;");
                    }
                    part.setFileName(attachmentToSend.getFileName());

                    multipart.addBodyPart(part);
                }
            }
        }
    }

    private Properties getMailProperties() {
        Properties properties = new Properties();
        properties.put(MAIL_SMTP_HOST, args.getHostArg());
        properties.put(MAIL_SMTP_PORT, args.getPortArg());

        if (StringUtils.hasLength(args.getAuthArg())
                && "true".equals(args.getAuthArg())) {
            properties.put(MAIL_SMTP_AUTH, args.getAuthArg());
            properties.put(MAIL_SMTP_USER, args.getUserArg());
            properties.put(MAIL_SMTP_PASSWORD, args.getPasswordArg());
        }

        if (StringUtils.hasLength(args.getStartTlsEnabledArg())) {
            properties.put(MAIL_SMTP_STARTTLS_ENABLE, args.getStartTlsEnabledArg());
        }

        return properties;
    }

    public static void setMailProperties(SendEmailCommand sendCommand, Properties mailProperties) {
        sendCommand.setHostArg(mailProperties.getProperty(MAIL_SMTP_HOST));
        sendCommand.setPortArg(mailProperties.getProperty(MAIL_SMTP_PORT));
        sendCommand.setAuthArg(mailProperties.getProperty(MAIL_SMTP_AUTH));
        sendCommand.setUserArg(mailProperties.getProperty(MAIL_SMTP_USER));
        sendCommand.setPasswordArg(mailProperties.getProperty(MAIL_SMTP_PASSWORD));
        sendCommand.setStartTlsEnabledArg(mailProperties.getProperty(MAIL_SMTP_STARTTLS_ENABLE));
    }

    @ServiceCommand
    public interface SendEmailCommand extends Command, SendEmailArgument {
    }

    @ServiceArgument
    public interface SendEmailArgument extends Argument {


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
         * From email address - defaults to mail properties
         * @param from
         */
        void setFromArg(String from);

        String getTextArg();

        /**
         * Alternative simple way of specifying body summary text
         * @param text
         */
        void setTextArg(String text);

        List<Document> getAttachmentsArg();

        /**
         * Mail attachments including body text if specified
         * @param attachments
         */
        void setAttachmentsArg(List<Document> attachments);

        /**
         * Mail properties
         */
        String getHostArg();

        void setHostArg(String host);

        String getPortArg();

        void setPortArg(String poer);

        String getAuthArg();

        void setAuthArg(String auth);

        String getUserArg();

        void setUserArg(String user);

        String getPasswordArg();

        void setPasswordArg(String password);

        String getStartTlsEnabledArg();

        void setStartTlsEnabledArg(String enabled);

    }
}
