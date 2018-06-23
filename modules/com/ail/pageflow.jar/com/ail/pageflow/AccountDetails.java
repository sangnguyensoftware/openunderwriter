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
import static com.google.common.base.Strings.isNullOrEmpty;

import java.io.IOException;
import java.time.Period;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.Type;
import com.ail.financial.CurrencyAmount;
import com.ail.financial.PaymentSchedule;
import com.ail.financial.ledger.Account;
import com.ail.financial.ledger.JournalLine;
import com.ail.financial.service.ActualizeMoneyProvisionsService.ActualizeMoneyProvisionsCommand;
import com.ail.financial.service.CalculateAccountBalanceService.CalculateAccountBalanceCommand;

/**
 * Page element to display the details of a party's account.
 */
public class AccountDetails extends PageElement {
	private static final long serialVersionUID = -4810599045554021748L;

	private String accountBinding;

	private String scheduleBinding;

    private String period = "P3M";

    private boolean showSummary = true;

    private boolean showNormalizedSchedule = false;

    private boolean showActualizedSchedule = false;

    private boolean showStatement = false;

	public AccountDetails() {
		super();
	}

	public String getAccountBinding() {
        return accountBinding;
    }

    public void setAccountBinding(String accountBinding) {
        this.accountBinding = accountBinding;
    }

    public String getScheduleBinding() {
        return scheduleBinding;
    }

    public void setScheduleBinding(String scheduleBinding) {
        this.scheduleBinding = scheduleBinding;
    }

    public boolean isShowNormalizedSchedule() {
        return showNormalizedSchedule;
    }

    public void setShowNormalizedSchedule(boolean showNormalizedSchedule) {
        this.showNormalizedSchedule = showNormalizedSchedule;
    }

    public boolean isShowSchedule() {
        return showNormalizedSchedule | showActualizedSchedule;
    }

    public boolean isShowStatement() {
        return showStatement;
    }

    public void setShowStatement(boolean showStatement) {
        this.showStatement = showStatement;
    }

    public boolean isShowActualizedSchedule() {
        return showActualizedSchedule;
    }

    public void setShowActualizedSchedule(boolean showActualizeSchedule) {
        this.showActualizedSchedule = showActualizeSchedule;
    }

    public boolean isShowSummary() {
        return showSummary;
    }

    public void setShowSummary(boolean showSummary) {
        this.showSummary = showSummary;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public List<Account> accounts(Type model) {
        List<Account> accounts = new ArrayList<>();

        if (!isNullOrEmpty(accountBinding)) {
            @SuppressWarnings("unchecked")
            List<Account> res = (List<Account>) getCoreProxy().query("get.accounts.for.party", fetchBoundObject(accountBinding, model));

            accounts.addAll(res);
        }

        return accounts;
    }

    public CurrencyAmount balance(Account account) throws BaseException {
        CalculateAccountBalanceCommand cabc = getCoreProxy().newCommand(CalculateAccountBalanceCommand.class);
        cabc.setAccountArg(account);
        cabc.invoke();
        return cabc.getBalanceRet();
    }

    public List<JournalLine> journalLines(Account account) throws BaseException {
        List<JournalLine> lines = new ArrayList<>();

        @SuppressWarnings("unchecked")
        List<JournalLine> foundLines = (List<JournalLine>) getCoreProxy().query("journalLines.for.account.between.two.dates", account, statementStartDate(), statementEndDate());
        lines.addAll(foundLines);

        return lines;
    }

	private Date statementEndDate() {
        return new Date();
    }

    private Date statementStartDate() {
        Period period = Period.parse(this.period);

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());

        cal.add(Calendar.DATE, -period.getDays());
        cal.add(Calendar.MONTH, -period.getMonths());
        cal.add(Calendar.YEAR, -period.getYears());

        return cal.getTime();
    }

    private Date scheduleEndDate() {
        Period period = Period.parse(this.period);

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());

        cal.add(Calendar.DATE, period.getDays());
        cal.add(Calendar.MONTH, period.getMonths());
        cal.add(Calendar.YEAR, period.getYears());

        return cal.getTime();
    }

    private Date scheduleStartDate() {
        return new Date();
    }

    public PaymentSchedule schedule(Type model) throws BaseException {
        try {
            PaymentSchedule schedule = (PaymentSchedule)fetchBoundObject(scheduleBinding, model);

            if (schedule != null && isShowActualizedSchedule()) {
                ActualizeMoneyProvisionsCommand ampc = CoreContext.getCoreProxy().newCommand(ActualizeMoneyProvisionsCommand.class);
                ampc.setPeriodStartDateArg(scheduleStartDate());
                ampc.setPeriodEndDateArg(scheduleEndDate());
                ampc.setNormalizedMoneyProvisionsArg(schedule.getMoneyProvision());
                ampc.invoke();
                schedule=new PaymentSchedule(ampc.getActualizedMoneyProvisionsRet(), "Actualized payment schedule");
            }

            return schedule;
        }
        catch(ClassCastException e) {
            throw new RenderingError("Schedule binding ("+scheduleBinding+") does not resolve to an instance of PaymentSchedule.");
        }
    }

    @Override
    public Type renderResponse(Type model) throws IllegalStateException, IOException {
        return executeTemplateCommand("AccountDetails", model);
    }
}
