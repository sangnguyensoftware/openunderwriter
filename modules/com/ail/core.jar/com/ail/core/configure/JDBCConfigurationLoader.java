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

import static com.ail.core.Functions.classForName;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import com.ail.core.Functions;
import com.ail.core.VersionEffectiveDate;

/**
 * This class handles the loading and saving of property (configuration)
 * information held in a JDBC datastore.<p>
 * This loader expects the loader.property file to define the following (as
 * a minimum):<ol>
 * <li><code>driver</code> - the JDBC driver class to use.</li>
 * <li><code>url</code> - The database connection URL.</li>
 * <li><code>databaseName</code> - The name of the database in which <code>table</code> can be found.</li>
 * <li><code>table</code> - The name of the database table holding configuration information.</li>
 * <li><code>user</code> - The DB user to use when accessing the database.</li>
 * <li><code>password</code> - The DB user's password.</li>
 * </ol>
 * The database table defined (<code>table</code> above) is expected to match the
 * description below. Assuming that the <code>user</code> has the necessary
 * privileges, this table will be created automatically:<pre>
 * +---------------+---------------+------+-----+---------+-------+
 * | Field         | Type          | Null | Key | Default | Extra |
 * +---------------+---------------+------+-----+---------+-------+
 * | namespace     | varchar(255)  |      |     |         |       |
 * | manager       | varchar(255)  |      |     |         |       |
 * | configuration | longblob      |      |     |         |       |
 * | validfrom     | bigint(20)    |      |     | 0       |       |
 * | validto       | bigint(20)    | YES  |     | NULL    |       |
 * | who           | varchar(32)   |      |     |         |       |
 * | version       | varchar(32)   |      |     |         |       |
 * +---------------+---------------+------+-----+---------+-------+</pre>
 */
public class JDBCConfigurationLoader extends AbstractConfigurationLoader {
    // we don't want to check for table existence every time we're called,
    // so we check once and hang onto the result in this static.
    protected static Boolean configTableExists=null;
    protected static Boolean configTableContainsData=null;

    /**
     * Open a connection to the configuration database.
     * Note: This is public for testing purposes only. Directly using the
     * configuration table of a live system is strongly discouraged!
     * @return connection to read data from.
     */
    public Connection openConnection() {
        // load the JDBC driver
        try {
            classForName(getLoaderParams().getProperty("driver"));

            return DriverManager.getConnection(getLoaderParams().getProperty("url"), getLoaderParams());
        }
        catch(ClassNotFoundException e) {
            throw new BootstrapConfigurationError("JDBC Driver Class ("+getLoaderParams().getProperty("driver")+") not found.");
        }
        catch(SQLException e) {
            throw new BootstrapConfigurationError("Database access error (driver:"+getLoaderParams().getProperty("driver")+", url:"+getLoaderParams().getProperty("url")+") "+e);
        }
    }

    /**
     * Check to see if the config table doesn't exist.
     * @return true if the table doesn't exist, false otherwise.
     */
    protected synchronized boolean isConfigTablePresent() {
        boolean ret=true; // fail safe - default to it not existing.

        // if we've done the check already, use the result we got then.
        if (configTableExists!=null) {
            ret=configTableExists.booleanValue();
        }
        else {
            Connection con=null;
            ResultSet rs=null;
            Statement stat=null;

            try {
                con=openConnection();
                stat=con.createStatement();
                rs=stat.executeQuery("SELECT count(*) FROM "+getLoaderParams().get("table"));
                ret=true;
            }
            catch(SQLException e) {
            		// What we want to do here is say "if this is a table not found exception, then ignore it.",
            		// but we can't because JDBC exceptions aren't that specific - or rather they are but only
            		// in a vendor specific way, so we'll need to be vendor specific here too :-(

            		// MySQL returns error code 1146 & a message in the form "General error: Table 'x' doesn't exist"
            		if (e.getErrorCode()==1146 && e.getMessage().indexOf("doesn't exist")!=0) {
            			ret=false;
            		}
            		else {
            			System.err.println(e.toString());
            		}
            	}
            finally {
                Functions.closeJdbc(con, stat, rs);
            }

            // save the result of the check, we don't want to re-run it every time.
            configTableExists=new Boolean(ret);
        }

        return ret;
    }

    protected boolean isConfigTablePopulated() {
        Connection con = null;
        ResultSet rs = null;
        Statement stat = null;

        try {
            con = openConnection();
            stat = con.createStatement();
            rs = stat.executeQuery("SELECT count(*) FROM " + getLoaderParams().get("table"));
            return rs.getLong(1) != 0;
        } catch (SQLException e) {
            return false;
        } finally {
            Functions.closeJdbc(con, stat, rs);
        }
    }

    /**
     * Create the config table in the database.
     */
    protected synchronized void createConfigTable() {
        String createTableSql=getLoaderParams().getProperty("createTableSql");
        Connection con=null;
        Statement st=null;

        try {
            con=openConnection();
            st=con.createStatement();
            st.execute(createTableSql);
        }
        catch(Exception e) {
            throw new BootstrapConfigurationError("Database table creation failed with exception: "+e);
        }
        finally {
            Functions.closeJdbc(con, st, null);
        }

        // reset the 'table exists' indicator
        configTableExists=null;
    }

    /**
     * Load a namespace's configuration.
     * Namespaces are date relative, and the table will contain more than one
     * copy of the same namespace's configuration as it was valid on different
     * dates. Each row in the database has a <code>validFrom</code> and
     * <code>validTo</code> timestamp. The latest version of the configuration
     * will have the <code>validTo</code> set to zero.<p>
     * NOTE: If either the database or table specified in loader.properties do
     * not exist when the system starts, this method handles this like a missing
     * namespace. But if the system is live, and the table disappears, that is
     * recognised as an error.
     * @param namespace The namespace to load.
     * @param date The date to load the namespace for.
     * @throws UnknownNamespaceError if the namespace does not exist, or is not valid for the date specificed.
     * @return The configuration.
     */
    @Override
    public Configuration loadConfiguration(String namespace, VersionEffectiveDate date) {
        Configuration result=null;
        PreparedStatement st=null;
        ResultSet rs=null;
        long ts=date.getTime();

        // check that the database and table exist, if they don't treat is like a missing namespace.
        if (!isConfigTablePresent()) {
            throw new UnknownNamespaceError("Unknown namespace (table not found): '"+namespace+"'");
        }

        Connection con=openConnection();

        try {
            // first try loading the latest record (validto=0).
            st=con.prepareStatement(
                "SELECT CONFIGURATION,VALIDFROM,VALIDTO FROM "+getLoaderParams().get("table")+
                " WHERE namespace=? AND ?>=validfrom AND validto=0");
            st.setString(1, namespace);
            st.setLong(2, ts);

            rs=st.executeQuery();

            // if we don't get any records, try loading from history.
            if (!rs.next()) {
                Functions.closeJdbc(null, st, rs);
                rs=null;
                st=null;

                st=con.prepareStatement(
                    "SELECT CONFIGURATION,VALIDFROM,VALIDTO FROM "+getLoaderParams().get("table")+
                    " WHERE namespace=? AND ?>=validfrom AND validto>?");
                st.setString(1, namespace);
                st.setLong(2, ts);
                st.setLong(3, ts);

                rs=st.executeQuery();

                // if we didn't get any results back, the namespace is bad.
                if (!rs.next()) {
                    throw new UnknownNamespaceError("Unknown namespace for timestamp (row not found). namespace: '"+namespace+"' timestamp:"+ts);
                }
            }

            // fetch the properties.
            InputStream is = rs.getBinaryStream("CONFIGURATION");
            ObjectInputStream objis = new ObjectInputStream(is);
            result = (Configuration)objis.readObject();
            result.setNamespace(namespace);
            result.setValidFrom(new VersionEffectiveDate(rs.getLong(2)));
            if (rs.getLong(3)!=0) {
                result.setValidTo(new VersionEffectiveDate(rs.getLong(3)));
            }

            // if there are any more results in the set, there's a duplicate - which is bad.
            if (rs.next()) {
                throw new DuplicateNamespaceError("Namespace: '"+namespace+"' is defined twice.");
            }

            return result;
        }
        catch(Exception e) {
            throw new BootstrapConfigurationError("Failed to read configuration (namespace="+namespace+") from database:"+e);
        }
        finally {
            Functions.closeJdbc(con, st, rs);
        }
    }

    /**
     * Save a configuration into the database.
     * All namespace records have a validfrom and a validto date stamp. A record
     * with validto set to zero indicates the latest config. This method will
     * update the current latest record, setting its validto to now. It will then
     * insert a new record with validFrom set to now+1ms, and validTo set to zero.<p>
     * If either the database or table specified in loader.properties does not
     * exist on the first attempt to save a configuration, this method will
     * attempt to create both. For this to work the DB user specified in loader.properties
     * must have the necessary 'GRANTS' for table and database creation.
     * @param namespace The namespace the configuration is associated with.
     * @param config The configuration to be saved.
     * @throws BootstrapConfigurationError if the configuration cannot be serialised, or the database access fails.
     */
    @Override
    public void saveConfiguration(String namespace, Configuration config) {

        // check that the database and table exist, and create it if it doesn't.
        if (!isConfigTablePresent()) {
            createConfigTable();
        }

        config.setNamespace(namespace);

        Connection con=openConnection();
        PreparedStatement st=null;

        try {
            // First update the existing record (if there is one) setting its validto to now.
            // If the config we're saving has a validFrom then make sure we only update that
            // entry - that way we'll avoid updating a record that someone else has already
            // updated (i.e. avoid a collision).
            long ts=new VersionEffectiveDate().getTime();
            StringBuffer sql=new StringBuffer();

            sql.append("UPDATE "+getLoaderParams().getProperty("table"));
            sql.append(" SET VALIDTO=? WHERE NAMESPACE=? AND VALIDTO=0");
            if (config.getValidFrom()!=null) {
                sql.append(" AND VALIDFROM=?");
            }

            st=con.prepareStatement(sql.toString());
            st.setLong(1, ts);
            st.setString(2, namespace);
            if (config.getValidFrom()!=null) {
                st.setLong(3, config.getValidFrom().getTime());
            }

            int rowsUpdated = st.executeUpdate();

            if (rowsUpdated == 0) {
                if (config.getValidFrom()!=null) {
                    throw new ConfigurationUpdateCollisionError(namespace);
                }
                ts = 0;
            }

            st.close();
            st=null;

            if (config.getValidTo() == null) {
                // unmarshal the config object into a serialised byte array
                ByteArrayOutputStream baos=new ByteArrayOutputStream();
                ObjectOutputStream oos=new ObjectOutputStream(baos);
                oos.writeObject(config);
                oos.flush();

                // insert the new record (validto=0);
                st=con.prepareStatement(
                    "INSERT INTO "+getLoaderParams().getProperty("table")+
                    " (NAMESPACE, MANAGER, CONFIGURATION, VALIDFROM, VALIDTO, WHO, VERSION) VALUES(?, ?, ?, ?, 0, ?, ?)");
                st.setString(1, namespace);
                st.setString(2, config.getManager());
                st.setBytes(3, baos.toByteArray());
                st.setLong(4, ts);
                st.setString(5, config.getWho());
                st.setString(6, config.getVersion());

                st.executeUpdate();
            }
        }
        catch(SQLException e) {
            throw new BootstrapConfigurationError("JDBC write access for namespace: "+namespace+" failed with exception: "+e);
        }
        catch(IOException e) {
            throw new BootstrapConfigurationError("Failed to serialize configuration: "+e);
        }
        finally {
            Functions.closeJdbc(con, st, null);
        }
    }

    /**
     * Return a list of all the namespaces in the database which have a validTo
     * date of 0.
     * @return A list of currently defined namespaces as Strings
     */
    @Override
    public Collection<String> getNamespaces() {
        Collection<String> ret=new ArrayList<>();

        Connection con=openConnection();
        PreparedStatement st=null;
        ResultSet rs=null;

        try {
            // first try loading the latest record (validto=0).
            st=con.prepareStatement(
                "SELECT namespace FROM "+getLoaderParams().get("table")+" WHERE validto=0");

            rs=st.executeQuery();

            while(rs.next()) {
                ret.add(rs.getString("NAMESPACE"));
            }

            return ret;
        }
        catch(SQLException e) {
            throw new BootstrapConfigurationError("JDBC read access fetching namespace list:"+e);
        }
        finally {
            Functions.closeJdbc(con, st, rs);
        }
    }

    /**
     * Return a list of details of all the namespaces in the database which have a validTo
     * date of 0.
     * @return A list of currently defined namespaces as Strings
     */
    @Override
    public Collection<ConfigurationSummary> getNamespacesSummary() {
        Collection<ConfigurationSummary> ret=new ArrayList<>();

        Connection con=openConnection();
        PreparedStatement st=null;
        ResultSet rs=null;

        try {
            // first try loading the latest record (validto=0).
            st=con.prepareStatement(
                "SELECT namespace,manager,validFrom,validTo,who,version FROM "+getLoaderParams().get("table")+" WHERE validto=0 ORDER BY namespace");

            rs=st.executeQuery();

            ConfigurationSummary detail;

            while(rs.next()) {
                detail=new ConfigurationSummary(rs.getString("NAMESPACE"),
                                               rs.getString("MANAGER"),
                                               new Date(rs.getLong("validFrom")),
                                               null,
                                               rs.getString("who"),
                                               rs.getString("version"));
                ret.add(detail);
            }

            return ret;
        }
        catch(SQLException e) {
            throw new BootstrapConfigurationError("JDBC read access fetching namespace list:"+e);
        }
        finally {
            Functions.closeJdbc(con, st, rs);
        }
    }

    /**
     * Return details of a namespaces versions.
     * @return A collection of instances of {@link ConfigurationSummary ConfigurationSummary} representing
     * the history of the specified namespace.
     */
    @Override
    public Collection<ConfigurationSummary> getNamespacesHistorySummary(String namespace) {
        Collection<ConfigurationSummary> ret=new ArrayList<>();

        Connection con=openConnection();
        PreparedStatement st=null;
        ResultSet rs=null;

        try {
            st=con.prepareStatement(
                "SELECT manager,validFrom,validTo,who,version FROM "+getLoaderParams().get("table")+" WHERE namespace=? ORDER BY validFrom");

            st.setString(1, namespace);

            rs=st.executeQuery();

            ConfigurationSummary detail;

            while(rs.next()) {
                detail=new ConfigurationSummary(namespace,
                                                rs.getString("MANAGER"),
                                                new Date(rs.getLong("validFrom")),
                                                new Date(rs.getLong("validTo")),
                                                rs.getString("who"),
                                                rs.getString("version"));
                ret.add(detail);
            }

            return ret;
        }
        catch(SQLException e) {
            throw new BootstrapConfigurationError("JDBC read access fetching namespace list:"+e);
        }
        finally {
            Functions.closeJdbc(con, st, rs);
        }
    }

    /**
     * When the configuration handler is asked to "reset", it passes that request onto
     * the loader currently in use. The loader should reset any internal state in this
     * method.
     */
    @Override
    public void reset() {
        configTableExists=null;
    }

    /**
     * Delete the repository holding configuration information. This not only removes all
     * configuration information {@link #purgeAllConfigurations} but also removes the
     * repository itself.<p>
     * <b>NOTE: ALL CONFIGURATION INFORMATION WILL BE LOST!</b>
     * <p>In the context of the JDBCConfigurationLoader this simply means dropping the
     * config table.
     */
    @Override
    public void deleteConfigurationRepository() {
        Connection con=openConnection();
        Statement st=null;

        try {
            st=con.createStatement();
            st.execute("DROP TABLE "+getLoaderParams().get("table"));
        }
        catch(SQLException e) {
            throw new BootstrapConfigurationError("JDBC table drop failed:"+e);
        }
        finally {
            Functions.closeJdbc(con, st, null);
        }
    }

    /**
     * Delete ALL configuration information. This will include all historical configuration
     * information. <p>
     * <b>NOTE: ALL CONFIGURATION INFORMATION WILL BE LOST!</b>
     * In the context of the JDBCConfigurationLoader this means delete all the records from
     * the configuration table.
     */
    @Override
    public void purgeAllConfigurations() {
        Connection con=openConnection();
        Statement st=null;

        try {
            st=con.createStatement();
            st.execute("DELETE FROM "+getLoaderParams().get("table"));
        }
        catch(SQLException e) {
            throw new BootstrapConfigurationError("JDBC table drop failed:"+e);
        }
        finally {
            Functions.closeJdbc(con, st, null);
        }
    }

    @Override
    public boolean isConfigurationRepositoryCreated() {
        return isConfigTablePresent() && isConfigTablePopulated();
    }
}
