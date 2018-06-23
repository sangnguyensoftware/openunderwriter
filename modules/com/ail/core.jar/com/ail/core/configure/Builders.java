/* Copyright Applied Industrial Logic Limited 2002. All rights reserved. */
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

package com.ail.core.configure;


public class Builders extends Group {
    static final long serialVersionUID = 2460072872773609457L;
    /**
     * @link aggregation
     * @supplierCardinality 0..*
     */
    /*# Builder lnkType; */

	public Builders() {
        setName("_Builders");
    }

    public void addBuilder(Builder vBuilder) throws IndexOutOfBoundsException {
		addGroup(vBuilder);
    }

    public Builder getBuilder(int index) throws IndexOutOfBoundsException {
		return (Builder)getGroup(index);
    }

    public int getBuilderCount() {
		return getGroupCount();
    }

    public void removeAllBuilder() {
		removeAllGroup();
    }

    public Builder removeBuilder(int index) {
		return (Builder)(removeGroup(index));
    }

    public void setBuilderAt(int index, Builder vBuilder) throws IndexOutOfBoundsException {
		setGroup(index, vBuilder);
    }
}
