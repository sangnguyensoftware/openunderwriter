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
/**
 * A custom WorkItemHandler for setting data in OU.
 *
 * There should be a number of parameters on the request:
 * The 'case type' must be one of 'policy', 'party', 'claim'.
 * The 'case id' must be the external reference id for the case.
 * The 'data directory id' must be either an xpath reference to the data item from the case, or a reference to a DataDictionary entry.
 * The 'value' must be a valid String value for the 'data directory id'.
 */
public class SetDataHandler extends ServiceWorkflowHandler {

    public SetDataHandler(StatefulKnowledgeSession ksession) {
        super(ksession);
    }

    @Override
    public CustomTask getCustomTask(WorkItem workItem) {
        return new SetData().initialise(workItem);
    }

    public class SetData implements CustomTask {

        @Override
        public CustomTask initialise(WorkItem workItem) {
            return this;
        }

        @Override
        public String getURLPath(WorkItem workItem) {
            return getProductServiceURLPath(workItem, "SetData", true);
        }

        @Override
        public Object getArgument(WorkItem workItem) {
            Argument arg = new Argument();

            arg.caseType = getParameterAsString(workItem, CASE_TYPE);
            arg.caseId = getParameterAsString(workItem, CASE_ID);
            arg.dataDirectoryId = getParameterAsString(workItem, DATA_DIRECTORY_ID);
            arg.value = getParameterAsString(workItem, VALUE);

            return arg;
        }

        @Override
        public void populateResults(JsonNode data, Map<String, Object> resultMap) {
        }

    }

    public class Argument {

        public String caseType;
        public String caseId;
        public String dataDirectoryId;
        public String value;

    }
}