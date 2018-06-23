package com.ail.ui.client.workflow;

import com.ail.ui.shared.ServiceException;
import com.ail.ui.shared.model.PolicyDetailDTO;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("workflow")
public interface WorkflowService extends RemoteService {

    /**
     * Decline referral for given id
     * @param policySystemId
     */
    void declineReferral(Long policySystemId);
    
    /**
     * Accept referral for given id with given percentage loading
     * @param policySystemId
     * @param loading Percentage loading
     * @return Policy
     */
    PolicyDetailDTO acceptWithLoadingReferral(Long policySystemId, Double loading);

    /**
     * Re-refer referral for given id
     * @param policySystemId
     * @return Policy
     */
    PolicyDetailDTO referReferral(Long policySystemId);

    /**
     * Retrieve policy by system id
     * @param id
     * @return
     * @throws ServiceException
     */
    PolicyDetailDTO policyBySystemId(Long id) throws ServiceException;

}
