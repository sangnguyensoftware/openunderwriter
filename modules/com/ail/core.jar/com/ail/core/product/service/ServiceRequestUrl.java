package com.ail.core.product.service;

import com.ail.core.PreconditionException;

public interface ServiceRequestUrl {

    /**
     * Return last part of URL
     * @param splitUrl
     * @return
     */
    String resolveServiceName() throws PreconditionException;;

    /**
     * Return first part of URL
     * @param splitUrl
     * @return
     * @throws PreconditionException
     */
    String resolveNamespace() throws PreconditionException;
}
