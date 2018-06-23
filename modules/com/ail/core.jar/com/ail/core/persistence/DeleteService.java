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

package com.ail.core.persistence;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceInterface;
import com.ail.core.Type;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.core.persistence.hibernate.HibernateDeleteService;

/**
 * Arguments required by delete service. The delete service supports two types
 * of usage: 1) when the objectArg in non-null it is taken to be the a request
 * to delete that specific object from persistent storage; 2) when queryNameArg
 * and (optionally) queryArgumentsArg are supplied, the named query is executed.
 * These two modes can be used independently or simultaneously.
 */
@ServiceInterface
public interface DeleteService {

    /**
     * Arguments required by delete service
     */
    @ServiceArgument
    public interface DeleteArgument extends Argument {

        /**
         * Setter for the objectArg property. * @see #getObjectArg
         * @param objectArg new value for property.
         */
        void setObjectArg(Type objectArg);

        /**
         * Getter for the objectArg property. Object to persist
         * @return Value of objectArg, or null if it is unset
         */
        Type getObjectArg();

        /**
         * Setter for the list of argument values referenced in the query.
         * @see #getQueryArgumentsArg
         * @param objectArg new value for property.
         */
        void setQueryArgumentsArg(Object... queryArgumentsArg);

        /**
         * Getter for the list of argument values referenced in the query
         * @return List of argument objects
         */
        Object[] getQueryArgumentsArg();

        /**
         * Setter for the query string property.
         * @see #getQuery
         * @param query new value for property.
         */
        void setQueryNameArg(String queryName);

        /**
         * Getter for the query property. 
         * @return Value of query
         */
        String getQueryNameArg();
    }

    @ServiceCommand(defaultServiceClass=HibernateDeleteService.class)
    public interface DeleteCommand extends Command, DeleteArgument {}
}


