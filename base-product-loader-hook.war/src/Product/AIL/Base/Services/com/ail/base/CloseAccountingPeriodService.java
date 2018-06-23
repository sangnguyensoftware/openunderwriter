package com.ail.base;
/* Copyright Applied Industrial Logic Limited 2018. All rights Reserved */
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
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_OK;

import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.JSONException;
import com.ail.core.RestfulServiceInvoker;
import com.ail.core.RestfulServiceReturn;
import com.ail.core.product.ProductServiceCommand;
import com.ail.financial.service.CloseAccountingPeriodService.CloseAccountingPeriodCommand;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;

/**
 * Close the specified accounting period.
 */
@ProductServiceCommand(serviceName = "CloseAccountingPeriodService", commandName = "CloseAccountingPeriod")
public class CloseAccountingPeriodService extends RestfulServiceInvoker {

    public static void invoke(ExecutePageActionArgument args) throws BaseException {
        new CloseAccountingPeriodService().invoke(Argument.class);
    }

    public RestfulServiceReturn service(Argument arg) throws JSONException {

        try {
            CloseAccountingPeriodCommand capc = (CloseAccountingPeriodCommand)CoreContext.getCoreProxy().newCommand(CloseAccountingPeriodCommand.class);
            capc.setAccountingPeriodExternalSystemIdArg(arg.accountingPeriodId);
            capc.invoke();

            return new Return(HTTP_OK);
        } catch (BaseException e) {
            return new Return(HTTP_INTERNAL_ERROR, e.getDescription());
        }
    }

    public static class Argument {
        String accountingPeriodId;
    }

    public static class Return extends RestfulServiceReturn {
        String message;
    
        public Return(int status) {
            super(status);
        }

        public Return(int status, String message) {
            super(status);
            this.message = message;
        }
    }
}