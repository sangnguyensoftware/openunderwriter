/* Copyright Applied Industrial Logic Limited 2006. All rights Reserved */
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.ail.annotation.TypeDefinition;

/**
 * Variables holds a simple list of name value pairs and offers a number of
 * comparison methods to act on values in the list. This class is designed to
 * be used from within a Drools rule set as a working-memory version of the
 * application data HashMap which Drools offers.
 */
@TypeDefinition
public class Variables extends Type {
    private static final long serialVersionUID = -5362666512306944366L;

    private boolean enableDebug=false;
    private CoreProxy core=new CoreProxy();
    private HashMap<String,Object> variable=new HashMap<String,Object>();

    /**
     * Add a new variable to the list
     * @param name Variable's name
     * @param o Variable's value
     */
    public void add(String name, Object o) {
        if (enableDebug) {
            core.logInfo("variable created: "+name+" value:"+o);
        }
        variable.put(name, o);
    }
    
    /**
     * Convert the contents of an Iterator into a variable.
     * @param name Variable's name
     * @param it Iterator to read object from
     * @return Collection containing object
     */
    public void add(String name, Iterator<?> it) {
        ArrayList<Object> ret=new ArrayList<Object>();
        
        while (it.hasNext()) {
            ret.add(it.next());
        }

        add(name, ret);
    }

    public Object get(String name) {
        if (!variable.containsKey(name)) {
            core.logWarning("Reference to undefined variable: '"+name+"', empty String returned");
            return "";
        }

        return variable.get(name);
    }

    /**
     * Compare the value of the variable named 'name' with the object 'o'. 
     * @param name Variable to compare
     * @param o Object to compare with
     * @return true if the variable's value '.equals()' 'o'.
     */
    public boolean equals(String name, Object o) {
        return get(name).equals(o);
    }

    /**
     * Return true if the value of variable 'name' is less than 'min'.
     * @param name Name of variable to be compared
     * @param min Min value
     * @return result of comparison
     */
    public boolean lessThan(String name, double min) {
        Number n=(Number)get(name);
        return n.doubleValue() < min;
    }
    
    /**
     * Return true if the value of variable 'name' is greater than 'max'.
     * @param name Name of variable to be compared
     * @param max Max value
     * @return result of comparison
     */
    public boolean greaterThan(String name, double max) {
        Number n=(Number)get(name);
        return n.doubleValue() > max;
    }
    
    /**
     * Compare the value of the named variable with a String, return
     * true if they are the same. The assumption here is that 's' is  
     * a value from a spreadsheet field. As such, it may well have
     * white space appended to it which isn't visible when editing
     * in Excel/OpenOffice. We'll trim that space off before
     * comparing.
     * @param name
     * @param s
     * @return True if 'name' and 's' are equal ignoring trailing white space on 's'.
     */
    public boolean equals(String name, String s) {
        String v=(String)get(name);
        return v.equals(s.trim());
    }

    /**
     * Compare the variable named 'name' with the value specified. Note: the variable named
     * is assumed to be of type Integer.
     * @param name The name of a variable
     * @param value The value to compare with.
     * @return true if variable 'name' == value, false otherwise.
     */
    public boolean equals(String name, int value) {
        Integer i=(Integer)get(name);
        return i==value;
    }

    /**
     * Determine whether the value of variable 'name' falls between min and max inclusive.
     * values. The variable is assumed to be an Integer.
     * @param name Variable to test
     * @param min Minimum value
     * @param max Maximum value
     * @return true if variable 'name' is between min and max, false otherwise.
     */
    public boolean between(String name, int min, int max) {
        Integer i=(Integer)get(name);
        return i>=min && i<=max;
    }

    /**
     * Compare the variable named 'name' with the value specified. Note: the variable named
     * is assumed to be of type Double.
     * @param name The name of a variable
     * @param value The value to compare with.
     * @return true if variable 'name' == value, false otherwise.
     */
    public boolean equals(String name, double value) {
        Double d=(Double)get(name);
        return d==value;
    }

    /**
     * Determine whether the value of variable 'name' falls between min and max inclusive.
     * values. The variable is assumed to be a Double.
     * @param name Variable to test
     * @param min Minimum value
     * @param max Maximum value
     * @return true if variable 'name' is between min and max, false otherwise.
     */
    public boolean between(String name, double min, double max) {
        Double d=(Double)get(name);
        return d>=min && d<=max;
    }

    /**
     * Determine if the iterable variable 'name' contains the object 'o'. The variable
     * is assumed to be iterable (i.e. implements {@link java.lang.Iterable}).
     * @param name Name of the variable to search
     * @param o Object to search for
     * @return true if 'o' is found in the variable 'name', false otherwise.
     */
    public boolean contains(String name, Object o) {
        Iterable<?> it=(Iterable<?>)get(name);
        
        for(Object c: it) {
            if (c.equals(o)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Determine if the iterable variable 'name' contains only objects that equal
     * 'o'. The variable is assumed to be iterable (i.e. implements {@link java.lang.Iterable}).
     * @param name Name of the variable to search
     * @param o Object to search for
     * @return true if 'o' is found in the variable 'name', false otherwise.
     */
    public boolean containsOnly(String name, Object o) {
        Iterable<?> it=(Iterable<?>)get(name);
        
        for(Object c: it) {
            if (!c.equals(o)) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Determine if the value of variable 'name' matches a specified
     * {@link java.util.regex.Pattern regular expression}. 
     * @param name Name of the variable to test
     * @param regex Regular expression
     * @return True if variable's value matches, false otherwise.
     */
    public boolean matches(String name, String regex) {
        String v=(String)get(name);
        return v.matches(regex);
    }

    /**
     * Determine if debugging is turned on or off
     * @return true if debugging is on, false otherwise.
     */
    public boolean isEnableDebug() {
        return enableDebug;
    }

    /**
     * Turn debug messages on or off. If turned on, a message is output every time
     * a new variable is added including details of its name and value.
     * @param enableDebug true to turn debug messages on, false otherwise.
     */
    public void setEnableDebug(boolean enableDebug) {
        this.enableDebug = enableDebug;
    }
}
