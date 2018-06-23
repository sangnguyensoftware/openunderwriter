/* Copyright Applied Industrial Logic Limited 2017. All rights Reserved */
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

package com.ail.core.audit.envers;

import static com.ail.core.persistence.hibernate.HibernateSessionBuilder.getSessionFactory;

import java.util.List;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;

import com.ail.annotation.ServiceImplementation;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.audit.FetchVersionNumbersService.FetchVersionNumbersArgument;

/**
 * Implementation of FetchRevisions using Envers
 */
@ServiceImplementation
public class EnversFetchVersionNumbersService extends Service<FetchVersionNumbersArgument> {
    @Override
    public void invoke() throws PreconditionException, PostconditionException {
        if (args.getSystemIdArg() == null) {
            throw new PreconditionException("args.getSystemIdArg() == null");
        }

        if (args.getTypeArg() == null) {
            throw new PreconditionException("args.getTypeArg() == null");
        }

        AuditReader auditReader = AuditReaderFactory.get(getSessionFactory().getCurrentSession());
        List<Number> revisions = auditReader.getRevisions(args.getTypeArg(), args.getSystemIdArg());

        args.setRevisionsRet(revisions);

        if (args.getRevisionsRet() == null) {
            throw new PostconditionException("args.getRevisionsRet() == null");
        }
    }
}


