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

package com.ail.core.document;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Lob;

import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;
import org.hibernate.envers.Audited;

import com.ail.annotation.TypeDefinition;
import com.ail.core.HasLabels;
import com.ail.core.Type;

@TypeDefinition
@Audited
@Entity
@NamedQueries({
    @NamedQuery(name = "get.documentplaceholder.by.systemId", query = "select doc from DocumentPlaceholder doc where doc.systemId = ?"),
    @NamedQuery(name = "get.documentplaceholder.by.externalSystemId", query = "select doc from DocumentPlaceholder doc where doc.externalSystemId = ?")
})
public class DocumentPlaceholder extends Type  implements HasLabels {

    private String type;

    private String otherType;

    @Lob
    private String description;

    private String title;

    @ElementCollection
    private Set<String> label;

    public DocumentPlaceholder() {
    }

    public DocumentPlaceholder(DocumentType type, String title) {
        this(type.getLongName(), title);
    }

    public DocumentPlaceholder(String type, String otherType, String title) {
        this(type, title);
        this.otherType = otherType;
    }

    public DocumentPlaceholder(String type, String title) {
        this.type = type;
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public DocumentType getTypeAsEnum() {
        return DocumentType.forName(type);
    }

    public void setTypeAsEnum(DocumentType type) {
        this.type = type.longName();
    }

    public boolean isOfBaseType() {
        return DocumentType.isValid(type);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOtherType() {
        return otherType;
    }

    public void setOtherType(String otherType) {
        this.otherType = otherType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public Set<String> getLabel() {
        if (label == null) {
            label = new HashSet<>();
        }

        return label;
    }

    @Override
    public void setLabel(Set<String> label) {
        this.label = label;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + typeHashCode();
        result = prime * result + (Long.valueOf(getSystemId()).hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((label == null) ? 0 : label.hashCode());
        result = prime * result + ((otherType == null) ? 0 : otherType.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        DocumentPlaceholder clone = (DocumentPlaceholder) super.clone();
        return clone;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        if (!typeEquals(obj))
            return false;
        DocumentPlaceholder other = (DocumentPlaceholder) obj;
        if (getSystemId() != other.getSystemId())
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (otherType == null) {
            if (other.otherType != null)
                return false;
        } else if (!otherType.equals(other.otherType))
            return false;
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.equals(other.title))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        if (label == null) {
            if (other.label != null)
                return false;
        } else if (!label.equals(other.label))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Document [description=" + description + ", otherType=" + otherType + ", title=" + title + ", type=" + type + ", getSystemId()="
                + getSystemId() + "]";
    }
}
