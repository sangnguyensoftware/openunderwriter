/* Copyright Applied Industrial Logic Limited 2016. All rights Reserved */
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

package com.ail.core.jsonmapping;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceInterface;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.core.jsonmapping.jackson.JacksonFromJSONService;

/**
 * Interface defining the bean class to encapsulate the arguments and returns
 * used by the FromJson entry point(s).
 */
@ServiceInterface
public interface FromJSONService {

    /**
     * Interface defining the bean class to encapsulate the arguments and
     * returns used by the FromJson entry point(s).
     */
    @ServiceArgument
    public interface FromJSONArgument extends Argument {

        /**
         * Fetch the value of ObjectRet. This is the value returned from the
         * FromJson entry point, and represents the object created when
         * <code>JSONArg</code> was unmarshalled.
         *
         * @return Unmarshalled object
         */
        Object getObjectRet();

        /**
         * Set the value of the ObjectRet property.
         *
         * @param objectRet
         *            The value to be set.
         */
        void setObjectRet(Object objectRet);

        /**
         * Fetch the JsonString that will be unmarshalled by the entry point.
         * This is the input to the unmarshal process, and should represent an
         * instance of the class specified in <code>ClassIn</code>.
         *
         * @return The JSON string to be unmarshalled.
         */
        String getJSONArg();

        /**
         * Set the value of the JSONArg property.
         *
         * @param jsonArg
         *            The value to be set.
         */
        void setJSONArg(String jsonArg);

        /**
         * Get the value of the ClassIn argument. This argument specifies the
         * class which <code>JsonArg</code> represents an instance of.
         *
         * @return The class to be unmarshalled into.
         */
        Class<?> getClassArg();

        /**
         * Set the value of the ClassArg property.
         *
         * @param classArg
         *            The value to be set.
         */
        void setClassArg(Class<?> classArg);

        /**
         * Optional comma separated list of options to be passed to the JSON
         * mapping framework. The actual option values depend on the framework
         * being used.
         *
         * @return mapping options
         */
        String getMappingOptionsArg();

        /**
         * @see #getMappingOptionsArg()
         * @param mappingOptionsArg
         */
        void setMappingOptionsArg(String mappingOptionsArg);

    }

    @ServiceCommand(defaultServiceClass = JacksonFromJSONService.class)
    public interface FromJSONCommand extends Command, FromJSONArgument {
    }
}
