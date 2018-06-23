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

import static com.ail.core.Functions.hideNull;
import static com.ail.party.Title.OTHER;
import static com.ail.party.Title.UNDEFINED;
import static javax.persistence.EnumType.STRING;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Enumerated;

import org.hibernate.envers.Audited;

import com.ail.annotation.TypeDefinition;
import com.ail.util.DateOfBirth;

@TypeDefinition
@Audited
@Entity
public class Person extends Party {
    static final long serialVersionUID = 2593580726988142332L;

    /** Person's date of birth */
    @Embedded
    @AttributeOverride(name = "date", column = @Column(name = "parDateOfBirth"))
    private DateOfBirth dateOfBirth;

    /** The person's first name */
    @Column(name="parFirstName")
    private String firstName;

    /** The person's surname */
    @Column(name="parSurname")
    private String surname;

    /** Text of title if not defined by Title property (i.e. title property==Other) */
    @Column(name="parOtherTitle")
    private String otherTitle;

    /** The person's title */
    @Enumerated(STRING)
    @Column(name="parTitle")
    private Title title = Title.UNDEFINED;

    /**
     * Getter returning the value of the dateOfBirth property. Person's date of birth
     * @return Value of the dateOfBirth property
     */
    public DateOfBirth getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * Setter to update the value of the dateOfBirth property. Person's date of birth
     * @param dateOfBirth New value for the dateOfBirth property
     */
    public void setDateOfBirth(DateOfBirth dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    /**
     * Getter returning the value of the firstName property. The person's first name
     * @return Value of the firstName property
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Setter to update the value of the firstName property. The person's first name
     * @param firstName New value for the firstName property
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
        updateLegalName();
    }

    /**
     * Getter returning the value of the surname property. The person's surname
     * @return Value of the surname property
     */
    public String getSurname() {
        return surname;
    }

    /**
     * Setter to update the value of the surname property. The person's surname
     * @param surname New value for the surname property
     */
    public void setSurname(String surname) {
        this.surname = surname;
        updateLegalName();
    }

    /**
     * Getter returning the value of the otherTitle property. Text of title if not defined by Title property
     * (i.e. title property==Other)
     * @return Value of the otherTitle property
     */
    public String getOtherTitle() {
        return otherTitle;
    }

    /**
     * Setter to update the value of the otherTitle property. Text of title if not defined by Title property
     * (i.e. title property==Other)
     * @param otherTitle New value for the otherTitle property
     */
    public void setOtherTitle(String otherTitle) {
        this.otherTitle = otherTitle;
        updateLegalName();
    }

    /**
     * Getter returning the value of the title property. The person's title
     * @return Value of the title property
     */
    public Title getTitle() {
        return title;
    }

    /**
     * Setter to update the value of the title property. The person's title
     * @param title New value for the title property
     */
    public void setTitle(Title title) {
        this.title = title;
        updateLegalName();
    }

     /**
      * Set the title property as a String. The String must represents a valid
      * com.ail.party.Title. i.e. it must be suitable for a call to
      * com.ail.party.Title.forName().
      * @param title New value for property.
      * @throws IndexOutOfBoundsException If title is not a valid com.ail.party.Title.
      */
     public void setTitleAsString(String title) throws IndexOutOfBoundsException{
        setTitle(Title.valueOf(title));
    }

    /**
     * Get the value of the title property as a string (as opposed to an instance of
     * com.ail.party.Title).
     * @return String representation of the title, or null if the property has not been set.
     */
    public String getTitleAsString() {
        if (title!=null) {
            return title.name();
        }
        return null;
    }

    /**
     * "actual title" is either the value of the <i>title</i> property, or the value of <i>otherTitle</i>
     * if <i>title</i>'s value is 'OTHER'.
     * @return actual title.
     */
    public String getActualTitle() {
        if (OTHER.equals(title)) {
            return otherTitle;
        }
        if (UNDEFINED.equals(title)) {
            return "";
        }
        return title!=null ? title.getLongName() : "";
    }

    @Override
    public String getShortName() {
        return getFirstName() != null ? getFirstName() : getActualTitle()+" "+getSurname();
    }

    private void updateLegalName() {
        setLegalName(pad(getActualTitle()) + pad(firstName) + hideNull(surname));
    }

    private String pad(String raw) {
        return raw==null || raw.length()==0 ? "" : raw + " ";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dateOfBirth == null) ? 0 : dateOfBirth.hashCode());
        result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
        result = prime * result + ((otherTitle == null) ? 0 : otherTitle.hashCode());
        result = prime * result + ((surname == null) ? 0 : surname.hashCode());
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
        Person other = (Person) obj;
        if (dateOfBirth == null) {
            if (other.dateOfBirth != null)
                return false;
        } else if (!dateOfBirth.equals(other.dateOfBirth))
            return false;
        if (firstName == null) {
            if (other.firstName != null)
                return false;
        } else if (!firstName.equals(other.firstName))
            return false;
        if (otherTitle == null) {
            if (other.otherTitle != null)
                return false;
        } else if (!otherTitle.equals(other.otherTitle))
            return false;
        if (surname == null) {
            if (other.surname != null)
                return false;
        } else if (!surname.equals(other.surname))
            return false;
        if (title != other.title)
            return false;
        return true;
    }
}
