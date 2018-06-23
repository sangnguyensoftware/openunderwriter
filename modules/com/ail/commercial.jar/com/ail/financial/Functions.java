/* Copyright Applied Industrial Logic Limited 2002. All rights reserved. */
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

package com.ail.financial;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.jresearch.ical.compat.javautil.DateIterator;
import org.jresearch.ical.compat.javautil.DateIteratorFactory;

import com.ail.annotation.XPathFunctionDefinition;
import com.ail.core.CoreProxy;

/**
 * Utility function class for dealing with the Financial objects that are
 * available for use from java and from XPath queries.
 **/
@XPathFunctionDefinition(namespace="f")
public class Functions {

    /**
     * Gets the next payment date based on the current date and the money provision frequency
     * @param moneyProvision
     * @return
     */
    public static Date nextPaymentDate(MoneyProvision moneyProvision) {
        Date paymentsStartDate = moneyProvision.getPaymentsStartDate();
        Calendar paymentsStartCal = Calendar.getInstance();
        paymentsStartCal.setTime(paymentsStartDate);

        Date nextPaymentDate = paymentsStartDate;

        String rdata = getRDataForMoneyProvisionFrequency(moneyProvision.getFrequency(), paymentsStartCal);
        try {
            DateIterator dateIterator = DateIteratorFactory.createDateIterator(rdata, paymentsStartDate, TimeZone.getDefault(), false);
            dateIterator.advanceTo(Calendar.getInstance().getTime());
            nextPaymentDate = dateIterator.next();
        } catch (ParseException pe) {
            CoreProxy cp = new CoreProxy();
            cp.logWarning("nextPaymentDate calculation failed for: " + moneyProvision + ". RData is: '" + rdata + "'");
        }

        return nextPaymentDate;
    }

    /**
     * Gets the ical/google-rfc-2445 rule to calculate intervals
     * @param frequency
     * @param startDate
     * @return
     */
    private static String getRDataForMoneyProvisionFrequency(FinancialFrequency frequency, Calendar startDate) {
        String rdata = "";

        switch (frequency) {
        case WEEKLY:
            rdata += "RRULE:FREQ=WEEKLY;INTERVAL=1;";
            break;
        case BIWEEKLY:
            rdata += "RRULE:FREQ=WEEKLY;INTERVAL=2;";
            break;
        case MONTHLY:
            rdata += "RRULE:FREQ=MONTHLY;INTERVAL=1;";
            break;
        case BIMONTHLY:
            rdata += "RRULE:FREQ=MONTHLY;INTERVAL=2;";
            break;
        case QUARTERLY:
            rdata += "RRULE:FREQ=MONTHLY;INTERVAL=3;";
            break;
        case SEMESTERLY:
            rdata += "RRULE:FREQ=MONTHLY;INTERVAL=6;";
            break;
        case YEARLY:
            rdata += "RRULE:FREQ=YEARLY;INTERVAL=1;";
            break;
        default:
            break;
        }

        return rdata;
    }

    /**
     * Gets the latest payment by date from a list of payment records
     * @param paymentHistory
     * @return
     */
    public static PaymentRecord lastPayment(List<PaymentRecord> paymentHistory) {
        if (paymentHistory != null && !paymentHistory.isEmpty()) {
            List<PaymentRecord> paymentHistorySortable = new ArrayList<>();
            paymentHistorySortable.addAll(paymentHistory);
            Collections.sort(paymentHistorySortable, new Comparator<PaymentRecord>() {
                @Override
                public int compare(PaymentRecord o1, PaymentRecord o2) {
                    return o1.getDate().compareTo(o2.getDate());
                }
            });
            return paymentHistorySortable.get(paymentHistorySortable.size() - 1);
        }

        return null;
    }
}
