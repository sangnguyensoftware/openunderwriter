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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.JSONException;
import com.ail.core.PostconditionException;
import com.ail.core.RestfulServiceInvoker;
import com.ail.core.RestfulServiceReturn;
import com.ail.core.product.ProductServiceCommand;
import com.ail.financial.ledger.AccountingPeriod;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;

/**
 * List all of the accounting periods know to the system along with their
 * external system IDs.
 */
@ProductServiceCommand(serviceName = "ListAccountingPeriodsService", commandName = "ListAccountingPeriods")
public class ListAccountingPeriodsService extends RestfulServiceInvoker {

    public static void invoke(ExecutePageActionArgument args) throws BaseException {
        new ListAccountingPeriodsService().invoke();
    }

    @SuppressWarnings("unchecked")
    public RestfulServiceReturn service() throws PostconditionException, JSONException {

        try {
            return new Return(HTTP_OK, (List<AccountingPeriod>) CoreContext.getCoreProxy().query("get.accountingperiods"));
        } catch (Throwable e) {
            return new Return(HTTP_INTERNAL_ERROR, e.getMessage());
        }
    }

    public static class Return extends RestfulServiceReturn {
        List<Period> periods = new ArrayList<Period>();

        public static class Period {
            String accountingPeriodId;
            Date startDate;
            Date endDate;
            String status;

            public Period(AccountingPeriod p) {
                this.accountingPeriodId = p.getExternalSystemId();
                this.startDate = p.getStartDate();
                this.endDate = p.getEndDate();
                this.status = p.getStatus().getName();
            }
        }

        String message;
        int count;

        public Return(int status, List<AccountingPeriod> periods) {
            super(status);
            if (periods != null && periods.size() != 0) {
                this.count = periods.size();
                for (AccountingPeriod period : periods) {
                    this.periods.add(new Period(period));
                }
            }
        }

        public Return(int status, String message) {
            super(status);
            this.message = message;
        }
    }
}