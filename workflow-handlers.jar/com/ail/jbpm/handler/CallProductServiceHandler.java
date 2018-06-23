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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.jackson.JsonNode;
import org.kie.api.runtime.process.WorkItem;
import org.kie.internal.runtime.StatefulKnowledgeSession;

/**
 * A custom WorkItemHandler for calling the OU product service.
 *
 * There should be a number of parameters on the request:
 * The 'case type' must be one of 'policy', 'party', 'claim'. If none of these then a default product service will be called.
 * The 'case id' must be the external reference id for the case. If empty then a default product service will be called.
 * The 'product name' is required where 'case type' is Party (as it can not be derived from Party). If empty then a default product service will be called.
 * The 'service name' is the name of the service.
 * The 'params' is optional and is a String of params in URL query param format, i.e. 'name1=value1&name2=value2'. They will be supplied to the product service in json format.
 * May also return a String of 'results' in URL query param form.
 */
public class CallProductServiceHandler extends ServiceWorkflowHandler {

    public CallProductServiceHandler(StatefulKnowledgeSession ksession) {
        super(ksession);
    }

    @Override
    public CustomTask getCustomTask(WorkItem workItem) {
        return new CallProductService().initialise(workItem);
    }

    public class CallProductService implements CustomTask {

        @Override
        public CustomTask initialise(WorkItem workItem) {
            return this;
        }

        @Override
        public String getURLPath(WorkItem workItem) {
            return getProductServiceURLPath(workItem, (String) workItem.getParameter(SERVICE_NAME), true);
        }

        @Override
        public Object getArgument(WorkItem workItem) {
            String params = (String) workItem.getParameter(PARAMS);
            if (params != null) {
                Map<String, String> args = new HashMap<>();
                // This will be a String of key=value&key=value... params. Convert them into a Map so that they will be passed to the product service as json args
                String [] keyValues = params.split("&");
                for (String keyValue : keyValues) {
                    if (keyValue.contains("=")) {
                        String[] splitKeyValue = keyValue.split("=");
                        args.put(splitKeyValue[0], splitKeyValue[1]);
                    }
                }
                return args;
            }

            return null;
        }

        @Override
        public void populateResults(JsonNode data, Map<String, Object> resultMap) {
            if (data != null) {
                StringBuilder results = new StringBuilder();
                for (Iterator<Entry<String, JsonNode>> i = data.getFields(); i.hasNext(); ) {
                    Entry<String, JsonNode> entry = i.next();
                    results.append("&").append(entry.getKey()).append("=").append(entry.getValue().asText());
                }
                resultMap.put(RESULTS, results.toString());
            }
        }

    }
}