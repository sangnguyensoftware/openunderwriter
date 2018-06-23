package com.ail.service.claim;

import org.springframework.util.StringUtils;

import com.ail.core.CoreProxy;
import com.ail.core.Functions;
import com.ail.core.PreconditionException;
import com.ail.core.product.service.ServiceRequestUrl;
import com.ail.insurance.claim.Claim;

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
        String claimId = splitUrl[0];

        if (StringUtils.isEmpty(claimId)) {
            claimId = splitUrl[1];
        }

        Claim p = (Claim)new CoreProxy().queryUnique("get.claim.by.externalSystemId", claimId);

        if (p==null) {
            throw new PreconditionException("Claim (externalId="+claimId+") could not be found.");
        }

        return Functions.productNameToConfigurationNamespace(p.getPolicy().getProductTypeId());
    }
}
