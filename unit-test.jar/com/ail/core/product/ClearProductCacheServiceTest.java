package com.ail.core.product;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.ail.core.Core;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.product.ClearProductCacheService.ClearProductCacheArgument;

public class ClearProductCacheServiceTest {
    private ClearProductCacheService sut = null;
    private Core mockCore = null;
    private ClearProductCacheArgument mockArgs = null;

    @Before
    public void setupSUT() {
        mockCore = mock(Core.class);
        mockArgs = mock(ClearProductCacheArgument.class);

        sut = new ClearProductCacheService();
        sut.setCore(mockCore);
        sut.setArgs(mockArgs);
    }

    @Test(expected=PreconditionException.class)
    public void testNullProductName() throws Exception {
        when(mockArgs.getProductNameArg()).thenReturn(null);
        sut.invoke();
    }

    @Test(expected=PreconditionException.class)
    public void testZeroLengthProductName() throws Exception {
        when(mockArgs.getProductNameArg()).thenReturn("");
        sut.invoke();
    }

    @Test(expected=PostconditionException.class)
    public void testNullResultFromCore() throws Exception {
        when(mockArgs.getProductNameArg()).thenReturn("rubbish");
        when(mockArgs.getNamespacesRet()).thenReturn(null);
        sut.invoke();
    }

    @Test
    public void testHappyPath() throws Exception {
        List<String> sampleResult=Arrays.asList("ONE", "TWO", "THREE");
        when(mockArgs.getProductNameArg()).thenReturn("AIL.MADEUP.NAMESPACE");
        when(mockCore.clearConfigurationCache(eq("AIL.MADEUP.NAMESPACE.Registry"))).thenReturn(sampleResult);
        sut.invoke();
        verify(mockArgs).setNamespacesRet(eq(sampleResult));
    }
}
