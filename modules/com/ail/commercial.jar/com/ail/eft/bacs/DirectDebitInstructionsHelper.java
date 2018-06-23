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

import static com.ail.eft.bacs.Functions.isValidDDITransactionCode;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang.StringUtils;

import com.ail.core.CoreProxy;
import com.github.ffpojo.FFPojoHelper;
import com.github.ffpojo.exception.FFPojoException;

/**
 * Turns a list of {@link Record}s into a list of DDI instruction strings
 */
public class DirectDebitInstructionsHelper {

    private ServiceUser serviceUser;
    private String processingDate;

    public DirectDebitInstructionsHelper() {
    }

    public ServiceUser getServiceUser() {
        return serviceUser;
    }

    public void setServiceUser(ServiceUser serviceUser) {
        this.serviceUser = serviceUser;
    }

    public String getProcessingDate() {
        return processingDate;
    }

    public void setProcessingDate(String processingDate) {
        this.processingDate = processingDate;
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
        if (StringUtils.isBlank(getProcessingDate())) {
            throw new IllegalArgumentException("processingDate must be set!");
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
        if (!isValidDDITransactionCode(record.getTransactionCode())) {
            throw new IllegalArgumentException("record transactionCode must be set and one of 0N, OC, or 0S!");
        }
    }

    public Collection<String> getLines(Collection<Record> records) throws IllegalArgumentException {
        validateServiceUser();

        Collection<String> lines = new ArrayList<>();

        try {
            for (Record record : records) {
                lines.add(FFPojoHelper.getInstance().parseToText(getDDIInstruction(record)));
            }
        } catch (FFPojoException e) {
            new CoreProxy().logError("Failed to generate DDI Instruction lines", e);
        }

        return lines;
    }

    private DirectDebitInstruction getDDIInstruction(Record record) {
        validatePayment(record);

        DirectDebitInstruction ddii = new DirectDebitInstruction();

        ddii.setOriginatingSortCode(StringUtils.remove(getServiceUser().getSortCode(), "-"));
        ddii.setOriginatingAccountNumber(getServiceUser().getAccountNumber());
        ddii.setOriginatorName(getServiceUser().getName());
        ddii.setOriginatorReference(record.getReference());
        ddii.setTransactionCode(record.getTransactionCode().getCode());

        ddii.setPaymentAccountName(record.getAccountName());
        ddii.setPaymentSortingCode(StringUtils.remove(record.getSortCode(), "-"));
        ddii.setPaymentAccountNumber(record.getAccountNumber());

        ddii.setProcessingDate(getProcessingDate());

        return ddii;
    }
}
