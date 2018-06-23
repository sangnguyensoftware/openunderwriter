package com.ail.base;
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
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_OK;

import java.util.Date;
import java.util.List;

import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.CoreProxy;
import com.ail.core.JSONException;
import com.ail.core.PostconditionException;
import com.ail.core.RestfulServiceInvoker;
import com.ail.core.RestfulServiceReturn;
import com.ail.core.product.ProductServiceCommand;
import com.ail.core.product.ProductUpgradeLog;
import com.ail.core.product.UpgradeProductService.UpgradeProductCommand;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;

/**
 * Execute any outstanding product upgrade commands associated with he specified
 * product.
 */
@ProductServiceCommand(serviceName = "UpgradeProductService", commandName = "UpgradeProduct")
public class UpgradeProductService extends RestfulServiceInvoker {

    public static void invoke(ExecutePageActionArgument args) throws BaseException {
        new UpgradeProductService().invoke();
    }

    public RestfulServiceReturn service() throws PostconditionException, JSONException {

        try {
            CoreProxy coreProxy = new CoreProxy();
            
            Date startedAt = new Date();
            
            UpgradeProductCommand puc = (UpgradeProductCommand)coreProxy.newCommand(UpgradeProductCommand.class);
            puc.setProductNameArg(CoreContext.getProductName());
            puc.invoke();

            return buildReturn(coreProxy, startedAt);
        } catch (BaseException e) {
            return new Return(HTTP_INTERNAL_ERROR, e.getDescription());
        }
    }

    private Return buildReturn(CoreProxy coreProxy, Date startedAt) {
        Return ret = new Return(HTTP_OK, "Upgrades complete.");
        
        coreProxy.flush();
        
        @SuppressWarnings("unchecked")
        List<ProductUpgradeLog> logs=(List<ProductUpgradeLog>)coreProxy.query("get.product.upgrade.since", startedAt);

        for(ProductUpgradeLog log: logs) {
            ret.executed++;
            if (log.isSuccess()) {
                ret.successful++;
            }
            else {
                ret.failed++;
            }
        }
        return ret;
    }

    public static class Return extends RestfulServiceReturn {
        String message;
        int executed;
        int successful;
        int failed;

        public Return(int status, String message) {
            super(status);
            this.message = message;
        }
    }
}