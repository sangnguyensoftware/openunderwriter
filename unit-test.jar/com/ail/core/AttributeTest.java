/* Copyright Applied Industrial Logic Limited 2002. All rights Reserved  */
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

import static java.util.Calendar.DATE;
import static java.util.Locale.CANADA;
import static java.util.Locale.GERMANY;
import static java.util.Locale.ITALIAN;
import static java.util.Locale.UK;
import static java.util.Locale.US;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.apache.commons.jxpath.JXPathContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ail.core.data.XPath;

/**
 * Tests to exercise the facilities offered by he Core's Attribute class.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({XPath.class})
public class AttributeTest {
    private Locale savedLocale;

    @Before
    public void setUp() throws Exception {
        savedLocale = ThreadLocale.getThreadLocale();
        ThreadLocale.setThreadLocale(UK);

        mockStatic(XPath.class);
    }

    @After
    public void teadDown() throws Exception {
        ThreadLocale.setThreadLocale(savedLocale);
    }

    @Test
    public void testAttributeValidation() throws Exception {
        Attribute attr;

        attr = new Attribute("q1", "1", "number;min=0;max=21");
        attr.setValue("-1");
        assertTrue(attr.isInvalid());
        attr.setValue("22");
        assertTrue(attr.isInvalid());
        attr.setValue("10");
        assertTrue(!attr.isInvalid());

        attr = new Attribute("q1", "1", "currency;min=0.20;max=21", "GBP");
        attr.setValue("0.12");
        assertTrue(attr.isInvalid());
        attr.setValue("22.54");
        assertTrue(attr.isInvalid());
        attr.setValue("10");
        assertTrue(!attr.isInvalid());

        attr = new Attribute("q1", "hello", "string;min=4;max=20");
        attr.setValue("ooo");
        assertTrue(attr.isInvalid());
        attr.setValue("ooooooooooooooooooooo");
        assertTrue(attr.isInvalid());
        attr.setValue("ooooooooooooo");
        assertTrue(!attr.isInvalid());

        attr = new Attribute("q1", "hello", "string;min=0;max=20");
        attr.setValue("");
        assertTrue(!attr.isInvalid());
        attr.setValue("oooooooooo");
        assertTrue(!attr.isInvalid());
        attr.setValue("ooooooooooooooooooooo");
        assertTrue(attr.isInvalid());

        attr = new Attribute("q1", "hello", "string;pattern=[0-9a-z]*");
        attr.setValue("abc123");
        assertTrue(!attr.isInvalid());
        attr.setValue("Aabc123");
        assertTrue(attr.isInvalid());

        attr = new Attribute("q1", "30/11/1978", "date;pattern=dd/MM/yyyy");
        attr.getObject();
    }

    @Test
    public void testAttributeFormatting() {
        Attribute attr;

        attr = new Attribute("amount5", "\u00A3" + "12.234", "number;pattern=\u00A3#.00");
        assertEquals("\u00A3" + "12.23", attr.getFormattedValue());
        assertEquals(12.234, attr.getObject());
    }

    @Test
    public void testReference() {
        Attribute ref = spy(new Attribute("refdata", "?", "choice,options=-1#?|1#Saloon|2#Coupe|3#Convertible"));

        doReturn(JXPathContext.newContext(ref)).when(ref).fetchJXPathContext();

        Attribute.setReferenceContext(ref);

        when(XPath.xpath("/format")).thenReturn("/format");
        Attribute attr = new Attribute("id123", "?", "ref=/format");
        assertFalse(attr.isInvalid());

        attr.setValue("Saloon");
        assertFalse(attr.isInvalid());

        attr.setValue("Hello");
        assertTrue(attr.isInvalid());
    }

    /**
     * Test that commas in a format option list are seen as part of the option
     * text itself, and not a format separator.
     */
    @Test
    public void testChoiceOptionWithCommas() {
        Attribute a = new Attribute("refdata", "?", "choice,options=-1#?|1#Saloon|2#House, Boat|3#Convertible, ship");
        assertEquals("-1#?|1#Saloon|2#House, Boat|3#Convertible, ship", a.getFormatOption("options"));
    }

    /**
     * Test choice attribute formatting
     */
    @Test
    public void testChoiceFormatting() {
        Attribute a = new Attribute("refdata", "Convertible, ship", "choice,options=-1#?|1#Saloon|2#House, Boat|3#Convertible, ship");
        assertEquals("Convertible, ship", a.getFormattedValue());
        assertEquals("Convertible, ship", a.getValue());
        assertEquals(3.0, a.getObject());

    }

    @Test
    public void testCurrencyAttribute() {
        Attribute attr = new Attribute("q1", "1002.23", "currency", "GBP");

        assertEquals(java.lang.String.class, attr.getValue().getClass());
        assertEquals(java.lang.Double.class, attr.getObject().getClass());

        assertEquals("\u00A3" + "1,002.23", attr.getFormattedValue());
        assertEquals("1002.23", attr.getValue());
        assertEquals(1002.23, attr.getObject());
    }

    /**
     * The getFormattedValue() method on attribute should return values
     * formatted for the current locale. This test checks that functionality.
     */
    @Test
    public void testLocaleSpecificCurrencyFormatting() throws Exception {

        // This test class may get run by developers anywhere in the world,
        // so put the JVM into a known locale for testing - and be sure to
        // switch back to the real one before returning.
        ThreadLocale runningLocale = ThreadLocale.getDefault();

        try {
            ThreadLocale.setDefault(new ThreadLocale(UK));
            Attribute gbp = new Attribute("q1", "1002.23", "currency", "GBP");

            // test that formatting works in the default locale
            assertEquals("\u00A3" + "1,002.23", gbp.getFormattedValue());

            Attribute usd = new Attribute("q1", "1002.23", "currency", "USD");

            // USD formatted for Canada
            ThreadLocale.setThreadLocale(CANADA);
            String v = usd.getFormattedValue();
            assertTrue(v.equals("US$1,002.23") || v.equals("USD1,002.23"));

            // USD formatted for USA
            ThreadLocale.setThreadLocale(US);
            assertEquals("$1,002.23", usd.getFormattedValue());

            // USD formatted for Germany
            ThreadLocale.setThreadLocale(GERMANY);
            assertEquals("1.002,23 USD", usd.getFormattedValue());

            ThreadLocale.setThreadLocale(US);
            usd.setFormat("currency;pattern=\u00A4 #,##0");
            assertEquals("$ 1,002", usd.getFormattedValue());
        } finally {
            ThreadLocale.setDefault(runningLocale);
        }
    }

    @Test
    public void testLocaleSpecificAUDCurrencyFormatting() throws Exception {

        // This test class may get run by developers anywhere in the world,
        // so put the JVM into a known locale for testing - and be sure to
        // switch back to the real one before returning.
        ThreadLocale runningLocale = ThreadLocale.getDefault();

        try {
            ThreadLocale.setThreadLocale(new Locale("en", "AU"));
            Attribute aud = new Attribute("q1", "1002.23", "currency", "AUD");

            // test that formatting works in the default locale
            assertEquals("$1,002.23", aud.getFormattedValue());
        } finally {
            ThreadLocale.setThreadLocale(runningLocale.getInstance());
        }
    }

    /**
     * The getFormattedValue() method on attribute should return values
     * formatted for the current locale. This test checks that functionality.
     */
    @Test
    public void testLocaleSpecificNumberFormatting() throws Exception {

        // This test class may get run by developers anywhere in the world,
        // so put the JVM into a known locale for testing - and be sure to
        // switch back to the real one before returning.
        ThreadLocale runningLocale = ThreadLocale.getDefault();

        try {
            ThreadLocale.setThreadLocale(UK);
            Attribute number = new Attribute("q1", "1002.23", "number");

            ThreadLocale.setThreadLocale(GERMANY);
            assertEquals("1.002,23", number.getFormattedValue());

            ThreadLocale.setThreadLocale(US);
            assertEquals("1,002.23", number.getFormattedValue());
        } finally {
            ThreadLocale.setDefault(runningLocale);
        }
    }

    /**
     * Values sent to an Attribute's set method may be in a format appropriate
     * to the current locale, or in the Attribute's own defined format
     * (pattern), or in a format suitable for the system's default locale. This
     * test checks these operations.
     */
    @Test
    public void testSetOperationsForLocaleSpecificdNumberValues() throws Exception {

        // This test class may get run by developers anywhere in the world,
        // so put the JVM into a known locale for testing - and be sure to
        // switch back to the real one before returning.
        ThreadLocale runningLocale = ThreadLocale.getDefault();

        try {
            ThreadLocale.setDefault(new ThreadLocale(UK));
            Attribute number = new Attribute("q1", "1002.23", "number");

            ThreadLocale.setThreadLocale(UK);
            number.setValue("2,004.32");
            assertEquals("2004.32", number.getValue());
            assertEquals(2004.32, number.getObject());

            ThreadLocale.setThreadLocale(GERMANY);
            number.setValue("1.002,64");
            assertEquals("1002.64", number.getValue());
            assertEquals(1002.64, number.getObject());
            number.setValue("9.021,131");
            assertEquals("9021.131", number.getValue());
            assertEquals(9021.131, number.getObject());

            ThreadLocale.setThreadLocale(UK);
            number.setValue("10,921.441");

            ThreadLocale.setThreadLocale(GERMANY);
            assertEquals("10921.441", number.getValue());
            assertEquals("10.921,441", number.getFormattedValue());
        } finally {
            ThreadLocale.setDefault(runningLocale);
        }
    }

    @Test
    public void testSetOperationsForLocaleSpecificdCurrencyValues() throws Exception {

        // This test class may get run by developers anywhere in the world,
        // so put the JVM into a known locale for testing - and be sure to
        // switch back to the real one before returning.
        ThreadLocale runningLocale = ThreadLocale.getDefault();

        try {
            ThreadLocale.setDefault(new ThreadLocale(UK));
            ThreadLocale.setThreadLocale(UK);

            Attribute money = new Attribute("q1", "1002.23", "currency", "GBP");
            money.setValue("1004.80");
            assertEquals("1004.8", money.getValue());
            money.setValue("2,001.90");
            assertEquals("2001.9", money.getValue());
            money.setValue("\u00A3" + "921.30");
            assertEquals("921.3", money.getValue());

            ThreadLocale.setThreadLocale(GERMANY);
            money.setValue("1004,80");
            assertEquals("1004.8", money.getValue());
            money.setValue("2.001,90");
            assertEquals("2001.9", money.getValue());
            money.setValue("921,30 GBP");
            assertEquals("921.3", money.getValue());
        } finally {
            ThreadLocale.setDefault(runningLocale);
        }
    }

    @Test
    public void testGetAndSetOperationsForPercentValues() throws Exception {

        // This test class may get run by developers anywhere in the world,
        // so put the JVM into a known locale for testing - and be sure to
        // switch back to the real one before returning.
        ThreadLocale runningLocale = ThreadLocale.getDefault();

        try {
            ThreadLocale.setDefault(new ThreadLocale(UK));
            ThreadLocale.setThreadLocale(UK);

            Attribute percent;

            percent = new Attribute("q1", "12%", "number,percent");
            assertEquals("12", percent.getValue());
            assertEquals(0.12, percent.getObject());
            assertEquals("12%", percent.getFormattedValue());

            percent = new Attribute("q1", "12", "number,percent");
            assertEquals("12", percent.getValue());
            assertEquals(.12, percent.getObject());
            assertEquals("12%", percent.getFormattedValue());

            percent.setValue("15");
            assertEquals("15", percent.getValue());
            assertEquals(.15, percent.getObject());
            assertEquals("15%", percent.getFormattedValue());

            percent.setValue("90%");
            assertEquals("90", percent.getValue());
            assertEquals(.9, percent.getObject());
            assertEquals("90%", percent.getFormattedValue());
        } finally {
            ThreadLocale.setDefault(runningLocale);
        }
    }

    @Test
    public void testNumberLocaleFormatting() throws Exception {
        // This test class may get run by developers anywhere in the world,
        // so put the JVM into a known locale for testing - and be sure to
        // switch back to the real one before returning.
        ThreadLocale runningLocale = ThreadLocale.getDefault();

        try {
            ThreadLocale.setDefault(new ThreadLocale(GERMANY));
            ThreadLocale.setThreadLocale(GERMANY);

            Attribute percent;

            percent = new Attribute("q1", "1000", "number,patter=#,###");
            assertEquals("1000", percent.getValue());
            assertEquals("1.000", percent.getFormattedValue());
        } finally {
            ThreadLocale.setDefault(runningLocale);
        }
    }

    @Test
    public void testYesOrNoWithRequiredEqualsNo() throws Exception {
        Attribute attr;
        attr = new Attribute("id", "Yes", "yesorno");
        assertTrue(attr.isYesornoType());
        assertFalse(attr.isUndefined());

        attr = new Attribute("id", "No", "yesorno");
        assertTrue(attr.isYesornoType());
        assertFalse(attr.isUndefined());

        attr = new Attribute("id", "?", "yesorno");
        assertTrue(attr.isYesornoType());
        assertTrue(attr.isUndefined());

        attr = new Attribute("id", "?", "yesorno;required=no");
        assertTrue(attr.isYesornoType());
        assertTrue(attr.isUndefined());
    }

    @Test
    public void testChoiceWithNonstandardBlank() throws Exception {
        Attribute attr;

        attr = new Attribute("id", "-", "choice,options=-1#-|1#Saloon|2#Coupe|3#Convertible");
        assertTrue(attr.isUndefined());

        attr.setValue("Saloon");
        assertFalse(attr.isUndefined());
    }

    @Test
    public void testRequiredOption() {
        Attribute attr;

        attr = new Attribute("id", "-", "string");
        assertTrue(attr.isRequired());

        attr = new Attribute("id", "-", "string,required=yes");
        assertTrue(attr.isRequired());

        attr = new Attribute("id", "-", "string,required=no");
        assertFalse(attr.isRequired());
    }

    @Test
    public void testNumericPatternFormatsRespectPatternSymbols() {
        Attribute attr;

        attr = new Attribute("id", "1234", "number,pattern=###");
        assertThat(attr.formatter(), is(instanceOf(DecimalFormat.class)));
        assertThat(((DecimalFormat) attr.formatter()).isGroupingUsed(), is(false));

        attr = new Attribute("id", "1234", "number,pattern=#,###");
        assertThat(attr.formatter(), is(instanceOf(DecimalFormat.class)));
        assertThat(((DecimalFormat) attr.formatter()).isGroupingUsed(), is(true));
    }

    @Test
    @Ignore
    public void shouldApplyMinDateValiation() {
        Calendar today = Calendar.getInstance(ThreadLocale.getThreadLocale());

        Calendar past = Calendar.getInstance(ThreadLocale.getThreadLocale());
        past.add(DATE, -20);

        Calendar future = Calendar.getInstance(ThreadLocale.getThreadLocale());
        future.add(DATE, 20);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        assertTrue("with 'date,min=10' 20 days ago should be invalid.", new Attribute("id", sdf.format(past.getTime()), "date,min=10").isInvalid());
        assertTrue("with 'date,min=10' today should be invalid.", new Attribute("id", sdf.format(today.getTime()), "date,min=10").isInvalid());
        assertFalse("with 'date,min=10' 20 days in the future should be valid.", new Attribute("id", sdf.format(future.getTime()), "date,min=10").isInvalid());
        assertFalse("with 'date,min=-10' today should be valid.", new Attribute("id", sdf.format(future.getTime()), "date,min=-10").isInvalid());
    }

    @Test
    @Ignore
    public void shouldApplyMaxDateValiation() {
        Calendar today = Calendar.getInstance(ThreadLocale.getThreadLocale());

        Calendar past = Calendar.getInstance(ThreadLocale.getThreadLocale());
        past.add(DATE, -20);

        Calendar future = Calendar.getInstance(ThreadLocale.getThreadLocale());
        future.add(DATE, 20);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        assertFalse("with 'date,max=10' 20 days ago should be valid.", new Attribute("id", sdf.format(past.getTime()), "date,max=10").isInvalid());
        assertFalse("with 'date,max=10' today should be valid.", new Attribute("id", sdf.format(today.getTime()), "date,max=10").isInvalid());
        assertTrue("with 'date,max=10' 20 days in the future should be invalid.", new Attribute("id", sdf.format(future.getTime()), "date,max=10").isInvalid());
        assertTrue("with 'date,max=-10' today should be invalid.", new Attribute("id", sdf.format(future.getTime()), "date,max=-10").isInvalid());
    }

    @Test
    @Ignore
    public void shouldApplyCombinedMaxAndMaxDateValiation() {
        Calendar today = Calendar.getInstance();

        Calendar past = Calendar.getInstance();
        past.add(DATE, -20);

        Calendar future = Calendar.getInstance();
        future.add(DATE, 20);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        assertTrue("with 'date,min=-10,max=10' 20 days ago should be valid.", new Attribute("id", sdf.format(past.getTime()), "date,min=-10,max=10").isInvalid());
        assertFalse("with 'date,min=-10,max=10' today should be valid.", new Attribute("id", sdf.format(today.getTime()), "date,min=-10,max=10").isInvalid());
        assertTrue("with 'date,min=-10,max=10' 20 days in the future should be invalid.", new Attribute("id", sdf.format(future.getTime()), "date,min=-10,max=10").isInvalid());
    }

    @Test
    public void getFormattedValueForNumberAttributesShouldNotThrowExceptionsForBadFormatting() {
        assertEquals("plop", new Attribute("id", "plop", "number").getFormattedValue());
        assertEquals("", new Attribute("id", null, "number").getFormattedValue());
    }

    @Test
    public void testCurrenctSplitWithValue() {
        ThreadLocale runningLocale = ThreadLocale.getDefault();

        try {
            ThreadLocale.setDefault(new ThreadLocale(UK));
            ThreadLocale.setThreadLocale(UK);

            String[] values = new Attribute("id", "0", "currency", "GBP").getCurrencySplitValue();

            assertTrue(values[0].indexOf('\u00A3') != -1);
            assertThat(values[1], is("0.00"));
            assertThat(values[2], is(""));
        } finally {
            ThreadLocale.setDefault(runningLocale);
        }
    }

    @Test
    public void testCurrenctSplitWithNullValue() {
        ThreadLocale runningLocale = ThreadLocale.getDefault();

        try {
            ThreadLocale.setDefault(new ThreadLocale(UK));
            ThreadLocale.setThreadLocale(UK);

            String[] values = new Attribute("id", null, "currency", "GBP").getCurrencySplitValue();

            assertTrue(values[0].indexOf('\u00A3') != -1);
            assertThat(values[1], is(nullValue()));
            assertThat(values[2], is(""));
        } finally {
            ThreadLocale.setDefault(runningLocale);
        }
    }

    @Test
    public void whenRequiredIsFalseBlankDatesShouldValidateOkay() {
        Attribute attr = new Attribute("id", "", "date,required=no,pattern=yyyy");
        assertFalse(attr.isInvalid());
    }

    @Test
    public void testThatCurrencyAttributesObjectRespectsTrailingZeros() {
        ThreadLocale runningLocale = ThreadLocale.getDefault();
        Attribute attr;

        try {
            ThreadLocale.setThreadLocale(UK);
            attr = new Attribute("id", "1.20", "currency", "EUR");
            assertThat((Number)attr.getObject(), is((Number)new Double(1.2)));

            ThreadLocale.setThreadLocale(ITALIAN);
            attr = new Attribute("id", "1,20", "currency,pattern=#.00", "EUR");
            assertThat((Number)attr.getObject(), is((Number)new Double(1.2)));
        } finally {
            ThreadLocale.setDefault(runningLocale);
        }
    }

    @Test
    public void testDeepEquals() {
        Attribute a = new Attribute("id", "value", "format");
        Attribute b = new Attribute("id", "value", "format");

        assertThat(a, is(equalTo(b)));

        b.getAttribute().add(new Attribute("id", "value", "format"));

        assertThat(a, is(not(equalTo(b))));
    }

    @Test
    public void testGetFormatType() {
        Attribute basicFormat = new Attribute("id", "value", "format");
        Attribute formatWithCommaOption = new Attribute("id", "value", "format,option=X");
        Attribute formatWithColonOption = new Attribute("id", "value", "format;option=Y");
        Attribute blankFormat = new Attribute("id", "value", "");
        Attribute nullFormat = new Attribute("id", "value", null);

        assertThat(basicFormat.getFormatType(), is("format"));
        assertThat(formatWithCommaOption.getFormatType(), is("format"));
        assertThat(formatWithColonOption.getFormatType(), is("format"));
        assertThat(blankFormat.getFormatType(), is(""));
        assertThat(nullFormat.getFormatType(), is((String)null));
    }

    @Test
    public void testTogglingRequiredByApiCall() {
        Attribute attr;

        attr = new Attribute("id", "", "number;required=yes");

        attr.setRequired(false);
        assertThat(attr.isInvalid(), is(false));

        attr.setRequired(true);
        assertThat(attr.isInvalid(), is(true));
    }

    @Test
    public void testPatternUpdateByApiCall() {
        Attribute attr;
        attr = new Attribute("q1", "30/11/1978", "date;pattern=dd/MM/yyyy");
        assertThat(attr.isInvalid(), is(false));

        attr.setPattern("dd-MM-yyyy");
        assertThat(attr.isInvalid(), is(true));
        attr.setValue("30-11-1978");
        assertThat(attr.isInvalid(), is(false));
    }

    @Test
    public void testMaxUpdateByApiCall() {
        Attribute attr;
        attr = new Attribute("q1", "0123456789", "string;max=20");
        assertThat(attr.isInvalid(), is(false));

        attr.setMax(5);
        assertThat(attr.isInvalid(), is(true));
        attr.setValue("123");
        assertThat(attr.isInvalid(), is(false));
    }

    @Test
    public void testMinUpdateByApiCall() {
        Attribute attr;
        attr = new Attribute("q1", "0123456789", "string;min=5");
        assertThat(attr.isInvalid(), is(false));

        attr.setMin(15);
        assertThat(attr.isInvalid(), is(true));
        attr.setValue("01234567890123456789");
        assertThat(attr.isInvalid(), is(false));
    }

    @Test
    public void testOptionsUpdateByApiCall() {
        Attribute attr;
        attr = new Attribute("q1", "Convertible", "choice,options=-1#?|1#Saloon|2#Coupe|3#Convertible");
        assertThat(attr.isInvalid(), is(false));

        attr.setOptions("-1#?|1#Saloon|2#Coupe");;
        assertThat(attr.isInvalid(), is(true));
        attr.setValue("Saloon");
        assertThat(attr.isInvalid(), is(false));
    }

    @Test
    public void testPlaceholderUpdateByApiCall() {
        Attribute attr;
        attr = new Attribute("q1", "", "string;placeholder=test test");
        assertThat(attr.getFormatOption("placeholder"), is("test test"));
        attr.setPlaceholder("new placeholder");;
        assertThat(attr.getFormatOption("placeholder"), is("new placeholder"));
    }

    @Test
    public void testRemoveOptionsAfterWhenOptionIsFound() {
        Attribute attr = new Attribute("id", "value", "choice,options=-1#?|1#Saloon|2#Coupe|3#Convertible");
        attr.removeChoiceOptionsAfter("Coupe");
        assertThat(attr.getFormatOption("options"), is("-1#?|1#Saloon|2#Coupe"));
    }

    @Test
    public void testRemoveOptionsAfterWhenOptionIsNotFound() {
        Attribute attr = new Attribute("id", "value", "choice,options=-1#?|1#Saloon|2#Coupe|3#Convertible");
        attr.removeChoiceOptionsAfter("Hatchback");
        assertThat(attr.getFormatOption("options"), is("-1#?|1#Saloon|2#Coupe|3#Convertible"));
    }

    @Test
    public void testRemoveOptionsAfterWhenThereAreNoOptions() {
        Attribute attr = new Attribute("id", "value", "choice,options=");
        attr.removeChoiceOptionsAfter("Hatchback");
        assertThat(attr.getFormatOption("options"), is(""));
    }

    @Test
    public void testRemoveOptionsAfterWhenAttributeIsNotAChoice() {
        Attribute attr = new Attribute("id", "value", "number");
        attr.removeChoiceOptionsAfter("Hatchback");
        assertThat(attr.getFormatOption("options"), is((String)null));
    }

    @Test
    public void testRemoveOptionsBeforeWhenOptionIsFound() {
        Attribute attr = new Attribute("id", "value", "choice,options=-1#?|1#Saloon|2#Coupe|3#Convertible");
        attr.removeChoiceOptionsBefore("Coupe");
        assertThat(attr.getFormatOption("options"), is("2#Coupe|3#Convertible"));
    }

    @Test
    public void testRemoveOptionsBeforeWhenOptionIsNotFound() {
        Attribute attr = new Attribute("id", "value", "choice,options=-1#?|1#Saloon|2#Coupe|3#Convertible");
        attr.removeChoiceOptionsBefore("Hatchback");
        assertThat(attr.getFormatOption("options"), is("-1#?|1#Saloon|2#Coupe|3#Convertible"));
    }

    @Test
    public void testRemoveOptionsBeforeWhenThereAreNoOptions() {
        Attribute attr = new Attribute("id", "value", "choice,options=");
        attr.removeChoiceOptionsBefore("Hatchback");
        assertThat(attr.getFormatOption("options"), is(""));
    }

    @Test
    public void testRemoveOptionsBeforeWhenAttributeIsNotAChoice() {
        Attribute attr = new Attribute("id", "value", "number");
        attr.removeChoiceOptionsBefore("Hatchback");
        assertThat(attr.getFormatOption("options"), is((String)null));
    }
}
