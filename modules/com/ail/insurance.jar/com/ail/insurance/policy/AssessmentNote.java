/* Copyright Applied Industrial Logic Limited 2002. All rights Reserved */
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

import com.ail.annotation.TypeDefinition;
import com.ail.core.Reference;

/**
 * Any number of assessment notes may be added to an assessment sheet. Notes have no effect on 
 * the assessment or calculation process, they simply provide a place for rules (or other actors)
 * to include some textual information into the assessment sheet.
 */
@TypeDefinition
public class AssessmentNote extends AssessmentLine {
    private static final long serialVersionUID = -4519518497757725779L;

    /**
     * Default constructor
     */
    public AssessmentNote() {
    }

    /**
     * Constructor
     * @param id The Id for this line.
     * @param reason Free text reason for the marker
     * @param relatedTo Reference to the policy object that caused the marker.
     */
    public AssessmentNote(String id, String reason, Reference relatedTo) {
        super(id, reason, relatedTo);
    }

    /**
     * Constructor
     * @param id The Id for this line.
     * @param reason Free text reason for the marker
     * @param relatedTo Reference to the policy object that caused the marker.
     * @param priority The priority of this line (lines with higher priority values are processed first)
     */
    public AssessmentNote(String id, String reason, Reference relatedTo, int priority) {
        super(id, reason, relatedTo, priority);
    }
}
