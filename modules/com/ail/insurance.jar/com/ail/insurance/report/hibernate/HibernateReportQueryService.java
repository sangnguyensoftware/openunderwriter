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

package com.ail.insurance.report.hibernate;

import java.util.ArrayList;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.persistence.QueryException;
import com.ail.core.persistence.hibernate.HibernateSessionBuilder;
import com.ail.insurance.report.ReportQueryService.ReportQueryArgument;


@ServiceImplementation
public class HibernateReportQueryService extends Service<ReportQueryArgument> {

    private static final long serialVersionUID = 3198893603834694389L;

    @SuppressWarnings("unchecked")
    @Override
    public void invoke() throws BaseException {
        args.setResultsListRet(new ArrayList<Object[]>());

        if (args.getQueryArg() == null || args.getQueryArg().length() == 0) {
            throw new PreconditionException("args.getQueryArg() == null");
        }

        try {
            Session session = HibernateSessionBuilder.getSessionFactory().getCurrentSession();
            Query query = session.createQuery(args.getQueryArg());
            query.setReadOnly(true);

            if (args.getQueryArgumentsArg() != null) {
                for(int argNo = 0; argNo < args.getQueryArgumentsArg().size(); argNo++) {
                    query.setParameter(argNo, args.getQueryArgumentsArg().get(argNo));
                }
            }

            args.setResultsListRet(query.list());

        }
        catch (HibernateException e) {
            throw new QueryException(e.getMessage(), e);
        }

    }

}
