package com.ail.pageflow.service;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ail.core.BaseException;
import com.ail.core.Core;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.configure.Configuration;
import com.ail.core.configure.Type;
import com.ail.core.configure.Types;
import com.ail.pageflow.PageFlow;

public class ListPageFlowsForProductServiceTest {

    ListPageFlowsForProductService sut;

    @Mock
    ListPageFlowsForProductService.ListPageFlowsForProductArgument argument;
    @Mock
    Core core;
    @Mock
    Configuration configuration;
    @Mock
    Configuration parentConfiguration;
    @Mock
    Types types;
    @Mock
    Type typeOne;
    @Mock
    Type typeTwo;
    @Mock
    Type typeThree;
    @Mock
    Types parentTypes;
    @Mock
    Type parentType;
    
    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        sut=spy(new ListPageFlowsForProductService());
        sut.setArgs(argument);
        
        doReturn("My.Product.Name").when(argument).getProductNameArg();
        doReturn(core).when(sut).getCore();
        
        when(core.getConfiguration()).thenReturn(configuration, parentConfiguration);

        // setup local configuration's types
        doReturn(types).when(configuration).getTypes();
        doReturn("local.configuration").when(configuration).getParentNamespace();
        doReturn(3).when(types).getTypeCount();
        doReturn(typeOne).when(types).getType(eq(0));
        doReturn(typeTwo).when(types).getType(eq(1));
        doReturn(typeThree).when(types).getType(eq(2));

        doReturn("com.ail.not.a.pageflow").when(typeOne).getKey();
        
        doReturn(PageFlow.class.getName()).when(typeTwo).getKey();
        doReturn("TypeTwo").when(typeTwo).getName();

        doReturn(PageFlow.class.getName()).when(typeThree).getKey();
        doReturn("TypeThree").when(typeThree).getName();

        // setup parent configuration's types
        doReturn(parentTypes).when(parentConfiguration).getTypes();
        doReturn(1).when(parentTypes).getTypeCount();
        doReturn(parentType).when(parentTypes).getType(eq(0));

        doReturn(PageFlow.class.getName()).when(parentType).getKey();
        doReturn("ParentType").when(parentType).getName();
    }
    
    @Test(expected=PreconditionException.class)
    public void testProductNamePreconditionCatchesNullStrings() throws BaseException {
        doReturn(null).when(argument).getProductNameArg();
        sut.invoke();
    }

    @Test(expected=PreconditionException.class)
    public void testProductNamePreconditionCatchesEmptyStrings() throws BaseException {
        doReturn("").when(argument).getProductNameArg();
        sut.invoke();
    }
    
    @Test(expected=PreconditionException.class)
    public void testConfigurationPreconditionCatchesNulls() throws BaseException {
        doReturn(null).when(core).getConfiguration();
        sut.invoke();
    }
    
    @Test(expected=PostconditionException.class) 
    public void testPostconditionExceptionForNullResults() throws BaseException {
        doReturn(null).when(argument).getPageFlowNameRet();
        sut.invoke();
    }
    
    @Test
    public void testHappyPath() throws BaseException {
        sut.invoke();

        verify(typeOne, times(1)).getKey();
        verify(typeOne, never()).getName();
        verify(typeTwo, times(1)).getKey();
        verify(typeTwo, times(1)).getName();
        verify(typeThree, times(1)).getKey();
        verify(typeThree, times(1)).getName();
        verify(parentType, times(1)).getKey();
        verify(parentType, times(1)).getName();
    }
    
    @Test
    public void checkThatNullTypesListIsHandled() throws BaseException {
        doReturn(null).when(configuration).getTypes();
        sut.invoke();
    }
}
