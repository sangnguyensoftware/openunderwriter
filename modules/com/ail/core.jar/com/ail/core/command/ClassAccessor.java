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
import com.ail.core.Service;
import com.ail.core.configure.Configuration;

/**
 * This accessor supports the use of class instances as services.
 */
public class ClassAccessor extends Accessor {
	private String serviceClass=null;
    @SuppressWarnings("rawtypes")
    transient Service instance=null; // transient tells the cloner to keep its hands off.

    public void invoke() throws BaseException {
        super.logEntry();
		instance.invoke();
        super.logExit();
    }

    @SuppressWarnings("unchecked")
    public void setArgs(Argument that) {
		instance.setArgs(that);
    }

	public Argument getArgs() {
		return instance.getArgs();
    }

	public Configuration getConfiguration() {
        return instance.getConfiguration();
    }

	public void setConfiguration(Configuration config) {
		instance.setConfiguration(config);
    }

	@SuppressWarnings("unchecked")
    public void setServiceClass(String serviceClass) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        this.serviceClass=serviceClass;
		this.instance=(Service<? extends Argument>)classForName(serviceClass).newInstance();
    }

	public String getServiceClass() {
        return serviceClass;
    }

    public void setInstance(Service<? extends Argument> instance) {
        this.instance=instance;
    }

    @SuppressWarnings("rawtypes")
    public Service getInstance() {
        return this.instance;
    }
}
