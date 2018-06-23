package com.ail.core.persistence.hibernate;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class HibernateNamingStrategyTest {
    HibernateNamingStrategy sut;

    @Before
    public void init() {
        sut = new HibernateNamingStrategy();
    }

    @Test
    public void testCollectionTableNaming() {
        String name = sut.collectionTableName("com.ail.Party", "Party", null, null, "Property");
        assertThat(name, is("cParPro"));
    }

}
