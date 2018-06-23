package com.ail.core.persistence.hibernate;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.engine.spi.SessionImplementor;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ail.core.Type;

public class XMLBlobTest {
    XMLBlob sut;

    @Mock
    Type type;
    @Mock
    Type clonedType;
    @Mock
    PreparedStatement st;
    @Mock
    SessionImplementor session;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        sut=new XMLBlob();

        doReturn(clonedType).when(type).clone();
    }

    @Test
    public void checkThatNullsAreHandledInDeepCopy() {
        Object result = sut.deepCopy(null);
        assertThat(result, is((Object)null));
    }

    @Test
    public void checkThatTypeBasedClassesClone() throws Throwable {
        Object result = sut.deepCopy(type);
        assertTrue(result==clonedType);
        assertThat(result, instanceOf(Type.class));
        verify(type).clone();
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void checkThatListBasedClassesClone() {
        List<Type> list=new ArrayList<>();

        list.add(type);

        Object result = sut.deepCopy(list);

        assertTrue(result != list);
        assertThat(result, instanceOf(List.class));

        List resultList=(List)result;
        assertThat(resultList.size(), is(1));
        assertThat(resultList.get(0), instanceOf(Type.class));
        assertTrue("List element must be cloned from original.", resultList.get(0) == clonedType);
    }

    @Test
    public void checkThatSetBasedClassesClone() {
        Set<Type> set=new HashSet<>();

        set.add(type);

        Object result = sut.deepCopy(set);

        assertTrue(result != set);
        assertThat(result, instanceOf(Set.class));

        @SuppressWarnings("unchecked")
        Set<Type> resultSet=(Set<Type>)result;
        assertThat(resultSet.size(), is(1));
        assertThat(resultSet.toArray()[0], instanceOf(Type.class));
        assertTrue("Set element must be cloned from original.", resultSet.toArray()[0] == clonedType);
    }

    @Test
    public void checkThatMapBasedClassesClone() {
        Map<String,Type> map = new HashMap<>();

        map.put("dummy", type);

        Object result = sut.deepCopy(map);

        assertTrue(result != map);
        assertThat(result, instanceOf(Map.class));

        @SuppressWarnings("unchecked")
        Map<String,Type> resultMap=(Map<String,Type>)result;
        assertThat(resultMap.size(), is(1));
        assertThat(resultMap.keySet(), contains("dummy"));
        assertTrue("Result may must contain the clone", resultMap.get("dummy")==clonedType);

    }
}
