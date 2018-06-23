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

import static com.ail.eft.bacs.Functions.formatJulianYearDay;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
/**
 * Service to generate a Direct Debit Instruction (DDI) file for BACS. See package-info for details.
 */
@ServiceImplementation
public class GenerateDirectDebitInstructionsService extends Service<GenerateDirectDebitInstructionsService.GenerateDirectDebitInstructionsArgument> {
    private static final long serialVersionUID = 5529681194148500651L;

    /**
     * Interface defining the arguments and returns associated with the Direct Debit Instruction service.
     */
    @ServiceArgument
    public interface GenerateDirectDebitInstructionsArgument extends Argument {

        /**
         * Whether or not to return the header and footer lines
         * @return
         */
        boolean getGenerateHeadersFootersArg();

        /**
         * @param generateHeadersFootersArg
         */
        void setGenerateHeadersFootersArg(boolean generateHeadersFootersArg);

        /**
         * The file submission type, single or multi
         * @return
         */
        FileSubmissionType getFileSubmissionTypeArg();

        /**
         * @param fileSubmissionType
         */
        void setFileSubmissionTypeArg(FileSubmissionType fileSubmissionType);

        /**
         * The processing day type, single or multi
         * @return
         */
        ProcessingDayType getProcessingDayTypeArg();

        /**
         * @param processingDayType
         */
        void setProcessingDayTypeArg(ProcessingDayType processingDayType);

        /**
         * The processing date of the file, typically today
         * @return
         */
        Date getProcessingDateArg();

        /**
         * @param processingDate
         */
        void setProcessingDateArg(Date processingDate);

        /**
         * The expiring date, today or later
         * @return
         */
        Date getExpiringDateArg();

        /**
         * @param expiringDate
         */
        void setExpiringDateArg(Date expiringDate);

        /**
         *
         * @return
         */
        String getSubmissionSerialNumberArg();

        /**
         * @param submissionSerialNumber
         */
        void setSubmissionSerialNumberArg(String submissionSerialNumber);

        /**
         *
         * @return
         */
        ServiceUser getServiceUserArg();

        /**
         * @param serviceUser
         */
        void setServiceUserArg(ServiceUser serviceUser);

        /**
         *
         * @return
         */
        String getNextFileSerialNumberArg();

        /**
         * @param nextFileSerialNumber
         */
        void setNextFileSerialNumberArg(String nextFileSerialNumber);

        /**
         *
         * @return
         */
        Collection<Record> getRecordsArg();

        /**
         * @param records
         */
        void setRecordsArg(Collection<Record> records);

        /**
         *
         * @return
         */
        Collection<String> getLinesRet();

        /**
         * @param lines
         */
        void setLinesRet(Collection<String> lines);
    }

    @ServiceCommand(defaultServiceClass=GenerateDirectDebitInstructionsService.class)
    public interface GenerateDirectDebitInstructionsCommand extends Command, GenerateDirectDebitInstructionsArgument {}

    @Override
    public void invoke() throws BaseException {
        try {
            if (args.getProcessingDateArg() == null) {
                throw new PreconditionException("args.getProcessingDateArg() == null");
            }
            if (args.getGenerateHeadersFootersArg() && args.getExpiringDateArg() == null) {
                throw new PreconditionException("args.getExpiringDateArg() == null");
            }
            if (args.getServiceUserArg() == null) {
                throw new PreconditionException("args.getServiceUserArg() == null");
            }

            String processingDate = formatJulianYearDay(args.getProcessingDateArg());

            Collection<String> lines = new ArrayList<>();


            HeadersFootersHelper hfh = new HeadersFootersHelper();
            if (args.getGenerateHeadersFootersArg()) {
                String expiringDate = formatJulianYearDay(args.getExpiringDateArg());

                hfh.setFileSubmissionType(args.getFileSubmissionTypeArg());
                hfh.setProcessingDayType(args.getProcessingDayTypeArg());
                hfh.setSubmissionSerialNumber(args.getSubmissionSerialNumberArg());
                hfh.setServiceUser(args.getServiceUserArg().getUserNumber());
                hfh.setNextFileSerialNumber(args.getNextFileSerialNumberArg());
                hfh.setProcessingDate(processingDate);
                hfh.setExpiringDate(expiringDate);
                hfh.setDdiCount(Integer.toString(args.getRecordsArg().size()));

                lines.addAll(hfh.getHeaderLines());
            }

            DirectDebitInstructionsHelper ddih = new DirectDebitInstructionsHelper();
            ddih.setServiceUser(args.getServiceUserArg());
            ddih.setProcessingDate(processingDate);
            lines.addAll(ddih.getLines(args.getRecordsArg()));

            if (args.getGenerateHeadersFootersArg()) {
                lines.addAll(hfh.getFooterLines());
            }

            args.setLinesRet(lines);
        } catch (IllegalArgumentException e) {
            throw new PreconditionException("Arguments invalid", e);
        }
    }
}
