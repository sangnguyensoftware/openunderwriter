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
import com.ail.core.jsonmapping.jackson.JacksonToJSONService;

@ServiceInterface
public interface ToJSONService {

    /**
     * This interface defines the Bean used to encapsulate the argument and return
     * values used by the ToJSON entry point.
     */
    @ServiceArgument
    public interface ToJSONArgument extends Argument {
        /**
         * Fetch the ObjectArg value. ObjectArg represents the object to be
         * converted into JSON.
         * @return The object to be marshalled
         */
        Object getObjectArg();

        /**
         * Set the value of the ObjectArg property.
         * @param objectArg The new value.
         */
        void setObjectArg(Object objectArg);

        /**
         * Fetch the JsonOut value. JsonRet represents the result of the marshal
         * process: a string of JSON built from <code>objectIn</code>
         * @return marshalled JSON object.
         */
        String getJSONRet();

        /**
         * Set the value of the JsonRet property.
         * @param jsonRet The new value.
         */
        void setJSONRet(String jsonRet);

        /**
         * Optional comma separated list of options to be passed to the JSON mapping framework. The
         * actual option values depend on the framework being used.
         * @return mapping options
         */
        String getMappingOptionsArg();

        /**
         * @see #getMappingOptionsArg()
         * @param mappingOptionsArg
         */
        void setMappingOptionsArg(String mappingOptionsArg);
    }

    @ServiceCommand(defaultServiceClass = JacksonToJSONService.class)
    public interface ToJSONCommand extends Command, ToJSONArgument {}
}
