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

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.ail.financial.PaymentRecordType.NEW;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

public class PaymentRecordTest {

    private PaymentRecord sut;
    
    private String transactionReference="TRANSACTION_ID";
    @Mock
    private CurrencyAmount currencyAmount;
    @Mock
    private PaymentMethod paymentMethod;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        
        doReturn("METHOD_ID").when(paymentMethod).getId();
        doReturn(Currency.GBP).when(currencyAmount).getCurrency();
        doReturn(new BigDecimal(20)).when(currencyAmount).getAmount();
    }

    @Test
    public void testConstructor() {
        sut = new PaymentRecord(currencyAmount, transactionReference, paymentMethod, NEW);
        assertEquals(NEW, sut.getType());
        assertEquals("METHOD_ID", sut.getMethodIdentifier());
        assertEquals(transactionReference, sut.getTransactionReference());
    }
}
