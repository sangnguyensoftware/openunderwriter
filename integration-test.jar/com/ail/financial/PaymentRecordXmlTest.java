package com.ail.financial;

import static com.ail.financial.PaymentRecordType.NEW;
import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.ail.core.CoreProxy;
import com.ail.core.XMLException;
import com.ail.core.XMLString;
import com.ail.core.configure.ConfigurationHandler;

public class PaymentRecordXmlTest {

    private CoreProxy coreProxy;

    @Before
    public void setup() {
        coreProxy = new CoreProxy();
        coreProxy.resetConfigurations();
        coreProxy.setVersionEffectiveDateToNow();
        ConfigurationHandler.resetCache();
    }

    @Test
    public void testConstructor() throws XMLException {
        PaymentRecord before = new PaymentRecord(new CurrencyAmount("1233", "GBP"), "trans", "method", NEW, new Date());

        XMLString xml = coreProxy.toXML(before);

        PaymentRecord after = coreProxy.fromXML(PaymentRecord.class, xml);

        assertEquals(before.getAmount(), after.getAmount());
        assertEquals(before.getTransactionReference(), after.getTransactionReference());
        assertEquals(before.getMethodIdentifier(), after.getMethodIdentifier());
        assertEquals(before.getType(), after.getType());
        assertEquals(before.getDate(), after.getDate());
    }
}
