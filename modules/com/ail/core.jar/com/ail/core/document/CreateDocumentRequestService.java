/* Copyright Applied Industrial Logic Limited 2003. All rights Reserved */
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

import static com.ail.core.document.DocumentRequestType.GENERATE_AND_DOWNLOAD;

import java.util.UUID;

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
public class CreateDocumentRequestService extends Service<CreateDocumentRequestService.CreateDocumentRequestArgument> {

    @ServiceArgument
    public interface CreateDocumentRequestArgument extends Argument {
        String getDocumentTypeArg();

        void setDocumentTypeArg(String documentTypeArg);

        Long getSourceUIDArg();

        void setSourceUIDArg(Long sourceUIDArg);

        Long getDocumentUIDArg();

        void setDocumentUIDArg(Long documentUIDArg);

        String getRequestIdRet();

        void setRequestIdRet(String requestIdRet);

        DocumentRequestType getDocumentRequestTypeArg();

        void setDocumentRequestTypeArg(DocumentRequestType requestTypeArg);
    }

    @ServiceCommand(defaultServiceClass = CreateDocumentRequestService.class)
    public interface CreateDocumentRequestCommand extends Command, CreateDocumentRequestArgument {
    }

    /** The 'business logic' of the entry point. */
    @Override
    public void invoke() throws BaseException, PreconditionException, PostconditionException {
        if (args.getDocumentRequestTypeArg() == null) {
            throw new PreconditionException("args.getDocumentRequestTypeArg() == null");
        }

        if (args.getDocumentRequestTypeArg().equals(GENERATE_AND_DOWNLOAD) && (args.getDocumentTypeArg() == null || args.getSourceUIDArg() == null)) {
            throw new PreconditionException("args.getDocumentRequestTypeArg().equals(GENERATE_AND_DOWNLOAD) && (args.getDocumentTypeArg() == null || args.getSourceUIDArg() == null)");
        }

        DocumentRequest documentRequest = core.newType(DocumentRequest.class);

        documentRequest.setRequestType(args.getDocumentRequestTypeArg());
        documentRequest.setDocumentType(args.getDocumentTypeArg());
        documentRequest.setSourceUID(args.getSourceUIDArg());
        documentRequest.setRequestId(UUID.randomUUID().toString());
        documentRequest.setDocumentUID(args.getDocumentUIDArg());

        core.create(documentRequest);

        args.setRequestIdRet(documentRequest.getRequestId());

        if (args.getRequestIdRet() == null) {
            throw new PostconditionException("args.getRequestIdRet() == null");
        }
    }
}
