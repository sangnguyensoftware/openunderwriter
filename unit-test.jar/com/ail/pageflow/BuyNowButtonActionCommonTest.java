package com.ail.pageflow;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import java.net.MalformedURLException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.liferay.portal.kernel.util.PropsUtil;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ PropsUtil.class })
public class BuyNowButtonActionCommonTest {
    private static final String MADE_UP_PORT = "1234";

    private static final String MADEUP_SERVER_NAME = "madeup.server.name";

    private BuyNowButtonActionCommon sut;

    @Before
    public void setUp() throws Exception {
        sut = mock(BuyNowButtonActionCommon.class, CALLS_REAL_METHODS);

        PowerMockito.mockStatic(PropsUtil.class);
        when(PropsUtil.get(eq("web.server.host"))).thenReturn(MADEUP_SERVER_NAME);
        when(PropsUtil.get(eq("web.server.http.port"))).thenReturn(MADE_UP_PORT);
    }

    @Test
    public void verifyThatExternalServerURLandPortAreRespectedWhenSet() throws MalformedURLException {
        String result;

        result = sut.externaliseURL("http://localhost:8080/file").toExternalForm();
        assertEquals("http://" + MADEUP_SERVER_NAME + ":" + MADE_UP_PORT + "/file", result);
    }

    @Test
    public void verifyThatExternalServerURLandPortAreIgnoredWhenNotSet() throws MalformedURLException {
        String result;

        // note: liferay sees to it that we get a -1 when the property is not
        // set
        when(PropsUtil.get(eq("web.server.http.port"))).thenReturn("-1");

        // Test for null values in settings
        when(PropsUtil.get(eq("web.server.host"))).thenReturn(null);
        result = sut.externaliseURL("http://localhost:8080/file").toExternalForm();
        assertEquals("http://localhost:8080/file", result);

        // Test for when settings return empty strings
        when(PropsUtil.get(eq("web.server.host"))).thenReturn("");
        result = sut.externaliseURL("http://localhost:8080/file").toExternalForm();
        assertEquals("http://localhost:8080/file", result);
    }

}
