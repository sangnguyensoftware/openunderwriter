package com.ail.core.persistence.hibernate;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ail.core.PreconditionException;
import com.ail.core.persistence.QueryException;
import com.ail.core.persistence.QueryService.QueryArgument;

@RunWith(PowerMockRunner.class)
@PrepareForTest({HibernateSessionBuilder.class})
public class HibernateQueryServiceTest {

    private static final String DUMMY_QUERY_NAME = "DUMMY_QUERY_NAME";

    private HibernateQueryService sut;

    @Mock
    private QueryArgument arg;
    @Mock
    private Session session;
    @Mock
    private SessionFactory sessionFactory;
    @Mock
    private Query query;
    @Mock
    private List<Object> queryResults;
    @Mock
    private Object queryObject;
    Object[] queryParams = new Object[0];

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        mockStatic(HibernateSessionBuilder.class);

        sut = new HibernateQueryService();
        sut.setArgs(arg);

        when(HibernateSessionBuilder.getSessionFactory()).thenReturn(sessionFactory);
        when(sessionFactory.getCurrentSession()).thenReturn(session);

        doReturn(DUMMY_QUERY_NAME).when(arg).getQueryNameArg();
        doReturn(queryParams).when(arg).getQueryArgumentsArg();

        doReturn(query).when(session).getNamedQuery(eq(DUMMY_QUERY_NAME));
        doReturn(1).when(queryResults).size();
        doReturn(queryObject).when(queryResults).get(eq(0));
    }

    @Test
    public void checkThatQueryListIsOnlyCalledOnce() throws PreconditionException, QueryException {
        sut.invoke();
        verify(query, times(1)).list();
    }

}
