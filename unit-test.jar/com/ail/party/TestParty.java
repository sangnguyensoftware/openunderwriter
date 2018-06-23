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

import static com.ail.insurance.policy.Broker.CLAIM_TELEPHONE_NUMBER;
import static com.ail.party.EmailAddress.MAIN_EMAIL_ADDRESS;
import static com.ail.party.PhoneNumber.MAIN_PHONE_NUMBER;
import static com.ail.party.PhoneNumber.MOBILE_PHONE_NUMBER;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

public class TestParty {

    private static final String EMAIL_ADDRESS_1 = "test@example.com";
    private static final String PHONE_NUMBER_1 = "12345678";
    private static final String PHONE_NUMBER_2 = "87654321";
    private static final String DUMMY_RELATIONSHIP_TYPE = "DUMMY_RELATIONSHIP_TYPE";
    private Party sut;

    @Before
    public void setup() {
        sut = new Party();
    }

    @Test
    public void fetchPartyRelationshipsWithNoParties() {
        Party party1 = mock(Party.class);
        Party party2 = mock(Party.class);
        Party party3 = mock(Party.class);

        sut.addPartyForRelationshipTypes(party1, DUMMY_RELATIONSHIP_TYPE);
        assertThat(sut.fetchPartiesForRelationshipTypes(DUMMY_RELATIONSHIP_TYPE), contains(party1));

        sut.addPartyForRelationshipTypes(party2, DUMMY_RELATIONSHIP_TYPE);
        assertThat(sut.fetchPartiesForRelationshipTypes(DUMMY_RELATIONSHIP_TYPE), contains(party1, party2));

        sut.addPartyForRelationshipTypes(party3, DUMMY_RELATIONSHIP_TYPE);
        assertThat(sut.fetchPartiesForRelationshipTypes(DUMMY_RELATIONSHIP_TYPE), contains(party1, party2, party3));
    }

    @Test
    public void setPartyRelationship() {
        Party party1 = mock(Party.class);
        Party party2 = mock(Party.class);

        sut.setPartyForRelationshipTypes(party1, DUMMY_RELATIONSHIP_TYPE);
        assertThat(sut.fetchPartiesForRelationshipTypes(DUMMY_RELATIONSHIP_TYPE), contains(party1));

        sut.setPartyForRelationshipTypes(party2, DUMMY_RELATIONSHIP_TYPE);
        assertThat(sut.fetchPartiesForRelationshipTypes(DUMMY_RELATIONSHIP_TYPE), contains(party2));
    }

    @Test
    public void setRemotePartyRelationship() {
        Party party1 = mock(Party.class);

        sut.setPartyForRelationshipTypes(party1, DUMMY_RELATIONSHIP_TYPE);
        assertThat(sut.fetchPartiesForRelationshipTypes(DUMMY_RELATIONSHIP_TYPE), contains(party1));

        sut.removePartyFromRelationshipTypes(party1, DUMMY_RELATIONSHIP_TYPE);
        assertThat(sut.fetchPartiesForRelationshipTypes(DUMMY_RELATIONSHIP_TYPE), hasSize(0));
    }

    /**
     * Test valid arguments for the Rate constructor
     *
     * @throws Exception
     */
    @Test
    public void testGoodFormats() throws Exception {
        Address addr = new Address("The House", "The Road", null, null, null, "Town", "County", "", "ABC 1DE");

        assertEquals("The House, The Road, Town, County. ABC 1DE", addr.toString());
    }

    @Test
    public void testAddContactSystem() {
        ContactSystem phone = new PhoneNumber(MAIN_PHONE_NUMBER, PHONE_NUMBER_1);
        sut.addPrimaryContactSystem(phone);
        assertThat(sut.getContactSystem(), hasSize(1));
        assertThat(sut.getContactSystem(), contains(phone));

        ContactSystem email = new EmailAddress(MAIN_EMAIL_ADDRESS, EMAIL_ADDRESS_1);
        sut.addPrimaryContactSystem(email);
        assertThat(sut.getContactSystem(), hasSize(2));
        assertThat(sut.getContactSystem(), containsInAnyOrder(phone, email));
    }

    @Test
    public void testRemoveContactSystem() {
        ContactSystem phone = new PhoneNumber(MAIN_PHONE_NUMBER, PHONE_NUMBER_1);
        ContactSystem email = new EmailAddress(MAIN_EMAIL_ADDRESS, EMAIL_ADDRESS_1);

        sut.addPrimaryContactSystem(phone);
        sut.addPrimaryContactSystem(email);

        assertThat(sut.fetchContactSystems(), hasSize(2));
        assertThat(sut.fetchContactSystems(), containsInAnyOrder(phone, email));

        sut.removeContactSystem(phone);

        assertThat(sut.fetchContactSystems(), hasSize(1));
        assertThat(sut.fetchContactSystems(), containsInAnyOrder(email));

        sut.removeContactSystem(email);

        assertThat(sut.fetchContactSystems(), hasSize(0));
    }

    @Test
    public void removeContactSystemMustOnlyRemoveFromCurrentList() {
        ContactSystem phone = new PhoneNumber(MAIN_PHONE_NUMBER, PHONE_NUMBER_1);
        ContactSystem email = new EmailAddress(MAIN_EMAIL_ADDRESS, EMAIL_ADDRESS_1);

        sut.addPrimaryContactSystem(phone);
        sut.addPrimaryContactSystem(email);

        sut.removeContactSystem(phone);

        assertThat(sut.fetchContactSystems(), hasSize(1));
        assertThat(sut.fetchContactSystems(), containsInAnyOrder(email));

        assertThat(sut.getContactSystem(), hasSize(2));
        assertThat(sut.getContactSystem(), containsInAnyOrder(phone, email));
    }

    @Test
    public void testSettingAndResettingPrimaryContactSystem() {
        sut.addPrimaryContactSystem(new PhoneNumber(MAIN_PHONE_NUMBER, PHONE_NUMBER_1));
        sut.addPrimaryContactSystem(new PhoneNumber(MAIN_PHONE_NUMBER, PHONE_NUMBER_2));

        assertThat(sut.fetchContactSystems(), hasSize(1));
        assertThat(sut.getContactSystem(), hasSize(2));

        assertThat(sut.fetchPrimaryContactSystem(PhoneNumber.class).get(), is(not(nullValue(PhoneNumber.class))));
        assertThat(sut.fetchPrimaryContactSystem(PhoneNumber.class).get().getPhoneNumber(), is(PHONE_NUMBER_2));
    }

    @Test
    public void testSettingAndResettingPrimaryContactSystemWithAlternateType() {
        sut.addPrimaryContactSystem(new PhoneNumber(MAIN_PHONE_NUMBER, PHONE_NUMBER_1));
        sut.addPrimaryContactSystem(new PhoneNumber(MOBILE_PHONE_NUMBER, PHONE_NUMBER_2));

        assertThat(sut.fetchContactSystems(), hasSize(2));
        assertThat(sut.getContactSystem(), hasSize(2));

        assertThat(sut.fetchPrimaryContactSystem(PhoneNumber.class).get(), is(not(nullValue(PhoneNumber.class))));
        assertThat(sut.fetchPrimaryContactSystem(PhoneNumber.class).get().getPhoneNumber(), is(PHONE_NUMBER_2));
    }

    @Test
    public void testAddingTheSameObjectAsPrimaryMultipleTimes() {
        sut.addPrimaryContactSystem(new PhoneNumber(MAIN_PHONE_NUMBER, PHONE_NUMBER_1));
        sut.addPrimaryContactSystem(new PhoneNumber(MAIN_PHONE_NUMBER, PHONE_NUMBER_1));
        sut.addPrimaryContactSystem(new PhoneNumber(MAIN_PHONE_NUMBER, PHONE_NUMBER_1));

        assertThat(sut.fetchContactSystems(), hasSize(1));
        assertThat(sut.getContactSystem(), hasSize(1));

        assertThat(sut.fetchPrimaryContactSystem(PhoneNumber.class).get(), is(not(nullValue(PhoneNumber.class))));
        assertThat(sut.fetchPrimaryContactSystem(PhoneNumber.class).get().getPhoneNumber(), is(PHONE_NUMBER_1));
    }

    @Test
    public void testPrimaryContactSelectionDefaultsIfNoneIsSpecified() {
        sut.addPrimaryContactSystem(new PhoneNumber(MAIN_PHONE_NUMBER, PHONE_NUMBER_1));

        assertThat(sut.fetchPrimaryContactSystem(PhoneNumber.class).get(), is(not(nullValue(PhoneNumber.class))));
    }

    @Test
    public void testManualPrimaryContactSelection() {
        sut.addPrimaryContactSystem(new PhoneNumber(MAIN_PHONE_NUMBER, PHONE_NUMBER_1));
        sut.addPrimaryContactSystem(new PhoneNumber(MOBILE_PHONE_NUMBER, PHONE_NUMBER_2));

        assertThat(sut.fetchPrimaryContactSystem(PhoneNumber.class).get(), is(not(nullValue(PhoneNumber.class))));
        assertThat(sut.fetchPrimaryContactSystem(PhoneNumber.class).get().getType(), is(MOBILE_PHONE_NUMBER));
    }

    @Test
    public void testPrimaryContactCannotBeDetermined() {
        sut.addContactSystem(new PhoneNumber(MAIN_PHONE_NUMBER, PHONE_NUMBER_1));
        sut.addContactSystem(new PhoneNumber(MOBILE_PHONE_NUMBER, PHONE_NUMBER_2));

        assertThat(sut.fetchPrimaryContactSystem(PhoneNumber.class).isPresent(), is(true));
    }

    @Test
    public void testPreDefinedContactSystemsWithStartDatesAreRespected() {
        Date startDate = mock(Date.class);
        sut.addPrimaryContactSystem(new PhoneNumber(startDate, null, MAIN_PHONE_NUMBER, PHONE_NUMBER_1));
        assertThat(sut.fetchPrimaryContactSystem(PhoneNumber.class).get().getStartDate(), is(startDate));
    }

    @Test
    public void testFetchWithNoMatches() {
        sut.addPrimaryContactSystem(new PhoneNumber(MAIN_PHONE_NUMBER, PHONE_NUMBER_1));
        assertThat(sut.fetchPrimaryContactSystem(Address.class).isPresent(), is(false));
    }

    @Test
    public void testFetchContactSystemIncludngAllTypes() {
        sut.addContactSystem(new PhoneNumber(MAIN_PHONE_NUMBER, PHONE_NUMBER_1));
        sut.addContactSystem(new PhoneNumber(MOBILE_PHONE_NUMBER, PHONE_NUMBER_2));

        assertThat(sut.fetchContactSystems(PhoneNumber.class, MAIN_PHONE_NUMBER), hasSize(1));

        assertThat(sut.fetchContactSystems(PhoneNumber.class), hasSize(2));
    }

    @Test
    public void testAddContactSystemThatsAlreadyPresent() {
        PhoneNumber phone = new PhoneNumber(MAIN_PHONE_NUMBER, PHONE_NUMBER_1);

        sut.addContactSystem(phone);
        sut.addContactSystem(phone);

        assertThat(sut.fetchContactSystems(PhoneNumber.class), hasSize(1));
    }

    @Test
    public void testAddReplacementContactSystem() {
        sut.addContactSystem(new PhoneNumber(MAIN_PHONE_NUMBER, PHONE_NUMBER_1));
        sut.addContactSystem(new PhoneNumber(MAIN_PHONE_NUMBER, PHONE_NUMBER_2));

        assertThat(sut.fetchContactSystems(PhoneNumber.class, MAIN_PHONE_NUMBER), hasSize(1));
        assertThat(sut.fetchContactSystems(PhoneNumber.class, MAIN_PHONE_NUMBER).iterator().next().getPhoneNumber(), is(PHONE_NUMBER_2));

        assertThat(sut.fetchContactSystems(PhoneNumber.class), hasSize(1));
    }

    @Test
    public void testAddedContactSystemsStartDatePreseved() {
        Date startDate = mock(Date.class);
        sut.addContactSystem(new PhoneNumber(startDate, null, MAIN_PHONE_NUMBER, PHONE_NUMBER_1));

        assertThat(sut.fetchContactSystems(PhoneNumber.class, MAIN_PHONE_NUMBER), hasSize(1));
        assertThat(sut.fetchContactSystems(PhoneNumber.class, MAIN_PHONE_NUMBER).iterator().next().getPhoneNumber(), is(PHONE_NUMBER_1));
        assertThat(sut.fetchContactSystems(PhoneNumber.class, MAIN_PHONE_NUMBER).iterator().next().getStartDate(), is(startDate));

        assertThat(sut.fetchContactSystems(PhoneNumber.class), hasSize(1));
    }

    @Test
    public void testPriorityResultsOnFetchPrimaryContactSystem() {
        PhoneNumber mainNumber = new PhoneNumber(null, null, MAIN_PHONE_NUMBER, PHONE_NUMBER_1);
        PhoneNumber claimNumber = new PhoneNumber(null, null, CLAIM_TELEPHONE_NUMBER, PHONE_NUMBER_2);

        sut.addContactSystem(mainNumber);
        sut.addContactSystem(claimNumber);

        assertThat(sut.fetchContactSystem(PhoneNumber.class, CLAIM_TELEPHONE_NUMBER, MAIN_PHONE_NUMBER).get(), is(claimNumber));
        assertThat(sut.fetchContactSystem(PhoneNumber.class, MAIN_PHONE_NUMBER, CLAIM_TELEPHONE_NUMBER).get(), is(mainNumber));
    }

    @Test
    public void testPriorityResultsOnFetchPrimaryContactSystemWithPrimary() {
        PhoneNumber mainNumber = new PhoneNumber(null, null, MAIN_PHONE_NUMBER, PHONE_NUMBER_1);
        PhoneNumber claimNumber = new PhoneNumber(null, null, CLAIM_TELEPHONE_NUMBER, PHONE_NUMBER_1);

        sut.addPrimaryContactSystem(mainNumber);
        sut.addContactSystem(claimNumber);

        assertThat(sut.fetchContactSystem(PhoneNumber.class).get(), is(mainNumber));
    }

    @Test
    public void testMultipleBlankPhoneNumberContactSystems() {
        PhoneNumber numberOne = new PhoneNumber(null, null, MAIN_PHONE_NUMBER, null);
        PhoneNumber numberTwo = new PhoneNumber(null, null, CLAIM_TELEPHONE_NUMBER, null);

        sut.addContactSystem(numberOne);
        sut.addContactSystem(numberTwo);

        assertThat(sut.fetchContactSystems(PhoneNumber.class).size(), is(2));
    }
}
