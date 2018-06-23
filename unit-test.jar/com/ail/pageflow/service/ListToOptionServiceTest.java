package com.ail.pageflow.service;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.contains;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.ail.core.BaseException;
import com.ail.core.PreconditionException;

@RunWith(MockitoJUnitRunner.class)
public class ListToOptionServiceTest {

    ListToOptionService sut = new ListToOptionService();

    @Mock
    ListToOptionService.ListToOptionArgument args;

    @Before
    public void setup() {
        sut.setArgs(args);

        doReturn(asList("one", "two", "three")).when(args).getOptionsArg();
        doReturn("two").when(args).getSelectedArg();
        doReturn(false).when(args).getExcludeUnknownArg();
    }

    @Test(expected = PreconditionException.class)
    public void shouldCheckListNotNullPreconditon() throws BaseException {
        doReturn(null).when(args).getOptionsArg();
        sut.invoke();
    }

    @Test
    public void shouldReturnOptionListForHappyPath() throws BaseException {
        sut.invoke();
        verify(args).setOptionMarkupRet(eq("<option disabled='disabled' value='?'>i18n_?</option><option value='one'>one</option><option value='two' selected='selected' >two</option><option value='three'>three</option>"));
    }

    @Test
    public void shouldNotIncludeUnknownArg() throws BaseException {
        doReturn(true).when(args).getExcludeUnknownArg();
        sut.invoke();
        verify(args).setOptionMarkupRet(eq("<option value='one'>one</option><option value='two' selected='selected' >two</option><option value='three'>three</option>"));
    }
    
    @Test
    public void unknownOptionShouldBeDisabled() throws BaseException {
        doReturn("unknown").when(args).getUnknownOptionArg();
        sut.invoke();
        verify(args).setOptionMarkupRet(contains("disabled='disabled'"));
    }
}

