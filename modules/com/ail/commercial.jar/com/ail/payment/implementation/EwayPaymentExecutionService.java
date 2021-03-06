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

import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.NotImplementedError;
import com.ail.core.Service;
import com.ail.payment.PaymentExecutionService;

/**
 * Stub implementation of eWay service. Full implementation is covered by a
 * non-os license.
 */
@ServiceImplementation
public class EwayPaymentExecutionService extends Service<PaymentExecutionService.PaymentExecutionArgument> {

    @Override
    public void invoke() throws BaseException {
        throw new NotImplementedError("Add-on not present. This service is not implemented in the community edition.");
    }
}
