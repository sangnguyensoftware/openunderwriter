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

package com.ail.core.audit;

import static com.ail.insurance.policy.PolicyStatus.ON_RISK;
import static com.ail.insurance.policy.PolicyStatus.QUOTATION;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.CoreProxy;
import com.ail.core.CoreUserBaseCase;
import com.ail.core.ThreadLocale;
import com.ail.insurance.policy.Policy;

public class TestPolicyAudit extends CoreUserBaseCase {
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

            List<Number> revisions = coreProxy.fetchVersionNumbers(Policy.class, policySystemId);

            assertThat(revisions, hasSize(2));

            coreProxy.closePersistenceSession();
        }
    }
}
