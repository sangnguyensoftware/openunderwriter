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
    /* Copyright Applied Industrial Logic Limited 2006. All rights Reserved */
package com.ail.pageflow;

import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.sort;

import java.io.IOException;
import java.util.List;

import com.ail.core.BaseException;
import com.ail.core.Type;
import com.ail.insurance.policy.AssessmentLine;

/**
 * <p>This PageElement renders a summary of a quotation which is aimed at brokers. The detailed format of the element
 * depends on the {@link com.ail.openquote.Policy#getStatus() state} of the {@link com.ail.openquote.Policy}.</p>
 * 
 * <p>The three alternative formats are shown below:</p>
 * 
 * <p>The quote is at Policy status when the premium has been calculated, but the proposer has not indicated
 * whether they would like to proceed or not. The premium detail section displays significant considerations included
 * in the premium, but stops short of displaying the whole assessment sheet ({@link AssessmentSheetDetails})<br/>
 * <img src="doc-files/BrokerQuotationSummary-1.png"/></p>
 * 
 * <p>A quote moved to submitted status once the user goes through "confirm and pay" and has supplied their
 * payment details and ticked the "I confirm the details I have given are correct" button. The primary difference
 * between this and the previous screenshot is that payment details are now included:<br/>
 * <p><img src="doc-files/BrokerQuotationSummary-2.png"/></p>
 * 
 * <p>If risk assessment or premium calculation refers or fails for any other reason, the quote is left at 
 * referred status and the page element displays the following. Payment details are not included as none have
 * been collected at this stage, and the "Assessment summary" focuses on the referral reasons.<br/>
 * <img src="doc-files/BrokerQuotationSummary-3.png"/></p>
 */
public class BrokerQuotationSummary extends PageContainer {
    private static final long serialVersionUID = -4810599045554021748L;

    public BrokerQuotationSummary() {
        super();
    }

    @Override
    public Type applyRequestValues(Type model) {
        return super.applyRequestValues(model);
    }

    @Override
    public Type processActions(Type model) throws BaseException {
        return super.processActions(model);
    }

    @Override
    public boolean processValidations(Type model) {
        return super.processValidations(model);
    }

    public List<AssessmentLine> orderLines(List<AssessmentLine> lines) {
        return sort(lines, on(AssessmentLine.class).getProcessedOrder());
    }

    /**
     * @throws  
     * @inheritDoc
     */
    @Override
	public Type renderResponse(Type model) throws IllegalStateException, IOException {
   	    return executeTemplateCommand("BrokerQuotationSummary", model);
	}
}
