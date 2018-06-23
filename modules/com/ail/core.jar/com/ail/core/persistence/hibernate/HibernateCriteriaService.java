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

import static com.ail.core.Functions.isEmpty;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import com.ail.annotation.ServiceImplementation;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.persistence.CriteriaException;
import com.ail.core.persistence.CriteriaService.CriteriaArgument;

/**
 * Implementation of the criteria service for Hibernate
 */
@ServiceImplementation
public class HibernateCriteriaService extends Service<CriteriaArgument> {
    @Override
    public void invoke() throws PreconditionException,PostconditionException, CriteriaException {

        if (args.getClassArg() == null) {
            throw new PreconditionException("args.getClassArg() == null");
        }

        try {
            Session session = HibernateSessionBuilder.getSessionFactory().getCurrentSession();

            if (isEmpty(args.getAliasArg())) {
                args.setCriteriaRet(session.createCriteria(args.getClassArg()));
            }
            else {
                args.setCriteriaRet(session.createCriteria(args.getClassArg(), args.getAliasArg()));
            }
        }
        catch (HibernateException e) {
			throw new CriteriaException(e.getMessage(), e);
		}

        if (args.getCriteriaRet() == null) {
            throw new PostconditionException("args.getCriteriaRet() == null");
        }
    }
}


