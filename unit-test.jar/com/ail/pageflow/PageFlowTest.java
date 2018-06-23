package com.ail.pageflow;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ail.core.CoreContext;
import com.ail.core.Type;
import com.ail.core.context.ResponseWrapper;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PageFlowContext.class, CoreContext.class})
public class PageFlowTest {
    private static final String DUMMY_PAGE_ID = "DUMMY_PAGE_ID";

    PageFlow sut;

    @Mock
    Type model;
    @Mock
    AbstractPage page;
    @Mock
    ResponseWrapper responseWrapper;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        mockStatic(CoreContext.class);
        when(CoreContext.getResponseWrapper()).thenReturn(responseWrapper);

        sut = spy(new PageFlow());

        sut.getPages().add(page);

        doReturn(DUMMY_PAGE_ID).when(page).getId();
        doReturn(DUMMY_PAGE_ID).when(sut).getCurrentPage();
    }

    @Test
    public void confirmValidationErrorsArePassedOntoContext() {
        doReturn(true).when(page).processValidations(eq(model));

        boolean res = sut.processValidations(model);

        assertThat(res, is(true));
        verify(responseWrapper).setValidationErrorsFound(eq(true));;
    }

    @Test
    public void confirmValidationOkayIsPassedOntoContext() {
        doReturn(false).when(page).processValidations(eq(model));

        boolean res = sut.processValidations(model);

        assertThat(res, is(false));
        verify(responseWrapper).setValidationErrorsFound(eq(false));;
    }
}
