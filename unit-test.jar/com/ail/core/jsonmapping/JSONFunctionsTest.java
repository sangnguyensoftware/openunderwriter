package com.ail.core.jsonmapping;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class JSONFunctionsTest {

    @Test
    public void testCompressWithNullString() {
        String jsonIn = null;

        String jsonOut = JSONFunctions.compressJSON(jsonIn);

        assertThat(jsonOut, is((String)null));
    }

    @Test
    public void testCompressWithEmptyString() {
        String jsonIn = "";

        String jsonOut = JSONFunctions.compressJSON(jsonIn);

        assertThat(jsonOut, is(jsonIn));
    }

    @Test
    public void testCompressWithNoWhiteSpace() {
        String jsonIn = "{\"job\":\"developer\"}";

        String jsonOut = JSONFunctions.compressJSON(jsonIn);

        assertThat(jsonOut, is(jsonIn));
    }

    @Test
    public void testCompressWithSpaces() {
        String jsonIn = "{\"job\" : \"developer\"}";

        String jsonOut = JSONFunctions.compressJSON(jsonIn);

        assertThat(jsonOut, is("{\"job\":\"developer\"}"));
    }

    @Test
    public void testCompressWithTabs() {
        String jsonIn = "{\"job\"\t:\t\"developer\"}";

        String jsonOut = JSONFunctions.compressJSON(jsonIn);

        assertThat(jsonOut, is("{\"job\":\"developer\"}"));
    }

    @Test
    public void testCompressWithSpaceInQuotes() {
        String jsonIn = "{\"job\":\"lead developer\"}";

        String jsonOut = JSONFunctions.compressJSON(jsonIn);

        assertThat(jsonOut, is(jsonIn));
    }

    @Test
    public void testCompressWithTabsInQuotes() {
        String jsonIn = "{\"job\":\"lead\tdeveloper\"}";

        String jsonOut = JSONFunctions.compressJSON(jsonIn);

        assertThat(jsonOut, is(jsonIn));
    }

    @Test
    public void testCompressWithCarriageReturn() {
        String jsonIn = "{\"job\":\n\"developer\"}";

        String jsonOut = JSONFunctions.compressJSON(jsonIn);

        assertThat(jsonOut, is("{\"job\":\"developer\"}"));
    }

    @Test
    public void testRemoveTrailingComma() {
        String jsonIn = "{\"elements\":[{\"one\":\"1\"},{\"two\":\"2\"},]}";

        String jsonOut = JSONFunctions.tidyJSON(jsonIn);

        assertThat(jsonOut, is("{\"elements\":[{\"one\":\"1\"},{\"two\":\"2\"}]}"));
    }

}
