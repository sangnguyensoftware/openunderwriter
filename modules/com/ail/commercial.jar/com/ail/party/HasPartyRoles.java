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
package com.ail.party;

import static com.ail.core.Functions.isBetween;
import static java.util.Arrays.asList;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.label.LabelsForSubjectService.LabelsForSubjectCommand;

/**
 * Interface to be implemented by all entities that have an association with
 * Party objects.
 */
public interface HasPartyRoles {
    List<PartyRole> getPartyRole();

    void setPartyRole(List<PartyRole> partyRole);

    /**
     * Get all the PartyRoles currently associated with a any roleType that matches the
     * supplied pattern using and effective date of today. To be considered valid
     * the PartyRole must be valid for the current time (i.e. startDate <= current
     * time <= endDate).
     * @param roleTypes Pattern matcher for role types to search for.
     * @return PartyRole or null if none is found.
     */
    public default List<PartyRole> fetchPartyRolesForRoleTypes(Pattern roleTypes) {
        return fetchPartyRolesForRoleTypes(new Date(), roleTypes);
    }

    /**
     * Get all the PartyRoles currently associated with a roleType that matches the
     * supplied pattern using and specified effective date. To be considered valid
     * the PartyRole must be valid for the current time (i.e. startDate <=
     * effectiveDate <= endDate).
     * @param roleTypes Pattern matcher for role types to search for.
     * @return List of matching Party objects.
     */
    public default List<PartyRole> fetchPartyRolesForRoleTypes(Date effectiveDate, Pattern roleTypes) {
        return getPartyRole().
            stream().
            filter(
                    pr -> roleTypes.matcher(pr.getRole()).matches() && isBetween(pr.getStartDate(), pr.getEndDate(), effectiveDate)
            ).
            collect(Collectors.toList());
    }

    /**
     * Get the party (unique only) associated with a roleType using
     * and effective date of today. To be considered valid the PartyRole must
     * be valid for the current time (i.e. startDate <= current time <= endDate)
     * and there must be only one PartyRole matching the roleType. If no valid
     * match is found, null is returned.
     * @param roleType Role type to search for.
     * @return Party or null if none is found.
     */
    public default Party fetchPartyForRoleType(String roleType) {
        return fetchPartyForRoleType(roleType, new Date());
    }

    public default Party fetchPartyForRoleType(String roleType, Date effectiveDate) {
        List<Party> parties = fetchPartiesForRoleTypes(effectiveDate, roleType);

        if (parties.size() != 1) {
            return null;
        }
        else {
            return parties.get(0);
        }
    }

    /**
     * Get all the Parties currently associated with a roleType using
     * and effective date of today. To be considered valid the PartyRole must
     * be valid for the current time (i.e. startDate <= current time <= endDate).
     * @param roleType Role type to search for.
     * @return Parties or null if none is found.
     */
    public default List<Party> fetchPartiesForRoleTypes(String... roleTypes) {
        return fetchPartiesForRoleTypes(new Date(), roleTypes);
    }

    /**
     * Get all the Parties currently associated with a any roleType that matches the
     * supplied pattern using and effective date of today. To be considered valid
     * the PartyRole must be valid for the current time (i.e. startDate <= current
     * time <= endDate).
     * @param roleTypes Pattern matcher for role types to search for.
     * @return Parties or null if none is found.
     */
    public default List<Party> fetchPartiesForRoleTypes(Pattern roleTypes) {
        return fetchPartiesForRoleTypes(new Date(), roleTypes);
    }

    /**
     * Get all the Parties currently associated with a roleType using
     * and specified effective date. To be considered valid the PartyRole must
     * be valid for the current time (i.e. startDate <= effectiveDate <= endDate).
     * @param roleType Role type to search for.
     * @return List of matching Party objects.
     */
    public default List<Party> fetchPartiesForRoleTypes(Date effectiveDate, String... roleTypes) {
        List<String> roleTypesList = asList(roleTypes);

        return getPartyRole().
            stream().
            filter(
                    pr -> roleTypesList.contains(pr.getRole()) && isBetween(pr.getStartDate(), pr.getEndDate(), effectiveDate)
            ).
            map(
                    pr -> pr.getParty()
            ).
            collect(Collectors.toList());
    }

    /**
     * Get all the Parties currently associated with a roleType that matches the
     * supplied pattern using and specified effective date. To be considered valid
     * the PartyRole must be valid for the current time (i.e. startDate <=
     * effectiveDate <= endDate).
     * @param roleTypes Pattern matcher for role types to search for.
     * @return List of matching Party objects.
     */
    public default List<Party> fetchPartiesForRoleTypes(Date effectiveDate, Pattern roleTypes) {
        return fetchPartyRolesForRoleTypes(effectiveDate, roleTypes).stream().map(pr -> pr.getParty()).collect(Collectors.toList());
    }

    /**
     * Set (and replace existing) the party associated with a role type. All
     * existing parties with the same role type will be end-dated (assuming they
     * haven't been already). The new party will be stored with a start date of
     * 'now' and a null end date.
     * @param party
     *            Party instance to be associated.
     * @param roleType
     *            Type of association.
     */
    public default void setPartyForRoleTypes(Party party, String... roleTypes) {
        AtomicBoolean alreadyPresent = new  AtomicBoolean();
        List<String> roleTypesList = asList(roleTypes);

        getPartyRole().
            stream().
            filter(pr -> roleTypesList.contains(pr.getRole()) && pr.getEndDate() == null).
            forEach(pr -> {
                if (party.equals(pr.getParty())) {
                    alreadyPresent.set(true);
                }
                else {
                    pr.endDate();
                }
            });

        if (alreadyPresent.get() == false) {
            addPartyForRoleTypes(party, roleTypes);
        }
    }

    /**
     * Add a party to the list of parties associated with a role type. The new party
     * will be stored with a start date of 'now' and a null end date.
     * @param party
     *            Party instance to be associated.
     * @param roleType
     *            Type of association.
     */
    public default void addPartyForRoleTypes(Party party, String... roleTypes) {
        asList(roleTypes).
            forEach(rt -> getPartyRole().add(new PartyRole(rt, party, new Date(), null)));
    }

    /**
     * End date all party roles that match the type and party specified.
     * @param party
     * @param roleType
     */
    public default void removePartyFromRoleTypes(Party party, String... roleTypes) {
        List<String> roleTypesList = asList(roleTypes);

        getPartyRole().
            stream().
            filter(pr -> pr.getParty().equals(party) && roleTypesList.contains(pr.getRole()) && pr.getEndDate() == null).
            forEach(pr -> pr.endDate());
    }

    /**
     * Fetch a list of the role types that are applicable to 'this' class.
     * @return List or party role types.
     * @throws BaseException
     */
    public default Set<String> getApplicableRoles() throws BaseException {
        LabelsForSubjectCommand lfsc = CoreContext.getCoreProxy().newCommand(LabelsForSubjectCommand.class);
        lfsc.setSubjectArg(this.getClass());
        lfsc.setDiscriminatorArg(Party.PARTY_ROLE_TYPES_LABEL_DISCRIMINATOR);
        lfsc.invoke();
        return lfsc.getLabelsRet();
    }
}
