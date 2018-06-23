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

import static javax.persistence.CascadeType.ALL;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.hibernate.envers.Audited;

import com.ail.annotation.TypeDefinition;
import com.ail.core.Identified;
import com.ail.core.Type;
import com.ail.insurance.HasAssets;

/**
 * An insured thing, or a thing about which information is collected and upon
 * which risk (and other factors) are assessed. Generally an Asset represents a
 * thing which is insured, for example a building in a household policy, or a
 * driver in a motor policy.
 */
@TypeDefinition
@Audited
@Entity
public class Asset extends Type implements Identified, HasAssets {
    static final long serialVersionUID = 7326823306523810654L;

    @OneToMany(cascade = ALL)
    private List<Asset> asset = new ArrayList<>();

    @Column(name = "assId") // Looks odd? See OU-1064
    private String ref;

    private String assetTypeId;

    @Override
    public String getId() {
        return ref; // Looks odd? See OU-1064
    }

    public String getRef() {
        return getId();
    }

    @Override
    public void setId(String id) {
        this.ref = id; // Looks odd? See OU-1064
    }

    public void setRef(String ref) {
        setId(ref);
    }

   public String getAssetTypeId() {
        return assetTypeId;
    }

    public void setAssetTypeId(String assetTypeId) {
        this.assetTypeId = assetTypeId;
    }

    @Override
    public List<Asset> getAsset() {
        if (asset==null) {
            asset = new ArrayList<>();
        }
        return asset;
    }

    @Override
    public void setAsset(List<Asset> asset) {
        this.asset = asset;
    }

    /**
     * Add a asset to the collection associated with this object.
     *
     * @param asset
     *            Instance of Asset to add.
     */
    @Override
    public void addAsset(Asset asset) {
        getAsset().add(asset);
    }

    /**
     * Remove a specific instance of Asset from the collection associated with this
     * object.
     *
     * @param asset
     *            Object to be removed from the collection.
     */
    @Override
    public void removeAsset(Asset asset) {
        getAsset().remove(asset);
    }

    @Override
    public boolean compareById(Identified that) {
        if (that.getId() != null && this.getId() != null && this.getClass().isAssignableFrom(that.getClass())) {
            return (this.ref.equals(that.getId()));
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((assetTypeId == null) ? 0 : assetTypeId.hashCode());
        result = prime * result + ((ref == null) ? 0 : ref.hashCode());
        result = prime * result + ((getAttribute() == null) ? 0 : getAttribute().hashCode());
        result = prime * result + ((getAsset() == null) ? 0 : getAsset().hashCode());
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
        Asset other = (Asset) obj;
        if (assetTypeId == null) {
            if (other.assetTypeId != null)
                return false;
        } else if (!assetTypeId.equals(other.assetTypeId))
            return false;
        if (ref == null) {
            if (other.ref != null)
                return false;
        } else if (!ref.equals(other.ref))
            return false;
        if (getAttribute() == null) {
            if (other.getAttribute() != null)
                return false;
        } else if (!getAttribute().equals(other.getAttribute()))
            return false;
        if (getAsset() != null) {
            if (other.getAsset() != null)
                return false;
        } else if (!getAsset().equals(other.getAsset()))
            return false;
        return true;
    }
}
