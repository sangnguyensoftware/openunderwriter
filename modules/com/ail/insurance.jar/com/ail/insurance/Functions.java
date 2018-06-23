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
package com.ail.insurance;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.spi.KnowledgeHelper;

import com.ail.annotation.XPathFunctionDefinition;
import com.ail.core.Attribute;
import com.ail.core.CoreProxy;
import com.ail.core.Fact;
import com.ail.core.ThreadLocale;
import com.ail.core.Type;
import com.ail.core.TypeXPathException;
import com.ail.financial.Currency;
import com.ail.insurance.policy.Asset;
import com.ail.insurance.policy.Policy;
import com.ail.insurance.policy.Section;
import com.ail.party.Address;


/**
 * This class defines a set of unrelated functions which are intended to be
 * used from within Drools rulesets.
 */
@XPathFunctionDefinition(namespace="i")
public class Functions {

    /**
     * Calculate the number of whole years between the specified date and 'when'. Typically,
     * this is used to calculate the age of something/someone, given that date represents
     * the date of creation or birth.
     * @param date Attribute of type date describing the date of creation or birth
     * @param when Date to calculate the age at
     * @return The number of whole years between 'date' and 'when'.
     * @throws Exception
     */
    public static Integer yearsSince(Attribute date, Date when) {
        return yearsSince("yearsSince(Attribute, Date)", date, when);
    }

    /**
     * Calculate the number of whole years between the specified date and 'when'. Typically,
     * this is used to calculate the age of something/someone, given that date represents
     * the date of creation or birth.
     * @param date date of creation or birth
     * @param when Date to calculate the age at
     * @return The number of whole years between 'date' and 'when'.
     * @throws Exception
     */
    public static Integer yearsSince(Date date, Date when) {
        return yearsSince("yearsSince(Date, Date)", date, when);
    }


    /**
     * Calculate the age of something given its date of creation (or birth).
     * @param date Attribute describing the date of birth or creation
     * @return Age in years
     */
    public static Integer ageInYears(Attribute date) {
        return yearsSince("ageInYears(Attribute)", date, new Date());
    }

    /**
     * Calculate then age of something at a specific date.
     * @param date Attribute describing the date of birth or creation
     * @param when Date to calculate the date at
     * @return Age in years
     */
    public static Integer ageInYearsAtDate(Attribute date, Date when) {
        return yearsSince("ageInYearsAtDate(Attribute, Date)", date, when);
    }

    /**
     * Calculate then age of something at the start of the current year.
     * @param date Attribute describing the date of birth or creation
     * @return Age in years
     */
    public static Integer ageInYearsAt1stJan(Attribute date) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_YEAR, 1);
        return yearsSince("ageInYearsAt1stJan(Attribute)", date, cal.getTime());
    }

    /**
     * Given a collection of dates, calculate the youngest - closest to today.
     * @param transactionDate Attributes describing the date of birth or creation
     * @return The youngest date found in 'dates', or null if no date attributes are found in <i>dates</i>.
     * @throws Exception
     */
    public static Attribute youngestOf(Collection<Attribute> dates) throws Exception {
        Attribute youngest=null;

        if (dates==null) {
            throw new IllegalArgumentException("youngesOf(Collection<Attribute>) function called with an empty list of dates");
        }

        for(Attribute attr: dates) {
            if (attr.isDateType()) {
                if (youngest==null || ((Date)youngest.getObject()).before((Date)attr.getObject())) {
                    youngest=attr;
                }
            }
        }

        if (youngest==null) {
            throw new IllegalArgumentException("youngesOf(Collection<Attribute>) function called with a list of attributes that contained no dates");
        }

        return youngest;
    }

    /**
     * Given a collection of date attributes, calculate the oldest - furthest to today.
     * @param transactionDate Attributes describing the date of birth or creation
     * @return The oldest date in 'dates'.
     * @throws Exception
     */
    public static Attribute oldestOf(Collection<Attribute> dates) throws Exception {
        Attribute oldest=null;

        if (dates==null) {
            throw new IllegalArgumentException("oldestOf(Collection<Attribute>) function called with an empty list of dates");
        }

        for(Attribute attr: dates) {
            if (attr.isDateType()) {
                if (oldest==null || ((Date)attr.getObject()).before((Date)oldest.getObject())) {
                    oldest=attr;
                }
            }
        }

        if (oldest==null) {
            throw new IllegalArgumentException("oldestOf(Collection<Attribute>) function called with a list of attributes that contained no dates");
        }

        return oldest;
   }

    /**
     * Given a collection of number attributes, calculate the highest .
     * @param numbers Attributes describing values
     * @return highest Highest attribute.
     * @throws Exception
     */
    public static Attribute highestOf(Collection<Attribute> numbers) throws Exception {
        Attribute highest=null;

        if (numbers==null) {
            throw new IllegalArgumentException("highestOf(Collection<Attribute>) function called with an empty list of numbers");
        }

        for(Attribute attr: numbers) {
            if (attr.isNumberType()) {
                if (highest==null || ((Number)attr.getObject()).floatValue() > ((Number)highest.getObject()).floatValue()) {
                    highest=attr;
                }
            }
        }

        if (highest==null) {
            throw new IllegalArgumentException("highestOf(Collection<Attribute>) function called with a list of attributes that contained no numbers");
        }

        return highest;
   }

    /**
     * Given a collection of number attributes, calculate the lowest .
     * @param numbers Attributes describing values
     * @return Lowest attribute.
     * @throws Exception
     */
    public static Attribute lowestOf(Collection<Attribute> numbers) throws Exception {
        Attribute lowest=null;

        if (numbers==null) {
            throw new IllegalArgumentException("lowestOf(Collection<Attribute>) function called with an empty list of numbers");
        }

        for(Attribute attr: numbers) {
            if (attr.isNumberType()) {
                if (lowest==null || ((Number)attr.getObject()).floatValue() < ((Number)lowest.getObject()).floatValue()) {
                    lowest=attr;
                }
            }
        }

        if (lowest==null) {
            throw new IllegalArgumentException("lowestOf(Collection<Attribute>) function called with a list of attributes that contained no numbers");
        }

        return lowest;
   }

    /**
     * Assert a new fact into dools working memory. The fact to be asserted is derived by evaluating an
     * xpath expression against a source object.
     * @param drools KnowledgeHelper to assert the new fact into.
     * @param name Name for facts
     * @param source Object to evaluate xpath against
     * @param xpath Xpath itself
     */
    public static void assertFact(KnowledgeHelper drools, String name, Type source, String xpath) {
       try {
           Iterator<?> it=source.xpathIterate(xpath);

           while(it.hasNext()) {
               drools.insert(new Fact(name, it.next()));
           }
       }
       catch(TypeXPathException e) {
           CoreProxy cp=new CoreProxy();
           cp.logWarning("XPath evaluation failed to return for fact: '"+name+"'. Xpath is: '"+xpath+"' reason: "+e.getDescription());
       }
   }

    private static Integer yearsSince(String called, Attribute date, Date when) {
        if (!date.isDateType()) {
            throw new IllegalArgumentException(called+" call with an attribute of type: "+date.getFormat()+". Only attributes of type date are valid.");
        }

        return yearsSince(called, (Date)date.getObject(), when);
    }

    private static Integer yearsSince(String called, Date date, Date when) {
        if (date==null) {
            throw new IllegalArgumentException(called+" function called with a null 'date' attribute.");
        }
        if (when==null) {
            throw new IllegalArgumentException(called+" function called with a null 'when' argument");
        }

        // create a calendar to represent the date of birth (or whatever)
        Calendar then=Calendar.getInstance();
        then.setTime(date);

        // create a calendar for now
        Calendar now=Calendar.getInstance();
        now.setTime(when);

        // age is the difference in years between then and now
        int age=now.get(Calendar.YEAR) - then.get(Calendar.YEAR);

        // if the anniversary hasn't yet passed this year, subtract 1
        then.add(Calendar.YEAR, age);
        if (now.before(then)) {
            age--;
        }

        return new Integer(age);
    }

    public static Object test(Object c) {
        try {
            return ((List<?>)c).size()!=0;
        }
        catch(Throwable t) {
            return false;
        }
    }

    /**
     * Convert a string to upper case. This simply wraps {@link java.lang.String#toUpperCase(ThreadLocale locale)}
     * @param string String to be converted
     * @return Upper case version of <i>string</i>, or null if <i>string</i> is null.
     */
    public static String toUpperCase(String string) {
        if (string==null) {
            return null;
        }
        else {
            return string.toUpperCase(ThreadLocale.getThreadLocale());
        }
    }

    /**
     * Convert a string to lower case. This simply wraps {@link java.lang.String#toLowerCase(ThreadLocale locale)}
     * @param string String to be converted
     * @return Lower case version of <i>string</i>, or null if <i>string</i> is null.
     */
    public static String toLowerCase(String string) {
        if (string==null) {
            return null;
        }
        else {
            return string.toLowerCase(ThreadLocale.getThreadLocale());
        }
    }

    /**
     * Return a substring of <i>string</i>. The substring begins at the specified beginIndex and extends to the
     * character at index endIndex - 1. Thus the length of the substring is endIndex-beginIndex.<p/>
     * Examples:<br/>
     *  <code>"hamburger".substring(4, 8) returns "urge"</code><br/>
     *  <code>"smiles".substring(1, 5) returns "mile"</code>
     * @param string String to get the substring from
     * @param beginIndex the beginning index, inclusive.
     * @param endIndex the ending index, exclusive.
     * @return the specified substring.
     */
    public static String substring(String string, int beginIndex, int endIndex) {
        if (string==null) {
            return null;
        }

        return string.substring(beginIndex, endIndex);
    }

    /**
     * Automatically assert all of the assets found in the supplied quotation into
     * the rule engine's working memory.
     * @param drools KnowledgeHelper to assert the new fact into.
     * @param policy The quotation to assert elements from.
     */
    public static void autoAssertAssets(KnowledgeHelper drools, Policy policy) {
        for(Asset asset: policy.getAsset()) {
            drools.insert(asset);
        }
    }

    /**
     * Automatically assert all of the sections found in the supplied quotation into
     * the rule engine's working memory;
     * @param drools KnowledgeHelper to assert the new fact into.
     * @param policy The quotation to assert elements from.
     */
    public static void autoAssertSections(KnowledgeHelper drools, Policy policy) {
        for(Section section: policy.getSection()) {
            drools.insert(section);
        }
    }

    /**
     * Automatically assert all of the attributes from single instance assets into
     * the rule engine's working memory as {@link Fact Facts}. A single instance asset is one for which
     * there is only one instance with that assetTypeId in the quotation. A Fact is
     * created for each of the attributes that these assets contain using the name of
     * the attribute as the Fact name, and the value of the attribute as the Fact's
     * value.<p/>
     * Some care must be taken during product design to avoid two assets of different
     * types from using the same attribute name. Otherwise this method will create
     * multiple instance of Facts with the same name which the rules will not be able
     * to differentiate.
     * @param drools KnowledgeHelper to assert the new fact into.
     * @param policy The quotation to assert elements from.
     */
    public static void autoAssertAssetAttributes(KnowledgeHelper drools, Policy policy) {
        Map<String, Integer> assetTypeIds=new HashMap<>();

        // loop through all the assets, and count the number of instances of each assetTypeId
        for(Asset asset: policy.getAsset()) {
            if (assetTypeIds.containsKey(asset.getAssetTypeId())) {
                Integer val=assetTypeIds.get(asset.getAssetTypeId());
                assetTypeIds.put(asset.getAssetTypeId(), val+1);
            }
            else {
                assetTypeIds.put(asset.getAssetTypeId(), 1);
            }
        }

        // for every assetTypeId that appears once in the quotation, assert all of it's attributes
        for(String assetTypeId: assetTypeIds.keySet()) {
            if (assetTypeIds.get(assetTypeId)==1) {
                autoAssertAttributes(drools, policy.getAssetByTypeId(assetTypeId).get(0));
            }
        }
    }

    /**
     * Assert all of the attributes associated with an object into the rule engine's working memory as {@link Fact Facts}.
     * A Fact is created for each of the attributes that these assets contain using the name of
     * the attribute as the Fact name, and the value of the attribute as the Fact's
     * value.<p/>
     * @param drools KnowledgeHelper to assert the new fact into.
     * @param type The type to assert elements from.
     */
    public static void autoAssertAttributes(KnowledgeHelper drools, Type type) {
        for(Attribute a: type.getAttribute()) {
            drools.insert(new Fact(a.getId(), a.getValue()));
        }
    }

    /**
     * Automatically assert all of the assets, section, and attributes of any single instance
     * assets into the rule engine's working memory.
     * @see #autoAssertAssets(KnowledgeHelper, Policy)
     * @see #autoAssertSections(KnowledgeHelper, Policy)
     * @see #autoAssertAssetAttributes(KnowledgeHelper, Policy)
     * @param drools KnowledgeHelper to assert the new fact into.
     * @param policy The quotation to assert elements from.
     */
    public static void autoAssertAll(KnowledgeHelper drools, Policy policy) {
        autoAssertAssets(drools, policy);
        autoAssertSections(drools, policy);
        autoAssertAssetAttributes(drools, policy);
    }


    /**
     * Translate an address into a String using the line breaks.
     * @param address Address to be converted (should contain line1, line2, line3, line4, line5, town, county, country, postcode attributes)
     * @param html boolean, true for <br> line separator, false for CR LF seperator
     * @return address with line breaks
     */
    public static String formatAddress(Address address, boolean html) {
        String lineBreak = html ? "<BR/>" : "\r\n";

        String[] addressArray = new String[9];
        addressArray[0] = address.getLine1().trim();
        addressArray[1] = address.getLine2().trim();
        addressArray[2] = address.getLine3().trim();
        addressArray[3] = address.getLine4().trim();
        addressArray[4] = address.getLine5().trim();
        addressArray[5] = address.getTown().trim();
        addressArray[6] = address.getCounty().trim();
        addressArray[7] = address.getCountry().trim();
        addressArray[8] = address.getPostcode().trim();

        String formattedAddress = "";
        for(int i=0; i<addressArray.length; i++){
            formattedAddress = formattedAddress + (addressArray[i].equals("") ? "" : addressArray[i] + lineBreak);
        }

        return formattedAddress;
    }

    /**
     * Remove HTML <tags> from text
     * @param text
     * @return
     */
    public static String removeHtmlTags(String text) {
        return text.replaceAll("<[^>]*>", "");

    }

    /**
     * Return currency symbol.
     * @param currency require e.g. USD
     * @return currency symbol e.g. $
     */
    public static String getCurrencySymbol(String currency) {
        return Currency.forName(currency).getSymbol();
    }

}
