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
package com.ail.core.audit;

import com.ail.core.Type;

public class Revision {

    private RevisionDetails revision;
    private RevisionType type;
    private Type entity;

    public Revision(RevisionDetails revision, Type entity, RevisionType type) {
        this.revision = revision;
        this.entity = entity;
        this.type = type;
    }

    public Type getEntity() {
        return entity;
    }

    public RevisionDetails getDetails() {
        return revision;
    }

    public RevisionType getType() {
        return type;
    }
}
