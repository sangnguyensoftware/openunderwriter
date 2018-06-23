package com.ail.pageflow.service;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.util.Locale;

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
import com.ail.core.ThreadLocale;
import com.ail.core.context.RequestWrapper;
import com.ail.pageflow.ExecutePageActionService;
import com.ail.pageflow.PageFlow;
import com.ail.pageflow.PageFlowContext;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ PageFlowContext.class, CoreContext.class, SelectPageFlowService.class, ThreadLocale.class })
public class SelectPageFlowServiceTest {
    private static final String TEST_PRODUCT_NAME = "TEST PRODUCT";
    private static final String TEST_PAGEFLOW_NAME = "TEST PAGEFLOW";

    SelectPageFlowService sut;
    Locale locale=Locale.CANADA;

    @Mock
    CoreProxy coreProxy;
    @Mock
    PageFlow pageflow;
    @Mock
    ExecutePageActionService.ExecutePageActionArgument argument;
    @Mock
    RequestWrapper requestWrapper;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(PageFlowContext.class);
        PowerMockito.mockStatic(CoreContext.class);
        PowerMockito.mockStatic(ThreadLocale.class);

        sut=new SelectPageFlowService();
        sut.setArgs(argument);

        whenNew(CoreProxy.class).withAnyArguments().thenReturn(coreProxy);

        when(PageFlowContext.getRequestWrapper()).thenReturn(requestWrapper);
        when(PageFlowContext.getCoreProxy()).thenReturn(coreProxy);
        when(PageFlowContext.getPageFlow()).thenReturn(pageflow);
        when(PageFlowContext.getPageFlowName()).thenReturn(TEST_PAGEFLOW_NAME);
        when(PageFlowContext.getProductName()).thenReturn(TEST_PRODUCT_NAME);
        when(PageFlowContext.getRequestLocale()).thenReturn(locale);
        when(coreProxy.newProductType(eq(TEST_PRODUCT_NAME), eq(TEST_PAGEFLOW_NAME), eq(PageFlow.class))).thenReturn(pageflow);
    }

    @Test
    public void testNullPageFlowNameIsIgnored() throws BaseException {
        when(PageFlowContext.getPageFlowName()).thenReturn((String) null);
        sut.invoke();
    }

    @Test
    public void testNullProductNameIsIgnored() throws BaseException {
        when(PageFlowContext.getProductName()).thenReturn((String) null);
        sut.invoke();
        PowerMockito.verifyStatic(never());
        PageFlowContext.setPageFlow(any(PageFlow.class));
    }

    @Test
    public void testPageflowAddedToPageflowContext() throws BaseException {
        sut.invoke();
        PowerMockito.verifyStatic(times(1));
        PageFlowContext.setPageFlow(eq(pageflow));
    }

    @Test
    public void testLocaleIsSetInContext() throws BaseException {
        sut.invoke();
        PowerMockito.verifyStatic(times(1));
        ThreadLocale.setThreadLocale(eq(locale));
    }

    @Test
    public void shouldSetNextPageWhenCurrentPageIsNull() throws BaseException {
        doReturn(null).when(pageflow).getCurrentPage();
        doReturn("TEST_PAGE").when(pageflow).getStartPage();
        sut.invoke();
        verify(pageflow).setNextPage(eq("TEST_PAGE"));
    }

    @Test
    public void shouldNotSetNextPageWhenCurrentPageIsNotNull() throws BaseException {
        doReturn("TEST_PAGE").when(pageflow).getCurrentPage();
        doReturn("TEST_PAGE").when(pageflow).getStartPage();
        sut.invoke();
        verify(pageflow, never()).setNextPage(anyString());
    }
}
