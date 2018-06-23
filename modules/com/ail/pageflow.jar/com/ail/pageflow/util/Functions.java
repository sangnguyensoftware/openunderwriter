/* Copyright Applied Industrial Logic Limited 2006. All rights Reserved */
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
package com.ail.pageflow.util;

import static com.ail.core.Attribute.DEFAULT_DATE_PATTERN;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.portlet.PortletRequest;
import javax.portlet.RenderResponse;

import com.ail.annotation.XPathFunctionDefinition;
import com.ail.core.Attribute;
import com.ail.core.Type;
import com.ail.pageflow.PageElement;

/**
 * This class defines a collection of functions used by the classes in {@link com.ail.pageflow}.
 */
@XPathFunctionDefinition(namespace="u")
public class Functions {
    private static SimpleDateFormat longFormat=new SimpleDateFormat("d MMMMM, yyyy");

    /**
     * Determine if a String is empty - null or zero length
     * @param s String to check
     * @return true if 's' is empty, false otherwise.
     * @deprecated Use {@link com.ail.core.Functions#isEmpty(String)} instead.
     */
    @Deprecated
    public static boolean isEmpty(String s) {
        return (s==null || s.length()==0);
    }

    /**
     * Convert null strings into empty strings. When a UI component is rendering it'll frequently
     * want to render null strings. The default java behaviour when you ask to output a null String
     * is to write "null" to the output - which isn't what we typically want on the UI.
     * @param s String to check
     * @return "" if the string was null, or the value of the string if it was not.
     */
    public static String hideNull(String s) {
        return (s==null) ? "" : s;
    }

    /**
     * Return the name of the portal page that a render response relates to.
     * From a PageElement we don't have much information to go on if we want to
     * query the environment that the portlet we're associated with is running in.
     * In the case of the LoginSection, we need to know which portal page we're
     * deployed to in order to make the jump from the public portal to the
     * authenticated one.
     * The action URL for non-authenticated takes this kind of form:
     *    /portal/portal/<portal-name>/<page-name>/<window-name>
     * When authenticated the same URL looks like this:
     *    /portal/auth/portal/<portal-name>/<page-name>/<window-name>
     */
    public static String getPortalPageName(RenderResponse response) {
        String[] actionUrlPart=response.createActionURL().toString().split("/");

        if ("auth".equals(actionUrlPart[2])) {
            return actionUrlPart[5];
        }
        else {
            return actionUrlPart[4];
        }
    }

    /**
     * Return the name of the portal that a render response relates to.
     * The action URL for non-authenticated takes this kind of form:
     *    /portal/portal/<portal-name>/<page-name>/<window-name>
     * When authenticated the same URL looks like this:
     *    /portal/auth/portal/<portal-name>/<page-name>/<window-name>
     * Alternative URL's may contain no portal name
     *    /wsrp_rewrite?wsrp-urlType=blockingAction&amp;wsrp-interactionState=JBPNS_/wsrp_rewrite
     */
    public static String getPortalName(RenderResponse response) {
        String actionUrl = response.createActionURL().toString();
        String[] actionUrlPart = actionUrl.split("/");

        // find index in url of portal name
        int nameIndex = 0;
        if (actionUrlPart.length > 2 && "auth".equals(actionUrlPart[2])) {
            nameIndex = 4;
        } else {
            nameIndex = 3;
        }

        // if none found return nothing
        if (nameIndex == 0 || actionUrlPart.length <= nameIndex) {
            return "";
        }

        // return portal name
        return actionUrlPart[nameIndex];

    }


    /**
     * Return true if the specified object has any UI error markers associated with it. UI
     * components use the conversion of attaching error attributes to model element to indicate
     * validation failures. This method will return true if it finds any such attributes
     * associated with the specified object.
     * @param model Object to check for error markers
     * @return true if error markers are found, false otherwise (including if model==null).
     */
    public static boolean hasErrorMarkers(Type model) {
        if (model != null) {
            for (Attribute a : model.getAttribute()) {
                if (a.getId().startsWith("error.")) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Add a new error to a model.
     * @param id unique error ID
     * @param message Message to be displayed
     * @param model Model to attach the error to
     */
    public static void addError(String id, String message, Type model) {
        Attribute error = new Attribute("error." + id, message, "string");
        if (!model.getAttribute().contains(error)) {
            model.addAttribute(error);
        }
    }

    /**
     * return true if the specified object has a specific error marker associated with it.
     * @see #hasErrorMarkers(Type)
     * @param id Name of error marker to look for
     * @param model Object to check for the error marker
     * @return true if the error marker is found, false otherwise (including if model==null).
     */
    public static boolean hasErrorMarker(String id, Type model) {
        if (model != null) {
            for (Attribute a : model.getAttribute()) {
                if (a.getId().startsWith("error." + id)) {
                    return true;
                }
            }
        }

        return false;
    }

    private static void lookupErrorTranslation(String error, StringBuffer errors, List<ErrorText> errorList) {
        boolean errorFound = false;

        if (errorList.size() != 0) {
            for (ErrorText e : errorList) {
                if (error.equals(e.getError())) {
                    if (errors.length() != 0) {
                        errors.append(", ");
                    }
                    errors.append(e.getText());
                    errorFound = true;
                }
            }
        }

        if (!errorFound) {
            errors.append(error);
        }
    }

    /**
     * Find the error(s) (if any) associated with an element in a model, and return them.
     * @param errorFilter Which errors to return
     * @param model The model to look in for the error
     * @return The error message, or "&nbsp;" (an empty String) if no message is found.
     */
    public static String findError(String errorFilter, Type model, PageElement element) {
        StringBuffer error = new StringBuffer();

        for (Attribute attr : model.getAttribute()) {
            if (attr.getId().startsWith("error." + errorFilter)) {
                lookupErrorTranslation(attr.getValue(), error, element.getErrorText());
            }
        }

        return (error.length() == 0) ? "&nbsp;" : error.toString();
    }

    /**
     * Find all the the errors (if any) associated with an element in a model, and return them.
     * @param model The model to look in for the error
     * @return The error message, or "&nbsp;" (an empty String) if no message is found.
     */
    public static String findErrors(Type model, PageElement element) {
        return findError("", model, element);
    }

    /**
     * Find the error(s) (if any) associated with an element in a model, and return them.
     * @param regexPattern The regex pattern for the errors to return, e.g. "(duration|overlap).*"
     * @param model The model to look in for the error
     * @return A JSON friendly, comma separated list of error strings, e.g. "Error 1", "Error 2"
     */
    public static String findErrorsByRegex(String regexPattern, Type model, PageElement element){
        StringBuffer errorsBuffer = new StringBuffer();
        Set<String> errorsFound = new HashSet<>();

        for (Attribute attr : model.getAttribute()) {
            if (attr.getId().matches("error\\." + regexPattern)){
                // some errors are raised twice, so only want to add them once to the errorsBuffer
                String errorValue = attr.getValue();

                if (!errorsFound.contains(errorValue)){
                    if (errorsBuffer.length() != 0) {
                        errorsBuffer.append(", ");
                    }
                    errorsBuffer.append("\"").append(errorValue).append("\"");
                    errorsFound.add(errorValue);
                }
            }
        }

        return errorsBuffer.toString();
    }

    /**
     * Remove error marker attributes attached to the specified object. The UI components use
     * the conversion of attaching error attributes to model element to indicate validation
     * failures. This method strips any such markers from the object passed in. Note: It doesn't
     * attempt to walk the object tree, it will only remove markers from the object itself.
     * @param model Object to remove markers from.
     */
    public static void removeErrorMarkers(Type model) {
        model.getAttribute().removeIf(attr -> attr.getId().startsWith("error."));
    }

    /**
     * Remove error marker attributes attached to the specified object. The UI components use
     * the conversion of attaching error attributes to model element to indicate validation
     * failures. This method strips any such markers from the object passed in. Note: It doesn't
     * attempt to walk the object tree, it will only remove markers from the object itself.
     * @param model Object to remove markers from.
     */
    public static void removeErrorMarkers(Type model, String errorFilter) {
        String errorPrefix = "error."+errorFilter;
        model.getAttribute().removeIf(attr -> attr.getId().startsWith(errorPrefix));
    }
    /**
     * Return a string representation of a date in "long" format. Long format is: "d MMMMM, yyyy". For example:
     * 10 November, 2007
     * @param date
     * @return String representation of <i>date</i>
     */
    public static String longDate(Date date) {
        synchronized(longFormat) {
            return longFormat.format(date);
        }
    }

    /**
     * Products frequently refer to content from their Registry or Pageflows by "relative" URLs. This method
     * expands relative URLs into absolute product URLs - i.e. a URL using the "product:" protocol. A relative URL
     * is one that starts with "~/", where "~" is shorthand for the product's home location. None relative URLs are
     * returned without modification.
     * @param url URL to be checked and expanded if necessary
     * @param request Associated request
     * @param productTypeId Product to be used in the expanded URL
     * @return Expanded URL if it was relative, URL as passed in otherwise.
     */
    public static String expandRelativeUrlToProductUrl(String url, PortletRequest request, String productTypeId) {
        if (url.startsWith("~/")) {
            return "product://"+request.getServerName()+":"+request.getServerPort()+"/"+productTypeId.replace('.', '/')+url.substring(1);
        }
        else {
            return url;
        }
    }

    /**
     * Convert a list of Strings into a semicolon separated list.
     * @param list List to be converted
     * @return semicolon separated list of values from the list.
     */
    public static String convertListToSemiColonString(List<String> list) {
        StringBuffer ret = new StringBuffer();

        for (Iterator<String> e = list.iterator(); e.hasNext();) {
            ret.append(e.next());
            if (e.hasNext()) {
                ret.append(";");
            }
        }

        return ret.toString();
    }

    /**
     * Convert a String of values in semicolon separated format into a List<String>.
     * @param csv String to be converted
     * @return List of strings
     */
    public static List<String> convertSemiColonStringToList(String csv) {
        return new ArrayList<>(Arrays.asList(csv.split("[ \t]*+;[ \t]*+")));
    }

    public static String defaultDateFormat() {
        return DEFAULT_DATE_PATTERN;
    }


    /**
     * Attributes define date patterns using the conventions of Java's SimpleDateFormatter, but
     * jQuery uses it's own format. This method converts from SimpleDateFormatter format to
     * jQuery format.
     * @param attrPattern pattern in SimpleDateFormatter format
     * @return pattern in jQuery date picker format
     */
    public static String simpleDateFormatToJqueryDateFormat(String attrPattern) {
        if (attrPattern == null) {
            attrPattern = DEFAULT_DATE_PATTERN;
        }

        // Year
        if (attrPattern.contains("yyyy")) {
            attrPattern = attrPattern.replaceAll("yyyy", "yy");
        } else {
            attrPattern = attrPattern.replaceAll("yy", "y");
        }

        // Month
        if (attrPattern.contains("MMMM")) {
            attrPattern = attrPattern.replace("MMMM", "MM");
        } else if (attrPattern.contains("MMM")) {
            attrPattern = attrPattern.replace("MMM", "M");
        } else if (attrPattern.contains("MM")) {
            attrPattern = attrPattern.replace("MM", "mm");
        } else if (attrPattern.contains("M")) {
            attrPattern = attrPattern.replace("M", "m");
        }

        // Day
        if (attrPattern.contains("DD")) {
            attrPattern = attrPattern.replace("DD", "oo");
        } else if (attrPattern.contains("D")) {
            attrPattern = attrPattern.replace("D", "o");
        }

        // Day of month
        if (attrPattern.contains("EEEE")) {
            attrPattern = attrPattern.replace("EEEE", "DD");
        } else if (attrPattern.contains("EEE")) {
            attrPattern = attrPattern.replace("EEE", "D");
        }

        return attrPattern;
    }
}
