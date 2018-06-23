package com.ail.base.workflow;
/* Copyright Applied Industrial Logic Limited 2014. All rights Reserved */
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
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_OK;

import org.apache.commons.lang.StringUtils;

import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.JSONException;
import com.ail.core.RestfulServiceInvoker;
import com.ail.core.RestfulServiceReturn;
import com.ail.core.TypeXPathException;
import com.ail.core.product.ProductServiceCommand;
import com.ail.insurance.policy.Policy;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;
import com.ail.pageflow.PageFlowContext;

/**
 * A service to take in an array of Strings that contain a policy id and any
 * number of other variables for expansion which returns an array of Strings
 * with the policy id removed and the variables expanded into their actual
 * values. The format will follow the following standard: {policyId:POL1234}
 * This is a comment for the task for policy ${d:policyNumber} of status
 * ${d:status} where policyId:POL1234 indicates the policy to be located, and
 * the ${d:policyNumber} is where in the text the policyNumber is to be
 * displayed. {policyId:POL1234} should not be output, and neither should
 * ${d:policyNumber} which will be replaced with the actual policy number, if
 * found, else blank. If the variable cannot be substituted it will be set to
 * blank.
 */
@ProductServiceCommand(serviceName = "VariableExpansionService", commandName = "VariableExpansion")
public class VariableExpansionService extends RestfulServiceInvoker {

    private static final String policyIdPrefix = "{policyId:";
    private static final String policyIdSuffix = "}";
    private static final String varPrefix = "${d:";
    private static final String varSuffix = "}";

    public static void invoke(ExecutePageActionArgument args) throws BaseException {
        new VariableExpansionService().invoke(Argument.class);
    }

    public RestfulServiceReturn service(Argument arg) throws JSONException {
        if (arg.variableStrings == null) {
            return new ClientError(HTTP_BAD_REQUEST, "variableStrings not populated!");
        }
        String[] expandedStrings = expandVariables(arg.variableStrings);

        return new Return(HTTP_OK, expandedStrings);
    }

    private String[] expandVariables(String[] variableStrings) {
        String[] expandedStrings = new String[variableStrings.length];

        for (int i = 0; i < variableStrings.length; i++) {
            expandedStrings[i] = expandVariable(variableStrings[i]);
        }

        return expandedStrings;
    }

    private String expandVariable(String variableString) {
        String expandedString = "";

        String policyId = StringUtils.substringBetween(variableString, "{policyId:", "}");
        if (StringUtils.isNotEmpty(policyId)) {
            Policy policy = (Policy) CoreContext.getCoreProxy().queryUnique("get.policy.by.externalSystemId", policyId);
            if (policy != null) {
                expandedString = variableString.replace(policyIdPrefix + policyId + policyIdSuffix, "");

                String[] varNames = StringUtils.substringsBetween(expandedString, varPrefix, varSuffix);
                if (varNames != null) {
                    for (String xpath : varNames) {
                        try {
                            Object data = policy.xpathGet(xpath);
                            if (data != null) {
                                expandedString = expandedString.replace(varPrefix + xpath + varSuffix, data.toString());
                            }
                        } catch (TypeXPathException e) {
                            PageFlowContext.getCoreProxy().logError("Unable to get value for xpath: '" + xpath + "' for policy with id " + policyId);
                        }
                    }
                }
            }
        }

        return expandedString.replaceAll("\\s{2,}", " ").trim();
    }

    public static class Argument {

        String[] variableStrings;

    }

    public static class Return extends RestfulServiceReturn {

        String[] expandedStrings;

        public Return(int status, String[] expandedStrings) {
            super(status);
            this.expandedStrings = expandedStrings;
        }
    }

}