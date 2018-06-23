/* Copyright Applied Industrial Logic Limited 2016. All rights Reserved */
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

package com.ail.pageflow.service;

import static com.ail.core.CoreContext.getRequestWrapper;
import static com.ail.core.CoreContext.setServiceRequestRecord;
import static com.ail.core.jsonmapping.JSONFunctions.compressJSON;
import static com.ail.core.jsonmapping.JSONFunctions.tidyJSON;
import static com.ail.pageflow.PageFlowContext.getCurrentPageName;
import static com.ail.pageflow.PageFlowContext.getPageFlowName;
import static com.ail.pageflow.PageFlowContext.getPolicy;
import static java.lang.String.format;
import static java.net.HttpURLConnection.HTTP_CONFLICT;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_NOT_IMPLEMENTED;
import static java.net.HttpURLConnection.HTTP_OK;

import java.io.IOException;

import javax.ws.rs.core.Response;

import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import com.ail.core.CoreContext;
import com.ail.core.CoreProxy;
import com.ail.core.ExceptionRecord;
import com.ail.core.Functions;
import com.ail.core.JSONException;
import com.ail.core.RestfulServiceReturn;
import com.ail.core.factory.UndefinedTypeError;
import com.ail.core.logging.ServiceRequestRecord;
import com.ail.insurance.policy.Policy;
import com.ail.pageflow.PageFlowContext;
import com.ail.pageflow.RenderingError;
import com.ail.pageflow.portlet.PageFlowCommon;
import com.ail.pageflow.service.InitialisePageFlowContextService.InitialisePageFlowContextCommand;

public class RestfulPageFlowBridge {

    private String policyId;
    private String jsonData;
    private MultipartFormDataInput parts;
    private String product;
    private String pageFlowName;
    private String pageName;
    private String version;
    private PageFlowCommon pageFlowCommon;

    public RestfulPageFlowBridge(String pageFlowName) {
        this.pageFlowName = pageFlowName;

        this.pageFlowCommon = new PageFlowCommon();
    }

    public RestfulPageFlowBridge product(String product) {
        this.product = product;
        return this;
    }

    public RestfulPageFlowBridge page(String pageName) {
        this.pageName = pageName;
        return this;
    }

    public RestfulPageFlowBridge policyId(String policyId) {
        this.policyId = policyId;
        return this;
    }

    public Response invoke() {
        Response response = null;
        Policy policy = null;

        try {
            initialiseServiceRequestRecord();

            if (policyId != null) {
                CoreProxy cp = new CoreProxy();

                policy = (Policy) cp.queryUnique("get.policy.by.externalSystemId", policyId);

                if (policy == null) {
                    cp.logInfo("Request policy does not exist: " + policyId);
                    response = Response.status(HTTP_NOT_FOUND).build();
                }

                PageFlowContext.setPolicySystemId(policy.getSystemId());
                PageFlowContext.setPageFlowInitliased(true);

                product = policy.getProductTypeId();
            }

            CoreProxy coreProxy = new CoreProxy(Functions.productNameToConfigurationNamespace(product));

            if (determineByPolicyHolder(policy, coreProxy)) {
                CoreContext.setRemoteUser(policy.getOwningUser());
            }

            PageFlowContext.setCoreProxy(coreProxy);
            PageFlowContext.setProductName(product);
            PageFlowContext.setPageFlowName(pageFlowName);
            PageFlowContext.setVersion(version);

            if (pageName!=null) {
                PageFlowContext.setNextPageName(pageName);
                PageFlowContext.setCurrentPageName(pageName);
            }

            initialisePageFlowContext();

            if (response == null) {
                PageFlowContext.setRestfulResponse(new RestfulServiceReturn(HTTP_OK));
                PageFlowContext.getResponseWrapper().startWriter();

                if (PageFlowContext.getPolicy() != null) {
                    pageFlowCommon.processAction();

                    if (thereWereNoValidationErrors() && requestedOperationWasNotProcsesed()) {
                        response = Response.status(HTTP_CONFLICT).entity(errorEntity("i18n_stale_request_error", "The operation requested ("+PageFlowContext.getRequestedOperation()+") is not compatible with the specified data")).build();
                    }
                }

                if (response == null) {
                    pageFlowCommon.doView();

                    PageFlowContext.getResponseWrapper().stopWriter();

                    response = buildResponse();
                }
            }
        } catch(IllegalArgumentException e) {
            logError(e);
            response = Response.status(HTTP_INTERNAL_ERROR).entity(errorEntity("i18n_internal_error", e)).build();
        } catch (Throwable e) {
            logError(e);
            if (e.getCause() instanceof UndefinedTypeError) {
                response = Response.status(HTTP_NOT_IMPLEMENTED).build();
            } else {
                response = Response.status(HTTP_INTERNAL_ERROR).entity(errorEntity("i18n_internal_error", e)).build();
            }
        } finally {
            writeServiceRequestRecord(response);
        }

        return response;
    }

    private boolean determineByPolicyHolder(Policy policy, CoreProxy coreProxy) {
        return policy != null && coreProxy.getParameterValue("DetermineRemoteUserByPolicyOwner", null) != null; // && CoreContext.getRemoteUser() == null;
    }

    private boolean thereWereNoValidationErrors() {
        return !PageFlowContext.getResponseWrapper().isValidationErrorsFound();
    }

    private boolean requestedOperationWasNotProcsesed() {
        return PageFlowContext.getRequestedOperation() != null && !PageFlowContext.getOperationsProcessed().contains(PageFlowContext.getRequestedOperation());
    }

    private String errorEntity(String errorCode, String errorMessage) {
        return "{ \"success\": false, \"errorCode\": \""+errorCode+"\", \"errorMessage\": \""+errorMessage+"\"}";
    }

    private String errorEntity(String errorCode, Throwable t) {
        return errorEntity(errorCode, t.toString().replace('"', '\''));
    }

    private void logError(Throwable e) {
        Policy policy = PageFlowContext.getPolicy();
        if (policy != null) {
            policy.addException(new ExceptionRecord(e));
        }

        String json = null;
        try {
            json = (jsonData != null ? jsonData : getJsonFromMultipart(parts));
        } catch (IOException oie) {
            PageFlowContext.getCoreProxy().logError("JSON logging error:" + oie.getMessage(), oie);
        }

        json = obfiscatePasswords(json);

        PageFlowContext.getCoreProxy().logError("Service call exception:" + e.getMessage()
                + "\n: pid: " + policyId
                + "\n: product: " + product
                + "\n: pageFlowName: " + pageFlowName
                + "\n: json: " + json + "\n", e);
    }

    String getJsonFromMultipart(MultipartFormDataInput parts) throws IOException {
        if (parts!=null) {
            for (InputPart part : parts.getParts()) {
                if (part.getMediaType() != null) {
                    if (isJsonPart(part)) {
                        return part.getBodyAsString();
                    }
                }
            }
        }
        return null;
    }

    Response buildResponse() throws JSONException {
        String jsonResponse;

        jsonResponse = format("{\"success\" : true, \"action\": \"%s\", %s }",
                           generateActionURL(),
                           tidyJSON(compressJSON(PageFlowContext.getResponseWrapper().getWriterBuffer().getBuffer())));

        return Response.status(HTTP_OK).entity(jsonResponse).build();
    }

    private String generateActionURL() {
        if (PageFlowContext.getPolicy() != null) {
            return format("%s/%s/policy/%s/page/%s",
                    getRequestWrapper().getServletRequest().getContextPath(),
                    getPageFlowName(),
                    getPolicy().getExternalSystemId(),
                    getCurrentPageName());
        }
        else {
            return format("%s/%s/product/%s/page/%s",
                    getRequestWrapper().getServletRequest().getContextPath(),
                    getPageFlowName(),
                    PageFlowContext.getProductName(),
                    getCurrentPageName());
        }
    }

    private void initialisePageFlowContext() throws RenderingError {
        try {
            CoreProxy coreProxy = new CoreProxy();
            InitialisePageFlowContextCommand command = coreProxy.newCommand(InitialisePageFlowContextCommand.class);
            command.invoke();
        } catch (Exception e) {
            throw new RenderingError("Failed to initialise PageFlowContext: " + e, e);
        }
    }

    private void initialiseServiceRequestRecord() {
        ServiceRequestRecord srr = new ServiceRequestRecord(product, pageFlowName + ((pageName != null) ? "/" + pageName : ""), policyId);
        PageFlowContext.getCoreProxy().create(srr);
        setServiceRequestRecord(srr);
    }

    private void writeServiceRequestRecord(Response response) {
        ServiceRequestRecord srr = CoreContext.getServiceRequestRecord();
        srr.setRequest(obfiscatePasswords(PageFlowContext.getRestfulRequestData()));
        srr.setExitTimestamp();

        if (response!=null && response.getEntity()!=null) {
            srr.setResponse(response.getEntity().toString());
        }
    }

    private boolean isJsonPart(InputPart part) {
        return part.getMediaType().toString().contains("application/json") || part.getMediaType().toString().contains("text/plain");
    }

    private String obfiscatePasswords(String json) {
        if (json == null) {
            return null;
        }

        return json.replaceFirst("[\\\"']password[\\\"']\\s*:\\s*[\\\"'][^\\\"']*[\\\"']","'password' : '********'");
    }
}