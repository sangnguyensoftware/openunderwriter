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

import java.util.ArrayList;
import java.util.List;

import com.ail.core.Identified;
import com.ail.core.Type;

/**
 * Define valid labels and the objects to which they can be applicable.
 */
public class Labels extends Type implements Constraint, Identified {
    private List<Label> label = new ArrayList<>();
    private List<Labels> labels = new ArrayList<>();
    private String discriminator;
    private String target;
    private String condition;

    public Labels(String target, String discriminator, String condition) {
        this.target = target;
        this.discriminator = discriminator;
        this.condition = condition;
    }

    public List<Label> getLabel() {
        if (label == null) {
            label = new ArrayList<>();
        }
        return label;
    }

    public void setLabel(List<Label> label) {
        this.label = label;
    }

    public List<Labels> getLabels() {
        if (labels == null) {
            labels = new ArrayList<>();
        }
        return labels;
    }

    public void setLabels(List<Labels> labels) {
        this.labels = labels;
    }

    @Override
    public String getDiscriminator() {
        return discriminator;
    }

    @Override
    public String getCondition() {
        return condition;
    }

    public String getTarget() {
        return target;
    }

    @Override
    public String getId() {
        return target + ":" + discriminator + ":" + condition;
    }

    @Override
    public void setId(String Id) {
        throw new IllegalStateException("setId() must not be called. Labels.id is a calculated value.");
    }

    public boolean validFor(String subject, String discriminator) {
        return subject.matches(this.target) && (getDiscriminator() == null || discriminator == null || (getDiscriminator() != null && getDiscriminator().matches(discriminator)));
    }

    @Override
    public boolean compareById(Identified that) {
        if ((that instanceof Labels)) {
            Labels thatLabels = (Labels) that;

            return (this.getId() != null && this.getId().equals(thatLabels.getId()));
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.typeHashCode();
        result = prime * result + ((condition == null) ? 0 : condition.hashCode());
        result = prime * result + ((discriminator == null) ? 0 : discriminator.hashCode());
        result = prime * result + ((label == null) ? 0 : label.hashCode());
        result = prime * result + ((labels == null) ? 0 : labels.hashCode());
        result = prime * result + ((target == null) ? 0 : target.hashCode());
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
        Labels other = (Labels) obj;
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
        if (label == null) {
            if (other.label != null)
                return false;
        } else if (!label.equals(other.label))
            return false;
        if (labels == null) {
            if (other.labels != null)
                return false;
        } else if (!labels.equals(other.labels))
            return false;
        if (target == null) {
            if (other.target != null)
                return false;
        } else if (!target.equals(other.target))
            return false;
        return super.typeEquals(obj);
    }

    @Override
    public String toString() {
        return "Labels [label=" + label + ", labels=" + labels + ", discriminator=" + discriminator + ", target=" + target + ", condition=" + condition + "]";
    }
}
