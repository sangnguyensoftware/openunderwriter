/* Copyright Applied Industrial Logic Limited 2006. All rights Reserved */
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
package com.ail.pageflow;

import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.sort;

import java.io.IOException;
import java.util.List;

import com.ail.core.Type;
import com.ail.insurance.policy.AssessmentLine;

/**
 * <p>Page element to display the contents of a quotation's assessment sheet(s) in a tabular format.</p>
 * <p><img src="doc-files/AssessmentSheetDetails.png"/></p>
 * <p>The rendered details (as shown above) include all of the assessment sheet line types: notes, calculations,
 * markers (referrals, declines etc). All the assessment sheets defined in the quotation are rendered, starting
 * with the sheet held at the quote level, then for each of the quote's sections. The table also includes
 * summary information: quote number, total premium, status, etc.</p>
 * <p>In the above example the policy has just one section (MotorPlusSection) which has no associated lines. All
 * section's assessment sheets are included even those that are empty.</p>
 */
public class AssessmentSheetDetails extends PageElement {
	private static final long serialVersionUID = -4810599045554021748L;

	public AssessmentSheetDetails() {
		super();
	}

	public List<AssessmentLine> orderLines(List<AssessmentLine> lines) {
	    return sort(lines, on(AssessmentLine.class).getProcessedOrder());
	}

    @Override
    public Type renderResponse(Type model) throws IllegalStateException, IOException {
        return executeTemplateCommand("AssessmentSheetDetails", model);
    }
}
