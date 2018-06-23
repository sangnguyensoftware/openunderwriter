package com.ail.pageflow;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.CoreProxy;
import com.ail.core.Type;
import com.ail.core.product.ProductUrlToExternalUrlService.ProductUrlToExternalUrlCommand;
import com.ail.core.urlhandler.product.FileNotFoundPostcondition;

@RunWith(PowerMockRunner.class)
@PrepareForTest(PageFlowContext.class)
public class PageScriptTest {
    private static final String PRODUCT_NAME = "PRODUCT_NAME";
    private static final String PRODUCT_URL = "http://PRODUCT_URL";

    PageScript sut;

    @Mock
    private Type model;
    @Mock
    private CoreProxy coreProxy;
    @Mock
    private ProductUrlToExternalUrlCommand productUrlToExternalUrlCommand;
    
    @Before()
    public void setup() {
        MockitoAnnotations.initMocks(this);

        PowerMockito.mockStatic(CoreContext.class);

        when(CoreContext.getProductName()).thenReturn(PRODUCT_NAME);
        when(CoreContext.getCoreProxy()).thenReturn(coreProxy);
        
        sut=new PageScript();

        doReturn(productUrlToExternalUrlCommand).when(coreProxy).newCommand(eq(ProductUrlToExternalUrlCommand.class));
        doReturn("http://localhost:8080/this/is/my/path").when(productUrlToExternalUrlCommand).getExternalUrlRet();
    }

    @Test
    public void ensureThatPageRelativeUrlIsReturnedForProductContent() {
        sut.setUrl(PRODUCT_URL);
        
        sut.initialise(model);
        
        assertThat(sut.getCanonicalUrl(), is("/this/is/my/path"));
    }

    @Test
    public void ensureThatAbsoluteUrlIsReturnedForNonProductContent() throws BaseException {
        sut.setUrl(PRODUCT_URL);
        
        doThrow(new FileNotFoundPostcondition("")).when(productUrlToExternalUrlCommand).invoke();
        
        sut.initialise(model);
        
        assertThat(sut.getCanonicalUrl(), is(PRODUCT_URL));
    }
}
