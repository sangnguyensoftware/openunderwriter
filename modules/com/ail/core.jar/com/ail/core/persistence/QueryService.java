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

import java.util.List;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceInterface;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.core.persistence.hibernate.HibernateQueryService;

/**
 * Arguments required by query service
 */
@ServiceInterface
public interface QueryService {

    /**
     * Arguments required by query service
     */
    @ServiceArgument
    public interface QueryArgument extends Argument {

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

        /**
         * Getter returning the results of the query
         * @return list of results from the query
         */
        List<?> getResultsListRet();

        /**
         * Setter for the list of query results
         * @param resultsListRet List of results
         */
        void setResultsListRet(List<Object> resultsListRet);

        /**
         * Gets the unique result - assuming there was only one object returned. This
         * is an alternative to getResultsList.
         * @return Result of the query
         */
        Object getUniqueResultRet();

        /**
         * Set the unique result.
         * @see #getUniqueResultRet()
         * @param type Result
         */
        void setUniqueResultRet(Object type);
    }

    @ServiceCommand(defaultServiceClass=HibernateQueryService.class)
    public interface QueryCommand extends Command, QueryArgument {}
}


