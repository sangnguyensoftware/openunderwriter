package com.ail.core.user.liferay;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
import com.ail.core.Type;
import com.ail.core.TypeXPathException;
import com.ail.core.user.GuessUsernameService.GuessUsernameArgument;

@RunWith(PowerMockRunner.class)
@PrepareForTest(CoreContext.class)
public class GuessUsernameServiceTest {

    private static final String RESULT_THREE = "result-three";
    private static final String RESULT_TWO = "result-two";
    private static final String RESULT_ONE = "result-one";
    private static final String XPATH_OPTION_THREE = "xpath-option-three";
    private static final String XPATH_OPTION_TWO = "xpath-option-two";
    private static final String XPATH_OPTION_ONE = "xpath-option-one";
    
    private GuessUsernameService sut;
    @Mock
    private GuessUsernameArgument args;
    @Mock
    private Type model;
    @Mock
    private Type sessionTemp;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        sut = new GuessUsernameService();
        sut.setArgs(args);
        
        doReturn(model).when(args).getModelArg();
        doReturn(XPATH_OPTION_ONE+","+XPATH_OPTION_TWO+","+XPATH_OPTION_THREE).when(args).getUsernameLocationsArg();

        doReturn(RESULT_ONE).when(model).xpathGet(eq(XPATH_OPTION_ONE), eq(String.class));
        doReturn(RESULT_TWO).when(model).xpathGet(eq(XPATH_OPTION_TWO), eq(String.class));
        doReturn(RESULT_THREE).when(model).xpathGet(eq(XPATH_OPTION_THREE), eq(String.class));
        
        PowerMockito.mockStatic(CoreContext.class);
        when(CoreContext.getSessionTemp()).thenReturn(sessionTemp);
    }

    public void checkThatNullModelIsIgnored() throws BaseException {
        doReturn(null).when(args).getModelArg();
        sut.invoke();
    }

    @Test
    public void checkThatXpathOneTakesPrcedence() throws BaseException {
        sut.invoke();
        verify(args, times(1)).setUsernameRet(eq(RESULT_ONE));
    }

    @Test
    public void checkThatXpathTwoTakesPrcedenceWhenOneThrowsAnException() throws BaseException {
        TypeXPathException exception=mock(TypeXPathException.class);
        doThrow(exception).when(model).xpathGet(eq(XPATH_OPTION_ONE), eq(String.class));
        sut.invoke();
        verify(args, times(1)).setUsernameRet(eq(RESULT_TWO));
    }
    
    @Test
    public void checkThatXpathThreeTakesPrcedenceWhenOneAndTwohrowExceptions() throws BaseException {
        TypeXPathException exception=mock(TypeXPathException.class);
        doThrow(exception).when(model).xpathGet(eq(XPATH_OPTION_ONE), eq(String.class));
        doThrow(exception).when(model).xpathGet(eq(XPATH_OPTION_TWO), eq(String.class));
        sut.invoke();
        verify(args, times(1)).setUsernameRet(eq(RESULT_THREE));
    }
    
    @Test
    public void whenAllAttemptsFailTheResultShouldBeAnAmptyString() throws BaseException {
        doReturn(null).when(model).xpathGet(eq(XPATH_OPTION_ONE), eq(String.class));
        doReturn(null).when(model).xpathGet(eq(XPATH_OPTION_TWO), eq(String.class));
        doReturn(null).when(model).xpathGet(eq(XPATH_OPTION_THREE), eq(String.class));
        sut.invoke();
        verify(args, times(1)).setUsernameRet(eq(""));
    }
}
