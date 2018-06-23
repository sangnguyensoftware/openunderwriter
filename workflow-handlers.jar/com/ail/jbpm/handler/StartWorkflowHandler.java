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
package com.ail.jbpm.handler;

import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.kie.api.runtime.process.WorkItem;
import org.kie.internal.runtime.StatefulKnowledgeSession;

import com.ail.jbpm.handler.StartWorkflowHandler.Argument.Attribute;

/**
 * A custom WorkItemHandler for calling other workflows.
 *
 * There should be a number of parameters on the request:
 * The 'product name' is required only where there is an overridden StartWorkflow service for the product.
 * The 'workflow id' is the full id of the workflow to start, including package name.
 * The 'params' is optional and is a String of params in URL query param format, i.e. 'name1=value1&name2=value2' etc.",
 */
public class StartWorkflowHandler extends ServiceWorkflowHandler {

    public StartWorkflowHandler(StatefulKnowledgeSession ksession) {
        super(ksession);
    }

    @Override
    public CustomTask getCustomTask(WorkItem workItem) {
        return new StartWorkflow().initialise(workItem);
    }

    public class StartWorkflow implements CustomTask {

        @Override
        public CustomTask initialise(WorkItem workItem) {
            return this;
        }

        @Override
        public String getURLPath(WorkItem workItem) {
            return getProductServiceURLPath(workItem, "StartWorkflow");
        }

        @Override
        public Object getArgument(WorkItem workItem) {
            Argument arg = new Argument();
            arg.workflowId = getParameterAsString(workItem, WORKFLOW_ID);

            Object params = workItem.getParameter(PARAMS);
            if (params != null) {
                arg.attributes = new Attribute[1];
                arg.attributes[0] = arg.new Attribute(PARAMS_ATTRIBUTE_NAME, params.toString());
            }

            return arg;
        }

        @Override
        public void populateResults(JsonNode data, Map<String, Object> resultMap) {
        }

    }

    public class Argument {

        public String workflowId;
        public Attribute[] attributes;

        public class Attribute {
            public String name;
            public String value;

            public Attribute(String name, String value) {
                super();
                this.name = name;
                this.value = value;
            }
        }
    }
}