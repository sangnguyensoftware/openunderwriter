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

package com.ail.core;

import static java.util.Locale.UK;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests to exercise the facilities offered by he Core's Attribute class.
 */
public class TestAttributeXMLTranslations {

    private boolean oneTimeSetupDone=false;

    @Before
    public void oneTimeSetup() {
        if (!oneTimeSetupDone) {
            new CoreProxy().resetConfigurations();
            oneTimeSetupDone=true;
        }
    }

    @Before
    public void setUp() throws Exception {
        ThreadLocale.setThreadLocale(UK);
    }

    @After
    public void teadDown() throws Exception {
        ThreadLocale.setThreadLocale(UK);
    }


    /**
     * In versions of the core up to and including 2.2 the attribute value property was always mapped to an xml attribute. For 2.3
     * the value was moved to the text of the element to allow for markup within the value - particularly for attributes of type "note".
     * This test checks that the new approach works, and that backward compatibility has been maintained - i.e. that 2.3 will
     * correctly read Xml generated from pre-2.3 systems.
     */
    @Test
    public void testXmlTextMapping() throws Exception{
    	Attribute attr;
    	String pre23Sample="<attribute xsi:type='com.ail.core.Attribute' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' id='name' format='note' value='hello'/>";
    	String post23Sample="<attribute xsi:type='com.ail.core.Attribute' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' id='name' format='note'><value><![CDATA[My Hat <b>Is</b> Off]]></value></attribute>";
    	String subAttrPre23Sample="<attribute xsi:type='com.ail.core.Attribute' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' id='name' format='note' value='hello'>"+
    	                              "<attribute id='code' format='string' value='a value'/>"+
    	                          "</attribute>";
       	String subAttrPost23Sample="<attribute xsi:type='com.ail.core.Attribute' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' id='name' format='note'>"+
       	                              "<attribute id='code' format='string' value='a value'/>"+
       	                              "<value><![CDATA[My Hat <b>Is</b> Off]]></value>"+
       	                           "</attribute>";

       	// check that the value for a note is mapped to an element
       	attr=new Attribute("test", "<b>text</b>", "note");
    	String xml=new CoreProxy().toXML(attr).toString();
        assertTrue(xml.indexOf("<value>")>0);

        // check that the value for non-notes is mapped to an attribute
        attr=new Attribute("test", "hello", "string");
        String xml2=new CoreProxy().toXML(attr).toString();
        assertTrue(xml2.indexOf("<value>")==-1);

    	attr=new CoreProxy().fromXML(Attribute.class, new XMLString(pre23Sample));
    	assertEquals("name", attr.getId());
    	assertEquals("note", attr.getFormat());
    	assertEquals("hello", attr.getValue());

    	attr=new CoreProxy().fromXML(Attribute.class, new XMLString(post23Sample));
    	assertEquals("name", attr.getId());
    	assertEquals("note", attr.getFormat());
    	assertEquals("My Hat <b>Is</b> Off", attr.getValue());

        attr=new CoreProxy().fromXML(Attribute.class, new XMLString(subAttrPre23Sample));
        assertEquals("name", attr.getId());
        assertEquals("note", attr.getFormat());
        assertEquals("hello", attr.getValue());

        attr=new CoreProxy().fromXML(Attribute.class, new XMLString(subAttrPost23Sample));
        assertEquals("name", attr.getId());
        assertEquals("note", attr.getFormat());
        assertEquals("My Hat <b>Is</b> Off", attr.getValue());

        xml=new CoreProxy().toXML(attr).toString();
    	assertTrue(xml.contains("<![CDATA[My Hat <b>Is</b> Off]]>"));
    	assertFalse(xml.contains("\"My Hat Is Off\""));
    }

    @Test
    public void checkThatListsOfAttributesMarshalAndUnmarshal() throws XMLException {
        XMLString xmlList;

        {
            List<Attribute> attrs=new ArrayList<>();

            attrs.add(new Attribute("name", "tom", "string"));
            attrs.add(new Attribute("name", "pam", "string"));
            attrs.add(new Attribute("name", "peter", "string"));

            xmlList=new CoreProxy().toXML(attrs);
        }

        {
            @SuppressWarnings("unchecked")
            List<Attribute> attrs = new CoreProxy().fromXML(List.class, xmlList);
            assertThat(attrs.size(), is(3));
        }
    }
}

