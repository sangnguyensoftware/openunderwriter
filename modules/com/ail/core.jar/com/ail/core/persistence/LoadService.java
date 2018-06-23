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
import com.ail.core.persistence.hibernate.HibernateLoadService;

/**
 * Arguments required by load service
 */
@ServiceInterface
public interface LoadService {
    
    /**
     * Arguments required by load service
     */
    @ServiceArgument
    public interface LoadArgument extends Argument {
        /**
         * Setter for the objectArg property. 
         * @see #getObjectArg
         * @param objectArg new value for property.
         */
        void setObjectRet(Type objectArg);

        /**
         * Getter for the objectArg property. Object loaded
         * @return Value of objectArg
         */
        Type getObjectRet();

        /**
         * Setter for the typeArg property. 
         * @see #getTypeArg
         * @param typeArg new value for property.
         */
        void setTypeArg(Class<?> typeArg);

        /**
         * Getter for the typeArg property. Type to load
         * @return Value of typeArg
         */
        Class<?> getTypeArg();

        /**
         * Setter for the idArg property. 
         * @see #getIdArg
         * @param idArg new value for property.
         */
        void setSystemIdArg(long id);

        /**
         * Getter for the idArg property. Id for Object to load
         * @return Value of idArg
         */
        long getSystemIdArg();
    }

    @ServiceCommand(defaultServiceClass=HibernateLoadService.class)
    public interface LoadCommand extends Command, LoadArgument {}
}


