package com.ail.pageflow.servlet;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.doThrow;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ail.core.persistence.hibernate.ForceSilentRollbackError;
import com.ail.pageflow.PageFlowContext;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PageFlowContext.class, PageFlowContextServletFilter.class})
public class PageFlowContextServletFilterTest {

    private PageFlowContextServletFilter sut;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        mockStatic(PageFlowContext.class);
    }

    @Test
    public void exceptionsInFinallyMustNotMaskProcessingExceptions() throws IOException, ServletException {
        Throwable initialiseException = new NullPointerException();
        Throwable cleanupException = new NullPointerException();

        sut = new PageFlowContextServletFilter();

        doThrow(initialiseException).when(PageFlowContext.class);
        PageFlowContext.initialise((HttpServletRequest)isNull(), (HttpServletResponse)isNull(), (ServletConfig)isNull());

        doThrow(cleanupException).when(PageFlowContext.class);
        PageFlowContext.clear();

        try {
            sut.doFilter(null, null, null);
        }
        catch(Throwable t) {
            assertThat(t, is(instanceOf(ServletException.class)));
            assertThat(t.getCause(), is(initialiseException));
        }
    }

    @Test
    public void exceptionsInFinallyAreThrownIfProcessingPassesWithoutExceptions() {
        Throwable cleanupException = new NullPointerException();
        FilterChain filterChain = mock(FilterChain.class);
        sut = new PageFlowContextServletFilter();

        doThrow(cleanupException).when(PageFlowContext.class);
        PageFlowContext.clear();

        try {
            sut.doFilter(null, null, filterChain);
        }
        catch(Throwable t) {
            assertThat(t, is(instanceOf(ServletException.class)));
            assertThat(t.getCause(), is(cleanupException));
        }
    }

    @Test(expected = ForceSilentRollbackError.class)
    public void testRollbackExceptionOnValidationFailure() throws Throwable {
        FilterChain filterChain = mock(FilterChain.class);

        sut = spy(new PageFlowContextServletFilter());

        doReturn(true).when(sut).validationRollbackRequired();

        sut.doFilter(null, null, filterChain);
    }
}
