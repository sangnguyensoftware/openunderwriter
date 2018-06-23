package com.ail.core;

import org.junit.Test;

import com.ail.insurance.policy.Asset;

public class ReferenceTest {

    @Test
    public void test() {
        new Reference(Asset.class, "id");
//        new Reference("com.ail.core.Attribute", "id");
    }

}
