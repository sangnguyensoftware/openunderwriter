package com.ail.core;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.Test;

import com.ail.insurance.policy.Asset;

public class FactTest {

    @Test
    public void testToStringMethods() {
        Fact fact;

        fact=new Fact("fact", "fact value");
        assertEquals("fact value", fact.toString());

        fact=new Fact("fact", new Integer(10));
        assertEquals("10", fact.toString());

        Attribute attribute=mock(Attribute.class);
        when(attribute.getFormattedValue()).thenReturn("attribute's value");
        fact=new Fact("fact", attribute);
        assertEquals("attribute's value", fact.toString());
    }

    @Test
    public void testNumericFromFactString() {
        Fact fact;

        try {
            fact=new Fact("fact", "fact value");
            assertEquals(0, fact.toInteger());
        }
        catch(IllegalArgumentException e) {
            // ignore - this is good.
        }

        fact=new Fact("fact", "10");
        assertEquals(10, fact.toInteger());
        assertEquals(10.0, fact.toDouble(), 0);
    }


    @Test
    public void testNumericFromFactNumeric() {
        Fact fact;

        fact=new Fact("fact", new Integer(10));
        assertEquals(10, fact.toInteger());
        assertEquals(10.0, fact.toDouble(), 0);

        fact=new Fact("fact", new Double(100));
        assertEquals(100, fact.toInteger());
        assertEquals(100.0, fact.toDouble(), 0);
    }

    @Test
    public void testNumericFromFactAttributeBadType() {
        Fact fact;

        Attribute attribute=mock(Attribute.class);

        when(attribute.isNumberType()).thenReturn(false);
        when(attribute.isCurrencyType()).thenReturn(false);
        when(attribute.isStringType()).thenReturn(false);

        fact=new Fact("fact", attribute);
        try {
            fact.toInteger();
            fail("When it isn't a number, currency or string attribute, an exception should be thrown.");
        }
        catch(IllegalArgumentException e) {
            // ignore - this is good.
        }
    }

    @Test
    public void testNumericFromFactAttributeCurrency() {
        Fact fact;

        Attribute attribute=mock(Attribute.class);

        when(attribute.isCurrencyType()).thenReturn(true);
        when(attribute.getObject()).thenReturn(new Integer(123));

        fact=new Fact("fact", attribute);
        assertEquals(123, fact.toInteger());
    }

    @Test
    public void testNumericFromFactAttributeNumber() {
        Fact fact;

        Attribute attribute=mock(Attribute.class);

        when(attribute.isNumberType()).thenReturn(true);
        when(attribute.getObject()).thenReturn(new Integer(124));

        fact=new Fact("fact", attribute);
        assertEquals(124, fact.toInteger());
    }

    @Test
    public void testNumericFromFactAttributeString() {
        Fact fact;

        Attribute attribute=mock(Attribute.class);

        when(attribute.isStringType()).thenReturn(true);
        when(attribute.getValue()).thenReturn("125");

        fact=new Fact("fact", attribute);
        assertEquals(125, fact.toInteger());
    }

    @Test
    public void testXpathString() {
        Fact fact;

        Type type=mock(Type.class);

        fact=new Fact("fact", type);

        when(type.xpathGet(eq("test xpath"))).thenReturn("hello world");
        assertEquals("hello world", fact.xpathString("test xpath"));
        reset(type);

        when(type.xpathGet(eq("test xpath"))).thenReturn(new Integer(10));
        assertEquals("", fact.xpathString("test xpath"));
        reset(type);
    }

    @Test
    public void testXpathInteger() {
        Fact fact;

        Type type=mock(Type.class);

        fact=new Fact("fact", type);

        when(type.xpathGet(eq("test xpath"))).thenReturn(10);
        assertEquals(10, fact.xpathInt("test xpath"));
        reset(type);

        Attribute attribute=mock(Attribute.class);
        when(type.xpathGet(eq("test xpath"))).thenReturn(attribute);
        when(attribute.isNumberType()).thenReturn(true);
        when(attribute.getObject()).thenReturn(100);
        assertEquals(100, fact.xpathInt("test xpath"));
        reset(type);
    }

    @Test
    public void testXpathDate() {
        Fact fact;

        Type type=mock(Type.class);

        fact=new Fact("fact", type);

        Date date=new Date();
        when(type.xpathGet(eq("test xpath"))).thenReturn(date);
        assertEquals(date, fact.xpathDate("test xpath"));
        reset(type);

        Attribute attribute=mock(Attribute.class);
        when(type.xpathGet(eq("test xpath"))).thenReturn(attribute);
        when(attribute.isDateType()).thenReturn(true);
        when(attribute.getObject()).thenReturn(date);
        assertEquals(date, fact.xpathDate("test xpath"));
        reset(type);
    }

    @Test
    public void testGetValue() {
        Asset asset = mock(Asset.class);
        Fact fact = new Fact("fact", asset);

        assertThat(fact.getValue(), is(asset));
    }
}
