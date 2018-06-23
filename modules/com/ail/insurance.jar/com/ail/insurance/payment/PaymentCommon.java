package com.ail.insurance.payment;

import static com.ail.core.CoreContext.getCoreProxy;
import static com.ail.core.DateTimeUtils.dayOfTheBiMonth;
import static com.ail.core.DateTimeUtils.dayOfTheMonth;
import static com.ail.core.DateTimeUtils.dayOfTheQuarter;
import static com.ail.core.DateTimeUtils.dayOfTheSemester;
import static com.ail.core.DateTimeUtils.dayOfTheWeek;
import static com.ail.core.DateTimeUtils.dayOfTheYear;
import static com.ail.core.DateTimeUtils.format;
import static com.ail.financial.FinancialFrequency.BIMONTHLY;
import static com.ail.financial.FinancialFrequency.MONTHLY;
import static com.ail.financial.FinancialFrequency.ONE_TIME;
import static com.ail.financial.FinancialFrequency.QUARTERLY;
import static com.ail.financial.FinancialFrequency.SEMESTERLY;
import static com.ail.financial.FinancialFrequency.WEEKLY;
import static com.ail.financial.FinancialFrequency.YEARLY;
import static com.ail.financial.MoneyProvisionStatus.NEW;
import static org.hibernate.criterion.Restrictions.and;
import static org.hibernate.criterion.Restrictions.disjunction;
import static org.hibernate.criterion.Restrictions.eq;
import static org.hibernate.criterion.Restrictions.ge;
import static org.hibernate.criterion.Restrictions.gt;
import static org.hibernate.criterion.Restrictions.in;
import static org.hibernate.criterion.Restrictions.isNull;
import static org.hibernate.criterion.Restrictions.le;
import static org.hibernate.criterion.Restrictions.or;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.criterion.LogicalExpression;

import com.ail.core.BaseException;
import com.ail.core.Reference;
import com.ail.financial.Currency;
import com.ail.financial.CurrencyAmount;
import com.ail.financial.FinancialFrequency;
import com.ail.financial.MoneyProvision;
import com.ail.financial.MoneyProvisionPurpose;
import com.ail.financial.ledger.Journal;
import com.ail.financial.ledger.Journal.JournalBuilder;
import com.ail.financial.ledger.JournalLine;
import com.ail.financial.ledger.JournalLine.JournalLineBuilder;
import com.ail.financial.ledger.JournalLineType;
import com.ail.financial.ledger.JournalType;
import com.ail.financial.ledger.Ledger;
import com.ail.financial.ledger.LedgerValidationException;
import com.ail.financial.service.PostJournalService.PostJournalCommand;
import com.ail.insurance.policy.Policy;
import com.ail.party.Party;

public class PaymentCommon {


    /**
     * Get the policy for a money provision
     * @param payment
     * @return
     */
    public static Policy fetchPolicyForMoneyProvision(MoneyProvision payment) {
        return (Policy) getCoreProxy().queryUnique("get.policy.for.moneyprovision", payment);
    }

    /**
     * Check to see if a payment already has journal entries - if it does we probably don't want to make it again. For {@link FinancialFrequency#ONE_TIME}
     * payments we should check across the whole date range of the payment not just the specified dates to make sure the payment is not repeated.
     * Behaviour for {@link FinancialFrequency#UNDEFINED} is not defined.
     * @param payment
     * @param journalType
     * @param from
     * @param to
     * @return
     */
    public static boolean journalExistsForPayment(MoneyProvision payment, JournalType journalType, Date from, Date to) {
        if (payment.getFrequency() == ONE_TIME) {
            return getCoreProxy().query("get.journal.by.origin.and.type.and.transaction.date.range", payment, journalType, payment.getPaymentsStartDate(), payment.getPaymentsEndDate()).size() != 0;
        } else {
            return getCoreProxy().query("get.journal.by.origin.and.type.and.transaction.date.range", payment, journalType, from, to).size() != 0;
        }
    }

    /**
     * Get a list of all the possible payments for the days of the month in a given date range
     * Behaviour for {@link FinancialFrequency#UNDEFINED} is not defined.
     * TODO Behaviour for {@link FinancialFrequency#BIWEEKLY} is not currently defined.
     * @param from
     * @param to
     * @param paymentPurpose
     * @return
     */
    @SuppressWarnings("unchecked")
    public static List<MoneyProvision> scheduledPayments(Date from, Date to, MoneyProvisionPurpose paymentPurpose) {
        Criteria criteria = getCoreProxy().criteria(MoneyProvision.class);

        criteria.add(eq("status", NEW));

        criteria.add(disjunction()
                        .add(getDayExpression(dayOfTheYear(from), dayOfTheYear(to), YEARLY))
                        .add(getDayExpression(dayOfTheSemester(from), dayOfTheYear(to), SEMESTERLY))
                        .add(getDayExpression(dayOfTheQuarter(from), dayOfTheYear(to), QUARTERLY))
                        .add(getDayExpression(dayOfTheBiMonth(from), dayOfTheYear(to), BIMONTHLY))
                        .add(getDayExpression(dayOfTheMonth(from), dayOfTheMonth(to), MONTHLY))
                        .add(getDayExpression(dayOfTheWeek(from), dayOfTheWeek(to), WEEKLY))
                        .add(in("frequency", new Object[] {ONE_TIME})));

        criteria.add(or(isNull("paymentsStartDate"), le("paymentsStartDate", to)));
        criteria.add(or(isNull("paymentsEndDate"), gt("paymentsEndDate", from)));
        if (paymentPurpose != null) {
            criteria.add(eq("purpose", paymentPurpose));
        }
        criteria.setReadOnly(true);

        List<?> payments = criteria.list();

        getCoreProxy().logInfo("Found " + payments.size() + " potential scheduledPayments to process up to effectiveDate " + format(to, "dd/MM/yyyy") + " for purpose " + paymentPurpose);

        return (List<MoneyProvision>) payments;
    }

    /**
     * Get an expression to restrict the results by day, dependent on the date from, date to, and frequency
     * @param dayFrom
     * @param dayTo
     * @param frequency
     * @return
     */
    private static LogicalExpression getDayExpression(int dayFrom, int dayTo, FinancialFrequency frequency) {
        if (dayFrom <= dayTo) {
            return and(eq("frequency", frequency), and(ge("day", dayFrom), le("day", dayTo)));
        } else {
            // rolling over the month so adjust the query
            return and(eq("frequency", frequency), or(ge("day", dayFrom), le("day", dayTo)));
        }
    }

    /**
     * Get the ledger for a party for a particular currency
     * @param party
     * @param currency
     * @return
     */
    public static Ledger ledgerFor(Party party, Currency currency) {
        return (Ledger) getCoreProxy().queryUnique("get.ledger.for.party.and.currency", party, currency);
    }

    /**
     * Post the journal
     * @param journal
     * @throws BaseException
     */
    public static void postJournal(Journal journal) throws BaseException {
        PostJournalCommand pjc = getCoreProxy().newCommand(PostJournalCommand.class);
        pjc.setJournalArgRet(journal);
        pjc.invoke();
    }

    /**
     * Build a journal from the supplied parameters
     * @param policy
     * @param journalLines
     * @param payment
     * @param transactionDate
     * @param journalType
     * @return
     * @throws LedgerValidationException
     */
    public static Journal buildJournal(Policy policy, Set<JournalLine> journalLines, MoneyProvision payment, Date transactionDate, JournalType journalType) throws LedgerValidationException {
        return new JournalBuilder().
                        subject(new Reference(Policy.class, policy.getExternalSystemId())).
                        withTransactionDate(transactionDate).
                        with(journalLines).
                        ofType(journalType).
                        forOrigin(payment).
                        build();
    }

    /**
     * Build the journal lines for the credit and debit side of a payment
     * @param policy
     * @param payment
     * @param lineType
     * @param debitParty
     * @param creditParty
     * @return
     * @throws LedgerValidationException
     */
    public static Set<JournalLine> buildJournalLines(Policy policy, MoneyProvision payment, JournalLineType lineType, Party debitParty, Party creditParty) throws LedgerValidationException {
        Set<JournalLine> lines = new HashSet<>();

        CurrencyAmount total = payment.getAmount();

        lines.add(new JournalLineBuilder().
                debit().
                ledger(ledgerFor(debitParty, total.getCurrency())).
                by(total).
                ofType(lineType).
                using(payment.getPaymentMethod()).
                build());

        lines.add(new JournalLineBuilder().
                credit().
                ledger(ledgerFor(creditParty, total.getCurrency())).
                by(total).
                ofType(lineType).
                using(payment.getPaymentMethod()).
                build());

        return lines;
    }
}
