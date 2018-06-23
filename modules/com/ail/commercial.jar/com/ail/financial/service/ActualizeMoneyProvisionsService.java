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
package com.ail.financial.service;

import static com.ail.core.DateTimeUtils.dateToLocalDate;
import static com.ail.core.DateTimeUtils.localDateToDate;
import static com.ail.financial.FinancialFrequency.ONE_TIME;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.financial.MoneyProvision;
import com.ail.financial.service.ActualizeMoneyProvisionsService.ActualizeMoneyProvisionsArgument;

/**
 * Service to create an actualized list of money provisions based on a
 * normalized source list. As an example, a "normalized" list of money
 * provisions may contain a single money provision that describes a repeating
 * monthly payment of £30 to be taken on the 1st of each month. Its actualized
 * counterpart will contain one money provision for each month of the period it
 * covers. If the request was to calculate an actualized list for 4 months then
 * 4 separate 1-off money provisions would be returned.
 */
@ServiceImplementation
public class ActualizeMoneyProvisionsService extends Service<ActualizeMoneyProvisionsArgument> {

    @Override
    public void invoke() throws BaseException {
        if (args.getPeriodStartDateArg() == null) {
            throw new PreconditionException("args.getPeriodStartDateArg() == null");
        }

        if (args.getPeriodEndDateArg()!=null && args.getPeriodStartDateArg().after(args.getPeriodEndDateArg())) {
            throw new PreconditionException("args.getPeriodEndDateArg()!=null && args.getPeriodStartDateArg().after(args.getPeriodEndDateArg())");
        }

        if (args.getNormalizedMoneyProvisionsArg() == null) {
            throw new PreconditionException("args.getNormalizedMoneyProvisionsArg() == null");
        }

        if (args.getNormalizedMoneyProvisionsArg().isEmpty()) {
            args.setActualizedMoneyProvisionsRet(new ArrayList<MoneyProvision>());
        } else {
            args.setActualizedMoneyProvisionsRet(actualize());
        }

        if (args.getActualizedMoneyProvisionsRet() == null) {
            throw new PostconditionException("args.getActualizedMoneyProvisionsRet() == null");
        }
    }

    private List<MoneyProvision> actualize() {
        List<MoneyProvision> actualized = new ArrayList<>();
        LocalDate periodStartDate = dateToLocalDate(args.getPeriodStartDateArg());
        LocalDate periodEndDate = dateToLocalDate(args.getPeriodEndDateArg() != null ? args.getPeriodEndDateArg() : args.getPeriodStartDateArg());

        for (LocalDate day = periodStartDate; day.isBefore(periodEndDate) || day.equals(periodEndDate); day = day.plusDays(1)) {
            MoneyProvision actual = null;
            for (MoneyProvision normal : args.getNormalizedMoneyProvisionsArg()) {
                if (normal.isPayableOn(day)) {
                    actual = rollupMoneyProvisionsForDay(actual, normal, day);
                }
            }
            if (actual != null) {
                actualized.add(actual);
            }
        }

        return actualized;
    }

    private MoneyProvision rollupMoneyProvisionsForDay(MoneyProvision actual, MoneyProvision normal, LocalDate day) {
        if (actual == null) {
            MoneyProvision ret = new MoneyProvision();
            ret.setAmount(normal.getAmount());
            ret.setPaymentMethod(normal.getPaymentMethod());
            ret.setFrequency(ONE_TIME);
            ret.setPaymentsStartDate(localDateToDate(day));
            ret.setPaymentsEndDate(localDateToDate(day));
            ret.setStatus(normal.getStatus());
            ret.setDescription(actualizedDescription(normal));
            ret.setPurpose(normal.getPurpose());
            return ret;
        }
        else {
            actual.setAmount(actual.getAmount().add(normal.getAmount()));
            actual.setPaymentMethod(null);
            actual.setStatus(null);
            actual.setDescription(actual.getDescription()+" + "+actualizedDescription(normal));
        }

        return actual;
    }

    private String actualizedDescription(MoneyProvision normal) {
        return String.format("%s of %s for %s", normal.getPaymentMethod().getClass().getSimpleName(), normal.getAmount().toFormattedString(), normal.getDescription());
    }

    @ServiceCommand(defaultServiceClass = ActualizeMoneyProvisionsService.class)
    public interface ActualizeMoneyProvisionsCommand extends Command, ActualizeMoneyProvisionsArgument {
    }

    @ServiceArgument
    public interface ActualizeMoneyProvisionsArgument extends Argument {
        Date getPeriodStartDateArg();

        void setPeriodStartDateArg(Date periodStartDateArg);

        Date getPeriodEndDateArg();

        void setPeriodEndDateArg(Date periodEndDateArg);

        List<MoneyProvision> getNormalizedMoneyProvisionsArg();

        void setNormalizedMoneyProvisionsArg(List<MoneyProvision> normalizedMoneyProvisionsArg);

        List<MoneyProvision> getActualizedMoneyProvisionsRet();

        void setActualizedMoneyProvisionsRet(List<MoneyProvision> actualizedMoneyProvisionsArg);
    }
}
