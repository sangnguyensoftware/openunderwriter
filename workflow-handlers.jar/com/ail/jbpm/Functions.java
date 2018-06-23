/* Copyright Applied Industrial Logic Limited 2002. All rights reserved. */
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
package com.ail.jbpm;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.jbpm.process.audit.JPAAuditLogService;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.manager.audit.AuditService;
import org.kie.api.runtime.manager.audit.ProcessInstanceLog;
import org.kie.api.runtime.process.ProcessContext;
import org.kie.api.runtime.process.WorkflowProcessInstance;

/**
 * Utility function class for use in jbpm workflow scripts.
 **/
public class Functions {

    /**
     * Returns the current UTC date time (with zeroed nanoseconds) as an ISO-8601 formatted String
     * @return  Short formatted date
     */
    public static String getDateNow() {
        return getDateNow(DateTimeFormatter.ofLocalizedDate(java.time.format.FormatStyle.SHORT));
    }

    /**
     * Returns the current UTC date time (with zeroed nanoseconds) as an ISO-8601 formatted String
     * @return  ISO-8601 formatted date
     */
    public static String getDateNowISO() {
        return getDateNow(DateTimeFormatter.ISO_DATE_TIME);
    }

    /**
     * Returns the current UTC date time (with zeroed nanoseconds) as an ISO-8601 formatted String
     * @param format    date/time format to return
     * @return  formatted date
     */
    public static String getDateNow(DateTimeFormatter format) {
        ZonedDateTime zdt = ZonedDateTime.now(ZoneOffset.UTC).withNano(0);
        return zdt.format(format);
    }

    /**
     * Returns a UTC date/time (with zeroed nanoseconds) a specified amount of time in the future.
     * Takes a time string of jbpm format with a number followed by a unit d = day, h = hour, m = minute, s = second. Order is not important.
     * E.g. 25m1d47s3h would return a date 1 day, 3 hours, 25 minutes, and 47 seconds in the future.
     * @param offset    standard jbpm notation to describe how far in the future
     * @return  Short formatted date
     */
    public static String getFutureDate(String offset) {
        return getFutureDate(offset, DateTimeFormatter.ofLocalizedDate(java.time.format.FormatStyle.SHORT));
   }

    /**
     * Returns a UTC date/time (with zeroed nanoseconds) a specified amount of time in the future.
     * Takes a time string of jbpm format with a number followed by a unit d = day, h = hour, m = minute, s = second. Order is not important.
     * E.g. 25m1d47s3h would return a date 1 day, 3 hours, 25 minutes, and 47 seconds in the future.
     * @param offset    standard jbpm notation to describe how far in the future
     * @return  ISO-8601 formatted date
     */
    public static String getFutureDateISO(String offset) {
        return getFutureDate(offset, DateTimeFormatter.ISO_DATE_TIME);
   }

    /**
     * Returns a UTC date/time (with zeroed nanoseconds) a specified amount of time in the future.
     * Takes a time string of jbpm format with a number followed by a unit d = day, h = hour, m = minute, s = second. Order is not important.
     * E.g. 25m1d47s3h would return a date 1 day, 3 hours, 25 minutes, and 47 seconds in the future.
     * @param offset    standard jbpm notation to describe how far in the future
     * @param format    date/time format to return
     * @return  formatted date
     */
    public static String getFutureDate(String offset, DateTimeFormatter format) {
        ZonedDateTime zdt = ZonedDateTime.now(ZoneOffset.UTC).withNano(0);

        char[] chars = offset.toCharArray();
        String num = "";
        for (int i = 0; i < chars.length; i++) {
            char ch = chars[i];
            if (Character.isDigit(ch)) {
                num += ch;
            } else if (Character.isLetter(ch)) {
                zdt = plusDate(zdt, num, ch);
                num = "";
            }
        }

        return zdt.format(format);
    }

    /**
     * Returns a future UTC date/time (with zeroed nanoseconds) at the specified time of day.
     * Takes a time string of standard format of hh:mm:ss.
     * E.g. 22:30:00 would return a date of either today at 10:30pm or, if that has already passed, then tomorrow at that time.
     * @param offset    standard time format for hours minutes seconds
     * @return  Short formatted date
     */
    public static String getFutureDateAt(String time) {
        return getFutureDateAt(time, DateTimeFormatter.ofLocalizedDate(java.time.format.FormatStyle.SHORT));
    }

    /**
     * Returns a future UTC date/time (with zeroed nanoseconds) at the specified time of day.
     * Takes a time string of standard format of hh:mm:ss.
     * E.g. 22:30:00 would return a date of either today at 10:30pm or, if that has aready passed, then tomorrow at that time.
     * @param offset    standard time format for hours minutes seconds
     * @return  ISO-8601 formatted date
     */
    public static String getFutureDateAtISO(String time) {
        return getFutureDateAt(time, DateTimeFormatter.ISO_DATE_TIME);
    }

    /**
     * Returns a future UTC date/time (with zeroed nanoseconds) at the specified time of day.
     * Takes a time string of standard format of hh:mm:ss.
     * E.g. 22:30:00 would return a date of either today at 10:30pm or, if that has already passed, then tomorrow at that time.
     * @param offset    standard time format for hours minutes seconds
     * @param format    date/time format to return
     * @return formatted date
     */
    public static String getFutureDateAt(String time, DateTimeFormatter format) {
        ZonedDateTime z1 = ZonedDateTime.now(ZoneOffset.UTC);

        if (!time.matches("\\d{2}:\\d{2}:\\d{2}") ) {
            return z1.format(format);
        }

        int hour = Integer.parseInt(time.substring(0, 2));
        int minute = Integer.parseInt(time.substring(3, 5));
        int second = Integer.parseInt(time.substring(6, 8));
        ZonedDateTime z2 = ZonedDateTime.now(ZoneOffset.UTC).withHour(hour).withMinute(minute).withSecond(second).withNano(0);
        if (z2.isBefore(z1)) {
            z2 = z2.plusDays(1);
        }

        return z2.format(format);
    }

    /**
     * Adds the specified number of units to a datetime.
     * @param zdt   the datetime
     * @param num   the number of units to add
     * @param type  the datetime unit, d, h, m, or s
     * @return
     */
    private static ZonedDateTime plusDate(ZonedDateTime zdt, String num, char type) {
        char ch = Character.toLowerCase(type);
        switch (ch) {
        case 'd':
            zdt = zdt.plusDays(Long.valueOf(num));
            break;
        case 'h':
            zdt = zdt.plusHours(Long.valueOf(num));
            break;
        case 'm':
            zdt = zdt.plusMinutes(Long.valueOf(num));
            break;
        case 's':
            zdt = zdt.plusSeconds(Long.valueOf(num));
            break;
        default:
            break;
        }

        return zdt;
    }

    /**
     * Setup a collection of Integers from 1 to 5 (e.g. java.util.Arrays.asList(1,2,3,4,5)) and store in a workflow context variable
     * @param kcontext workflow context
     * @param varName = name of workflow data item to store counter in
     * @param size
     * @return
     */
    public static void setLoopCounter(ProcessContext kcontext, String varName, int size) {
        List<Integer> loopCounter = getLoopCounter(size);
        kcontext.setVariable(varName,loopCounter);
        return;
    }

    /**
     * Return a collection of Integers from 1 to 5 (e.g. java.util.Arrays.asList(1,2,3,4,5))
     * @param size
     * @return
     */
    public static List<Integer> getLoopCounter(int size) {
        List<Integer> loopCounter = new ArrayList<>();

        for (int i = 1; i <= size; i++) {
            loopCounter.add(i);
        }

        return loopCounter;
    }

    /**
     * Turns a string in the form of url query parameters (i.e. name1=value1&name2=value2 etc) to a Properties object containing those params
     * @param queryParameters
     * @return
     */
    public static Properties queryParametersToProperties(String queryParameters) {
        Properties props = new Properties();

        if (queryParameters != null) {
            for (String param : queryParameters.split("&")) {
                String[] parts = param.split("=");
                if (parts.length == 2) {
                    props.put(parts[0], parts[1]);
                }
            }
        }

        return props;
    }

    /**
     * Check a value matches a list of values regardless of case or leading/trailing spaces
     * @param value to compare
     * @param matchList comma separated list of values to compare with
     * @return true if match
     */
    public static boolean isMatch(String value, String matchList) {
        return isMatch(value, matchList, ",");
    }
    /**
     * Check a value matches a list of values regardless of case or leading/trailing spaces
     * @param value to compare
     * @param matchList list of values to compare with
     * @param separator value separator for matchList
     * @return true if match
     */
    public static boolean isMatch(String value, String matchList, String separator) {
        return isMatch(value, matchList, separator, false, true);
    }
    /**
     * Check a value matches a list of values regardless of case or leading/trailing spaces
     * @param value to compare
     * @param matchList list of values to compare with
     * @param separator value separator for matchList
     * @param caseSensitive true if comparison is to be case sensitive
     * @param trimmed true if leading/trailing spaces tobe ignored
     * @return true if match
     */
    public static boolean isMatch(String value, String matchList, String separator, boolean caseSensitive, boolean trimmed) {
        if (matchList != null && value != null) {
            if(trimmed) value = value.trim();
            for (String item : matchList.split(separator)) {
                if(trimmed) item = item.trim();
                if((caseSensitive && value.equals(item)) || value.equalsIgnoreCase(item)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Looks through current running processes to check if any are the same as this one. Intended to stop multiple versions of jobs that should be unique, for example
     * scheduled jobs. Will check the version of the process as compared to this one and if this is lower or equal this one will get killed. Does not attempt to kill other
     * processes if their version number is lower as this causes synchronisation exceptions.
     * There should be a variable present on the calling process that, when set to a speccified value, will cause the process to gracefully terminate.
     * @param kcontext
     * @param variableNameToTest    should be the name of a variable whose value will be checked on this process and any other process to establish they are the same type
     * @param killVariableToSetName name of a variable to set on this process to ensure it stops after this method as run.
     * @param killVariableToSetValue    value to set for the variable to make it stop
     * @return the result of this method run
     */
    @SuppressWarnings("rawtypes")
    public static String killDuplicateInstances(ProcessContext kcontext, String variableNameToTest, String killVariableToSetName, String killVariableToSetValue) {
        Environment env = kcontext.getKieRuntime().getEnvironment();
        AuditService as = new JPAAuditLogService(env, org.jbpm.process.audit.strategy.PersistenceStrategyType.KIE_SESSION);

        // Get all the information about this process
        WorkflowProcessInstance thisProcessInstance = (WorkflowProcessInstance) kcontext.getProcessInstance();
        long thisProcessInstanceId = thisProcessInstance.getId();
        org.kie.api.definition.process.Process thisProcess = thisProcessInstance.getProcess();
        String theProcessId = thisProcess.getId();
        double thisVersion = getVersionAsDouble(thisProcess.getVersion());
        Object thisProcessInstanceTestVariable = thisProcessInstance.getVariable(variableNameToTest);

        String result = "No other instances of " + theProcessId + ":" + thisProcessInstanceTestVariable + " to kill. Running this one at version " + thisVersion;

        List processInstanceLogs = as.findActiveProcessInstances(theProcessId);
        if (processInstanceLogs.size() > 1) {
            for (Object o : processInstanceLogs) {
                ProcessInstanceLog thatProcessInstanceLog = (ProcessInstanceLog) o;

                // Get all the information about the other process
                WorkflowProcessInstance thatProcessInstance = (WorkflowProcessInstance) kcontext.getKieRuntime().getProcessInstance(thatProcessInstanceLog.getProcessInstanceId());
                if (thatProcessInstance != null) {
                    long thatProcessInstanceId = thatProcessInstance.getId();
                    double thatVersion = getVersionAsDouble(thatProcessInstanceLog.getProcessVersion());
                    Object thatProcessInstanceTestVariable = thatProcessInstance.getVariable(variableNameToTest);

                    // make sure it's a different process instance than this one, and that the test variable values match
                    if (thisProcessInstanceId != thatProcessInstanceId
                            && thisProcessInstanceTestVariable.equals(thatProcessInstanceTestVariable)) {
                        result = "Found other instances of " + theProcessId + ":" + thisProcessInstanceTestVariable + ". ";
                        if (thisVersion <= thatVersion) {
                            result += "This instance version is " + thisVersion + " and that is " + thatVersion + " therefore stopping this instance.";
                            kcontext.setVariable(killVariableToSetName, killVariableToSetValue);
                        }
                    }
                }
            }
        }

        return result;
    }

    private static double getVersionAsDouble(String stringVersion) {
        double version = 0d;

        try {
            version = Double.valueOf(stringVersion);
        } catch (Exception ignored) {}

        return version;
    }

}
