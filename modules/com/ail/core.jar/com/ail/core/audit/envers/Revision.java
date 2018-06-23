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
package com.ail.core.audit.envers;

import static javax.persistence.FetchType.EAGER;
import static org.hibernate.annotations.FetchMode.JOIN;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.envers.ModifiedEntityNames;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import com.ail.core.logging.ServiceRequestRecord;

@Entity
@RevisionEntity(RevisionBuilder.class)
public class Revision implements com.ail.core.audit.RevisionDetails {
    @Id
    @GeneratedValue
    @RevisionNumber
    private int id;

    @RevisionTimestamp
    private long timestamp;

    @ModifiedEntityNames
    @ElementCollection(fetch = EAGER)
    @JoinTable(name = "recRevisionChanges", joinColumns = @JoinColumn(name = "recRev"))
    @Column(name = "recEntityName")
    @Fetch(JOIN)
    private Set<String> modifiedEntityNames = new HashSet<>();

    @OneToOne
    @JoinColumn(name = "serviceRequestRecord")
    private ServiceRequestRecord serviceRequestRecord;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    @Transient
    public Date getRevisionDate() {
        return new Date(timestamp);
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public Set<String> getModifiedEntityNames() {
        return modifiedEntityNames;
    }

    @Override
    public void setModifiedEntityNames(Set<String> modifiedEntityNames) {
        this.modifiedEntityNames = modifiedEntityNames;
    }

    @Override
    public ServiceRequestRecord getServiceRequestRecord() {
        return serviceRequestRecord;
    }

    @Override
    public void setServiceRequestRecord(ServiceRequestRecord serviceRequestRecord) {
        this.serviceRequestRecord = serviceRequestRecord;
    }
}
