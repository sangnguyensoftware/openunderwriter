package com.ail.jbpm.handler;

import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.kie.api.runtime.process.WorkItem;

public interface CustomTask {

    /**
     * Does any initialisation required when the work item is available.
     * @param workItem
     */
    public CustomTask initialise(WorkItem workItem);

    /**
     * This is expected to return the path portion of the URL to a service in OU
     * @return  a URL path to a service in OU
     */
    public String getURLPath(WorkItem workItem);

    /**
     * Get the argument to pass to the service call. This can be simply the default parameters on the work item or this method can be overridden
     * to add any other object.
     * @param workItem
     * @return  the map of parameters
     */
    public Object getArgument(WorkItem workItem);

    /**
     * This is expected to populate the results of a service call to a resultsMap which will be returned to a workflow when a custom task completes
     * @param data
     * @param resultMap
     */
    public void populateResults(JsonNode data, Map<String, Object> resultMap);
}
