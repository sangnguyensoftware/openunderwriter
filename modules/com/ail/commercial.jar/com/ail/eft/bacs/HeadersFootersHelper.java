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

import static com.ail.eft.bacs.FileSubmissionType.MULTI_FILE_SUBMISSION;
import static com.ail.eft.bacs.FileSubmissionType.SINGLE_FILE_SUBMISSION;
import static com.ail.eft.bacs.ProcessingDayType.MULTI_PROCESSING_DAY;
import static com.ail.eft.bacs.ProcessingDayType.SINGLE_PROCESSING_DAY;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ail.core.CoreProxy;
import com.github.ffpojo.FFPojoHelper;
import com.github.ffpojo.exception.FFPojoException;

/**
 * This class populates the header rows of a DirectDebitInstruction submission file.
 *
 */
public class HeadersFootersHelper {

    private static final List<String> labelIdentifiers = Arrays.asList(new String[]{"VOL", "HDR", "UHL", "EOF", "EOV", "UTL"});

	private FileSubmissionType fileSubmissionType;
    private ProcessingDayType processingDayType;
	private String submissionSerialNumber;
	private String serviceUser;
	private String nextFileSerialNumber;
	private String processingDate;
    private String expiringDate;
    private String debitValueTotal;
    private String creditValueTotal;
    private String debitItemCount;
    private String creditItemCount;
    private String ddiCount;

	public HeadersFootersHelper() {
	}

    public FileSubmissionType getFileSubmissionType() {
		return fileSubmissionType;
	}

	public void setFileSubmissionType(FileSubmissionType fileSubmissionType) {
		this.fileSubmissionType = fileSubmissionType;
	}

	public ProcessingDayType getProcessingDayType() {
        return processingDayType;
    }

    public void setProcessingDayType(ProcessingDayType processingDayType) {
        this.processingDayType = processingDayType;
    }

    public String getSubmissionSerialNumber() {
		return submissionSerialNumber;
	}

	public void setSubmissionSerialNumber(String submissionSerialNumber) {
		this.submissionSerialNumber = submissionSerialNumber;
	}

	public String getServiceUser() {
		return serviceUser;
	}

	public void setServiceUser(String serviceUser) {
		this.serviceUser = serviceUser;
	}

	public String getNextFileSerialNumber() {
		return nextFileSerialNumber;
	}

	public void setNextFileSerialNumber(String nextFileSerialNumber) {
		this.nextFileSerialNumber = nextFileSerialNumber;
	}

    public String getProcessingDate() {
        return processingDate;
    }

    public void setProcessingDate(String processingDate) {
        this.processingDate = processingDate;
    }

    public String getExpiringDate() {
        return expiringDate;
    }

    public void setExpiringDate(String expiringDate) {
        this.expiringDate = expiringDate;
    }

    public String getDebitValueTotal() {
        return debitValueTotal;
    }

    public void setDebitValueTotal(String debitValueTotal) {
        this.debitValueTotal = debitValueTotal;
    }

    public String getCreditValueTotal() {
        return creditValueTotal;
    }

    public void setCreditValueTotal(String creditValueTotal) {
        this.creditValueTotal = creditValueTotal;
    }

    public String getDebitItemCount() {
        return debitItemCount;
    }

    public void setDebitItemCount(String debitItemCount) {
        this.debitItemCount = debitItemCount;
    }

    public String getCreditItemCount() {
        return creditItemCount;
    }

    public void setCreditItemCount(String creditItemCount) {
        this.creditItemCount = creditItemCount;
    }

    public String getDdiCount() {
        return ddiCount;
    }

    public void setDdiCount(String ddiCount) {
        this.ddiCount = ddiCount;
    }

    private void validate() throws IllegalArgumentException {
        if (fileSubmissionType == null) {
            throw new IllegalArgumentException("fileSubmissionType must be set!");
        }
        if (StringUtils.isBlank(getSubmissionSerialNumber())) {
            throw new IllegalArgumentException("submissionSerialNumber must be set!");
        }
        if (StringUtils.isBlank(getServiceUser())) {
            throw new IllegalArgumentException("serviceUser must be set!");
        }
        if (fileSubmissionType == MULTI_FILE_SUBMISSION && StringUtils.isBlank(getNextFileSerialNumber())) {
            throw new IllegalArgumentException("nextFileSerialNumber must be set for a multi file submission!");
        }
        if (StringUtils.isBlank(getProcessingDate())) {
            throw new IllegalArgumentException("processingDate must be set!");
        }
        if (StringUtils.isBlank(getExpiringDate())) {
            throw new IllegalArgumentException("expiringDate must be set!");
        }
    }

	public Collection<String> getHeaderLines() throws IllegalArgumentException {
		validate();

		Collection<String> lines = new ArrayList<>();

		try {
            lines.add(FFPojoHelper.getInstance().parseToText(getVOL1()));
            lines.add(FFPojoHelper.getInstance().parseToText(getHDR1()));
            lines.add(FFPojoHelper.getInstance().parseToText(getHDR2()));
            lines.add(FFPojoHelper.getInstance().parseToText(getUHL1()));
        } catch (FFPojoException e) {
            new CoreProxy().logError("Failed to generate header lines", e);
        }

		return lines;
	}

    public Collection<String> getFooterLines() throws IllegalArgumentException {
        validate();

        Collection<String> lines = new ArrayList<>();

        try {
            lines.add(FFPojoHelper.getInstance().parseToText(getEOF1()));
            lines.add(FFPojoHelper.getInstance().parseToText(getEOF2()));
            lines.add(FFPojoHelper.getInstance().parseToText(getUTL1()));
        } catch (FFPojoException e) {
            new CoreProxy().logError("Failed to generate footer lines", e);
        }

        return lines;
    }

    private VolumeHeaderLabel1 getVOL1() {
    	VolumeHeaderLabel1 vhl1 = new VolumeHeaderLabel1();

    	vhl1.setSubmissionSerialNumber(getSubmissionSerialNumber());
    	vhl1.setOwnerIdentification(getServiceUser());

    	return vhl1;
	}

	private HeaderLabel1 getHDR1() {
		HeaderLabel1 hdr1 = new HeaderLabel1();

		hdr1.setFileIdentifier2(getServiceUser());
		if (getFileSubmissionType() == MULTI_FILE_SUBMISSION) {
			hdr1.setFileIdentifier5(getNextFileSerialNumber());
		} else if (getFileSubmissionType() == SINGLE_FILE_SUBMISSION) {
			hdr1.setFileIdentifier5("1");
		}
		hdr1.setFileIdentifier6(getServiceUser());
		hdr1.setSetIdentification(getSubmissionSerialNumber());
		hdr1.setCreationDate(getProcessingDate());
		hdr1.setExpirationDate(getExpiringDate());

		return hdr1;
	}

	private HeaderLabel2 getHDR2() {
		HeaderLabel2 hdr2 = new HeaderLabel2();

        if (getProcessingDayType() == MULTI_PROCESSING_DAY) {
            hdr2.setRecordLength("00106");
        } else if (getProcessingDayType() == SINGLE_PROCESSING_DAY) {
            hdr2.setRecordLength("00100");
        }

		return hdr2;
	}

    private UserHeaderLabel1 getUHL1() {
    	UserHeaderLabel1 uhl1 = new UserHeaderLabel1();

    	uhl1.setProcessingDate(getProcessingDate());

		return uhl1;
	}

    private EndOfFileLabel1 getEOF1() {
        EndOfFileLabel1 eof1 = new EndOfFileLabel1();

        eof1.setFileIdentifier2(getServiceUser());
        if (getFileSubmissionType() == MULTI_FILE_SUBMISSION) {
            eof1.setFileIdentifier5(getNextFileSerialNumber());
        } else if (getFileSubmissionType() == SINGLE_FILE_SUBMISSION) {
            eof1.setFileIdentifier5("1");
        }
        eof1.setFileIdentifier6(getServiceUser());
        eof1.setSetIdentification(getSubmissionSerialNumber());
        eof1.setCreationDate(getProcessingDate());
        eof1.setExpirationDate(getExpiringDate());

        return eof1;
    }

    private EndOfFileLabel2 getEOF2() {
        EndOfFileLabel2 eof2 = new EndOfFileLabel2();

        if (getProcessingDayType() == MULTI_PROCESSING_DAY) {
            eof2.setRecordLength("00106");
        } else if (getProcessingDayType() == SINGLE_PROCESSING_DAY) {
            eof2.setRecordLength("00100");
        }

        return eof2;
    }

    private UserTrailerLabel1 getUTL1() {
        UserTrailerLabel1 utl1 = new UserTrailerLabel1();

        if (StringUtils.isNotBlank(getDdiCount())) {
            utl1.setDdiCount(getDdiCount());
        }
        if (StringUtils.isNotBlank(getDebitValueTotal())) {
            utl1.setDebitValueTotal(getDebitValueTotal());
        }
        if (StringUtils.isNotBlank(getCreditValueTotal())) {
            utl1.setCreditValueTotal(getCreditValueTotal());
        }
        if (StringUtils.isNotBlank(getDebitItemCount())) {
            utl1.setDebitItemCount(getDebitItemCount());
        }
        if (StringUtils.isNotBlank(getCreditItemCount())) {
            utl1.setCreditItemCount(getCreditItemCount());
        }

        return utl1;
    }

    public static final boolean isHeaderOrFooterLine(String line) {
        String labelIdentifier = line.substring(0,  3);

        return labelIdentifiers.contains(labelIdentifier);
    }

}
