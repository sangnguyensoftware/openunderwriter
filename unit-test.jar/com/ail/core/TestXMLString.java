/* Copyright Applied Industrial Logic 2003. All Rights Reserved */
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
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.Locale;

import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Tests for the XMLString class. These tests use the following example xml/xsl
 * strings:
 * <p>
 * <table cellpadding="2" cellspacing="2" border="1" width="100%"> <tbody>
 * <tr>
 * <td valign="Top">xml1</td>
 * <td valign="Top"><code>
 *          &lt;doc&gt;<br>
 *			&nbsp;&nbsp;&lt;person forename='fred' surname='clucknasty'/&gt;<br>
 *			&nbsp;&nbsp;&lt;person forename='wilma' surname='clucknasty'/&gt;<br>
 *			&lt;/doc&gt;
 *          </code></td>
 * </tr>
 * <tr>
 * <td valign="Top">xml2</td>
 * <td valign="Top"><code>
 *          &lt;doc&gt;Hello&lt;/doc&gt;
 *          </code></td>
 * </tr>
 * <tr>
 * <td valign="Top">xsl1</td>
 * <td valign="Top"><code>
 *          &lt;?xml version="1.0"?&gt;<br>
 *          &lt;xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"&gt;<br>
 *          &nbsp;&nbsp&lt;xsl:template match="doc"&gt;<br>
 *          &nbsp;&nbsp&nbsp;&nbsp&lt;out&gt;&lt;xsl:value-of select="."/&gt;&lt;/out&gt;<br>
 *          &nbsp;&nbsp&lt;/xsl:template&gt;<br>
 *          &lt;/xsl:stylesheet&gt;<br>
 *          </code></td>
 * </tr>
 * </tbody> </table>
 *
 * @version $Revision: 1.6 $
 */
public class TestXMLString {
    public static XMLString loadedXML = null;
    public static String eol = System.getProperty("line.separator");
    private XMLString xsl1;
    private XMLString xml1;
    private XMLString xml2;
    private Locale savedLocale;

    @Before
    public void setUpLocale() throws Exception {
        savedLocale=ThreadLocale.getThreadLocale();
        ThreadLocale.setThreadLocale(UK);
    }

    @After
    public void teadDownLocale() throws Exception {
        ThreadLocale.setThreadLocale(savedLocale);
    }

    @Before
    public void setUp() {
        xsl1 = new XMLString("<?xml version=\"1.0\"?>"
                + "<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" version=\"1.0\">"
                + "  <xsl:template match=\"doc\">" + "    <out><xsl:value-of select=\".\"/></out>" + "  </xsl:template>"
                + "</xsl:stylesheet>");
        xml1 = new XMLString("<doc>" + "  <person forename='fred' surname='clucknasty'/>"
                + "  <person forename='wilma' surname='clucknasty'/>" + "</doc>");
        xml2 = new XMLString("<doc>Hello</doc>");
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 1) {
            loadedXML = new XMLString(new File(args[0]));
        }
    }

    /**
     * Test that XMLString's eval method correctly handles an xpath.
     * <ol>
     * <li>Apply the xpath expression "doc/person[1]/@forename" to xml1</li>
     * <li>Fail if any exceptions are thrown</li>
     * <li>Fail if the result returned is not "fred"</li>
     * </ol>
     */
    @Test
    public void testXpathEval() throws Exception {
        String res = xml1.eval("doc/person[1]/@forename");
        assertEquals("fred", res);
    }

    /**
     * Test that XMLString's inline eval method correctly handles an xpath.
     * <ol>
     * <li>Apply the xpath expression "doc/person[1]/@forename" to a copy of
     * xml1</li>
     * <li>Fail if any exceptions are thrown</li>
     * <li>Fail if the copy does not equal "fred"</li>
     * </ol>
     */
    @Test
    public void testXpathEvalInline() throws Exception {
        XMLString x = (XMLString) xml1.clone();
        x.evalInline("doc/person[2]/@forename");
        assertEquals(x.toString(), "wilma");
    }

    /**
     * Test that XMLString's transform method works.
     * <ol>
     * <li>Apply xsl1 to a copy of xml2</li>
     * <li>Fail if any exceptions are thrown</li>
     * <li>Fail if the return string does not equal
     * "&lt;out&gt;Hello&lt;/out&gt;" (with an XML header)</li>
     * </ol>
     */
    @Test
    public void testXSLT() throws Exception {
        XMLString xml = (XMLString) xml2.clone();
        String res = xml.transform(xsl1);
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?><out>Hello</out>", res);
    }

    /**
     * Test that XMLString's inline transform method works.
     * <ol>
     * <li>Apply xsl1 to a copy of xml2</li>
     * <li>Fail if any exceptions are thrown</li>
     * <li>Fail if the copy does not equal "&lt;out&gt;Hello&lt;/out&gt;" (with
     * an XML header)</li>
     * </ol>
     */
    @Test
    public void testXSLTInline() throws Exception {
        XMLString xml = (XMLString) xml2.clone();
        xml.transformInline(xsl1);
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?><out>Hello</out>", xml.toString());
    }

    /**
     * Test an xpath eval that matches multiple nodes.
     */
    @Test
    public void testMultiNodeXPathMatch() throws Exception {
        XMLString xml = (XMLString) xml1.clone();
        String ret = xml.evalToText("/doc/person[]/@forename");
        assertEquals("fred|wilma", ret);
    }

    /**
     * Test an xpath query that causes problems in a server side environment.
     * Note: this test depends on an external xml file - it will not run unless
     * the name of the file is passed as an argument to main() in this class.
     *
     * @throws Exception
     */
    @Test
    public void testOddSectionError() throws Exception {
        if (loadedXML != null) {
            assertEquals("TermLife", loadedXML.evalToText("/demoPolicy/section/sectionTypeId"));
        }
    }

    /**
     * Test that the stringToSafeXML correctly converts string chars that would
     * upset XML into entity chars that won't
     * <p>
     */
    @Test
    public void testStringToSafeXml() {
        // make sure normal string are left alone
        assertEquals("hello 1 2 3 4", new XMLString("hello 1 2 3 4").toStringWithEntityReferences(false));

        // any entity refs already in the string should be left alone.
        assertEquals("&apos;&quot;&amp;&gt;&lt;", new XMLString("&apos;&quot;&amp;&gt;&lt;").toStringWithEntityReferences(false));

        // any entity refs already in the string should be left alone (2).
        assertEquals("&apos;gogo;A&quot;B&amp;C&gt;&amp;helloD&lt;", new XMLString("&apos;gogo;A&quot;B&amp;C&gt;&helloD&lt;").toStringWithEntityReferences(false));

        // make sure empty string are handled ok
        assertEquals("", new XMLString("").toStringWithEntityReferences(false));

        // make sure Strings with unsafe chars in various positions are handled ok
        assertEquals("&quot;1234&quot;", new XMLString("\"1234\"").toStringWithEntityReferences(false));
        assertEquals("<123>", new XMLString("<123>").toStringWithEntityReferences(true));
        assertEquals("<hell in='a' toople=\"up &amp; down\">Here &amp; there</hell>", new XMLString("<hell in='a' toople=\"up & down\">Here & there</hell>").toStringWithEntityReferences(true));
        assertEquals("<hell in='a' toople=\"up &apos; down\">Here &apos; there</hell>", new XMLString("<hell in='a' toople=\"up &apos; down\">Here &apos; there</hell>").toStringWithEntityReferences(true));
        assertEquals("<hell in='a' toople=\"up &apos; down\">Here &apos; there</hell>", new XMLString("<hell in='a' toople=\"up ' down\">Here &apos; there</hell>").toStringWithEntityReferences(true));
        assertEquals("&lt;123&gt;", new XMLString("<123>").toStringWithEntityReferences(false));
        assertEquals("A&lt;B&gt;C&quot;&quot;D", new XMLString("A<B>C\"\"D").toStringWithEntityReferences(false));
        assertEquals("&apos;&quot;&amp;&amp;&quot;&apos;", new XMLString("'\"&&amp;&quot;&apos;").toStringWithEntityReferences(true));

        // test character references
        assertEquals("&#xffa;&#163;&#0221;", new XMLString("&#xffa;\u00A3&#0221;").toStringWithEntityReferences(false));
    }

    /**
     * Test that the stringToSafeXML correctly converts string chars that would
     * upset XML into entity chars that won't
     * <p>
     */
    @Test
    public void testSafeXmlToString() {
        // make sure normal string as left alone
        assertEquals("hello 1 2 3 4", new XMLString("hello 1 2 3 4").toStringWithoutEntityReferences());

        // make sure empty string are handled ok
        assertEquals("", new XMLString("").toStringWithoutEntityReferences());

        // make sure Strings with unsafe chars in various positions are handled
        // ok
        assertEquals("\"1234\"", new XMLString("&quot;1234&quot;").toStringWithoutEntityReferences());
        assertEquals("<123>", new XMLString("&lt;123&gt;").toStringWithoutEntityReferences());
        assertEquals("A<B>C\"\"D", new XMLString("A&lt;B&gt;C&quot;&quot;D").toStringWithoutEntityReferences());

        // test character references
        assertEquals("A"+"\u00A3"+"B", new XMLString("A&#163;B").toStringWithoutEntityReferences());
    }

    /**
     * Test that an XMLString correctly returns the xsi:type of the XML even if
     * there are spaces before the root tag.
     */
    @Test
    public void testXsiTypeDetectionWithSpace() {
        XMLString xml;

        // With header, with space
        xml = new XMLString(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        + "  <quoteSection xsi:type=\"java:com.ail.insurance.policy.Policy\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");
        assertThat(xml.determineType(), is("com.ail.insurance.policy.Policy"));

        // Without header, with space
        xml = new XMLString(
                "  <quoteSection xsi:type=\"java:com.ail.insurance.policy.Policy\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");
        assertThat(xml.determineType(), is("com.ail.insurance.policy.Policy"));

        // With header, without space
        xml = new XMLString(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                        + "<quoteSection xsi:type=\"java:com.ail.insurance.policy.Policy\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");
        assertThat(xml.determineType(), is("com.ail.insurance.policy.Policy"));

        // Without header, without space
        xml = new XMLString(
                "<quoteSection xsi:type=\"java:com.ail.insurance.policy.Policy\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");
        assertThat(xml.determineType(), is("com.ail.insurance.policy.Policy"));
    }

    @Test
    public void testTypeDetectionWithBothSingleAndDoubeQuotes() {
        XMLString xml;

        // With single quotes
        xml = new XMLString(
                "<quoteSection xsi:type=\'java:com.ail.insurance.policy.Policy\' xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");
        assertThat(xml.determineType(), is("com.ail.insurance.policy.Policy"));

        // With double quotes
        xml = new XMLString(
                "<quoteSection xsi:type=\"java:com.ail.insurance.policy.Policy\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");
        assertThat(xml.determineType(), is("com.ail.insurance.policy.Policy"));
    }

    @Test
    public void testTypeDetectionWithoutXsiNamespace() {
        XMLString xml;

        xml = new XMLString(
                "<quoteSection type=\"java:com.ail.insurance.policy.Policy\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");
        assertThat(xml.determineType(), is("com.ail.insurance.policy.Policy"));
    }

    @Test
    public void testConfigExtractXpath() throws XPathExpressionException, SAXException, TransformerException {
        XMLString xml=new XMLString("<configuration><builders><builder name='test' class='com.ail.core.Test'/></builders></configuration>");
        assertEquals("<builder class=\"com.ail.core.Test\" name=\"test\"/>", xml.eval("/configuration/builders/*").toString());
    }
}

