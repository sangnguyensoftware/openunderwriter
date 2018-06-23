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
package com.ail.pageflow.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;

import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.CoreProxy;
import com.ail.core.Functions;
import com.ail.core.document.Document;
import com.ail.core.document.DocumentRequest;
import com.ail.core.document.FetchDocumentService.FetchDocumentCommand;
import com.ail.insurance.policy.Policy;

//@WebServlet(name = "FetchDocumentServlet", urlPatterns = { "/fetchDocument" })
public class FetchDocumentServlet extends HttpServlet {

    private static final long serialVersionUID = 6984589565187737714L;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String reference = request.getParameter("reference");
        CoreProxy core = new CoreProxy();

        DocumentRequest dr = (DocumentRequest) core.queryUnique("get.documentRequest.by.requestId", reference);
        if (dr != null) {
            switch (dr.getRequestType()) {
            case GENERATE_AND_DOWNLOAD:
                generateAndDownload(request, response, core, dr);
                break;
            default:
                throw new ServletException("Document requst type for request systemId=" + dr.getSystemId() + " not recognised.");
            }
        } else {
            Document doc = (Document) core.queryUnique("get.document.by.externalSystemId", reference);
            if (doc != null) {
                downloadOnly(request, response, doc);
            } else {
                response.sendError(HttpServletResponse.SC_GONE);
            }
        }
    }

    private void downloadOnly(HttpServletRequest request, HttpServletResponse response, Document document) throws IOException {
        if (StringUtils.hasLength(document.getMimeType())) {
            response.setContentType(document.getMimeType());
        } else {
            response.setContentType("application/pdf");
        }

        addHeadersToResponse(request, response, document);

        response.getOutputStream().write(document.getDocumentContent().getContent());
        response.getOutputStream().flush();
    }

    private void generateAndDownload(HttpServletRequest request, HttpServletResponse response, CoreProxy core, DocumentRequest dr) throws IOException, ServletException {
        String documentType = dr.getDocumentType();
        Long sourceUID = dr.getSourceUID();

        try {
            String commandName = "Fetch" + dr.getDocumentType() + "Document";

            Document document = generete(core, sourceUID, commandName);

            response.setContentType("application/pdf");

            addHeadersToResponse(request, response, document);

            response.getOutputStream().write(document.getDocumentContent().getContent());
            response.getOutputStream().flush();
        } catch (BaseException e) {
            throw new ServletException("Failed to fetch " + documentType + " (policyUID:" + sourceUID + ") for display.", e);
        }
    }

    private Document generete(CoreProxy core, Long policyUID, String command) throws BaseException {
        try {
            CoreContext.initialise();
            CoreContext.setProductName(fetchProductName(core, policyUID));
            CoreContext.setCoreProxy(createCoreProxy());

            FetchDocumentCommand cmd = core.newCommand(command, FetchDocumentCommand.class);
            cmd.setModelIDArg(policyUID);

            cmd.invoke();

            return cmd.getDocumentRet();
        } finally {
            CoreContext.destroy();
        }
    }

    private CoreProxy createCoreProxy() {
        return new CoreProxy(Functions.productNameToConfigurationNamespace(CoreContext.getProductName()));
    }

    private String fetchProductName(CoreProxy core, long policyUID) {
        Policy policy = (Policy) core.queryUnique("get.policy.by.systemId", policyUID);
        return policy.getProductTypeId();
    }

    private void addHeadersToResponse(HttpServletRequest request, HttpServletResponse response, Document document) {
        response.setHeader("Pragma", "private");
        response.setHeader("Cache-Control", "private");
        response.setHeader("Set-Cookie", "fileDownload=true; path=/");

        if (isInlineResponseRequested(request)) {
            response.setHeader("Content-Disposition", "inline");
        } else {
            response.setHeader("Content-Disposition", "attachment;filename=\"" + document.getFileName() + "\"");
        }
    }

    private boolean isInlineResponseRequested(HttpServletRequest request) {
        return request.getParameter("inline") != null;
    }
}
