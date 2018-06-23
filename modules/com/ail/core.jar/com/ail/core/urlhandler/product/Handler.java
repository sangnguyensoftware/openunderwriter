/* Copyright Applied Industrial Logic Limited 2008. All rights reserved. */
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

package com.ail.core.urlhandler.product;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import com.ail.core.CoreProxy;
import com.ail.core.product.ProductUrlToConnectionService.ProductUrlToConnectionCommand;

/**
 * The handler deals with URLs of the form: "product://".
 * <p>
 * This handler is sensitive to the structure of the product namespace hierarchy
 * and will look for the content being referenced in the product namespace that
 * the URL implies, and in all of that namespace's parents.
 * </p>
 * <p>
 * A product URL contains within it the namespace of the product owning the
 * content. For example:
 * product://localhost:8080/AIL/Demo/LifePlus/Rules/SomeRulesResource.xml
 * addresses content in the AIL.Demo.LifePlus product namespace. Where a
 * namespace has a parent namespace, the content will be searched for using the
 * exact URL supplied, but also in that namespace's ancestors.
 * </p>
 * <p>
 * In the example above, the product has the parent AIL.Base. Therefore if the
 * content "Rules/SomeRulesResource.xml" is not found in AIL.Demo.LifePlus the
 * handler will search for it in AIL.Base - giving it the effective URL
 * product://localhost:8080/AIL/Base/Rules/SomeRuleResource.xml.
 * </p>
 */
public class Handler extends URLStreamHandler {

    protected URLConnection openConnection(URL productURL) throws IOException {
        ProductUrlToConnectionCommand productUrlToConnectionCommand;

        productUrlToConnectionCommand=new CoreProxy().newCommand(ProductUrlToConnectionCommand.class);
        
        productUrlToConnectionCommand.setProductUrlArg(productURL);

        try {
            productUrlToConnectionCommand.invoke();
        } catch (Throwable e) {
            throw new FileNotFoundException(productURL.toString());
        }
        
        return productUrlToConnectionCommand.getURLConnectionRet();
    }
}
