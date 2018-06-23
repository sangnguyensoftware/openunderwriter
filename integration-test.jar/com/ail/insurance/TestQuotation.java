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

import static com.ail.core.Functions.productNameToConfigurationNamespace;
import static com.ail.financial.Currency.GBP;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.CoreProxy;
import com.ail.core.CoreUserBaseCase;
import com.ail.core.PreconditionException;
import com.ail.core.Reference;
import com.ail.core.VersionEffectiveDate;
import com.ail.financial.CurrencyAmount;
import com.ail.insurance.acceptance.AssessPaymentOptionsService.AssessPaymentOptionsCommand;
import com.ail.insurance.policy.AssessmentLine;
import com.ail.insurance.policy.AssessmentSheet;
import com.ail.insurance.policy.CalculationLine;
import com.ail.insurance.policy.ConstrainValueOutOfBounds;
import com.ail.insurance.policy.FixedSum;
import com.ail.insurance.policy.MarkerResolution;
import com.ail.insurance.policy.Policy;
import com.ail.insurance.policy.PolicyStatus;
import com.ail.insurance.policy.ReferValueOutOfBounds;
import com.ail.insurance.quotation.AddPolicyNumberService.AddPolicyNumberCommand;
import com.ail.insurance.quotation.AddQuoteNumberService.AddQuoteNumberCommand;
import com.ail.insurance.quotation.AssessRiskService.AssessRiskCommand;
import com.ail.insurance.quotation.CalculatePremiumService.CalculatePremiumCommand;
import com.ail.insurance.quotation.EnforceComplianceService.EnforceComplianceCommand;
import com.ail.insurance.quotation.RefreshAssessmentSheetsService.RefreshAssessmentSheetsCommand;
import com.ail.util.Rate;

/**
 * Tests related to premium calculations.
 */
public class TestQuotation extends CoreUserBaseCase {
    private static final long serialVersionUID = 2030295330203910171L;

    /**
     * Sets up the fixture (run before every test). Get an instance of Core, and
     * delete the testnamespace from the config table.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        CoreProxy coreProxy = new CoreProxy(this.getConfigurationNamespace(), this);
        setCore(coreProxy.getCore());
        tidyUpTestData();
        setupSystemProperties();
        setupConfigurations();
        setupTestProducts();
        setVersionEffectiveDate(new VersionEffectiveDate());
        CoreContext.initialise();
        CoreContext.setCoreProxy(coreProxy);
        CoreContext.getCoreProxy().openPersistenceSession();
    }

    /**
     * Tears down the fixture (run after each test finishes)
     *
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        tidyUpTestData();
        CoreContext.getCoreProxy().closePersistenceSession();
        CoreContext.destroy();
    }

    /**
     * Simple assess risk. Take a simple policy through assess risk.
     * <ol>
     * <li>Create a policy (based on TestPolicy1).</li>
     * <li>Pass the policy into assess risk.</li>
     * <li>Fail if the section doesn't have exactly 2 assessment lines</li>
     * <li>Fail if the policy doesn't have exactly 1 assessment line</li>
     * <li>Fail if any exceptions are thrown</li>
     * </ol>
     *
     * @throws BaseException
     */
    @Test
    public void testSimpleAssessRisk() throws Exception {
        // try invoking the entry point with no args...
        Policy policy = getCore().newProductType("com.ail.core.product.TestProduct04", Policy.class);
        AssessRiskCommand command = getCore().newCommand(AssessRiskCommand.class);
        command.setPolicyArgRet(policy);
        command.setProductTypeIdArg(policy.getProductTypeId());
        command.invoke();
        Policy pout = command.getPolicyArgRet();
        assertEquals(2, pout.getSectionById("sec1").getAssessmentSheet().getLineCount());
        assertEquals(1, pout.getAssessmentSheet().getLineCount());
    }

    /**
     * A policy's risk can only be assessed if the policy is at Application
     * status. This test ensures that this is the case.
     * <ol>
     * <li>Create a policy (based on TestPolicy1).</li>
     * <li>Set the policy's status to Quotation.</li>
     * <li>Pass the policy into assess risk.</li>
     * <li>Fail if a PreconditionException isn't thrown.</li>
     * <li>Fail if any exception other than Precondition is thrown.</li>
     * </ol>
     *
     * @throws Exception
     */
    @Test
    public void testAssessRiskStatusCheck() throws Exception {
        // try invoking the entry point with no args...
        Policy policy = getCore().newProductType("com.ail.core.product.TestProduct04", Policy.class);
        policy.setStatus(PolicyStatus.QUOTATION);
        AssessRiskCommand command = getCore().newCommand(AssessRiskCommand.class);
        command.setPolicyArgRet(policy);
        try {
            command.invoke();
            fail("risk assessed on policy at Quotation state.");
        } catch (PreconditionException e) {
            // ok, this is what we want to see
        }
    }

    /**
     * Make sure assess risk clears up old entries before adding new ones.If
     * assess risk is called twice with the same policy, on the second call it
     * should delete all the assessment sheet entries it created on the first
     * time, then re-assess the risk from scratch.
     * <ol>
     * <li>Create an instance of the test policy</li>
     * <li>Pass the policy into assess risk</li>
     * <li>Check that the policy's assessment sheet has 1 entry</li>
     * <li>Check that the section's assessment sheet has 2 entries</li>
     * <li>Add an entry to the policy's assessment sheet, and to the section's
     * sheet.</li>
     * <li>Pass the policy back into assess risk
     * <li>
     * <li>Check that the policy's assessment sheet has 1 entry</li>
     * <li>Check that the section's assessment sheet has 2 entries</li>
     * </ol>
     *
     * @throws Exception
     */
    @Test
    public void testOldLineRemoval() throws Exception {
        // try invoking the entry point with no args...
        Policy policy = getCore().newProductType("com.ail.core.product.TestProduct04", Policy.class);
        AssessRiskCommand command = getCore().newCommand(AssessRiskCommand.class);
        command.setPolicyArgRet(policy);
        command.setProductTypeIdArg(policy.getProductTypeId());
        command.invoke();
        policy = command.getPolicyArgRet();
        assertEquals(2, policy.getSectionById("sec1").getAssessmentSheet().getLineCount());
        assertEquals(1, policy.getAssessmentSheet().getLineCount());

        policy.getAssessmentSheet().setLockingActor("AssessRisk");
        policy.getAssessmentSheet().addLoading("dummy one", "reason", null, "nothing", "nothing", new Rate("10%"));
        policy.getAssessmentSheet().clearLockingActor();

        policy.getSectionById("sec1").getAssessmentSheet().setLockingActor("AssessRisk");
        policy.getSectionById("sec1").getAssessmentSheet().addLoading("dummy two", "reason", null, "nothing", "nothing", new Rate("10%"));
        policy.getSectionById("sec1").getAssessmentSheet().clearLockingActor();

        assertEquals(3, policy.getSectionById("sec1").getAssessmentSheet().getLineCount());
        assertEquals(2, policy.getAssessmentSheet().getLineCount());

        command.setPolicyArgRet(policy);
        command.invoke();
        policy = command.getPolicyArgRet();
        assertEquals(2, policy.getSectionById("sec1").getAssessmentSheet().getLineCount());
        assertEquals(1, policy.getAssessmentSheet().getLineCount());
    }

    /**
     * Simple assess risk & calculate premium test. Take a simple policy through
     * the assess & calculate services to get a premium. The TestPolicy1 should
     * pass thought the default product and end up with a premium of 250.00.
     * <ol>
     * <li>Create a policy (based on TestPolicy1).</li>
     * <li>Pass the policy into assess risk.</li>
     * <li>Fail if the section doesn't have exactly 2 assessment lines</li>
     * <li>Fail if the policy doesn't have exactly 1 assessment line</li>
     * <li>Pass the policy into calculate premium</li>
     * <li>Fail if the section doesn't have exactly 2 assessment lines</li>
     * <li>Fail if the policy doesn't have exactly 6 assessment lines</li>
     * <li>Fail if the policy status is not set to Quotation</li>
     * <li>Fail if the total premium assessment line is not set to 250.00</li>
     * <li>Fail if any exceptions are thrown</li>
     * </ol>
     *
     * @throws BaseException
     */
    @Test
    public void testSimpleAssessRiskCalculatePremium() throws Exception {
        Policy policy = getCore().newProductType("com.ail.core.product.TestProduct04", Policy.class);

        AssessRiskCommand cmd1 = getCore().newCommand(AssessRiskCommand.class);
        cmd1.setPolicyArgRet(policy);
        cmd1.setProductTypeIdArg(policy.getProductTypeId());
        cmd1.invoke();
        policy = cmd1.getPolicyArgRet();

        // check that only the expected lines are present on the section
        assertNotNull(policy.getSectionById("sec1").getAssessmentSheet().findLineById("loading1"));
        assertNotNull(policy.getSectionById("sec1").getAssessmentSheet().findLineById("sum insured"));
        assertEquals(2, policy.getSectionById("sec1").getAssessmentSheet().getLineCount());

        // check that only the expected lines are on the policy (flag comes from
        // the base product)
        assertNotNull(policy.getAssessmentSheet().findLineById("flag"));
        assertEquals(1, policy.getAssessmentSheet().getLineCount());

        CalculatePremiumCommand cmd2 = getCore().newCommand(CalculatePremiumCommand.class);
        cmd2.setPolicyArgRet(policy);
        cmd2.invoke();
        policy = cmd2.getPolicyArgRet();

        assertEquals(PolicyStatus.QUOTATION, policy.getStatus());
        assertEquals(2, policy.getSectionById("sec1").getAssessmentSheet().getLineCount());

        // check that only the expected lines are present
        assertNotNull(policy.getAssessmentSheet().findLineById("tax"));
        assertNotNull(policy.getAssessmentSheet().findLineById("charge"));
        assertNotNull(policy.getAssessmentSheet().findLineById("brokerage"));
        assertNotNull(policy.getAssessmentSheet().findLineById("commission1"));
        assertNotNull(policy.getAssessmentSheet().findLineById("flag"));
        assertNotNull(policy.getAssessmentSheet().findLineById("total premium"));
        assertEquals(6, policy.getAssessmentSheet().getLineCount());

        // make sure there's a tax line
        assertTrue("332.63==policy.getTotalPremium().getAmount().doubleValue()", 332.63 == policy.getTotalPremium().getAmount().doubleValue());
    }

    /**
     * This test is identical to
     * <code>testSimpleAssessRiskCalculatePremium</code>, with the exception
     * that no tax is (should!) be added to the quotation.
     *
     * @throws Exception
     */
    @Test
    public void testSimpleAssessRiskCalculatePremiumNoTaxNoCommission() throws Exception {
        Policy policy = getCore().newProductType("com.ail.core.product.TestProduct05", Policy.class);

        AssessRiskCommand cmd1 = getCore().newCommand(AssessRiskCommand.class);
        cmd1.setPolicyArgRet(policy);
        cmd1.setProductTypeIdArg(policy.getProductTypeId());
        cmd1.invoke();
        policy = cmd1.getPolicyArgRet();

        assertEquals(2, policy.getSectionById("sec1").getAssessmentSheet().getLineCount());
        assertEquals(1, policy.getAssessmentSheet().getLineCount());

        CalculatePremiumCommand cmd2 = getCore().newCommand(CalculatePremiumCommand.class);
        cmd2.setPolicyArgRet(policy);
        cmd2.invoke();
        policy = cmd2.getPolicyArgRet();

        assertEquals(PolicyStatus.QUOTATION, policy.getStatus());
        assertEquals(2, policy.getSectionById("sec1").getAssessmentSheet().getLineCount());
        assertEquals(5, policy.getAssessmentSheet().getLineCount());

        assertTrue(257.63 == policy.getTotalPremium().getAmount().doubleValue());
    }

    @Test
    public void testPriorityAssessmentLine() throws Exception {
        Policy policy;
        AssessmentSheet sheet;
        CalculatePremiumCommand cmd2;

        policy = getCore().newProductType("com.ail.core.product.TestProduct05", Policy.class);

        sheet = new AssessmentSheet();

        // without ordering
        sheet.setLockingActor("me");
        sheet.addFixedSum("base", null, null, "total premium", new CurrencyAmount(100.0, GBP));
        sheet.addLoading("load1", null, "total premium", "total premium", new Rate("5%"), 2);
        sheet.addFixedSum("load2", null, "total premium", new CurrencyAmount(10.0, GBP), 1);
        sheet.addLoading("load3", null, "total premium", "total premium", new Rate("5%"), 3);
        sheet.clearLockingActor();

        policy.setAssessmentSheet(sheet);
        policy.getSectionById("sec1").setAssessmentSheet(new AssessmentSheet());

        cmd2 = getCore().newCommand(CalculatePremiumCommand.class);
        cmd2.setPolicyArgRet(policy);
        cmd2.invoke();
        policy = cmd2.getPolicyArgRet();

        assertEquals(PolicyStatus.QUOTATION, policy.getStatus());

        // with ordering
        policy = getCore().newProductType("com.ail.core.product.TestProduct05", Policy.class);

        sheet = new AssessmentSheet();

        sheet.setLockingActor("me");
        sheet.addFixedSum("base", null, null, "total premium", new CurrencyAmount(100.0, GBP));
        sheet.addLoading("load1", null, "total premium", "total premium", new Rate("5%"), 1);
        sheet.addFixedSum("load2", null, "total premium", new CurrencyAmount(10.0, GBP), 2);
        sheet.addLoading("load3", null, "total premium", "total premium", new Rate("5%"), 3);
        sheet.clearLockingActor();

        policy.setAssessmentSheet(sheet);
        policy.getSectionById("sec1").setAssessmentSheet(new AssessmentSheet());

        cmd2 = getCore().newCommand(CalculatePremiumCommand.class);
        cmd2.setPolicyArgRet(policy);
        cmd2.invoke();
        policy = cmd2.getPolicyArgRet();

        assertEquals(PolicyStatus.QUOTATION, policy.getStatus());
    }

    /**
     * Policy with a refer. If a policy has a refer line, calculate premium
     * should set the policy status to referred, and not process any
     * calculations.
     * <ol>
     * <li>Create a policy (based on TestPolicy1).</li>
     * <li>Create a section level assessment sheet</li>
     * <ol>
     * <li>Add a base premium line of &pound;20</li>
     * <li>Add a refer (Id=ref1)</li>
     * </ol>
     * <li>Create an empty policy level assessment sheet</li>
     * <li>Pass the policy into calculate premium</li>
     * <li>Fail if the policy status is not set to Referred</li>
     * <li>Fail if there are not exactly 2 lines in the section's sheet</li>
     * <li>Fail if there are any lines in the policy's assessment sheet</li>
     * <li>Fail if any exceptions are thrown</li>
     * </ol>
     *
     * @throws BaseException
     */
    @Test
    public void testCalculatePremiumRefer() throws BaseException {
        // policy with one referral on the section.
        Policy policy = getCore().newProductType("com.ail.core.product.TestProduct08", Policy.class);

        // run it through calc premium
        CalculatePremiumCommand cmd2 = getCore().newCommand(CalculatePremiumCommand.class);
        cmd2.setPolicyArgRet(policy);
        cmd2.invoke();
        policy = cmd2.getPolicyArgRet();

        // make sure the status comes out correct
        assertEquals(PolicyStatus.REFERRED, policy.getStatus());

        // make sure _all_ entries were added the the sheets
        assertEquals(7, policy.getAssessmentSheet().getLineCount());
        assertEquals(2, policy.getSectionById("sec1").getAssessmentSheet().getLineCount());
    }

    /**
     * Policy with a decline. If a policy has a decline line, calculate premium
     * should set the policy status to declined, and not process any
     * calculations.
     * <ol>
     * <li>Create a policy (based on TestPolicy1).</li>
     * <li>Create a section level assessment sheet</li>
     * <ol>
     * <li>Add a base premium line of &pound;20</li>
     * <li>Add a decline (Id=dec1)</li>
     * </ol>
     * <li>Create an empty policy level assessment sheet</li>
     * <li>Pass the policy into calculate premium</li>
     * <li>Fail if the policy status is not set to Declined</li>
     * <li>Fail if there are not exactly 2 lines in the section's sheet</li>
     * <li>Fail if there are any lines in the policy's assessment sheet</li>
     * <li>Fail if any exceptions are thrown</li>
     * </ol>
     *
     * @throws BaseException
     */
    @Test
    public void testCalculatePremiumDecline() throws BaseException {
        // policy with one referral on the section.
        Policy policy = getCore().newProductType("com.ail.core.product.TestProduct09", Policy.class);

        // run it through calc premium
        CalculatePremiumCommand cmd2 = getCore().newCommand(CalculatePremiumCommand.class);
        cmd2.setPolicyArgRet(policy);
        cmd2.invoke();
        policy = cmd2.getPolicyArgRet();

        // make sure the status comes out correct
        assertEquals(PolicyStatus.DECLINED, policy.getStatus());

        // make sure no entries were added the the sheets
        assertEquals(7, policy.getAssessmentSheet().getLineCount());
        assertEquals(2, policy.getSectionById("sec1").getAssessmentSheet().getLineCount());
    }

    /**
     * Policy with refer and decline. If a policy has both refer and decline
     * lines, calculate premium should set its status to declined.
     * <ol>
     * <li>Create a policy (based on TestPolicy1).</li>
     * <li>Create a section level assessment sheet</li>
     * <ol>
     * <li>Add a base premium line of &pound;20</li>
     * <li>Add a referral (Id=ref1)</li>
     * <li>Add a decline (Id=dec1)</li>
     * </ol>
     * <li>Create an empty policy level assessment sheet</li>
     * <li>Pass the policy into calculate premium</li>
     * <li>Fail if the policy status is not set to Declined</li>
     * <li>Fail if there are not exactly 3 lines in the section's sheet</li>
     * <li>Fail if there are any lines in the policy's assessment sheet</li>
     * <li>Fail if any exceptions are thrown</li>
     * </ol>
     *
     * @throws BaseException
     */
    @Test
    public void testCalculatePremiumDeclineAndRefer() throws BaseException {
        // policy with one referral on the section.
        Policy policy = getCore().newProductType("com.ail.core.product.TestProduct10", Policy.class);

        // run it through calc premium
        CalculatePremiumCommand cmd2 = getCore().newCommand(CalculatePremiumCommand.class);
        cmd2.setPolicyArgRet(policy);
        cmd2.invoke();
        policy = cmd2.getPolicyArgRet();

        // make sure the status comes out correct
        assertEquals(PolicyStatus.DECLINED, policy.getStatus());

        // make sure no entries were added the the sheets
        assertEquals(8, policy.getAssessmentSheet().getLineCount());
        assertEquals(2, policy.getSectionById("sec1").getAssessmentSheet().getLineCount());
    }

    /**
     * Single resolution, single refer. Ensure that a resolved referral is
     * ignored, and the quotation goes ahead.
     * <ol>
     * <li>Create a policy (based on TestPolicy1).</li>
     * <li>Createa section level assessment sheet</li>
     * <ol>
     * <li>Add a base premium line of &pound;20</li>
     * <li>Add a referral (Id=ref1)</li>
     * <li>Add a resolution for ref1</li>
     * </ol>
     * <li>Create an empty policy level assessment sheet</li>
     * <li>Pass the policy into calculate premium</li>
     * <li>Fail if the policy status is not set to Quotation</li>
     * <li>Fail if there are not exactly 3 lines in the section's sheet</li>
     * <li>Fail if there is not exactly 1 line in the policy's assessment sheet
     * </li>
     * <li>Fail if any exceptions are thrown</li>
     * </ol>
     *
     * @throws BaseException
     */
    @Test
    public void testReferMarkerResolution() throws BaseException {
        // policy with one referral on the section.
        Policy policy = getCore().newProductType("com.ail.core.product.TestProduct08", Policy.class);

        // run it through calc premium
        CalculatePremiumCommand cmd2 = getCore().newCommand(CalculatePremiumCommand.class);
        cmd2.setPolicyArgRet(policy);
        cmd2.invoke();
        policy = cmd2.getPolicyArgRet();

        // we should get a referral the first time
        assertEquals(PolicyStatus.REFERRED, policy.getStatus());

        // mark the referral as resolved
        policy.getAssessmentSheet().setLockingActor("me");
        policy.getAssessmentSheet().addLine(new MarkerResolution("res1", "nice day", new Reference(AssessmentLine.class, "dummyid2"), null));
        policy.getAssessmentSheet().clearLockingActor();

        // set the status back to Application (or calc premium will refuse to
        // process it)
        policy.setStatus(PolicyStatus.APPLICATION);

        // calc premium again
        cmd2.setPolicyArgRet(policy);
        cmd2.invoke();
        policy = cmd2.getPolicyArgRet();

        // this time we get a quotation
        assertEquals(PolicyStatus.QUOTATION, policy.getStatus());

        // make sure all the entries are there - it's a quote so commission etc
        // will have been added
        assertEquals(8, policy.getAssessmentSheet().getLineCount());
        assertEquals(2, policy.getSectionById("sec1").getAssessmentSheet().getLineCount());
    }

    /**
     * Single resolution, double refer. Ensure that a policy with two referrals,
     * only one of which is resolved, ends up with a referred status.
     * <ol>
     * <li>Create a policy (based on TestPolicy1).</li>
     * <li>Create section level assessment sheet</li>
     * <ol>
     * <li>Add a base premium line of &pound;20</li>
     * <li>Add two referrals (Ids=ref1 and ref1)</li>
     * <li>Add a resolution for ref1</li>
     * </ol>
     * <li>Create an empty policy level assessment sheet</li>
     * <li>Pass the policy into calculate premium</li>
     * <li>Fail if the policy status is not set to Referred</li>
     * <li>Fail if there are not exactly 4 lines in the section's sheet</li>
     * <li>Fail if there are any entries in the policy's assessment sheet</li>
     * <li>Fail if any exceptions are thrown</li>
     * </ol>
     *
     * @throws BaseException
     */
    @Test
    public void testDoubleReferMarkerResolution() throws BaseException {
        // policy with one referral on the section.
        Policy policy = getCore().newProductType("com.ail.core.product.TestProduct11", Policy.class);

        // run it through calc premium
        CalculatePremiumCommand cmd2 = getCore().newCommand(CalculatePremiumCommand.class);
        cmd2.setPolicyArgRet(policy);
        cmd2.invoke();
        policy = cmd2.getPolicyArgRet();

        // we should get a referral the first time
        assertEquals(PolicyStatus.REFERRED, policy.getStatus());

        // mark one of the referrals as resolved
        policy.getAssessmentSheet().setLockingActor("me");
        policy.getAssessmentSheet().addLine(new MarkerResolution("res1", "nice day", new Reference(AssessmentLine.class, "dummyid3"), null));
        policy.getAssessmentSheet().clearLockingActor();

        // set the status back to Application (or calc premium will refuse to
        // process it)
        policy.setStatus(PolicyStatus.APPLICATION);

        // calc premium again
        cmd2.setPolicyArgRet(policy);
        cmd2.invoke();
        policy = cmd2.getPolicyArgRet();

        // check that we still get a referral
        assertEquals(PolicyStatus.REFERRED, policy.getStatus());

        // make sure all the entries are there - it's a quote so commission etc
        // will have been added
        assertEquals(9, policy.getAssessmentSheet().getLineCount());
        assertEquals(2, policy.getSectionById("sec1").getAssessmentSheet().getLineCount());
    }

    @Test
    public void testMultipleContributors() throws Exception {
        // policy with one referral on the section.
        Policy policy = getCore().newProductType("com.ail.core.product.TestProduct12", Policy.class);

        // run it through calc premium
        CalculatePremiumCommand cmd2 = getCore().newCommand(CalculatePremiumCommand.class);
        cmd2.setPolicyArgRet(policy);
        cmd2.invoke();
        policy = cmd2.getPolicyArgRet();

        // make sure the status comes out correct
        assertEquals(PolicyStatus.QUOTATION, policy.getStatus());

        // make sure no entries were added the the sheets
        assertEquals(9, policy.getAssessmentSheet().getLineCount());
        assertEquals(2, policy.getSectionById("sec1").getAssessmentSheet().getLineCount());

        assertTrue("Premium should be 332.63, but was:" + policy.getTotalPremium().getAmount().doubleValue(), 332.63 == policy.getTotalPremium().getAmount().doubleValue());
    }

    /**
     * Test that the policy number generator generates unique numbers.
     *
     * @throws Exception
     */
    @Test
    public void testAddPolicyNumber() throws Exception {
        CoreContext.setCoreProxy(new CoreProxy(productNameToConfigurationNamespace("com.ail.core.product.TestProduct04")));

        Policy policy = getCore().newProductType("com.ail.core.product.TestProduct04", Policy.class);

        policy.setStatus(PolicyStatus.QUOTATION);

        AddPolicyNumberCommand command = getCore().newCommand(AddPolicyNumberCommand.class);
        command.setPolicyArgRet(policy);
        command.invoke();

        assertNotNull(command.getPolicyArgRet().getPolicyNumber());
        String n1 = command.getPolicyArgRet().getPolicyNumber();

        command.getPolicyArgRet().setPolicyNumber(null);
        command.invoke();

        assertNotNull(command.getPolicyArgRet().getPolicyNumber());
        String n2 = command.getPolicyArgRet().getPolicyNumber();

        assertTrue("'" + n1 + "' should not equal '" + n2 + "'", !n1.equals(n2));
    }

    /**
     * Test that the quote number generator generates unique numbers.
     *
     * @throws Exception
     */
    @Test
    public void testAddQuoteNumber() throws Exception {
        Policy quote = getCore().newProductType("com.ail.core.product.TestProduct04", Policy.class);

        quote.setStatus(PolicyStatus.APPLICATION);

        AddQuoteNumberCommand command = getCore().newCommand(AddQuoteNumberCommand.class);
        command.setPolicyArgRet(quote);
        command.invoke();

        assertNotNull(command.getPolicyArgRet().getQuotationNumber());
        String n1 = command.getPolicyArgRet().getQuotationNumber();

        command.getPolicyArgRet().setQuotationNumber(null);
        command.invoke();

        assertNotNull(command.getPolicyArgRet().getQuotationNumber());
        String n2 = command.getPolicyArgRet().getQuotationNumber();

        assertTrue("'" + n1 + "' should not equal '" + n2 + "'", !n1.equals(n2));
    }

    @Test
    public void testEnforceCompliance() throws Exception {
        Policy quote = getCore().newProductType("com.ail.core.product.TestProduct04", Policy.class);

        quote.setStatus(PolicyStatus.APPLICATION);
        quote.setAssessmentSheet(new AssessmentSheet());
        EnforceComplianceCommand command = getCore().newCommand(EnforceComplianceCommand.class);
        command.setPolicyArgRet(quote);
        command.invoke();
    }

    @Test
    public void testAssessPaymentOptions() throws Exception {
        Policy quote = getCore().newProductType("com.ail.core.product.TestProduct04", Policy.class);

        quote.setStatus(PolicyStatus.QUOTATION);

        quote.setAssessmentSheet(new AssessmentSheet());
        quote.getAssessmentSheet().setLockingActor("test");
        quote.getAssessmentSheet().addLine(new FixedSum("total premium", "", null, null, new CurrencyAmount(120, GBP)));
        quote.getAssessmentSheet().clearLockingActor();

        AssessPaymentOptionsCommand command = getCore().newCommand(AssessPaymentOptionsCommand.class);
        command.setPolicyArg(quote);
        command.invoke();

        assertEquals(2, command.getOptionsRet().size());

        quote.setAssessmentSheet(new AssessmentSheet());
        quote.getAssessmentSheet().setLockingActor("test");
        quote.getAssessmentSheet().addLine(new FixedSum("total premium", "", null, null, new CurrencyAmount(621.10, GBP)));
        quote.getAssessmentSheet().clearLockingActor();

        command = getCore().newCommand(AssessPaymentOptionsCommand.class);
        command.setPolicyArg(quote);
        command.invoke();

        assertEquals(3, command.getOptionsRet().size());
    }

    /**
     * Test that the constrain type of control line works with a max value. The
     * constrain type of control line allows the a line's value to be
     * constrained between a min and max value. This test checks that the max
     * constraint works on FixedSum lines.
     * <ol>
     * <li>Create a test policy with an assessment sheet with three lines:
     * <ul>
     * <li>control line referring to "total premium" with a min of 100GBP and
     * max of 200GBP</li>
     * <li>fixed sum line called "total premium" with a value of 120GBP</li>
     * <li>fixed sum line with a value of 120GP with a contributesTo set to
     * "total premium"</li>
     * </ul>
     * </li>
     * <li>Invoke the RefreshAssessmentSheets service with the test policy.</li>
     * <li>Test that the value in "total premium" is 200GBP, fail otherwise.
     * </li>
     * <li>Fail if any exceptions are thrown.</li>
     * </ol>
     *
     * @throws Exception
     */
    @Test
    public void testFixedSumMaxConstrainpedValueControlLines() throws Exception {
        Policy quote = getCore().newProductType("com.ail.core.product.TestProduct04", Policy.class);

        quote.setStatus(PolicyStatus.QUOTATION);

        quote.setAssessmentSheet(new AssessmentSheet());
        quote.getSectionById("sec1").setAssessmentSheet(new AssessmentSheet());
        quote.getAssessmentSheet().setLockingActor("test");
        quote.getAssessmentSheet().addLine(new ConstrainValueOutOfBounds("premium constraint", "premium out of range.", "total premium", new CurrencyAmount(100, GBP), new CurrencyAmount(200, GBP)));
        quote.getAssessmentSheet().addLine(new FixedSum("total premium", "", null, null, new CurrencyAmount(120, GBP)));
        quote.getAssessmentSheet().addLine(new FixedSum("contributor", "", null, "total premium", new CurrencyAmount(120, GBP)));
        quote.getAssessmentSheet().clearLockingActor();

        RefreshAssessmentSheetsCommand command = getCore().newCommand(RefreshAssessmentSheetsCommand.class);
        command.setPolicyArgRet(quote);
        command.setOriginArg("test-new");
        command.setProductTypeIdArg("com.ail.core.product.TestProduct04");
        command.invoke();

        quote = command.getPolicyArgRet();

        assertEquals(new CurrencyAmount(200, GBP), quote.getTotalPremium());
    }

    /**
     * Test that the constrain type of control line works with a min value. The
     * constrain type of control line allows the a line's value to be
     * constrained between a min and max value. This test checks that the min
     * constraint works on FixedSum lines.
     * <ol>
     * <li>Create a test policy with an assessment sheet with three lines:
     * <ul>
     * <li>control line referring to "total premium" with a min of 300GBP and
     * max of 800GBP</li>
     * <li>fixed sum line called "total premium" with a value of 120GBP</li>
     * <li>fixed sum line with a value of 120GP with a contributesTo set to
     * "total premium"</li>
     * </ul>
     * </li>
     * <li>Invoke the RefreshAssessmentSheets service with the test policy.</li>
     * <li>Test that the value in "total premium" is 300GBP, fail otherwise.
     * </li>
     * <li>Fail if any exceptions are thrown.</li>
     * </ol>
     *
     * @throws Exception
     */
    @Test
    public void testFixedSumMinConstrainedValueControlLines() throws Exception {
        Policy quote = getCore().newProductType("com.ail.core.product.TestProduct04", Policy.class);

        quote.setStatus(PolicyStatus.QUOTATION);

        quote.setAssessmentSheet(new AssessmentSheet());
        quote.getSectionById("sec1").setAssessmentSheet(new AssessmentSheet());
        quote.getAssessmentSheet().setLockingActor("test");
        quote.getAssessmentSheet().addLine(new ConstrainValueOutOfBounds("premium constraint", "premium out of range.", "total premium", new CurrencyAmount(300, GBP), new CurrencyAmount(800, GBP)));
        quote.getAssessmentSheet().addFixedSum("total premium", "", new CurrencyAmount(120, GBP));
        quote.getAssessmentSheet().addFixedSum("contributor", "", "total premium", new CurrencyAmount(120, GBP));
        quote.getAssessmentSheet().clearLockingActor();

        RefreshAssessmentSheetsCommand command = getCore().newCommand(RefreshAssessmentSheetsCommand.class);
        command.setPolicyArgRet(quote);
        command.setOriginArg("test-new");
        command.setProductTypeIdArg("com.ail.core.product.TestProduct04");
        command.invoke();

        quote = command.getPolicyArgRet();

        assertEquals(new CurrencyAmount(300, GBP), quote.getTotalPremium());
    }

    /**
     * Test that the constrain type of control line works with a min value. The
     * constrain type of control line allows the a line's value to be
     * constrained between a min and max value. This test checks that the min
     * constraint works on Totalizer lines.
     * <ol>
     * <li>Create a test policy with an assessment sheet with the following
     * lines:
     * <ul>
     * <li>control line referring to "total premium" with a min of 300GBP and
     * max of 800GBP</li>
     * <li>fixed sum line called "value1" with a value of 120GBP</li>
     * <li>fixed sum line called "value2" with a value of 80GBP</li>
     * <li>fixed sum line called "value3" with a value of 20GBP</li>
     * <li>a totalizer called "total premium" with dependsOn=
     * "value1, value2, value3"</li>
     * <li>Invoke the RefreshAssessmentSheets service with the test policy.</li>
     * <li>Test that the value in "total premium" is 300GBP, fail otherwise.
     * </li>
     * <li>Fail if any exceptions are thrown.</li>
     * </ol>
     *
     * @throws Exception
     */
    @Test
    public void testSumMinConstrainedValueControlLines() throws Exception {
        Policy quote = getCore().newProductType("com.ail.core.product.TestProduct04", Policy.class);

        quote.setStatus(PolicyStatus.QUOTATION);

        quote.setAssessmentSheet(new AssessmentSheet());
        quote.getSectionById("sec1").setAssessmentSheet(new AssessmentSheet());
        quote.getAssessmentSheet().setLockingActor("test");
        quote.getAssessmentSheet().addLine(new ConstrainValueOutOfBounds("premium constraint", "premium out of range.", "total premium", new CurrencyAmount(300, GBP), new CurrencyAmount(800, GBP)));
        quote.getAssessmentSheet().addFixedSum("value1", "", new CurrencyAmount(120, GBP));
        quote.getAssessmentSheet().addFixedSum("value2", "", new CurrencyAmount(80, GBP));
        quote.getAssessmentSheet().addFixedSum("value3", "", new CurrencyAmount(20, GBP));
        quote.getAssessmentSheet().addTotalizer("total premium", "", "value1, value2, value3");
        quote.getAssessmentSheet().clearLockingActor();

        RefreshAssessmentSheetsCommand command = getCore().newCommand(RefreshAssessmentSheetsCommand.class);
        command.setPolicyArgRet(quote);
        command.setOriginArg("test-new");
        command.setProductTypeIdArg("com.ail.core.product.TestProduct04");
        command.invoke();

        quote = command.getPolicyArgRet();

        assertEquals(new CurrencyAmount(300, GBP), quote.getTotalPremium());
    }

    /**
     * Test that the constrain type of control line works with a max value. The
     * constrain type of control line allows the a line's value to be
     * constrained between a min and max value. This test checks that the max
     * constraint works on Totalizer lines.
     * <ol>
     * <li>Create a test policy with an assessment sheet with the following
     * lines:
     * <ul>
     * <li>control line referring to "total premium" with a min of 300GBP and
     * max of 310GBP</li>
     * <li>fixed sum line called "value1" with a value of 120GBP</li>
     * <li>fixed sum line called "value2" with a value of 120GBP</li>
     * <li>fixed sum line called "value3" with a value of 120GBP</li>
     * <li>a totalizer called "total premium" with dependsOn=
     * "value1, value2, value3"</li>
     * <li>Invoke the RefreshAssessmentSheets service with the test policy.</li>
     * <li>Test that the value in "total premium" is 310GBP, fail otherwise.
     * </li>
     * <li>Fail if any exceptions are thrown.</li>
     * </ol>
     *
     * @throws Exception
     */
    @Test
    public void testSumMaxConstrainedValueControlLines() throws Exception {
        Policy quote = getCore().newProductType("com.ail.core.product.TestProduct04", Policy.class);

        quote.setStatus(PolicyStatus.QUOTATION);

        quote.setAssessmentSheet(new AssessmentSheet());
        quote.getSectionById("sec1").setAssessmentSheet(new AssessmentSheet());
        quote.getAssessmentSheet().setLockingActor("test");
        quote.getAssessmentSheet().addLine(new ConstrainValueOutOfBounds("premium constraint", "premium out of range.", "total premium", new CurrencyAmount(300, GBP), new CurrencyAmount(310, GBP)));
        quote.getAssessmentSheet().addFixedSum("value1", "", new CurrencyAmount(120, GBP));
        quote.getAssessmentSheet().addFixedSum("value2", "", new CurrencyAmount(120, GBP));
        quote.getAssessmentSheet().addFixedSum("value3", "", new CurrencyAmount(120, GBP));
        quote.getAssessmentSheet().addTotalizer("total premium", "", "value1, value2, value3");
        quote.getAssessmentSheet().clearLockingActor();

        RefreshAssessmentSheetsCommand command = getCore().newCommand(RefreshAssessmentSheetsCommand.class);
        command.setPolicyArgRet(quote);
        command.setOriginArg("test-new");
        command.setProductTypeIdArg("com.ail.core.product.TestProduct04");
        command.invoke();

        quote = command.getPolicyArgRet();

        assertEquals(new CurrencyAmount(310, GBP), quote.getTotalPremium());
    }

    /**
     * Test that the constraint type of control line works with a min value. The
     * constraint type of control line allows the a line's value to be
     * constrained between a min and max value. This test checks that the min
     * constraint works on SumBehaviour lines.
     * <ol>
     * <li>Create a test policy with an assessment sheet with the following
     * lines:
     * <ul>
     * <li>control line referring to "total premium" with a min of 300GBP and
     * max of 800GBP</li>
     * <li>fixed sum line called "total premium" with a value of 400GBP</li>
     * <li>fixed sum discount called of 200GBP</li>
     * <li>fixed sum discount called of 80GBP</li>
     * <li>Invoke the RefreshAssessmentSheets service with the test policy.</li>
     * <li>Test that the value in "total premium" is 300GBP, fail otherwise.
     * </li>
     * <li>Fail if any exceptions are thrown.</li>
     * </ol>
     *
     * @throws Exception
     */
    @Test
    public void testSumBehaviourConstrainedValueControlLines() throws Exception {
        Policy quote = getCore().newProductType("com.ail.core.product.TestProduct04", Policy.class);

        quote.setStatus(PolicyStatus.QUOTATION);

        quote.setAssessmentSheet(new AssessmentSheet());
        quote.getSectionById("sec1").setAssessmentSheet(new AssessmentSheet());
        quote.getAssessmentSheet().setLockingActor("test");
        quote.getAssessmentSheet().addLine(new ConstrainValueOutOfBounds("premium constraint", "premium out of range.", "total premium", new CurrencyAmount(300, GBP), new CurrencyAmount(800, GBP)));
        quote.getAssessmentSheet().addDiscount("value1", "dicsount 1", "total premium", new CurrencyAmount(200, GBP));
        quote.getAssessmentSheet().addDiscount("value2", "discount 2", "total premium", new CurrencyAmount(80, GBP));
        quote.getAssessmentSheet().addFixedSum("total premium", "", new CurrencyAmount(400, GBP));
        quote.getAssessmentSheet().clearLockingActor();

        RefreshAssessmentSheetsCommand command = getCore().newCommand(RefreshAssessmentSheetsCommand.class);
        command.setPolicyArgRet(quote);
        command.setOriginArg("test-new");
        command.setProductTypeIdArg("com.ail.core.product.TestProduct04");
        command.invoke();

        quote = command.getPolicyArgRet();

        assertEquals(new CurrencyAmount(300, GBP), quote.getTotalPremium());
    }

    /**
     * Test that the constraint type of control line works with a max value. The
     * constraint type of control line allows the a line's value to be
     * constrained between a min and max value. This test checks that the max
     * constraint works on RateBehaviour lines.
     * <ol>
     * <li>Create a test policy with an assessment sheet with the following
     * lines:
     * <ul>
     * <li>control line referring to "total premium" with a min of 300GBP and
     * max of 800GBP</li>
     * <li>fixed sum line called "total premium" with a value of 600GBP</li>
     * <li>fixed rate based loading of 50%</li>
     * <li>Invoke the RefreshAssessmentSheets service with the test policy.</li>
     * <li>Test that the value in "total premium" is 800GBP, fail otherwise.
     * </li>
     * <li>Fail if any exceptions are thrown.</li>
     * </ol>
     *
     * @throws Exception
     */
    @Test
    public void testRateBehaviourConstrainedValueControlLines() throws Exception {
        Policy quote = getCore().newProductType("com.ail.core.product.TestProduct04", Policy.class);

        quote.setStatus(PolicyStatus.QUOTATION);

        quote.setAssessmentSheet(new AssessmentSheet());
        quote.getSectionById("sec1").setAssessmentSheet(new AssessmentSheet());
        quote.getAssessmentSheet().setLockingActor("test");
        quote.getAssessmentSheet().addLine(new ConstrainValueOutOfBounds("premium constraint", "premium out of range.", "total premium", new CurrencyAmount(300, GBP), new CurrencyAmount(800, GBP)));
        quote.getAssessmentSheet().addFixedSum("total premium", "", new CurrencyAmount(600, GBP));
        quote.getAssessmentSheet().addLoading("rate1", "loading 1", "total premium", "total premium", new Rate("50%"));
        quote.getAssessmentSheet().clearLockingActor();

        RefreshAssessmentSheetsCommand command = getCore().newCommand(RefreshAssessmentSheetsCommand.class);
        command.setPolicyArgRet(quote);
        command.setOriginArg("test-new");
        command.setProductTypeIdArg("com.ail.core.product.TestProduct04");
        command.invoke();

        quote = command.getPolicyArgRet();

        assertEquals(new CurrencyAmount(800, GBP), quote.getTotalPremium());
    }

    /**
     * Test that the constraint type of control line works with a max value. The
     * constraint type of control line allows the a line's value to be
     * constrained between a min and max value. This test checks that the max
     * constraint works on RateBehaviour lines.
     * <ol>
     * <li>Create a test policy with an assessment sheet with the following
     * lines:
     * <ul>
     * <li>control line referring to "total premium" with a min of 100GBP and
     * max of 200GBP</li>
     * <li>fixed sum line called "total premium" with a value of 200GBP</li>
     * <li>2 X fixed rate based loading of 10%</li>
     * <li>Invoke the RefreshAssessmentSheets service with the test policy.</li>
     * <li>Test that the value in "total premium" is 200GBP, fail otherwise.
     * </li>
     * <li>Fail if any exceptions are thrown.</li>
     * </ol>
     *
     * @throws Exception
     */
    @Test
    public void testRateBehaviourConstrainedValueControlLinesWithTwoLoadings() throws Exception {
        Policy quote = getCore().newProductType("com.ail.core.product.TestProduct04", Policy.class);

        quote.setStatus(PolicyStatus.QUOTATION);

        quote.setAssessmentSheet(new AssessmentSheet());
        quote.getSectionById("sec1").setAssessmentSheet(new AssessmentSheet());
        quote.getAssessmentSheet().setLockingActor("test");
        quote.getAssessmentSheet().addLine(new ConstrainValueOutOfBounds("premium constraint", "premium out of range.", "total premium", new CurrencyAmount(100, GBP), new CurrencyAmount(200, GBP)));
        quote.getAssessmentSheet().addFixedSum("total premium", "", new CurrencyAmount(200, GBP));
        quote.getAssessmentSheet().addLoading("rate1", "loading 1", "total premium", "total premium", new Rate("10%"));
        quote.getAssessmentSheet().addLoading("rate2", "loading 2", "total premium", "total premium", new Rate("10%"));
        quote.getAssessmentSheet().clearLockingActor();

        RefreshAssessmentSheetsCommand command = getCore().newCommand(RefreshAssessmentSheetsCommand.class);
        command.setPolicyArgRet(quote);
        command.setOriginArg("test-new");
        command.setProductTypeIdArg("com.ail.core.product.TestProduct04");
        command.invoke();

        quote = command.getPolicyArgRet();

        assertEquals(new CurrencyAmount(200, GBP), quote.getTotalPremium());
    }

    /**
     * Test that the referral type of control line works with a max value. The
     * referral type of control line allows the a line's value to be monitored
     * through out the rating process and for a referral to be automatically
     * raised if that line's value goes outside of predefined limits.
     * <ol>
     * <li>Create a test policy with an assessment sheet with the following
     * lines:
     * <ul>
     * <li>referral control line monitoring "total premium" with a min of 300GBP
     * and max of 800GBP</li>
     * <li>fixed sum line called "total premium" with a value of 600GBP</li>
     * <li>fixed sum loading of 300GBP</li>
     * <li>Invoke the RefreshAssessmentSheets service with the test policy.</li>
     * <li>A referral should be found in the assessment sheet, fail otherwise.
     * </li>
     * <li>Fail if any exceptions are thrown.</li>
     * </ol>
     *
     * @throws Exception
     */
    @Test
    public void testReferControlLines() throws Exception {
        Policy quote = getCore().newProductType("com.ail.core.product.TestProduct04", Policy.class);

        quote.setStatus(PolicyStatus.QUOTATION);

        quote.setAssessmentSheet(new AssessmentSheet());
        quote.getSectionById("sec1").setAssessmentSheet(new AssessmentSheet());
        quote.getAssessmentSheet().setLockingActor("test");
        quote.getAssessmentSheet().addLine(new ReferValueOutOfBounds("premium constraint", "premium out of range.", "total premium", new CurrencyAmount(300, GBP), new CurrencyAmount(800, GBP)));
        quote.getAssessmentSheet().addFixedSum("total premium", "premium", new CurrencyAmount(600, GBP));
        quote.getAssessmentSheet().addLoading("loading", "loading 1", "total premium", new CurrencyAmount(300, GBP));
        quote.getAssessmentSheet().clearLockingActor();

        RefreshAssessmentSheetsCommand command = getCore().newCommand(RefreshAssessmentSheetsCommand.class);
        command.setPolicyArgRet(quote);
        command.setOriginArg("test-new");
        command.setProductTypeIdArg("com.ail.core.product.TestProduct04");
        command.invoke();

        quote = command.getPolicyArgRet();

        assertTrue(quote.getAssessmentSheet().isMarkedForRefer());
    }

    /**
     * Test that the referral type of control line works with a max value. The
     * referral type of control line allows the a line's value to be monitored
     * through out the rating process and for a referral to be automatically
     * raised if that line's value goes outside of predefined limits.
     * <ol>
     * <li>Create a test policy with an assessment sheet with the following
     * lines:
     * <ul>
     * <li>referral control line monitoring "total premium" with a min of 300GBP
     * and max of 800GBP</li>
     * <li>fixed sum line called "total premium" with a value of 600GBP</li>
     * <li>fixed sum loading of 300GBP</li>
     * <li>Invoke the RefreshAssessmentSheets service with the test policy.</li>
     * <li>A referral should be found in the assessment sheet, fail otherwise.
     * </li>
     * <li>Fail if any exceptions are thrown.</li>
     * </ol>
     *
     * @throws Exception
     */
    @Test
    public void testDependAndContribute() throws Exception {
        Policy quote = getCore().newProductType("com.ail.core.product.TestProduct04", Policy.class);

        quote.setStatus(PolicyStatus.QUOTATION);

        quote.setAssessmentSheet(new AssessmentSheet());
        quote.getSectionById("sec1").setAssessmentSheet(new AssessmentSheet());
        quote.getAssessmentSheet().setLockingActor("test");
        quote.getAssessmentSheet().addLine(new ConstrainValueOutOfBounds("premium constraint", "premium out of range.", "total premium", new CurrencyAmount(300, GBP), new CurrencyAmount(800, GBP)));
        quote.getAssessmentSheet().addFixedSum("total premium", "", new CurrencyAmount(100, GBP));
        quote.getAssessmentSheet().addLoading("tax line", "tax line", "total premium", "total premium", new Rate("15%"));
        quote.getAssessmentSheet().clearLockingActor();

        RefreshAssessmentSheetsCommand command = getCore().newCommand(RefreshAssessmentSheetsCommand.class);
        command.setPolicyArgRet(quote);
        command.setOriginArg("test-new");
        command.setProductTypeIdArg("com.ail.core.product.TestProduct04");
        command.invoke();

        quote = command.getPolicyArgRet();

        CalculationLine tax = (CalculationLine) quote.getAssessmentSheet().findLineById("tax line");
        assertEquals(new CurrencyAmount(15, GBP), tax.getAmount());
    }

    @Test
    public void testTotalizerLines() throws Exception {
        Policy quote = getCore().newProductType("com.ail.core.product.TestProduct04", Policy.class);

        quote.setStatus(PolicyStatus.QUOTATION);

        quote.setAssessmentSheet(new AssessmentSheet());
        quote.getSectionById("sec1").setAssessmentSheet(new AssessmentSheet());
        quote.getAssessmentSheet().setLockingActor("test");
        quote.getAssessmentSheet().addFixedSum("line1", "testing 1", new CurrencyAmount(100.00, GBP));
        quote.getAssessmentSheet().addFixedSum("line2", "testing 2", new CurrencyAmount(25.00, GBP));
        quote.getAssessmentSheet().addFixedSum("line3", "testing 3", new CurrencyAmount(50.00, GBP));
        quote.getAssessmentSheet().addTotalizer("total", "total", "line1,line2,line3");
        quote.getAssessmentSheet().clearLockingActor();

        RefreshAssessmentSheetsCommand command = getCore().newCommand(RefreshAssessmentSheetsCommand.class);
        command.setPolicyArgRet(quote);
        command.setOriginArg("test-new");
        command.setProductTypeIdArg("com.ail.core.product.TestProduct04");
        command.invoke();

        quote = command.getPolicyArgRet();

        assertEquals(new CurrencyAmount(175.00, GBP), quote.xpathGet("assessmentSheet/line[id='total']/amount"));
    }

    @Test
    public void testNominator() {
        Rate rate = new Rate("11%");
        assertEquals(new BigDecimal("11"), rate.getNominator());
    }
}
