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

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

public class PaymentScheduleTest {

    private PaymentSchedule sut;


    @Before
    public void setup() {
        sut = new PaymentSchedule();
    }

    @Test
    public void testFirstProvisionValueIsRetrieved() {

        List<MoneyProvision> provisions = Lists.newArrayList();

        MoneyProvision moneyProvision1 = new MoneyProvision();
        moneyProvision1.setAmount(new CurrencyAmount(300, Currency.USD));
        moneyProvision1.setPaymentsStartDate(new Date(1220227300L));

        MoneyProvision moneyProvision2 = new MoneyProvision();
        moneyProvision2.setAmount(new CurrencyAmount(200, Currency.USD));
        moneyProvision2.setPaymentsStartDate(new Date(1220227200L));

        MoneyProvision moneyProvision3 = new MoneyProvision();
        moneyProvision3.setAmount(new CurrencyAmount(400, Currency.USD));
        moneyProvision3.setPaymentsStartDate(new Date(1220227400L));

        MoneyProvision moneyProvision4 = new MoneyProvision();
        moneyProvision4.setAmount(new CurrencyAmount(100, Currency.USD));
        moneyProvision4.setPaymentsStartDate(new Date(1220227100L));

        MoneyProvision moneyProvision5 = new MoneyProvision();
        moneyProvision5.setAmount(new CurrencyAmount(500, Currency.USD));
        moneyProvision5.setPaymentsStartDate(new Date(1220227500L));

        provisions.add(moneyProvision1);
        provisions.add(moneyProvision2);
        provisions.add(moneyProvision3);

        sut.setMoneyProvision(provisions);

        MoneyProvision prov = sut.getFirstProvision();
        assertEquals(200, prov.getAmount().getAmount().intValue());

        provisions.add(moneyProvision4);

        prov = sut.getFirstProvision();
        assertEquals(100, prov.getAmount().getAmount().intValue());

        provisions.add(moneyProvision5);

        prov = sut.getFirstProvision();
        assertEquals(100, prov.getAmount().getAmount().intValue());

    }
}
