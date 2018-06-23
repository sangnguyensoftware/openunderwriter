package com.ail.pageflow.render;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.List;

import javax.mail.MessagingException;

import org.springframework.stereotype.Service;

import com.ail.core.BaseException;
import com.ail.core.Core;
import com.ail.core.document.Document;
import com.ail.core.document.DocumentType;
import com.ail.insurance.policy.Policy;
import com.ail.pageflow.BrokerQuotationSummary;
import com.ail.pageflow.render.RenderService.RenderCommand;

/**
 * Create a BodyPart holding the assessment sheet (HTML) and attach it to
 * the specified MimeMultipart.
 */
@Service
public class AttachAssessmentSheetToEmail {
    public static void invoke(Core core, Policy policy, List<Document> attachments) throws BaseException, MessagingException, MalformedURLException, IOException {
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        PrintWriter writer=new PrintWriter(baos);

        new AddStyleForEmail().invoke(writer);

        RenderCommand rbqs=core.newCommand("AssessmentSheetDetails", "text/html", RenderCommand.class);
        rbqs.setModelArgRet(policy);
        rbqs.setWriterArg(writer);
        rbqs.setRenderIdArg("email");
        rbqs.setPageElementArg(new BrokerQuotationSummary());
        rbqs.invoke();

        writer.close();

        Document attachment = core.create(new Document(
                DocumentType.ATTACHMENT.name(), "attachment", baos.toByteArray(), "assessment.html",
                policy.getQuotationNumber() + " Assessment.html", "text/html", policy.getProductTypeId()));
        attachments.add(attachment);

    }
}