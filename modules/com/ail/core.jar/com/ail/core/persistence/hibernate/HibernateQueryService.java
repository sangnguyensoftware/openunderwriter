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

package com.ail.core.persistence.hibernate;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import com.ail.annotation.ServiceImplementation;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.persistence.QueryException;
import com.ail.core.persistence.QueryService.QueryArgument;

/**
 * Implementation of the query service for Hibernate
 */
@ServiceImplementation
public class HibernateQueryService extends Service<QueryArgument> {
    @Override
    @SuppressWarnings("unchecked")
    public void invoke() throws PreconditionException,QueryException {

	    if (args.getQueryNameArg()==null || args.getQueryNameArg().length()==0) {
	        throw new PreconditionException("args.getQueryArg()==null");
	    }

        Session session=null;

        try {
            session = HibernateSessionBuilder.getSessionFactory().getCurrentSession();
            Query query = null;
            String queryArg = args.getQueryNameArg();

            if (queryArg.startsWith("from ")) { // HQL query
                query = session.createQuery(queryArg);

            } else {  // Named query
                query = session.getNamedQuery(queryArg);

                for(int argNo=0 ; argNo<args.getQueryArgumentsArg().length ; argNo++) {
                    query.setParameter(argNo, args.getQueryArgumentsArg()[argNo]);
                }
            }

            List<Object> results = query.list();

            args.setResultsListRet(results);

            if (results.size()==1) {
                args.setUniqueResultRet(results.get(0));
            }
		}
        catch (HibernateException e) {
			throw new QueryException(e.getMessage(), e);
		}
    }
}


