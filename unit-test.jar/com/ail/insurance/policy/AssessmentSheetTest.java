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

package com.ail.insurance.policy;

import static com.ail.financial.Currency.GBP;
import static com.ail.insurance.policy.AssessmentStage.AFTER_RATING;
import static java.math.BigDecimal.ROUND_HALF_UP;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ail.financial.Currency;
import com.ail.financial.CurrencyAmount;
import com.ail.util.Rate;

/**
 * The assessment sheet is at the center of the quotation process. It can be thought of as a restricted spreadsheet. Entries can be
 * absolute amounts (Sums) or rates (Percentage). Each entry may depend on a maximum of one other entry, and may contribute to
 * another. Most of what the calculatePremium service does is to run through this table processing it and producing results from it.
 */
public class AssessmentSheetTest {

    AssessmentSheet sut;

    @Mock
    Marker referral;
    @Mock
    Marker decline;
    @Mock
    Marker subjectivity;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        doReturn(MarkerType.REFER).when(referral).getType();
        doReturn("referral").when(referral).getId();

        doReturn(MarkerType.DECLINE).when(decline).getType();
        doReturn("decline").when(decline).getId();

        doReturn(MarkerType.SUBJECTIVITY).when(subjectivity).getType();
        doReturn("subjectivity").when(subjectivity).getId();

        sut=new AssessmentSheet();
    }

    /**
     * Test summary methods and simple line additions
     */
    @Test
    public void testSimpleManipulation() {
        AssessmentSheet as = new AssessmentSheet();

        as.setLockingActor("me");

        // should be no line in the sheet
        assertEquals(0, as.getLineCount());

        as.addLine(new FixedSum("base premium", "calculate value", null, "final premium",
                new CurrencyAmount("100.00", Currency.GBP)));
        as.addLine(new RateBehaviour("load1", "No reason at all", null, "final premium", "base premium", BehaviourType.LOAD,
                new Rate("15%")));

        // should be two entries now, but no refers or declines
        assertEquals(2, as.getLineCount());
        assertTrue(!as.isMarkedForDecline());
        assertTrue(!as.isMarkedForRefer());

        // add a refer
        as.addLine(new Marker("ref1", "Just a bad day", null, MarkerType.REFER));

        // Three entries now, marked for refer, but not decline
        assertEquals(3, as.getLineCount());
        assertTrue(!as.isMarkedForDecline());
        assertTrue(as.isMarkedForRefer());

        // get some lines by name (id)
        assertNotNull(as.findLineById("base premium"));
        assertNotNull(as.findLineById("load1"));
        assertNotNull(as.findLineById("ref1"));
        assertNull(as.findLineById("hello!"));
    }

    /**
     * Test assessment sheet double lock. Assessment sheets are locked to an actor before lines can be added. This test ensures that
     * two actors can lock the same sheet, but that a sheet can be locked by the same actor more than once.
     * <ol>
     * <li>Create an assessment sheet</li>
     * <li>Lock the sheet to the actor "me"</li>
     * <li>Attempt to lock the sheet for the actor "you"</li>
     * <li>Fail if an IllegalStateException isn't thrown</li>
     * <li>File if any other exception is thrown</li>
     * <li>Lock the sheet as the actor "me" again</li>
     * <li>Fail if any exceptions af thrown</li>
     * </ol>
     *
     * @throws Exception
     */
    @Test
    public void testAssessmentSheetDoubleLock() throws Exception {
        AssessmentSheet as = new AssessmentSheet();

        as.setLockingActor("me");

        try {
            as.setLockingActor("you");
            fail("locked sheet twice");
        }
        catch (IllegalStateException e) {
            // good
        }

        as.setLockingActor("me");
    }

    /**
     * Check that lines can only be added to the sheet if either the sheet is locked to an actor, or the lines already have an
     * origin.
     * <ol>
     * <li>Create an assessment sheet</li>
     * <li>Add a line to the sheet (without locking first)</li>
     * <li>Fail if an IllegalStateException isn't thrown</li>
     * <li>Fail if any other exception is thrown</li>
     * <li>Lock the assessment sheet to "me"</li>
     * <li>Try to add the line again</li>
     * <li>Fail if any exceptions are thrown</li>
     * <li>Clear the lock</li>
     * <li>Create a FixedSum line, and set its origin to "me"</li>
     * <li>Try to add the line</li>
     * <li>Fail if any exceptions are thrown</li>
     * </ol>
     */
    @Test
    public void testAddLineWithoutLock() throws Exception {
        AssessmentSheet as = new AssessmentSheet();

        try {
            as.addLine(new FixedSum("w1", "calculate value", null, "final premium", new CurrencyAmount("100.00", Currency.GBP)));
            fail("added a line without a lock!");
        }
        catch (IllegalStateException e) {
            // good
        }

        as.setLockingActor("me");

        as.addLine(new FixedSum("w2", "calculate value", null, "final premium", new CurrencyAmount("100.00", Currency.GBP)));

        as.clearLockingActor();

        FixedSum sum = new FixedSum("w3", "calculate value", null, "final premium", new CurrencyAmount("100.00", Currency.GBP));
        sum.setOrigin("me");

        as.addLine(sum);
    }

    /**
     * Test the removal of lines by actor. An actor can remove all of their lines from an assessment sheet using the
     * <code>removeLinesByOrigin</code> method. This should remove all of that actor's lines, but not touch any of the others.
     * <ol>
     * <li>Create an assessment sheet</li>
     * <li>Set the locking actor to "me"</li>
     * <li>Add two lines to the sheet</li>
     * <li>Check that the sheet has two lines (get the count)</li>
     * <li>Unlock the sheet, and re-lock as "you"</li>
     * <li>Add three further lines to the sheet</li>
     * <li>Check that the sheet has five lines</li>
     * <li>Unlock the sheet</li>
     * <li>use 'removeLInesByOrigin' to remove all of the lines created by "me"</li>
     * <li>Check that the sheet has three lines</li>
     * <li>Check that the lines are those added by "you" - by checking their IDs</li>
     * </ol>
     */
    @Test
    public void testRemoveLinesByActor() {
        AssessmentSheet as = new AssessmentSheet();

        as.setLockingActor("me");

        as.addLine(new FixedSum("l1", "calculate value", null, "final premium", new CurrencyAmount("100.00", Currency.GBP)));
        as.addLine(new FixedSum("l2", "calculate value", null, "final premium", new CurrencyAmount("100.00", Currency.GBP)));
        assertEquals(2, as.getLineCount());

        as.clearLockingActor();
        as.setLockingActor("you");

        as.addLine(new FixedSum("l3", "calculate value", null, "final premium", new CurrencyAmount("100.00", Currency.GBP)));
        as.addLine(new FixedSum("l4", "calculate value", null, "final premium", new CurrencyAmount("100.00", Currency.GBP)));
        as.addLine(new FixedSum("l5", "calculate value", null, "final premium", new CurrencyAmount("100.00", Currency.GBP)));
        assertEquals(5, as.getLineCount());

        as.clearLockingActor();

        as.removeLinesByOrigin("me");
        assertEquals(3, as.getLineCount());

        assertNotNull(as.findLineById("l3"));
        assertNotNull(as.findLineById("l4"));
        assertNotNull(as.findLineById("l5"));
    }

    /**
     * Check that duplicate lines are detected. An assessment sheet cannot contain two assessment lines with the same id. This
     * should be detected when an attempt is made to add the second line. This test ensures that that is the case.
     * <ol>
     * <li>Create an assessment sheet</li>
     * <li>Lock the sheet to actor "me"</li>
     * <li>Add a line with the id "l1"</li>
     * <li>Fail if any exceptions are thrown</li>
     * <li>Attempt to add another line with the id "l1"</li>
     * <li>Fail if a DuplicateAssessmentSheetLineError isn't thrown</li>
     * <li>Fail if any other exceptions are thrown.</li>
     * <li>Fail is no exception is thrown</li>
     * </ol>
     *
     * @throws Exception
     */
    @Test
    public void testDuplicateLineDetection() throws Exception {
        AssessmentSheet as = new AssessmentSheet();

        as.setLockingActor("me");

        as.addLine(new FixedSum("l1", "calculate value", null, "final premium", new CurrencyAmount("100.00", Currency.GBP)));

        try {
            as.addLine(new FixedSum("l1", "calculate value", null, "final premium", new CurrencyAmount("100.00", Currency.GBP)));
            fail("added to lines with the same ID");
        }
        catch (DuplicateAssessmentLineError e) {
            // good!
        }
    }

    public void testMarkerLines() {
        AssessmentSheet assessmentSheet = new AssessmentSheet();

        Marker marker1 = new Marker("id1", "ref 1", null, MarkerType.REFER);
        Marker marker2 = new Marker("id2", "dec 1", null, MarkerType.DECLINE);
        Marker marker3 = new Marker("id3", "sub 1", null, MarkerType.SUBJECTIVITY);

        assessmentSheet.setLockingActor("AssessRisk");
        assessmentSheet.addLine(marker1);
        assessmentSheet.addLine(marker2);
        assessmentSheet.addLine(marker3);
        assessmentSheet.clearLockingActor();

        assertEquals(3, assessmentSheet.markerLines().size());
        assertEquals(3, assessmentSheet.markerLines(true).size());
        assertEquals(3, assessmentSheet.markerLines(false).size());

        marker1.setDisabled(true);
        marker3.setDisabled(true);

        assertEquals(1, assessmentSheet.markerLines().size());
        assertEquals(3, assessmentSheet.markerLines(true).size());
        assertEquals(1, assessmentSheet.markerLines(false).size());
    }

    @Test
    public void testStatusAggregationClearStatus() {
        sut.setLockingActor("test");
        sut.addLine(subjectivity);
        assertThat(sut.isMarkedForDecline(), is(false));
        assertThat(sut.isMarkedForRefer(), is(false));
    }

    @Test
    public void testStatusAggregationReferredStatus() {
        sut.setLockingActor("test");
        sut.addLine(referral);
        assertThat(sut.isMarkedForDecline(), is(false));
        assertThat(sut.isMarkedForRefer(), is(true));
    }

    @Test
    public void testStatusAggregationDeclineStatus() {
        sut.setLockingActor("test");
        sut.addLine(decline);
        assertThat(sut.isMarkedForDecline(), is(true));
        assertThat(sut.isMarkedForRefer(), is(false));
    }

    @Test
    public void testRoundValueControlLine() {

        FixedSum fixedSum = new FixedSum("target", "reason", new CurrencyAmount("149.99", GBP));
        RoundValue roundValue = new RoundValue("id", "reason", "target", -2, ROUND_HALF_UP);

        AssessmentSheet sheet = new AssessmentSheet();
        sheet.setLockingActor("me");
        sheet.setAssessmentStage(AFTER_RATING);
        sheet.addLine(fixedSum);
        sheet.addLine(roundValue);
        fixedSum.setAssessmentSheet(sheet);

        roundValue.execute(sheet, fixedSum);

        assertThat(fixedSum.getAmount(), is(new CurrencyAmount("100.00", Currency.GBP)));
    }

    @Test
    public void checkThatIdPassedInIsAppliedToLine() {
        AssessmentSheet sheet;

        sheet = new AssessmentSheet();
        sheet.setLockingActor("me");
        sheet.addDiscount("ID", "REASON", null, null, null, 1);

        assertThat(sheet.findLineById("ID"), is(notNullValue()));

        sheet = new AssessmentSheet();
        sheet.setLockingActor("me");
        sheet.addLoading("ID", "REASON", null, null, null, 1);

        assertThat(sheet.findLineById("ID"), is(notNullValue()));
    }
}
