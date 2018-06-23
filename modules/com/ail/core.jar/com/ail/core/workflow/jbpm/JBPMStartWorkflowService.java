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
package com.ail.core.workflow.jbpm;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.codec.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

import com.ail.annotation.ServiceImplementation;
import com.ail.core.Attribute;
import com.ail.core.CoreProxy;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.workflow.SignalWorkflowService.SignalWorkflowArgument;
import com.ail.core.workflow.StartWorkflowService.StartWorkflowArgument;
import com.ail.core.workflow.WorkflowHelper;

/**
 * Service implementation to start a workflow. Invokes a rest service in jBPM and checks only for a successful 200
 * response, does not read any other data.
 */
@ServiceImplementation
public class JBPMStartWorkflowService extends Service<StartWorkflowArgument> {

    @Override
    public void invoke() throws PreconditionException {
        if (WorkflowHelper.isExecuteOnChangeField(args.getModelArg(), args.getOnChangeFieldArg())) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JBPMRestHelper restHelper = new JBPMRestHelper(args.getPropertiesArg());

                        if (args.getAbortExistingArg()) {
                            abortExisting(restHelper);
                        }

                        ClientRequest restRequest = null;
                        try {
                            restRequest = restHelper.getProcessRequest(getAppropriateDeploymentId(restHelper), getPath(), false);
                        } catch (URISyntaxException | UnsupportedEncodingException e) {
                            throw new PreconditionException("Could not create start workflow request.", e);
                        }

                        ClientResponse<String> response = restRequest.post(String.class);
                        if (response.getStatus() != 200) {
                            throw new PostconditionException(response.getStatus() + " response for start workflow.");
                        }
                    } catch (Exception e) {
                        new CoreProxy().logError("StartWorkflowCommand failed.", e);
                    }
                }
            });
            t.start();
        }
    }

    private String getAppropriateDeploymentId(JBPMRestHelper restHelper) throws PreconditionException, PostconditionException {
        String deploymentIdForProcess = null;

        for (String deploymentId : restHelper.getJbpmProperties().getDeploymentIds()) {
            try {
                ClientRequest restRequest = restHelper.getProcessRequest(deploymentId, getProcessStartformPath(), false);
                ClientResponse<String> response = restRequest.post(String.class);
                if (response.getStatus() == 200) {
                    deploymentIdForProcess = deploymentId;
                } else if (response.getStatus() == 404) {
                    new CoreProxy().logInfo("Process " + args.getWorkflowIdArg() + " not available for deploymentId " + deploymentId);
                }
            } catch (Exception e) {
                throw new PreconditionException("Could not create or execute process startform request.", e);
            }

        }

        return deploymentIdForProcess;
    }

    private String getProcessStartformPath() {
        return args.getWorkflowIdArg() + "/startform";
    }

    /**
     * Looks for any existing workflow process instances that have the same caseType/caseId and the same id/name as the
     * workflow being started and aborts them.
     * @param restHelper
     * @throws PreconditionException
     * @throws PostconditionException
     */
    private void abortExisting(JBPMRestHelper restHelper) throws PreconditionException, PostconditionException {
        ClientRequest restRequest = null;

        try {
            restRequest = restHelper.getHistoryRequest(getHistoryProcessesPath());
        } catch (URISyntaxException | UnsupportedEncodingException e) {
            throw new PreconditionException("Could not create history processes request.", e);
        }

        try {
            ClientResponse<String> response = restRequest.get(String.class);
            if (response.getStatus() != 200) {
                throw new PostconditionException(response.getStatus() + " response for history processes.");
            }
            ObjectMapper mapper = new ObjectMapper();
            JsonNode historyLogList = mapper.readTree(response.getEntity()).path("historyLogList");
            Iterator<JsonNode> processInstancesIterator = historyLogList.getElements();
            while (processInstancesIterator.hasNext()) {
                JsonNode processInstance = processInstancesIterator.next();
                int processStatus = processInstance.findValue("status").getIntValue();
                if (processStatus == 1) { // 1 is Active processes, the only ones we are interested in
                    String processId = processInstance.findValue("process-id").getTextValue();
                    if (args.getWorkflowIdArg().equals(processId)) {
                        Number processInstanceId = processInstance.findValue("process-instance-id").getNumberValue();
                        String externalId = processInstance.findValue("external-id").getTextValue(); // a.k.a. the deployment id

                        try {
                            restRequest = restHelper.getProcessRequest(externalId, getAbortProcessPath(processInstanceId.toString()), false);
                        } catch (URISyntaxException | UnsupportedEncodingException e) {
                            throw new PreconditionException("Could not create abort process request.", e);
                        }

                        response = restRequest.post(String.class);
                        if (response.getStatus() != 200) {
                            throw new PostconditionException(response.getStatus() + " response for abort process.");
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new PostconditionException("Could not get response for signal workflow.", e);
        }
    }

    /**
     * Constructs the relative path to the jBPM rest service for getting the history process instances that
     * contain a variable named {@link SignalWorkflowArgument#getCaseTypeArg()}Id with the value
     * of {@link SignalWorkflowArgument#getModelArg()#getExternalSystemId()}
     * @return  the relative path after the base path to the jBPM rest services
     * @throws UnsupportedEncodingException
     */
    private String getHistoryProcessesPath() throws UnsupportedEncodingException {
        String path = "";

        if (args.getCaseTypeArg() != null) {
            String caseId = args.getModelArg().getExternalSystemId();
            path += "variable/" + args.getCaseTypeArg().getName().toLowerCase() + "Id" + "/value/" + URLEncoder.encode(caseId, CharEncoding.UTF_8) + "/";
        }

        return path + "instances";
    }

    /**
     * Constructs the relative path to the jBPM rest service for aborting a process instance
     * @return  the relative path after the base path to the jBPM rest services
     * @throws UnsupportedEncodingException
     */
    private String getAbortProcessPath(String processInstanceId) throws UnsupportedEncodingException {
        String path = "instance/" + processInstanceId + "/abort";

        return path;
    }

    /**
     * Get the path to the rest call to start a workflow. May pass a caseId and/or a productId in the workflow map parameter.
     * @return
     * @throws UnsupportedEncodingException
     */
    private String getPath() throws UnsupportedEncodingException {
        String path = args.getWorkflowIdArg() + "/start";

        String pathArgs = "" ;

        if (args.getCaseTypeArg() != null) {
            String caseId = args.getModelArg().getExternalSystemId();
            String typeId = args.getCaseTypeArg().getName().toLowerCase() + "Id";
            pathArgs += "map_" + typeId + "=" + URLEncoder.encode(caseId, CharEncoding.UTF_8);
        }

        if (StringUtils.isNotBlank(args.getProductIdArg())) {
            if (!pathArgs.isEmpty()) {
                pathArgs += "&";
            }
            pathArgs += "map_productId=" + URLEncoder.encode(args.getProductIdArg(), CharEncoding.UTF_8);
        }

        List<Attribute> attributes = args.getAttributeArg();
        if (attributes != null) {
            for (Attribute att : attributes) {
                // need to avoid duplicate parameters
                if (!pathArgs.contains("map_" + att.getId())) {
                    String value = att.getValue();

                    if (!pathArgs.isEmpty()) {
                        pathArgs += "&";
                    }
                    pathArgs += "map_" + att.getId() + "=" + URLEncoder.encode(value, CharEncoding.UTF_8);
                }
            }
        }

        return path + (StringUtils.isNotBlank(pathArgs) ? "?" + pathArgs : "");
    }
}