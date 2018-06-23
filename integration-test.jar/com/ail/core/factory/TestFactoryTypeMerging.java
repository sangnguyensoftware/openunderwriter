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

package com.ail.core.factory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.ail.core.Core;
import com.ail.core.CoreProxy;
import com.ail.core.CoreUserBaseCase;
import com.ail.core.History;
import com.ail.core.Version;
import com.ail.core.VersionEffectiveDate;
import com.ail.core.configure.ConfigurationHandler;
import com.ail.core.configure.Group;
import com.ail.core.dummyservice.DummyService;

/**
 * The core's factories support the concept of basing one type on another - in effect
 * allowing type definition to extend one another. The tests here try exercise that
 * support.
 */
public class TestFactoryTypeMerging extends CoreUserBaseCase {
    private static boolean oneTimeSetupDone=false;

    /**
     * Sets up the fixture (run before every test). Get an instance of Core, and
     * delete the testnamespace from the config table.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        setCore(new Core(this));

        if (!oneTimeSetupDone) {
            System.setProperty("org.xml.sax.parser", "org.apache.xerces.parsers.SAXParser");
            System.setProperty("java.protocol.handler.pkgs", "com.ail.core.urlhandler");
            System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");

            tidyUpTestData();

            new CoreProxy().resetConfigurations();
            new DummyService().resetConfiguration();
            resetConfiguration();

            oneTimeSetupDone=true;
        }

        ConfigurationHandler.resetCache();

    }

    /**
     * Always select the latest configuration.
     *
     * @return
     */
    @Override
    public VersionEffectiveDate getVersionEffectiveDate() {
        return new VersionEffectiveDate();
    }

    /**
     * The Type class offers a merge() method which will merge values from a 'donor' object
     * into a 'subject'. The type factories use this method to implement the 'Extends' facility where one
     * type definition extends another. The javadocs {@link com.ail.core.Type#mergeWithDataFrom(com.ail.core.Type, Core) here}
     * describe what should happen during a merge. This test checks that functionality.
     * @throws Exception
     */
    @Test
    public void testSimpleAttributeMerging() throws Exception {
        Version merged=(Version)getCore().newType("ExtendingType");

        assertEquals("H.G.Wells", merged.getAuthor());
        assertEquals("The loganberry's are sweet", merged.getComment());
        assertEquals("Peach and mint", merged.getSource());

        assertNotNull(merged.xpathGet("attribute[id='baseattr']"));
        assertNotNull(merged.xpathGet("attribute[id='subattr']"));
        assertEquals("string", merged.xpathGet("attribute[id='one']/format"));
        assertEquals("feet", merged.xpathGet("attribute[id='one']/unit"));
        assertEquals("overriden-two", merged.xpathGet("attribute[id='two']/value"));
        assertEquals("string,32", merged.xpathGet("attribute[id='two']/format"));

        assertNotNull(merged.xpathGet("attribute[id='root']"));
        assertNotNull(merged.xpathGet("attribute[id='root']/attribute[id='branch1']"));
        assertNotNull(merged.xpathGet("attribute[id='root']/attribute[id='branch2']"));
        assertNotNull(merged.xpathGet("attribute[id='root2']/attribute[id='branch3']"));
        assertEquals("string,32", merged.xpathGet("attribute[id='root']/attribute[id='branch1']/format"));
        assertEquals("ExtBranch", merged.xpathGet("attribute[id='root']/attribute[id='branch1']/value"));

        Group group=(Group)getCore().newType("ExtendingGroup");

        assertEquals("mygroup", group.getName());
        assertEquals("value 1", group.findParameter("param1").getValue());
        assertEquals("overriden value 2", group.findParameter("param2").getValue());
        assertEquals("value 3", group.findParameter("param3").getValue());
    }

    /**
     * Castor should support the use of &lt;xi:include&gt;. This test instantiates a type whose definition
     * uses xi:include to pull in external content.
     * @throws Exception
     */
    @Test
    public void testXmlInclude() throws Exception {
        History h=(History)getCore().newType("Includer");
        assertTrue(getCore().toXML(h).toString().indexOf("bloody hell")!=-1);
    }

    /**
     * xi:include doesn't support relative URIs out of the box. Without it, all URI's used to include
     * content into an XML document have to be absolute. This isn't convenient, especially when we want
     * let users move products and their type around in CMS without having to edit the content of the
     * types which those products define. For this reason, extends the XML parser to support relative URLs.
     * This test builds an instance of a type which includes using a relative URI.
     * @throws Exception
     */
    @Test
    public void testXmlIncludeRelativeUrl() throws Exception {
        History h=(History)getCore().newType("IncluderRelative");
        assertTrue(getCore().toXML(h).toString().indexOf("bloody hell")!=-1);
    }

    @Test
    public void testMapMerge() throws Exception {
        Version version;
        MapTrialType donor=new MapTrialType();
        MapTrialType subject=new MapTrialType();

        version=new Version();
        version.setVersion("1");
        donor.getMyMap().put("one", version);

        version=new Version();
        version.setVersion("2");
        donor.getMyMap().put("two", version);

        version=new Version();
        version.setVersion("3");
        subject.getMyMap().put("three", version);

        subject.mergeWithDataFrom(donor, getCore());

        assertTrue(subject.getMyMap().containsKey("one"));
        assertTrue(subject.getMyMap().containsKey("two"));
        assertTrue(subject.getMyMap().containsKey("three"));
    }
}