package com.ail.base.contact;
/* Copyright Applied Industrial Logic Limited 2014. All rights Reserved */
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
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_OK;

import org.apache.commons.lang.StringUtils;

import com.ail.base.data.DataHelper;
import com.ail.core.BaseException;
import com.ail.core.CoreProxy;
import com.ail.core.JSONException;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.RestfulServiceReturn;
import com.ail.core.Type;
import com.ail.core.product.ProductServiceCommand;
import com.ail.core.workflow.CaseType;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;
import com.ail.pageflow.PageFlowContext;
import com.ail.party.Party;

/**
 * A service to send an SMS. Currently placeholder. If
 * {@link MinimalArgument#userToContact} is '*Proposer' or 'Broker' then this
 * service will send an SMS to the PersonalProposer or Broker. If it begins
 * 'Party:' then whatever is after 'Party:' will be queried by xpath on the
 * Policy to try to retrieve a Party type to which to send the SMS. The
 * {@link MinimalArgument#message} may either contain the message or be of the
 * form 'Template:[template name]' which should reference a valid template name
 * for this product.
 */
@ProductServiceCommand(serviceName = "SendViaSMSService", commandName = "SendViaSMS")
public class SendViaSMSService extends BaseContactService {

    public static void invoke(ExecutePageActionArgument args) throws BaseException {
        new SendViaSMSService().invoke(MinimalArgument.class);
    }

    public RestfulServiceReturn service(MinimalArgument arg) throws JSONException, PreconditionException, PostconditionException {
        CoreProxy coreProxy = PageFlowContext.getCoreProxy();
        coreProxy.logDebug("SendSMSService argument: " + arg);

        if (StringUtils.isBlank(arg.userToContact)) {
            coreProxy.logInfo("SendViaSMSService Invalid value for arg userToContact: " + arg.userToContact);
            return new ClientError(HTTP_BAD_REQUEST, "Invalid value for arg userToContact: " + arg.userToContact);
        }

        Type type = new DataHelper().getType(CaseType.forName(arg.caseType), arg.caseId);
        Party party = getParty(type, arg.userToContact);
        party.getMobilephoneNumber();

        return new RestfulServiceReturn(HTTP_OK);
    }

}