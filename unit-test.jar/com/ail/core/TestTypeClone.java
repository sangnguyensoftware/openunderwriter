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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import com.ail.core.document.Document;
import com.ail.core.document.DocumentType;
import com.ail.core.logging.LoggingArgumentImpl;
import com.ail.core.logging.LoggingCommandImpl;
import com.ail.core.logging.LoggingService.LoggingArgument;
import com.ail.core.logging.LoggingService.LoggingCommand;
import com.ail.core.logging.Severity;

/**
 * All classes in the type model must be cloneable - what's more the clone must
 * done in a deep fashion. The tests in the is class check that the base classes
 * manage this correctly.
 */
public class TestTypeClone {

    /**
     * Test to ensure that a sample of the Type classes in the core are
     * cloneable, and don't throw 'CloneNotSupported' exceptions when a clone is
     * attempted.
     * <ul>
     * <li>Create an instance of a few based types.</li>
     * <li>Clone each of the instances.</li>
     * <li>Fail if any exceptions are thrown.</li>
     * </ul>
     */
    @Test
    public void testCloneable() throws Exception {
        Type type = new Attribute();
        Version version = new Version();
        History history = new History();

        type.clone();
        version.clone();
        history.clone();
    }

    /**
     * Types must clone their own attributes, this test checks that some of the
     * Core's basic types handle this correctly.
     * <ul>
     * <li>For each type to be tested:</li>
     * <ul>
     * <li>Create an instance of the type</li>
     * <li>Populate some attributes on the instance</li>
     * <li>Clone the instance</li>
     * <li>Fail if the clone's attributes don't match the original's</li>
     * <li>Modify all the attributes on the original instance</li>
     * <li>Fail if any of the clone's attributes are modified</li>
     * </ul>
     * <li>Fail if any exceptions are thrown.</li>
     * </ul>
     */
    @Test
    public void testDeepCloneType() throws Exception {
        // create the original type.
        Type type = new Attribute();
        type.setSerialVersion(21);
        type.setLock(true);

        // clone the instance, and make sure the clone's attributes are
        // identical.
        Type type2 = (Type) type.clone();
        assertEquals(type.getSerialVersion(), type2.getSerialVersion());

        // modify the original and make sure the clone's attributes don't get
        // modified.
        type.setSerialVersion(99);
        type.setLock(false);
        assertTrue(type.getSerialVersion() != type2.getSerialVersion());
        assertTrue(type.getLock() != type2.getLock());
    }

    @Test
    public void testDeepCloneVersion() throws Exception {
        // create the original version
        Version version = new Version();
        version.setAuthor("Author");
        version.setComment("Comment");
        version.setCopyright("Copyright");
        version.setDate("Date");
        version.setSource("Source");
        version.setState("State");
        version.setVersion("Version");

        // clone the original and make sure the clone's attributes are identical
        Version version2 = (Version) version.clone();
        assertEquals(version.getAuthor(), version2.getAuthor());
        assertEquals(version.getComment(), version2.getComment());
        assertEquals(version.getCopyright(), version2.getCopyright());
        assertEquals(version.getDate(), version2.getDate());
        assertEquals(version.getSource(), version2.getSource());
        assertEquals(version.getState(), version2.getState());
        assertEquals(version.getVersion(), version2.getVersion());

        // modify the original and make sure the clone's attributes are not
        // changed
        version.setAuthor("Author1");
        version.setComment("Comment1");
        version.setCopyright("Copyright1");
        version.setDate("Date1");
        version.setSource("Source1");
        version.setState("State1");
        version.setVersion("Version1");
        assertTrue(!version.getAuthor().equals(version2.getAuthor()));
        assertTrue(!version.getComment().equals(version2.getComment()));
        assertTrue(!version.getCopyright().equals(version2.getCopyright()));
        assertTrue(!version.getDate().equals(version2.getDate()));
        assertTrue(!version.getSource().equals(version2.getSource()));
        assertTrue(!version.getState().equals(version2.getState()));
        assertTrue(!version.getVersion().equals(version2.getVersion()));
    }

    @Test
    public void testDeepCloneHistory() throws Exception {
        // create the original history
        History history = new History();
        history.setSerialVersion(21);
        history.setLock(true);
        for (int i = 0; i < 10; i++) {
            Version v = new Version();
            v.setAuthor("author" + i);
            v.setComment("comment" + i);
            v.setCopyright("copyright" + i);
            v.setDate("date" + i);
            v.setSource("source" + i);
            v.setState("state" + i);
            v.setVersion("version" + i);
            history.addVersion(v);
        }

        // clone the original
        History history1 = (History) history.clone();
        assertEquals(history.getSerialVersion(), history1.getSerialVersion());
        assertEquals(history.getLock(), history1.getLock());
        assertEquals(history.getVersionCount(), history1.getVersionCount());
        for (int i = 0; i < 10; i++) {
            assertEquals(history.getVersion(i).getAuthor(), history1.getVersion(i).getAuthor());
            assertEquals(history.getVersion(i).getComment(), history1.getVersion(i).getComment());
            assertEquals(history.getVersion(i).getCopyright(), history1.getVersion(i).getCopyright());
            assertEquals(history.getVersion(i).getDate(), history1.getVersion(i).getDate());
            assertEquals(history.getVersion(i).getSource(), history1.getVersion(i).getSource());
            assertEquals(history.getVersion(i).getState(), history1.getVersion(i).getState());
            assertEquals(history.getVersion(i).getVersion(), history1.getVersion(i).getVersion());
        }
    }

    @Test
    public void testCommandClone() throws Exception {
        LoggingCommand c = new LoggingCommandImpl();
        LoggingArgument a = new LoggingArgumentImpl();
        c.setArgs(a);
        LoggingCommand c1 = (LoggingCommand) ((LoggingCommandImpl)c).clone();
        assertTrue(c.hashCode() != c1.hashCode());
        assertTrue(c.getArgs().hashCode() != c1.getArgs().hashCode());
    }

    /**
     * An issue was raised with regards to the cloning of types (specifically
     * Asset in the insurance model) that used Attributes. The Attributes were
     * not being correctly cloned which meant that each instance of the type
     * ended up sharing a collection of attributes so a change to an attribute
     * on one asset showed up on all the other instances too. This test ensures
     * that this bug is fixed.
     * <ul>
     * <li>Create an instance of a type (com.ail.core.Version).</li>
     * <li>Add two attributes to the instance.</li>
     * <li>Clone the instance.</li>
     * <li>Compare the references in the attribute collection on the original
     * instance with those on the clone. Fail if any are the same.</li>
     * <li>Fail if any exceptions are thrown.</li>
     * </ul>
     */
    @Test
    public void testDeepCloneWithAttributes() throws Exception {
        Version original = new Version();
        original.addAttribute(new Attribute("one", "1", "number,#.##"));

        Version clon = (Version) original.clone();

        assertTrue("Attributes share a reference!!", clon.getAttribute().get(0) != original.getAttribute().get(0));

        clon.getAttribute().get(0).setValue("2");

        assertEquals("1", original.getAttribute().get(0).getValue());
        assertEquals("2", clon.getAttribute().get(0).getValue());
    }

    /**
     * Since JDK 1.5 java supports enumerated types. This test checks that our cloning code in
     * the Type class correctly supports them.
     * @throws Exception
     */
    @Test
    public void testEnumCloning() throws Exception {
        LoggingArgumentImpl limp=new LoggingArgumentImpl();

        limp.setSeverity(Severity.DEBUG);

        LoggingArgumentImpl clon=(LoggingArgumentImpl)limp.clone();

        assertEquals(clon.getSeverity(), limp.getSeverity());
    }

    @Test
    public void testMapCloning() throws Exception {
        Version v;

        TypeWithMap src=new TypeWithMap();

        v=new Version();
        v.setAuthor("ONE");
        src.getMyMap().put("one", v);

        v=new Version();
        v.setAuthor("TWO");
        src.getMyMap().put("two", v);

        v=new Version();
        v.setAuthor("THREE");
        src.getMyMap().put("three", v);

        TypeWithMap cln=(TypeWithMap)src.clone();

        assertTrue(cln!=src);
        assertNotNull(cln.getMyMap().get("one"));
        assertNotNull(cln.getMyMap().get("two"));
        assertNotNull(cln.getMyMap().get("three"));
        assertTrue(cln.getMyMap().get("one")!=src.getMyMap().get("one"));
        assertTrue(cln.getMyMap().get("two")!=src.getMyMap().get("two"));
        assertTrue(cln.getMyMap().get("three")!=src.getMyMap().get("three"));
    }

    @Test
    public void testSetCloning() throws Exception {
        Version v;

        TypeWithSet src=new TypeWithSet();

        v=new Version();
        v.setAuthor("ONE");
        src.getMySet().add(v);

        v=new Version();
        v.setAuthor("TWO");
        src.getMySet().add(v);

        v=new Version();
        v.setAuthor("THREE");
        src.getMySet().add(v);

        TypeWithSet cln=(TypeWithSet)src.clone();

        assertEquals(cln.getMySet().size(), src.getMySet().size());

        for(Version vsrc: src.getMySet()) {
            if (cln.getMySet().contains(vsrc)) {
                fail("clone set contained same element as src");
            }
        }

        assertTrue(cln!=src);
    }

    @Test
    public void verifyThatEnumsAreCloned() throws CloneNotSupportedException {
        Document document=new Document(DocumentType.ATTACHMENT, null, "my document title", "filename", "mime-type", "product-type-id");

        Document clone=(Document) document.clone();

        assertThat(clone.getTitle(), is("my document title"));
        assertThat(clone.getTypeAsEnum(), is(DocumentType.ATTACHMENT));

    }
}

/**
 * Sample Type with a Set to help in testing the core's cloning
 */
class TypeWithMap extends Type {
    private Map<String,Version> myMap=new HashMap<>();

    public Map<String,Version> getMyMap() {
        return myMap;
    }

    public void setMyMap(Map<String,Version> myMap) {
        this.myMap=myMap;
    }
}

/**
 * Sample Type with a Set to help in testing the core's cloning
 */
class TypeWithSet extends Type {
    private Set<Version> mySet=new HashSet<>();

    public Set<Version> getMySet() {
        return mySet;
    }

    public void setMySet(Set<Version> mySet) {
        this.mySet=mySet;
    }
}
