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

import javax.persistence.Entity;

import org.hibernate.envers.Audited;

import com.ail.annotation.TypeDefinition;

/**
 * Represents the details of a payment method for PayPal. We don't collect or store any payment information
 * related to PayPal so this class simply has to generate an ID to identify itself..
 */
@TypeDefinition
@Audited
@Entity
public class PayPal extends PaymentMethod {

    @Override
    public String getId() {
        return "PayPal";
    }

}
