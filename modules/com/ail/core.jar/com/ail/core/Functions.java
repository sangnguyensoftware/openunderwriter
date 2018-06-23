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

package com.ail.core;

import static com.ail.core.language.I18N.i18n;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.ail.annotation.XPathFunctionDefinition;
import com.ail.core.command.CommandInvocationError;
import com.ail.core.language.I18N;

/**
 * Utility function class. A collection of useful static methods that are
 * available for use from java and from XPath queries.
 **/
@XPathFunctionDefinition(namespace="c")
public class Functions {

    /**
     * Utility method to load a class in a standard way. Using this method rather than
     * directly invoking Class.forName to allow for classes to be loaded in a consistent
     * way.
     * @param name Name of the class to be loaded
     * @return Loaded, and initialised class
     * @throws ClassNotFoundException If the class cannot be found
     */
    public static Class<?> classForName(String name) throws ClassNotFoundException {
        return Class.forName(name, true, Thread.currentThread().getContextClassLoader());
    }

    /**
     * Builds a <code>String[]</code> from a <code>String</code> with a
     * given character separating values. Example "val1|val2|val3".
     * @param string String containing values.
     * @param separator Denotes character(s) in between values.
     * @return String[] of values.
     */
    public static String[] getStringArrayFromString(String string, String separator) {

            int separatorLength = separator.length();

            if (string.indexOf(separator) == -1) {
                if (string.length() == 0) {
                    String[] array = {};
                    return array;
                } else {
                    String[] array = {string};
                    return array;
                }
            }

            ArrayList<String> v = new ArrayList<>();
            int index1 = 0;
            int index2 = 0;

            while(true) {
                index2 = string.indexOf(separator, index1 + separatorLength);
                if (index2 == -1) break;
                v.add(string.substring(index1, index2));
                index1 = index2 + separatorLength;
            }
            v.add(string.substring(index1));

            String[] array = new String[v.size()];
            for (int i = 0; i < v.size(); i ++) {
                array[i] = (String)v.get(i);
            }

            return array;
    }

    /**
     * Close a connection to a JDBC database in a safe fashion.
     * @param con Connection to be closed (or null)
     * @param st Statement to be closed (or null)
     * @param rs ResultSet to be closed (or null)
     */
    public static void closeJdbc(Connection con, Statement st, ResultSet rs) {
        if (rs!=null) {
            try {
                rs.close();
            }
            catch(SQLException e) {
                System.err.println("Failed to close resultset:"+e);
            }
        }
        if (st!=null) {
            try {
                st.close();
            }
            catch(SQLException e) {
                System.err.println("Failed to close statement:"+e);
            }
        }
        if (con!=null) {
            try {
                con.close();
            }
            catch(SQLException e) {
                System.err.println("Failed to close connection:"+e);
            }
        }
    }

    /**
     * Turn an array of Objects into a comma separated list where each value in the list
     * is the result of calling toString() on the corresponding Object.<p>
     * If the array of objects is empty, an empty string is returned.
     * @param e An array of objects to be CSV'ed
     * @return A String in comma separated value format.
     */
    public static String arrayAsCsv(Object[] e) {
        return collectionAsCsv(Arrays.asList(e));
    }

    /**
     * Turn a collection of Objects into a comma separated list where each value in the list
     * is the result of calling toString() on the corresponding Object.<p>
     * If the array of objects is empty, an empty string is returned.
     * @param e An array of objects to be CSV'ed
     * @return A String in comma separated value format.
     */
    public static String collectionAsCsv(Collection<Object> objects) {
        return valuesAsSeparatedString(objects, ",");
    }

    /**
     * @see #valuesAsLineSeparatedString(Collection)
     * @param objects Array of objects to place into the string
     * @return String representation of the objects;
     */
    public static String arraysAsLineSeparatedString(Object[] objects) {
        return collectionAsLineSeparatedString(Arrays.asList(objects));
    }

    public static TypeEnum enumForName(String name, TypeEnum[] enums) {
        for(TypeEnum s: enums) {
            if (s.longName().equalsIgnoreCase(name) || s.name().equalsIgnoreCase(name)) {
                return s;
            }
        }

        throw new IllegalArgumentException("'"+name+"' is not a valid value in enum "+enums[0].getClass().getName());

    }

    /**
     * Load the content from a URL into a String.
     * @param url URL to load content from
     * @return Content
     * @throws IOException
     */
    public static String loadUrlContentAsString(URL url) throws IOException {
        try (InputStream in = url.openStream()) {
            return IOUtils.toString(in);
        }
    }

    /**
     * Utility method to expand 'variables' embedded in a string with respect to a model. Variables
     * are in the form '${&lt;xpath&gt;}', where xpath is an expression compatible with JXPath. The
     * xpath expression is evaluated against <i>root</i> and the result placed into the string returned.</p>
     * For example: if the <i>src</i> value of <b>"Your quote number is: ${/quoteNumber}"</b> is passed in with a
     * <i>model</i> containing value of 'FQ1234' in it's <code>quoteNumber</code> property; this method would
     * return <b>"Your quote number is: FQ1234"</b>.
     * @param src Source string containing embedded variables
     * @param root All xpath expressions are evaluated against this object
     * @return A string matching <i>src</i> but with variable references expanded.
     * @see #expand(String, Type, Type)
     */
    public static String expand(String src, Type root) {
        return expand(src, root, root);
    }

    /**
     * Utility method to expand 'variables' embedded in a string with respect to a model. Variables
     * are in the form '${&lt;xpath&gt;}', where xpath is an expression compatible with JXPath. The
     * xpath expression is evaluated against a <i>model</i> and the result placed into the string returned.</p>
     * <p>Two models are supported: <code>root</code> and <code>local</code>. XPath expressions starting with '.'
     * are evaluated against <code>local</code>; all others are evaluated against <code>root</code>.</p>
     * For example: if the <i>src</i> value of <b>"Your quote number is: ${/quoteNumber}"</b> is passed in with a
     * <i>model</i> containing value of 'FQ1234' in it's <code>quoteNumber</code> property; this method would
     * return <b>"Your quote number is: FQ1234"</b>.
     * @param src Source string containing embedded variables
     * @param root Any xpath expression not starting with '.' is evaluated against this instance
     * @param local Any xpath expression starting with '.' is evaluated against this instance
     * @return A string matching <i>src</i> but with variable references expanded.
     * @see #expand(String, Type)
     */
    public static String expand(String src, Type root, Type local) {
        if (src!=null) {
            int tokenStart, tokenEnd;
            StringBuilder buf=new StringBuilder(src);

            do {
                tokenStart=buf.indexOf("${");
                tokenEnd=buf.indexOf("}", tokenStart);

                if (tokenStart>=0 && tokenEnd>tokenStart) {
                    String val=null;

                    try {
                        if (buf.charAt(tokenStart+2)=='.') {
                            val=(String)local.xpathGet(buf.substring(tokenStart+2, tokenEnd));
                        }
                        else {
                            val=(String)root.xpathGet(buf.substring(tokenStart+2, tokenEnd));
                        }
                        val=i18n(val);
                    }
                    catch(Throwable t) {
                        // ignore this - the 'if val==null' below will handle the problem.
                    }

                    if (val==null) {
                        val="<b>could not resolve: "+buf.substring(tokenStart+2, tokenEnd)+"</b>";
                    }

                    buf.replace(tokenStart, tokenEnd+1, val);
                }
            } while(tokenStart>=0 && tokenEnd>=0);

            return buf.toString();
        }
        else {
            return null;
        }
    }

    /**
     * Utility method which expands any variables it finds embedded in the passed in
     * content against the model supplied, and writes the output to a specified writer.
     * @param writer Where the expanded output is written no.
     * @param content The String to read content from
     * @param model The model to resolve variable references against
     */
    public static void expand(Writer writer, String content, Type model) {
        try {
            writer.write(expand(content, model));
        }
        catch(Exception e) {
            new CoreProxy().logError("Failed to read/expand content:", e);
        }
    }

    /**
     * Utility method which reads content from a url, expands any variables it finds embedded in
     * the content against the model supplied, and writes the output to a specified writer.
     * @param writer Where the expanded output is written no.
     * @param url The URL to read content from
     * @param model The model to resolve variable references against
     */
    public static void expand(Writer writer, URL url, Type model) {
        BufferedReader reader = null;

        try {
            reader=new BufferedReader(new InputStreamReader(url.openStream()));

            for(String line=reader.readLine() ; line!=null ; line=reader.readLine()) {
                writer.write(expand(line, model));
            }
        }
        catch(Exception e) {
            new CoreProxy().logError("Failed to read URL: '"+url+"'", e);
        }
        finally {
            try {
                if (reader!=null) {
                    reader.close();
                }
            } catch (IOException e) {
                new CoreProxy().logError("Failure while reading URL: '"+url+"'", e);
            }
        }
    }

    /**
     * Utility method which reads content from a url, expands any variables it finds embedded in
     * the content against the model supplied, and writes the output to a specified writer.
     * @param writer Where the expanded output is written no.
     * @param content Stream to read content from.
     * @param model The model to resolve variable references against
     */
    public static void expand(Writer writer, InputStream content, Type model) {
        try {
            BufferedReader reader=new BufferedReader(new InputStreamReader(content));

            for(String line=reader.readLine() ; line!=null ; line=reader.readLine()) {
                writer.write(expand(line, model));
            }

            reader.close();
        }
        catch(Exception e) {
            new CoreProxy().logError("Failed to read input stream.", e);
        }
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
     * @deprecated Use {@link DateTimeUtils#format(Date, String)} instead
     */
    @Deprecated
    public static String format(Date date, String pattern) {
        return DateTimeUtils.format(date, pattern);
    }

    /**
     * @deprecated Use {@link DateTimeUtils#truncateTime(Date)} instead
     */
    @Deprecated
    public static Date truncateTime(Date date) {
        return DateTimeUtils.truncateTime(date);
    }

    /**
     * @deprecated Use {@link DateTimeUtils#getDateTime()} instead
     */
    @Deprecated
    public static Date getDateTime() {
        return DateTimeUtils.getDateTime();
    }

    /**
     * @deprecated Use {@link DateTimeUtils#addDateTime(Date,int,int,int,int,int,int,int)} instead
     */
    @Deprecated
    public static Date addDateTime(Date date, int ms, int s, int min, int hr, int day, int mon, int yr) {
        return DateTimeUtils.addDateTime(date, ms, s, min, hr, day, mon, yr);
    }

    public static URL absoluteConfigureUrl(Core core, String suppliedUrl) throws MalformedURLException {
        if (!suppliedUrl.startsWith("~/")) {
            return new URL(suppliedUrl);
        }
        else {
            String leafUrl=suppliedUrl.substring(2);
            // The URL we've been passed isn't complete, we'll assume it's relative.
            for(String derivedUrl: core.getConfigurationSource()) {
                try {
                    derivedUrl=derivedUrl.substring(0, derivedUrl.lastIndexOf('/')+1)+leafUrl;
                    return new URL(derivedUrl);
                }
                catch(Exception ex) {
                    core.logDebug("Loading content failed from:"+derivedUrl+", reason:"+ex.toString());
                }
            }
            throw new CommandInvocationError("Exausted possible content locations for:"+suppliedUrl);
        }
    }

    /**
     * The script may be local (the value of the script property), or remote (loaded using the
     * url property). This method will return the script no matter where it is.
     * @return A String representing the script
     */
    public static String loadScriptOrUrlContent(Core core, String suppliedUrl, String script) {
        if (script==null && suppliedUrl==null) {
            throw new CommandInvocationError("No script or url property was defined");
        }

        if (script!=null) {
            return script;
        }

        // The url may be absolute or it may be relative to the source URL of
        // the owning configuration, or one of it's parents.
        if (!suppliedUrl.startsWith("~/")) {
            try {
                return Functions.loadUrlContentAsString(new URL(suppliedUrl));
            }
            catch(MalformedURLException e) {
                throw new CommandInvocationError("Failed to load content from: '"+suppliedUrl+"'. URL is malformed.");
            }
            catch(IOException e) {
                throw new CommandInvocationError("Failed to read content from: '"+suppliedUrl+"'. IOException.");
            }
        }
        else {
            // ignore the leading "~/"
            String leafUrl=suppliedUrl.substring(2);

            Exception lastException=null;

            // The URL we've been passed isn't complete, we'll assume it's relative.
            for(String derivedUrl: core.getConfigurationSource()) {
                derivedUrl=derivedUrl.substring(0, derivedUrl.lastIndexOf('/')+1)+leafUrl;

                try {
                    String content=Functions.loadUrlContentAsString(new URL(derivedUrl));
                    core.logDebug("Content found in:"+derivedUrl);
                    return content;
                }
                catch(Exception ex) {
                    core.logDebug("Content not found in:"+derivedUrl+", trying next.");
                    lastException=ex;
                }
            }
            throw new CommandInvocationError("Exausted possible content locations.", lastException);
        }
    }

    /**
     * Return the configuration namespace associated with a product. The convention is
     * that each product has it's own configuration namespace. The productName is
     * in fact the name of the package containing (as a minimum) the product's
     * Registry.
     * @param productName Name of the product to get the namespace.
     * @return Configuration namespace where the product's configuration can be found.
     */
    public static String productNameToConfigurationNamespace(String productName) {
        if (productName.endsWith(".Registry")) {
            throw new IllegalStateException("Cannot convert product name into namespace: product name already ends with .Registry");
        }

        return productName+".Registry";
    }

    /**
     * Return the product name associated with a configuration namespace. The convention is
     * that each product has it's own configuration namespace. The productName is
     * in fact the name of the package containing (as a minimum) the product's
     * Registry.
     * @param configuratioNamespace Namespace to fetch the product name for.
     * @return Product name associated with the namespace
     */
    public static String configurationNamespaceToProductName(String configuratioNamespace) {
        if (!configuratioNamespace.endsWith(".Registry")) {
            throw new IllegalStateException("Cannot convert namespace into product name: namespace doesn't end with .Registry");
        }

        return configuratioNamespace.substring(0, configuratioNamespace.length()-9);
    }

    /**
     * Return the name of a product's default type based on it's namespace.
     * @param productName Name of product to get the default type for.
     * @return Name of the default type.
     */
    public static String productNameToDefaultType(String productName) {
        int idx=productName.lastIndexOf('.');

        // namespace might not include a '.' at all. If it doesn't, use the whole thing.
        return (idx<0) ? productName : productName.substring(idx+1);
    }

    /**
     * Return text defendant upon boolean test.
     * @param test boolean, true or false test
     * @param textOnTrue String, text to be displayed on true test result
     * @param textOnFalse String, text to be displayed on false test result
     * @return text defendant upon conditional test
     */
    public static String conditionalText(boolean test, String textOnTrue, String textOnFalse) {
        return test ? textOnTrue : textOnFalse;
    }

    /**
     * Return text dependent upon boolean test.
     * @param object Type, parent object
     * @param valueXPaths String, comma separated list of xpaths pointing to values to on object
     * @param seperator String, text used to separate values in concatenated string
     * @param notEmptyXPaths String, comma separated list of xpaths to be tested where if all are empty, an empty string to be return from function
     * @return concatenated string
     */
    public static String getConcatString(Type object, String valueXPaths, String seperator, String notEmptyXPaths) {
        String concatValue = "";

        // Loop through valueXPaths
        List<String> valueXPathsList = Arrays.asList(valueXPaths.split(","));
        for(String valueXPath: valueXPathsList){
            String value = (String)object.xpathGet(valueXPath);

            // if value to be tested and empty return empty string from method
            if(notEmptyXPaths.contains(valueXPath) && value.equals("")){
                return "";
            }

            if(!concatValue.equals("") && !value.equals("")){
                concatValue = concatValue + seperator;
            }
            concatValue = concatValue + value;
        }

        return concatValue;
    }

    /**
     * Return concatenated xpath results.
     * @param objects Type[], parent object
     * @param valueXPaths String, comma separated list of xpaths pointing to values to on object
     * @param seperator String, text used to separate values in concatenated string
     * @param notEmptyXPaths String, comma separated list of xpaths to be tested where if all are empty, an empty string to be return from function
     * @param html boolean, true for <br> line separator, false for CR LF seperator
     * @return concatenated string with line breaks
     */
    public static String getConcatArrayStrings(Type[] objects, String valueXPaths, String seperator, String notEmptyXPaths, boolean html) {
        String lineBreak = html ? "<BR/>" : "\r\n";

        String concatValue = "";

        for (int i = 0; i < objects.length; i++){
            String value = getConcatString(objects[i], valueXPaths, seperator, notEmptyXPaths);

            if(!concatValue.equals("") && !value.equals("")){
                concatValue = concatValue + lineBreak;
            }
            if(!value.equals("")){
                concatValue = concatValue + value;
            }
        }

        return concatValue;
    }

    /**
     * Convert a collection of objects into a \n separated string of the .toString
     * values of the objects in the collection. If the collection of objects is empty
     * an empty string will be returned.
     * @param objects Collection of objects to be converted
     * @return String representation of the objects.
     */
    public static String collectionAsLineSeparatedString(Collection<Object> objects) {
        return collectionAsLineSeparatedString(objects, "", "");
    }

    /**
     * Convert a collection of objects into a \n separated string of the .toString
     * values of the objects in the collection. If the collection of objects is empty
     * an empty string will be returned.
     * @param objects Collection of objects to be converted
     * @param ignoreValue String value not to be displayed (and replaced by replaceValue)
     * @param replaceValue String value used to replace ignoreValue, null for no replacement.  e.g. replace "0" with "-".
     * @return String representation of the objects.
     */
    public static String collectionAsLineSeparatedString(Collection<Object> objects, String ignoreValue, String replaceValue) {
        return collectionAsLineSeparatedString(objects, ignoreValue, replaceValue, 0);
    }

    /**
     * Convert a collection of objects into a \n separated string of the .toString
     * values of the objects in the collection. If the collection of objects is empty
     * an empty string will be returned.
     * @param objects Collection of objects to be converted
     * @param ignoreValue String value not to be displayed (and replaced by replaceValue)
     * @param replaceValue String value used to replace ignoreValue, null for no replacement.  e.g. replace "0" with "-".
     * @param maxLength Maximum character length of each line returned
     * @return String representation of the objects.
     */
    public static String collectionAsLineSeparatedString(Collection<? extends Object> objects, String ignoreValue, String replaceValue, int maxLength) {
        return valuesAsSeparatedString(objects, "\n", ignoreValue, replaceValue, maxLength);
    }

    public static String valuesAsSeparatedString(Collection<? extends Object> objects, String separator) {
        return valuesAsSeparatedString(objects, separator, "", "");
    }

    public static String valuesAsSeparatedString(Collection<? extends Object> objects, String separator, String ignoreValue, String replaceValue) {
        return valuesAsSeparatedString(objects, separator, "", "", 0);
    }

    public static String valuesAsSeparatedString(Collection<? extends Object> objects, String separator, String ignoreValue, String replaceValue, int maxLength) {
        return valuesAsSeparatedString(objects, separator, ignoreValue, replaceValue, maxLength, true);
    }

    public static String valuesAsSeparatedString(Collection<? extends Object> objects, String separator, String ignoreValue, String replaceValue, int maxLength, boolean localise) {

        StringBuffer v=null;

        for(Object n: objects) {
            String item = n.toString();
            item = (!ignoreValue.isEmpty() && item.equals(ignoreValue)) ? replaceValue : item;

            item = localise ? I18N.i18n(item) : item;

            item = (maxLength>0 && item.length()>maxLength ) ? item.substring(0, maxLength) : item;

            if (v==null) {
                v=new StringBuffer(item);
            }
            else {
                v.append(separator+item);
            }
        }

        return v!=null ? v.toString() : "";
    }

    /**
     * Return text from html.
     * @param html String HTML formated string
     * @return formated string
     */
    public static String getTextFromHTML(String html) {

        html = Pattern.compile("<p>", Pattern.CASE_INSENSITIVE).matcher(html).replaceAll("");
        html = Pattern.compile("</p>", Pattern.CASE_INSENSITIVE).matcher(html).replaceAll("\n\n");
        html = Pattern.compile("<br>", Pattern.CASE_INSENSITIVE).matcher(html).replaceAll("\n");
        html = Pattern.compile("<br/>", Pattern.CASE_INSENSITIVE).matcher(html).replaceAll("\n");
        html = Pattern.compile("<br />", Pattern.CASE_INSENSITIVE).matcher(html).replaceAll("\n");

        return html;

    }

    /**
     * Return true if <code>string</code> matches the regular expression defined in <code>regexp</code>.
     * @param string String to perform the match in.
     * @param regexp Regular expression to match.
     * @return true if a match is found, also otherwise.
     */
    public static boolean matches(String string, String regexp) {
        return string.matches(regexp);
    }


    /**
     * Return <code>string</code> with all instances of <code>regex</code> replaced with <code>replacement</code>
     * @param string String to replace in.
     * @param regex Regular expression to match for.
     * @param replacement String to use as replacement.
     * @return string with instances of regex replaced with replacement.
     */
    public static String replace(String string, String regex, String replacement) {
        return string.replaceAll(regex, replacement);
    }

    /**
     * Replaces the characters of a string from the right with the specified number of mask characters
     * @param original
     * @param numberOfLastDigits
     * @param maskChar
     * @return
     */
    public static String maskStringFromLeft(String original, int numberOfLastDigits, char maskChar) {
        if (original == null) {
            return StringUtils.repeat(Character.toString(maskChar), numberOfLastDigits);
        } else if (original.length() > numberOfLastDigits) {
            return original.replaceFirst(".{" + (original.length() - numberOfLastDigits) + "}?", StringUtils.repeat(Character.toString(maskChar), (original.length() - numberOfLastDigits)));
        } else {
            return original.replaceFirst(".{" + original.length() + "}?", StringUtils.repeat(Character.toString(maskChar), original.length()));
        }
    }

    /**
     * Implementation of simple conditional logic. The following conditions are evaluated in order:
     * <ol>
     * <li><code>condition</code> is an instance of Boolean: return <code>forTrue</code> for a value of TRUE; <code>forFalse</code> otherwise.</li>
     * <li><code>condition</code> is an instance of Collection: return <code>forTrue</code> if size() > 0; <code>forFalse</code> otherwise.</li>
     * <li>when <code>condition</code> is not null return <code>forTrue</code>; otherwise return <code>forFalse</code>.</li>
     * </ol>
     * @param condition
     *            Condition to be tested
     * @param forTrue
     *            Returned if <code>condition</code> is true.
     * @param forFalse
     *            Returned if <code>condition</code> is false.
     * @return
     * <code>forTrue</code> or <core>forFalse</code> depending on the rules above.
     */
    public static Object when(Object condition, Object forTrue, Object forFalse) {
        if (condition instanceof Boolean) {
            return ((Boolean)condition) ? forTrue : forFalse;
        } else if (condition instanceof Collection) {
            return ((Collection<?>) condition).size() != 0 ? forTrue : forFalse;
        } else {
            return condition != null ? forTrue : forFalse;
        }
    }

    /**
     * @deprecated Use {@link DateTimeUtils#dateToLocalDate(Date)} instead
     */
    @Deprecated
    public static LocalDate dateToLocalDate(Date date) {
        return DateTimeUtils.dateToLocalDate(date);
    }

    /**
     * @deprecated Use {@link DateTimeUtils#dateToLocalDate(Date)} instead
     */
    @Deprecated
    public static Date localDateToDate(LocalDate date) {
        return DateTimeUtils.localDateToDate(date);
    }

    /**
     * Determine if a String is empty - null or zero length
     * @param s String to check
     * @return true if 's' is empty, false otherwise.
     */
    public static boolean isEmpty(String s) {
        return (s==null || s.length()==0);
    }

    /**
     * Get the value of an attribute with the given id on the given {@link Type}
     * @param type
     * @param attributeId
     * @return the attribute value, or null if not found
     */
    public static String getAttributeValue(Type type, String attributeId) {
        try {
            return (String) type.xpathGet("attribute[id='" + attributeId + "']/value");
        } catch (TypeXPathException e) {
            return null;
        }
    }

    /**
     * Get the value of an attribute with the given id on the given {@link Type}
     * @param type
     * @param attributeId
     * @param fallback value if none found
     * @return the attribute value, or null if not found
     */
    public static String getAttributeValue(Type type, String attributeId, String fallback) {
        try {
            return (String) type.xpathGet("attribute[id='" + attributeId + "']/value");
        } catch (TypeXPathException e) {
            return fallback;
        }
    }

    /**
     * Set the value of an attribute with the given id on the given {@link Type}
     * @param type
     * @param attributeId
     * @param value
     * @return true if set without error, else false
     */
    public static boolean setAttributeValue(Type type, String attributeId, String value) {
        try {
            type.xpathSet("attribute[id='" + attributeId + "']/value", value);
            return true;
        } catch (TypeXPathException e) {
            return false;
        }
    }

    /**
     * Test whether a date falls between a start and end date. A value of null for
     * startDate is taken to mean the beginning of time. A value of null for endDate
     * is taken to mean the end of time.
     * If <code>test</code> is null, then if both startDate & endDate are null, true
     * is returned. If either are non-null, false is returned.
     * @param startDate
     * @param endDate
     * @param test
     * @return true if startDate<=test<=endDate; false otherwise.
     */
    public static boolean isBetween(Date startDate, Date endDate, Date test) {
        if (test == null) {
            return startDate == null && endDate == null;
        }
        if (startDate != null && test.before(startDate)) {
            return false;
        }
        if (endDate != null && test.after(endDate)) {
            return false;
        }

        return true;
    }

    /**
     * For use in JXpath expression where the class name is needed
     * @param obj Object to return the class name of
     * @return SimpleName of the class of which obj is an instance.
     */
    public static String simpleName(Object obj) {
        if (obj instanceof List) {
            @SuppressWarnings("rawtypes")
            List objs = (List)obj;
            if (objs.size() > 0) {
                return objs.get(0).getClass().getSimpleName();
            }
        }

        return obj.getClass().getSimpleName();
    }
}
