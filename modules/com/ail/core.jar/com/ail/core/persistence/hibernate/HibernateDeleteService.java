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

import org.hibernate.Query;
import org.hibernate.Session;

import com.ail.annotation.ServiceImplementation;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.persistence.DeleteException;
import com.ail.core.persistence.DeleteService.DeleteArgument;

/**
 * Implementation of the delete service for Hibernate
 */
@ServiceImplementation
public class HibernateDeleteService extends Service<DeleteArgument> {
    /** The 'business logic' of the entry point. 
     * @throws PostconditionException */
    @Override
    public void invoke() throws PreconditionException, DeleteException, PostconditionException {

        // check arguments
	    if (args.getObjectArg()==null && args.getQueryNameArg()==null){
	        throw new PreconditionException("args.getObjectArg()==null && args.getQueryNameArg()==null");
	    }
        
        Session session=null;
        
        // create record
		try {
            session = HibernateSessionBuilder.getSessionFactory().getCurrentSession(); 

            if (args.getObjectArg() != null) {
                session.delete(args.getObjectArg());
            }

            if (args.getQueryNameArg() != null) {
                Query query = session.getNamedQuery(args.getQueryNameArg());
                
                for(int argNo=0 ; argNo<args.getQueryArgumentsArg().length ; argNo++) {
                    query.setParameter(argNo, args.getQueryArgumentsArg()[argNo]);
                }
                
                query.executeUpdate();
            }
		} 
        catch (Throwable t) {
			throw new DeleteException("Hibernate could not delete the object from the database", t);
		}
    }
}
