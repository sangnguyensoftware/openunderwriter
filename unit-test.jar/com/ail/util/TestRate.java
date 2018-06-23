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

package com.ail.util;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;

import org.junit.Test;

public class TestRate {

    @Test
    public void testGoodProperFormats() throws Exception {
        new Rate("3/20");
        new Rate("10%");
        new Rate("10percent");
        new Rate("10PeRcEnT");
        new Rate("100PERMIL");
        new Rate("100Permil");
        new Rate("0.5");
    }

    @Test
    public void testGoodImproperFormats() throws Exception {
        new Rate("20/3");
        new Rate("110%");
        new Rate("110percent");
        new Rate("110PeRcEnT");
        new Rate("1100PERMIL");
        new Rate("1100Permil");
        new Rate("2.5");
    }

    /**
     * Test how badly formatted arguments are handled by the constructor.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testBadStringAlpha() {
        new Rate("abcd");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRateLessThanZero() {
        new Rate("-0.1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadStringNumber() {
        new Rate("1.1.1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadFormatsLessThanZero() {
        new Rate("-1%");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadFormatsBadRate() {
        new Rate("10plop");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadFormatsLessThanZeroMill() {
        new Rate("-1permil");
    }

    @Test
    public void testRateCalculationsWithProperFraction() {
        Rate rate;

        rate = new Rate("10%");
        assertThat(rate.getRate(), is("10%"));
        assertThat(rate.applyTo("1000", 0, BigDecimal.ROUND_HALF_UP), is("100"));
        assertThat(rate.applyTo(1000, 0, BigDecimal.ROUND_HALF_UP), is(100.0));
        assertThat(rate.applyTo(new BigDecimal(1000), 0, BigDecimal.ROUND_HALF_UP), is(new BigDecimal("100")));

        rate = new Rate("10Percent");
        assertThat(rate.getRate(), is("10Percent"));

        assertThat(rate.applyTo("1000", 0, BigDecimal.ROUND_HALF_UP), is("100"));
        assertThat(rate.applyTo(1000, 0, BigDecimal.ROUND_HALF_UP), is(100.0));
        assertThat(rate.applyTo(new BigDecimal(1000), 0, BigDecimal.ROUND_HALF_UP), is(new BigDecimal("100")));

        rate = new Rate("10Permil");
        assertThat(rate.getRate(), is("10Permil"));
        assertThat(rate.applyTo("1000", 0, BigDecimal.ROUND_HALF_UP), is("10"));
        assertThat(rate.applyTo(1000, 0, BigDecimal.ROUND_HALF_UP), is(10.0));
        assertThat(rate.applyTo(new BigDecimal(1000), 0, BigDecimal.ROUND_HALF_UP), is(new BigDecimal("10")));

        // try a fraction
        rate = new Rate("3/20");
        assertThat(rate.getRate(), is("3/20"));
        assertThat(rate.applyTo("1000", 0, BigDecimal.ROUND_HALF_UP), is("150"));
        assertThat(rate.applyTo(1000, 0, BigDecimal.ROUND_HALF_UP), is(150.0));
        assertThat(rate.applyTo(new BigDecimal(1000), 0, BigDecimal.ROUND_HALF_UP), is(new BigDecimal("150")));

        rate = new Rate("10.5%");
        assertThat(rate.getRate(), is("10.5%"));
        assertThat(rate.applyTo("10", 1, BigDecimal.ROUND_HALF_UP), is("1.1"));

        rate = new Rate("0.5");
        assertThat(rate.getRate(), is("0.5"));
        assertThat(rate.applyTo("50", 0, BigDecimal.ROUND_HALF_UP), is("25"));
    }

    @Test
    public void testRateCalculationsWithImroperFraction() {
        Rate rate;

        rate = new Rate("110%");
        assertThat(rate.getRate(), is("110%"));
        assertThat(rate.applyTo("1000", 0, BigDecimal.ROUND_HALF_UP), is("1100"));
        assertThat(rate.applyTo(1000, 0, BigDecimal.ROUND_HALF_UP), is(1100.0));
        assertThat(rate.applyTo(new BigDecimal(1000), 0, BigDecimal.ROUND_HALF_UP), is(new BigDecimal("1100")));

        rate = new Rate("110Percent");
        assertThat(rate.getRate(), is("110Percent"));
        assertThat(rate.applyTo("1000", 0, BigDecimal.ROUND_HALF_UP), is("1100"));
        assertThat(rate.applyTo(1000, 0, BigDecimal.ROUND_HALF_UP), is(1100.0));
        assertThat(rate.applyTo(new BigDecimal(1000), 0, BigDecimal.ROUND_HALF_UP), is(new BigDecimal("1100")));

        rate = new Rate("1100Permil");
        assertThat(rate.getRate(), is("1100Permil"));
        assertThat(rate.applyTo("1000", 0, BigDecimal.ROUND_HALF_UP), is("1100"));
        assertThat(rate.applyTo(1000, 0, BigDecimal.ROUND_HALF_UP), is(1100.0));
        assertThat(rate.applyTo(new BigDecimal(1000), 0, BigDecimal.ROUND_HALF_UP), is(new BigDecimal("1100")));

        // try a fraction
        rate = new Rate("20/10");
        assertThat(rate.getRate(), is("20/10"));
        assertThat(rate.applyTo("1000", 0, BigDecimal.ROUND_HALF_UP), is("2000"));
        assertThat(rate.applyTo(1000, 0, BigDecimal.ROUND_HALF_UP), is(2000.0));
        assertThat(rate.applyTo(new BigDecimal(1000), 0, BigDecimal.ROUND_HALF_UP), is(new BigDecimal("2000")));

        rate = new Rate("100.5%");
        assertThat(rate.getRate(), is("100.5%"));
        assertThat(rate.applyTo("10", 1, BigDecimal.ROUND_HALF_UP), is("10.1"));

        rate = new Rate("2.5");
        assertThat(rate.getRate(), is("2.5"));
        assertThat(rate.applyTo("50", 0, BigDecimal.ROUND_HALF_UP), is("125"));
    }
}
