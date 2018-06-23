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
import static org.apache.commons.codec.binary.Base64.encodeBase64String;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.CallbackException;
import org.hibernate.Session;
import org.hibernate.classic.Lifecycle;
import org.hibernate.envers.Audited;

import com.ail.annotation.TypeDefinition;
import com.ail.core.CoreContext;
import com.ail.core.ForeignSystemReference;
import com.ail.core.HasForeignSystemReferences;
import com.ail.core.Type;
import com.ail.core.product.HasProduct;

@TypeDefinition
@Audited
@Entity
public class DocumentContent extends Type implements Lifecycle, HasProduct, HasForeignSystemReferences {

    static final long serialVersionUID = 3175904078919370552L;

    @OneToMany(cascade = ALL)
    @MapKey(name = "type")
    Map<String,ForeignSystemReference> foreignSystemReference = new HashMap<>();

    @Lob
    @JsonIgnore
    private byte[] inTableContent;

    transient private byte[] content;

    private String productTypeId;

    public DocumentContent() {
    }

    public DocumentContent(String productTypeId, byte[] content) {
        this.productTypeId = productTypeId;
        this.content = content;
    }

    public DocumentContent(byte[] content) {
        this(CoreContext.getProductName(), content);
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public byte[] getInTableContent() {
        return inTableContent;
    }

    public void setInTableContent(byte[] inTableContent) {
        this.inTableContent = inTableContent;
    }

    @Override
    public String getProductTypeId() {
        return this.productTypeId;
    }

    @Override
    public void setProductTypeId(String productTypeId) {
        this.productTypeId = productTypeId;
    }

    @Override
    public Map<String,ForeignSystemReference> getForeignSystemReference() {
        if (foreignSystemReference == null) {
            foreignSystemReference = new HashMap<>();
        }

        return foreignSystemReference;
    }

    @Override
    public void setForeignSystemReference(Map<String,ForeignSystemReference> foreignSystemReference) {
        this.foreignSystemReference = foreignSystemReference;
    }

    @Override
    public boolean onSave(Session s) throws CallbackException {
    	return onLifeCycle("DocumentContentOnSaveCommand");
    }

    @Override
    public boolean onUpdate(Session s) throws CallbackException {
    	return onLifeCycle("DocumentContentOnUpdateCommand");
    }

    @Override
    public boolean onDelete(Session s) throws CallbackException {
    	return onLifeCycle("DocumentContentOnDeleteCommand");
    }

    @Override
    public void onLoad(Session s, Serializable id) {
    	onLifeCycle("DocumentContentOnLoadCommand");
    }

    public String calculateMD5Hash() throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] md5 = md.digest(getContent());
        return encodeBase64String(md5);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + typeHashCode();
        result = prime * result + (Long.valueOf(getSystemId()).hashCode());
        result = prime * result + Arrays.hashCode(content);
        return result;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        DocumentContent clone = (DocumentContent) super.clone();
        if (this.getContent() != null) {
            clone.setContent(this.getContent().clone());
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
        DocumentContent other = (DocumentContent) obj;
        if (getSystemId() != other.getSystemId())
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Document [getSystemId()=" + getSystemId() + "]";
    }
}
