/* Copyright Applied Industrial Logic Limited 2005. All rights Reserved */
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

package com.ail.core.product;

import java.net.URL;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceInterface;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.core.product.liferay.LiferayProductUrlToExternalUrlService;

/**
 * <p>
 * Service interface for a service which converts product URLs used internally
 * but the system into a form that can be referenced externally. External
 * reference is taken to mean that the URLs can be passed to a client's browser
 * and be referenced successfully.
 * </p>
 * <p>
 * Product content is referred to internally (within the server) using
 * "product:" URLs. As this kind of reference can only be used in a context
 * where the product URL hander is available, they cannot be passed back to a
 * client without some conversion. This conversion is specific to the content
 * management system that is being used to hold the content itself.
 * </p>
 * <p>As an example of what this conversion might look like, and taking a
 * liferay implementation of this service as an example:<br/>
 * Passing a value into ProductUrlArg of "product:/AIL/Demo/MotorPlus/HTML/Welcome.html"<br/>
 * Returns a value in ExternalUrlRet or: ""<br/>
 * </p>
 * 
 */
@ServiceInterface
public class ProductUrlToExternalUrlService {

    @ServiceArgument
    public interface ProductUrlToExternalUrlArgument extends Argument {
        URL getProductUrlArg();

        void setProductUrlArg(URL productUrlArg);

        String getExternalUrlRet();

        void setExternalUrlRet(String externalUrlRet);
    }

    @ServiceCommand(defaultServiceClass = LiferayProductUrlToExternalUrlService.class)
    public interface ProductUrlToExternalUrlCommand extends Command, ProductUrlToExternalUrlArgument {
    }
}
