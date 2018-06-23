package com.ail.core.product;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ail.core.BaseException;
import com.ail.core.Core;
import com.ail.core.configure.UnknownNamespaceError;
import com.ail.core.product.RemoveProductService.RemoveProductArgument;

public class RemoveProductServiceTest {
    RemoveProductService sut;

    @Mock
    RemoveProductArgument args;
    @Mock
    Core core;
    @Mock
    ProductDetails productDetails;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        sut = spy(new RemoveProductService());
        sut.setArgs(args);
        sut.setCore(core);

        doReturn(productDetails).when(args).getProductDetailsArg();
        doReturn("test.product").when(productDetails).getName();
    }

    @Test
    public void UnkownNamesaceErrorShouldBeIgnore() throws BaseException {
        doThrow(UnknownNamespaceError.class).when(sut).getConfiguration();
        sut.invoke();
    }

}
