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

import static com.ail.financial.PaymentRecordType.COMPLETE;
import static com.ail.insurance.payment.PaymentCommon.buildJournal;
import static com.ail.insurance.payment.PaymentCommon.buildJournalLines;
import static com.ail.insurance.payment.PaymentCommon.fetchPolicyForMoneyProvision;
import static com.ail.insurance.payment.PaymentCommon.journalExistsForPayment;
import static com.ail.insurance.payment.PaymentCommon.postJournal;
import static com.ail.insurance.payment.PaymentCommon.scheduledPayments;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.drools.core.util.StringUtils;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.core.language.I18N;
import com.ail.financial.MoneyProvision;
import com.ail.financial.MoneyProvisionPurpose;
import com.ail.financial.PaymentRecord;
import com.ail.financial.ledger.Journal;
import com.ail.financial.ledger.JournalLine;
import com.ail.insurance.payment.JournalRecorderService.JournalRecorderArgument;
import com.ail.insurance.policy.Policy;
import com.ail.party.Party;

/**
 * <p>
 * This service searches for scheduled payments that are due to be made on the date specified.
 * For each payment it loops through the list of debtors and creditors; if a journal entry has not yet been
 * made for the type of journal then it will be created and posted.
 * </p>
 * <p>
 * This service is only concerned with translating Scheduled Payments into
 * Journals. It does not perform payment transactions with external providers.
 * It also adds to the policy payment history.
 * </p>
 */
@ServiceImplementation
public class JournalRecorderService extends Service<JournalRecorderArgument> {

    private String configurationNamespace;
    @Override
    public void invoke() throws BaseException {
        validate();

        List<MoneyProvision> payments = scheduledPayments(args.getFromDateArg(), args.getToDateArg(), args.getPaymentPurposeArg());
        args.setPotentialPaymentsRet(payments.size());
        for (MoneyProvision payment : payments) {
            Policy policy = fetchPolicyForMoneyProvision(payment);

            boolean newJournalCreated = false;
            for (DebtorCreditorJournalEntry dcje : args.getDebtorsCreditorsArg()) {
                if (!journalExistsForPayment(payment, dcje.getJournalType(), args.getFromDateArg(), args.getToDateArg())) {
                    Set<JournalLine> journalLines = buildJournalLines(policy, payment, dcje.getJournalLineType(), getDebtorParty(dcje, policy), getCreditorParty(dcje, policy));

                    Journal journal = buildJournal(policy, journalLines, payment, args.getToDateArg(), dcje.getJournalType());

                    postJournal(journal);

                    args.setJournalLinesCreatedRet(args.getJournalLinesCreatedRet() + journalLines.size());

                    newJournalCreated = true;
                }
            }

            if (newJournalCreated) {
                policy.getPaymentHistory().add(new PaymentRecord(payment.getAmount(), I18N.i18n(payment.getPurpose().getLongName(), payment.getPurpose().getName()), payment.getPaymentMethod(), COMPLETE));
            }
        }
    }

    private Party getDebtorParty(DebtorCreditorJournalEntry dcje, Policy policy) {
        Party debtor = dcje.getDebtor();
        if (debtor == null) {
            debtor = (Party) policy.xpathGet(dcje.getDebtorPolicyXpath());
        }

        return debtor;
    }

    private Party getCreditorParty(DebtorCreditorJournalEntry dcje, Policy policy) {
        Party debtor = dcje.getCreditor();
        if (debtor == null) {
            debtor = (Party) policy.xpathGet(dcje.getCreditorPolicyXpath());
        }

        return debtor;
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
        if (args.getDebtorsCreditorsArg() == null || args.getDebtorsCreditorsArg().isEmpty()) {
            throw new PreconditionException("args.getDebtorsCreditorsArg() is null or empty");
        }
        for (DebtorCreditorJournalEntry dcje : args.getDebtorsCreditorsArg()) {
            if (dcje.getDebtor() == null && StringUtils.isEmpty(dcje.getDebtorPolicyXpath())) {
                throw new PreconditionException("args.getDebtorsCreditorsArg() debtor and debtorPolicyXpath is null or empty");
            }
            if (dcje.getCreditor() == null && StringUtils.isEmpty(dcje.getCreditorPolicyXpath())) {
                throw new PreconditionException("args.getDebtorsCreditorsArg() creditor and creditorPolicyXpath is null or empty");
            }
        }
    }

    @Override
    public String getConfigurationNamespace() {
        if (configurationNamespace == null) {
            return super.getConfigurationNamespace();
        } else {
            return configurationNamespace;
        }
    }

    public void setConfigurationNamespace(String configurationNamespace) {
        this.configurationNamespace = configurationNamespace;
    }

    @ServiceCommand(defaultServiceClass = JournalRecorderService.class)
    public interface JournalRecorderCommand extends Command, JournalRecorderArgument {
    }

    @ServiceArgument
    public interface JournalRecorderArgument extends Argument {

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
        MoneyProvisionPurpose getPaymentPurposeArg();

        /**
         * The purpose of the payment to find. If null then will find all.
         */
        void setPaymentPurposeArg(MoneyProvisionPurpose paymentPurposeArg);

        /**
         * A chain of debtors and creditors to create journal records for
         */
        List<DebtorCreditorJournalEntry> getDebtorsCreditorsArg();

        /**
         * A chain of debtors and creditors to create journal records for
         */
        void setDebtorsCreditorsArg(List<DebtorCreditorJournalEntry> debtorsCreditorsArg);

        /**
         * The number of potential payments for this date
         */
        int getPotentialPaymentsRet();

        /**
         * The number of potential payments for this date
         */
        void setPotentialPaymentsRet(int paymentsToProcessRet);

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
