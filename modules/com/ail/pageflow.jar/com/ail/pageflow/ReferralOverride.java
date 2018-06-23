/* Copyright Applied Industrial Logic Limited 2015. All rights Reserved */
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
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import com.ail.core.BaseException;
import com.ail.core.Type;
import com.ail.insurance.policy.AssessmentLine;
import com.ail.insurance.policy.Policy;
import com.ail.insurance.quotation.PolicyManualOverrideService.PolicyManualOverrideCommand;

public class ReferralOverride extends PageContainer {
    private static final long serialVersionUID = -4810599045554021748L;

    /**
     * Default constructor
     */
    public ReferralOverride() {
        super();
    }

    public List<AssessmentLine> orderLines(List<AssessmentLine> lines) {
        return sort(lines, on(AssessmentLine.class).getProcessedOrder());
    }

	@Override
	public Type renderResponse(Type model) throws IllegalStateException, IOException {
	    return executeTemplateCommand("ReferralOverride", model);
	}

    @Override
    public Type processActions(Type model) throws BaseException {
        Properties params = PageFlowContext.getOperationParameters();

        if (params != null) {
            String op = PageFlowContext.getRequestedOperation();

            if (StringUtils.isNotBlank(op)) {
                PolicyManualOverrideCommand pmoc = PageFlowContext.getCoreProxy().newCommand(PolicyManualOverrideCommand.class);
                pmoc.setOperationArg(op);
                pmoc.setPolicyArg((Policy)model);
                pmoc.setParameterMapArg(PageFlowContext.getRequestWrapper().getParameterMap());
                pmoc.invoke();

                PageFlowContext.flagActionAsProcessed();
            }
        }

        return model;
    }
}
