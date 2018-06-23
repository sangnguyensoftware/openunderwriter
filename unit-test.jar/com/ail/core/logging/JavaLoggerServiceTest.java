/* Copyright Applied Industrial Logic Limited 2002. All rights Reserved */
/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later 
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51 
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.ail.core.logging;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ail.core.logging.LoggingService.LoggingArgument;

@RunWith(MockitoJUnitRunner.class)
public class JavaLoggerServiceTest {

    @Mock
    private JavaLoggerService sut;
    @Mock
    private Logger logger;
    @Mock
    private LoggingArgument args;
    @Mock
    private Throwable throwable;

    @Before
    public void setup() {
        sut = spy(new JavaLoggerService());
        sut.setArgs(args);
        doReturn(logger).when(sut).getLogger();
        doReturn(throwable).when(args).getCause();
    }

    @Test
    public void shouldPassCauseOnWhenItIsDefine() {
        sut.invoke();
        verify(logger, times(1)).log(any(Level.class), any(String.class), eq(throwable));
        verify(logger, times(0)).log(any(Level.class), any(String.class));
    }

    @Test
    public void shouldNotPassCauseOnWhenItIsDefine() {
        doReturn(null).when(args).getCause();
        sut.invoke();
        verify(logger, times(0)).log(any(Level.class), any(String.class), any(Throwable.class));
        verify(logger, times(1)).log(any(Level.class), any(String.class));
    }

    @Test
    public void testMappingOfDebugSeverity() {
        doReturn(Severity.DEBUG).when(args).getSeverity();
        sut.invoke();
        verify(logger).log(eq(Level.FINEST), any(String.class), any(Throwable.class));
    }

    @Test
    public void testMappingOfInfoSeverity() {
        doReturn(Severity.INFO).when(args).getSeverity();
        sut.invoke();
        verify(logger).log(eq(Level.INFO), any(String.class), any(Throwable.class));
    }

    @Test
    public void testMappingOfWarningSeverity() {
        doReturn(Severity.WARNING).when(args).getSeverity();
        sut.invoke();
        verify(logger).log(eq(Level.WARNING), any(String.class), any(Throwable.class));
    }

    @Test
    public void testMappingOfErrorSeverity() {
        doReturn(Severity.ERROR).when(args).getSeverity();
        sut.invoke();
        verify(logger).log(eq(Level.SEVERE), any(String.class), any(Throwable.class));
    }

    @Test
    public void testMappingOfFatalSeverity() {
        doReturn(Severity.FATAL).when(args).getSeverity();
        sut.invoke();
        verify(logger).log(eq(Level.SEVERE), any(String.class), any(Throwable.class));
    }
}

