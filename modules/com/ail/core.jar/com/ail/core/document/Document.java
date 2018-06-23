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

package com.ail.core.document;

import static javax.persistence.CascadeType.ALL;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;

import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;
import org.hibernate.envers.AuditJoinTable;
import org.hibernate.envers.Audited;

import com.ail.annotation.TypeDefinition;
import com.ail.core.HasLabels;
import com.ail.core.Type;

@TypeDefinition
@Audited
@Entity
@NamedQueries({
    @NamedQuery(name = "get.document.by.systemId", query = "select doc from Document doc where doc.systemId = ?"),
    @NamedQuery(name = "get.document.by.externalSystemId", query = "select doc from Document doc where doc.externalSystemId = ?")
})
public class Document extends Type implements HasLabels {

    static final long serialVersionUID = 3175904078919370552L;

    private String type;

    private String otherType;

    private String fileName;

    @Lob
    private String description;

    private String title;

    private String mimeType;

    /**
     * This has a one-to-one relationship with DocumentContent, it is implemented
     * as one-to-many as a workaround to facilitate lazy loading.
     */
    @OneToMany(cascade = ALL, fetch=FetchType.LAZY)
    @JoinColumn(name = "dcoDocumentUIDdoc", referencedColumnName = "UID")
    @AuditJoinTable(name="jdocConDco_")
    private List<DocumentContent> content = new ArrayList<>();

    @ElementCollection
    private Set<String> label;

    public Document() {
    }

    public Document(DocumentType type, byte[] document, String title, String fileName, String mimeType, String productTypeId) {
        this(type.getLongName(), document, title, fileName, mimeType, productTypeId);
    }

    public Document(String type, String otherType, String title, String fileName, String mimeType, String productTypeId) {
        this(type, otherType, null, title, fileName, mimeType, productTypeId);
    }

    public Document(String type, String otherType, byte[] document, String title, String fileName, String mimeType, String productTypeId) {
        this(type, document, title, fileName, mimeType, productTypeId);
        this.otherType = otherType;
    }

    public Document(String type, byte[] document, String title, String fileName, String mimeType, String productTypeId) {
        this.type = type;
        setDocumentContent(new DocumentContent(productTypeId, document));
        this.title = title;
        this.fileName = fileName;
        this.mimeType = translateMimeType(mimeType);
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

    /**
     * @deprecated Use getDocumentContent instead
     * @return
     */
    @Deprecated
    public byte[] getDocument() {
        return getDocumentContent().getContent();
    }

    /**
     * @deprecated Use setDocumentContent instead
     * @return
     */
    @Deprecated
    public void setDocument(byte[] document) {
        setDocumentContent(new DocumentContent(document));
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public DocumentContent getDocumentContent() {
        if (this.content.size() != 1) {
            return null;
        }
        return this.content.get(0);
    }

    public void setDocumentContent(DocumentContent content) {
        this.content.clear();
        this.content.add(content);
    }

    public List<DocumentContent> getContent() {
        if (content == null) {
            content = new ArrayList<>();
        }
        return this.content;
    }

    public void setContent(List<DocumentContent> content) {
        this.content = content;
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

    public String calculateMD5Hash() throws NoSuchAlgorithmException {
        return getDocumentContent().calculateMD5Hash();
    }

    private String translateMimeType(String type) {
        if ("image/jpg".equals(type)) {
            return "image/jpeg";
        }
        return type;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + typeHashCode();
        result = prime * result + (Long.valueOf(getSystemId()).hashCode());
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
        result = prime * result + ((content == null) ? 0 : content.hashCode());
        result = prime * result + ((label == null) ? 0 : label.hashCode());
        result = prime * result + ((mimeType == null) ? 0 : mimeType.hashCode());
        result = prime * result + ((otherType == null) ? 0 : otherType.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Document clone = (Document) super.clone();
        if (this.getDocumentContent() != null) {
            clone.setDocumentContent((DocumentContent)this.getDocumentContent().clone());
        }
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
        Document other = (Document) obj;
        if (getSystemId() != other.getSystemId())
            return false;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        if (!content.equals(other.content))
            return false;
        if (fileName == null) {
            if (other.fileName != null)
                return false;
        } else if (!fileName.equals(other.fileName))
            return false;
        if (mimeType == null) {
            if (other.mimeType != null)
                return false;
        } else if (!mimeType.equals(other.mimeType))
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
        return "Document [description=" + description + ", fileName=" + fileName + ", mimeType=" + mimeType + ", otherType=" + otherType + ", title=" + title + ", type=" + type + ", getSystemId()="
                + getSystemId() + "]";
    }
}
