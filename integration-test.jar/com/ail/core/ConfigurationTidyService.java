package com.ail.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;

import com.ail.core.configure.AbstractConfigurationLoader;

public class ConfigurationTidyService {
    Properties properties;

    public ConfigurationTidyService clearNamespace(String namespace) {

        if (properties == null) {
            AbstractConfigurationLoader loader = AbstractConfigurationLoader.loadLoader();
            properties = loader.getLoaderParams();
        }

        // load the JDBC driver
        try {
            Class.forName(properties.getProperty("driver"));
            Connection con = DriverManager.getConnection(properties.getProperty("url") + properties.getProperty("databaseName"), properties);
            Statement st = con.createStatement();
            st.execute("DELETE FROM " + properties.getProperty("table"));
            st.close();
            con.close();
        } catch (Exception e) {
            if (e.toString().indexOf("doesn't exist") != -1) {
                // this is ok to ignore - the table isn't always there
            } else {
                System.err.println("ignored: " + e);
            }
        }

        return this;
    }

    public ConfigurationTidyService clearTestNamespace() {
        clearNamespace(CoreUserBaseCase.TEST_NAMESPACE);
        return this;
    }

    public ConfigurationTidyService clearCoreNamespace() {
        clearNamespace(Core.CORE_NAMESPACE);
        return this;
    }

    public ConfigurationTidyService clearCoreProxyNamespace() {
        clearNamespace(CoreProxy.DefaultNamespace);
        return this;
    }
}