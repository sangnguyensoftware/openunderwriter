package com.ail.ui.server.transformer;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.text.DateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ail.core.language.I18N;
import com.ail.insurance.policy.Policy;
import com.ail.ui.shared.model.PolicyDetailDTO;

@RunWith(PowerMockRunner.class)
@PrepareForTest({I18N.class, PolicySummaryDetailTransformer.class})
public class PolicySummaryDetailTransformerTest {

    @Mock
    private Policy policy;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        mockStatic(I18N.class);

        when(I18N.i18n(eq("i18n_policy_status_on_risk"))).thenReturn("On Risk");
    }

    @Test
    public void testTransform() throws Exception {

        Date createdDate = new Date();
        Date inceptionDate = new Date(createdDate.getTime() + 1);
        Date expiryDate = new Date(createdDate.getTime() + 2);

        doReturn("POL1").when(policy).getPolicyNumber();
        doReturn("QF1").when(policy).getQuotationNumber();
        doReturn("name|address, postcode").when(policy).getId();
        doReturn("ON_RISK").when(policy).getStatusAsString();
        doReturn(createdDate).when(policy).getCreatedDate();
        doReturn(inceptionDate).when(policy).getInceptionDate();
        doReturn(expiryDate).when(policy).getExpiryDate();
        doReturn(3L).when(policy).getMtaIndex();
        doReturn(5L).when(policy).getRenewalIndex();


        PolicyDetailDTO policyDetail = new PolicySummaryDetailTransformer().apply(policy);

        assertEquals("POL1", policyDetail.getPolicyNumber());
        assertEquals("QF1", policyDetail.getQuotationNumber());
        assertEquals("name", policyDetail.getClientName());
        assertEquals("address, postcode", policyDetail.getClientAddress().get(0));
        assertEquals("ONR", policyDetail.getStatus());
        assertEquals("3", policyDetail.getMtaIndex());
        assertEquals("5", policyDetail.getRenewalIndex());
        assertEquals(DateFormat.getDateInstance().format(expiryDate), policyDetail.getExpiryDate());
        assertEquals(DateFormat.getDateInstance().format(inceptionDate), policyDetail.getInceptionDate());
        assertEquals(DateFormat.getDateInstance().format(createdDate), policyDetail.getCreatedDate());

    }

    @Test
    public void testEmptyAddress() throws Exception {

        doReturn("|, ").when(policy).getId();

        PolicyDetailDTO policyDetail = new PolicySummaryDetailTransformer().apply(policy);

        assertEquals("", policyDetail.getClientName());
        assertEquals(0, policyDetail.getClientAddress().size());

        doReturn(null).when(policy).getId();

        policyDetail = new PolicySummaryDetailTransformer().apply(policy);

        assertEquals("", policyDetail.getClientName());
        assertEquals(0, policyDetail.getClientAddress().size());
    }

    @Test
    public void testNullTransform() throws Exception {
        PolicyDetailDTO policyDetail = new PolicySummaryDetailTransformer().apply(policy);
        assertEquals(null, policyDetail.getQuotationNumber());
    }
}
