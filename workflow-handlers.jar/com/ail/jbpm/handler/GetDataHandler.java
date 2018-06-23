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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.codehaus.jackson.JsonNode;
import org.kie.api.runtime.process.WorkItem;
import org.kie.internal.runtime.StatefulKnowledgeSession;
/**
 * A custom WorkItemHandler for getting data from OU.
 *
 * There should be a number of parameters on the request:
 * The 'case type' must be one of 'policy', 'party', 'claim'.
 * The 'case id' must be the external reference id for the case.
 * The 'data directory id' must be either an xpath reference to the data item from the case, or a reference to a DataDictionary entry.
 * The 'format' is optional but if supplied must be a valid FormatType - currently only TIMER is supported to return a valid workflow timer task format.
 *
 * It will return a 'value' to the workflow - a valid String value for the 'data directory id'.
 */
public class GetDataHandler extends ServiceWorkflowHandler {

    public GetDataHandler(StatefulKnowledgeSession ksession) {
        super(ksession);
    }

    @Override
    public CustomTask getCustomTask(WorkItem workItem) {
        return new GetData().initialise(workItem);
    }

    public class GetData implements CustomTask {

        private String caseType;
        private String caseId;
        private String dataDirectoryId;
        private String format;
        private String urlPath;

        @Override
        public CustomTask initialise(WorkItem workItem) {
            caseType = getParameterAsString(workItem, CASE_TYPE);
            caseId = getParameterAsString(workItem, CASE_ID);
            dataDirectoryId = getParameterAsString(workItem, DATA_DIRECTORY_ID);
            format = getParameterAsString(workItem, FORMAT);

            return this;
        }

        @Override
        public String getURLPath(WorkItem workItem) {
            urlPath = getProductServiceURLPath(workItem, "GetData", true);

            return urlPath;
        }

        @Override
        public Object getArgument(WorkItem workItem) {
            Argument arg = new Argument();

            arg.caseType = this.caseType;
            arg.caseId = this.caseId;
            arg.dataDirectoryId = this.dataDirectoryId;
            arg.format = this.format;

            return arg;
        }

        @Override
        public void populateResults(JsonNode data, Map<String, Object> resultMap) {
            String value = "";
            if (data != null && data.path("data") != null) {
                value = data.path("data").getTextValue();
            } else {
                Logger.getGlobal().log(Level.WARNING, "Failed to get data for " + caseType + ":" + caseId + ":" + dataDirectoryId + ":" + format +
                                                        " from " + ServiceCaller.getInstance().getBaseURL() + urlPath);
            }
            resultMap.put(VALUE, value);
        }

    }

    public class Argument {

        public String caseType;
        public String caseId;
        public String dataDirectoryId;
        public String format;

    }
}