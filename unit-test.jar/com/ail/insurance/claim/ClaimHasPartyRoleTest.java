package com.ail.insurance.claim;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.CoreProxy;
import com.ail.core.label.LabelsForSubjectService.LabelsForSubjectCommand;
import com.ail.party.Party;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CoreContext.class})
public class ClaimHasPartyRoleTest {
    private static final String DUMMY_ROLE_TYPE_TWO = "dummy_role_type_two";
    private static final String DUMMY_ROLE_TYPE_ONE = "dummy_role_type_one";
    private Claim sut;
    private Set<String> partyRoleTypes = new HashSet<>();

    @Mock
    private CoreProxy coreProxy;
    @Mock
    private LabelsForSubjectCommand labelsForSubjectCommand;
    @Mock
    private Party party;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        sut = new Claim();

        mockStatic(CoreContext.class);

        when(CoreContext.getCoreProxy()).thenReturn(coreProxy);

        partyRoleTypes.add(DUMMY_ROLE_TYPE_ONE);
        partyRoleTypes.add(DUMMY_ROLE_TYPE_TWO);


        doReturn(labelsForSubjectCommand).when(coreProxy).newCommand(eq(LabelsForSubjectCommand.class));
        doReturn(partyRoleTypes).when(labelsForSubjectCommand).getLabelsRet();
    }

    @Test
    public void testGetApplicableRoles() throws BaseException {
        Set<String> types = sut.getApplicableRoles();

        verify(labelsForSubjectCommand).setDiscriminatorArg(eq(Party.PARTY_ROLE_TYPES_LABEL_DISCRIMINATOR));
        verify(labelsForSubjectCommand).setSubjectArg(eq(Claim.class));

        assertThat(types, containsInAnyOrder(DUMMY_ROLE_TYPE_ONE, DUMMY_ROLE_TYPE_TWO));
    }

    @Test
    public void testAddPartyToClaim() {
        List<Party> parties;

        // should be no parties in any roles to start with.
        parties = sut.fetchPartiesForRoleTypes(DUMMY_ROLE_TYPE_ONE);
        assertThat(parties, hasSize(0));
        parties = sut.fetchPartiesForRoleTypes(DUMMY_ROLE_TYPE_TWO);
        assertThat(parties, hasSize(0));

        // add a party with one role and check it has been added.
        sut.addPartyForRoleTypes(party, DUMMY_ROLE_TYPE_ONE);
        parties = sut.fetchPartiesForRoleTypes(DUMMY_ROLE_TYPE_ONE);
        assertThat(parties, containsInAnyOrder(party));
        assertThat(parties, hasSize(1));

        // add a party with a second role and check it has been added, and confirm the
        // 1st is still present too.
        sut.addPartyForRoleTypes(party, DUMMY_ROLE_TYPE_TWO);
        parties = sut.fetchPartiesForRoleTypes(DUMMY_ROLE_TYPE_TWO);
        assertThat(parties, containsInAnyOrder(party));
        assertThat(parties, hasSize(1));
        parties = sut.fetchPartiesForRoleTypes(DUMMY_ROLE_TYPE_ONE);
        assertThat(parties, containsInAnyOrder(party));
        assertThat(parties, hasSize(1));
    }

    @Test
    public void testDeleteParty() {
        List<Party> parties;

        // add a party with one role and check it has been added.
        sut.addPartyForRoleTypes(party, DUMMY_ROLE_TYPE_ONE);
        parties = sut.fetchPartiesForRoleTypes(DUMMY_ROLE_TYPE_ONE);
        assertThat(parties, containsInAnyOrder(party));
        assertThat(parties, hasSize(1));

        // delete the party/role
        sut.removePartyFromRoleTypes(party, DUMMY_ROLE_TYPE_ONE);
        parties = sut.fetchPartiesForRoleTypes(DUMMY_ROLE_TYPE_ONE);
        assertThat(parties, hasSize(0));
    }
}
