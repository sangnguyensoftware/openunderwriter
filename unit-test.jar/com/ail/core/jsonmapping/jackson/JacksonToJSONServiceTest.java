package com.ail.core.jsonmapping.jackson;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.IOException;

import org.codehaus.jackson.map.ObjectWriter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ail.core.Attribute;
import com.ail.core.JSONException;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.jsonmapping.ToJSONService.ToJSONArgument;

@RunWith(PowerMockRunner.class)
@PrepareForTest({JacksonToJSONService.class, Mapper.class})
public class JacksonToJSONServiceTest {

    private static final String DUMMY_JSON_RESULT = "string";

    private JacksonToJSONService sut;

    @Mock
    private ToJSONArgument args;
    @Mock
    private Mapper mapper;
    @Mock
    private ObjectWriter objectWriter;
    @Mock
    private IOException ioException;

    private Attribute object;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        sut=new JacksonToJSONService();
        sut.setArgs(args);

        object = new Attribute("id", "value", "format");

        doReturn(object).when(args).getObjectArg();
        doReturn(DUMMY_JSON_RESULT).when(args).getJSONRet();

        whenNew(Mapper.class).withNoArguments().thenReturn(mapper);
        doReturn(objectWriter).when(mapper).buildWriter(null);
        doReturn(DUMMY_JSON_RESULT).when(objectWriter).writeValueAsString(eq(object));
    }

    @Test(expected=PreconditionException.class)
    public void objectArgMustNotBeNullOnEntry() throws Exception {
        doReturn(null).when(args).getObjectArg();
        sut.invoke();
    }

    @Test(expected=PostconditionException.class)
    public void jsonRetMustNotBeNullOnExit() throws Exception {
        doReturn(null).when(args).getJSONRet();
        sut.invoke();
    }

    @Test(expected=JSONException.class)
    public void checkThatExceptionFromJacksonIsTranslated() throws Exception {
        doThrow(ioException).when(objectWriter).writeValueAsString(eq(object));
        sut.invoke();
    }

    @Test
    public void happyPath() throws Exception {
        sut.invoke();
        verify(args).setJSONRet(eq(DUMMY_JSON_RESULT));
    }
}
