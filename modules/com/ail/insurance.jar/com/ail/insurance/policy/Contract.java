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

import java.util.Collection;
import java.util.Date;
import java.util.Vector;

import com.ail.annotation.TypeDefinition;
import com.ail.core.Type;
import com.ail.financial.MoneyProvision;
import com.ail.party.Organisation;
import com.ail.party.Party;

/**
 * A contract collects a group of policy objects together into a larger unit.
 */
@TypeDefinition
public class Contract extends Type {
    private static final long serialVersionUID = -2392239121746383188L;

    /**
     * @link aggregation
     * @supplierCardinality 0..*
     * @directed
     * @clientCardinality 1
     */
    /*# Policy policyLink; */

    /** The collection of policies associated with this contract */
    private Vector<Policy> policy = new Vector<Policy>();

    /** The company 
     * @link aggregation
     * @supplierCardinality 1
     * @clientCardinality 0..*
     * @clientQualifier company*/
    private Organisation company;

    /** The Date at which the contract comes into force. */
    private Date startDate;

    /** The date when the contract ceases to be in force. */
    private Date endDate;

    /** description 
     * @link aggregation
     * @clientCardinality 0..*
     * @supplierCardinality 1..*
     * @clientQualifier client*/
    private Party client;

    /** The financial amount associated with this contract 
     * @clientRole financialAmount*/
    private MoneyProvision financialAmount;

    /**
     * Get the collection of instances of com.ail.insurance.policy.Policy associated with this object.
     * @return policy A collection of instances of Excess
     * @see #setPolicy
     */
    public Collection<Policy> getPolicy() {
        return policy;
    }

    /**
     * Set the collection of instances of com.ail.insurance.policy.Policy associated with this object.
     * @param policy A collection of instances of Excess
     * @see #getPolicy
     */
    public void setPolicy(Collection<Policy> policy) {
        this.policy = new Vector<Policy>(policy);
    }

    /**
     * Get a count of the number of com.ail.insurance.policy.Policy instances associated with this object
     * @return Number of instances
     */
    public int getPolicyCount() {
        return this.policy.size();
    }

    /**
     * Fetch a spacific com.ail.insurance.policy.Policy from the collection by index number.
     * @param i Index of element to return
     * @retun The instance of com.ail.insurance.policy.Policy at the specified index
     */
    public Policy getPolicy(int i) {
        return (com.ail.insurance.policy.Policy) this.policy.get(i);
    }

    /**
     * Remove the element specified from the list.
     * @param i Index of element to remove
     */
    public void removePolicy(int i) {
        this.policy.remove(i);
    }

    /**
     * Remove the specified instance of com.ail.insurance.policy.Policy from the list.
     * @param policy Instance to be removed
     */
    public void removePolicy(Policy policy) {
        this.policy.remove(policy);
    }

    /**
     * Add an instance of com.ail.insurance.policy.Policy to the list associated with this object.
     * @param policy Instance to add to list
     */
    public void addPolicy(Policy policy) {
        this.policy.add(policy);
    }

    /**
     * Getter returning the value of the company property. The company
     * @return Value of the company property
     */
    public Organisation getCompany() {
        return company;
    }

    /**
     * Setter to update the value of the company property. The company
     * @param company New value for the company property
     */
    public void setCompany(Organisation company) {
        this.company = company;
    }

    /**
     * Getter returning the value of the startDate property. The Date at which the contract comes into force.
     * @return Value of the startDate property
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Setter to update the value of the startDate property. The Date at which the contract comes into force.
     * @param startDate New value for the startDate property
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * Getter returning the value of the endDate property. The date when the contract ceases to be in force.
     * @return Value of the endDate property
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * Setter to update the value of the endDate property. The date when the contract ceases to be in force.
     * @param endDate New value for the endDate property
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * Getter returning the value of the client property. description
     * @return Value of the client property
     */
    public Party getClient() {
        return client;
    }

    /**
     * Setter to update the value of the client property. description
     * @param client New value for the client property
     */
    public void setClient(Party client) {
        this.client = client;
    }

    /**
     * Getter returning the value of the financialAmount property. The financial amount associated with this contract
     * @return Value of the financialAmount property
     */
    public MoneyProvision getFinancialAmount() {
        return financialAmount;
    }

    /**
     * Setter to update the value of the financialAmount property. The financial amount associated with this contract
     * @param financialAmount New value for the financialAmount property
     */
    public void setFinancialAmount(MoneyProvision financialAmount) {
        this.financialAmount = financialAmount;
    }
}
