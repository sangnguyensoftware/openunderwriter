package com.ail.core;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class TypeClonerTest {
    public TypeCloner sut;
    
    @Mock 
    Type source;
    @Mock
    Type destination;
    
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        sut=new TypeCloner(source, destination);
    }

    @Test
    public void shouldRelyOnClassCloneToCloneNumbers() throws SecurityException, NoSuchFieldException {

        Field field=TypeClonerTest.class.getDeclaredField("sut");
        
        assertTrue(sut.cloneWasHandledByJava(field, Number.class));
    }
}
