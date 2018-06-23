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

package com.ail.core.jsonmapping.jackson;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectReader;

import com.ail.annotation.ServiceImplementation;
import com.ail.core.JSONException;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.jsonmapping.FromJSONService.FromJSONArgument;

/**
 * This entry point uses the Jackson framework to unmarshal an JSON into the
 * object it represents.
 * <p>
 * Two arguments are accepted:
 * <ul>
 * <li>classIn - The Class object to be used. The unmarshal will create an
 * instance of this class and populate it as the root element of the JSON
 * String.</li>
 * <li>JSONArg - The JSON string representing the object to be unmarshalled.
 * </li>
 * </ul>
 * One return object is generated:
 * <ul>
 * <li>ObjectOut - The object resulting from the unmarshal.</li>
 * </ul>
 * These arguments and returns are encapsulated in an instance of
 * FromJSONCommandArg.
 */
@ServiceImplementation
public class JacksonFromJSONService extends Service<FromJSONArgument> {

    /**
     * The 'business logic' of the entry point.
     *
     * @throws PreconditionException
     * @throws PostconditionException
     * @throws JSONException
     */
    @Override
    public void invoke() throws PreconditionException, PostconditionException, JSONException {
        if (args.getJSONArg() == null || args.getJSONArg().isEmpty()) {
            throw new PreconditionException("args.getJSONArg() == null || args.getJSONArg().isEmpty()");
        }

        if (args.getClassArg() == null) {
            throw new PreconditionException("args.getClassArg() == null");
        }

        try {
            ObjectReader objectReader = new Mapper().buildReader(args.getClassArg(), args.getMappingOptionsArg());

            Object objectRet = objectReader.readValue(args.getJSONArg());

            args.setObjectRet(objectRet);
        } catch (IOException e) {
            throw new JSONException("Failed to convert JSON string to object of type: "+args.getClassArg().getName(), e);
        }

        if (args.getObjectRet() == null) {
            throw new PostconditionException("args.getObjectRet() == null");
        }
    }
}
