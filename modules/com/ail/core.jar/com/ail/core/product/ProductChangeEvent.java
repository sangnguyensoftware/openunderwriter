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
package com.ail.core.product;

import static javax.persistence.EnumType.STRING;

import javax.persistence.Entity;
import javax.persistence.Enumerated;

import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

import com.ail.annotation.TypeDefinition;
import com.ail.core.Type;

@Entity
@TypeDefinition
@NamedQueries({
    @NamedQuery(name = "get.productchangeevents.before.createdDate", query = "select pce from ProductChangeEvent pce where pce.createdDate < ?"),
    @NamedQuery(name = "delete.productchangeevents.before.createdDate", query = "delete from ProductChangeEvent pce where pce.createdDate < ?")
})
public class ProductChangeEvent extends Type {
    @Enumerated(STRING)
    private ProductChangeEventType type;

    private String path;

    public ProductChangeEvent() {
    }

    public ProductChangeEvent(ProductChangeEventType type, String path) {
        this.type = type;
        this.path = path;
    }

    public ProductChangeEventType getType() {
        return type;
    }

    public void setType(ProductChangeEventType type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
