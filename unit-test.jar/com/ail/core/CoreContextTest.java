package com.ail.core;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(CoreContext.class)
public class CoreContextTest {

    @Mock
    ActionRequest actionRequest;
    @Mock
    ActionResponse actionResponse;
    @Mock
    PortletConfig portletConfig;
    
    @Before
    public void setup() {
        PowerMockito.mockStatic(CoreContext.class);
    }
    
    @Test
    public void testContextInitialisation() {
        CoreContext.initialise(actionRequest, actionResponse, portletConfig);
    }
}
