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

package com.ail.party;

import java.util.Date;

import javax.persistence.Entity;

import org.hibernate.envers.Audited;

import com.ail.annotation.TypeDefinition;
import com.ail.core.Functions;
import com.ail.core.Mergable;

/**
 * Class to encapsulate the details of a postal address.
 */
@TypeDefinition
@Audited
@Entity
public class Address extends ContactSystem implements Mergable  {
    static final long serialVersionUID = 6426298969158775043L;
    public static final String MAIN_POSTAL_ADDRESS = "i18n_contact_system_main_postal_address";

    private String line1;
    private String line2;
    private String line3;
    private String line4;
    private String line5;
    private String town;
    private String county;
    private String country;
    private String postcode;

    public Address() {
        super();
    }

    public Address(Date startDate, Date endDate, String type,String line1, String line2, String line3, String line4, String line5, String town, String county, String country, String postcode) {
        super();
        this.line1 = line1;
        this.line2 = line2;
        this.line3 = line3;
        this.line4 = line4;
        this.line5 = line5;
        this.town = town;
        this.county = county;
        this.country = country;
        this.postcode = postcode;
        updateFullAddress();
    }

    public Address(String line1, String line2, String line3, String line4, String line5, String town, String county, String country, String postcode) {
      this(null, null, MAIN_POSTAL_ADDRESS, line1, line2, line3, line4, line5, town, county, country, postcode);
    }

    public Address(String type, String line1, String line2, String line3, String line4, String line5, String town, String county, String country, String postcode) {
        this(null, null, type, line1, line2, line3, line4, line5, town, county, country, postcode);
    }

    public String getLine1() {
        return line1;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
        updateFullAddress();
    }

    public String getLine2() {
        return line2;
    }

    public void setLine2(String line2) {
        this.line2 = line2;
        updateFullAddress();
    }

    public String getLine3() {
        return line3;
    }

    public void setLine3(String line3) {
        this.line3 = line3;
        updateFullAddress();
    }

    public String getLine4() {
        return line4;
    }

    public void setLine4(String line4) {
        this.line4 = line4;
        updateFullAddress();
    }

    public String getLine5() {
        return line5;
    }

    public void setLine5(String line5) {
        this.line5 = line5;
        updateFullAddress();
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
        updateFullAddress();
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
        updateFullAddress();
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
        updateFullAddress();
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
        updateFullAddress();
    }

    private void updateFullAddress() {
        setFullAddress(toString());
    }

    private String nn(String s) {
        return (s == null) ? "" : s.trim();
    }

    /**
     * Return the address formatted on a single line with comma separated fields.
     * @return formatted address
     */
    @Override
    public String toString() {
        String ret = nn(line1) + ',' + nn(line2) + ',' + nn(line3) + ',' + nn(line4) + ',' + nn(line5) + ',' + nn(town) + ',' + nn(county) + ',' + nn(country);

        ret = ret.replaceAll(",,+", ",").replaceFirst("^,", "").replaceFirst(",$", "").replace(",", ", ") + ".";

        if (!Functions.isEmpty(postcode)) {
            ret = ret + " " + postcode;
        }

        return ret;
    }

    @Override
    public boolean isSameContactAs(ContactSystem other) {
        if (other == null || !(other instanceof Address)) {
            return false;
        }

        Address that = (Address) other;

        if (country == null) {
            if (that.country != null)
                return false;
        } else if (!country.equals(that.country))
            return false;
        if (county == null) {
            if (that.county != null)
                return false;
        } else if (!county.equals(that.county))
            return false;
        if (line1 == null) {
            if (that.line1 != null)
                return false;
        } else if (!line1.equals(that.line1))
            return false;
        if (line2 == null) {
            if (that.line2 != null)
                return false;
        } else if (!line2.equals(that.line2))
            return false;
        if (line3 == null) {
            if (that.line3 != null)
                return false;
        } else if (!line3.equals(that.line3))
            return false;
        if (line4 == null) {
            if (that.line4 != null)
                return false;
        } else if (!line4.equals(that.line4))
            return false;
        if (line5 == null) {
            if (that.line5 != null)
                return false;
        } else if (!line5.equals(that.line5))
            return false;
        if (postcode == null) {
            if (that.postcode != null)
                return false;
        } else if (!postcode.equals(that.postcode))
            return false;
        if (town == null) {
            if (that.town != null)
                return false;
        } else if (!town.equals(that.town))
            return false;

        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((country == null) ? 0 : country.hashCode());
        result = prime * result + ((county == null) ? 0 : county.hashCode());
        result = prime * result + ((line1 == null) ? 0 : line1.hashCode());
        result = prime * result + ((line2 == null) ? 0 : line2.hashCode());
        result = prime * result + ((line3 == null) ? 0 : line3.hashCode());
        result = prime * result + ((line4 == null) ? 0 : line4.hashCode());
        result = prime * result + ((line5 == null) ? 0 : line5.hashCode());
        result = prime * result + ((postcode == null) ? 0 : postcode.hashCode());
        result = prime * result + ((town == null) ? 0 : town.hashCode());
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
        if (!isSameContactAs((Address)obj)) {
            return false;
        }

        return super.equals(obj);
    }

    @Override
    public void mergeFrom(Mergable donor) {
        if (donor instanceof Address) {
            Address donorAddress = (Address)donor;

            if (this.line1 == null || "".equals(this.line1) && donorAddress.line1 != null) {
                this.line1 = donorAddress.line1;
            }

            if (this.line2 == null || "".equals(this.line2) && donorAddress.line2 != null) {
                this.line2 = donorAddress.line2;
            }

            if (this.line3 == null || "".equals(this.line3) && donorAddress.line3 != null) {
                this.line3 = donorAddress.line3;
            }

            if (this.line4 == null || "".equals(this.line4) && donorAddress.line4 != null) {
                this.line4 = donorAddress.line4;
            }

            if (this.line5 == null || "".equals(this.line5) && donorAddress.line5 != null) {
                this.line5 = donorAddress.line5;
            }

            if (this.town == null || "".equals(this.town) && donorAddress.town != null) {
                this.town = donorAddress.town;
            }

            if (this.county == null || "".equals(this.county) && donorAddress.county != null) {
                this.county = donorAddress.county;
            }

            if (this.country == null || "".equals(this.country) && donorAddress.country != null) {
                this.country = donorAddress.country;
            }

            if (this.postcode == null || "".equals(this.postcode) && donorAddress.postcode != null) {
                this.postcode = donorAddress.postcode;
            }

            updateFullAddress();
        }
    }
}
