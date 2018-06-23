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

package com.ail.core.product.liferay;

import java.net.URL;

import com.ail.annotation.ServiceImplementation;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.RunAsProductHandler;
import com.ail.core.Service;
import com.ail.core.product.ProductUrlToConnectionService;
import com.ail.core.product.liferay.LiferayLocateFileEntryService.LiferayLocateFileEntryCommand;
import com.ail.core.urlhandler.product.DocumentLibraryURLConnection;
import com.ail.core.urlhandler.product.FileNotFoundPostcondition;

@ServiceImplementation
public class LiferayProductUrlToConnectionService extends Service<ProductUrlToConnectionService.ProductUrlToConnectionArgument> {

    @Override
    public void invoke() throws PreconditionException, PostconditionException {
        if (args.getProductUrlArg() == null) {
            throw new PreconditionException("args.getProductUrlArg()==null");
        }

        DocumentLibraryURLConnection connection = new DocumentLibraryURLConnection(args.getProductUrlArg());

        try {
            new RunAsProductHandler() {
                DocumentLibraryURLConnection connection;
                URL url;

                RunAsProductHandler with(URL url, DocumentLibraryURLConnection connection) {
                    this.connection = connection;
                    this.url = url;
                    return this;
                }

                @Override
                protected void doRun() throws Exception {
                    LiferayLocateFileEntryCommand llfec=getCore().newCommand(LiferayLocateFileEntryCommand.class);
                    llfec.setProductUrlArg(url);
                    llfec.invoke();
                    connection.setFileEntry(llfec.getFileEntryRet());
                }
            }.with(args.getProductUrlArg(), connection).run();
        } catch (Throwable e) {
            throw new FileNotFoundPostcondition(args.getProductUrlArg().toString());
        }

        args.setURLConnectionRet(connection);

        if (args.getURLConnectionRet() == null) {
            throw new PostconditionException("args.getURLConnection()==null");
        }
    }
}
