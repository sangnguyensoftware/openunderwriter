package com.ail.core.configure;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class AnnotationConfigurationTest {

    @Test
    public void checkThatAnnotationConfigurationIsNotNull() {
        assertThat(AnnotationConfiguration.getInstance(), is(not(nullValue())));
    }
}
