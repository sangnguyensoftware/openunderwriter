/* Copyright Applied Industrial Logic Limited 2017. All rights Reserved */
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

import java.io.IOException;
import java.util.List;

import com.ail.core.BaseException;
import com.ail.core.Type;
import com.ail.core.audit.FetchRevisionsService.FetchRevisionsCommand;
import com.ail.core.audit.Revision;
import com.ail.insurance.policy.Policy;

/**
 * Page element to display the contents of the bound object's audit history
 */
public class AuditDetails extends PageElement {
	private static final long serialVersionUID = -4810599045554021748L;

	public AuditDetails() {
		super();
	}

	public List<Revision> getPolicyRevisions() throws BaseException {
	    FetchRevisionsCommand frc = PageFlowContext.getCoreProxy().newCommand(FetchRevisionsCommand.class);
	    frc.setClassArg(Policy.class);
	    frc.setUIDArg(PageFlowContext.getPolicySystemId());
	    frc.invoke();
	    return frc.getRevisionsRet();
	}

	public String getAffectedEntities(Revision revision) {
	    StringBuffer res = new StringBuffer();

	    revision.getDetails().getModifiedEntityNames().stream().forEach(c -> res.append(c.substring(c.lastIndexOf('.')+1)).append(' '));

	    return res.toString();
	}

    @Override
    public Type renderResponse(Type model) throws IllegalStateException, IOException {
        return executeTemplateCommand("AuditDetails", model);
    }
}
