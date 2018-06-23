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

package com.ail.core.persistence.hibernate;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.hibernate.ObjectNotFoundException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.ail.core.Attribute;
import com.ail.core.BaseException;
import com.ail.core.Core;
import com.ail.core.CoreContext;
import com.ail.core.CoreUserBaseCase;
import com.ail.core.ForeignSystemReference;
import com.ail.core.PreconditionException;
import com.ail.core.VersionEffectiveDate;
import com.ail.core.configure.AbstractConfigurationLoader;
import com.ail.core.configure.Group;
import com.ail.core.configure.Parameter;
import com.ail.core.document.Document;
import com.ail.core.document.DocumentContent;
import com.ail.core.persistence.CloseSessionService.CloseSessionCommand;
import com.ail.core.persistence.ConnectionError;
import com.ail.core.persistence.CreateService.CreateCommand;
import com.ail.core.persistence.LoadService.LoadCommand;
import com.ail.core.persistence.OpenSessionService.OpenSessionCommand;
import com.ail.core.persistence.QueryService.QueryCommand;
import com.ail.core.persistence.UpdateService.UpdateCommand;
import com.ail.financial.Currency;
import com.ail.financial.CurrencyAmount;
import com.ail.financial.FinancialFrequency;
import com.ail.financial.MoneyProvision;
import com.ail.financial.MoneyProvisionStatus;
import com.ail.financial.PayPal;
import com.ail.financial.PaymentCard;
import com.ail.financial.PaymentSchedule;
import com.ail.insurance.policy.Policy;

/**
 */
public class TestCoreHibernatePersistence extends CoreUserBaseCase {
    private static boolean onetimeSetupDone = false;

    @Before
    public void setUp() throws Exception {

        setCore(new Core(this));

        if (!onetimeSetupDone) {
            CoreContext.initialise();

            setupSystemProperties();

            tidyUpTestData();

            setupConfigurations();

            onetimeSetupDone=true;
        }

        truncateTables("jAttAtt", "attAttribute", "cngConfigureGroup");

        setVersionEffectiveDate(new VersionEffectiveDate());
    }

    /**
     * @throws Exception
     */
    @Test
    public void testHibernateCreateDirectAccess() throws Exception {
        getCore().newCommand(OpenSessionCommand.class).invoke();

        CreateCommand command = getCore().newCommand(CreateCommand.class);

        try {
            command.invoke();
        }
        catch (PreconditionException e) {
            // exception is what this test expects
            return;
        }
        finally {
            getCore().newCommand(CloseSessionCommand.class).invoke();
        }

        fail("create with null args should throw a PreconditionException");
    }

    /**
     * @throws Exception
     */
    @Test
    public void testHibernateCreateFailPredifnedSerialVersion() throws Exception {
        getCore().newCommand(OpenSessionCommand.class).invoke();

        // set up object
        Attribute attr = new Attribute();
        attr.setId("ATTR1");
        attr.setFormat("int");
        attr.setUnit("feet");
        attr.setValue("21");
        attr.setSerialVersion(1);

        CreateCommand command = getCore().newCommand(CreateCommand.class);
        command.setObjectArg(attr);

        try {
            command.invoke();
        }
        catch (BaseException e) {
            return;
        }
        catch (ConnectionError e) {
            e.printStackTrace();
            fail("Test couldn't connect.");
            return;
        }
        finally {
            getCore().newCommand(CloseSessionCommand.class).invoke();
        }
        fail("create should fail due to pre-defined serial version");
    }

    /**
     * @throws Exception
     */
    @Test
    public void testHibernateCreateSuccess() throws Exception {
        getCore().newCommand(OpenSessionCommand.class).invoke();

        // set up object
        Attribute attr = new Attribute();
        attr.setId("ATTR1");
        attr.setFormat("int");
        attr.setUnit("feet");
        attr.setValue("21");

        CreateCommand command = getCore().newCommand(CreateCommand.class);
        command.setObjectArg(attr);

        try {
            command.invoke();
        }
        catch (Exception e) {
            e.printStackTrace();
            fail("create failed");
        }
        finally {
            getCore().newCommand(CloseSessionCommand.class).invoke();
        }
    }

    /**
     * @throws Exception
     */
    @Test
    public void testHibernateCreateFailMissingObject() throws Exception {
        getCore().newCommand(OpenSessionCommand.class).invoke();

        CreateCommand command = getCore().newCommand(CreateCommand.class);

        try {
            command.invoke();
        }
        catch (BaseException e) {
            return;
        }
        catch (ConnectionError e) {
            fail("Test couldn't connect.");
            return;
        }
        finally {
            getCore().newCommand(CloseSessionCommand.class).invoke();
        }

        fail("create should fail due to missing object in arg failed");
    }

    /**
     * @throws Exception
     */
    @Test
    public void testHibernateUpdateSuccess() throws Exception {
        getCore().newCommand(OpenSessionCommand.class).invoke();

        Attribute attr = new Attribute();
        attr.setId("ATTR1");
        attr.setFormat("int");
        attr.setUnit("feet");
        attr.setValue("21");

        CreateCommand create = getCore().newCommand(CreateCommand.class);
        create.setObjectArg(attr);
        create.invoke();

        attr = (Attribute) create.getObjectArg();
        attr.setId("ATTR1");
        attr.setFormat("int");
        attr.setUnit("feet");
        attr.setValue("22");

        UpdateCommand update = getCore().newCommand(UpdateCommand.class);
        update.setObjectArg(attr);

        try {
            update.invoke();
        }
        catch (BaseException e) {
            e.printStackTrace();
            fail("update failed");
        }
        finally {
            getCore().newCommand(CloseSessionCommand.class).invoke();
       }
    }

    /**
     * <ol>
     * <li>Create a record</li>
     * <li>Update the record</li>
     * <li>Update the record using the same serialVersionId - this update should fail</li>
     * </ol>
     * @throws Exception
     */
    @Test
    public void testHibernateUpdateFailWrongSerialVersion() throws Exception {
        Attribute attr;

        {
            getCore().newCommand(OpenSessionCommand.class).invoke();

            attr = new Attribute();
            attr.setId("ATTR2");
            attr.setFormat("int");
            attr.setUnit("feet");
            attr.setValue("21");

            CreateCommand create = getCore().newCommand(CreateCommand.class);
            create.setObjectArg(attr);
            create.invoke();

            getCore().newCommand(CloseSessionCommand.class).invoke();
        }

        long svid=attr.getSerialVersion();

        {
            getCore().newCommand(OpenSessionCommand.class).invoke();

            attr.setId("PLOP");
            UpdateCommand upate = getCore().newCommand(UpdateCommand.class);
            upate.setObjectArg(attr);
            upate.invoke();

            getCore().newCommand(CloseSessionCommand.class).invoke();
        }

        attr.setSerialVersion(svid);

        {
            getCore().newCommand(OpenSessionCommand.class).invoke();

            UpdateCommand command = getCore().newCommand(UpdateCommand.class);
            command.setObjectArg(attr);

            try {
                command.invoke();
                getCore().newCommand(CloseSessionCommand.class).invoke();

                fail("update should failed due to wrong serial version");
            }
            catch (BaseException e) {
                // This is what we want.
            }
            catch (ConnectionError e) {
                fail("Test couldn't connect.");
            }
        }
    }

    /**
     * Test direct access to the validator
     *
     * @throws Exception
     */
    @Test
    public void testHibernateUpdateFailMissingObject() throws Exception {
        getCore().newCommand(OpenSessionCommand.class).invoke();

        UpdateCommand command = getCore().newCommand(UpdateCommand.class);

        try {
            command.invoke();
        }
        catch (BaseException e) {
            return;
        }
        catch (ConnectionError e) {
            fail("Test couldn't connect.");
            return;
        }
        finally {
            getCore().newCommand(CloseSessionCommand.class).invoke();
        }

        fail("update should fail due to missing object in arg failed");
    }

    /**
     * Test that a persisted object can be retrieved using the Load service.
     * <ol>
     * <li>Create an instance of TestCoreHibernatePersistenceTestObject (id=1, value="Test Object")</li>
     * <li>Persist the instance (Using the TestCreateService).</li>
     * <li>Create an instance of the TestLoadService command.</li>
     * <li>Set the command to locate an instance of TestCoreHibernatePersistenceTestObject with an id of 32.</li>
     * <li>Invoke the command.
     * <li>
     * <li>Check that the result returned is an instance of TestCoreHibernatePersistenceTestObject</li>
     * <li>Check that the result has an id of 32</li>
     * <li>Check that the result has a value of "Test Object"</li>
     * <li>Fail if any checks fail</li>
     * <li>Fail if any exceptions are thrown</li>
     * </ol>
     */
    @Test
    public void testHibernateLoadSuccess() throws Exception {
        getCore().newCommand(OpenSessionCommand.class).invoke();

        Attribute attr = new Attribute();
        attr.setId("ATTR1");
        attr.setFormat("int");
        attr.setUnit("feet");
        attr.setValue("21");

        CreateCommand command1 = getCore().newCommand(CreateCommand.class);
        command1.setObjectArg(attr);
        command1.invoke();

        LoadCommand command = getCore().newCommand(LoadCommand.class);
        command.setTypeArg(Attribute.class);
        command.setSystemIdArg(command1.getObjectArg().getSystemId());
        command.invoke();

        assertNotNull("Object returned was null", command.getObjectRet());
        assertTrue("Object returned is of wrong type", command.getObjectRet() instanceof Attribute);
        Attribute res = (Attribute) command.getObjectRet();
        assertEquals("Objet returned has wrong ID", "ATTR1", res.getId());
        assertEquals("Object returned has wrong value", "21", res.getValue());

        getCore().newCommand(CloseSessionCommand.class).invoke();
    }

    /**
     * Test that a persisted object can be retrieved using the Query service.
     * <ol>
     * <li>Create an instance of TestCoreHibernatePersistenceTestObject (id=1, value="Test Object")</li>
     * <li>Persist the instance (Using the TestCreateService).</li>
     * <li>Create an instance of the TestLoadService command.</li>
     * <li>Set the command to locate an instance of TestCoreHibernatePersistenceTestObject with an id of 32.</li>
     * <li>Invoke the command.
     * <li>
     * <li>Check that the result returned is an instance of TestCoreHibernatePersistenceTestObject</li>
     * <li>Check that the result has an id of 32</li>
     * <li>Check that the result has a value of "Test Object"</li>
     * <li>Fail if any checks fail</li>
     * <li>Fail if any exceptions are thrown</li>
     * </ol>
     */
    @Test
    public void testHibernateQuerySuccess() throws Exception {
        {
            getCore().newCommand(OpenSessionCommand.class).invoke();

            Attribute attr = new Attribute();
            attr.setId("ATTR1");
            attr.setFormat("int");
            attr.setUnit("feet");
            attr.setValue("21");

            CreateCommand command1 = getCore().newCommand(CreateCommand.class);
            command1.setObjectArg(attr);
            command1.invoke();

            getCore().newCommand(CloseSessionCommand.class).invoke();
        }

        {
            getCore().newCommand(OpenSessionCommand.class).invoke();

            QueryCommand command = getCore().newCommand(QueryCommand.class);
            command.setQueryNameArg("get.attribute.by.unit");
            command.setQueryArgumentsArg("feet");

            command.invoke();

            assertNotNull("Object returned was null", command.getResultsListRet());
            assertTrue("Query returned no objects", command.getResultsListRet().size()>0);
            assertTrue("Object returned is of wrong type", command.getUniqueResultRet() instanceof Attribute);
            Attribute res = (Attribute) command.getUniqueResultRet();
            assertEquals("Objet returned has wrong ID", "ATTR1", res.getId());
            assertEquals("Object returned has wrong value", "21", res.getValue());

            getCore().newCommand(CloseSessionCommand.class).invoke();
        }
    }

    /**
     * The Core exposes persistence methods in an easy-to-use form. This test
     * runs through some simple scenarios using those methods.
     * @throws Exception
     */
    @Test
    public void testCorePersistenceMethods() throws Exception {
        Attribute attr1;
        Attribute attr2;

        {
            getCore().openPersistenceSession();

            attr1=new Attribute("ATTR1", "12", "int", "feet");

            attr1=(Attribute)getCore().create(attr1);
            attr2=getCore().load(Attribute.class, attr1.getSystemId());

            assertEquals("ATTR1", attr2.getId());
            assertEquals("12", attr2.getValue());
            assertEquals("feet", attr2.getUnit());
            assertEquals("int", attr2.getFormat());
            assertEquals(attr1.getSystemId(), attr2.getSystemId());
            assertEquals(attr1.getSerialVersion(), attr2.getSerialVersion());

            getCore().closePersistenceSession();
        }

        {
            getCore().openPersistenceSession();

            attr2.setValue("22");
            attr2.setId("NEWATTR");

            getCore().update(attr2);

            assertEquals("NEWATTR", attr2.getId());
            assertEquals("22", attr2.getValue());
            assertEquals("feet", attr2.getUnit());
            assertEquals("int", attr2.getFormat());

            getCore().closePersistenceSession();
        }

        {
            Attribute attr;

            getCore().openPersistenceSession();

            attr=(Attribute)getCore().queryUnique("get.attribute.by.unit", "feet");
            assertEquals("NEWATTR", attr.getId());
            assertEquals("22", attr.getValue());
            assertEquals("feet", attr.getUnit());
            assertEquals("int", attr.getFormat());

            getCore().closePersistenceSession();
        }

        {
            getCore().openPersistenceSession();

            for(int i=0 ; i<100 ; i++) {
                getCore().create(new Attribute("ATTR", "Val "+i, "string", "inch"));
            }

            List<?> l=getCore().query("get.attribute.by.unit", "inch");

            assertEquals(100, l.size());

            getCore().closePersistenceSession();
        }

        {
            getCore().openPersistenceSession();

            for(int i=0 ; i<10 ; i++) {
                getCore().create(new Attribute("ATTR", "Val "+i, "string", "cm"));
            }

            List<?> l=getCore().query(" where unit = 'cm'", Attribute.class);

            assertEquals(10, l.size());

            getCore().closePersistenceSession();

        }
    }

    @Test
    @Ignore
    public void testAttributesWithinAttributes() {
        Attribute attr=new Attribute("PARENT", "JOE", "string");

        for(int i=0 ; i<10 ; i++) {
            attr.addAttribute(new Attribute("CHILD"+i, "JIM", "string"));
        }

        {
            getCore().openPersistenceSession();

            // This should cascade and create all the child attributes too.
            getCore().create(attr);

            getCore().closePersistenceSession();
        }

        {
            getCore().openPersistenceSession();

            Attribute attr2=getCore().load(Attribute.class, attr.getSystemId());

            System.out.println(getCore().toXML(attr2));

            getCore().closePersistenceSession();
        }
    }

    @Test
    public void testDocumentUpdate() {
        Long documentUID;

        {
            getCore().openPersistenceSession();

            Document document = new Document("Type", new byte[1], "title", "filename", "mime-type", "product-type-id");

            documentUID = getCore().create(document).getSystemId();

            getCore().closePersistenceSession();
        }

        {
            getCore().openPersistenceSession();

            Document document = (Document)getCore().queryUnique("get.document.by.systemId", documentUID);

            getCore().update(document);

            getCore().closePersistenceSession();
        }
    }

    @Test
    public void testPopulationOfExternalSystemId() {
        String documentUUID;

        {
            getCore().openPersistenceSession();

            Document document = new Document("Type", new byte[1], "title", "filename", "mime-type", "product-type-id");

            documentUUID = getCore().create(document).getExternalSystemId();

            assertThat(documentUUID, is(notNullValue(String.class)));
            assertThat(documentUUID, is(not("")));

            getCore().closePersistenceSession();
        }

        {
            getCore().openPersistenceSession();

            Document document = (Document)getCore().queryUnique("get.document.by.externalSystemId", documentUUID);

            getCore().update(document);

            getCore().closePersistenceSession();
        }
    }

    /**
     * Test the persistence of a deep hierarchy. Actually not all that deep, but deep enough to prove the point.
     * The configure system defines a class hierarchy which is used to hold configurations. It's more or less
     * a copy-book implementation of the composite pattern. We'll play with two types here: Parameter and Group;
     * Both extend Component, and Component extends Type in common with all other model types in the system. Group
     * contains a list of Parameters, and a list of Groups.
     *
     * <ol>
     * <li>Create an in memory instance of a Group, with on Parameter in it.</li>
     * <li>Create a persistence session, store the instance, get it's id, close the session.</li>
     * <li>Create a new session, load the object using it's id, check the content of the instance, close the session.</li>
     * <li>Create another session, load the object again, and delete it, then close the session</li>
     * <li>Create a session, try to load the object using it's id again and try to check it's content. close the session. Fail if this works!</li>
     * <li>Fail if any exceptions are thrown from the above steps (except the last one).</li>
     * </ol>
     */
    @Test
    @Ignore
    public void testDeepTypeHierarchy() {
        long groupId;

        Group g=new Group();
        g.setName("my group");

        Parameter p=new Parameter();
        p.setName("myParameter");
        p.setValue("parameter value");

        p.addAttribute(new Attribute("attr1", "attr1's value", "string"));
        p.addAttribute(new Attribute("attr2", "attr2's value", "string"));

        g.addParameter(p);

        {
            getCore().openPersistenceSession();

            g=getCore().create(g);

            groupId=g.getSystemId();

            getCore().closePersistenceSession();
        }

        {
            getCore().openPersistenceSession();

            Group g1=getCore().load(Group.class, groupId);

            assertNotNull(g1);
            assertNotNull(g1.findParameter("myParameter"));
            assertEquals("parameter value", g1.findParameterValue("myParameter"));

            getCore().closePersistenceSession();
        }

        {
            getCore().openPersistenceSession();

            Group g1=getCore().load(Group.class, groupId);

            getCore().delete(g1);

            getCore().closePersistenceSession();
        }

        try {
            getCore().openPersistenceSession();

            Group g1=getCore().load(Group.class, groupId);

            assertNotNull(g1);
            assertNotNull(g1.findParameter("myParameter"));
            assertEquals("parameter value", g1.findParameterValue("myParameter"));

            getCore().closePersistenceSession();

            fail("Loaded and object that doesn't exist, and read values from it? That just isn't right.");
        }
        catch(ObjectNotFoundException e) {
            //this is okay
        }
        catch(Throwable e) {
            fail("Should have got an ObjectNotFound, but got a "+e+" instead");
        }
        finally {
            getCore().closePersistenceSession();
        }
    }

    @Test
    public void testMoneyProvisionPersistence() {
        long moneyProvisionId;

        {
            MoneyProvision mp = new MoneyProvision();
            mp.setAmount(new CurrencyAmount(10, Currency.GBP));
            mp.setDescription("Money provision test");
            mp.setFrequency(FinancialFrequency.MONTHLY);
            mp.setPaymentMethod(new PayPal());

            getCore().openPersistenceSession();

            moneyProvisionId = getCore().create(mp).getSystemId();

            getCore().closePersistenceSession();
        }

        {
            getCore().openPersistenceSession();

            MoneyProvision mp = getCore().load(MoneyProvision.class, moneyProvisionId);
            assertThat(mp, is(notNullValue()));
            assertThat(mp.getDescription(), is("Money provision test"));
            assertThat(mp.getFrequency(), is(FinancialFrequency.MONTHLY));
            assertThat(mp.getPaymentMethod(), is(notNullValue()));
            assertThat(mp.getPaymentMethod(), is(instanceOf(PayPal.class)));
            assertThat(mp.getAmount(), is(new CurrencyAmount(10, Currency.GBP)));

            getCore().closePersistenceSession();
        }
    }

    @Test
    public void testPaymentSchedulePersistence() {
        long paymentScheduleId;

        {
            PaymentSchedule ps=new PaymentSchedule();
            ps.setDescription("schedule description");
            ps.setMoneyProvision(new ArrayList<MoneyProvision>());

            ps.getMoneyProvision().add(new MoneyProvision());
            ps.getMoneyProvision().get(0).setAmount(new CurrencyAmount(10, Currency.GBP));
            ps.getMoneyProvision().get(0).setDescription("Money provision test 1");
            ps.getMoneyProvision().get(0).setFrequency(FinancialFrequency.MONTHLY);
            ps.getMoneyProvision().get(0).setPaymentMethod(new PayPal());

            ps.getMoneyProvision().add(new MoneyProvision());
            ps.getMoneyProvision().get(1).setAmount(new CurrencyAmount(20, Currency.GBP));
            ps.getMoneyProvision().get(1).setDescription("Money provision test 2");
            ps.getMoneyProvision().get(1).setFrequency(FinancialFrequency.WEEKLY);
            ps.getMoneyProvision().get(1).setPaymentMethod(new PaymentCard());


            getCore().openPersistenceSession();

            paymentScheduleId = getCore().create(ps).getSystemId();

            getCore().closePersistenceSession();
        }

        {
            getCore().openPersistenceSession();

            PaymentSchedule ps = getCore().load(PaymentSchedule.class, paymentScheduleId);

            assertThat(ps, is(notNullValue()));
            assertThat(ps.getMoneyProvision(), hasSize(2));

            getCore().closePersistenceSession();
        }
    }

    @Test
    public void testPolicyPaymentSchedulePersistence() throws ParseException {
        long policyId;
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
        Date paymentsStartDateInRange = dateFormatter.parse("01-01-2000");
        Date paymentsStartDateOutOfRange = dateFormatter.parse("11-01-2000");

        {
            Policy policy=new Policy();

            // add a payment option to the policy
            {
                PaymentSchedule ps=new PaymentSchedule();

                ps.setDescription("payment-option schedule description");
                ps.setMoneyProvision(new ArrayList<MoneyProvision>());

                ps.getMoneyProvision().add(new MoneyProvision());
                ps.getMoneyProvision().get(0).setDescription("Money provision payment-option test 1");
                ps.getMoneyProvision().get(0).setAmount(new CurrencyAmount(10, Currency.GBP));
                ps.getMoneyProvision().get(0).setStatus(MoneyProvisionStatus.NEW);
                ps.getMoneyProvision().get(0).setPaymentsStartDate(paymentsStartDateInRange);
                ps.getMoneyProvision().get(0).setFrequency(FinancialFrequency.MONTHLY);
                ps.getMoneyProvision().get(0).setPaymentMethod(new PayPal());

                ps.getMoneyProvision().add(new MoneyProvision());
                ps.getMoneyProvision().get(1).setDescription("Money provision payment-option test 2");
                ps.getMoneyProvision().get(1).setAmount(new CurrencyAmount(10, Currency.GBP));
                ps.getMoneyProvision().get(1).setStatus(MoneyProvisionStatus.CANCELLED);
                ps.getMoneyProvision().get(1).setPaymentsStartDate(paymentsStartDateInRange);
                ps.getMoneyProvision().get(1).setFrequency(FinancialFrequency.MONTHLY);
                ps.getMoneyProvision().get(1).setPaymentMethod(new PayPal());

                ps.getMoneyProvision().add(new MoneyProvision());
                ps.getMoneyProvision().get(2).setDescription("Money provision payment-option test 3");
                ps.getMoneyProvision().get(2).setAmount(new CurrencyAmount(10, Currency.GBP));
                ps.getMoneyProvision().get(2).setStatus(MoneyProvisionStatus.NEW);
                ps.getMoneyProvision().get(2).setPaymentsStartDate(paymentsStartDateOutOfRange);
                ps.getMoneyProvision().get(2).setFrequency(FinancialFrequency.MONTHLY);
                ps.getMoneyProvision().get(2).setPaymentMethod(new PayPal());

                policy.getPaymentOption().add(ps);
            }

            // add payment details to the policy
            {
                PaymentSchedule ps=new PaymentSchedule();
                ps.setDescription("payment-details schedule description");
                ps.setMoneyProvision(new ArrayList<MoneyProvision>());

                ps.getMoneyProvision().add(new MoneyProvision());
                ps.getMoneyProvision().get(0).setDescription("Money provision payment-details test 1");
                ps.getMoneyProvision().get(0).setAmount(new CurrencyAmount(10, Currency.GBP));
                ps.getMoneyProvision().get(0).setStatus(MoneyProvisionStatus.NEW);
                ps.getMoneyProvision().get(0).setPaymentsStartDate(paymentsStartDateInRange);
                ps.getMoneyProvision().get(0).setFrequency(FinancialFrequency.MONTHLY);
                ps.getMoneyProvision().get(0).setPaymentMethod(new PayPal());

                ps.getMoneyProvision().add(new MoneyProvision());
                ps.getMoneyProvision().get(1).setDescription("Money provision payment-details test 2");
                ps.getMoneyProvision().get(1).setAmount(new CurrencyAmount(10, Currency.GBP));
                ps.getMoneyProvision().get(1).setStatus(MoneyProvisionStatus.CANCELLED);
                ps.getMoneyProvision().get(1).setPaymentsStartDate(paymentsStartDateInRange);
                ps.getMoneyProvision().get(1).setFrequency(FinancialFrequency.MONTHLY);
                ps.getMoneyProvision().get(1).setPaymentMethod(new PayPal());

                ps.getMoneyProvision().add(new MoneyProvision());
                ps.getMoneyProvision().get(2).setDescription("Money provision payment-details test 3");
                ps.getMoneyProvision().get(2).setAmount(new CurrencyAmount(10, Currency.GBP));
                ps.getMoneyProvision().get(2).setStatus(MoneyProvisionStatus.NEW);
                ps.getMoneyProvision().get(2).setPaymentsStartDate(paymentsStartDateOutOfRange);
                ps.getMoneyProvision().get(2).setFrequency(FinancialFrequency.MONTHLY);
                ps.getMoneyProvision().get(2).setPaymentMethod(new PayPal());

                policy.setPaymentDetails(ps);
            }

            getCore().openPersistenceSession();

            policyId = getCore().create(policy).getSystemId();

            getCore().closePersistenceSession();
        }

        {
            getCore().openPersistenceSession();

            Policy policy = getCore().load(Policy.class, policyId);

            assertThat(policy, is(notNullValue()));

            getCore().closePersistenceSession();
        }

        {
            Date startDate = dateFormatter.parse("01-01-2000");
            Date endDate = dateFormatter.parse("02-01-2000");

            getCore().openPersistenceSession();

            @SuppressWarnings("unchecked")
            List<MoneyProvision> payments = (List<MoneyProvision>) getCore().query("get.pending.payments", startDate, endDate);
            assertThat(payments, hasSize(1));
            assertThat(payments.get(0).getDescription(), is("Money provision payment-details test 1"));

            Policy policy = (Policy) getCore().queryUnique("get.policy.for.moneyprovision", payments.get(0));
            assertThat(policy, is(notNullValue()));
            assertThat(policy.getSystemId(), is(policyId));

            getCore().closePersistenceSession();
        }
    }

    @Test
    public void testForeignSystemReferenceWRTDocumentContent() {
        {
            getCore().openPersistenceSession();

            DocumentContent documentContent = new DocumentContent();
            documentContent.setProductTypeId("AIL.Demo.WidgetShowcase");
            documentContent.getForeignSystemReference().put("Liferay", new ForeignSystemReference("Liferay", "1234"));

            getCore().create(documentContent);

            getCore().closePersistenceSession();
        }
    }

    private void truncateTables(String... tables) throws Exception {
        Properties props = AbstractConfigurationLoader.loadLoader().getLoaderParams();

        Class.forName(props.getProperty("driver"));

        Connection con = DriverManager.getConnection(props.getProperty("url"), props);

        for (String table : tables) {
            try {
                Statement st = con.createStatement();
                st.execute("delete from "+table);
                st.close();
            } catch (SQLException e) {
            }
        }
        con.close();
    }
}
