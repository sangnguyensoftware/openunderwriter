package com.ail.core.audit.envers;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.Type;
import com.ail.core.audit.FetchVersionNumbersService.FetchVersionNumbersArgument;
import com.ail.core.persistence.hibernate.HibernateSessionBuilder;

@RunWith(PowerMockRunner.class)
@PrepareForTest({HibernateSessionBuilder.class, AuditReaderFactory.class})
public class EnversFetchVersionNumbersServiceTest extends EnversFetchVersionNumbersService {
    private static final long SYSTEM_ID = 1L;

    private EnversFetchVersionNumbersService sut;

    @Mock
    private FetchVersionNumbersArgument args;
    @Mock
    private Session session;
    @Mock
    private SessionFactory sessionFactory;
    @Mock
    private AuditReader auditReader;

    List<Number> revisions = new ArrayList<>();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        sut = new EnversFetchVersionNumbersService();
        sut.setArgs(args);

        mockStatic(HibernateSessionBuilder.class);
        mockStatic(AuditReaderFactory.class);

        when(HibernateSessionBuilder.getSessionFactory()).thenReturn(sessionFactory);
        when(sessionFactory.getCurrentSession()).thenReturn(session);
        when(AuditReaderFactory.get(session)).thenReturn(auditReader);

        doReturn(SYSTEM_ID).when(args).getSystemIdArg();
        doReturn(Type.class).when(args).getTypeArg();
        doReturn(revisions).when(args).getRevisionsRet();
    }

    @Test(expected=PreconditionException.class)
    public void systemIdArgCannotBeNull() throws PreconditionException, PostconditionException {
        doReturn(null).when(args).getSystemIdArg();
        sut.invoke();
    }

    @Test(expected=PreconditionException.class)
    public void typeArgCannotBeNull() throws PreconditionException, PostconditionException {
        doReturn(null).when(args).getTypeArg();
        sut.invoke();
    }

    @Test(expected = PostconditionException.class)
    public void revisionsRetCannotBeNull() throws PreconditionException, PostconditionException {
        doReturn(null).when(args).getRevisionsRet();
        sut.invoke();
    }
}
