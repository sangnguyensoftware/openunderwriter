package com.ail.base.data;
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
import com.ail.core.PostconditionException;
import com.ail.core.RestfulServiceInvoker;
import com.ail.core.RestfulServiceReturn;
import com.ail.core.product.ProductServiceCommand;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;

/**
 * Service to set data on a Type, which could be a Policy, Claim, or Party. This
 * service expects the body of the request to contain the type, the type id, the
 * data directory id which will be set by xpath on the type, and the value to
 * set.
 */
@ProductServiceCommand(serviceName = "SetDataService", commandName = "SetData")
public class SetDataService extends RestfulServiceInvoker {

    public static void invoke(ExecutePageActionArgument args) throws BaseException {
        new SetDataService().invoke(Argument.class);
    }

    public RestfulServiceReturn service(Argument arg) throws PostconditionException {
        if (StringUtils.isBlank(arg.caseType)) {
            return new ClientError(HTTP_BAD_REQUEST, "Invalid value for arg caseType: " + arg.caseType);
        }
        if (StringUtils.isBlank(arg.caseId)) {
            return new ClientError(HTTP_BAD_REQUEST, "Invalid value for arg caseId: " + arg.caseId);
        }
        if (StringUtils.isBlank(arg.dataDirectoryId)) {
            return new ClientError(HTTP_BAD_REQUEST, "Invalid value for arg dataDirectoryId: " + arg.dataDirectoryId);
        }
        if (StringUtils.isBlank(arg.value)) {
            return new ClientError(HTTP_BAD_REQUEST, "Invalid value for arg templateName: " + arg.value);
        }

        new DataHelper().setData(arg.caseType, arg.caseId, arg.dataDirectoryId, arg.value);

        return new RestfulServiceReturn(HTTP_OK);
    }

    public static class Argument {

        String caseType;
        String caseId;
        String dataDirectoryId;
        String value;
    }

}