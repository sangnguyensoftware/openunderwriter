/* Copyright Applied Industrial Logic Limited 2002. All rights reserved. */
/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

package com.ail.insurance;

import static com.ail.financial.CardIssuer.MASTERCARD;
import static com.ail.insurance.policy.ClauseType.EXCLUSION;
import static com.ail.insurance.policy.ClauseType.SUBJECTIVITY;
import static com.ail.insurance.policy.PolicyStatus.ON_RISK;
import static com.ail.insurance.policy.PolicyStatus.QUOTATION;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.CoreProxy;
import com.ail.core.CoreUserBaseCase;
import com.ail.core.Reference;
import com.ail.core.ThreadLocale;
import com.ail.core.persistence.CloseSessionService.CloseSessionCommand;
import com.ail.core.persistence.CreateService.CreateCommand;
import com.ail.core.persistence.LoadService.LoadCommand;
import com.ail.core.persistence.OpenSessionService.OpenSessionCommand;
import com.ail.core.persistence.hibernate.HibernateSessionBuilder;
import com.ail.financial.DirectDebit;
import com.ail.financial.PaymentCard;
import com.ail.financial.PaymentHoliday;
import com.ail.insurance.claim.Claim;
import com.ail.insurance.policy.AssessmentNote;
import com.ail.insurance.policy.AssessmentSheet;
import com.ail.insurance.policy.Asset;
import com.ail.insurance.policy.BehaviourType;
import com.ail.insurance.policy.Clause;
import com.ail.insurance.policy.FixedSum;
import com.ail.insurance.policy.Marker;
import com.ail.insurance.policy.MarkerResolution;
import com.ail.insurance.policy.PersonalProposer;
import com.ail.insurance.policy.Policy;
import com.ail.insurance.policy.PolicyStatus;
import com.ail.insurance.policy.RateBehaviour;
import com.ail.insurance.policy.SumBehaviour;
import com.ail.insurance.policy.Totalizer;
import com.ail.insurance.quotation.PrepareRequoteService.PrepareRequoteCommand;
import com.ail.party.Party;
import com.ail.party.Person;
import com.ail.party.Title;
import com.ail.util.DateOfBirth;

public class TestPolicyPersistence extends CoreUserBaseCase {
    private Locale savedLocale;

    @Before
    public void setUp() throws Exception {
        CoreProxy coreProxy = new CoreProxy(this.getConfigurationNamespace(), this);
        setCore(coreProxy.getCore());
        tidyUpTestData();
        setupSystemProperties();
        setupConfigurations();
        setupTestProducts();
        coreProxy.setVersionEffectiveDateToNow();
        CoreContext.initialise();
        CoreContext.setCoreProxy(coreProxy);
        savedLocale = ThreadLocale.getThreadLocale();
        ThreadLocale.setThreadLocale(Locale.UK);
    }

    @After
    public void tearDown() throws Exception {
        CoreContext.destroy();
        tidyUpTestData();
        ThreadLocale.setThreadLocale(savedLocale);
    }

    @Test
    public void testSavedPolicyCreateFindSave() throws BaseException {
        long policySystemId;

        {
            Policy policy = getCore().newProductType("com.ail.core.product.TestProduct07", Policy.class);

            getCore().newCommand(OpenSessionCommand.class).invoke();

            policySystemId = getCore().create(policy).getSystemId();

            getCore().newCommand(CloseSessionCommand.class).invoke();
        }

        {
            getCore().newCommand(OpenSessionCommand.class).invoke();

            Policy savedPolicy = getCore().load(Policy.class, policySystemId);

            getCore().newCommand(CloseSessionCommand.class).invoke();

            assertNotNull(savedPolicy);
        }
    }

    @Test
    public void testPolicyWithDocumentCreate() throws BaseException {
        getCore().newCommand(OpenSessionCommand.class).invoke();

        Policy policy = getCore().newProductType("com.ail.core.product.TestProduct07", Policy.class);

        policy.attachQuotationDocument(new byte[100]);

        getCore().create(policy);

        getCore().newCommand(CloseSessionCommand.class).invoke();
    }


    @Test
    public void testRequoteProcess() throws BaseException {
        Long policySystemId;

        {
            getCore().newCommand(OpenSessionCommand.class).invoke();

            Policy policy = getCore().newProductType("com.ail.core.product.TestProduct07", "Policy", Policy.class);

            policy = getCore().create(policy);

            policySystemId = policy.getSystemId();

            getCore().newCommand(CloseSessionCommand.class).invoke();
        }

        {
            getCore().newCommand(OpenSessionCommand.class).invoke();

            Policy policy = (Policy)getCore().queryUnique("get.policy.by.systemId", policySystemId);

            PrepareRequoteCommand prs = getCore().newCommand(PrepareRequoteCommand.class);

            prs.setPolicyArg(policy);

            prs.invoke();

            policy = prs.getRequoteRet();

            getCore().update(policy);

            getCore().newCommand(CloseSessionCommand.class).invoke();
        }
    }

    @Test
    public void testPersistedValues() throws BaseException {

        long policySystemId;

        {
            Policy policy = getCore().newProductType("com.ail.core.product.TestProduct07", Policy.class);

            long start = System.currentTimeMillis();

            getCore().newCommand(OpenSessionCommand.class).invoke();

            CreateCommand create = getCore().newCommand(CreateCommand.class);
            create.setObjectArg(policy);
            create.invoke();

            policySystemId = create.getObjectArg().getSystemId();

            getCore().newCommand(CloseSessionCommand.class).invoke();

            long end = System.currentTimeMillis();
            System.out.printf("save took: %dms\n", end - start);

        }

        {
            long start = System.currentTimeMillis();

            getCore().newCommand(OpenSessionCommand.class).invoke();

            LoadCommand loadCommand = getCore().newCommand(LoadCommand.class);
            loadCommand.setTypeArg(Policy.class);
            loadCommand.setSystemIdArg(policySystemId);
            loadCommand.invoke();
            Policy policy = (Policy) loadCommand.getObjectRet();

            assertNotNull(policy);
            assertEquals("0101010", policy.getId());
            assertEquals("com.ail.core.product.TestProduct07", policy.getProductTypeId());
            assertEquals("POL1234", policy.getPolicyNumber());
            assertEquals("QUO4321", policy.getQuotationNumber());
            assertNotNull(policy.getInceptionDate());
            assertNotNull(policy.getExcess());
            assertTrue(PolicyStatus.APPLICATION.equals(policy.getStatus()));

            Person ph = (Person) policy.getClient();

            assertTrue(Title.MR.equals(ph.getTitle()));
            assertEquals("Lord", ph.getOtherTitle());
            assertEquals("Jimbo", ph.getFirstName());
            assertEquals("Clucknasty", ph.getSurname());
            assertEquals(new DateOfBirth(1964, 11, 8).getDateAsString(), ph.getDateOfBirth().getDateAsString());
            assertEquals("Mr. Jimbo Clucknasty", ph.getLegalName());
            assertEquals("The Top Flat", ph.getAddress().getLine1());
            assertEquals("East Wing", ph.getAddress().getLine2());
            assertEquals("Plugsmear House", ph.getAddress().getLine3());
            assertEquals("Snorberry Lane", ph.getAddress().getLine4());
            assertEquals("Frobmarshington", ph.getAddress().getLine5());
            assertEquals("Upper Wallingham", ph.getAddress().getTown());
            assertEquals("Kunt", ph.getAddress().getCounty());
            assertEquals("UK", ph.getAddress().getCountry());
            assertEquals("KU19 3FS", ph.getAddress().getPostcode());

            assertNotNull(policy.getAssetById("a11"));
            assertEquals(19, policy.getAssetById("a11").getAttribute().size());

            assertNotNull(policy.getSectionById("s11"));

            assertNotNull(policy.getCoverageById("c1"));
            assertEquals("East river tea bag cover", policy.getCoverageById("c1").getDescription());
            assertTrue(policy.getCoverageById("c1").isEnabled());
            assertTrue(!policy.getCoverageById("c1").isOptional());
            assertEquals("\u00A3" + "1,000.00", policy.getCoverageById("c1").getLimit().toString());
            assertEquals("\u00A3" + "500.00", policy.getCoverageById("c1").getDeductible().toString());

            AssessmentSheet sheet = policy.getAssessmentSheet();

            AssessmentNote note1 = (AssessmentNote) sheet.findLineById("note1");
            assertEquals("note1", note1.getId());
            assertEquals("because i want to", note1.getReason());
            assertNull(note1.getRelatesTo());

            MarkerResolution res1 = (MarkerResolution) sheet.findLineById("res1");
            assertEquals("res1", res1.getId());
            assertEquals("because I've resolved it", res1.getReason());
            assertNull(res1.getRelatesTo());
            assertEquals("note1", res1.getBehaviourId());

            Marker mkr1 = (Marker) sheet.findLineById("mkr1");
            assertEquals("mkr1", mkr1.getId());
            assertEquals("declines are good", mkr1.getReason());
            assertEquals("DECLINE", mkr1.getTypeAsString());
            assertEquals("com.ail.insurance.policy.Asset", mkr1.getRelatesTo().getTypeAsString());
            assertEquals("as1", mkr1.getRelatesTo().getId());

            FixedSum fix1 = (FixedSum) sheet.findLineById("fix1");
            assertEquals("fix1", fix1.getId());
            assertEquals("fixed sums are good", fix1.getReason());
            assertEquals("com.ail.insurance.policy.Asset", fix1.getRelatesTo().getTypeAsString());
            assertEquals("as1", fix1.getRelatesTo().getId());
            assertEquals("final premium", fix1.getContributesTo());
            assertEquals("\u00A3" + "123.00", fix1.getAmount().toString());

            Totalizer tot = (Totalizer) sheet.findLineById("tot");
            assertEquals("tot", tot.getId());
            assertEquals("totals are nice", tot.getReason());
            assertEquals("com.ail.insurance.policy.Asset", tot.getRelatesTo().getTypeAsString());
            assertEquals("as1", tot.getRelatesTo().getId());
            assertEquals("final premium", tot.getContributesTo().toString());
            assertEquals("one,two,tree", tot.getDependsOn());

            RateBehaviour rat = (RateBehaviour) sheet.findLineById("rat");
            assertEquals("rat", rat.getId());
            assertEquals("I rate rates", rat.getReason());
            assertEquals("com.ail.insurance.policy.Asset", rat.getRelatesTo().getTypeAsString());
            assertEquals("as2", rat.getRelatesTo().getId());
            assertEquals("total premium", rat.getContributesTo().toString());
            assertTrue(BehaviourType.LOAD.equals(rat.getType()));
            assertEquals("2%", rat.getRate().getRate());
            assertEquals("one", rat.getDependsOn());

            SumBehaviour sum = (SumBehaviour) sheet.findLineById("sum");
            assertEquals("sum", sum.getId());
            assertEquals("Sum do sum dont", sum.getReason());
            assertEquals("com.ail.insurance.policy.Asset", sum.getRelatesTo().getTypeAsString());
            assertEquals("as2", sum.getRelatesTo().getId());
            assertEquals("final premium", sum.getContributesTo().toString());
            assertTrue(BehaviourType.LOAD.equals(sum.getType()));
            assertEquals("\u00A3" + "321.00", sum.getAmount().toString());

            getCore().newCommand(CloseSessionCommand.class).invoke();

            long end = System.currentTimeMillis();
            System.out.printf("load took: %dms\n", end - start);
        }
    }

    @Test
    public void testPaymentHolidayPersistence() throws BaseException {
        Long policySystemId;

        {
            Policy policy = new Policy();
            policy.getPaymentHoliday().add(new PaymentHoliday());

            getCore().newCommand(OpenSessionCommand.class).invoke();

            CreateCommand create = getCore().newCommand(CreateCommand.class);
            create.setObjectArg(policy);
            create.invoke();

            policySystemId = create.getObjectArg().getSystemId();

            getCore().newCommand(CloseSessionCommand.class).invoke();
        }

        {
            getCore().newCommand(OpenSessionCommand.class).invoke();

            LoadCommand loadCommand = getCore().newCommand(LoadCommand.class);
            loadCommand.setTypeArg(Policy.class);
            loadCommand.setSystemIdArg(policySystemId);
            loadCommand.invoke();
            Policy policy = (Policy) loadCommand.getObjectRet();

            assertThat(policy.getPaymentHoliday(), hasSize(1));

            getCore().newCommand(CloseSessionCommand.class).invoke();
        }
    }

    @Test
    public void testPolicyAudit() throws BaseException {
        CoreProxy coreProxy = new CoreProxy();
        Long policySystemId;

        {
            coreProxy.openPersistenceSession();

            Policy policy = new Policy();
            policy.setStatus(QUOTATION);

            policySystemId = coreProxy.create(policy).getSystemId();

            coreProxy.closePersistenceSession();
        }

        {
            coreProxy.openPersistenceSession();

            Policy policy = coreProxy.load(Policy.class, policySystemId);
            policy.setStatus(ON_RISK);

            coreProxy.update(policy);

            coreProxy.closePersistenceSession();
        }

        {
            coreProxy.openPersistenceSession();

            AuditReader auditReader = AuditReaderFactory.get(HibernateSessionBuilder.getSessionFactory().getCurrentSession());
            List<Number> revisions = auditReader.getRevisions(Policy.class, policySystemId);

            assertThat(revisions, hasSize(2));

            Policy p1 = auditReader.find(Policy.class, policySystemId, revisions.get(0));
            assertThat(p1.getStatus(), is(QUOTATION));

            Policy p2 = auditReader.find(Policy.class, policySystemId, revisions.get(1));
            assertThat(p2.getStatus(), is(ON_RISK));

            coreProxy.closePersistenceSession();
        }
    }

    @Test
    public void testPaymenMethodInPolicyToXml() throws Exception {
        CoreProxy coreProxy = new CoreProxy();

        PersonalProposer party = new PersonalProposer();

        party.setTitle(Title.UNDEFINED);

        party.getPaymentMethod().add(new DirectDebit("87706054", "01-02-03", "Tester"));
        party.getPaymentMethod().add(new PaymentCard(MASTERCARD, "9284 2839 2938 4829", "Tester Test", null, "123"));


        try {
            coreProxy.openPersistenceSession();

            coreProxy.create(party);
        }
        finally {
            coreProxy.closePersistenceSession();
        }
    }


    public Map<Number,Policy> getPolicyRevisions(long policyId) {
        Map<Number,Policy> policies = new HashMap<>();

        AuditReader auditReader = AuditReaderFactory.get(HibernateSessionBuilder.getSessionFactory().getCurrentSession());
        List<Number> revisions = auditReader.getRevisions(Policy.class, policyId);

        for(Number rev: revisions) {
            policies.put(rev, auditReader.find(Policy.class, policyId, rev));
        }

        return policies;
    }

    @Test
    public void testPolicyLabels() throws BaseException {
        long policySystemId;

        {
            Policy policy = getCore().newProductType("com.ail.core.product.TestProduct07", Policy.class);

            policy.getLabel().add("Label 1");
            policy.getLabel().add("Label 2");
            policy.getLabel().add("Label 3");

            getCore().openPersistenceSession();

            policySystemId = getCore().create(policy).getSystemId();

            getCore().closePersistenceSession();
        }

        {
            getCore().openPersistenceSession();

            Policy policy = getCore().load(Policy.class, policySystemId);

            assertThat(policy.getLabel(), hasSize(3));

            getCore().closePersistenceSession();
        }
    }

    @Test
    public void testClause() throws BaseException {
        long policySystemId;

        {
            Policy policy = getCore().newProductType("com.ail.core.product.TestProduct07", Policy.class);

            policy.getClause().add(new Clause(SUBJECTIVITY, "sub123", "Title 1"));
            policy.getClause().get(0).getRelatesTo().add(new Reference(Asset.class, "1234"));

            policy.getClause().add(new Clause(EXCLUSION, "exc123", "Title 2"));


            getCore().openPersistenceSession();

            policySystemId = getCore().create(policy).getSystemId();

            getCore().closePersistenceSession();
        }

        {
            getCore().openPersistenceSession();

            Policy policy = getCore().load(Policy.class, policySystemId);

            assertThat(policy.getClause(), hasSize(2));

            getCore().closePersistenceSession();
        }
    }


    @Test
    public void testClaim() throws BaseException {
        long policySystemId;

        {
            Policy policy = getCore().newProductType("com.ail.core.product.TestProduct07", Policy.class);

            policy.addClaim(new Claim());

            getCore().openPersistenceSession();

            policySystemId = getCore().create(policy).getSystemId();

            getCore().closePersistenceSession();
        }

        {
            getCore().openPersistenceSession();

            Policy policy = getCore().load(Policy.class, policySystemId);

            assertThat(policy.getClaim(), hasSize(1));

            getCore().closePersistenceSession();
        }
    }

    @Test
    public void testParties() throws BaseException {
        long policySystemId;

        {
            Policy policy = getCore().newProductType("com.ail.core.product.TestProduct07", Policy.class);

            Person proposer = new Person();
            proposer.setFirstName("TEST Firstname");
            proposer.setSurname("TEST Surname");
            policy.setClient(proposer);

            getCore().openPersistenceSession();

            policySystemId = getCore().create(policy).getSystemId();

            getCore().closePersistenceSession();
        }

        {
            getCore().openPersistenceSession();

            Policy policy = getCore().load(Policy.class, policySystemId);

            assertThat(policy.getClient(), is(not((Party)null)));
            assertThat(policy.getClient().getLegalName(), is("TEST Firstname TEST Surname"));

            getCore().closePersistenceSession();
        }
    }
}
