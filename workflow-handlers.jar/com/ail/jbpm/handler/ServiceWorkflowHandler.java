package com.ail.jbpm.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.jbpm.process.workitem.AbstractWorkItemHandler;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.internal.runtime.StatefulKnowledgeSession;
/**
 * Provides the common functionality to invoke arbitrary services in OU, e.g. policy, pageflow, product services.
 * Implementations are expected to override at least {@link #buildURLPath(WorkItem)} to construct the path portion
 * of the URL to the required service and most likely {@link #getArgument(WorkItem)} to get the argument for the service call.
 *
 */
public abstract class ServiceWorkflowHandler extends AbstractWorkItemHandler {

    public static final String CASE_ID = "case id";
    public static final String CASE_TYPE = "case type";
    public static final String DATA_DIRECTORY_ID = "data directory id";
    public static final String FORMAT = "format";
    public static final String VALUE = "value";
    public static final String PARAMS = "params";
    public static final String PARAMS_ATTRIBUTE_NAME = "Params";
    public static final String RESULTS = "results";
    public static final String SERVICE_NAME = "service name";
    public static final String PRODUCT_NAME = "product name";
    public static final String TEMPLATE_NAME = "template name";
    public static final String DOCUMENT_ID = "document id";
    public static final String USER_TO_CONTACT = "user to contact";
    public static final String SEND_AN_EMAIL = "send an email";
    public static final String SEND_AN_SMS = "send an sms";
    public static final String SEND_TO_PORTAL = "send to portal";
    public static final String SEND_VIA_POST = "send via post";
    public static final String SEND_USING_PREFERRED_METHOD = "send using preferred method";
    public static final String SUBJECT = "subject";
    public static final String MESSAGE = "message";
    public static final String DOCUMENTS_TO_ATTACH = "documents to attach";
    public static final String WORKFLOW_ID = "workflow id";

    public static final String POLICY = "policy";
    public static final String POLICY_ID = "policyId";
    public static final String CLAIM = "claim";
    public static final String CLAIM_ID = "claimId";
    public static final String PARTY = "party";
    public static final String PARTY_ID = "partyId";
    public static final String PRODUCT = "product";

    public ServiceWorkflowHandler(StatefulKnowledgeSession ksession) {
        super(ksession);
    }

    /**
     * Executes a jbpm work item for calling an arbitrary OU service.
     * @param workItem    the jbpm work item
     * @param manager    the jbpm work item manager
     */
    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        Logger.getGlobal().info("work item parameters: " + getParameters(workItem).toString());

        CustomTask customTask = getCustomTask(workItem);

        final Map<String, Object> resultMap = new HashMap<>();
        String url = customTask.getURLPath(workItem);
        if (url != null) {
            JsonNode data = ServiceCaller.getInstance().callService(url, customTask.getArgument(workItem));

            customTask.populateResults(data, resultMap);
        }

        manager.completeWorkItem(workItem.getId(), resultMap);
    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        manager.abortWorkItem(workItem.getId());
    }

    /**
     * Does any initialisation required when the work item is available.
     * @param workItem
     */
    public abstract CustomTask getCustomTask(WorkItem workItem);

    /**
     * Get the parameters to pass to the service call. These can be the default parameters on the work item or this method can be overridden
     * to add more parameters.
     * @param workItem
     * @return  the map of parameters
     */
    public Map<String, Object> getParameters(WorkItem workItem) {
        return workItem.getParameters();
    }

    /**
     * Gets a parameter as a String. Checks for null.
     * @param workItem
     * @param key
     * @return  the parameter as a String, or null
     */
    public String getParameterAsString(WorkItem workItem, String key) {
        Object value = workItem.getParameters().get(key);
        if (value != null) {
            return value.toString();
        }

        return null;
    }

    /**
     * Gets a parameter as boolean. Checks for null.
     * @param workItem
     * @param key
     * @return  the parameter as a boolean. False if null.
     */
    public boolean getParameterAsBoolean(WorkItem workItem, String key) {
        Object value = workItem.getParameters().get(key);
        if (value != null) {
            return Boolean.valueOf(value.toString());
        }

        return false;
    }

    /**
     * Gets a parameter as a List of Strings. Checks for null.
     * @param workItem
     * @param key
     * @return  the parameter as a List of Strings, or null
     */
    public List<String> getParameterAsStringList(WorkItem workItem, String key) {
        Object value = workItem.getParameters().get(key);
        if (value != null) {
            List<String> stringList = new ArrayList<>();
            try {
                List<?> values = (List<?>) value;
                for (Object val : values) {
                    stringList.add(val.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return stringList;
        }

        return null;
    }

    /**
     * Gets the value of a variable in the process a work item us running in
     * @param workItem  the current WorkItem
     * @param variableName  the name of the variable
     * @return  the value of the variable, or null if not found
     */
    public Object getVariable(WorkItem workItem, String variableName) {
        return ((WorkflowProcessInstance) getSession().getProcessInstance(workItem.getProcessInstanceId())).getVariable(variableName);
    }

    /**
     * Constructs a path to call a product service.
     * Uses the workItem to try and get a 'product name' parameter to call a service on a particular product, but if that is not available defaults to the AIL.Base product.
     * @param workItem
     * @param serviceName
     * @return
     */
    public String getProductServiceURLPath(WorkItem workItem, String serviceName) {
        return getProductServiceURLPath(workItem, serviceName, false);
    }

    /**
     * Constructs a path to call a product service.
     * Uses the workItem to try and get a 'product name' parameter to call a service on a particular product, but if that is not available defaults to the AIL.Base product.
     * If useCase is true, also uses the workItem to try and get values for caseType and caseId.
     * If it finds these for a policy, will call a policy service.
     * If it finds these for a claim, will call a claim service.
     * If it finds these for a party will add the party id to the product service as there is no separate party service as yet.
     * @param workItem
     * @param serviceName
     * @param useCase
     * @return
     */
    public String getProductServiceURLPath(WorkItem workItem, String serviceName, boolean useCase) {
        String caseType = getParameterAsString(workItem, CASE_TYPE);
        String caseId = getParameterAsString(workItem, CASE_ID);
        String productName = getParameterAsString(workItem, PRODUCT_NAME);

        StringBuilder urlPath = new StringBuilder(PRODUCT).append("/");

        if (StringUtils.isNotBlank(productName)) {
            urlPath.append(productName);
        } else {
            urlPath.append("AIL.Base");
        }

        if (useCase && StringUtils.isNotBlank(caseId)) {
            if (POLICY.equalsIgnoreCase(caseType)) {
                urlPath = new StringBuilder(POLICY).append("/").append(caseId);
            } else if (CLAIM.equalsIgnoreCase(caseType)) {
                urlPath = new StringBuilder(CLAIM).append("/").append(caseId);
            } else if (PARTY.equalsIgnoreCase(caseType)) {
                urlPath.append("/").append(PARTY).append("/").append(caseId);
            }
        }

        urlPath.append("/").append(serviceName);

        return urlPath.toString();
    }
}
