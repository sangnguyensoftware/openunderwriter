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

import static com.ail.core.Functions.isEmpty;
import static com.ail.financial.MoneyProvisionPurpose.PREMIUM;
import static com.ail.pageflow.PageFlowContext.getRequestedOperation;
import static com.ail.pageflow.PageFlowContext.getRequestedOperationId;
import static com.ail.pageflow.util.Functions.addError;
import static com.ail.pageflow.util.Functions.hasErrorMarkers;
import static com.ail.pageflow.util.Functions.removeErrorMarkers;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Period;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.ail.core.BaseException;
import com.ail.core.Type;
import com.ail.core.context.RequestWrapper;
import com.ail.financial.MoneyProvision;
import com.ail.financial.PaymentHoliday;
import com.ail.financial.PaymentSchedule;
import com.ail.insurance.policy.Policy;

public class PaymentHolidayDetails extends PageContainer {
	private static final long serialVersionUID = -4810599045554021748L;
    /**
     * The name of the Type describing a payment holiday. The type must be recognizable by {@link com.ail.core.CoreProxy#newType(String) Core.newType(String)}.
     * The widget will create instances of this type in response to the 'add' link being selected. If the {@link #isAddAndDeleteEnabled() addDeleteEnabled}
     * property is false, this property may be null as it will never be referred to.
     */
    private String type;

    /** Set to true if the user should be able to add and remove payment holidays */
    private boolean addAndDeleteEnabled = true;

    private boolean readOnly = false;

    private boolean showCurrent = true;

    private boolean showHistory = true;

    private boolean showNotes = true;

    private boolean initialiseNotes = false;

    private NoteDetails noteDetails = new NoteDetails("payment_holiday_note_types");

    @Override
    public Type processActions(Type model) throws BaseException {
        if (isReadOnly() || !conditionIsMet(model)) {
            return model;
        }

        if (isShowNotes()) {
            Policy policy = (Policy)model;

            for (PaymentHoliday holiday : policy.getPaymentHoliday()) {
                noteDetails.processActions(holiday);
            }
        }

        handlePaymentHolidayAdd(model);

        handlePaymentHolidayDelete(model);

        return model;
    }

    @Override
    public Type applyRequestValues(Type model) {
        if (isReadOnly() || !conditionIsMet(model)) {
            return model;
        }

        if (isShowNotes()) {
            Policy policy = (Policy)model;

            for (PaymentHoliday holiday : policy.getPaymentHoliday()) {
                noteDetails.applyRequestValues(holiday);
            }
        }

        handlePaymentHolidayUpdates(model);

        return model;
    }

    @Override
    public boolean processValidations(Type model) {
        if (isReadOnly() || !conditionIsMet(model)) {
            return false;
        }

        AtomicBoolean errorsFound = new AtomicBoolean(false);

        Policy policy = (Policy)model;

        int holidayCounter = 0;
        List<PaymentHoliday> holidays = policy.getPaymentHolidaySortedByCreatedDate();
        int holidaysSize = holidays.size();

        for (PaymentHoliday holiday : holidays) {
            holidayCounter += 1;

            if (isShowNotes()) {
                noteDetails.processValidations(holiday);
            }

            if (holiday.getStartDate() == null && !hasErrorMarker("startDate", holiday)) {
                addError("startDate", "i18n_required_error", holiday);
            }

            if (holiday.getStartDate() != null && holiday.getEndDate() !=null && holiday.getStartDate().after(holiday.getEndDate())) {
                addError("startDate", "i18n_invalid_error", holiday);
                addError("endDate", "i18n_invalid_error", holiday);
            }

            int totalDurationCounter = 0;

            for (PaymentHoliday that : policy.getPaymentHoliday()) {
                Period duration = holiday.durationPeriod();
                totalDurationCounter += duration==null ? 0 : duration.getMonths();
                if (that == holiday) {
                    continue;
                }
                if (holiday.overlapsWith(that)) {
                    if (!hasErrorMarker("overlap", holiday)) {
                        addError("overlap", i18n("i18n_payment_holiday_date_overlap_error"), holiday);
                    }
                    if (!hasErrorMarker("overlap", that)) {
                        addError("overlap", i18n("i18n_payment_holiday_date_overlap_error"), that);
                    }
                }
            }

            Period duration = holiday.durationPeriod();
            if (holiday.durationPeriod() != null) {
                String lifetimeMonthsString = PageFlowContext.getCoreProxy().getGroup("PaymentHoliday").findParameterValue("lifetimeMonthsPermitted", "");
                int lifetimeMonths = lifetimeMonthsString.isEmpty()?0:Integer.parseInt(lifetimeMonthsString);
                if(lifetimeMonths!=0 && lifetimeMonths<totalDurationCounter) {
                    addError("duration.lifetime", i18n("i18n_max_lifetime_error", lifetimeMonths), holiday);
                }

                String minMonthsString = PageFlowContext.getCoreProxy().getGroup("PaymentHoliday").findParameterValue("minimumMonthsPermitted", "");
                int minMonths = minMonthsString.isEmpty()?0:Integer.parseInt(minMonthsString);
                if(minMonths!=0 && minMonths>duration.getMonths()) {
                    addError("duration.min", i18n("i18n_min_consecutive_error", minMonthsString), holiday);
                }

                String maxMonthsString = PageFlowContext.getCoreProxy().getGroup("PaymentHoliday").findParameterValue("consectutiveMonthsPermitted", "");
                int maxMonths = maxMonthsString.isEmpty()?0:Integer.parseInt(maxMonthsString);
                if(maxMonths!=0 && maxMonths<duration.getMonths()) {
                    addError("duration.consecutive", i18n("i18n_max_consecutive_error", maxMonthsString), holiday);
                }
            }

            // only implement thse validaiton rules if it's the last created holiday this is the one that's just been added
            if (holidaysSize == holidayCounter) {
                // don't allow holidays to be added if a claim has been raised
                if (policy.hasActiveClaim()) {
                    addError("general.claim", i18n("i18n_payment_holiday_active_claim_error"), holiday);
                }

                // is the policy in arrears?
                if (policy.isInArrears()) {
                    // if the start date is before the arrears start date raise an error
                    if (holiday.getStartDate().before(policy.getArrearsStartDate())) {
                        addError("startDate", i18n("i18n_payment_holiday_start_date_before_arrears"), holiday);
                    }
                }
                else if (holiday.getStartDate() != null){
                    // the policy is not in arrears so don't allow payment holidays in the past
                    Calendar todayCal = Calendar.getInstance();
                    Calendar startDateCal = Calendar.getInstance();
                    startDateCal.setTime(holiday.getStartDate());

                    int startDateMonth = startDateCal.get(Calendar.MONTH);
                    int startDateYear = startDateCal.get(Calendar.YEAR);
                    int thisMonth = todayCal.get(Calendar.MONTH);
                    int thisYear = todayCal.get(Calendar.YEAR);

                    // is the payment holiday in the future?
                    if (startDateMonth < thisMonth && startDateYear <= thisYear) {
                        addError("startDate", i18n("i18n_payment_holiday_start_date_in_past"), holiday);
                    }

                    // if the start date is this month
                    if (startDateMonth == thisMonth && startDateYear == thisYear) {
                        // check to make sure the collection date hasn't past for this month or isn't due in the next 3 days
                        // if it is, raise an error
                        PaymentSchedule paymentSchedule = policy.getPaymentDetails();
                        List<MoneyProvision> moneyProvisions = paymentSchedule.getMoneyProvision();

                        for (MoneyProvision moneyProvision : moneyProvisions) {
                            if (moneyProvision.getPurpose() == PREMIUM) {
                                if (moneyProvision.getDay() <= todayCal.get(Calendar.DAY_OF_MONTH) + 3) {
                                    addError("startDate", i18n("i18n_payment_holiday_start_date_before_months_payment"), holiday);
                                }
                            }
                        }
                    }
                }
            }

            if (hasErrorMarkers(holiday)) {
                errorsFound.set(true);
            }
        }

        return errorsFound.get();
    }

    @Override
    public Type renderResponse(Type model) throws IllegalStateException, IOException {
        return executeTemplateCommand("PaymentHolidayDetails", model);
    }

    @Override
    public void applyElementId(String basedId) {
        if (noteDetails != null) {
            noteDetails.applyElementId(basedId + ID_SEPARATOR + "notes");
        }
        super.applyElementId(basedId);
    }

    private void handlePaymentHolidayUpdates(Type model) {

        Policy policy=(Policy)model;

        RequestWrapper request = PageFlowContext.getRequestWrapper();

        for(int i=0 ; i<policy.getPaymentHoliday().size() ; i++) {
            PaymentHoliday holiday = policy.getPaymentHoliday().get(i);

            removeErrorMarkers(holiday);

            String startDateString = request.getParameter(encodeId("/paymentHoliday[" + (i + 1) + "]/startDate"));
            String endDateString = request.getParameter(encodeId("/paymentHoliday[" + (i + 1) + "]/endDate"));

            if (startDateString != null && startDateString.length() != 0) {
                try {
                    Date startDate = new SimpleDateFormat("yyyy-MM-dd").parse(startDateString);
                    holiday.setStartDate(startDate);
                } catch (ParseException e) {
                    addError("startDateInvalid", "i18n_invalid_error", holiday);
                }
            }

            if (endDateString != null && endDateString.length() != 0) {
                try {
                    Date endDate = new SimpleDateFormat("yyyy-MM-dd").parse(endDateString);
                    holiday.setEndDate(endDate);
                } catch (ParseException e) {
                    addError("endDateInvalid", "i18n_invalid_error", holiday);
                }
            }

            // Check if the title or body is set for this PH
            if (getRequestedOperationId() != null && getRequestedOperationId().equals(getId())) {

                String titleId = request.getParameter(encodeId("/paymentHoliday[" + (i + 1) + "]/titleId"));
                String bodyId = request.getParameter(encodeId("/paymentHoliday[" + (i + 1) + "]/bodyId"));
                if (titleId != null && titleId.length() > 0 && bodyId != null && bodyId.length() > 0) {
                    try {
                        // Add back in to request params
                        request.getParameterMap().put(encodeId("titleId"), new String[] {titleId});
                        request.getParameterMap().put(encodeId("bodyId"), new String[] {bodyId});

                        String labelId = request.getParameter(encodeId("/paymentHoliday[" + (i + 1) + "]/labelId"));
                        request.getParameterMap().put(encodeId("labelId"), new String[] {labelId});

                        noteDetails.setId(getId());
                        noteDetails.applyRequestValues(holiday);
                        noteDetails.processActions(holiday);
                    } catch (BaseException e) {
                        addError("noteInvalid", "i18n_invalid_error", holiday);
                    }
                }
            }
        }

        if (getRequestedOperationId() != null && getRequestedOperationId().equals(getId())) {
            // Check to create a new entry if start and end dates are set
            String newStartDateId = request.getParameter(encodeId("startDateId"));
            String newEndDateId = request.getParameter(encodeId("endDateId"));
            String newDurationId = request.getParameter(encodeId("durationId"));

            boolean validNewStartDateId = newStartDateId != null && newStartDateId.length() > 0;
            boolean validNewEndDateId = newEndDateId != null && newEndDateId.length() > 0;
            boolean validNewDurationId = newDurationId != null && newDurationId.length() > 0;
            boolean validDatesPair = (validNewStartDateId && validNewDurationId) || (validNewStartDateId && validNewEndDateId);

            PaymentHoliday paymentHoldiday = (PaymentHoliday)PageFlowContext.getCoreProxy().newType(type, PaymentHoliday.class);

            // if a start, end and duration specified raise an error
            if (validNewStartDateId && validNewEndDateId && validNewDurationId) {
                addError("datesInvalid", "i18n_invalid_error", paymentHoldiday);
            }
            else {
                Date startDate = null;

                // if either a valid start and duration or valid start and end date, create a start date
                if (validDatesPair){
                    try {
                        startDate = new SimpleDateFormat("yyyy-MM-dd").parse(newStartDateId);
                        paymentHoldiday.setStartDate(startDate);
                    } catch (ParseException e) {
                        addError("startDateInvalid", "i18n_invalid_error", paymentHoldiday);
                    }
                }

                // if a start and end date pair add the end date
                if (validNewStartDateId && validNewEndDateId) {
                    try {
                        Date endDate = new SimpleDateFormat("yyyy-MM-dd").parse(newEndDateId);
                        paymentHoldiday.setEndDate(endDate);
                    } catch (ParseException e) {
                        addError("endDateInvalid", "i18n_invalid_error", paymentHoldiday);
                    }
                }

                // if a start and duration pair calculate the end date
                if (validNewStartDateId && validNewDurationId){
                    try {
                        int duration = Integer.parseInt(newDurationId);

                        Calendar startDateCal = Calendar.getInstance();
                        startDateCal.setTime(startDate);
                        // add the months specified in the duraiton
                        startDateCal.add(Calendar.MONTH, duration);
                        // minus one day
                        startDateCal.add(Calendar.DATE, -1);

                        Date endDate = startDateCal.getTime();
                        paymentHoldiday.setEndDate(endDate);

                    } catch (NumberFormatException e){
                        addError("durationInvalid", "i18n_invalid_error", paymentHoldiday);
                    }
                }

                // finally add common functionality
                if (validDatesPair) {
                    // add notes
                    try {
                        noteDetails.setId(getId());
                        noteDetails.applyRequestValues(paymentHoldiday);
                        noteDetails.processActions(paymentHoldiday);
                    } catch (BaseException e) {
                        addError("noteInvalid", "i18n_invalid_error", paymentHoldiday);
                    }

                    // add holiday to the policy
                    policy.getPaymentHoliday().add(paymentHoldiday);
                }
            }
        }
    }

    private void handlePaymentHolidayAdd(Type model) {
        if (addButtonPressed()) {
            if (isEmpty(type)) {
                throw new RenderingError("PaymentHolidayDetails PageFlow element does not define a value for 'type'");
            }
            Policy policy = (Policy)model;
            policy.getPaymentHoliday().add(PageFlowContext.getCoreProxy().newType(type, PaymentHoliday.class));
            PageFlowContext.getCoreProxy().flush();
            PageFlowContext.flagActionAsProcessed();
        }
    }

	private void handlePaymentHolidayDelete(Type model) {
        if (deleteButtonPressed()) {
            Policy policy = (Policy)model;

            int row = Integer.parseInt((String)PageFlowContext.getOperationParameters().get("row"));

            policy.getPaymentHoliday().remove(row-1);

            PageFlowContext.flagActionAsProcessed();
        }
    }

    private boolean addButtonPressed() {
        return (this.getClass().getSimpleName() + "-add").equals(getRequestedOperation()) && this.getId().equals(PageFlowContext.getRequestedOperationId());
    }

    private boolean deleteButtonPressed() {
        return (this.getClass().getSimpleName() + "-delete").equals(getRequestedOperation()) && this.getId().equals(PageFlowContext.getRequestedOperationId());
    }

    public boolean isAddAndDeleteEnabled() {
        return addAndDeleteEnabled;
    }

    public void setAddAndDeleteEnabled(boolean addAndDeleteEnabled) {
        this.addAndDeleteEnabled = addAndDeleteEnabled;
        getNoteDetails().setAddAndDeleteEnabled(addAndDeleteEnabled);;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        getNoteDetails().setReadOnly(readOnly);
    }

    public boolean isShowCurrent() {
        return showCurrent;
    }

    public void setShowCurrent(boolean showCurrent) {
        this.showCurrent = showCurrent;
    }

    public boolean isShowHistory() {
        return showHistory;
    }

    public void setShowHistory(boolean showHistory) {
        this.showHistory = showHistory;
    }

    public boolean isShowNotes() {
        return showNotes;
    }

    public void setShowNotes(boolean showNotes) {
        this.showNotes = showNotes;
    }

    public boolean getInitialiseNotes() {
        return initialiseNotes;
    }

    public void setInitialiseNotes(boolean initialiseNotes) {
        this.initialiseNotes = initialiseNotes;
    }


    /**
     * By default the Note Details is an instance of {@link NoteDetails} but this may be
     * overridden using this property.
     * @return NoteDetails
     */
    public NoteDetails getNoteDetails() {
        return noteDetails;
    }

    /**
     * @see #setNoteDetails()
     * @param noteDetails Note details
     */
    public void setNoteDetails(NoteDetails noteDetails) {
        this.noteDetails = noteDetails;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


}
