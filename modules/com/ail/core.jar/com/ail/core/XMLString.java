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
package com.ail.core;

import static com.ail.core.Functions.classForName;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPathExpressionException;

import org.apache.xpath.XPathAPI;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This class is designed to do most of the kinds of jobs you
 * are likely to need to do with a string of XML. It doesn't
 * attempt to be a complete replacement for using javax.xml
 * directly, it just takes some of the pain out of the most
 * common usages.
 **/
public class XMLString implements Cloneable, Serializable {
    static final long serialVersionUID = -7687502065734633603L;
    // regex to match character entity reference in formats like: #x011f, or #2101. This assumes
    // that the code has already taken care of checking the '&' which much prefix entity refs.
    private static Pattern charRefEntityPattern=Pattern.compile("^(#[0-9]*)$|^(#[xX]{1}+[0-9a-fA-F]*)$");
    private static transient TransformerFactory transformFactory=null;
    private static transient DocumentBuilderFactory documentBuilderFactory=null;

    private transient Document xmlDocument=null;
    private StringBuffer xmlString=null;

    private Transformer getTransformer(Source source) throws TransformerConfigurationException {
		if (transformFactory==null) {
			transformFactory=TransformerFactory.newInstance();
		}

		if (source==null) {
			return transformFactory.newTransformer();
		}
		else {
			return transformFactory.newTransformer(source);
		}
    }

    private DocumentBuilder getDocumentBuilder() {
		if (documentBuilderFactory==null) {
			documentBuilderFactory=DocumentBuilderFactory.newInstance();
			documentBuilderFactory.setNamespaceAware(true);
		}

		try {
			return documentBuilderFactory.newDocumentBuilder();
		}
		catch(ParserConfigurationException e) {
			throw new XMLConfigurationError(e.toString());
		}
    }

    /**
     * Return the Document object representing this XMLString. The value
     * returned is a copy, changes made to it will not appear in the String
     * and changes to the String will not appear in the Document.
     * @return The Document object.
     * @throws SAXException
     */
    public Document getXmlDocument() throws SAXException {
		if (xmlDocument==null) {
			StringReader in=new StringReader(this.toString());
			try {
				xmlDocument=getDocumentBuilder().parse(new InputSource(in));
			}
			catch(IOException e) {
				// ignore, we're always reading a StringReader so this'll never happen.
			}
		}
		return xmlDocument;
    }

    /**
     * Constructor that builds an XMLString based upon the specified String.
     * @param string String in XML format
     **/
    public XMLString(String string) {
		setXMLString(string);
    }

    /**
     * Constructor that builds an XMLString based the contents of the
     * document pointed to by a URL.
     * @param url URL to read document from.
     * @throws IOException if the URL cannot be read.
     **/
    public XMLString(URL url) throws IOException {
		setXMLString(url);
    }

    /**
     * Constructor that builds an XMLString based the contents of the
     * specified file.
     * @param file The file to read document from.
     * @throws FileNotFoundException if the specified file cannot be found.
     * @throws IOException if the file cannot be read.
     **/
    public XMLString(File file) throws FileNotFoundException, IOException {
		setXMLString(file);
    }

    /**
     * Constructor that builds an XMLString based the document read from
     * the specified input stream.
     * @param inputStream Stream to read document from.
     * @throws IOException if the input stream cannot be read.
     **/
    public XMLString(InputStream inputStream) throws IOException {
		setXMLString(inputStream);
    }

	/**
	 * Replace the current XMLString with the string specified.
	 * @param string String to replace the current string.
	 **/
	public void setXMLString(StringBuffer sb) {
		xmlString=sb;
		xmlDocument=null;
	}

    /**
     * Replace the current XMLString with the string specified.
     * @param string String to replace the current string.
     **/
    public void setXMLString(String string) {
		xmlString=new StringBuffer(string);
		xmlDocument=null;
    }

    /**
     * Replace the current XMLString with the contents of the document
     * loaded for the specified URL.
     * @param url URL to load XMLString from.
     * @throws IOException If the URL cannot be read.
     **/
    public void setXMLString(URL url) throws IOException {
		setXMLString(url.openStream());
    }

    /**
     * Replace the current XMLString with the contents of the
     * specified file.
     * @param file File to read new string from.
     * @throws FileNotFoundException If file cannot be opened.
     * @throws IOException If an error occurs while reading the file.
     **/
    public void setXMLString(File file) throws FileNotFoundException, IOException {
		setXMLString(new FileInputStream(file));
    }

    /**
     * Replace the current XMLString with the contents of the document
     * read from the specified input stream.
     * @param inputStream Stream to read the new document from.
     * @throws IOException If an error occurs while reading from the stream.
     **/
    public void setXMLString(InputStream inputStream) throws IOException {
    	BufferedReader br=new BufferedReader(new InputStreamReader(inputStream));
		StringBuffer sb=new StringBuffer();

		for(String s=br.readLine() ; s!=null ; s=br.readLine()) {
			sb.append(s);
		}

		br.close();

		setXMLString(sb);
    }

    /**
     * Return the current XML document as a string
     * @return The current document.
     **/
    @Override
    public String toString() {
		return xmlString.toString();
    }

    /**
     * Apply the specified XSLT to the contents of this XMLString.
     * @return The result of the transformation.
     * @param source A string representing the XSLT to apply.
     * @throws TransformerException If an error occurs during transformation.
     **/
    public String transform(String source) throws TransformerException {
		Transformer t=getTransformer(new StreamSource(new StringReader(source)));
		StringReader xmlSource=new StringReader(toString());
		StringWriter xmlResult=new StringWriter();
		t.transform(new StreamSource(xmlSource), new StreamResult(xmlResult));

		return xmlResult.toString();
    }

    /**
     * Apply the specified XSLT to the contents of this XMLString.
     * @return The result of the transformation.
     * @param source An XMLString representing the XSLT to apply.
     * @throws TransformerException If an error occurs during transformation.
     **/
    public String transform(XMLString source) throws TransformerException {
		return this.transform(source.toString());
    }

    /**
     * Apply the specified XSLT to the contents of this XMLString, and
     * replace the XMLString with the result of the transformation.
     * @param source A string representing the XSLT to apply.
     * @throws TransformerException If an error occurs during transformation.
     **/
    public void transformInline(String source) throws TransformerException {
		setXMLString(transform(source));
    }

    /**
     * Apply the specified XSLT to the contents of this XMLString, and
     * replace the XMLString with the result of the transformation.
     * @param source An XMLString representing the XSLT to apply.
     * @throws TransformerException If an error occurs during transformation.
     **/
    public void transformInline(XMLString source) throws TransformerException {
		this.transformInline(source.toString());
    }

    /**
     * Apply the specified XPath expression to the current XMLString.
     * @param expr XPath expression
     * @return The result of the query.
     * @throws XPathExpressionException
     * @throws SAXException
     * @throws TransformerException
     **/
    public String eval(String expr) throws XPathExpressionException, SAXException, TransformerException {
        StringWriter sw = new StringWriter();

        Transformer serializer;
        serializer = getTransformer(null);

        serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

        NodeIterator ni = XPathAPI.selectNodeIterator(getXmlDocument(), expr);
        for (Node n = null; (n = ni.nextNode()) != null;) {
            if (n.getNodeType() == Node.ATTRIBUTE_NODE) {
                sw.append(n.getNodeValue());
            } else {
                serializer.transform(new DOMSource(n), new StreamResult(sw));
            }
        }

        return (sw.toString());
    }

    /**
     * Apply the specified xpath expression to the current XMLString.
     * @param xpath XMLString representing the query
     * @return The result of the query.
     * @throws TransformerException
     **/
    public String eval(XMLString xpath) throws XPathExpressionException, SAXException, TransformerException {
		return (this.eval(xpath.toString()));
    }

	/**
	 * Apply the specified xpath expression to the current XMLString. The expression may
	 * include xpath commands (e.g. count()). This method is likely to be slower than
	 * {@see #eval(String) eval(String)} or {@see #eval(XMLString) eval(XMLString)} but
	 * supports commands - which the other methods do not.
	 * @param xpath The query.
	 * @return The result of the query.
	 **/
	public String evalCommand(String xpath) throws SAXException, TransformerException {
		XObject xo=XPathAPI.eval(getXmlDocument(), xpath);
		return(xo.toString());
	}

	/**
	 * Apply the specified xpath expression to the current XMLString. The expression may
	 * include xpath commands (e.g. count()). This method is likely to be slower than
	 * {@see #eval(String) eval(String)} or {@see #eval(XMLString) eval(XMLString)} but
	 * supports commands - which the other methods do not.
	 * @param xpath XMLString representing the query
	 * @return The result of the query.
	 **/
	public String evalCommand(XMLString xpath) throws SAXException, TransformerException {
		return (this.evalCommand(xpath.toString()));
	}


    private String evalToTextLocal(String xpath) throws XPathExpressionException, SAXException, TransformerException {
        if (xpath.indexOf('@')==-1) {
            return eval(xpath+"/text()");
        }
        else {
            return eval(xpath);
        }
    }

    /**
     * Evaluate an xpath expression to a text value. If the xpath specified points to an
     * attribute this method will return the value of that attribute, if it points to an
     * element then the element is assumed to be a text element an its value is returned.
     * @see #evalToText(com.ail.core.XMLString)
     * @param xpath The xpath expression
     * @return The value of the text element or attribute.
     * @throws TransformerException
     */
    public String evalToText(String xpath) throws XPathExpressionException, SAXException, TransformerException {
        // If there's an empty array in the xpath, we have to iterate over it
        int brIdx=xpath.indexOf("[]");

        if (brIdx!=-1) {
            StringBuffer ret=new StringBuffer();
            String prefix=xpath.substring(0, brIdx+1);
            String postfix=xpath.substring(brIdx+1);
            String part=null;

            for(int i=1 ; (part=evalToTextLocal(prefix+i+postfix)).length()!=0 ; i++) {
                if (i==1) {
                    ret.append(part);
                }
                else {
                    ret.append('|').append(part);
                }
            }

            // the above loop will leave trailing '|' in 'ret', chop it off and return
            return ret.toString();
        }
        else {
            return evalToTextLocal(xpath);
        }
    }

    /**
     * Evaluate an xpath expression to a text value. In addition to processing normal xpath
     * expressions, this method has handles two special cases to simplify xpath use.<p>
     * Firstly, if the xpath specified points to an attribute this method will return the
     * value of that attribute, if it points to an element then the element is assumed to
     * be a text element an its value is returned.<p>
     * For example, an xpath expression of <code>/policy/policy-holder/@forname</code> this
     * method would return the value of the forname attribute (as eval() would), but given
     * the xpath <code>/policy/policy-number</code> it would actually evaulate
     * <code>/policy/policy-number/text()</code> returning the text of the element.<p>
     * Secondly, this method has special handling for multi-node matches, consider the following xml
     * source doc:<p><code>
     * &lt;doc&gt;<br>
     * &nbsp;&nbsp;&lt;person forename="fred" surname="clucknasty"/&gt;<br>
     * &nbsp;&nbsp;&lt;person forename="wilma" surname="clucknasty"/&gt;<br>
     * &lt;/doc&gt;</code><p>
     * An xpath expression to get both of the forename nodes like this:<p><code>
     * /doc/person/@forename</code><p>
     * would return 'fredwilma' - which isn't especially useful. This method offers
     * special handling, which allows the following xpath expression:<p><code>
     * /doc/person[]/@forname</code><p>
     * to return the string 'fred|wilma'.
     * @param xpath The xpath expression
     * @return The value of the text element or attribute.
     * @throws TransformerException
     */
    public String evalToText(XMLString xpath) throws XPathExpressionException, SAXException, TransformerException {
        return evalToText(xpath.toString());
    }

    /**
     * Apply the specified xpath expression to the current XMLString, and
     * replace the current contents of the XMLString with the result.
     * @param xpath A string representing the query to apply.
     * @throws TransformerException
     **/
    public void evalInline(String xpath) throws XPathExpressionException, SAXException, TransformerException {
		setXMLString(this.eval(xpath));
    }

    /**
     * Apply the specified xpath expression to the current XMLString, and
     * replace the current contents of the XMLString with the result.
     * @param xpath An XMLString representing the query to apply.
     * @throws TransformerException
     **/
    public void evalInline(XMLString xpath) throws XPathExpressionException, SAXException, TransformerException {
		this.evalInline(xpath.toString());
    }

	public boolean equals(String xml) {
		return xml.equals(this.toString());
	}

    /**
     * Fetch the root elements xsi:type value if there is one, or
     * null if none is present. Note: This method assumes that the
     * namespace being used is "xsi".
     * @return The value of xsi:type, without the java: prefix.
     */
    public String determineType() {
        // It might be better to use xpath here, but it would be slower too.
        String xml=xmlString.toString();

        StringTokenizer tk=new StringTokenizer(xml, "<>\n\r");

        // no < or > chars at all? an odd piece of xml...
        if (!tk.hasMoreTokens()) {
            return null;
        }

        String t=tk.nextToken();

        // skip the xml header if there is one, and any white space.
        while(t.charAt(0)=='?' || t.charAt(1)=='!' || t.trim().length()==0) {
            t=tk.nextToken();
        }

        int startIdx=t.indexOf("java:");
        if (startIdx==-1) {
            return null;
        }

        // +15 to skip the 'xsi:type="java:' bit
        startIdx+=5;

        int endIndex=t.indexOf(t.charAt(startIdx-6), startIdx);
        if (endIndex==-1) {
            return null;
        }

        return t.substring(startIdx, endIndex);
    }

    /**
     * Determine the type represented by the xmlString. This assumes that
     * the type can be determined by an xsi:type attribute on the root
     * element.
     * @return the Class represented by the string, or null.
     */
    public Class<?> getType() throws ClassNotFoundException {
        String type = determineType();

        if (type==null) {
            throw new ClassNotFoundException("Class not found, \"type\" was not present on root element.");
        }
        else {
            return classForName(type);
        }
    }

	public boolean equals(XMLString xml) {
		return equals(xml.toString());
	}

	/**
	 * Clone this XMLString object.
	 * @return A clone of this object, or null if the cloning process fails.
	 **/
	@Override
    public Object clone() {
		try {
			return (XMLString)super.clone();
		}
		catch(Exception e) {
			// ignore, we'll return null
		}
		return null;
	}

	/**
     * Similar to {@link #toString() toString()} but with entity references expanded. This simply means
     * that any characters in the XMLString which have corresponding entity references will be replaced
     * with them; so if the string contains "&lt;name&gt;Johnson's&lt;name&gt;" this method will
     * return "&lt;name&gt;Johnson&amp;apos;s&lt;name&gt;"
     * @param excludeBraces If false "&lt;" & "&gt;" are replaced with "&amp;lt;" and "&amp;gt;"; otherwise they are left as is.
     * @return The value of this XMLString with references expanded.
	 */
    public String toStringWithEntityReferences(boolean excludeBraces) {
        StringBuilder sb=new StringBuilder();
        String s;
        char c;
        int scolon;
        boolean inAngles=false, inQuotes=false, inApos=false;

        for(int i=0 ; i<xmlString.length() ; i++) {
            c=xmlString.charAt(i);
            switch(c) {
            case '&':
                scolon=xmlString.indexOf(";", i+1);
                if (scolon>0) {
                    s=xmlString.substring(i+1, scolon);
                    if ("amp".equals(s)
                    ||  "quot".equals(s)
                    ||  "apos".equals(s)
                    ||  "lt".equals(s)
                    ||  "gt".equals(s)
                    ||  (s.charAt(0)=='#' && charRefEntityPattern.matcher(s).find())) {
                        sb.append(xmlString.substring(i, scolon+1));
                        i=scolon;
                    }
                    else {
                        sb.append("&amp;");
                    }
                }
                else {
                    sb.append("&amp;");
                }
                break;
            case '<':
                if (!excludeBraces) {
                    sb.append("&lt;");
                }
                else {
                    sb.append(c);
                    inAngles=true;
                }
                break;
            case '>':
                if (!excludeBraces) {
                    sb.append("&gt;");
                }
                else {
                    sb.append(c);
                    inAngles=false;
                }
                break;
            case '\'':
                if (!excludeBraces | !inAngles | (inAngles & inQuotes)) {
                    sb.append("&apos;");
                }
                else {
                    if (inAngles) {
                        sb.append(c);
                        inApos=!inApos;
                    }
                }
                break;
            case '"':
                if (!excludeBraces | !inAngles | (inAngles & inApos)) {
                    sb.append("&quot;");
                }
                else {
                    if (inAngles) {
                        sb.append(c);
                        inQuotes=!inQuotes;
                    }
                }
                break;
            default:
                if (c>128) {
                    sb.append("&#"+(int)c+";");
                }
                else {
                    sb.append(c);
                }
            }
        }

        return sb.toString();
    }

    /**
     * This method does the reverse of {@link #toStringWithEntityReferences(boolean)}.
     * @return Content of the string with entity references replaced with their literal
     * values.
     */
    public String toStringWithoutEntityReferences() {
        StringBuilder sb=new StringBuilder();
        String s;
        char c;
        int scolon;

        for(int i=0 ; i<xmlString.length() ; i++) {
            c=xmlString.charAt(i);
            switch(c) {
            case '&':
                scolon=xmlString.indexOf(";", i+1);
                if (scolon>0) {
                    s=xmlString.substring(i+1, scolon);
                    if ("amp".equals(s)) {
                        sb.append('&');
                        i+=4;
                    }
                    else if ("apos".equals(s)) {
                        sb.append('\'');
                        i+=5;
                    }
                    else if ("quot".equals(s)) {
                        sb.append('"');
                        i+=5;
                    }
                    else if ("lt".equals(s)) {
                        sb.append('<');
                        i+=3;
                    }
                    else if ("gt".equals(s)) {
                        sb.append('>');
                        i+=3;
                    }
                    else if (s.charAt(0)=='#' && charRefEntityPattern.matcher(s).find()) {
                        if (s.charAt(1)=='x' || s.charAt(1)=='X') {
                            sb.append((char)Integer.parseInt(s.substring(2), 16));
                        }
                        else {
                            sb.append((char)Integer.parseInt(s.substring(1)));
                        }
                        i=scolon;
                    }
                    else {
                        sb.append('&');
                    }
                }
                else {
                    sb.append("&");
                }
                break;
            default:
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
