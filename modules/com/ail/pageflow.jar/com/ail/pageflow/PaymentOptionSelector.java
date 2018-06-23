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
package com.ail.pageflow;

import static com.ail.core.CoreContext.getRequestWrapper;
import static com.ail.pageflow.util.Functions.addError;

import java.io.IOException;

import com.ail.core.Type;
import com.ail.core.context.RequestWrapper;
import com.ail.financial.PaymentSchedule;
import com.ail.insurance.policy.Policy;
import com.ail.pageflow.util.Functions;

/**
 * <p>
 * This element displays the payment options available and prompts the user to
 * select one from the list
 * </p>
 * <p>
 * The options to be listed are defined by the
 * {@link com.ail.openquote.Policy#getPaymentOption() Policy.getPaymentOption()}
 * . Each option comprises a {@link com.ail.financial.PaymentSchedule
 * PaymentSchedule}; when an option is selected, the associate schedule is
 * copied to {@link com.ail.openquote.Policy#setPaymentDetails(PaymentSchedule)
 * Policy.setPaymentDetails()} and therefore defines how payments will be made
 * for this policy.
 * </p>
 * <p>
 * Only one option may be selected. Page validation will fail unless one options
 * is selected.
 * </p>
 * <p>
 * <img src="doc-files/PaymentOptionSelector.png"/>
 * </p>
 * In the example above, three payment options are defined; two a simple single
 * payment schedules; the third is a more complex schedule with a deposit and
 * multiple follow on payments.
 */
public class PaymentOptionSelector extends PageElement {
    private static final long serialVersionUID = -4810599045554021748L;

    @Override
    public Type applyRequestValues(Type model) {
        Policy quote = (Policy) model;
        RequestWrapper request = getRequestWrapper();

        // null any existing payment option. In state terms is it okay for a
        // quote to have a null paymentDetails. It just means that none has been
        // selected. We'll use it for validation too.
        quote.setPaymentDetails(null);

        // if an option was selected...
        if (request.getParameter("selectedOption") != null) {
            // the option's value will be the index of the payment option in the
            // model.
            int selectedOption = Integer.parseInt(request.getParameter("selectedOption"));

            quote.setPaymentDetails(quote.getPaymentOption().get(selectedOption));
        }

        return quote;
    }

    @Override
    public boolean processValidations(Type model) {
        Policy quote = (Policy) model;

        Functions.removeErrorMarkers(quote);

        if (quote.getPaymentDetails() == null) {
            addError("paymentDetails", i18n("i18n_payment_option_select_error"), model);
            return true;
        }

        return false;
    }

    @Override
    public Type renderResponse(Type model) throws IllegalStateException, IOException {
        return executeTemplateCommand("PaymentOptionSelector", model);
    }
}
