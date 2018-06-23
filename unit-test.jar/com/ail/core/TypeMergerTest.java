package com.ail.core;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ail.insurance.policy.Policy;

public class TypeMergerTest {

    @Mock
    private Core core;
    private Attribute source;
    private Attribute destination;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void nullStringReplacedWithNonNull() {
        source = new Attribute(null,"value", null);
        destination = new Attribute(null, null, null);

        new TypeMerger(source, destination, core).invoke();

        assertThat(destination.getValue(), is("value"));
    }

    @Test
    public void emptyStringReplacedWithNonNull() {
        source = new Attribute(null,"value", null);
        destination = new Attribute(null, "", null);

        new TypeMerger(source, destination, core).invoke();

        assertThat(destination.getValue(), is("value"));
    }

    @Test
    public void emptyStringNotReplacedNull() {
        source = new Attribute(null, null, null);
        destination = new Attribute(null, "", null);

        new TypeMerger(source, destination, core).invoke();

        assertThat(destination.getValue(), is(""));
    }

    @Test
    public void checkThatInternationalisedAttributeValuesAreMerged() {
        Attribute parent = new Attribute("parent", null, "string");
        Attribute child = new Attribute("line", "i18n_?", "choice,options=-1#i18n_?|1#Taxi|2#Black Car|3#Standard Black Car|4#Preferred Black Car|5#Car Service|6#Luxury Car Service|7#Preferred Luxury Car Service|8#Ambulette|9#Limo|10#CAP");
        parent.addAttribute(child);

        Attribute target = new Attribute("target", null, "string");
        target.mergeWithDataFrom(parent, core);

        assertThat(target.getAttribute().get(0).getValue(), is("i18n_?"));
    }

    @Test
    public void checkThatDateAttributeValuesAreMerged() {
        Attribute parent = new Attribute("parent", null, "string");
        Attribute child = new Attribute("line", "mm/dd/yyyy", "date,max=0,pattern=MM/dd/yyyy");
        parent.addAttribute(child);

        Attribute target = new Attribute("target", null, "string");
        target.mergeWithDataFrom(parent, core);

        assertThat(target.getAttribute().get(0).getValue(), is("mm/dd/yyyy"));
    }

    @Test
    public void testLongObject() {
        Policy donor=new Policy();
        donor.setOwningUser(1234L);

        Policy target = new Policy();
        target.mergeWithDataFrom(donor, core);

        assertTrue(donor.getOwningUser().equals(target.getOwningUser()));
    }
}
