/* Copyright Applied Industrial Logic Limited 2018. All rights reserved. */
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
 */package com.ail.core;

/**
 * Interface describing model objects that have a human readable reference
 * number. This may, for example, be a policy number, a quote number, a claim
 * number or a membership number. Implementors are not expected to guarantee
 * that the same reference number will always be returned; they may change the
 * number as they move through their lifecycle.
 */
public interface HasReferenceNumber {
    String getReferenceNumber();
}
