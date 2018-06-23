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

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

import com.ail.annotation.ServiceImplementation;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.workflow.TaskProcessVariablesService.TaskProcessVariablesArgument;
/**
 * Service implementation to retrieve all the variables from the Process that started a Task. Invokes a rest service in jBPM to retrieve the details for a task,
 * gets the process id from that, then invokes another rest service in jBPM to retrieve the variables for that process and return them.
 */
@ServiceImplementation
public class JBPMTaskProcessVariablesService extends Service<TaskProcessVariablesArgument> {

    @Override
    public void invoke() throws PreconditionException, PostconditionException {
        ClientRequest restRequest = null;
        JBPMRestHelper restHelper = new JBPMRestHelper(args.getPropertiesArg());
        try {
            restRequest = restHelper.getTaskRequest(getTaskPath());
        } catch (URISyntaxException e) {
            throw new PreconditionException("Could not create task query request.", e);
        }

        try {
            ClientResponse<String> response = restRequest.get(String.class);
            if (response.getStatus() != 200) {
                throw new PostconditionException(response.getStatus() + " response for task query.");
            }
            ObjectMapper mapper = new ObjectMapper();
            JsonNode taskSummary = mapper.readTree(response.getEntity()).path("taskSummaryList");
            String processInstanceId = taskSummary.findValue("process-instance-id").asText();
            String deploymentId = taskSummary.findValue("deployment-id").asText();
            try {
                restRequest = restHelper.getProcessRequest(deploymentId, getProcessInstancePath(processInstanceId), true);
            } catch (URISyntaxException e) {
                throw new PreconditionException("Could not create signal process request.", e);
            }

            response = restRequest.get(String.class);
            if (response.getStatus() != 200) {
                throw new PostconditionException(response.getStatus() + " response for process instance query.");
            }
            JsonNode process = mapper.readTree(response.getEntity()).path("variables");
            Map<String, String> returnMap = new HashMap<>();
            for (Iterator<String> fieldNames = process.getFieldNames(); fieldNames.hasNext(); ) {
                String fieldName = fieldNames.next();
                returnMap.put(fieldName, process.findValue(fieldName).asText());
            }
            args.setVariablesRet(returnMap);
        } catch (Exception e) {
            throw new PostconditionException("Could not get response for get variables for task process.", e);
        }
    }

    /**
     * Constructs the relative path to the jBPM rest service for getting the details of a task with id {@link TaskProcessVariablesArgument#getTaskIdArg()}
     * @return  the relative path after the base path to the jBPM rest services
     */
    private String getTaskPath() {
        return "query?taskId=" + args.getTaskIdArg();
    }

    /**
     * Constructs the relative path to the jBPM rest service for getting the task process instance
     * @param   processInstanceId   the id of the process instance
     * @return  the relative path after the base path to the jBPM rest services
     */
    private String getProcessInstancePath(String processInstanceId) {
        return "instance/" + processInstanceId;
    }
}