package com.ail.core.configure;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.mysql.jdbc.Connection;

public class JDBCConfigurationLoaderTest {

    private static final String CONFIG_TABLE = "CONFIG_TABLE";

    private static final String DUMMY_NAMESPACE = "DUMMY_NAMESPACE";

    JDBCConfigurationLoader sut;

    private Configuration configuration;

    @Mock
    private Connection connection;
    @Mock
    private Properties params;
    @Mock
    private PreparedStatement updateStatement;
    @Mock
    private PreparedStatement insertStatement;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        sut = spy(new JDBCConfigurationLoader());

        configuration = new Configuration();

        doReturn(connection).when(sut).openConnection();
        doReturn(true).when(sut).isConfigTablePresent();
        doReturn(true).when(sut).isConfigTablePopulated();
        doReturn(params).when(sut).getLoaderParams();
        doReturn(CONFIG_TABLE).when(params).getProperty(eq("table"));

        doReturn(updateStatement).when(connection).prepareStatement(eq("UPDATE CONFIG_TABLE SET VALIDTO=? WHERE NAMESPACE=? AND VALIDTO=0"));
        doReturn(insertStatement).when(connection).prepareStatement(eq("INSERT INTO CONFIG_TABLE (NAMESPACE, MANAGER, CONFIGURATION, VALIDFROM, VALIDTO, WHO, VERSION) VALUES(?, ?, ?, ?, 0, ?, ?)"));
    }

    @Test
    public void firstSaveForNamespacedMustSetValidFromToZero() throws SQLException {
        configuration.setValidFrom(null);
        doReturn(0).when(updateStatement).executeUpdate();

        sut.saveConfiguration(DUMMY_NAMESPACE, configuration);

        verify(insertStatement).setLong(eq(4), eq(0L));
    }

}
