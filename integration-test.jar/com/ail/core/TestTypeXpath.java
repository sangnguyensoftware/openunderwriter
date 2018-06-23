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

package com.ail.core;

import static java.util.Locale.UK;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ail.core.configure.ConfigurationHandler;
import com.ail.core.data.DataDictionary;
import com.ail.core.data.Definition;

/**
 * Test the Type class' xpath handling
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({CoreContext.class})
public class TestTypeXpath extends CoreUserBaseCase {
    boolean initialised = false;
    private java.util.Locale savedLocale;

    @Mock
    private CoreProxy coreProxy;

    private DataDictionary testDataDictionary;

    @Before
    public void setUp() {
        if (!initialised) {
            super.setupSystemProperties();
            ConfigurationHandler.resetCache();
            setVersionEffectiveDate(new VersionEffectiveDate());
            setCore(new Core(this));
            getCore().resetConfiguration();
            setVersionEffectiveDate(new VersionEffectiveDate());
            ConfigurationHandler.resetCache();
            initialised = true;

            testDataDictionary = new DataDictionary();
            testDataDictionary.addDefinition(new Definition("attrib1", "attribute[id='attrib1']"));
            testDataDictionary.addDefinition(new Definition("attrib2", "attribute[id='attrib2']"));
            testDataDictionary.addDefinition(new Definition("gender1", "attribute[id='gender1']"));
            testDataDictionary.addDefinition(new Definition("gender2", "attribute[id='gender2']"));
            testDataDictionary.addDefinition(new Definition("amount1", "attribute[id='amount1']"));
            testDataDictionary.addDefinition(new Definition("amount2", "attribute[id='amount2']"));
            testDataDictionary.addDefinition(new Definition("amount3", "attribute[id='amount3']"));
            testDataDictionary.addDefinition(new Definition("amount5", "attribute[id='amount5']"));

            mockStatic(CoreContext.class);

            when(CoreContext.getProductName()).thenReturn("TEST");
            when(CoreContext.getCoreProxy()).thenReturn(coreProxy);
            when(coreProxy.newProductType("TEST", "DataDictionary", DataDictionary.class)).thenReturn(testDataDictionary);

        }
    }

    @Before
    public void setupLocale() {
        savedLocale = ThreadLocale.getThreadLocale();
        ThreadLocale.setThreadLocale(UK);
    }

    @After
    public void resetLocale() {
        ThreadLocale.setThreadLocale(savedLocale);
    }

    /**
     * Test some simple gets and sets using Type's xpath methods. Type offers
     * support for processing XPATH expressions against a Type's instance. This
     * test performs some basic checks to ensure the support is working.
     * <ol>
     * <li>Create an instance of Version (Author="J.R.Hartley", Version="2").</li>
     * <li>Fail if the xpath "author" doesn't return "J.R.Hartley".</li>
     * <li>Fail if the xpath "version" doesn't return "2".</li>
     * <li>Fail if the xpath "version" specifying Integer as the return doesn't
     * return Integer(2).</li>
     * <li>Use xpathSet to set "version" to "45"
     * <li>Fail if the xpath "version" specifying Integer as the return doesn't
     * return Integer(45).</li>
     * <li>Fail if any exceptions are thrown</li>
     * </ol>
     */
    @Test
    public void testSimpleXpathGetAndSet() throws Exception {
        Version version = new Version();
        version.setAuthor("J.R.Hartley");
        version.setVersion("2");

        assertEquals("J.R.Hartley", version.xpathGet("author"));
        assertEquals("2", (String) version.xpathGet("version"));
        assertEquals(new Integer(2), (Integer) version.xpathGet("version", Integer.class));

        version.xpathSet("version", "45");
        assertEquals(new Integer(45), (Integer) version.xpathGet("version", Integer.class));

        try {
            assertNull(version.xpathGet("this/path/does/not/exist"));
            fail("Expected exception not thrown");
        } catch (TypeXPathException e) {
            // ignore this - it's what we want.
        } catch (Throwable t) {
            fail("wrong exception thrown (" + t + ")");
        }
    }

    /**
     * Test some more complex gets and sets using Type's xpath methods. Type
     * offers support for processing XPATH expressions against a Type's
     * instance. This test performs some basic checks to ensure the support is
     * working.
     * <ol>
     * </ol>
     */
    @Test
    public void testComplexXpathGetAndSet() throws Exception {
        History history = new History();
        history.setSerialVersion(21);
        history.setLock(true);
        for (int i = 0; i < 10; i++) {
            Version v = new Version();
            v.setAuthor("author" + i);
            v.setComment("comment" + i);
            v.setCopyright("copyright" + i);
            v.setDate("date" + i);
            v.setSource("source" + i);
            v.setState("state" + i);
            v.setVersion("version" + i);
            history.addVersion(v);
        }

        assertEquals(new Integer(21), history.xpathGet("serialVersion", Integer.class));
        assertEquals("author0", history.xpathGet("version[1]/author"));
        assertEquals("author0", history.xpathGet("version[1]/author", String.class));
    }

    /**
     * Test some attribute xpaths
     */
    @Test
    public void testAttributeXpathGetAndSet() throws Exception {
        Version v = new Version();
        v.setAuthor("author");
        v.setComment("comment");
        v.setCopyright("copyright");
        v.setDate("date");
        v.setSource("source");
        v.setState("state");
        v.setVersion("version");

        v.addAttribute(new Attribute("attrib1", "hello", "string"));
        v.addAttribute(new Attribute("attrib2", "30/11/1978", "date,pattern=dd/MM/yyyy"));
        v.addAttribute(new Attribute("gender1", "Male", "choice,options=-1#?|1#Male|2#Female"));
        v.addAttribute(new Attribute("gender2", "?", "choice,options=-1#?|1#Male|2#Female"));
        v.addAttribute(new Attribute("amount1", "\u00A3"+"12", "currency", "GBP"));
        v.addAttribute(new Attribute("amount2", "12", "currency", "EUR"));
        v.addAttribute(new Attribute("amount3", "12%", "number,percent"));
        v.addAttribute(new Attribute("amount5", "\u00A3"+"12.234", "number,pattern=\u00A3#.##"));
        v.addAttribute(new Attribute("answer", "Yes", "yesorno"));

        try {
            v.addAttribute(new Attribute("amount4", "12", "currency"));
        } catch (IllegalStateException e) {
            // this is good - you can't create a currency attribute without
            // defining the currency itself as the unit.
        }

        Calendar c = Calendar.getInstance();
        c.set(1978, 10, 30, 0, 0, 0);

        assertEquals("hello", v.xpathGet("attrib1/value"));
        assertEquals("30/11/1978", v.xpathGet("attrib2/value"));
        assertEquals(c.getTime().toString(), v.xpathGet("attrib2/object", Date.class).toString());
        assertEquals(new Double(1.0), (Double) v.xpathGet("gender1/object"));
        assertEquals("12", v.xpathGet("amount1/value"));
        assertEquals("\u00A3"+"12.00", v.xpathGet("amount1/formattedValue"));
        assertEquals("12", v.xpathGet("amount1/object", String.class));
        assertEquals("12", v.xpathGet("amount2/object", String.class));
        assertEquals("12", v.xpathGet("amount2/value"));
        assertEquals("Male", v.xpathGet("gender1/formattedValue"));

        assertEquals(0.12, v.xpathGet("amount3/object"));
        assertEquals("12", (String) v.xpathGet("amount3/value"));
        assertEquals("12%", (String) v.xpathGet("amount3/formattedValue"));

        assertEquals(new Double(12.234), (Double) v.xpathGet("amount5/object"));
        assertEquals("12.234", v.xpathGet("amount5/value"));
        assertEquals("\u00A3"+"12.23", (String) v.xpathGet("amount5/formattedValue"));
        assertEquals("\u20AC"+"12.00", (String) v.xpathGet("amount2/formattedValue"));
        assertEquals("Male", (String) v.xpathGet("gender1/value"));
        assertEquals(new Double(1.0), (Double) v.xpathGet("gender1/object"));
        assertEquals("Male", (String) v.xpathGet("gender1/formattedValue"));
        assertEquals("?", (String) v.xpathGet("gender2/value"));
        assertEquals(new Double(-1.0), (Double) v.xpathGet("gender2/object"));
        assertEquals("?", (String) v.xpathGet("gender2/formattedValue"));
    }

    @Test
    public void testFunctions() {
        TypeXPathFunctionRegister.getInstance().registerFunctionLibrary("test",
                Functions.class);

        Version v = new Version();

        v.addAttribute(new Attribute("attrib1", "", "string"));
        v.addAttribute(new Attribute("attrib2", "30/11/1978", "date,pattern=dd/MM/yyyy"));
        v.addAttribute(new Attribute("gender1", "Male", "choice,options=-1#?|1#Male|2#Female"));
        v.addAttribute(new Attribute("gender2", "?", "choice,options=-1#?|1#Male|2#Female"));

        Functions.setAttributeValue(v, "attrib1", "hello");

        // 'standard' xpath function
        assertEquals(new Double(4.0), v.xpathGet("count(/attribute)", Double.class));
        assertEquals("hello", v.xpathGet("attribute[id='attrib1']/value"));

        // One of ours from com.ail.core.Function
        assertEquals("30 November, 1978", v.xpathGet("test:format(attrib2/object, 'dd MMMM, yyyy')"));
        assertEquals("hello", Functions.getAttributeValue(v, "attrib1"));
    }

    @Test
    public void testXpathFunctionRegistration() {
        Version v = new Version();

        v.addAttribute(new Attribute("dob1", "30/11/1978", "date,pattern=dd/MM/yyyy"));
        v.addAttribute(new Attribute("dob2", "30/11/1986", "date,pattern=dd/MM/yyyy"));
        v.addAttribute(new Attribute("dob3", "30/11/1964", "date,pattern=dd/MM/yyyy"));

        // register some new functions
        TypeXPathFunctionRegister.getInstance().registerFunctionLibrary("test", LocalFunctions.class);

        // invoke the newly registered function
        assertEquals(39, v.xpathGet("test:age(attribute[id='dob1'])"));
        assertEquals(31, v.xpathGet("test:age(test:youngest(attribute))"));

        // check that the "standard" functions still work
        assertEquals(new Double(3.0), v.xpathGet("count(/attribute)", Double.class));
    }

    @Test
    public void testCollection() {
        Version v = new Version();

        v.addAttribute(new Attribute("attrib1", "hello", "string"));
        v.addAttribute(new Attribute("attrib2", "30/11/1978", "date,pattern=dd/MM/yyyy"));
        v.addAttribute(new Attribute("gender1", "Male", "choice,options=-1#?|1#Male|2#Female"));
        v.addAttribute(new Attribute("gender2", "?", "choice,options=-1#?|1#Male|2#Female"));

        Object o = v.xpathGet("attribute");

        assertTrue(o instanceof List);
    }

    /**
     * Check the both relative and absolute xpath references are handled
     * correctly.
     */
    @Test
    public void testAbsoluteAndRelative() {
        Version version = new Version();
        version.setAuthor("J.R.Hartley");

        assertEquals("J.R.Hartley", version.xpathGet("/author"));
        assertEquals("J.R.Hartley", version.xpathGet("./author"));
    }

    /**
     * Test some attribute xpaths
     */
    @Test
    @SuppressWarnings("rawtypes")
    public void testAttributeXpathIterate() throws Exception {
        Version v = new Version();

        v.addAttribute(new Attribute("attrib1", "hello", "string"));
        v.addAttribute(new Attribute("attrib2", "30/11/1978", "date,pattern=dd/MM/yyyy"));
        v.addAttribute(new Attribute("gender1", "Male", "choice,options=-1#?|1#Male|2#Female"));
        v.addAttribute(new Attribute("gender2", "?", "choice,options=-1#?|1#Male|2#Female"));
        v.addAttribute(new Attribute("amount1", "\u00A3"+"12", "currency", "GBP"));
        v.addAttribute(new Attribute("amount2", "12", "currency", "EUR"));
        v.addAttribute(new Attribute("amount3", "12%", "number,percent"));
        v.addAttribute(new Attribute("amount5", "\u00A3"+"12.234", "number,pattern=\u00A3#.##"));
        v.addAttribute(new Attribute("answer", "Yes", "yesorno"));

        int count = 0;
        for (Iterator i = v.xpathIterate("/attribute"); i.hasNext(); count++) {
            i.next();
        }
        assertEquals("Wrong number of Attribute returned", count, 9);

        count = 0;
        for (Iterator<Attribute> i = v.xpathIterate("/attribute",  Attribute.class); i.hasNext(); count++) {
            i.next();
        }

        assertEquals("Wrong number of Attribute returned", count, 9);
    }

    @Test
    public void testXpathWithNullArguments() {
        Attribute attr=new Attribute("AttrId", "string value", "string");
        assertEquals(null, attr.xpathGet(null));
        assertEquals(null, attr.xpathGet(null, String.class));
        assertEquals(null, attr.xpathIterate(null));
        assertEquals(null, attr.xpathIterate(null, String.class));
    }

    @Test
    public void testJxpathWithClass() {
        Version v=new Version();

        assertThat(v.xpathGet("c:simpleName(/)"), is("Version"));
    }

    /**
     * Define a few simple functions for use from JXPath expressions
     *
     */
    public static class LocalFunctions {
        public static Integer yearsSince(Date date, Date when) {
            // create a calendar to represent the date of birth (or whatever)
            Calendar then = Calendar.getInstance();
            then.setTime(date);

            // create a calendar for now
            Calendar now = Calendar.getInstance();
            now.setTime(when);

            // age is the difference in years between then and now
            int age = now.get(Calendar.YEAR) - then.get(Calendar.YEAR);

            // if the anniversary hasn't yet passed this year, subtract 1
            then.add(Calendar.YEAR, age);
            if (now.before(then)) {
                age--;
            }

            return age;
        }

        public static Integer age(Attribute attrib) {
            if (attrib.isDateType()) {
                return yearsSince((Date) attrib.getObject(), new Date());
            }
            return 0;
        }

        /**
         * Returns the youngest of a collection of date attributes. The
         * collection is assumed to be made up attributes (and only attributes)
         * and only attributes of type date will be taken into account - any
         * other attributes will be ignored.
         *
         * @param attribs
         *            Collection of date attributes
         * @return The youngest date in the collection.
         */
        public static Attribute youngest(Collection<Attribute> attribs) {
            Attribute youngest = null;

            for (Attribute at : attribs) {
                if (at.isDateType()) {
                    if (youngest == null
                            || ((Date) youngest.getObject()).before((Date) at
                                    .getObject())) {
                        youngest = at;
                    }
                }
            }

            return youngest;
        }
    }
}
