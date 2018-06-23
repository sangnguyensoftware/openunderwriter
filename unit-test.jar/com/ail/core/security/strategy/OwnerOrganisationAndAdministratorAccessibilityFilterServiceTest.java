package com.ail.core.security.strategy;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ail.core.Owned;
import com.ail.core.PreconditionException;
import com.ail.core.persistence.UpdateException;
import com.ail.core.security.FilterListAccessibilityToUserArgumentImpl;
import com.ail.core.security.FilterListAccessibilityToUserService.FilterListAccessibilityToUserArgument;

public class OwnerOrganisationAndAdministratorAccessibilityFilterServiceTest {
    OwnerOrganisationAndAdministratorAccessibilityFilterService sut;

    FilterListAccessibilityToUserArgument args;

    List<Object> listArg = new ArrayList<>();

    @Mock
    Owned ownedByUser;
    @Mock
    Owned ownedByGuest;
    @Mock
    Owned ownedBySomeoneElse;
    @Mock
    Owned ownerByUserInSameOrganisation;
    @Mock
    Object unownedObject;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        sut = spy(new OwnerOrganisationAndAdministratorAccessibilityFilterService());
        args = new FilterListAccessibilityToUserArgumentImpl();
        sut.setArgs(args);

        listArg.add(ownedByUser);
        listArg.add(ownedByGuest);
        listArg.add(ownedBySomeoneElse);
        listArg.add(ownerByUserInSameOrganisation);
        listArg.add(unownedObject);
        args.setListArg(listArg);

        doReturn(true).when(sut).isObjectOwnedByUser(eq(ownedByUser));
        doReturn(false).when(sut).isObjectOwnedByUser(eq(ownedBySomeoneElse));
        doReturn(false).when(sut).isObjectOwnedByUser(eq(ownedByGuest));
        doReturn(false).when(sut).isObjectOwnedByUser(eq(ownerByUserInSameOrganisation));

        doReturn(true).when(sut).isObjectOwnedByGuest(eq(ownedByGuest));
        doReturn(false).when(sut).isObjectOwnedByGuest(eq(ownedByUser));
        doReturn(false).when(sut).isObjectOwnedByGuest(eq(ownedBySomeoneElse));
        doReturn(false).when(sut).isObjectOwnedByGuest(eq(ownerByUserInSameOrganisation));

        doReturn(true).when(sut).isObjectsOwnerInSameOrganisation(eq(ownerByUserInSameOrganisation));
        doReturn(false).when(sut).isObjectsOwnerInSameOrganisation(eq(ownedByUser));
        doReturn(false).when(sut).isObjectsOwnerInSameOrganisation(eq(ownedBySomeoneElse));
        doReturn(false).when(sut).isObjectsOwnerInSameOrganisation(eq(ownedByGuest));

        doReturn(false).when(sut).isGuestUser();
        doReturn(false).when(sut).isUserInOverridingRole();
    }

    @Test(expected = PreconditionException.class)
    public void listArgCannotBeNull() throws PreconditionException, UpdateException {
        args.setListArg(null);
        sut.invoke();
    }

    @Test(expected = PreconditionException.class)
    public void listRetMustBeNull() throws PreconditionException, UpdateException {
        args.setListRet(new ArrayList<>());
        sut.invoke();
    }

    @Test
    public void testGuestUserCanAccessGuestUsersObjectsOnly() throws PreconditionException, UpdateException {
        doReturn(true).when(sut).isGuestUser();
        doReturn(false).when(sut).isObjectOwnedByUser(eq(ownedByUser));
        doReturn(false).when(sut).isObjectsOwnerInSameOrganisation(eq(ownerByUserInSameOrganisation));

        sut.invoke();

        assertThat(sut.getArgs().getListRet().size(), is(2));
        assertThat(sut.getArgs().getListRet(), hasItems(ownedByGuest, unownedObject));
    }

    @Test
    public void testOwningUserCanAccessOwnedObjectsAndOrganisationObjectOnly() throws PreconditionException, UpdateException {
        sut.invoke();

        assertThat(sut.getArgs().getListRet().size(), is(3));
        assertThat(sut.getArgs().getListRet(), hasItems(ownedByUser, ownerByUserInSameOrganisation, unownedObject));
    }

    @Test
    public void testOverrideCanAccessAllObjects() throws PreconditionException, UpdateException {
        doReturn(true).when(sut).isUserInOverridingRole();

        sut.invoke();

        assertThat(sut.getArgs().getListRet().size(), is(5));
    }

}
