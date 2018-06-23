package com.ail.party;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class AddressTest {

    @Test
    public void testFullAddressAllFields() {
        Address address = new Address("", "line1", "line2", "line3", "line4", "line5", "town", "county", "country", "postcode");
        assertThat(address.getFullAddress(), is("line1, line2, line3, line4, line5, town, county, country. postcode"));
    }

    @Test
    public void testFullAddressMissingLine1() {
        Address address = new Address("", "", "line2", "line3", "line4", "line5", "town", "county", "country", "postcode");
        assertThat(address.getFullAddress(), is("line2, line3, line4, line5, town, county, country. postcode"));
    }

    @Test
    public void testFullAddressMissingMidLine() {
        Address address = new Address("", "line1", "line2", null, "line4", "line5", "town", "county", "country", "postcode");
        assertThat(address.getFullAddress(), is("line1, line2, line4, line5, town, county, country. postcode"));
    }

    @Test
    public void testFullAddressMissingCountry() {
        Address address = new Address("", "line1", "line2", "line3", "line4", "line5", "town", "county", null, "postcode");
        assertThat(address.getFullAddress(), is("line1, line2, line3, line4, line5, town, county. postcode"));
    }

    @Test
    public void testFullAddressMissingPostcode() {
        Address address = new Address("", "line1", "line2", "line3", "line4", "line5", "town", "county", "country", null);
        assertThat(address.getFullAddress(), is("line1, line2, line3, line4, line5, town, county, country."));
    }
}
