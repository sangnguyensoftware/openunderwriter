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
 * A custom WorkItemHandler for calling the OU GetDocument service.
 *
 * There should also be a number of parameters on the request in a Map:
 * The 'case type' must be one of 'policy', 'party', 'claim'.
 * The 'case id' must be the external reference id for the case.
 * The 'product name' is required either where 'case type' is Party (as it can not be derived from Party), or where there is an overridden GenerateDocument service for the product.
 * The 'template name' will be the template name appropriate for the particular context.
 * The 'document id' will be the unique external reference id of the document.
 */
public class GenerateDocumentHandler extends ServiceWorkflowHandler {

    public GenerateDocumentHandler(StatefulKnowledgeSession ksession) {
        super(ksession);
    }

    @Override
    public CustomTask getCustomTask(WorkItem workItem) {
        return new GenerateDocument().initialise(workItem);
    }

    public class GenerateDocument implements CustomTask {

        @Override
        public CustomTask initialise(WorkItem workItem) {
            return this;
        }

        @Override
        public String getURLPath(WorkItem workItem) {
            return getProductServiceURLPath(workItem, "GetDocument", true);
        }

        @Override
        public Object getArgument(WorkItem workItem) {
            Argument arg = new Argument();

            arg.caseType = getParameterAsString(workItem, CASE_TYPE);
            arg.caseId = getParameterAsString(workItem, CASE_ID);
            arg.templateName = getParameterAsString(workItem, TEMPLATE_NAME);

            return arg;
        }

        @Override
        public void populateResults(JsonNode data, Map<String, Object> resultMap) {
            if (data != null && data.path("documentId") != null) {
                // Check if there is actually a doc generated.
                resultMap.put(DOCUMENT_ID, data.path("documentId").getTextValue());
            }
        }
    }

    public class Argument {

        public String caseType;
        public String caseId;
        public String templateName;

    }
}