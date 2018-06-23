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
package com.ail.pageflow;

import static com.ail.core.CoreContext.getCoreProxy;
import static com.ail.core.DateTimeUtils.dateToLocalDate;
import static com.ail.core.DateTimeUtils.localDateToDate;
import static com.ail.core.Functions.isEmpty;
import static com.ail.financial.FinancialFrequency.MONTHLY;
import static com.ail.financial.FinancialFrequency.ONE_TIME;
import static com.ail.financial.FinancialFrequency.QUARTERLY;
import static com.ail.financial.FinancialFrequency.SEMESTERLY;
import static com.ail.financial.FinancialFrequency.UNDEFINED;
import static com.ail.financial.FinancialFrequency.WEEKLY;
import static com.ail.financial.FinancialFrequency.YEARLY;
import static com.ail.pageflow.PageFlowContext.getPolicy;
import static com.ail.pageflow.util.Functions.addError;
import static com.ail.pageflow.util.Functions.simpleDateFormatToJqueryDateFormat;
import static com.google.common.base.Strings.isNullOrEmpty;
import static java.math.BigDecimal.ZERO;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import com.ail.core.Attribute;
import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.Type;
import com.ail.core.context.RequestWrapper;
import com.ail.financial.Currency;
import com.ail.financial.CurrencyAmount;
import com.ail.financial.FinancialFrequency;
import com.ail.financial.MoneyProvision;
import com.ail.financial.MoneyProvisionPurpose;
import com.ail.financial.MoneyProvisionStatus;
import com.ail.financial.PaymentMethod;
import com.ail.financial.PaymentSchedule;
import com.ail.financial.ledger.Account;
import com.ail.financial.service.ActualizeMoneyProvisionsService.ActualizeMoneyProvisionsCommand;
import com.ail.financial.service.CalculateAccountBalanceService.CalculateAccountBalanceCommand;
import com.ail.party.Party;

/**
 * Page element to display the details of a money provision associated with a
 * policy, and allow the user to manipulate those details.
 *
 * 1) If "binding" evaluates to nothing: 2) If "type" is defined 3) Create an
 * instance of "type" and add it to "binding/..". 4) Else 5) Error 6) If
 * "binding" returns multiple matches: 7) Error
 *
 * 8) If accountBinding and amountBinding are both defined 9) Error 10) If
 * accountBinding is defined 11) Fetch the balance from the account for use as
 * payment amount. 12) If amountBinding is defined 13) Fetch the value of the
 * binding (CurrencyAmount) for use as payment amount. 14) If payment amount is
 * +ve 15) Error (handle credits here in future?)
 *
 * 8) Open the editor
 */
public class PaymentScheduleDetails extends PageElement {
    private static final String SCRATCH_MONEY_PROVISION = "moneyProvision";

    private static final long serialVersionUID = -4874299081734021748L;

    /** Optionally select an account to use as the amount due. */
    private String accountBinding;

    /** Optionally select an amount to use as the amount due. */
    private String amountBinding;

    /** Optionally the element itself defines the amount */
    private String amount;

    /**
     * Payment frequency options as a comma separated list. Defaults to
     * ONE_TIME,MONTHLY,YEARLY. Any valid values for
     * {@link FinancialFrequency} are supported.
     */
    private String frequencyOptions = ONE_TIME + "," + WEEKLY + "," + MONTHLY + ","+ QUARTERLY + ","+ SEMESTERLY + "," + YEARLY;

    /** Minimum number of payments in a repeating series. Defaults to 2. */
    private int minimumNumberOfPayments = 2;

    /** Maximum number of payments that valid. Defaults to 12. */
    private int maximumNumberOfPayments = 12;

    /**
     * Optionally defines the name of a type to be created if <code>binding</code>
     * does not resolve to an instance of MoneyProvision.
     */
    private String type;

    /**
     * The purpose for the payment schedule. This has the effect of filtering the
     * payment methods on the policy.
     */
    private MoneyProvisionPurpose purpose;

    /**
     * A comma separated list of payment methods types that are allowed for this
     * money provision. Each method must be the simple classname of a subclass or
     * {@link PaymentMethod}. For example" "DirectDebit,PaymentCard,PayPal". If
     * empty or null all payment methods are allowed.
     */
    private String allowablePaymentMethods;

    public PaymentScheduleDetails() {
        super();
        this.binding = "/client";
    }

    public String getAccountBinding() {
        return accountBinding;
    }

    public void setAccountBinding(String accountBinding) {
        this.accountBinding = accountBinding;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAllowablePaymentMethods() {
        return allowablePaymentMethods;
    }

    public void setAllowablePaymentMethods(String allowablePaymentMethods) {
        this.allowablePaymentMethods = allowablePaymentMethods;
    }

    public String getAmountBinding() {
        return amountBinding;
    }

    public void setAmountBinding(String amountBinding) {
        this.amountBinding = amountBinding;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public MoneyProvisionPurpose getPurpose() {
        return purpose;
    }

    public void setPurpose(MoneyProvisionPurpose purpose) {
        this.purpose = purpose;
    }

    public String getFrequencyOptions() {
        return frequencyOptions;
    }

    public void setFrequencyOptions(String frequencyOptions) {
        this.frequencyOptions = frequencyOptions;
    }

    public int getMinimumNumberOfPayments() {
        return minimumNumberOfPayments;
    }

    public void setMinimumNumberOfPayments(int minimumNumberOfPayments) {
        this.minimumNumberOfPayments = minimumNumberOfPayments;
    }

    public int getMaximumNumberOfPayments() {
        return maximumNumberOfPayments;
    }

    public void setMaximumNumberOfPayments(int maximumNumberOfPayments) {
        this.maximumNumberOfPayments = maximumNumberOfPayments;
    }

    public String patternToJqueryDateFormat(String attrPattern) {
        return simpleDateFormatToJqueryDateFormat(attrPattern);
    }

    public CurrencyAmount selectedAmount() throws BaseException {
        if (hasScratchMoneyProvision()) {
            return getScratchMoneyProvision().getAmount();
        }
        else {
            return determineAmountDue();
        }
    }

    public FinancialFrequency selectedFrequency() {
        if (hasScratchMoneyProvision()) {
            return getScratchMoneyProvision().getFrequency();
        }
        else {
            return UNDEFINED;
        }
    }

    public PaymentMethod selectedPaymentMethod() {
        if (hasScratchMoneyProvision()) {
            return getScratchMoneyProvision().getPaymentMethod();
        }
        else {
            return null;
        }
    }

    public Date selectedPaymentDate() {
        if (hasScratchMoneyProvision()) {
            return getScratchMoneyProvision().getPaymentsStartDate();
        }
        else {
            return new Date();
        }
    }

    public int selectedPaymentDay() {
        if (hasScratchMoneyProvision()) {
            return getScratchMoneyProvision().getDay();
        }
        else {
            return 1;
        }
    }

    public int selectedNumberOfPayments() {
        if (hasScratchMoneyProvision()) {
            return getScratchMoneyProvision().getNumber();
        }
        else {
            return getMinimumNumberOfPayments();
        }
    }

    public List<PaymentMethod> determineMethodOptions() {
        Type model = PageFlowContext.getPolicy();
        List<PaymentMethod> methods = new ArrayList<>();

        Party party = (Party)fetchBoundObject(model, null);

        if (party != null) {
            methods.addAll(
                party.getPaymentMethod().stream().filter(
                        pm -> isEmpty(allowablePaymentMethods) || allowablePaymentMethods.contains(pm.getClass().getSimpleName())
                ).collect(Collectors.toList())
            );
        }

        return methods;
    }

    public CurrencyAmount determineAmountDue() {
        CurrencyAmount amountDue;

        Type model = PageFlowContext.getPolicy();

        if (isNullOrEmpty(amount) && isNullOrEmpty(accountBinding) && isNullOrEmpty(accountBinding)) {
            throw new RenderingError("'binding' did not resolve to a MoneyProvison and one of amountBinding or accountBinding was not specified.");
        }

        if (this.amount != null) {
            amountDue = new CurrencyAmount(this.amount);
        } else if (!isNullOrEmpty(amountBinding) && !isNullOrEmpty(accountBinding)) {
            throw new RenderingError("Only one of amountBinding & accountBinding can be specified.");
        } else if (!isNullOrEmpty(amountBinding)) {
            amountDue = (CurrencyAmount) fetchBoundObject(amountBinding, model);
        } else if (!isNullOrEmpty(accountBinding)) {
            @SuppressWarnings("unchecked")
            List<Account> res = (List<Account>) getCoreProxy().query("get.accounts.for.party", fetchBoundObject(accountBinding, model));

            Account account = res.get(0);

            CalculateAccountBalanceCommand cabc = getCoreProxy().newCommand(CalculateAccountBalanceCommand.class);
            cabc.setAccountArg(account);
            try {
                cabc.invoke();
            } catch (BaseException e) {
                throw new RenderingError("Failed to calculate account balance for acount: "+accountBinding, e);
            }
            amountDue = new CurrencyAmount(cabc.getBalanceRet().getAmount().negate(), cabc.getBalanceRet().getCurrency());
        } else {
            throw new RenderingError("Failed to find a CurrencyAmount to render as payment amount.");
        }

        if (ZERO.compareTo(amountDue.getAmount()) >= 0) {
            return new CurrencyAmount(0, amountDue.getCurrency());
        }

        return amountDue;
    }

    private List<MoneyProvision> roundScratchIntoMoneyProvisions() {
        List<MoneyProvision> ret = new ArrayList<>();

        MoneyProvision source = getScratchMoneyProvision();

        if (source.getFrequency() == ONE_TIME || source.getNumber() == 1) {
            ret.add(source);
        } else {
            BigDecimal totalAmount = source.getAmount().getAmount();
            Currency currency = source.getAmount().getCurrency();
            int numberOfPayments = source.getNumber();
            LocalDate paymentDate = dateToLocalDate(source.getPaymentsStartDate());

            BigDecimal divisor = new BigDecimal(source.getNumber());
            BigDecimal basic = totalAmount.divide(divisor, currency.getFractionDigits(), RoundingMode.HALF_UP);
            BigDecimal remainder = totalAmount.subtract(basic.multiply(divisor));

            if (remainder.compareTo(ZERO) != 0) {
                MoneyProvision initial = new MoneyProvision();
                initial.setFrequency(ONE_TIME);
                initial.setAmount(new CurrencyAmount(basic.add(remainder), currency));
                initial.setPaymentMethod(source.getPaymentMethod());
                initial.setDay(source.getDay());
                initial.setPaymentsStartDate(localDateToDate(paymentDate));
                initial.setPaymentsEndDate(localDateToDate(paymentDate));
                initial.setPurpose(purpose);
                initial.setNumber(1); // Only ever one odd payment
                ret.add(initial);
                numberOfPayments--;
                paymentDate = paymentDate.plus(source.getFrequency().toPeriod().get());
            }

            MoneyProvision repeating = new MoneyProvision();
            repeating.setFrequency(source.getFrequency());
            repeating.setAmount(new CurrencyAmount(basic, currency));
            repeating.setPaymentMethod(source.getPaymentMethod());
            repeating.setDay(source.getDay());
            repeating.setNumber(numberOfPayments);
            repeating.setPurpose(purpose);
            repeating.setPaymentsStartDate(localDateToDate(paymentDate));
            paymentDate = paymentDate.plus(source.getFrequency().toPeriod().get().multipliedBy(numberOfPayments - 1));
            repeating.setPaymentsEndDate(localDateToDate(paymentDate));

            ret.add(repeating);
        }

        return ret;
    }

    public List<MoneyProvision> determinActualizedPayments() throws BaseException {
        List<MoneyProvision> ret = new ArrayList<>();

        try {
            if (getScratchMoneyProvision().getPaymentMethod() != null) {
                List<MoneyProvision> source = roundScratchIntoMoneyProvisions();
                ActualizeMoneyProvisionsCommand ampc = getCoreProxy().newCommand(ActualizeMoneyProvisionsCommand.class);
                ampc.setNormalizedMoneyProvisionsArg(source);

                Date startDate = source.stream().map(mp -> mp.getPaymentsStartDate()).min(Date::compareTo).get();
                ampc.setPeriodStartDateArg(startDate);

                Date endDate = source.stream().filter(mp -> mp.getPaymentsEndDate() != null).map(mp -> mp.getPaymentsEndDate()).max(Date::compareTo).orElse(null);
                ampc.setPeriodEndDateArg(endDate);

                ampc.invoke();
                ret.addAll(ampc.getActualizedMoneyProvisionsRet());
            }
        }
        catch(Throwable t) {
            CoreContext.getCoreProxy().logWarning("determinActualizedPayments failed: "+t);
        }

        return ret;
    }

    @Override
    public Type applyRequestValues(Type model) {

        // If our condition isn't met, apply nothing.
        if (!conditionIsMet(model)) {
            return model;
        }

        MoneyProvision moneyProvision = getScratchMoneyProvision();

        RequestWrapper request = PageFlowContext.getRequestWrapper();

        try {
            Currency currency = moneyProvision.getAmount().getCurrency();
            String value = request.getParameter(encodeId("amount"));

            moneyProvision.setAmount(new CurrencyAmount(value, currency));
        }
        catch(Throwable e) {
            addError("amount", i18n("i18n_invalid_error"), moneyProvision);
        }

        try {
            String value = request.getParameter(encodeId("method"));
            moneyProvision.setPaymentMethod((PaymentMethod)getCoreProxy().queryUnique("get.paymentmethod.by.externalSystemId", value));
        }
        catch(Throwable e) {
            addError("method", i18n("i18n_invalid_error"), moneyProvision);
        }

        try {
            String value = request.getParameter(encodeId("frequency"));
            moneyProvision.setFrequency(FinancialFrequency.forName(value));
        }
        catch(Throwable e) {
            addError("frequency", i18n("i18n_invalid_error"), moneyProvision);
        }

        try {
            String value = request.getParameter(encodeId("date"));
            Date date = new SimpleDateFormat(Attribute.DEFAULT_DATE_PATTERN).parse(value);
            moneyProvision.setPaymentsStartDate(date);
        } catch (Throwable e) {
            addError("date", i18n("i18n_invalid_error"), moneyProvision);
        }

        if (ONE_TIME == moneyProvision.getFrequency()) {
            moneyProvision.setPaymentsEndDate(moneyProvision.getPaymentsStartDate());
            moneyProvision.setNumber(1);
            String value = request.getParameter(encodeId("number"));
        }
        else {
            try {
                String value = request.getParameter(encodeId("number"));
                moneyProvision.setNumber(Integer.parseInt(value));
            }
            catch(Throwable e) {
                addError("number", i18n("i18n_invalid_error"), moneyProvision);
            }

            try {
                String value = request.getParameter(encodeId("day"));
                moneyProvision.setDay(Integer.parseInt(value));
            }
            catch(Throwable e) {
                addError("day", i18n("i18n_invalid_error"), moneyProvision);
            }

            moneyProvision.alignDatesToDays();
        }

        return super.applyRequestValues(model);
    }

    @Override
    public boolean processValidations(Type model) {

        boolean errorFound = false;
        MoneyProvision moneyProvision = getScratchMoneyProvision();

        Calendar start = Calendar.getInstance();
        start.setTime(moneyProvision.getPaymentsStartDate());

        if (start.get(Calendar.DAY_OF_MONTH) > 28) {
            addError("date", i18n("i18n_invalid_error"), moneyProvision);
            errorFound = true;
        }

        if (ONE_TIME != moneyProvision.getFrequency()) {
             if (moneyProvision.getNumber() < minimumNumberOfPayments || moneyProvision.getNumber() > maximumNumberOfPayments) {
                addError("number", i18n("i18n_invalid_error"), moneyProvision);
                errorFound = true;
            }
        }

        return errorFound;
    }

    @Override
    public Type processActions(Type model) throws BaseException {
        if (calculateButtonPressed()) {
            PageFlowContext.flagActionAsProcessed();
        }

        if (saveButtonPressed()) {
            if (getPolicy().getPaymentDetails() == null) {
                getPolicy().setPaymentDetails(new PaymentSchedule());
            }

            List<MoneyProvision> newCaluculatedMoneyProvisions = roundScratchIntoMoneyProvisions();

            // If a MoneyProvision has MoneyProvisionStatus of NEW or REQUESTED, set the end date to the new start date
            // and create a new MP

            // now means start of today
            Date now = new Date();
            now.setHours(0);
            now.setMinutes(0);
            now.setSeconds(0);
            LocalDate localNow = dateToLocalDate(new Date());

            // The MP cannot be edited so set end date on the old value and add the new one
            boolean addNew = getPolicy().getPaymentDetails().getMoneyProvision().stream()
                                                                                .anyMatch(mp -> (MoneyProvisionStatus.AUTHORISED.equals(mp.getStatus()) ||
                                                                                                 MoneyProvisionStatus.CANCELLED.equals(mp.getStatus()) ||
                                                                                                 MoneyProvisionStatus.COMPLETE.equals(mp.getStatus())) &&
                                                                                                 mp.getPaymentsEndDate().after(now));

                Date newStartDate = newCaluculatedMoneyProvisions.get(0).getPaymentsStartDate();

                for (Iterator<MoneyProvision> it = getPolicy().getPaymentDetails().getMoneyProvision().listIterator(); it.hasNext();) {

                    MoneyProvision mp = it.next();
                    if ((MoneyProvisionStatus.AUTHORISED.equals(mp.getStatus()) ||
                         MoneyProvisionStatus.CANCELLED.equals(mp.getStatus()) ||
                         MoneyProvisionStatus.COMPLETE.equals(mp.getStatus())) &&
                         mp.getPaymentsEndDate() != null &&
                         (dateToLocalDate(mp.getPaymentsEndDate()).isEqual(localNow) || dateToLocalDate(mp.getPaymentsEndDate()).isAfter(localNow))) {
                        mp.setPaymentsEndDate(newStartDate);
                    }
                    if ((MoneyProvisionStatus.NEW.equals(mp.getStatus()) ||
                         MoneyProvisionStatus.REQUESTED.equals(mp.getStatus()))) {
                        boolean found = false;
                        // Find matching MP Frequency in scratch and get start date
                        for (MoneyProvision match : newCaluculatedMoneyProvisions) {
                            if (mp.getFrequency().equals(match.getFrequency())) {
                                mp.setAmount(match.getAmount());
                                mp.setPaymentMethod(match.getPaymentMethod());
                                mp.setFrequency(match.getFrequency());
                                mp.setPaymentsStartDate(match.getPaymentsStartDate());
                                mp.setNumber(match.getNumber());
                                found = true;
                                break;
                            }
                        }
                        if (found == false) {
                            // removed (now no rounding)
                            it.remove();
                        }
                    }
                }
                if (addNew || getPolicy().getPaymentDetails().getMoneyProvision().size() == 0) {
                  getPolicy().getPaymentDetails().getMoneyProvision().addAll(newCaluculatedMoneyProvisions);
                } else {
                    // Check to add new MP (now rounding)
                    for (MoneyProvision match : newCaluculatedMoneyProvisions) {
                        boolean found = false;

                        for (MoneyProvision mp : getPolicy().getPaymentDetails().getMoneyProvision()) {
                            if (mp.getFrequency().equals(match.getFrequency())) {
                                found = true;
                                break;
                            }
                        }
                        if (found == false) {
                            getPolicy().getPaymentDetails().getMoneyProvision().add(match);
                        }
                    }

                }
            PageFlowContext.flagActionAsProcessed();
        }

        return super.processActions(model);
    }

    private boolean calculateButtonPressed() {
        return (this.getClass().getSimpleName() + "-calculate").equals(PageFlowContext.getRequestedOperation());
    }

    private boolean saveButtonPressed() {
        return (this.getClass().getSimpleName() + "-save").equals(PageFlowContext.getRequestedOperation());
    }

    @Override
    public Type renderResponse(Type model) throws IllegalStateException, IOException {
        return executeTemplateCommand("PaymentScheduleDetails", model);
    }

    public boolean hasScratchMoneyProvision() {
        return PageFlowContext.getRequestWrapper().getServletRequest().getAttribute(SCRATCH_MONEY_PROVISION) != null;
    }

    public MoneyProvision getScratchMoneyProvision() {
        MoneyProvision moneyProvision;

        RequestWrapper request = PageFlowContext.getRequestWrapper();

        moneyProvision = (MoneyProvision) request.getServletRequest().getAttribute(SCRATCH_MONEY_PROVISION);

        if (moneyProvision == null) {
            moneyProvision = new MoneyProvision();
            moneyProvision.setPaymentsStartDate(new Date());
            moneyProvision.setNumber(1);
            moneyProvision.setAmount(determineAmountDue());
            moneyProvision.setFrequency(ONE_TIME);
            moneyProvision.setPurpose(purpose);

            request.getServletRequest().setAttribute(SCRATCH_MONEY_PROVISION, moneyProvision);
        }

        return moneyProvision;
    }
}
