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

import com.ail.core.CoreProxy;
import com.ail.core.Type;
import com.ail.core.document.FetchDocumentService.FetchDocumentCommand;
import com.ail.insurance.policy.Policy;

/**
 * PageFlow action to generate the certificate document for the current
 * quotation. The document will be generated and persisted with the quotation.
 */
public class GenerateEndorsementDocumentAction extends Action {
    private static final long serialVersionUID = 7575333121831400599L;

    @Override
    public Type executeAction(Type model, ActionType currentPhase) {
        if (getWhen().equals(currentPhase)) {
            if (conditionIsMet(model)) {
                try {
                    CoreProxy proxy = PageFlowContext.getCoreProxy();

                    Policy quote = (Policy) model;

                    // This will force the quote do to be generated if it hasn't
                    // been already, and will do nothing otherwise.
                    FetchDocumentCommand cmd = proxy.newCommand("FetchEndorsementDocument", FetchDocumentCommand.class);
                    cmd.setModelIDArg(quote.getSystemId());
                    cmd.invoke();

                    // If we are processing a quotation (i.e. we're not on
                    // listing quotations, we're processing a quote)
                    if (model instanceof Policy) {
                        // ...assume that we have just updated the persisted
                        // quote and keep the session in step
                        PageFlowContext.setPolicy((Policy) cmd.getModelRet());
                    }
                } catch (Exception e) {
                    throw new RenderingError("Failed to generate/display endorsement", e);
                }
            }
        }

        return model;
    }
}
