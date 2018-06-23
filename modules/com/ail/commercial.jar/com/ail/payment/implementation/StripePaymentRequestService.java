/* Copyright Applied Industrial Logic Limited 2016. All rights Reserved */
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

import java.util.HashMap;
import java.util.Map;

import com.ail.core.BaseException;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.payment.PaymentRequestService;
import com.stripe.exception.CardException;
import com.stripe.model.Charge;

public class StripePaymentRequestService  extends Service<PaymentRequestService.PaymentRequestArgument> {

    @Override
    public void invoke() throws BaseException {

        if (args.getAmountArg() == null) {
            throw new PreconditionException("args.getAmountArg()==null");
        }

        if (args.getCustomerIdArg() == null) {
            throw new PreconditionException("args.getCustomerIdArg()==null");
        }

        Map<String, Object> chargeParams = getPaymentParams();

        try {

            Charge charge = Charge.create(chargeParams);

            args.setPaymentIdRet(charge.getBalanceTransaction());

        } catch(CardException e) {
            throw new PostconditionException("Customer Stripe payment problem - Card error:" + e.getMessage(), e);
        } catch (Exception e) {
            throw new PostconditionException("Customer Stripe payment problem", e);
        }

        if (args.getPaymentIdRet() == null) {
            throw new PostconditionException("args.getPaymentIdRet()==null");
        }
    }

    private Map<String, Object> getPaymentParams() {

        String paymentDescription = args.getDescriptionArg();

        if (paymentDescription == null || paymentDescription.length() == 0) {
            paymentDescription = args.getCustomerIdArg();
            if (args.getCustomerNameArg() != null) {
                paymentDescription += ", " + args.getCustomerNameArg();
            }
        }

        Map<String, Object> chargeParams = new HashMap<String, Object>();
        chargeParams.put("amount", args.getAmountArg().getAmount().intValue());
        chargeParams.put("currency", args.getAmountArg().getCurrencyAsString());
        chargeParams.put("customer", args.getCustomerIdArg());
        chargeParams.put("description", "Charge: " + paymentDescription);

        return chargeParams;
    }
}
