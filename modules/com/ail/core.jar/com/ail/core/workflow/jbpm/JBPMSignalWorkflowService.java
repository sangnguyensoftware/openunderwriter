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

import org.apache.commons.codec.CharEncoding;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

import com.ail.annotation.ServiceImplementation;
import com.ail.core.CoreProxy;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.workflow.SignalWorkflowService.SignalWorkflowArgument;
import com.ail.core.workflow.WorkflowHelper;
/**
 * Service implementation to send a signal to a workflow. Firstly invokes a rest service in jBPM to find all processes that contain a variable
 * and optionally a value for that variable, then sends a signal to any active processes returned.
 */
@ServiceImplementation
public class JBPMSignalWorkflowService extends Service<SignalWorkflowArgument> {

    @Override
    public void invoke() {
        if (WorkflowHelper.isExecuteOnChangeField(args.getModelArg(), args.getOnChangeFieldArg())) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        ClientRequest restRequest = null;
                        JBPMRestHelper restHelper = new JBPMRestHelper(args.getPropertiesArg());
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
                                    Number processInstanceId = processInstance.findValue("process-instance-id").getNumberValue();
                                    String externalId = processInstance.findValue("external-id").getTextValue(); // a.k.a. the deployment id

                                    try {
                                        restRequest = restHelper.getProcessRequest(externalId, getSignalProcessPath(processInstanceId.toString()), false);
                                    } catch (URISyntaxException | UnsupportedEncodingException e) {
                                        throw new PreconditionException("Could not create signal process request.", e);
                                    }

                                    response = restRequest.post(String.class);
                                    if (response.getStatus() != 200) {
                                        throw new PostconditionException(response.getStatus() + " response for signal process.");
                                    }
                                }
                            }
                        } catch (Exception e) {
                            throw new PostconditionException("Could not get response for signal workflow.", e);
                        }
                    } catch (Exception e) {
                        new CoreProxy().logError("SignalWorkflowCommand failed.", e);
                    }
                }
            });
            t.start();
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
        String caseId = args.getModelArg().getExternalSystemId();
        String path = "variable/" + args.getCaseTypeArg().getName().toLowerCase() + "Id" + "/value/" + URLEncoder.encode(caseId, CharEncoding.UTF_8);

        return path + "/instances";
    }

    /**
     * Constructs the relative path to the jBPM rest service for signalling a process instance
     * with a {@link SignalWorkflowArgument#getSignalNameArg()} of {@link SignalWorkflowArgument#getSignalValueArg()}
     * @return  the relative path after the base path to the jBPM rest services
     * @throws UnsupportedEncodingException
     */
    private String getSignalProcessPath(String processInstanceId) throws UnsupportedEncodingException {
        String path = "instance/" + processInstanceId + "/signal?signal=";
        path += URLEncoder.encode(args.getSignalNameArg(), CharEncoding.UTF_8);
        if (args.getSignalValueArg() != null) {
            path += "&event=" + URLEncoder.encode(args.getSignalValueArg(), CharEncoding.UTF_8);
        }

        return path;
    }
}