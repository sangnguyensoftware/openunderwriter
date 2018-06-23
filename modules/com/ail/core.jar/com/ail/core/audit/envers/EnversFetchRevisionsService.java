/* Copyright Applied Industrial Logic Limited 2018. All rights Reserved */
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

import static org.hibernate.envers.query.AuditEntity.id;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionType;

import com.ail.annotation.ServiceImplementation;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.Type;
import com.ail.core.audit.FetchRevisionsService.FetchRevisionsArgument;
import com.ail.core.persistence.CreateException;
import com.ail.core.persistence.hibernate.HibernateSessionBuilder;

/**
 * An implementation of FetchRevisionsService for Envers.
 */
@ServiceImplementation
public class EnversFetchRevisionsService extends Service<FetchRevisionsArgument> {

    @Override
    public void invoke() throws PreconditionException, CreateException {
        args.setRevisionsRet(new ArrayList<>());

        AuditReader auditReader = AuditReaderFactory.get(HibernateSessionBuilder.getSessionFactory().getCurrentSession());

        @SuppressWarnings("unchecked")
        List<Object[]> revisions = auditReader.
                            createQuery().
                            forRevisionsOfEntity(args.getClassArg(), false, true).
                            add(id().eq(args.getUIDArg())).
                            getResultList();

        for(Object[] res: revisions) {
            args.getRevisionsRet().add(new com.ail.core.audit.Revision((Revision)res[1], (Type) res[0], toRevisionType(res[2])));
        }
    }

    private com.ail.core.audit.RevisionType toRevisionType(Object o) {
        switch((RevisionType)o) {
        case ADD: return com.ail.core.audit.RevisionType.INSERT;
        case MOD: return com.ail.core.audit.RevisionType.UPDATE;
        case DEL: return com.ail.core.audit.RevisionType.DELETE;
        default: throw new IllegalStateException("revision type: " + o + " is not recognized.");
        }
    }
}
