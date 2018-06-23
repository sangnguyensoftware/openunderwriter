/* Copyright Applied Industrial Logic Limited 2013. All rights reserved. */
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
package com.ail.core.configure.hook;

import java.util.LinkedList;

import com.ail.core.CoreContext;
import com.ail.core.PreconditionException;
import com.ail.core.Version;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;

/**
 * Detect changes to DLFileEntries. We use a Liferay listener to detect file
 * events within the document repository which relate to product content.
 * However, these events don't tell us explicitly what attribute of a file has
 * changed, they simply inform us that there has been a change. We want to
 * detect only significant events, which can be characterised as any event which
 * changes the version number; but we want to ignore less significant events,
 * like changes of file access dates. Differentiating between significant and
 * insignificant changes is the function of this class.
 */
class ChangeDetector {
    static final int HISTORY_MAX_SIZE = 20;
    private LinkedList<DLFileEntry> history = new LinkedList<>();

    ChangeDetector() {
        this(new LinkedList<DLFileEntry>());
    }

    ChangeDetector(LinkedList<DLFileEntry> history) {
        this.history = history;
    }

    /**
     * Provide access to the size of the history cache to ease testing.
     */
    int size() {
        return history.size();
    }

    /**
     * To make testing easier, expose the hitory's contains() method.
     */
    boolean contains(DLFileEntry fileEntry) {
        return history.contains(fileEntry);
    }

    /**
     * Record a FileEntry for which changes should be detected.
     *
     * @param fileEntry
     */
    void record(final DLFileEntry fileEntry) {

        // If it isn't already in the list then add the new element to the end
        // of the list
        if (!history.contains(fileEntry)) {
            history.add(fileEntry);
        }

        // Limit the size of the list to HISTORY_MAX_SIZE
        if (history.size() > HISTORY_MAX_SIZE) {
            history.remove();
        }
    }

    /**
     * Detect if a file entry has been changed wrt it's state when it was
     * record()'ed.
     *
     * @param fileEntry
     * @return true if the file has changed; false otherwise.
     */
    boolean isChanged(final DLFileEntry fileEntry) {

        int index = history.indexOf(fileEntry);

        if (index >= 0) {
            DLFileEntry element = history.get(index);

            Version heldVersion = new Version(element.getVersion());
            Version newVersion = new Version(fileEntry.getVersion());

            try {
                if (newVersion.isNewerVersionThan(heldVersion)) {
                    history.set(index, fileEntry);
                    return true;
                } else {
                    return false;
                }
            } catch (PreconditionException e) {
                CoreContext.getCoreProxy().logError("Content version numbers (" + heldVersion.getVersion() + " & " + newVersion.getVersion() + ") for " + fileEntry.getName() + " could not be compared. Any updates will be ignored.");
                return false;
            }
        }

        // Theoretically, we should never reach this point because record() is
        // always called before isChanged() for any events on a given
        // DLFileEntry.
        throw new IllegalStateException("DLFileElement '" + fileEntry.getTitle() + "' not found in history cache.");
    }
}
