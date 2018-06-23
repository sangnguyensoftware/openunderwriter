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
package com.ail.insurance.payment;

import static com.ail.insurance.payment.PaymentCommon.journalExistsForPayment;
import static com.ail.insurance.payment.PaymentCommon.scheduledPayments;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.financial.MoneyProvision;
import com.ail.financial.MoneyProvisionPurpose;
import com.ail.financial.ledger.JournalType;
import com.ail.insurance.payment.PaymentDueQueryService.PaymentDueQueryArgument;

/**
 * This service searches for scheduled payments that do not yet have a matching
 * journal line, i.e. those that are due to be processed between the from and to dates
 * specified. The caller can then do what they choose with these payments.
 */
@ServiceImplementation
public class PaymentDueQueryService extends Service<PaymentDueQueryArgument> {

    @Override
    public void invoke() throws BaseException {
        validate();

        List<MoneyProvision> payments = scheduledPayments(args.getFromDateArg(), args.getToDateArg(), args.getPaymentPurposeArg());
        for (Iterator<MoneyProvision> i = payments.iterator(); i.hasNext();) {
            if (journalExistsForPayment(i.next(), args.getJournalTypeArg(), args.getFromDateArg(), args.getToDateArg())) {
                i.remove();
            }
        }

        args.setPaymentsDueRet(payments);
    }

    private void validate() throws PreconditionException {
        if (args.getFromDateArg() == null) {
            throw new PreconditionException("args.getFromDateArg() is null");
        }
        if (args.getToDateArg() == null) {
            throw new PreconditionException("args.getToDateArg() is null");
        }
        if (args.getPaymentPurposeArg() == null) {
            throw new PreconditionException("args.getPaymentPurposeArg() is null");
        }
        if (args.getJournalTypeArg() == null) {
            throw new PreconditionException("args.getJournalTypeArg() is null");
        }
    }

    @ServiceCommand(defaultServiceClass = PaymentDueQueryService.class)
    public interface PaymentDueQueryCommand extends Command, PaymentDueQueryArgument {
    }

    @ServiceArgument
    public interface PaymentDueQueryArgument extends Argument {

        /**
         * The date from which to find due payments.
         */
        Date getFromDateArg();

        /**
         * The date from which to find due payments.
         */
        void setFromDateArg(Date fromDateArg);

        /**
         * The date up to and including which to find due payments.
         */
        Date getToDateArg();

        /**
         * The date up to and including which to find due payments.
         */
        void setToDateArg(Date toDateArg);

        /**
         * The purpose of the payment to find. If null then will find all.
         */
        MoneyProvisionPurpose getPaymentPurposeArg();

        /**
         * The purpose of the payment to find. If null then will find all.
         */
        void setPaymentPurposeArg(MoneyProvisionPurpose paymentPurposeArg);

        /**
         * The journal type to check for the existence of
         */
        JournalType getJournalTypeArg();

        /**
         * The journal type to check for the existence of
         */
        void setJournalTypeArg(JournalType journalTypeArg);

        /**
         * The payments due for this date
         */
        List<MoneyProvision> getPaymentsDueRet();

        /**
         * The payments due for this date
         */
        void setPaymentsDueRet(List<MoneyProvision> paymentsDueRet);
    }
}
