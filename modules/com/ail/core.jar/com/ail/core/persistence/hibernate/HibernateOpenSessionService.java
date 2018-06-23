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

import org.hibernate.Session;

import com.ail.annotation.ServiceImplementation;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.persistence.OpenSessionService.OpenSessionArgument;
import com.ail.core.persistence.UpdateException;

/**
 * Implementation of the open session service for Hibernate
 */
@ServiceImplementation
public class HibernateOpenSessionService extends Service<OpenSessionArgument> {
    @Override
    public void invoke() throws PreconditionException, UpdateException {
        Session session = HibernateSessionBuilder.getSessionFactory().getCurrentSession();
        session.getTransaction().begin();
    }
}
