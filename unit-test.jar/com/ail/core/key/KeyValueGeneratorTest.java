package com.ail.core.key;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ail.core.CoreContext;
import com.ail.core.CoreProxy;

@RunWith(PowerMockRunner.class)
@PrepareForTest(CoreContext.class)
public class KeyValueGeneratorTest {
    private static final String TEST_KEY = "TEST";

    KeyValueGenerator sut;
    @Mock
    private CoreProxy coreProxy;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        sut = new KeyValueGenerator();

        PowerMockito.mockStatic(CoreContext.class);

        when(CoreContext.getCoreProxy()).thenReturn(coreProxy);
        doReturn("0").when(coreProxy).getParameterValue(eq("KeyGenerators." + TEST_KEY + ".Range"), eq("0"));
    }

    @Test
    public void checkThatSinglRangeOfZeroGivesMinValValueOfZero() {
        assertThat(sut.fetchMinValue(TEST_KEY), is(0L));
    }

    @Test
    public void checkThatSingleRangeOfNonZeroGivesMinValueOfNonZero() {
        doReturn("1234").when(coreProxy).getParameterValue(eq("KeyGenerators." + TEST_KEY + ".Range"), eq("0"));
        assertThat(sut.fetchMinValue(TEST_KEY), is(1234L));
    }

    @Test
    public void checkThatFullRangeOfNonZeroGivesMinValueOfNonZero() {
        doReturn("4321-5432").when(coreProxy).getParameterValue(eq("KeyGenerators." + TEST_KEY + ".Range"), eq("0"));
        assertThat(sut.fetchMinValue(TEST_KEY), is(4321L));
    }

    @Test
    public void checkThatMultiFullRangeOfNonZeroGivesMinValueOfNonZero() {
        doReturn("4324-5432, 6543-7654").when(coreProxy).getParameterValue(eq("KeyGenerators." + TEST_KEY + ".Range"), eq("0"));
        assertThat(sut.fetchMinValue(TEST_KEY), is(4324L));
    }

    @Test
    public void checkThatSinglRangeOfZeroGivesNextValueOfCurrentPlusOne() {
        doReturn("0-*").when(coreProxy).getParameterValue(eq("KeyGenerators." + TEST_KEY + ".Range"), eq("0"));
        assertThat(sut.fetchNextValue(TEST_KEY, 0L), is(1L));
        assertThat(sut.fetchNextValue(TEST_KEY, 2L), is(3L));
        assertThat(sut.fetchNextValue(TEST_KEY, 999998L), is(999999L));
    }

    @Test
    public void checkThatMultipleRangeOfZeroGivesNextValueOfCurrentPlusOne() {
        doReturn("1000-2000,2000-3000").when(coreProxy).getParameterValue(eq("KeyGenerators." + TEST_KEY + ".Range"), eq("0"));
        assertThat(sut.fetchNextValue(TEST_KEY, 1000L), is(1001L));
        assertThat(sut.fetchNextValue(TEST_KEY, 2000L), is(2001L));
    }

    @Test
    public void checkThatMultipleRangeSkipsUpRanges() {
        doReturn("1000-1500,2000-3000,5000-*").when(coreProxy).getParameterValue(eq("KeyGenerators." + TEST_KEY + ".Range"), eq("0"));
        assertThat(sut.fetchNextValue(TEST_KEY, 1500L), is(2000L));
        assertThat(sut.fetchNextValue(TEST_KEY, 3000L), is(5000L));
    }

    @Test
    public void checkThatIndividualValesSkipUpRanges() {
        doReturn("1000,1005,1020,3000").when(coreProxy).getParameterValue(eq("KeyGenerators." + TEST_KEY + ".Range"), eq("0"));
        assertThat(sut.fetchNextValue(TEST_KEY, 1000L), is(1005L));
        assertThat(sut.fetchNextValue(TEST_KEY, 1005L), is(1020L));
        assertThat(sut.fetchNextValue(TEST_KEY, 1020L), is(3000L));
    }

    @Test(expected = KeyGenerationError.class)
    public void checkThatRunningOutOfRangesThrowsAnError() {
        doReturn("1000,1005-2000").when(coreProxy).getParameterValue(eq("KeyGenerators." + TEST_KEY + ".Range"), eq("0"));
        sut.fetchNextValue(TEST_KEY, 2000L);
    }
}
