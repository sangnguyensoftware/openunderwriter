/* Copyright Applied Industrial Logic Limited 2018. All rights Reserved */
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

package com.ail.core.document;

import static com.ail.core.CoreContext.getCoreProxy;
import static fr.opensagres.xdocreport.template.formatter.NullImageBehaviour.KeepImageTemplate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

import com.ail.annotation.ServiceImplementation;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.Type;
import com.ail.core.document.liferay.LiferayFileDownloadService.LiferayFileDownloadCommand;
import com.ail.core.language.I18N;

import fr.opensagres.xdocreport.converter.ConverterTypeTo;
import fr.opensagres.xdocreport.converter.Options;
import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.core.document.SyntaxKind;
import fr.opensagres.xdocreport.core.io.XDocArchive;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.images.ByteArrayImageProvider;
import fr.opensagres.xdocreport.document.images.IImageProvider;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.FieldExtractor;
import fr.opensagres.xdocreport.template.FieldsExtractor;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import fr.opensagres.xdocreport.template.formatter.FieldsMetadata;

/**
 * This class provides an implementation of the render document service which
 * renders to PDF using the
 * <a href="https://github.com/opensagres/xdocreport">XDocReport document
 * generator</a>.
 * <p/>
 */
@ServiceImplementation
public class RenderXDocReportPdfDocumentService extends Service<RenderDocumentService.RenderDocumentArgument> {

    @Override
    public void invoke() throws PreconditionException, PostconditionException, RenderException {
        if (args.getSourceDataArg() == null && args.getSourceModelArg() == null) {
            throw new PreconditionException("args.getSourceDataArg() == null && args.getSourceModelArg() == null");
        }

        if (args.getTemplateUrlArg() == null || args.getTemplateUrlArg().length() == 0) {
            throw new PreconditionException("args.getTemplateUrlArg()==null || args.getTemplateUrlArg().length()==0");
        }

        args.setRenderedDocumentRet(renderDocument().toByteArray());

        if (args.getRenderedDocumentRet() == null || args.getRenderedDocumentRet().length == 0) {
            throw new PostconditionException("args.getRenderedDocumentRet()==null || args.getRenderedDocumentRet().length==0");
        }
    }

    private ByteArrayOutputStream renderDocument() throws PreconditionException, RenderException {
        Type data = fetchData();

        IXDocReport report = fetchReport();

        Map<String, Boolean> fields = fetchFields(report, report.createFieldsMetadata());

        IContext context = createContext(report);

        processFields(data, fields, context);

        return generatePdf(report, context);
    }

    private ByteArrayOutputStream generatePdf(IXDocReport report, IContext context) throws RenderException {
        Options options = Options.getTo(ConverterTypeTo.PDF);

        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            report.convert(context, options, output);

            return output;
        } catch (XDocReportException | IOException e) {
            throw new RenderException("XDoc Converter Exception during report pdf conversion: '" + args.getTranslationUrlArg() + "'", e);
        }
    }

    private void processFields(Type data, Map<String, Boolean> fields, IContext context) throws PreconditionException {
        for (String fieldName : fields.keySet()) {
            if (fieldName.endsWith("_image")) {
                try {
                    String docExternalId = "";
                    String attributeValue = "";
                    int attributeTypeIndex = fieldName.indexOf(":");
                    if (attributeTypeIndex >= 0) {
                        attributeValue = fieldName.substring(attributeTypeIndex + 1, fieldName.lastIndexOf("_image"));
                        String attributeName = fieldName.substring(0, attributeTypeIndex);
                        docExternalId = data.xpathGet("document[" + attributeName + "='" + attributeValue + "'][1]/externalSystemId", "").toString();
                    } else {
                        attributeValue = fieldName.substring(0, fieldName.lastIndexOf("_image"));
                        docExternalId = data.xpathGet("document[fileName='" + attributeValue + "'][1]/externalSystemId", "").toString();
                    }

                    IImageProvider image = null;

                    if (!docExternalId.isEmpty()) {
                        String protocol = getCoreProxy().getParameterValue("ProductRepository.Protocol");
                        String host = getCoreProxy().getParameterValue("ProductRepository.Host");
                        String port = getCoreProxy().getParameterValue("ProductRepository.Port");
                        URL imageUrl = new URL(protocol + "://" + host + ":" + port + "/pageflow-portlet/fetchDocument?reference=" + docExternalId);
                        image = new ByteArrayImageProvider(imageUrl.openStream());
                    }
                    else {
                        LiferayFileDownloadCommand lfdc = getCoreProxy().newCommand(LiferayFileDownloadCommand.class);
                        URL imageUrl = new URL(data.xpathGet(attributeValue, null));
                        String filename = FilenameUtils.getBaseName(imageUrl.getPath());
                        lfdc.setPatternArg(filename);
                        lfdc.setFolderPathArg(FilenameUtils.getFullPath(imageUrl.getPath()));
                        lfdc.setCreatorNameArg(getCoreProxy().getParameterValue("ProductRepository.Username"));
                        lfdc.setCompanyIdArg(new Long(getCoreProxy().getParameterValue("ProductRepository.CompanyID")));
                        lfdc.setRepositoryIdArg(new Long(getCoreProxy().getParameterValue("ProductRepository.RepositoryID")));
                        lfdc.invoke();

                        image = new ByteArrayImageProvider(lfdc.getFilesRet().get(filename));
                    }

                    if (image != null) {
                        image.setBehaviour(KeepImageTemplate);
                        image.setUseImageSize(true);
                        image.setResize(true);
                        context.put(fieldName, image);
                    }
                } catch (Exception e) {
                    throw new PreconditionException("Image insertion failue: '" + fieldName + "': ", e);
                }
            } else {
                String fieldValue = "";

                if (fieldName.endsWith("_html")) {
                    String attributeName = fieldName.substring(0, fieldName.lastIndexOf("_html"));
                    attributeName = attributeName.replace('_', ' ');
                    fieldValue = data.xpathGet(attributeName, "").toString();
                } else {
                    fieldValue = data.xpathGet(fieldName.replace('_', ' '), fieldName).toString();
                    fieldValue = i18n(fieldValue);
                }
                context.put(fieldName, fieldValue);
            }
        }
    }

    private IContext createContext(IXDocReport report) throws RenderException {
        try {
            return report.createContext();
        } catch (XDocReportException e) {
            throw new RenderException("XDoc Report Exception during report create context: '" + args.getTranslationUrlArg() + "'", e);
        }
    }

    private Map<String, Boolean> fetchFields(IXDocReport report, FieldsMetadata metadata) throws RenderException {
        Map<String, Boolean> fields = new HashMap<>();
        FieldsExtractor<FieldExtractor> copyFieldsExtractor = FieldsExtractor.create();
        try {
            createCopyOf(report).extractFields(copyFieldsExtractor);
        } catch (XDocReportException | IOException e) {
            throw new RenderException("XDoc Report Exception during report copy field read: '" + args.getTranslationUrlArg() + "'", e);
        }
        for (FieldExtractor field : copyFieldsExtractor.getFields()) {
            String fieldN = field.getName();
            Boolean fieldL = Boolean.valueOf(field.isList());
            fields.put(fieldN, fieldL);
            if (fieldN.endsWith("_html")) {
                metadata.addFieldAsTextStyling(fieldN, SyntaxKind.Html);
            } else if (fieldN.endsWith("_image")) {
                metadata.addFieldAsImage(fieldN);
            }
        }
        return fields;
    }

    private IXDocReport createCopyOf(IXDocReport report) throws RenderException {
        report.setCacheOriginalDocument(true);
        XDocArchive arch = report.getOriginalDocumentArchive();
        try {
            IXDocReport reportCopy = XDocReportRegistry.getRegistry().createReport(arch.createCopy());
            reportCopy.setTemplateEngine(report.getTemplateEngine());
            return reportCopy;
        } catch (XDocReportException | IOException e) {
            throw new RenderException("Exception during create report copy: '" + args.getTranslationUrlArg() + "'", e);
        }
    }

    private IXDocReport fetchReport() throws RenderException {
        try {
            InputStream inTemplate = (new URL(args.getTemplateUrlArg())).openStream();
            return XDocReportRegistry.getRegistry().loadReport(inTemplate, TemplateEngineKind.Velocity);
        } catch (IOException | XDocReportException e) {
            throw new RenderException("Template load issue: '" + args.getTranslationUrlArg() + "'", e);
        }
    }

    private Type fetchData() throws PreconditionException {
        if (args.getSourceModelArg() != null) {
            return args.getSourceModelArg();
        }
        try {
            return (Type) core.fromXML(args.getSourceDataArg().getType(), args.getSourceDataArg());
        } catch (Exception e) {
            throw new PreconditionException("Failed to translate source data into an object.", e);
        }
    }

    public String i18n(String key) {
        return I18N.i18n(key);
    }

    public String i18n(String key, Object... args) {
        String format = I18N.i18n(key);
        Formatter formatter = new Formatter();
        String ret = formatter.format(format, args).toString();
        formatter.close();
        return ret;
    }
}
