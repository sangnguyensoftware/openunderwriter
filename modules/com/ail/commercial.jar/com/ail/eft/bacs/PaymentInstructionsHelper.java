/* Copyright Applied Industrial Logic Limited 2002. All rights Reserved */
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
package com.ail.eft.bacs;

import static com.ail.eft.bacs.Functions.formatJulianDay;
import static com.ail.eft.bacs.Functions.isValidPaymentTransactionCode;
import static com.ail.eft.bacs.Functions.toPence;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang.StringUtils;

import com.ail.core.CoreProxy;
import com.github.ffpojo.FFPojoHelper;
import com.github.ffpojo.exception.FFPojoException;

/**
 *
 */
public class PaymentInstructionsHelper {

    private ServiceUser serviceUser;

    public PaymentInstructionsHelper() {
    }

    public ServiceUser getServiceUser() {
        return serviceUser;
    }

    public void setServiceUser(ServiceUser serviceUser) {
        this.serviceUser = serviceUser;
    }

    private void validateServiceUser() throws IllegalArgumentException {
        if (StringUtils.isBlank(getServiceUser().getSortCode())) {
            throw new IllegalArgumentException("serviceUser sortCode must be set!");
        }
        if (StringUtils.isBlank(getServiceUser().getAccountNumber())) {
            throw new IllegalArgumentException("serviceUser accountNumber must be set!");
        }
        if (StringUtils.isBlank(getServiceUser().getName())) {
            throw new IllegalArgumentException("serviceUser name must be set!");
        }
    }

    private void validatePayment(Record record) throws IllegalArgumentException {
        if (record == null) {
            throw new IllegalArgumentException("record must be set!");
        }
        if (StringUtils.isBlank(record.getAccountName())) {
            throw new IllegalArgumentException("record accountName must be set!");
        }
        if (StringUtils.isBlank(record.getSortCode())) {
            throw new IllegalArgumentException("record sortCode must be set!");
        }
        if (StringUtils.isBlank(record.getAccountNumber())) {
            throw new IllegalArgumentException("record accountNumber must be set!");
        }
        if (StringUtils.isBlank(record.getReference())) {
            throw new IllegalArgumentException("record reference must be set!");
        }
        if (!isValidPaymentTransactionCode(record.getTransactionCode())) {
            throw new IllegalArgumentException("record transactionCode must be set and one of 01, 17, 18, 19, Z4, Z5, or 99!");
        }
        if (record.getProcessingDate() == null) {
            throw new IllegalArgumentException("record processingDate must be set!");
        }
    }

    public Collection<String> getLines(Collection<Record> records) throws IllegalArgumentException {
        validateServiceUser();

        Collection<String> lines = new ArrayList<>();

        try {
            FFPojoHelper ffpojoHelper = FFPojoHelper.getInstance();
            for (Record record : records) {
                lines.add(ffpojoHelper.parseToText(getPaymentInstruction(record)));
            }
        } catch (FFPojoException e) {
            new CoreProxy().logError("Failed to generate Payment Instruction lines", e);
        }

        return lines;
    }

    private PaymentInstruction getPaymentInstruction(Record record) {
        validatePayment(record);

        PaymentInstruction pi = new PaymentInstruction();

        pi.setDestinationSortingCode(StringUtils.remove(record.getSortCode(), "-"));
        pi.setDestinationAccountNumber(record.getAccountNumber());
        pi.setTransactionCode(record.getTransactionCode().getCode());
        pi.setOriginatingSortCode(StringUtils.remove(getServiceUser().getSortCode(), "-"));
        pi.setOriginatingAccountNumber(getServiceUser().getAccountNumber());
        pi.setFreeFormat(formatJulianDay(record.getProcessingDate()));
        pi.setAmountInPence(toPence(record.getAmount()));
        pi.setServiceUserName(getServiceUser().getName());
        pi.setServiceUserReference(record.getReference());
        pi.setDestinationAccountName(record.getAccountName());

        return pi;
    }
}
