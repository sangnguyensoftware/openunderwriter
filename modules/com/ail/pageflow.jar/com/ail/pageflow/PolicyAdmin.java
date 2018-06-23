/* Copyright Applied Industrial Logic Limited 2016. All rights Reserved */
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
package com.ail.pageflow;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import com.ail.core.Attribute;
import com.ail.core.BaseException;
import com.ail.core.Type;
import com.ail.insurance.policy.Policy;

public class PolicyAdmin extends PageContainer {

    private static final long serialVersionUID = -4810598045554021748L;

    private static final String DATE_FIELD_FORMAT = "yyyy-MM-dd";

    /**
     * Default constructor
     */
    public PolicyAdmin() {
        super();
    }

	@Override
	public Type renderResponse(Type model) throws IllegalStateException, IOException {
	    return executeTemplateCommand("PolicyAdmin", model);
	}

	@Override
    public Type processActions(Type model) throws BaseException {
        Properties params = PageFlowContext.getOperationParameters();

        if (params != null) {
            String op = PageFlowContext.getRequestedOperation();

            if ("Save".equals(op)) {

                Policy policy = PageFlowContext.getPolicy();

                Attribute validationAttribute = policy.getAttributeById("validation.error");
                policy.removeAttribute(validationAttribute);

                Map<String, String[]> updateParams = PageFlowContext.getRequestWrapper().getParameterMap();

                String[] inceptionDateParams = updateParams.get("inceptionDate");
                if (inceptionDateParams != null && inceptionDateParams.length != 0) {
                    String inceptionDateParam = inceptionDateParams[0];
                    try {
                        if (StringUtils.isNotBlank(inceptionDateParam)) {
                            Date  inceptionDate = new SimpleDateFormat(DATE_FIELD_FORMAT).parse(inceptionDateParam);
                            policy.setInceptionDate(inceptionDate);
                        }
                    } catch (ParseException e) {
                        policy.addAttribute(new Attribute("validation.error", "Incorrect format for Inception date: " + inceptionDateParam, "string"));
                    }
                }

                String[] expiryDateParams = updateParams.get("expiryDate");
                if (expiryDateParams != null && expiryDateParams.length != 0) {
                    String expiryDateParam = expiryDateParams[0];
                    try {
                        if (StringUtils.isNotBlank(expiryDateParam)) {
                            Date  expiryDate = new SimpleDateFormat(DATE_FIELD_FORMAT).parse(expiryDateParam);
                            policy.setExpiryDate(expiryDate);
                        }
                    } catch (ParseException e) {
                        policy.addAttribute(new Attribute("validation.error", "Incorrect format for Expiry date: " + expiryDateParam, "string"));
                    }
                }

                PageFlowContext.flagActionAsProcessed();
            }
        }

        return model;
    }
}
