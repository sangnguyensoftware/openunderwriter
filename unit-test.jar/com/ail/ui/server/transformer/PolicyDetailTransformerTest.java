package com.ail.ui.server.transformer;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ail.core.ThreadLocale;
import com.ail.financial.Currency;
import com.ail.financial.CurrencyAmount;
import com.ail.insurance.policy.Policy;
import com.ail.insurance.policy.PolicyLink;
import com.ail.insurance.policy.PolicyLinkType;
import com.ail.insurance.policy.PolicyStatus;
import com.ail.party.Party;
import com.ail.ui.shared.model.PolicyDetailDTO;
import com.google.gwt.thirdparty.guava.common.collect.Lists;

public class PolicyDetailTransformerTest {

    @Mock
    private Party party;
    @Mock
    private Policy policy;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        doReturn(PolicyStatus.ON_RISK).when(policy).getStatus();
    }

    @Test
    public void testTransform() throws Exception {

        Locale savedLocale=ThreadLocale.getThreadLocale();

        try {
            ThreadLocale.setThreadLocale(Locale.UK);

            Date quoteDate = new Date();
            Date inceptionDate = new Date(quoteDate.getTime() + 1);
            Date expiryDate = new Date(quoteDate.getTime() + 2);

            doReturn("POL1").when(policy).getPolicyNumber();
            doReturn("QF1").when(policy).getQuotationNumber();
            doReturn("Product1").when(policy).getProductTypeId();
            doReturn(true).when(policy).isTotalPremiumDefined();
            doReturn(new CurrencyAmount(10, Currency.AED)).when(policy).getTotalPremium();
            doReturn(quoteDate).when(policy).getQuotationDate();
            doReturn(inceptionDate).when(policy).getInceptionDate();
            doReturn(expiryDate).when(policy).getExpiryDate();
            doReturn(party).when(policy).getClient();
            doReturn("Name").when(party).getLegalName();
            doReturn(3L).when(policy).getMtaIndex();
            doReturn(5L).when(policy).getRenewalIndex();
            doReturn(Lists.newArrayList(new PolicyLink(PolicyLinkType.MTA_FOR, 123L))).when(policy).getPolicyLink();

            PolicyDetailDTO policyDetail = new PolicyDetailTransformer().apply(policy);

            assertEquals("POL1", policyDetail.getPolicyNumber());
            assertEquals("QF1", policyDetail.getQuotationNumber());
            assertEquals(DateFormat.getDateInstance().format(expiryDate), policyDetail.getExpiryDate());
            assertEquals(DateFormat.getDateInstance().format(inceptionDate), policyDetail.getInceptionDate());
            assertEquals(DateFormat.getDateInstance().format(quoteDate), policyDetail.getQuoteDate());
            assertEquals("Product1", policyDetail.getProduct());
            assertEquals(Currency.AED.toString() + "10.00", policyDetail.getPremium());
            assertEquals("3", policyDetail.getMtaIndex());
            assertEquals("5", policyDetail.getRenewalIndex());
            assertEquals(123L, policyDetail.getSupersededBy());

        }
        finally {
            ThreadLocale.setThreadLocale(savedLocale);
        }
    }

    @Test
    public void testNullTransform() throws Exception {

        PolicyDetailDTO policyDetail = new PolicyDetailTransformer().apply(policy);
        assertEquals(null, policyDetail.getQuotationNumber());
    }
}
