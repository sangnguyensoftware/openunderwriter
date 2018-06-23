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
 * Interface to be implemented by any entity that represents a party that has
 * relationships with other parties.
 */
public interface HasPartyRelationships {
    List<PartyRelationship> getPartyRelationship();

    void setPartyRelationship(List<PartyRelationship> PartyRelationship);

    /**
     * Get the party (unique only) associated with a relationshipType using
     * and effective date of today. To be considered valid the PartyRelationship must
     * be valid for the current time (i.e. startDate <= current time <= endDate)
     * and there must be only one PartyRelationship matching the relationshipType. If no valid
     * match is found, null is returned.
     * @param relationshipType Type to search for.
     * @return Party or null if none is found.
     */
    public default Party fetchPartyForRelationshipType(String relationshipType) {
        return fetchPartyForRelationshipType(relationshipType, new Date());
    }

    public default Party fetchPartyForRelationshipType(String relationshipType, Date effectiveDate) {
        List<Party> parties = fetchPartiesForRelationshipTypes(effectiveDate, relationshipType);

        if (parties.size() != 1) {
            return null;
        }
        else {
            return parties.get(0);
        }
    }

    /**
     * Get all the Parties currently associated with a relationshipType using
     * and effective date of today. To be considered valid the PartyRelationship must
     * be valid for the current time (i.e. startDate <= current time <= endDate).
     * @param relationshipType Type to search for.
     * @return PartyRelationship or null if none is found.
     */
    public default List<Party> fetchPartiesForRelationshipTypes(String... relationshipTypes) {
        return fetchPartiesForRelationshipTypes(new Date(), relationshipTypes);
    }

    /**
     * Get all the Parties currently associated with a relationshipType that matches
     * the specified pattern using and effective date of today. To be considered
     * valid the PartyRelationship must be valid for the current time (i.e.
     * startDate <= current time <= endDate).
     * @param relationshipType Pattern matcher for types to search for.
     * @return PartyRelationship or null if none is found.
     */
    public default List<Party> fetchPartiesForRelationshipTypes(Pattern relationshipTypes) {
        return fetchPartiesForRelationshipTypes(new Date(), relationshipTypes);
    }

    /**
     * Get all the Parties currently associated with a relationshipType which match
     * the specified pattern using and specified effective date. To be considered
     * valid the PartyRelationship must be valid for the current time (i.e.
     * startDate <= effectiveDate <= endDate).
     * @param relationshipType Pattern matcher for types to search for.
     * @return List of matching Party objects.
     */
    public default List<Party> fetchPartiesForRelationshipTypes(Date effectiveDate, Pattern relationshipTypes) {
        return getPartyRelationship().
            stream().
            filter(
                    pr -> relationshipTypes.matcher(pr.getRelationship()).matches() && isBetween(pr.getStartDate(), pr.getEndDate(), effectiveDate)
            ).
            map(
                    pr -> pr.getParty()
            ).
            collect(Collectors.toList());
    }

    /**
     * Get all the Parties currently associated with a relationshipType using
     * and specified effective date. To be considered valid the PartyRelationship must
     * be valid for the current time (i.e. startDate <= effectiveDate <= endDate).
     * @param relationshipType Type to search for.
     * @return List of matching Party objects.
     */
    public default List<Party> fetchPartiesForRelationshipTypes(Date effectiveDate, String... relationshipTypes) {
        List<String> relationshipTypesList = asList(relationshipTypes);

        return getPartyRelationship().
            stream().
            filter(
                    pr -> relationshipTypesList.contains(pr.getRelationship()) && isBetween(pr.getStartDate(), pr.getEndDate(), effectiveDate)
            ).
            map(
                    pr -> pr.getParty()
            ).
            collect(Collectors.toList());
    }

    /**
     * Set (and replace existing) the party associated with a relationship type. All
     * existing parties with the same relationship type will be end-dated (assuming they
     * haven't been already). The new party will be stored with a start date of
     * 'now' and a null end date.
     * @param party
     *            Party instance to be associated.
     * @param relationshipType
     *            Type of association.
     */
    public default void setPartyForRelationshipTypes(Party party, String... relationshipTypes) {
        AtomicBoolean alreadyPresent = new  AtomicBoolean();
        List<String> relationshipTypesList = asList(relationshipTypes);

        getPartyRelationship().
            stream().
            filter(pr -> relationshipTypesList.contains(pr.getRelationship()) && pr.getEndDate() == null).
            forEach(pr -> {
                if (party.equals(pr.getParty())) {
                    alreadyPresent.set(true);
                }
                else {
                    pr.endDate();
                }
            });

        if (alreadyPresent.get() == false) {
            addPartyForRelationshipTypes(party, relationshipTypes);
        }
    }

    /**
     * Add a party to the list of parties associated with a relationship type. The new party
     * will be stored with a start date of 'now' and a null end date.
     * @param party
     *            Party instance to be associated.
     * @param relationshipType
     *            Type of association.
     */
    public default void addPartyForRelationshipTypes(Party party, String... relationshipTypes) {
        asList(relationshipTypes).
            forEach(rt -> getPartyRelationship().add(new PartyRelationship(rt, party, new Date(), null)));
    }

    /**
     * End date all party relationships that match the type and party specified.
     * @param party
     * @param relationshipType
     */
    public default void removePartyFromRelationshipTypes(Party party, String... relationshipTypes) {
        List<String> relationshipTypesList = asList(relationshipTypes);

        getPartyRelationship().
            stream().
            filter(pr -> pr.getParty().equals(party) && relationshipTypesList.contains(pr.getRelationship()) && pr.getEndDate() == null).
            forEach(pr -> pr.endDate());
    }

    /**
     * Fetch a list of the relationship types that are applicable to 'this' class.
     * @return List or party relationship types.
     * @throws BaseException
     */
    public default Set<String> getApplicableRelationships() throws BaseException {
        LabelsForSubjectCommand lfsc = CoreContext.getCoreProxy().newCommand(LabelsForSubjectCommand.class);
        lfsc.setSubjectArg(this.getClass());
        lfsc.setDiscriminatorArg(Party.PARTY_RELATIONSHIP_TYPES_LABEL_DISCRIMINATOR);
        lfsc.invoke();
        return lfsc.getLabelsRet();
    }
}
