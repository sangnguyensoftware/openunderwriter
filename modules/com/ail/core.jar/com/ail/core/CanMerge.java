/* Copyright Applied Industrial Logic Limited 2018. All rights Reserved */
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
package com.ail.core;

/**
 * Implementing classes (Types) handle their own merge processing - i.e. they
 * implement logic to merge data from a <code>donor</code> object into their own
 * state. By default, Types rely on then generic
 * {@link Type#mergeWithDataFrom(Type, Core)} implementation. It is only
 * necessary for Types to implement CanMerge when they need to handle the merge
 * in a class-specific way.
 */
public interface CanMerge {
    void mergeWithDataFrom(Type donor, Core core);
}
