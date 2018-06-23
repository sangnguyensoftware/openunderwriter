/* Copyright Applied Industrial Logic Limited 2017. All rights Reserved */
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
package com.ail.core.label;

import com.ail.core.Identified;
import com.ail.core.Type;

public class Label extends Type implements Constraint, Identified {
    private String text;
    private String discriminator;
    private String condition;

    public Label(String text, String discriminator, String condition) {
        this.text = text;
        this.discriminator = discriminator;
        this.condition = condition;
    }

    public Label(String text) {
        this(text, null, null);
    }

    public String getText() {
        return text;
    }

    @Override
    public String getDiscriminator() {
        return discriminator;
    }

    @Override
    public String getCondition() {
        return condition;
    }

    @Override
    public String getId() {
        return text + ":" + discriminator + ":" + condition;
    }

    @Override
    public void setId(String Id) {
        throw new IllegalStateException("setId() must not be called. Label.id is a calculated value.");
    }

    @Override
    public boolean compareById(Identified that) {
        if ((that instanceof Label)) {
            Label thatLabel = (Label) that;

            return (this.getId() != null && this.getId().equals(thatLabel.getId()));
        } else {
            return false;
        }
    }

    public boolean validFor(String discriminator) {
        return (getDiscriminator() == null && discriminator == null) || (getDiscriminator() != null && discriminator != null && getDiscriminator().matches(discriminator));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.typeHashCode();
        result = prime * result + ((condition == null) ? 0 : condition.hashCode());
        result = prime * result + ((discriminator == null) ? 0 : discriminator.hashCode());
        result = prime * result + ((text == null) ? 0 : text.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Label other = (Label) obj;
        if (condition == null) {
            if (other.condition != null)
                return false;
        } else if (!condition.equals(other.condition))
            return false;
        if (discriminator == null) {
            if (other.discriminator != null)
                return false;
        } else if (!discriminator.equals(other.discriminator))
            return false;
        if (text == null) {
            if (other.text != null)
                return false;
        } else if (!text.equals(other.text))
            return false;
        return super.typeEquals(obj);
    }

    @Override
    public String toString() {
        return "Label [text=" + text + ", discriminator=" + discriminator + ", condition=" + condition + "]";
    }
}
