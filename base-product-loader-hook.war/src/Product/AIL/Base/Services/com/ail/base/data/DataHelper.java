package com.ail.base.data;
/* Copyright Applied Industrial Logic Limited 2014. All rights Reserved */
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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.StringTokenizer;

import com.ail.core.Attribute;
import com.ail.core.CoreContext;
import com.ail.core.PostconditionException;
import com.ail.core.Type;
import com.ail.core.TypeXPathException;
import com.ail.core.workflow.CaseType;
import com.ail.core.workflow.FormatType;
import com.ail.insurance.claim.Claim;
import com.ail.insurance.policy.Policy;
import com.ail.pageflow.portlet.PageFlowException;
import com.ail.party.Party;

/**
 * The data helper intended to handle data calls.
 */
public class DataHelper {

    /**
     * Gets data from OU
     * 
     * @param caseTypeString
     *            the case type, e.g. policy
     * @param caseId
     *            the id of the type, e.g. for policy the external id
     * @param dataDirectoryId
     *            the data item on the type, e.g. inceptionDate
     * @param format
     *            a format type to apply to any returned data, e.g. TIMER
     * @return
     */
    public Object getData(String caseTypeString, String caseId, String dataDirectoryId, String format) {
        Object data = null;
        CaseType caseType = CaseType.forName(caseTypeString);

        Type type = getType(caseType, caseId);
        String formatPattern = null;
        if (type != null) {
            try {
                data = type.xpathGet(dataDirectoryId);
            } catch (TypeXPathException e) {
                CoreContext.getCoreProxy()
                        .logInfo("Unable to get value for xpath '" + dataDirectoryId + "' for " + caseTypeString + " with id " + caseId + ". Trying attributes...");
            }
            if (data == null) {
                try {
                    CoreContext.getCoreProxy().logInfo("Trying " + caseTypeString + ".xpathGet(" + buildAttributeString(dataDirectoryId) + ")");
                    Attribute attribute = (Attribute) type.xpathGet(buildAttributeString(dataDirectoryId));
                    CoreContext.getCoreProxy().logInfo("Got attribute " + attribute);
                    if (attribute != null) {
                        data = attribute.getValue();
                        formatPattern = attribute.getFormatOption("pattern");
                    }
                } catch (TypeXPathException e) {
                    CoreContext.getCoreProxy().logInfo("Unable to get value for " + caseTypeString + ".attribute[id=" + dataDirectoryId + "]");
                }
            }
        }

        return formatData(data, format, formatPattern);
    }

    /**
     * Set data in OU
     * 
     * @param caseTypeString
     *            the case type, e.g. policy
     * @param caseId
     *            the id of the type, e.g. for policy the external id
     * @param dataDirectoryId
     *            the data item on the type, e.g. statusAsString
     * @param value
     *            the value to set, e.g. ON_RISK
     * @throws PageFlowException
     */
    public void setData(String caseTypeString, String caseId, String dataDirectoryId, String value) throws PostconditionException {
        CaseType caseType = CaseType.forName(caseTypeString);

        Type type = getType(caseType, caseId);
        boolean valueSet = false;
        if (type != null) {
            try {
                type.xpathSet(dataDirectoryId, value);
                valueSet = true;
            } catch (TypeXPathException e) {
                CoreContext.getCoreProxy().logInfo("setData failed for xpath '" + dataDirectoryId + "' for " + caseTypeString + " with id " + caseId + ". Trying attributes...");
            }
            if (!valueSet) {
                try {
                    type.xpathSet(buildAttributeString(dataDirectoryId), value);
                    valueSet = true;
                } catch (TypeXPathException e1) {
                    CoreContext.getCoreProxy().logInfo("setData failed for " + caseTypeString + "[" + caseId + "].attribute[id='" + dataDirectoryId + "'](" + value + ")");
                }
            }
        }

        if (!valueSet) {
            throw new PostconditionException("setData failed, see logs for details.");
        }
    }

    /**
     * Turns a string into an xpath of Attributes. The input can be an attribute id
     * or a dot-separated hierarchy of attribute ids.
     * 
     * @param dataDirectoryId
     * @return
     */
    private String buildAttributeString(String dataDirectoryId) {
        String attributeString = "";

        for (StringTokenizer st = new StringTokenizer(dataDirectoryId, "."); st.hasMoreTokens();) {
            attributeString += "attribute[id='" + st.nextToken() + "']";
            if (st.hasMoreTokens()) {
                attributeString += "/";
            }
        }

        return attributeString;
    }

    /**
     *
     * @param data
     *            the data
     * @param format
     *            the type of format, e.g. TIMER
     * @param formatPattern
     *            any format pattern, e.g. this will be the format specified on an
     *            attribute.
     * @return the formatted data
     */
    private Object formatData(Object data, String format, String formatPattern) {
        Object formattedData = data;

        if (format != null && !format.isEmpty()) {
            try {
                FormatType formatType = FormatType.forName(format);

                if (FormatType.TIMER.equals(formatType)) {
                    formattedData = formatTimer(data, formatPattern);
                }
                if (FormatType.UPPER_CASE.equals(formatType)) {
                    formattedData = formatText(data, formatPattern, true, false, false);
                }
                if (FormatType.LOWER_CASE.equals(formatType)) {
                    formattedData = formatText(data, formatPattern, false, true, false);
                }
                if (FormatType.UPPER_CASE_TRIMMED.equals(formatType)) {
                    formattedData = formatText(data, formatPattern, true, false, true);
                }
                if (FormatType.LOWER_CASE_TRIMMED.equals(formatType)) {
                    formattedData = formatText(data, formatPattern, false, true, true);
                }
                if (FormatType.TRIMMED.equals(formatType)) {
                    formattedData = formatText(data, formatPattern, false, false, true);
                }
            } catch (Exception e) {
                CoreContext.getCoreProxy().logInfo("Unable to format data: " + data + ", with format: " + format);
            }
        }

        return formattedData;
    }

    /**
     * Formats a date into a jbpm Timer String for one time execution
     * 
     * @param data
     *            the date, can be a Date object or a String. If a String then the
     *            formatPattern should also be provided
     * @param formatPattern
     *            the pattern to be used to convert a data String into a Date
     * @return the formatted Timer string
     */
    private Object formatTimer(Object data, String formatPattern) {
        ZoneId zoneId = ZoneId.of("Z");

        ZonedDateTime zdt = null;
        if (Date.class.isInstance(data)) {
            zdt = ZonedDateTime.ofInstant(((Date) data).toInstant(), zoneId);
        } else if (String.class.isInstance(data)) {
            zdt = ZonedDateTime.parse((String) data, DateTimeFormatter.ofPattern(formatPattern));
        }

        if (zdt != null) {
            return "R/" + zdt.format(DateTimeFormatter.ISO_DATE_TIME) + "/PT00S";
        }

        return null;
    }

    /**
     * Formats a string to upper/lower case and trimmed of leading/trailing spaces
     * as required
     * 
     * @param data
     *            the date, can be a Date object or a String. If a String then the
     *            formatPattern should also be provided
     * @param formatPattern
     *            the pattern to be used to convert a data String into a Date
     * @param upperCase
     *            if true covert string to upper case
     * @param lowerCase
     *            if true covert string to lower case
     * @param trimmed
     *            if true trim leading/trailing spaces
     * @return the formatted string
     */
    private Object formatText(Object data, String formatPattern, boolean upperCase, boolean lowerCase, boolean trimmed) {
        if (data == null)
            return "";
        String value = (String) data;
        value = trimmed ? value.trim() : value;
        value = upperCase ? value.toUpperCase() : value;
        value = lowerCase ? value.toLowerCase() : value;
        return value;
    }

    /**
     * Gets a Type from the CoreContext or the database if it has not been set in
     * the context
     * 
     * @param caseType
     *            the case type, e.g. CaseType.POLICY
     * @param caseId
     *            the id of the case, e.g. external system id of a policy
     * @return the instance of the Type
     */
    public Type getType(CaseType caseType, String caseId) {
        Type type = null;

        try {
            if (CaseType.POLICY.equals(caseType)) {
                return (Policy) CoreContext.getCoreProxy().queryUnique("get.policy.by.externalSystemId", caseId);
            } else if (CaseType.PARTY.equals(caseType)) {
                return (Party) CoreContext.getCoreProxy().queryUnique("get.party.by.externalSystemId", caseId);
            } else if (CaseType.CLAIM.equals(caseType)) {
                return (Claim) CoreContext.getCoreProxy().queryUnique("get.claim.by.externalSystemId", caseId);
            }
        } catch (Exception e) {
            CoreContext.getCoreProxy().logError("Unable to get Type for caseType: " + caseType + " and caseId: " + caseId);
        }

        return type;
    }
}
