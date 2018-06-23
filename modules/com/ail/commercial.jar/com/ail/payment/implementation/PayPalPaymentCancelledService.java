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

package com.ail.payment.implementation;

import com.ail.annotation.ServiceImplementation;
import com.ail.core.Service;
import com.ail.payment.PaymentCancelledService;

/**
 * Implementation of the payment cancelled service for PayPal. In the case
 * of PayPal there is no further action to be taken. 
 *
 * The PayPal payment process starts with a PayPalPaymentRequestService. This
 * returns a URL to forward to in order to get user authorisation. That will
 * either succeed and lead to the PayPalPaymentApprovedService being called; or,
 * fail and lead to PayPalPaymentCancelledService being called. Following the
 * PayPalPaymentApprovedService call, the PayPalPaymentExecutionServer must be
 * called to execute the payment.
 * 
 * Once the process is complete, the transaction is said to be a "sale" in
 * PayPal terminology. It will appear in the user's and merchant's transaction
 * history and is available for refunds etc.
 */
@ServiceImplementation
public class PayPalPaymentCancelledService extends Service<PaymentCancelledService.PaymentCancelledArgument> {
    @Override
    public void invoke() {
    }
}
