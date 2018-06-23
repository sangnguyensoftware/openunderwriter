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

package com.ail.insurance.policy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import com.ail.annotation.TypeDefinition;
import com.ail.core.Identified;
import com.ail.core.Type;
import com.ail.financial.CurrencyAmount;

/**
 * An Excess represents the amount for which the policy holder is liable before a claim can be made. An excess
 * may be either a "deductible excess" or a standard excess. See the {@link #isDeductible() isDeductible()} method
 * for details.
 */
@TypeDefinition
public class Excess extends Type implements Identified {
    static final long serialVersionUID = -7196587543319758798L;

    private String id;
    private List<String> assetId = new ArrayList<>();
    private CurrencyAmount amount;
    private String title;
    private String excessTypeId;
    private boolean deductible = false;
    private boolean enabled = false;

    /**
     * Return the ID of this excess. This ID is unique within the policy.
     * @return This excess' ID
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * @see #getId()
     * @param id
     */
    @Override
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Fetch the collection of assetId objects associated with this instance.
     * @return A collection of instances of String.
     */
    public Collection<String> getAssetId() {
        return assetId;
    }

    /**
     * Set the collection of instances of String associated with this object.
     * @param assetId A collection of instances of String
     */
    public void setAssetId(Collection<String> assetId) {
        this.assetId = new Vector<>(assetId);
    }

    /**
     * Fetch a count of the number of assetId objects associated with this instance.
     * @return Count of assetId objects.
     */
    public int getAssetIdCount() {
        return assetId.size();
    }

    /**
     * Get the instance of assetId at a specific index from the collection of String objects associated with this instance.
     * @param i Index
     * @return Instance of String found at index i.
     */
    public String getAssetId(int i) {
        return assetId.get(i);
    }

    /**
     * Remove a specific assetId from the collection of String objects associated with this object.
     * @param i Index of assetId to remove.
     */
    public void removeAssetId(int i) {
        assetId.remove(i);
    }

    /**
     * Add a assetId to the collection associated with this object.
     * @param assetId Instance of String to add.
     */
    public void addAssetId(String assetId) {
        this.assetId.add(assetId);
    }

    /**
     * Remove a specific instance of String from the collection associated with this object.
     * @param assetId Object to be removed from the collection.
     */
    public void removeAssetId(String assetId) {
        this.assetId.remove(assetId);
    }

    public CurrencyAmount getAmount() {
        return amount;
    }

    public void setAmount(CurrencyAmount amount) {
        this.amount = amount;
    }

    public String getExcessTypeId() {
        return excessTypeId;
    }

    public void setExcessTypeId(String excessTypeId) {
        this.excessTypeId = excessTypeId;
    }

    /**
     * Returns true if this excess should be treated as a deductible, or false if it is a normal excess.
     * The meaning of this flag has significance during claims processing. Typically, an excess
     * which is marked as deductible will be processed as follows:<code><pre>
     * if ( loss &lt; deductible ) then payment = nill
     * otherwise if [( loss - deductible )  &lt; limit] then payment = ( loss - deductible )
     * otherwise payment = ( limit - deductible )</pre></code>
     * Whereas a normal excess is processed like this:<code><pre>
     * if ( loss < excess ) then payment = nill
     * otherwise if [( loss - excess )  < limit] then payment = ( loss - excess )
     * otherwise payment = limit</pre></code>
     * @return Returns the deductible indicator.
     */
    public boolean isDeductible()
    {
      return deductible;
    }

    /**
     * @see #isDeductible()
     * @param deductible The deductible to set.
     */
    public void setDeductible(boolean deductible)
    {
      this.deductible = deductible;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAssetId(Vector<String> assetId) {
        this.assetId = assetId;
    }

    @Override
    public boolean compareById(Identified that) {
        if (that.getId() != null && this.getId() != null && this.getClass().isAssignableFrom(that.getClass())) {
            return (this.id.equals(that.getId()));
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.typeHashCode();
        result = prime * result + ((amount == null) ? 0 : amount.hashCode());
        result = prime * result + ((assetId == null) ? 0 : assetId.hashCode());
        result = prime * result + (deductible ? 1231 : 1237);
        result = prime * result + (enabled ? 1231 : 1237);
        result = prime * result + ((excessTypeId == null) ? 0 : excessTypeId.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
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
        if (!typeEquals(obj))
            return false;
        Excess other = (Excess) obj;
        if (amount == null) {
            if (other.amount != null)
                return false;
        } else if (!amount.equals(other.amount))
            return false;
        if (assetId == null) {
            if (other.assetId != null)
                return false;
        } else if (!assetId.equals(other.assetId))
            return false;
        if (deductible != other.deductible)
            return false;
        if (enabled != other.enabled)
            return false;
        if (excessTypeId == null) {
            if (other.excessTypeId != null)
                return false;
        } else if (!excessTypeId.equals(other.excessTypeId))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (title == null) {
            if (other.title != null)
                return false;
        } else if (!title.equals(other.title))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Excess [id=" + id + ", assetId=" + assetId + ", amount=" + amount + ", title=" + title + ", excessTypeId=" + excessTypeId + ", deductible=" + deductible + ", enabled=" + enabled + "]";
    }
}
