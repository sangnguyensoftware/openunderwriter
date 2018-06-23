package com.ail.base;
/* Copyright Applied Industrial Logic Limited 2007. All rights reserved. */
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
import java.util.Calendar;
import java.util.Date;

import com.ail.pageflow.PageFlowContext;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;
import com.ail.core.product.ProductServiceCommand;
import com.ail.insurance.policy.Policy;

/**
 * Initialise a quotation in preparation for a new business quotation. This
 * service is called at the beginning of a product's quotation page flow. At
 * this point, the quotation will already have been created and setup with
 * respect to its type definition. However, type definitions are static and
 * therefore cannot fill in variable data like today's date. This service
 * provides a convenient place to carry out such initialisations.
 */
@ProductServiceCommand(serviceName = "InitialiseNewBusinessService", commandName = "InitialiseNewBusiness")
public class InitialiseNewBusinessService {
    /**
     * Service entry point.
     * 
     * @param args
     *            Contains the quotation object available for initialisation.
     */
    public static void invoke(ExecutePageActionArgument args) {
        Calendar date = null;
        Policy policy = (Policy) args.getModelArgRet();

        /* Set the policy and expiry dates to today */
        policy.setQuotationDate(new Date());
        policy.setInceptionDate(new Date());

        /* Set the policy's expiry date to yesterday + 1 year */
        date = Calendar.getInstance();
        date.add(Calendar.DATE, -1);
        date.add(Calendar.YEAR, 1);
        policy.setExpiryDate(date.getTime());

        /* Set the policy expiry date to today + 30 days */
        date = Calendar.getInstance();
        date.add(Calendar.DATE, 30);
        policy.setQuotationExpiryDate(date.getTime());

        /* Give the current user ownership - null is okay if the user is a guest */
        policy.setOwningUser(PageFlowContext.getRemoteUser());
    }
}