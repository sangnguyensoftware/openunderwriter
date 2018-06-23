/* Copyright Applied Industrial Logic Limited 2002. All rights reserved. */
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

package com.ail.core.command;

import static com.ail.core.Functions.classForName;

import com.ail.core.BaseException;
import com.ail.core.CoreProxy;
import com.ail.core.Service;
import com.ail.core.configure.Configuration;
import com.ail.core.configure.ConfigurationError;
import com.ail.core.factory.SpringFactory;

/**
 * This accessor supports the use of Spring components as services. Using the SpringAccessor in 
 * place of the ClassAccessor allows service to be wired using by spring. For complex services this
 * is the preferred approach as it makes the services easier to test and more convenient to 
 * code through the use of annotations.
 */
public class SpringAccessor extends Accessor {
    private String serviceClass = null;

    @SuppressWarnings("rawtypes")
    transient Service instance = null; // transient tells the cloner to keep its
                                       // hands off.

    public void invoke() throws BaseException {
        super.logEntry();
        getInstance().invoke();
        super.logExit();
    }

    @SuppressWarnings("unchecked")
    public void setArgs(Argument that) {
        getInstance().setArgs(that);
    }

    public Argument getArgs() {
        return getInstance().getArgs();
    }

    public Configuration getConfiguration() {
        throw new CommandInvocationError("Get configuration cannot be invoked on a Spring service");
    }

    public void setConfiguration(Configuration properties) {
        throw new CommandInvocationError("Set configuration cannot be invoked on a Spring service");
    }

    public void setServiceClass(String serviceClass) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        this.serviceClass = serviceClass;
    }

    public String getServiceClass() {
        return serviceClass;
    }

    public void setInstance(Service<? extends Argument> instance) {
        this.instance = instance;
    }

    /**
     * Following the normal Spring annotation convention the component name is
     * based on the class name as follows: for the class "com.thing.MyComponent"
     * the component name will default to "myComponent".
     */
    private String getServiceComponentName() {
        int lastDot = serviceClass.lastIndexOf('.');
        return Character.toLowerCase(serviceClass.charAt(lastDot+1)) + serviceClass.substring(lastDot + 2);
    }

    @SuppressWarnings("rawtypes")
    public Service getInstance() {
        if (instance == null) {
            try {
                SpringFactory factory = (SpringFactory) new CoreProxy().fetchBuilder("SpringBuilder");
                instance = (Service) factory.instantiateType(getServiceComponentName(), classForName(serviceClass));
            } catch (ClassNotFoundException e) {
                throw new ConfigurationError("Service '" + serviceClass + "' caanot be instantiated", e);
            }
        }

        return instance;
    }
}
