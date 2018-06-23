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
package com.ail.core.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ail.annotation.TypeDefinition;
import com.ail.core.Type;

/**
 * A DataDictionary associates a business name for a system field with the xpath binding that points to that field in the database.
 * This class is populated from an XML file with a root element of dataDictionary declared in the product registry as for example:
 * <pre>
 *      <type name="DataDictionary" builder="CastorXMLBuilder" singleInstance="true" key="com.ail.core.data.DataDictionary">
            <parameter name="Url">product://localhost:8080/AIL/Base/DataDictionary.xml<\/parameter>
        <\/type>
 * </pre>
 * A product can extend the dictionary of a parent product by adding he extends parameter to the above declaration, for example:
 * <pre>
        <type name="DataDictionary" builder="CastorXMLBuilder" singleInstance="true" key="com.ail.core.data.DataDictionary">
            <parameter name="extends">super.DataDictionary<\/parameter>
            <parameter name="Url">~/DataDictionary.xml<\/parameter>
        <\/type>
 * </pre>
 * The file can specify {@link Definition} elements that can be nested in relation to one another to any arbitrary depth, but when
 * Castor loads them into this class via {@link #addDefinition(Definition)} they are added as a flat structure.
 *
 * By default the key name of each entry will be prepended with all of its parents key names separated with <code>/</code> unless the {@link Definition#isNameAbsolute()}
 * is set to true, in which case only the defined name will be used. NB care should be taken with this as all the definitions must be
 * unique, so any name clashes will lead to overwritten entries.
 *
 * The key name of each entry will be also be prepended with all of its parents key names separated with <code>.</code>. This is necessary for elements that
 * will end up being used in repeating page elements.
 *
 * See base-product-loader-hook.war/src/product/AIL/Base/DataDictionary.xml, demo-product-loader-hook.war/src/product/AIL/Demo/EquinePlus/DataDictionary.xml
 * and demo-product-loader-hook.war/src/product/AIL/Demo/MotorPlus/DataDictionary.xml for examples on how to compose the DataDictionary file.
 *
 *
 * See demo-product-loader-hook.war/src/product/AIL/Demo/EquinePlus/PageFlows/QuotationPageFlow.xml ansd
 * demo-product-loader-hook.war/src/product/AIL/Demo/MotorPlus/PageFlows/QuotationPageFlow.xml for examples on how to compose the DataDictionary file.
 *
 */
@TypeDefinition
public class DataDictionary extends Type {

    private Map<String, Definition> definitions = null;

    private static String[] suffixes = {"/value", "/formattedValue", "/object"};

    public DataDictionary() {
        definitions = new HashMap<>();
    }

    /**
     * @return the definitions
     */
    public Map<String, Definition> getDefinitions() {
        return definitions;
    }

    /**
     * @param definitions the definitions to set
     */
    public void setDefinitions(Map<String, Definition> definitions) {
        this.definitions = definitions;
    }

    /**
     * Adds the definition to the dictionary, both as an absolute entry and a relative entry.
     * @param definition the definition to add
     */
    public void addDefinition(Definition definition) {
        this.definitions.put(definition.getId(), definition);

        for (Definition childDefinition : definition.getDefinitions()) {
            Definition childDefinitionB = new Definition(childDefinition.getName(), childDefinition.getDescription(), childDefinition.getBinding());
            childDefinitionB.setDefinitions(childDefinition.getDefinitions());

            String prependName = childDefinition.isNameAbsolute() ? "" : definition.getId() + "/";
            childDefinition.setName(prependName + childDefinition.getId());
            childDefinition.setBinding(definition.getBinding() + "/" + childDefinition.getBinding());
            addDefinition(childDefinition);

            childDefinitionB.setName(definition.getId() + "." + childDefinitionB.getId());
            addDefinition(childDefinitionB);
        }
    }

    /**
     * Get the Definitions in this Dictionary as a Collection
     *
     * @return A List of Definitions
     */
    public Collection<Definition> listDefinitions() {
        return getDefinitions().values();
    }

    /**
     * Populates the definitions map from a List of Definitions. Makes life easier with Castor.
     * @param definitions
     */
    public void setDefinitions(Collection<Definition> definitions) {
        for (Definition definition : definitions) {
            addDefinition(definition);
        }
    }

    /**
     * Return the binding matching the specified key. This must cater for various scenarios, for example the key
     * might contain an xpath function, or end with /value, or /formattedValue, or /object. In these scenarios we
     * need to strip off the extra bits before we do the dictionary lookup then add them back on to the xpath
     * that we found in the dictionary definition.
     *
     * @return binding string, or the value of key if a match cannot be found.
     */
    public String translate(String key) {
        String xpath = key;
        String beforeKey = "";
        String afterKey = "";

        if (key.contains("(")) {
            // must be a function in here, strip it out and put it back in later
            xpath = StringUtils.substringBetween(key, "(", ")");
            if (xpath.contains(",")) {
                xpath = StringUtils.substringBefore(xpath, ",");
            }
            beforeKey = StringUtils.substringBefore(key, xpath);
            afterKey = StringUtils.substringAfter(key, xpath);
        }

        Definition definition = getDefinition(key);
        if (definition != null) {
            return reapplyFunction(definition.getBinding(), beforeKey, afterKey);
        } else {
            for (String suffix : suffixes) {
                if (xpath.endsWith(suffix)) {
                    definition = getDefinition(StringUtils.substringBeforeLast(xpath, suffix));
                    if (definition != null) {
                        return reapplyFunction(definition.getBinding() + suffix, beforeKey, afterKey);
                    }
                }
            }

            // If we've got to here then we're just dealing with raw xpath rather than a dictionary reference so return it as is
            return key;
        }
    }

    private String reapplyFunction(String binding, String beforeKey, String afterKey) {
        return beforeKey + binding + afterKey;
    }

    private Definition getDefinition(String key) {
        return definitions.get(key.toLowerCase());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((definitions == null) ? 0 : definitions.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DataDictionary other = (DataDictionary) obj;
        if (definitions == null) {
            if (other.definitions != null)
                return false;
        } else if (!definitions.equals(other.definitions))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "DataDictionary [definitions=" + definitions + "]";
    }
}