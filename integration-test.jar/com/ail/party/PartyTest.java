package com.ail.party;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.BeforeClass;
import org.junit.Test;

import com.ail.core.CoreContext;
import com.ail.core.CoreProxy;

public class PartyTest {
    CoreProxy core = new CoreProxy();

    @BeforeClass
    public static void setup() {
        CoreContext.initialise();
        CoreProxy core = new CoreProxy();

        core.openPersistenceSession();
        core.create(new Party("ID1", "Tom", "tom@example.com", "0011", "0021", new Address("line1", null, null, null, null, null, null, null, "PKPK123")));
        core.create(new Party("ID2", "Tom2", "tom2@example.com", "0012", "0022", null));
        core.create(new Party("ID3", "Dick", "dick@example.com", "0013", "0023", null));
        core.create(new Party("ID4", "Harry", "harry@example.com", "0014", "0024", null));
        core.closePersistenceSession();
    }

    @Test
    public void testFindBySystemId() {
        core.openPersistenceSession();

        assertThat(core.queryUnique("get.party.by.systemId", 1L), is(not(nullValue())));
        assertThat(core.queryUnique("get.party.by.systemId", 1000L), is(nullValue()));

        core.closePersistenceSession();
    }

    @Test
    public void testFindByLegalName() {
        core.openPersistenceSession();

        assertThat(core.query("get.parties.by.legalName", "Tom").size(), is(equalTo(1)));
        assertThat(core.query("get.parties.by.legalName", "Tom%").size(), is(equalTo(2)));
        assertThat(core.query("get.parties.by.legalName", "Fred").size(), is(equalTo(0)));
        assertThat(core.query("get.parties.by.legalName", "Harry").size(), is(equalTo(1)));

        core.closePersistenceSession();
    }

    @Test
    public void testFindByEmailAddress() {
        core.openPersistenceSession();

        assertThat(core.query("get.parties.by.emailAddress", "%@example.com").size(), is(equalTo(4)));

        core.closePersistenceSession();
    }

    @Test
    public void testFindByPostCode() {
        core.openPersistenceSession();

        assertThat(core.query("get.parties.by.postcode", "P%").size(), is(equalTo(1)));

        core.closePersistenceSession();
    }
}
