/* Copyright Applied Industrial Logic Limited 2015. All rights Reserved */
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

import java.util.Date;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;

@ServiceImplementation
public class ExpireOldDocumentRequestsService extends Service<ExpireOldDocumentRequestsService.ExpireOldDocumentRequestsArgument> {

    @ServiceArgument
    public interface ExpireOldDocumentRequestsArgument extends Argument {
    }

    @ServiceCommand(defaultServiceClass = ExpireOldDocumentRequestsService.class)
    public interface ExpireOldDocumentRequestsCommand extends Command, ExpireOldDocumentRequestsArgument {
    }

    public void invoke() throws BaseException, PreconditionException, PostconditionException {
        int expiryMinutes = new Integer(core.getParameter("DocumentRequestExpiryMinutes").getValue());

        long expiry = System.currentTimeMillis() - (expiryMinutes * 1000 * 60); 
        
        core.delete("delete.documentRequests.by.createdDate", new Date(expiry));
    }
}
