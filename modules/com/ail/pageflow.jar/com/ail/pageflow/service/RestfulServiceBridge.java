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

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_NOT_IMPLEMENTED;
import static java.net.HttpURLConnection.HTTP_OK;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import com.ail.core.Attribute;
import com.ail.core.CoreContext;
import com.ail.core.CoreProxy;
import com.ail.core.ExceptionRecord;
import com.ail.core.Functions;
import com.ail.core.JSONException;
import com.ail.core.RestfulServiceInvoker.ClientError;
import com.ail.core.RestfulServiceReturn;
import com.ail.core.RestfulServiceStatusReturn;
import com.ail.core.factory.UndefinedTypeError;
import com.ail.core.logging.ServiceRequestRecord;
import com.ail.insurance.claim.Claim;
import com.ail.insurance.policy.Policy;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionCommand;
import com.ail.pageflow.PageFlowContext;
import com.ail.pageflow.RenderingError;
import com.ail.party.Party;
import com.google.common.collect.Maps;

public class RestfulServiceBridge {

    private String policyId;
    private String partyId;
    private String claimId;
    private String jsonData;
    private MultipartFormDataInput parts;
    private String product;
    private String command;
    private String userAgent;
    private String version;

    public RestfulServiceBridge(String command, HttpServletRequest request, HttpServletResponse response, ServletConfig config) {
        this.command = command;
        if (request != null) {
            userAgent = request.getHeader("User-Agent");
            version = request.getHeader("Interface-Version");
            if (version == null || version.isEmpty()) {
                version= "0.0";
            }
        }
    }

    public RestfulServiceBridge product(String product) {
        this.product = product;
        return this;
    }

    public RestfulServiceBridge policyId(String policyId) {
        this.policyId = "null".equals(policyId) ? null : policyId;
        return this;
    }

    public RestfulServiceBridge partyId(String partyId) {
        this.partyId = "null".equals(partyId) ? null : partyId;
        return this;
    }

    public RestfulServiceBridge claimId(String claimId) {
        this.claimId = "null".equals(claimId) ? null : claimId;
        return this;
    }

    public RestfulServiceBridge jsonData(String jsonData) {
        this.jsonData = jsonData;
        return this;
    }

    public RestfulServiceBridge multiparts(MultipartFormDataInput parts) {
        this.parts = parts;
        return this;
    }

    public Response invoke() {
        Response response = null;

        try {
            initialiseServiceRequestRecord();

            if (policyId != null) {
                CoreProxy cp = new CoreProxy();
                Policy policy = (Policy) cp.queryUnique("get.policy.by.externalSystemId", policyId);
                if (policy == null) {
                    cp.logInfo("Request policy does not exist: " + policyId);
                    response = Response.status(HTTP_NOT_FOUND).build();
                }
                else if (policyIsNotAccessibleToRemoteUser(policy)) {
                    cp.logInfo("User does not have acccess to request policy: " + policyId + ", user: " + PageFlowContext.getRemoteUser());
                    response = Response.status(HTTP_NOT_FOUND).build();
                }
                else {
                    if (product == null) {
                        product = policy.getProductTypeId();
                    }
                    PageFlowContext.setPolicy(policy);

                    setPolicyAttribute(policy, "UserAgent", userAgent);
                    setPolicyAttribute(policy, "InterfaceVersion", version);
                }
            }
            if (partyId != null) {
                CoreProxy cp = new CoreProxy();
                Party party = (Party) cp.queryUnique("get.party.by.externalSystemId", partyId);
                if (party == null) {
                    cp.logInfo("Request party does not exist: " + partyId);
                    response = Response.status(HTTP_NOT_FOUND).build();
                }
            }
            if (claimId != null) {
                CoreProxy cp = new CoreProxy();
                Claim claim = (Claim) cp.queryUnique("get.claim.by.externalSystemId", claimId);
                if (claim == null) {
                    cp.logInfo("Request claim does not exist: " + claimId);
                    response = Response.status(HTTP_NOT_FOUND).build();
                } else if (claimIsNotAccessibleToRemoteUser(claim)) {
                    cp.logInfo("User does not have acccess to request claim: " + claimId + ", user: " + PageFlowContext.getRemoteUser());
                    response = Response.status(HTTP_NOT_FOUND).build();
                } else {
                    if (product == null) {
                        product = claim.getPolicy().getProductTypeId();
                    }
                    PageFlowContext.setPolicy(claim.getPolicy());
                    PageFlowContext.setClaim(claim);
                }
            }

            CoreProxy coreProxy = new CoreProxy(Functions.productNameToConfigurationNamespace(product));

            PageFlowContext.setCoreProxy(coreProxy);
            PageFlowContext.setProductName(product);
            PageFlowContext.setPageFlowName("QuotationPageFlow");
            PageFlowContext.setVersion(version);

            if (response == null) {
                if (parts != null) {
                    PageFlowContext.setRestfulRequestPostData(getJsonFromMultipart(parts));
                    PageFlowContext.setRestfulRequestAttachment(getAttachmentFromMultipart(parts));
                } else if (jsonData != null) {
                    PageFlowContext.setRestfulRequestPostData(jsonData);
                }

                if (product == null) {
                    return Response.status(HTTP_BAD_REQUEST).entity(errorEntity("i18n_bad_arguments","product was not specified and could not be derived")).build();
                }

                execute("SelectPageFlowCommand");
                execute(command);
                execute("PersistPolicyFromPageFlowCommand");
                execute("PersistClaimFromPageFlowCommand");
                execute("PersistPartyFromPageFlowCommand");

                response = buildResponse();
            }
        } catch(IllegalArgumentException e) {
            logError(e);
            response = Response.status(HTTP_INTERNAL_ERROR).entity(errorEntity("i18n_internal_error", e)).build();
        } catch (Throwable e) {
            if (e.getCause() instanceof UndefinedTypeError) {
                response = Response.status(HTTP_NOT_IMPLEMENTED).build();
            } else {
                logError(e);
                response = Response.status(HTTP_INTERNAL_ERROR).entity(errorEntity("i18n_internal_error", e)).build();
            }
        } finally {
            writeServiceRequestRecord(response);
        }

        return response;
    }

    private String errorEntity(String errorCode, String errorMessage) {
        return "{ \"success\" : false, \"errorCode\" : \""+errorCode+"\", \"errorMessage\": \""+errorMessage+"\"}";
    }

    private String errorEntity(String errorCode, Throwable t) {
        return errorEntity(errorCode, t.toString().replace('"', '\''));
    }

    private void setPolicyAttribute(Policy policy, String id, String value) {
        if ((Boolean)policy.xpathGet("i:test(attribute[id='" + id + "'])")) {
            String storedValue = (String)policy.xpathGet("attribute[id='" + id + "']/value");
            if (storedValue == null || !storedValue.contains(value)) {
                storedValue += getFormattedAttributeValue(id, value);
                policy.xpathSet("attribute[id='" + id + "']/value", storedValue);
            }
        } else {
            policy.addAttribute(new Attribute(id, getFormattedAttributeValue(id, value), "string"));
        }
    }

    private String getFormattedAttributeValue(String id, String value) {
       return "[" + id + ": " + value + ", From: " + new Date() + ")] ";
    }

    Map<String, Object> getAttachmentFromMultipart(MultipartFormDataInput parts) throws IOException {
        for (InputPart part : parts.getParts()) {
            if (part.getMediaType() != null) {
                if (!isJsonPart(part)) {
                    InputStream inputStream = part.getBody(InputStream.class, null);
                    Map<String, Object> fileMap = Maps.newHashMap();
                    fileMap.put("file", IOUtils.toByteArray(inputStream));
                    fileMap.put("fileName", getFileName(part));
                    fileMap.put("mimeType", part.getMediaType().getType() + "/" + part.getMediaType().getSubtype());
                    return fileMap;
                }
            }
        }
        return null;
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
                + "\n: cmd: " + command
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
        RestfulServiceReturn ret = CoreContext.getRestfulResponse();

        String cacheControl = getCacheControl(ret);

        if (ret == null) {
            return Response.status(HTTP_OK).header("Cache-Control", cacheControl).entity("{\"success\" : true}").build();
        }

        if (ret.getClass().equals(ClientError.class)) {
            String errorCode = ((ClientError) ret).errorCode;
            return Response.status(ret.returnStatus).entity("{\"success\" : false, \"errorCode\" : \"" + errorCode.replace('"', '\'') + "\"}").build();
        } else if (ret.getClass().equals(RestfulServiceReturn.class)) {
            return Response.status(ret.returnStatus).header("Cache-Control", cacheControl).entity("{\"success\" : true}").build();
        } else if (ret.getClass().equals(RestfulServiceStatusReturn.class)) {
            return Response.status(ret.returnStatus).header("Cache-Control", cacheControl).build();
        } else {
            String result = CoreContext.getCoreProxy().toJSON(ret);
            result = result.replaceFirst("\\{", "{\"success\" : true,");
            result = result.replaceFirst(",}$", "}");
            return Response.status(ret.returnStatus).header("Cache-Control", cacheControl).entity(result).build();
        }
    }

    private String getCacheControl(RestfulServiceReturn ret) {
        int age = ret.getCacheMaxAge();
        if (age == RestfulServiceReturn.NO_CACHE) {
            return "no-cache, no-store";
        } else {
            return "private, max-age=" + age;
        }
    }

    void execute(String commandName) {
        ExecutePageActionCommand c = PageFlowContext.getCoreProxy().newCommand(ExecutePageActionCommand.class);

        c.setServiceNameArg(commandName);
        c.setModelArgRet(PageFlowContext.getPolicy());

        try {
            c.invoke();
        } catch (Throwable e) {
            if (e.getCause() != null && e.getCause().getCause() != null && e.getCause().getCause() instanceof IllegalArgumentException) {
                throw new IllegalArgumentException("Argument missmatch. Request's data cannot be mapped to arguments expected by " + commandName, e);
            } else {
                throw new RenderingError("Failed to execute action command: " + commandName, e);
            }
        }
    }

    private void initialiseServiceRequestRecord() {
        ServiceRequestRecord srr = new ServiceRequestRecord(product, command, policyId);
        CoreContext.getCoreProxy().create(srr);
        CoreContext.setServiceRequestRecord(srr);
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

    private boolean policyIsNotAccessibleToRemoteUser(Policy policy) {
        if (CoreContext.getRemoteUser() == null || policy.getOwningUser() == null) {
            return false;
        } else {
            return !policy.getOwningUser().equals(PageFlowContext.getRemoteUser());
        }
    }

    private boolean claimIsNotAccessibleToRemoteUser(Claim claim) {
        if (CoreContext.getRemoteUser() == null || claim.getOwningUser() == null) {
            return false;
        } else {
            return !claim.getOwningUser().equals(PageFlowContext.getRemoteUser());
        }
    }

    private String getFileName(InputPart part) {
        MultivaluedMap<String, String> headers = part.getHeaders();
        String[] contentDispositionHeader = headers.getFirst("Content-Disposition").split(";");
        for (String name : contentDispositionHeader) {
            if ((name.trim().startsWith("filename"))) {
                String[] split = name.split("=");
                return split[1].trim().replaceAll("\"", "");
            }
        }
        return "";
    }

    private String obfiscatePasswords(String json) {
        if (json == null) {
            return null;
        }

        return json.replaceFirst("[\\\"']password[\\\"']\\s*:\\s*[\\\"'][^\\\"']*[\\\"']","'password' : '********'");
    }
}