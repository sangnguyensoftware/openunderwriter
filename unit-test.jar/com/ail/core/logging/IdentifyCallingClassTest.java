package com.ail.core.logging;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.ail.core.Core;
import com.ail.core.CoreProxy;

public class IdentifyCallingClassTest {

    private IdentifyCallingClass sut;
    
    @Before
    public void setup() {
        sut = spy(new IdentifyCallingClass());
    }

    @Test
    public void shouldFindCoreLoggerUsers() {
        Class<?>[] classContext=(Class<?>[]) Arrays.asList(
                    IdentifyCallingClassTest.class, 
                    IdentifyCallingClassTest.class, 
                    Core.class, 
                    IdentifyCallingClass.class).toArray();
        
        doReturn(classContext).when(sut).getStack();
        assertEquals(IdentifyCallingClass.class, sut.callingClass());
    }

    @Test
    public void shouldFindCoreProxyLoggerUsers() {
        Class<?>[] classContext=(Class<?>[]) Arrays.asList(
                    IdentifyCallingClassTest.class, 
                    Core.class, 
                    CoreProxy.class, 
                    IdentifyCallingClass.class).toArray();
        
        doReturn(classContext).when(sut).getStack();
        assertEquals(IdentifyCallingClass.class, sut.callingClass());
    }

    @Test(expected=IllegalStateException.class)
    public void shouldSpotIllegalUsage() {
        Class<?>[] classContext=new Class<?>[0];
        
        doReturn(classContext).when(sut).getStack();
        sut.callingClass();
    }
}
