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

import java.util.Date;

import com.ail.annotation.TypeDefinition;

/**
 * A named piece of information used during rule processing. Typically you assert objects into the rule engine's
 * working memory and these objects need have nothing in common. They don't have to implement a given interface, or
 * sub class a specific class, the engine simply uses reflection to query them. Wrapping objects in an instance of
 * Fact has some advantages - and some disadvantages. Chief among the advantages are that Facts can be given
 * arbitrary (and duplicate) names this allows us to group objects in ways that the the raw objects wouldn't lend
 * themselves to. Also, this class offers some helper methods which make navigating and converting elements of our
 * type model easier. The major disadvantage is that once you've wrapped an object (e.g. an Asset) in a fact, you
 * can't then use native rule Condition queries on it as easily.
 */
@TypeDefinition
public class Fact extends Type {
    private static final long serialVersionUID = -2842993394541843117L;
    private String name;
    private Object value;

    /**
     * Create a new Fact
     * @param name Fact's name
     * @param value Object describing the fact (the object to be wrapped).
     */
    public Fact(String name, Object value) {
        super();
        this.value = value;
        this.name = name;
    }

    /**
     * Get the name of this Fact. The name is set when the Fact is create - it cannot be modified. The name
     * is simply an arbitrary description of the thing being wrapped.
     * @return
     */
    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    /**
     * Return the value of this Fact as a String. The value returned is total dependent on the type of object
     * that is wrapped.
     * @return String representation of the wrapped Object.
     */
    public String getStringValue() {
        if (value instanceof String) {
            return (String)value;
        }
        if (value instanceof Attribute) {
            return ((Attribute)value).getValue();
        }
        else if (value instanceof Number) {
            return ((Number)value).toString();
        }
        else {
            return "";
        }
    }

    /**
     * Return the value of this Fact as an integer. If possible interpret the wrapped object's integer value.
     * @return Value of the wrapped object as an integer
     * @deprecated user toInteger instead
     */
    @Deprecated
    public int getIntegerValue() {
        return (int)getNumericValue();
    }

    /**
     * Return the value of this Fact as an integer. If possible interpret the wrapped object's integer value.
     * @return Value of the wrapped object as an integer
     */
    public int toInteger() {
        return (int)getNumericValue();
    }

    @Override
    public String toString() {
        if (value instanceof Attribute) {
            Attribute attributeValue=(Attribute)value;
            return attributeValue.getFormattedValue();
        }
        else {
            return value.toString();
        }
    }

    /**
     * Return the value of this Fact as an double. If possible interpret the wrapped object's double value.
     * @return Value of the wrapped object as an double
     * @deprecated user toDouble instead
     */
    @Deprecated
    public double getNumericValue() {
        return toDouble();
    }

    /**
     * Return the value of this Fact as an double. If possible interpret the wrapped object's double value.
     * @return Value of the wrapped object as an double
     */
    public double toDouble() {
        if (value instanceof Attribute) {
            Attribute attributeValue=(Attribute)value;
            if (attributeValue.isNumberType() || attributeValue.isCurrencyType()) {
                Number n=(Number)attributeValue.getObject();
                return n.doubleValue();
            }
            else if (attributeValue.isStringType()) {
            	try {
            		return Double.parseDouble(attributeValue.getValue());
            	}
            	catch(NumberFormatException e) {
                    throw new IllegalArgumentException("Attribute: "+attributeValue+" ("+name+") cannot be converted to a number.");
            	}
            }
            else {
                throw new IllegalArgumentException("Attribute: "+attributeValue+" ("+name+") cannot be converted to a number.");
            }
        }
        else if (value instanceof Number) {
            return ((Number)value).doubleValue();
        }
        else if (value instanceof String) {
        	try {
        		return Double.parseDouble((String)value);
        	}
        	catch(Throwable e) {
        		// ignore, let the fall through handle it
        	}
        }

        throw new IllegalArgumentException("Fact ("+name+") cannot be converted to a number");
    }

    /**
     * Evaluate an xpath expression on the fact's value and return the result as an int.
     * @param xpath Xpath to evaluate
     * @return result of evaluation, or zero if evaluation fails
     */
    public int xpathInt(String xpath) {
        String error;

        try {
            Object o=((Type)value).xpathGet(xpath);

            if (o instanceof Number) {
                return ((Number)o).intValue();
            }
            else if (o instanceof Attribute) {
                Attribute attr=(Attribute)o;
                if (attr.isNumberType() || attr.isCurrencyType()) {
                    return ((Number)attr.getObject()).intValue();
                }
            }

            error="xpathInt: xpath '"+xpath+"' did not evaluate to a integer or an Attribute of type number or currency. Dafault of 0 returned.";
        }
        catch(TypeXPathException e) {
            error="xpathInt: evaluation of '"+xpath+" failed to return a result, default of 0 returned.";
        }

        CoreProxy cp=new CoreProxy();
        cp.logWarning(error);

        return 0;
    }

    /**
     * Evaluate an xpath expression on the fact's value and return the result as a String. If the xpath resolves to
     * an Attribute then that attributes value (result of calling getValue()) is returned.
     * @param xpath Xpath to evaluate
     * @return result of evaluation, or an empty string ("") if evaluation fails
     */
    public String xpathString(String xpath) {
        String error="";

        try {
            Object o=((Type)value).xpathGet(xpath);

            if (o instanceof String) {
                return (String)o;
            }
            else if (o instanceof Attribute) {
                Attribute attr=(Attribute)o;
                return attr.getValue();
            }
            else {
                error="xpathString: xpath '"+xpath+"' did not evaluate to a string or an Attribute. Empty string returned";
            }
        }
        catch(TypeXPathException e) {
            error="xpathString: evaluation of '"+xpath+" failed to return a result. Empty string returned.";
        }

        CoreProxy cp=new CoreProxy();
        cp.logWarning(error);

        return "";
    }

    /**
     * Evaluate an xpath expression on the fact's value and return the result as a Date. If the xpath resolves to
     * an attribute and that attribute is of type date, then it's date is returned.
     * @param xpath Xpath to evaluate
     * @return result of evaluation, or today's date if evaluation fails
     */
    public Date xpathDate(String xpath) {
        String error="";

        try {
            Object o=((Type)value).xpathGet(xpath);

            if (o instanceof Date) {
                return (Date)o;
            }
            else if (o instanceof Attribute) {
                Attribute attr=(Attribute)o;
                if (attr.isDateType()) {
                    return (Date)attr.getObject();
                }
            }

            error="xpathDate: xpath '"+xpath+"' did not evaluate to a date or a date Attribute. Dafault of today's date returned.";
        }
        catch(TypeXPathException e) {
            error="xpathDate: evaluation of '"+xpath+" failed to return a result, default of today's date returned.";
        }

        CoreProxy cp=new CoreProxy();
        cp.logWarning(error);

        return new Date();
    }
}
