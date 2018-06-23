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
package com.ail.core.configure;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * This ConfigurationLoader is almost identical to the JDBCConfigurationLoader, except in
 * that it gets its database connection from a connection pool via a JNDI lookup (rather than
 * by creating the connection itself).<p>
 * This loader expects the loader.property file to define the following properties:<ol>
 * <li><code>jndiname</code> - the JNDI name of the connection pool to use.</li></ol>
 */
public class ConnectionPoolConfigurationLoader extends JDBCConfigurationLoader {
    private DataSource dataSource=null;

    /**
     * Do a lookup and get the datasource - cache it for later use.
     * @return datasource
     */
    private DataSource getDataSource() {
        if (dataSource==null) {
            String jndiName=getLoaderParams().getProperty("jndiname");
            try {
                Context ctx = new InitialContext();
                dataSource=(DataSource)ctx.lookup(jndiName);
            }
            catch(NamingException e) {
                throw new BootstrapConfigurationError("Failed to create initial context (jndiname="+jndiName+"):"+e);
            }
            catch(Throwable t) {
                throw new BootstrapConfigurationError("Failed to get database connection  (jndiname="+jndiName+"):"+t);
            }
        }

        return dataSource;
    }

	/**
	 * Open a connection to the configuration database.
	 * Note: This is public for testing purposes only. Directly using the
	 * configuration table of a live system is strongly discouraged!
	 * @return connection to read data from.
	 */
	public Connection openConnection() {
		try {
            return getDataSource().getConnection();
        }
        catch(SQLException e) {
            throw new BootstrapConfigurationError("Database access error:"+e);
        }
    }
}









