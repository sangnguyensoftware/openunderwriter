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

import org.codehaus.jackson.map.ObjectWriter;

import com.ail.annotation.ServiceImplementation;
import com.ail.core.JSONException;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.jsonmapping.ToJSONService.ToJSONArgument;

/**
 * This entry point converts an objects into an JSON string representing it
 * using the jackson framework. The object passed as an argument is marshalled
 * into an JSON string and returned. This entry points accepts one argument:
 * <ul>
 * <li>ObjectArg - The object to be marshalled.</li>
 * </ul>
 * And generates one return value:
 * <ul>
 * <li>JSONRet - The result of the marshalling process.</li>
 * </ul>
 * These arguments and returns are encapsulated in an instance of ToXMLCommand.
 */
@ServiceImplementation
public class JacksonToJSONService extends Service<ToJSONArgument> {

    @Override
    public void invoke() throws PreconditionException, PostconditionException, JSONException {
        if (args.getObjectArg() == null) {
            throw new PreconditionException("args.getObjectArg() == null");
        }

        try {
            ObjectWriter objectWriter = new Mapper().buildWriter(args.getMappingOptionsArg());

            String json = objectWriter.writeValueAsString(args.getObjectArg());

            args.setJSONRet(json);
        } catch (IOException e) {
            throw new JSONException("Failed to 'toJSON' object: " + args.getObjectArg(), e);
        }

        if (args.getJSONRet() == null) {
            throw new PostconditionException("args.getJSONRet() == null");
        }
    }
}
