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

package com.ail.core.user.liferay;

import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.Service;
import com.ail.core.Type;
import com.ail.core.TypeXPathException;
import com.ail.core.user.GuessUsernameService.GuessUsernameArgument;

/**
 * Implementation of the GuessUsername service for Liferay. The service attempts
 * to derive the user's username based on a number of options. The intention is
 * that this service is used before a user has actually logged in, or before
 * they have even created an account. The service will work through a number of
 * options to determine what their username is likely to be based on the
 * information that they have already supplied. As the location of possible user
 * name information is highly dependent on the context within which the service
 * is working, the possible locations are defined as XPaths into a model and
 * core session (CoreContext.getSession()). The XPaths themselves are defined as
 * service parameters.
 */
@ServiceImplementation
public class GuessUsernameService extends Service<GuessUsernameArgument> {

    @Override
    public void invoke() throws BaseException {
        if (args.getUsernameLocationsArg() == null || args.getUsernameLocationsArg().length()==0) {
            args.setUsernameRet("");
        }
        else {
            String username = null;
            
            for(String xpath: args.getUsernameLocationsArg().split(",")) {
                username = tryGettingUsernameUsing(xpath, args.getModelArg(), username);
                username = tryGettingUsernameUsing(xpath, CoreContext.getSessionTemp(), username);
            }
            
            args.setUsernameRet(username != null ? username : "");
        }
    }

    private String tryGettingUsernameUsing(String xpathOption, Type src, String username) {
        if (src != null && username == null && xpathOption != null && xpathOption.length() != 0) {
            try {
                return src.xpathGet(xpathOption, String.class);
            }
            catch(TypeXPathException e) {
                // ignore - fall through to the return
            }
        }
        return username;
    }
}

