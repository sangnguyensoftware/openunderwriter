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

import com.ail.core.CoreProxy;

/**
 * <b>Name</b><br/>
 * &nbsp;&nbsp;com.ail.core.configure.ConfigurationReset - Utility to reset a list of named configurations.
 * <br/><br/>
 * <b>Synopsis</b><br/>
 * &nbsp;&nbsp;com.ail.core.configure.ConfigurationReset [namespace1 namespace2 ... namespaceN]
 * <br/><br/>
 * <b>Description</b><br/>
 * By default (without any namespace arguments) this utility will reset the <code>com.ail.core.Core</code> and
 * <code>com.ail.core.CoreProxy</code> namespaces. Any further namespaces specified on the command line will
 * also be reset.<br/><br/>
 *
 * The details of the database to which the reset configurations should be written are specified in the form
 * defined by {@link com.ail.core.configure.AbstractConfigurationLoader AbstractConfigurationLoader}. Generally,
 * this takes the form of a collection of -D's on the command line.<br/><br/>
 *
 * <b>Example</b><br/>
 * To reset only the default namespaces in a MySQL database using the JDBC configuration loader, you would
 * execute the following:
 * <br/><br/>
 * <code>$java -Dcom.ail.core.configure.loader=com.ail.core.configure.JDBCConfigurationLoader \<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;-Dcom.ail.core.configure.loaderParam.driver=com.mysql.jdbc.Driver \<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;-Dcom.ail.core.configure.loaderParam.url=jdbc:mysql://localhost:3306/core_2_0 \<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;-Dcom.ail.core.configure.loaderParam.user=root \<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;-Dcom.ail.core.configure.loaderParam.password=bombay2000 \<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;-Dcom.ail.core.configure.loaderParam.table=config \<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;-Dcom.ail.core.configure.loaderParam.databaseName=core_2_0 \<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;com.ail.core.configure.ConfigurationReset
 * <br/><br/>
 * @version 1.0
 */
public class ConfigurationReset {

    public static void main(String args[]) throws Exception {
        new CoreProxy().resetConfigurations();
    }
}
