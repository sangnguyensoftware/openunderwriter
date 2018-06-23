package com.ail.party.search.hibernate;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import java.util.Collection;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ail.core.BaseException;
import com.ail.core.Core;
import com.ail.core.CoreContext;
import com.ail.core.CoreProxy;
import com.ail.core.security.FilterListAccessibilityToUserService.FilterListAccessibilityToUserCommand;
import com.ail.party.Address;
import com.ail.party.Party;
import com.ail.party.search.PartySearchService.PartySearchArgument;

public class HibernatePartySearchServiceTest {
    HibernatePartySearchService sut;
    CoreProxy core = new CoreProxy();

    @Mock
    private PartySearchArgument args;
    @Mock
    private Core mockCore;
    @Mock
    private FilterListAccessibilityToUserCommand filterListAccessibilityToUserCommand;

    @BeforeClass
    public static void setup() {
        CoreContext.initialise();
        CoreProxy core = new CoreProxy();

        core.openPersistenceSession();
        core.create(new Party("ID1", "Tom", "tom@example.com", "0011", "0021", new Address("line1", null, null, null, null, null, null, null, "PKPK123")));
        core.create(new Party("ID2", "Tom2", "tom2@example.com", "0012", "0022", null));
        core.create(new Party("ID3", "Dick", "dick@example.com", "0013", "0023", null));
        core.create(new Party("ID4", "Harry", "harry@example.com", "0014", "0024", null));
        core.closePersistenceSession();
    }

    @Before
    public void setupSut() {
        MockitoAnnotations.initMocks(this);

        sut = new HibernatePartySearchService();

        sut.setArgs(args);
        sut.setCore(mockCore);

        doReturn(filterListAccessibilityToUserCommand).when(mockCore).newCommand(eq("FilterListAccessibilityToUserCommand"), eq(FilterListAccessibilityToUserCommand.class));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testWithNoArgs() throws BaseException {
        core.openPersistenceSession();
        sut.invoke();

        ArgumentCaptor<Collection> captor = ArgumentCaptor.forClass(Collection.class);
        verify(args).setPartyRet(captor.capture());
        assertThat(captor.getValue().size(), is(eq(4)));

        core.closePersistenceSession();
    }
}
