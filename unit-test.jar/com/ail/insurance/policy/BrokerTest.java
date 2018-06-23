package com.ail.insurance.policy;

import org.junit.Test;

public class BrokerTest {

    @Test
    public void testBrokerClaimsTelephoneNumberCanBeBlankOrNull() {
        Broker broker = new Broker();

        broker.setClaimTelephoneNumber("");
        broker.setClaimTelephoneNumber(null);
    }
}
