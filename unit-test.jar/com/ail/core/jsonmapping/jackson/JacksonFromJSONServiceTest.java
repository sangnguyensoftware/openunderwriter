package com.ail.core.jsonmapping.jackson;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import org.codehaus.jackson.map.ObjectReader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ail.core.Attribute;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.jsonmapping.FromJSONService.FromJSONArgument;

@RunWith(PowerMockRunner.class)
@PrepareForTest({JacksonFromJSONService.class, Mapper.class})
public class JacksonFromJSONServiceTest {

    private static final String DUMMY_JSON_STRING = "string";

    private JacksonFromJSONService sut;

    @Mock
    private FromJSONArgument args;
    @Mock
    private Attribute object;
    @Mock
    private Mapper mapper;
    @Mock
    private ObjectReader objectReader;
    @Mock
    private Attribute attribute;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        sut = new JacksonFromJSONService();

        sut.setArgs(args);

        doReturn(Attribute.class).when(args).getClassArg();
        doReturn(DUMMY_JSON_STRING).when(args).getJSONArg();
        doReturn(object).when(args).getObjectRet();

        whenNew(Mapper.class).withNoArguments().thenReturn(mapper);
        doReturn(objectReader).when(mapper).buildReader(eq(Attribute.class), eq((String)null));
        doReturn(attribute).when(objectReader).readValue(eq(DUMMY_JSON_STRING));
    }

    @Test(expected = PreconditionException.class)
    public void nullJSONArgIsNotAllowed() throws Exception {
        doReturn(null).when(args).getJSONArg();
        sut.invoke();
    }

    @Test(expected = PreconditionException.class)
    public void emptyJSONArgIsNotAllowed() throws Exception {
        doReturn(null).when(args).getJSONArg();
        sut.invoke();
    }

    @Test(expected = PreconditionException.class)
    public void nullClassArgIsNotAllowed() throws Exception {
        doReturn(null).when(args).getClassArg();
        sut.invoke();
    }

    @Test(expected = PostconditionException.class)
    public void nullObjectRetIsNotAllowed() throws Exception {
        doReturn(null).when(args).getObjectRet();
        sut.invoke();
    }

}
