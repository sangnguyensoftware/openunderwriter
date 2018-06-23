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

import static com.ail.financial.ledger.JournalLineType.PREMIUM;
import static com.ail.financial.ledger.JournalType.PREMIUM_RECEIVED;
import static com.ail.insurance.payment.PaymentCommon.buildJournal;
import static com.ail.insurance.payment.PaymentCommon.buildJournalLines;
import static com.ail.insurance.payment.PaymentCommon.fetchPolicyForMoneyProvision;
import static com.ail.insurance.payment.PaymentCommon.journalExistsForPayment;
import static com.ail.insurance.payment.PaymentCommon.postJournal;
import static com.ail.insurance.payment.PaymentCommon.scheduledPayments;

import java.util.Date;
import java.util.List;
import java.util.Set;

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
import com.ail.financial.ledger.Journal;
import com.ail.financial.ledger.JournalLine;
import com.ail.insurance.payment.PremiumReceivedProcessorService.PremiumReceivedProcessorArgument;
import com.ail.insurance.policy.Policy;
import com.ail.party.Party;

/**
 * <p>
 * This service searches for scheduled payments that are due to be received on the effective
 * date specified and debits the payment provider's account. Matching credits are
 * made to the client account.
 * </p>
 * <p>
 * This service is only concerned with translating Scheduled Payments into
 * Journals. It does not perform payment transactions with external providers.
 * </p>
 */
@ServiceImplementation
public class PremiumReceivedProcessorService extends Service<PremiumReceivedProcessorArgument> {

    private String configurationNamespace;
    @Override
    public void invoke() throws BaseException {
        if (args.getFromDateArg() == null) {
            throw new PreconditionException("args.getFromDateArg() == null");
        }
        if (args.getToDateArg() == null) {
            throw new PreconditionException("args.getToDateArg() == null");
        }

        List<MoneyProvision> payments = scheduledPayments(args.getFromDateArg(), args.getToDateArg(), args.getPaymentPurpose());
        args.setPaymentsToProcessRet(payments.size());
        for (MoneyProvision payment : payments) {
            if (journalExistsForPayment(payment, PREMIUM_RECEIVED, args.getFromDateArg(), args.getToDateArg())) {
                continue;
            }

            Policy policy = fetchPolicyForMoneyProvision(payment);

            Set<JournalLine> journalLines = buildJournalLines(policy, payment, PREMIUM, args.getPaymentProviderArg(), policy.getClient());

            Journal journal = buildJournal(policy, journalLines, payment, args.getToDateArg(), PREMIUM_RECEIVED);

            postJournal(journal);

            args.setJournalLinesCreatedRet(args.getJournalLinesCreatedRet() + journalLines.size());
        }
    }

    @Override
    public String getConfigurationNamespace() {
        return configurationNamespace;
    }

    public void setConfigurationNamespace(String configurationNamespace) {
        this.configurationNamespace = configurationNamespace;
    }

    @ServiceCommand(defaultServiceClass = PremiumReceivedProcessorService.class)
    public interface PremiumReceivedProcessorCommand extends Command, PremiumReceivedProcessorArgument {
    }

    @ServiceArgument
    public interface PremiumReceivedProcessorArgument extends Argument {

        /**
         * The date from which to find scheduled payments.
         */
        Date getFromDateArg();

        /**
         * The date from which to find scheduled payments.
         */
        void setFromDateArg(Date fromDateArg);

        /**
         * The date up to and including which to find scheduled payments.
         */
        Date getToDateArg();

        /**
         * The date up to and including which to find scheduled payments.
         */
        void setToDateArg(Date toDateArg);

        /**
         * The purpose of the payment to find. If null then will find all.
         */
        MoneyProvisionPurpose getPaymentPurpose();

        /**
         * The purpose of the payment to find. If null then will find all.
         */
        void setPaymentPurpose(MoneyProvisionPurpose paymentPurpose);

        /**
         * The source of the credit to the Receiver account
         */
        Party getPaymentProviderArg();

        /**
         * The source of the credit to the Receiver account
         */
        void setPaymentProviderArg(Party paymentProviderArg);

        /**
         * The number of payments to process for this date
         */
        int getPaymentsToProcessRet();

        /**
         * The number of payments to process for this date
         */
        void setPaymentsToProcessRet(int paymentsToProcessRet);

        /**
         * The number of journal lines created for this processing date
         */
        int getJournalLinesCreatedRet();

        /**
         * The number of journal lines created for this processing date
         */
        void setJournalLinesCreatedRet(int journalLinesCreatedRet);
    }
}
