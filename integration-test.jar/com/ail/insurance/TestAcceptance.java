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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.CoreProxy;
import com.ail.core.CoreUserBaseCase;
import com.ail.core.PreconditionException;
import com.ail.core.VersionEffectiveDate;
import com.ail.financial.PaymentSchedule;
import com.ail.insurance.acceptance.AcceptQuotationService.AcceptQuotationCommand;
import com.ail.insurance.acceptance.PolicyDocumentation;
import com.ail.insurance.acceptance.PremiumCollectionRequestService.PremiumCollectionRequestCommand;
import com.ail.insurance.acceptance.ProduceDocumentationService.ProduceDocumentationCommand;
import com.ail.insurance.acceptance.PutOnRiskService.PutOnRiskCommand;
import com.ail.insurance.policy.Policy;
import com.ail.insurance.policy.PolicyStatus;

public class TestAcceptance extends CoreUserBaseCase {
    private static final long serialVersionUID = -1883228598369537657L;

	/**
	 * Sets up the fixture (run before every test).
	 * Get an instance of Core, and delete the testnamespace from the config table.
	 */
    @Before
    public void setUp() {
        CoreProxy coreProxy = new CoreProxy(this.getConfigurationNamespace(), this);
        setCore(coreProxy.getCore());
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
	 * @throws Exception
	 */
    @After
    public void tearDown() throws Exception {
		CoreContext.getCoreProxy().closePersistenceSession();
		CoreContext.destroy();
        tidyUpTestData();
	}


	/**
	 * Test put on risk from quotation status
	 * @throws Exception
	 */
    @Test
    public void testPutOnRiskSuccess(){

        Policy policy = (Policy)getCore().newProductType("com.ail.core.product.TestProduct04", Policy.class);

		policy.setId("pol1");
		policy.setPolicyNumber("pol1");
		policy.setStatus(PolicyStatus.QUOTATION);

		// run command
		PutOnRiskCommand command = getCore().newCommand(PutOnRiskCommand.class);
		command.setPolicyArgRet(policy);

		try {
			command.invoke();
		} catch (BaseException e) {
			e.printStackTrace();
			fail("put on risk failed");
		}
		policy = command.getPolicyArgRet();

		// check that the status is set
		assertNotNull(policy);
		assertNotNull(policy.getStatus());
		assertEquals(PolicyStatus.ON_RISK, policy.getStatus());

	}

	/**
	 * Test put on risk from refer status
	 * @throws Exception
	 */
    @Test
	public void testPutOnRiskFail() throws Exception {

		// create policy
		Policy policy = new Policy();
		policy.setId("pol1");
		policy.setPolicyNumber("pol1");
		policy.setStatus(PolicyStatus.REFERRED);

		// run command
		PutOnRiskCommand command = getCore().newCommand(PutOnRiskCommand.class);
        command.setPolicyArgRet(policy);

        try {
			command.invoke();
		} catch (PreconditionException e) {
		    // this is what we like
        }
	}

	/**
	 * Test collect premium from on risk status & no payment details
	 * @throws Exception
	 */
    @Test
	public void testCollectPremiumFail() throws Exception {

		// create policy
		Policy policy = new Policy();
		policy.setId("pol1");
		policy.setPolicyNumber("pol1");
		policy.setStatus(PolicyStatus.ON_RISK);

		// run command
		PremiumCollectionRequestCommand command = getCore().newCommand(PremiumCollectionRequestCommand.class);
		command.setPolicyArgRet(policy);
		try {
			command.invoke();
            fail("collect premium should fail due to no payment details");
		} catch (PreconditionException e) {
		    // ignore this - it's what we're looking for
        }
	}

	/**
	 * Test produce documentation from on risk status
	 * @throws Exception
	 */
    @Test
	public void testProduceDocumentationSuccess() throws Exception {

		// create policy
		Policy policy = new Policy();
		policy.setId("pol1");
		policy.setPolicyNumber("pol1");
		policy.setStatus(PolicyStatus.ON_RISK);

		// run command
		ProduceDocumentationCommand command = getCore().newCommand(ProduceDocumentationCommand.class);
		command.setPolicyArg(policy);
		command.invoke();

		// check that the documentation is set
		PolicyDocumentation docs = command.getDocumentationRet();
		assertNotNull(docs);
	}


	/**
	 * Test produce documentation from refer staus
	 * @throws Exception
	 */
    @Test
	public void testProduceDocumentationFail() throws Exception {

		// create policy
		Policy policy = new Policy();
		policy.setId("pol1");
		policy.setPolicyNumber("pol1");
		policy.setStatus(PolicyStatus.REFERRED);

		// run command
		ProduceDocumentationCommand command = getCore().newCommand(ProduceDocumentationCommand.class);
		command.setPolicyArg(policy);
		try {
		    command.invoke();
        }
        catch(PreconditionException e) {
            // this is what we want
        }
	}

    @Test
    public void testAcceptQuotationSuccess() throws Exception {
        // create policy
        Policy policy = new Policy();
        policy.setStatus(PolicyStatus.QUOTATION);
        policy.setPaymentDetails(new PaymentSchedule());

        AcceptQuotationCommand cmd = getCore().newCommand(AcceptQuotationCommand.class);
        cmd.setPolicyArgRet(policy);
        cmd.invoke();

        assertTrue(PolicyStatus.SUBMITTED.equals(policy.getStatus()));
    }
}
