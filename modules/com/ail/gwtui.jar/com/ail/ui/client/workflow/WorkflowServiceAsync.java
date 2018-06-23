package com.ail.ui.client.workflow;

import com.ail.ui.shared.ServiceException;
import com.ail.ui.shared.model.PolicyDetailDTO;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface WorkflowServiceAsync {

    void declineReferral(Long policySystemId, AsyncCallback<Void> callback)
            throws ServiceException;
    
    void referReferral(Long policySystemId, AsyncCallback<PolicyDetailDTO> asyncCallback)
            throws ServiceException;
    
    void acceptWithLoadingReferral(Long policySystemId, Double loading, AsyncCallback<PolicyDetailDTO> asyncCallback)
            throws ServiceException;
    
    void policyBySystemId(Long policySystemId, AsyncCallback<PolicyDetailDTO> callback)
            throws ServiceException;

}
