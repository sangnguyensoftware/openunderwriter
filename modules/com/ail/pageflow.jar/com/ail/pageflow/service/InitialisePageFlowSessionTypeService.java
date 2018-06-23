/* Copyright Applied Industrial Logic Limited 2002. All rights Reserved */
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

import com.ail.annotation.ServiceImplementation;
import com.ail.core.Attribute;
import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.Service;
import com.ail.core.Type;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;
import com.ail.pageflow.PageFlowContext;

/**
 */
@ServiceImplementation
public class InitialisePageFlowSessionTypeService extends Service<ExecutePageActionArgument> {
    private static final long serialVersionUID = 3198813603833694389L;

    @Override
    public void invoke() throws BaseException {

        if (configurationSourceIsRequest() && isNotAnAjaxRequest()) {
            PageFlowContext.clear();
        }

        if (PageFlowContext.getSessionTemp() == null) {
            Type type = PageFlowContext.getCoreProxy().newType(Attribute.class);
            PageFlowContext.setSessionSessionTemp(type);
        }
    }

    protected boolean configurationSourceIsRequest() {
        return CoreContext.getPreferencesWrapper().isConfiguredByRequest();
    }

    protected boolean isNotAnAjaxRequest() {
        return !CoreContext.getRequestWrapper().isAjaxRequest();
    }
}