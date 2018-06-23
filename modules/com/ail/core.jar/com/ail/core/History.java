/* Copyright Applied Industrial Logic Limited 2002. All rights reserved. */
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

package com.ail.core;

import java.util.Enumeration;
import java.util.Vector;
import java.util.Arrays;
import java.util.Collection;

import com.ail.annotation.TypeDefinition;

/**
 * The history object is a collection of Version objects which document the
 * change history of an entity. A history object represents a snapshot of
 * history of an object, rather than the history itself. Changes made to the
 * History object's Version records do not effect the actual history.
 */
@TypeDefinition
public class History extends Type {
	private Vector<Version> version=null;

    /**
     * @supplierCardinality 0..*
     * @link aggregationByValue 
     */
    /*# Version lnkVersion; */

	private Vector<Version> getVersionVector() {
        if (version==null) {
            version=new Vector<Version>();
        }

        return version;
    }

    private void setVersionVector(Collection<Version> c) {
        version=new Vector<Version>(c);
    }

    /**
     * Add a Version to this group.
     * @param vVersion Version to add.
     * @throws IndexOutOfBoundsException Group is "full"
     **/
    public void addVersion(Version vVersion) throws IndexOutOfBoundsException {
		getVersionVector().add(vVersion);
    }

    /**
     * Return a list of the versions as an enumeration.
     * @return Enumeration of properties
     **/
    public Enumeration<Version> enumerateVersion() {
        return getVersionVector().elements();
    }

    /**
     * Fetch a specific Version by it's index in history.
     * @param index Index of Version to fetch.
     * @throws IndexOutOfBoundsException index is out of range
     * @return The Version at 'index'.
     **/
    public Version getVersion(int index) throws IndexOutOfBoundsException {
        return (Version)getVersionVector().elementAt(index);
    }

    /**
     * Return the list of the versions as a Collection.
     * @return Collection of Version elements.
     **/
    public Collection<Version> getVersion() {
		return getVersionVector();
    }

    /**
     * Set the version collection.
     * @param version
     */
    public void setVersion(Collection<Version> version) {
        setVersionVector(version);
    }

    /**
     * Fetch a count of the number of versions. 
     * @return Count of properties in the group.
     **/
    public int getVersionCount() {
        return getVersionVector().size();
	}

    /**
     * Remove (delete) all the versions. Persisted history is not effected.
     **/
    public void removeAllVersion() {
		getVersionVector().removeAllElements();
    }

    /**
     * Remove (delete) a specific Version from the list. Persisted history is
     * not effected.
     * @param index The index of the Version to remove.
     * @return removed Version, or null if index is out of range.
     **/
    public Version removeVersion(int index) {
        return (Version)getVersionVector().remove(index);
    }

    /**
     * Set the Version at a specified index in this history, this replaces
     * the Version currently at that index. Persistent history is not effected.
     * @param index Index into the group
     * @param vVersion Version to be added.
     * @throws IndexOutOfBoundsException If index is out of range.
     **/
    public void setVersionAt(int index, Version vVersion) throws IndexOutOfBoundsException {
        getVersionVector().set(index, vVersion);
	}

    /**
     * Replace all the version elements in this history with those in the array
     * provided. Persistent history is not effected.
     * @param VersionArray Array of properties
     **/
    public void setVersion(Version[] versionArray) {
		setVersionVector(Arrays.asList(versionArray));
    }
}
