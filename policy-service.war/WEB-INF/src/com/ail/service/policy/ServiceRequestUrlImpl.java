package com.ail.service.policy;

import org.springframework.util.StringUtils;

import com.ail.core.CoreProxy;
import com.ail.core.Functions;
import com.ail.core.PreconditionException;
import com.ail.core.product.service.ServiceRequestUrl;
import com.ail.insurance.policy.Policy;

public class ServiceRequestUrlImpl implements ServiceRequestUrl {

    private String[] splitUrl;

    ServiceRequestUrlImpl(String url) throws PreconditionException {
        if (url == null) {
            throw new PreconditionException("Request url is null");
        }

        splitUrl = url.split("/");

        if (splitUrl.length < 3) {
            throw new PreconditionException("Request url is malformed");
        }
    }

    @Override
    public String resolveServiceName() {
        return splitUrl[splitUrl.length - 1];
    }

    @Override
    public String resolveNamespace() throws PreconditionException {
        String policyId = splitUrl[0];

        if (StringUtils.isEmpty(policyId)) {
            policyId = splitUrl[1];
        }

        Policy p = (Policy)new CoreProxy().queryUnique("get.policy.by.externalSystemId", policyId);

        if (p==null) {
            throw new PreconditionException("Policy (externalId="+policyId+") could not be found.");
        }

        return Functions.productNameToConfigurationNamespace(p.getProductTypeId());
    }
}
