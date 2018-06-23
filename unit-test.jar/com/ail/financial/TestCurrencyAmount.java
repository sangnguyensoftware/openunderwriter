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

package com.ail.financial;

import static com.ail.financial.Currency.GBP;
import static com.ail.financial.Currency.USD;
import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Test;

import com.ail.core.ThreadLocale;
import com.ail.financial.Currency;
import com.ail.financial.CurrencyAmount;
import com.ail.util.Rate;

public class TestCurrencyAmount {

    @Test
    public void testGoodValues() {
        new CurrencyAmount("110.21", USD);
        new CurrencyAmount(110.21, USD);
        new CurrencyAmount(new BigDecimal("110.21"), USD);
    }

    @Test
    public void testCurrencyScale() {
        CurrencyAmount ca;

        ca=new CurrencyAmount("100", USD);
        assertEquals("100.00", ca.getAmountAsString());

        ca=new CurrencyAmount(".24", USD);
        assertEquals("0.24", ca.getAmountAsString());
    }

    @Test
    public void testCurrencyApplyRate() {
        CurrencyAmount ca;
        Rate rate;

        rate=new Rate("25%");
        ca=new CurrencyAmount(50, USD);
        ca=ca.apply(rate);
        assertEquals("12.50", ca.getAmountAsString());
    }

    /**
     * A CurrencyAmount created with the default constructor should
     * (when its add/subtract method is called) automatically assume
     * the currency of the CurrencyAmount passed in.
     */
    @Test
    public void testAutoCreate() {
        CurrencyAmount test;

        test=new CurrencyAmount(0, USD);
        test=test.add(new CurrencyAmount(10, USD));
        assertEquals(USD, test.getCurrency());
        assertTrue(10.0==test.getAmount().doubleValue());

        test=new CurrencyAmount(0, GBP);
        test=test.subtract(new CurrencyAmount(10, GBP));
        assertEquals(GBP, test.getCurrency());
        assertTrue(-10.0==test.getAmount().doubleValue());
    }

    /**
     * Test the <code>greaterThan</code> method.
     * <ol>
     * <li>Create two Currency amounts (test1 & test2), set test1 to 100USD and
     * test2 to 101USD.</li>
     * <li>Fail if test2.greaterThan(test1) returns false.</li>
     * <li>Fail if test1.greaterThan(test2) returns true.</li>
     * <li>Repeat the previous tests using the overloaded amount, currency method.</li>
     * <li>Create another CurrencyAmount (test3) for 101GBP.</li>
     * <li>Fail if test1.greaterThan(test3) does anything other than throwing an IllegalArgumentException.</li>
     * <li>Fail if test3.greaterThan(test1) does anything other than throwing an IllegalArgumentException.</li>
     * <li>Repeat the previous test using the overloaded amount, currency method.</li>
     * </ol>
     */
    @Test
    public void testGreaterThan() {
        CurrencyAmount test1, test2;

        test1=new CurrencyAmount(100.00, USD);
        test2=new CurrencyAmount(101.00, USD);

        assertTrue(test2.greaterThan(test1));
        assertTrue(!test1.greaterThan(test2));

        assertTrue(test2.greaterThan(100.00, USD));
        assertTrue(!test1.greaterThan(101.00, USD));

        CurrencyAmount test3=new CurrencyAmount(101.00, GBP);

        try {
            test1.greaterThan(test3);
            fail("IllegalArgumentException not thrown");
        }
        catch(IllegalArgumentException e) {
            // ok, this is what we want.
        }
        catch(Throwable e) {
            fail("Wrong exception thrown:"+e);
        }

        try {
            test3.greaterThan(100.00, USD);
            fail("IllegalArgumentException not thrown");
        }
        catch(IllegalArgumentException e) {
            // ok, this is what we want.
        }
        catch(Throwable e) {
            fail("Wrong exception thrown:"+e);
        }
    }

    /**
     * Test the <code>lessThan</code> method.
     * <ol>
     * <li>Create two Currency amounts (test1 & test2), set test1 to 101USD and
     * test2 to 100USD.</li>
     * <li>Fail if test2.lessThan(test1) returns false.</li>
     * <li>Fail if test1.lessThan(test2) returns true.</li>
     * <li>Repeat the previous tests using the overloaded amount, currency method.</li>
     * <li>Create another CurrencyAmount (test3) for 100GBP.</li>
     * <li>Fail if test1.lessThan(test3) does anything other than throwing an IllegalArgumentException.</li>
     * <li>Fail if test3.lessThan(test1) does anything other than throwing an IllegalArgumentException.</li>
     * <li>Repeat the previous test using the overloaded amount, currency method.</li>
     * </ol>
     */
    @Test
    public void testLessThan() {
        CurrencyAmount test1, test2;

        test1=new CurrencyAmount(101.00, USD);
        test2=new CurrencyAmount(100.00, USD);

        assertTrue(test2.lessThan(test1));
        assertTrue(!test1.lessThan(test2));

        assertTrue(test2.lessThan(101.00, USD));
        assertTrue(!test1.lessThan(100.00, USD));

        CurrencyAmount test3=new CurrencyAmount(101.00, GBP);

        try {
            test1.lessThan(test3);
            fail("IllegalArgumentException not thrown");
        }
        catch(IllegalArgumentException e) {
            // ok, this is what we want.
        }
        catch(Throwable e) {
            fail("Wrong exception thrown:"+e);
        }

        try {
            test3.lessThan(100.00, USD);
            fail("IllegalArgumentException not thrown");
        }
        catch(IllegalArgumentException e) {
            // ok, this is what we want.
        }
        catch(Throwable e) {
            fail("Wrong exception thrown:"+e);
        }
    }

    /**
     * Currency provides a method to format it's value into a String in a format
     * appropriate to the current ThreadLocale (as defined by the {@link com.ail.core.ThreadLocale}).
     */
    @Test
    public void testFormatting() {
        CurrencyAmount test;

        ThreadLocale.setThreadLocale(java.util.Locale.UK);
        test=new CurrencyAmount(100000, GBP);
        assertEquals("\u00A3"+"100,000.00", test.toFormattedString());

        ThreadLocale.setThreadLocale(java.util.Locale.US);
        assertEquals("GBP100,000.00", test.toFormattedString());

        ThreadLocale.setThreadLocale(java.util.Locale.GERMANY);
        assertEquals("100.000,00 GBP", test.toFormattedString());
    }
    
    @Test
    public void testFractionalDigits() {
        CurrencyAmount test;

        ThreadLocale.setThreadLocale(java.util.Locale.UK);
        test=new CurrencyAmount(100000, Currency.JPY);
        assertEquals("JPY100,000", test.toFormattedString());

        test=new CurrencyAmount(100000, Currency.USD);
        assertEquals("USD100,000.00", test.toFormattedString());
    }
    
    @Test
    public void testChaining() {
        CurrencyAmount test;
        
        test=new CurrencyAmount(100, GBP);
        test=test.add(new CurrencyAmount(10, GBP)).add(new CurrencyAmount(10, GBP));
        assertEquals(new CurrencyAmount(120, GBP), test);

        test=new CurrencyAmount(100, GBP);
        test=test.subtract(new CurrencyAmount(10, GBP)).subtract(new CurrencyAmount(10, GBP));
        assertEquals(new CurrencyAmount(80, GBP), test);

        test=new CurrencyAmount(90, GBP);
        test=test.add(new CurrencyAmount(10, GBP)).apply(new Rate("40%"));
        assertEquals(new CurrencyAmount(40, GBP), test);
    }
    
    @Test
    public void testCurrencyAmountForBrazil() {
        java.util.Locale saved=java.util.Locale.getDefault();
        try {
            ThreadLocale.setThreadLocale(new java.util.Locale("pt", "BR"));
            assertEquals(new BigDecimal("113.40"), new CurrencyAmount("113,40", "GBP").getAmount());
            assertEquals(new BigDecimal("-113.40"), new CurrencyAmount("-113,40", "GBP").getAmount());
            assertEquals(new BigDecimal("1113.40"), new CurrencyAmount("1.113,40", "GBP").getAmount());

            ThreadLocale.setThreadLocale(new java.util.Locale("en", "GB"));
            assertEquals(new BigDecimal("113.40"), new CurrencyAmount("113.40", "GBP").getAmount());
            assertEquals(new BigDecimal("-113.40"), new CurrencyAmount("-113.40", "GBP").getAmount());
            assertEquals(new BigDecimal("1113.40"), new CurrencyAmount("1,113.40", "GBP").getAmount());
        }
        finally {
            java.util.Locale.setDefault(saved);
        }
    }
    
    @Test
    public void testStringConversions() {
        BigDecimal amount=new BigDecimal("2000");
        assertEquals(2000, amount.intValue());
        CurrencyAmount ca=new CurrencyAmount("2000", "GBP");
        assertEquals(2000, ca.getAmount().intValue());
    }
}
