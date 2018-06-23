package com.ail.service.product;

import org.springframework.util.StringUtils;

import com.ail.core.Functions;
import com.ail.core.PreconditionException;
import com.ail.core.product.service.ServiceRequestUrl;

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
    public String resolveNamespace() {
        String product = splitUrl[0];
        if (StringUtils.isEmpty(product)) {
            product = splitUrl[1];
        }
        return Functions.productNameToConfigurationNamespace(product);
    }
}
