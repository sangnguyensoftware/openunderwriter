package com.ail.core.key;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ail.core.Core;
import com.ail.core.CoreContext;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.configure.Configuration;
import com.ail.core.configure.Parameter;
import com.ail.core.key.GenerateUniqueKeyService.GenerateUniqueKeyArgument;

@SuppressWarnings("deprecation")
@RunWith(PowerMockRunner.class)
@PrepareForTest(CoreContext.class)
public class ConfigurationKeyGeneratorServiceTest {
    private Core mockCore;
    private GenerateUniqueKeyArgument mockArgs;
    private Configuration mockConfiguration;
    private ConfigurationKeyGeneratorService SUT;
    private Parameter mockNextNumberParameter;
    private Parameter mockBlockSizeParameter;

    @Before
    public void setupMocks() {
        mockCore = mock(Core.class);
        mockArgs = mock(GenerateUniqueKeyArgument.class);
        mockConfiguration = mock(Configuration.class);
        mockNextNumberParameter = mock(Parameter.class);
        mockBlockSizeParameter = mock(Parameter.class);

        ConfigurationKeyGeneratorService GUKS=new ConfigurationKeyGeneratorService();
        GUKS.setCore(mockCore);
        GUKS.setArgs(mockArgs);
        SUT=spy(GUKS);
        doReturn(mockConfiguration).when(SUT).getConfiguration();
        doNothing().when(SUT).setConfiguration(mockConfiguration);

        when(mockBlockSizeParameter.getValue()).thenReturn("20");
        when(mockNextNumberParameter.getValue()).thenReturn("20");
        when(mockNextNumberParameter.getNamespace()).thenReturn("dummynamespace");

        when(mockConfiguration.findParameter(eq("KeyNextNumber"))).thenReturn(mockNextNumberParameter);
        when(mockConfiguration.findParameter(eq("KeyBlockSize"))).thenReturn(mockBlockSizeParameter);

        when(mockArgs.getKeyIdArg()).thenReturn("Key");

        when(mockCore.getParameter("KeyNextNumber")).thenReturn(mockNextNumberParameter);

        PowerMockito.mockStatic(CoreContext.class);

        when(CoreContext.getProductName()).thenReturn("product");
    }

    @Test(expected=PreconditionException.class)
    public void testKeyTypeNullPrecondidition() throws Exception {
        when(mockArgs.getKeyIdArg()).thenReturn(null);
        SUT.invoke();
    }

    @Test(expected=PreconditionException.class)
    public void testKeyTypeZeroLengthPrecondidition() throws Exception {
        when(mockArgs.getKeyIdArg()).thenReturn("");
        SUT.invoke();
    }

    @Test(expected=PreconditionException.class)
    public void testProductTypeIdPrecondition() throws Exception {
        when(CoreContext.getProductName()).thenReturn(null);
        SUT.invoke();
    }

    @Test(expected=PostconditionException.class)
    public void testKeyIdPostcondition() throws Exception {
        when(mockArgs.getKeyRet()).thenReturn(null);
        SUT.invoke();
    }

    @Test
    @Ignore
    public void testUniqueNumber() throws Exception {
        SUT.invoke();
        verify(mockArgs, times(1)).setKeyRet(21L);
        SUT.invoke();
        verify(mockArgs, times(1)).setKeyRet(22L);
    }
}
