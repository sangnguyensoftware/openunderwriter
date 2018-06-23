/* Copyright Applied Industrial Logic Limited 2017. All rights Reserved */
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

package com.ail.pageflow.service;

import static com.ail.pageflow.PageElement.OPERATION_PARAM;

import java.util.Collection;
import java.util.Collections;
import java.util.Properties;

import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.pageflow.ExecutePageActionService;
import com.ail.pageflow.PageFlowContext;
/**
 * Get the name of the pageflow we're working for. This comes from one of three
 * places: in normal operation it is picked up from the portlet preference named
 * 'pageflow', or from the request property "pageflow.name". In development
 * mode (i.e. in the sandpit) it is picked up from the session.
 */
@ServiceImplementation
public class AddOperationParametersToPageFlowContextService extends Service<ExecutePageActionService.ExecutePageActionArgument> {
    private static final long serialVersionUID = 3198893603833694389L;

    @Override
    public void invoke() throws BaseException {
        if (CoreContext.getRequestWrapper() == null) {
            throw new PreconditionException("CoreContext.getRequestWrapper() == null");
        }

        String operatonalParam = null;
        Properties params = new Properties();
        Collection<String> paramNames = Collections.list(CoreContext.getRequestWrapper().getParameterNames());

        // Search for a param called 'op' - this is how Ajax will pass it to us.
        for (String paramName : paramNames) {
            if (OPERATION_PARAM.equals(paramName)) {
                operatonalParam = CoreContext.getRequestWrapper().getParameter(OPERATION_PARAM);
                break;
            }
        }

        // Nothing from Ajax? Search for a param starting with "op=" - this is
        // how a non-ajax request sends it to us
        if (operatonalParam == null) {
            for (String paramName : paramNames) {
                if (paramName.startsWith("op=")) {
                    operatonalParam = paramName;
                    break;
                }
            }
        }

        if (operatonalParam != null) {
            for (String param : operatonalParam.split(":")) {
                String[] parts = param.split("=");
                if (parts.length == 2) {
                    params.put(parts[0], parts[1]);
                }
            }
        }

        PageFlowContext.setOperationParameters(params);
    }
}