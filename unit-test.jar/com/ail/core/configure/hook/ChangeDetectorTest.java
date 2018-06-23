package com.ail.core.configure.hook;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.liferay.portlet.documentlibrary.model.DLFileEntry;

public class ChangeDetectorTest {

    /**
     * ChangeDetector maintains a history of the files which have had events
     * associated with them. This list is limited to holding HISTORY_MAX_SIZE
     * values and should not grow beyond that size. This test ensures that this
     * is the case.
     */
    @Test
    public void testHistorySizeLimit() {
        ChangeDetector sut = new ChangeDetector();

        // Add twice as many elements to the history as it should be able to
        // contain
        for (int i = 0; i < ChangeDetector.HISTORY_MAX_SIZE * 2; i++) {
            DLFileEntry mockFileEntry = mock(DLFileEntry.class);
            sut.record(mockFileEntry);
        }

        // Check that the detector hasn't breached it's size limit
        assertEquals(ChangeDetector.HISTORY_MAX_SIZE, sut.size());
    }

    /**
     * Elements in the ChangeDetector's history list should be recycled on a
     * FIFO basis. This test ensures that this is the case.
     */
    @Test
    public void testHistoryDiscardsOld() {
        ChangeDetector sut = new ChangeDetector();

        List<DLFileEntry> added = new ArrayList<>();

        // Add twice as many elements to the history as it should be able to
        // contain
        for (int i = 0; i < ChangeDetector.HISTORY_MAX_SIZE * 2; i++) {
            DLFileEntry mockFileEntry = mock(DLFileEntry.class);
            sut.record(mockFileEntry);
            added.add(mockFileEntry);
        }

        // Confirm that the oldest elements have been removed
        for (int i = 0; i < ChangeDetector.HISTORY_MAX_SIZE; i++) {
            assertFalse(sut.contains(added.get(i)));
        }

        // Confirm that the newest elements are still present
        for (int i = ChangeDetector.HISTORY_MAX_SIZE; i < ChangeDetector.HISTORY_MAX_SIZE * 2; i++) {
            assertTrue(sut.contains(added.get(i)));
        }
    }

    /**
     * The ChangeDetector's record() method "should" always be called before
     * isChanged() is called for a give FileEntry. Check that a failure to call
     * record() is correctly handled.
     */
    @Test(expected = IllegalStateException.class)
    public void testChangeOnUnrecordedFileEntry() {
        ChangeDetector sut = new ChangeDetector();

        DLFileEntry mockFileEntry = mock(DLFileEntry.class);
        sut.isChanged(mockFileEntry);
    }

    @Test
    public void testRecordAddedWhenUnknown() {
        @SuppressWarnings("unchecked")
        LinkedList<DLFileEntry> mockHistory = mock(LinkedList.class);
        ChangeDetector sut = new ChangeDetector(mockHistory);

        DLFileEntry mockFileEntry;

        mockFileEntry = mock(DLFileEntry.class);
        when(mockHistory.contains(mockFileEntry)).thenReturn(false);
        sut.record(mockFileEntry);
        verify(mockHistory).add(eq(mockFileEntry));
    }

    @Test
    public void testRecordIgnoredWhenKnown() {
        @SuppressWarnings("unchecked")
        LinkedList<DLFileEntry> mockHistory = mock(LinkedList.class);
        ChangeDetector sut = new ChangeDetector(mockHistory);

        DLFileEntry mockFileEntry;

        mockFileEntry = mock(DLFileEntry.class);
        when(mockHistory.contains(mockFileEntry)).thenReturn(true);
        sut.record(mockFileEntry);
        verify(mockHistory, never()).add(eq(mockFileEntry));
    }

    /**
     * A change should be detected when the version number of file that is held
     * in the ChangeDetector's history is seen to have increased. This test
     * ensures that only increases of version number cause isChanged() to return
     * true.
     */
    @Test
    public void testChangeDetection() {
        @SuppressWarnings("unchecked")
        LinkedList<DLFileEntry> mockHistory = mock(LinkedList.class);
        ChangeDetector sut = new ChangeDetector(mockHistory);

        DLFileEntry mockFileEntry;

        // prime the ChangeDetector with version 1.0 of the file.
        mockFileEntry = mock(DLFileEntry.class);
        when(mockFileEntry.getVersion()).thenReturn("1.0");
        when(mockFileEntry.getTitle()).thenReturn("FileName");
        when(mockHistory.get(eq(0))).thenReturn(mockFileEntry);
        when(mockHistory.indexOf(mockFileEntry)).thenReturn(0);
        sut.record(mockFileEntry);

        // check that a change is detected when the version number increases
        mockFileEntry = mock(DLFileEntry.class);
        when(mockFileEntry.getVersion()).thenReturn("1.1");
        when(mockFileEntry.getTitle()).thenReturn("FileName");
        assertTrue(sut.isChanged(mockFileEntry));
        when(mockHistory.get(eq(0))).thenReturn(mockFileEntry);
        when(mockHistory.indexOf(mockFileEntry)).thenReturn(0);

        // check that no change is detected when the version number goes down
        mockFileEntry = mock(DLFileEntry.class);
        when(mockFileEntry.getVersion()).thenReturn("1.0");
        when(mockFileEntry.getTitle()).thenReturn("FileName");
        assertFalse(sut.isChanged(mockFileEntry));

        // check that no change is detected when the version number stays the
        // same
        mockFileEntry = mock(DLFileEntry.class);
        when(mockFileEntry.getVersion()).thenReturn("1.1");
        when(mockFileEntry.getTitle()).thenReturn("FileName");
        assertFalse(sut.isChanged(mockFileEntry));
    }

    /**
     * In version numbers, 1.10 is bigger than 1.9. Make sure isChanged()
     * recognises this.
     */
    @Test
    public void testChangeDetectionForOutlineVersions() {
        @SuppressWarnings("unchecked")
        LinkedList<DLFileEntry> mockHistory = mock(LinkedList.class);
        ChangeDetector sut = new ChangeDetector(mockHistory);

        DLFileEntry mockFileEntry;

        // prime the ChangeDetector with version 1.9 of the file.
        mockFileEntry = mock(DLFileEntry.class);
        when(mockFileEntry.getVersion()).thenReturn("1.9");
        when(mockFileEntry.getTitle()).thenReturn("FileName");
        when(mockHistory.get(eq(0))).thenReturn(mockFileEntry);
        when(mockHistory.indexOf(mockFileEntry)).thenReturn(0);
        sut.record(mockFileEntry);

        // check that a change is detected when the version number increases
        mockFileEntry = mock(DLFileEntry.class);
        when(mockFileEntry.getVersion()).thenReturn("1.10");
        when(mockFileEntry.getTitle()).thenReturn("FileName");
        assertTrue(sut.isChanged(mockFileEntry));
        when(mockHistory.get(eq(0))).thenReturn(mockFileEntry);
        when(mockHistory.indexOf(mockFileEntry)).thenReturn(0);
    }
}
