package com.ail.base.workflow;
/* Copyright Applied Industrial Logic Limited 2014. All rights Reserved */
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
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_OK;

import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientRequestFactory;

import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.CoreProxy;
import com.ail.core.Functions;
import com.ail.core.JSONException;
import com.ail.core.PostconditionException;
import com.ail.core.RestfulServiceInvoker;
import com.ail.core.RestfulServiceReturn;
import com.ail.core.RestfulServiceStatusReturn;
import com.ail.core.product.ProductServiceCommand;
import com.ail.core.workflow.TaskProcessVariablesService.TaskProcessVariablesCommand;
import com.ail.insurance.policy.Policy;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;
import com.ail.pageflow.PageFlowContext;

/**
 * Service that takes a workflow task id and uses that to interrogate the task's
 * process to see which pageflow to return. Also takes a boolean 'content'
 * argument, where it is false this service returns the full path to a pageflow,
 * where it is true this service actually calls the pageflow and returns the
 * JSON response from that pageflow.
 * 
 * This service expects either there to be a pageflowName (and optionally
 * pageName) variable on the task process as well as either policyId OR
 * productID (if both then policyId is preferred and used to retrieve the
 * productId). If no pageflowName variable is present then this service will
 * look to the config in the Registry for the product to determine the default
 * pageflowName for the policy in the group jBPMProperties, attribute
 * jbpm.policy.pageflow.default.
 */
@ProductServiceCommand(serviceName = "PageflowForTaskService", commandName = "PageflowForTask")
public class PageflowForTaskService extends RestfulServiceInvoker {

    public static void invoke(ExecutePageActionArgument args) throws BaseException {
        new PageflowForTaskService().invoke(Argument.class);
    }

    public RestfulServiceReturn service(Argument arg) throws PostconditionException, JSONException {
        CoreProxy coreProxy = PageFlowContext.getCoreProxy();
        coreProxy.logDebug("PageflowForTaskService argument: " + arg);

        Map<String, String> taskVariables = pageflowForTask(arg.taskId, coreProxy);

        String pageflowPath = getPageflowPath(taskVariables);
        if (StringUtils.isEmpty(pageflowPath)) {
            return new ClientError(HTTP_BAD_REQUEST, "pageflowUrl not populated!");
        }

        coreProxy.logDebug("PageflowForTaskService return: " + pageflowPath);

        String content = null;
        if (arg.content) {
            try {
                content = getPageflowContent(pageflowPath);
                writeResponse(content);
                return new RestfulServiceStatusReturn(HTTP_OK);
            } catch (Exception e) {
                throw new PostconditionException("Could not get content for pageflowUrl!", e);
            }
        } else {
            return new Return(HTTP_OK, pageflowPath);
        }
    }

    /**
     * Writes the content directly to the servlet response output stream
     * 
     * @param content
     *            the (JSON) content
     * @throws Exception
     */
    private static void writeResponse(String content) throws Exception {
        PageFlowContext.getResponseWrapper().setContentType(MediaType.APPLICATION_JSON);
        PageFlowContext.getResponseWrapper().getServletResponse().getOutputStream().write(content.getBytes());
    }

    /**
     * Calls the TaskProcessVariablesCommand to get all the process variables for a
     * task.
     * 
     * @param taskId
     * @param coreProxy
     * @return map of the task process variables
     * @throws PostconditionException
     */
    private Map<String, String> pageflowForTask(String taskId, CoreProxy coreProxy) throws PostconditionException {
        try {
            coreProxy.logDebug("Invoking TaskProcessVariablesCommand for task " + taskId);
            TaskProcessVariablesCommand tpvc = (TaskProcessVariablesCommand) coreProxy.newCommand(TaskProcessVariablesCommand.class);
            tpvc.setTaskIdArg(taskId);
            // This will use the AIL.Base Registry jBPMProperties as at this point we have
            // no idea of the Policy or Product.
            // The task query REST call only needs the protocol, host, and port bit though
            // so should be ok.
            // If not then the AIL.Base Registry needs to be set to the correct values for
            // this project.
            tpvc.setPropertiesArg(coreProxy.getGroup("jBPMProperties").getParameterAsProperties());
            tpvc.invoke();
            coreProxy.logDebug("Finished invoking TaskProcessVariablesCommand for task " + taskId);
            return tpvc.getVariablesRet();
        } catch (Exception e) {
            coreProxy.logError("TaskProcessVariablesCommand failed for task " + taskId, e);
        }

        return null;
    }

    /**
     * Get the full path to the pageflow using the task process variables. There may
     * or may not be a default pageflow and page name set up in the Registry
     * configuration for this policy.
     * 
     * @param variables
     * @return the full path to the pageflow
     * @throws PostconditionException
     */
    private String getPageflowPath(Map<String, String> variables) throws PostconditionException {
        Object pageflowName = variables.get("pageflowName");
        Object productId = variables.get("productId");
        Object policyId = variables.get("policyId");
        Object pageName = variables.get("pageName");

        if (productId == null && policyId == null) {
            throw new PostconditionException("Not enough data to build pageflow path: productId = " + productId + ", policyId = " + policyId);
        }

        if (pageflowName == null) {
            // if we have no pageflow name we want to try and find the default one, if it
            // exists in the config
            if (policyId != null) {
                // If we have a policyId then we want to get its productId.
                Policy policy = (Policy) CoreContext.getCoreProxy().queryUnique("get.policy.by.externalSystemId", policyId);
                if (policy != null) {
                    productId = policy.getProductTypeId();
                }
            }
            CoreProxy coreProxy = new CoreProxy(Functions.productNameToConfigurationNamespace((productId.toString())));
            Properties productJBPMProperties = coreProxy.getGroup("jBPMProperties").getParameterAsProperties();
            pageflowName = productJBPMProperties.getProperty("jbpm.policy.pageflow.default");
            if (pageName == null) {
                pageName = productJBPMProperties.getProperty("jbpm.policy.page.default");
            }
        }

        if (pageflowName == null) {
            throw new PostconditionException("Not enough data to build pageflow path: pageflowName = " + pageflowName);
        }

        String path = getBasePath(PageFlowContext.getRequestWrapper().getServletRequest()) + "/pageflow/" + pageflowName;

        // prefer policyId to productId in the path, especially since we will have
        // retrieved the productId if we have a policyId
        if (policyId != null) {
            path += "/policy/" + policyId;
        } else if (productId != null) {
            path += "/product/" + productId;
        }

        if (pageName != null) {
            path += "/page/" + pageName;
        }

        return path;
    }

    /**
     * Get the JSON content for the pageflow
     * 
     * @param pageflowPath
     * @return
     * @throws Exception
     */
    private String getPageflowContent(String pageflowPath) throws Exception {
        HttpServletRequest request = PageFlowContext.getRequestWrapper().getServletRequest();
        ClientRequestFactory factory = new ClientRequestFactory();
        ClientRequest clientRequest = factory.createRequest(pageflowPath).accept(MediaType.APPLICATION_JSON).header(HttpHeaders.AUTHORIZATION,
                request.getHeader(HttpHeaders.AUTHORIZATION));
        return (String) clientRequest.get(String.class).getEntity();
    }

    /**
     * Get the base path to OU
     * 
     * @param request
     * @return
     */
    private String getBasePath(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
    }

    public static class Argument {

        String taskId;
        boolean content;

    }

    public static class Return extends RestfulServiceReturn {

        String action;

        public Return(int status, String action) {
            super(status);
            this.action = action;
        }
    }
}
