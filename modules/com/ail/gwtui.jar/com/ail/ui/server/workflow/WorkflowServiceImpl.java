package com.ail.ui.server.workflow;

import java.util.HashMap;
import java.util.Map;

import com.ail.core.CoreProxy;
import com.ail.core.transformer.TransformerException;
import com.ail.insurance.policy.Policy;
import com.ail.insurance.quotation.PolicyManualOverrideService;
import com.ail.insurance.quotation.PolicyManualOverrideService.PolicyManualOverrideCommand;
import com.ail.ui.client.workflow.WorkflowService;
import com.ail.ui.server.transformer.PolicyDetailTransformer;
import com.ail.ui.shared.ServiceException;
import com.ail.ui.shared.model.PolicyDetailDTO;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class WorkflowServiceImpl extends RemoteServiceServlet implements WorkflowService {

    @Override
    public void declineReferral(final Long policySystemId) {

        doOperation(policySystemId, PolicyManualOverrideService.DECLINE_OPERATION);

    }

    @Override
    public PolicyDetailDTO referReferral(final Long policySystemId) {


        doOperation(policySystemId,
                PolicyManualOverrideService.RECALCULATE_OPERATION,
                getLoadingParameter(policySystemId, Double.valueOf("0")));

        doOperation(policySystemId,
                PolicyManualOverrideService.REFER_OPERATION);

        return policyBySystemId(policySystemId);
    }

    @Override
    public PolicyDetailDTO acceptWithLoadingReferral(final Long policySystemId, Double loading) {

        doOperation(policySystemId,
                PolicyManualOverrideService.ONRISK_OPERATION,
                getLoadingParameter(policySystemId, loading));

        return policyBySystemId(policySystemId);

    }

    private Map<String, String[]> getLoadingParameter(final Long policySystemId, Double loading) {
        Map<String, String[]> paramaterMap = new HashMap<String, String[]>();
        paramaterMap.put(
                PolicyManualOverrideService.MANUAL_PCT_LOADING_PARAM_NAME + ":0:" + policySystemId,
                        new String[] { String.valueOf(loading) });
        return paramaterMap;
    }

    private void doOperation(final Long policySystemId, String operation) {
        doOperation(policySystemId, operation, new HashMap<String, String[]>());
    }

    private void doOperation(final Long policySystemId, String operation, Map<String, String[]> parameterMap) {
        try {
            CoreProxy core = new CoreProxy();

            Policy policy = (Policy) core.queryUnique("get.policy.by.systemId", policySystemId);

            if (PolicyManualOverrideService.REFER_OPERATION.equals(operation)) {
                policy.setPolicyNumber(null);
            }

            PolicyManualOverrideCommand pmoc = core.newCommand(PolicyManualOverrideCommand.class);
            pmoc.setPolicyArg(policy);
            pmoc.setParameterMapArg(parameterMap);
            pmoc.setOperationArg(operation);
            pmoc.invoke();

            core.update(policy);

        } catch (Throwable e) {
            throw new ServiceException(e);
        }
    }

    @Override
    public PolicyDetailDTO policyBySystemId(Long id) throws ServiceException {

        CoreProxy core = new CoreProxy();
        Policy policy = (Policy) core.queryUnique("get.policy.by.systemId", id);

        if (policy != null) {
            try {
                return new PolicyDetailTransformer().apply(policy);
            } catch (TransformerException e) {
                core.logError(this.getClass().getName(), e);
                throw new ServiceException(e);
            }
        }

        return new PolicyDetailDTO();
    }

}
