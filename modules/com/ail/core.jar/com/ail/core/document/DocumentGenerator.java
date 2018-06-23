/* Copyright Applied Industrial Logic Limited 2007. All rights Reserved */
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
package com.ail.core.document;

import com.ail.core.Type;


/**
 * Define the interfaces that the Core must implement in order to expose the document services.
 */
public interface DocumentGenerator {
    
    /**
     * Render a document and return it as a byte array.
     * @param productName The name of the product which "owns" the document definition
     * @param documentDefinitionName The name of the document definition to generate the document from.
     * @param model The dynamic data to be used in the document.
     * @return The rendered document
     */
    byte[] generateDocument(String productName, String documentDefinitionName, Type model);
}
