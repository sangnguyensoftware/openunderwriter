/* Copyright Applied Industrial Logic Limited 2003. All rights Reserved */
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

import com.ail.core.BaseError;

/**
 * This error is thrown when an assessment sheet detects two assessment lines with the same id
 */
public class DuplicateAssessmentLineError extends BaseError {
    private static final long serialVersionUID = 5971903046851604256L;

    /**
     * Constructor
     * @param lineId The ID of the line in question.
     **/
    public DuplicateAssessmentLineError(String lineId) {
        super("Sheet already contains a line with this id:'"+lineId+"'");
    }
}
