/* Copyright Applied Industrial Logic Limited 2003. All rights Reserved */
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Set;

import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.rtf.RTFEditorKit;

import com.ail.annotation.ServiceImplementation;
import com.ail.core.CoreContext;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.Type;
import com.ail.core.language.I18N;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImage;
import com.itextpdf.text.pdf.PdfIndirectObject;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * This class provides an implementation of the render document service which renders to PDF
 * using iText.<p/>
 */
@ServiceImplementation
public class RenderItextPdfDocumentService extends Service<RenderDocumentService.RenderDocumentArgument> {

    @Override
    public void invoke() throws PreconditionException, PostconditionException, RenderException {
        if (args.getSourceDataArg()==null) {
            throw new PreconditionException("args.getSourceDataArg()==null");
        }

        if (args.getTemplateUrlArg()==null || args.getTemplateUrlArg().length()==0) {
            throw new PreconditionException("args.getTemplateUrlArg()==null || args.getTemplateUrlArg().length()==0");
        }

        boolean pdfaConformance = false;
        if (args.getPDFaConformanceArg()!=null && args.getPDFaConformanceArg().equals("TRUE")) {
            pdfaConformance = true;
        }

        // The resulting PDF will end up in this array
        ByteArrayOutputStream output=new ByteArrayOutputStream();
        PdfStamper stamper = null;
        PdfReader pdfTemplate = null;
        Type data = null;

        try {
            data=(Type)core.fromXML(args.getSourceDataArg().getType(), args.getSourceDataArg());
        } catch (Exception e) {
            throw new PreconditionException("Failed to translate source data into an object.", e);
        }

        try {
            String xpath=null;
            pdfTemplate = new PdfReader(new URL(args.getTemplateUrlArg()));

            stamper = new PdfStamper(pdfTemplate, output);

            Set<String> fieldNames = stamper.getAcroFields().getFields().keySet();
            List<String> imagePlaceholders = new ArrayList<>();

            for (String fieldName : fieldNames) {

                if(stamper.getAcroFields().getFieldType(fieldName)==AcroFields.FIELD_TYPE_PUSHBUTTON) {
                    if(fieldName.endsWith("_af_image")) {

                        imagePlaceholders.add(fieldName);

                        List<AcroFields.FieldPosition> positionList = stamper.getAcroFields().getFieldPositions(fieldName);

                        try {

                            String docExternalId = "";
                            String attributeValue = "";
                            int attributeTypeIndex = fieldName.indexOf(":");
                            if(attributeTypeIndex>=0) {
                                attributeValue = fieldName.substring(attributeTypeIndex+1, fieldName.lastIndexOf("_af_image"));
                                String attributeName = fieldName.substring(0,attributeTypeIndex);
                                docExternalId = data.xpathGet("document["+attributeName+"='"+attributeValue+"'][1]/externalSystemId", "").toString();
                            }
                            else {
                                attributeValue = fieldName.substring(0, fieldName.lastIndexOf("_af_image"));
                                docExternalId = data.xpathGet("document[fileName='"+attributeValue+"'][1]/externalSystemId", "").toString();
                            }

                            if(!docExternalId.isEmpty()) {
                                String protocol = CoreContext.getCoreProxy().getParameterValue("ProductRepository.Protocol");
                                String host = CoreContext.getCoreProxy().getParameterValue("ProductRepository.Host");
                                String port = CoreContext.getCoreProxy().getParameterValue("ProductRepository.Port");
                                URL imageUrl = new URL(protocol+"://"+host+":"+port+"/pageflow-portlet/fetchDocument?reference="+docExternalId);

                                for (AcroFields.FieldPosition fieldPosition : positionList) {
                                    Rectangle position = fieldPosition.position;
                                    Image image = Image.getInstance(imageUrl);
                                    image.setAbsolutePosition(position.getLeft(), position.getBottom());
                                    image.scaleToFit(position.getWidth(), position.getHeight());

                                    PdfImage stream = new PdfImage(image, attributeValue, null);
                                    PdfIndirectObject ref = stamper.getWriter().addToBody(stream);
                                    image.setDirectReference(ref.getIndirectReference());
                                    PdfContentByte over = stamper.getOverContent(fieldPosition.page);
                                    over.addImage(image);
                                }
                            }

                        } catch (Exception e) {
                            throw new PreconditionException("Image insertion failue: '"+fieldName+"': "+e.toString());
                        }
                    }
                }
                else {
                    xpath = stamper.getAcroFields().getField(fieldName);
                    if(xpath.toLowerCase().startsWith("html:")) {

                        int attributeTypeIndex = xpath.indexOf(":");
                        xpath = xpath.substring(attributeTypeIndex+1);

                        String htmlStr = "";
                        try {
                            htmlStr = data.xpathGet(xpath).toString();
                        } catch (Exception e) {
                            throw new PreconditionException("xpath expression: '"+xpath+"' could not be evaulated: "+e.toString());
                        }

                        OutputStream os = new ByteArrayOutputStream();
                        HTMLEditorKit htmlEditorKit = new HTMLEditorKit();
                        RTFEditorKit rtfEditorKit = new RTFEditorKit();
                        String rtfStr = null;

                        htmlStr = htmlStr.replaceAll("<br.*?>","#NEW_LINE#");
                        htmlStr = htmlStr.replaceAll("</p>","#NEW_LINE#");
                        htmlStr = htmlStr.replaceAll("<p.*?>","");
                        InputStream is = new ByteArrayInputStream(htmlStr.getBytes());
                        try {
                            javax.swing.text.Document doc = htmlEditorKit.createDefaultDocument();
                            htmlEditorKit.read(is, doc, 0);
                            rtfEditorKit .write(os, doc, 0, doc.getLength());
                            rtfStr = os.toString();
                            rtfStr = rtfStr.replaceAll("#NEW_LINE#","\\\\par ");
                        } catch (IOException e) {
                              e.printStackTrace();
                        } catch (BadLocationException e) {
                              e.printStackTrace();
                        }
                        stamper.getAcroFields().setFieldRichValue(fieldName, rtfStr);

                    }
                    else {
                        try {
                            String value = data.xpathGet(xpath).toString();
                            value = i18n(value);
                            stamper.getAcroFields().setField(fieldName, value);
                        } catch (Exception e) {
                            throw new PreconditionException("xpath expression: '"+xpath+"' could not be evaulated: "+e.toString());
                        }
                    }

                }
            }

            for (String imagePlaceholder : imagePlaceholders) {
               stamper.getAcroFields().removeField(imagePlaceholder);
            }

            stamper.setFormFlattening(true);

            if(pdfaConformance) {
                stamper.getWriter().setPdfVersion(PdfWriter.PDF_VERSION_1_4);
                stamper.getWriter().setPDFXConformance(PdfWriter.PDFA1A);
                stamper.getWriter().createXmpMetadata();
            }

            stamper.close();
            pdfTemplate.close();
        }
        catch (DocumentException e) {
            throw new RenderException("PDF generation failed: " + e);
        } catch (MalformedURLException e) {
            throw new RenderException("Failed to read template: '"+args.getTranslationUrlArg()+"' "+e);
        } catch (IOException e) {
            throw new RenderException("IO Exception during document render: "+e);
        }
        finally {
            if (stamper!=null) {
                try {
                    stamper.close();
                } catch (Exception e) {
                    // Ignored
                }
            }
        }

        args.setRenderedDocumentRet(output.toByteArray());

        if (args.getRenderedDocumentRet()==null || args.getRenderedDocumentRet().length==0) {
            throw new PostconditionException("args.getRenderedDocumentRet()==null || args.getRenderedDocumentRet().length==0");
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

