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
 * A service to send a letter. Currently placeholder. If
 * {@link MaximalArgument#userToContact} is '*Proposer' or 'Broker' then this
 * service will send out the letter to the PersonalProposer or Broker. If it
 * begins 'Party:' then whatever is after 'Party:' will be queried by xpath on
 * the Policy to try to retrieve a Party type to which to send the letter. In
 * this case then the {@link MaximalArgument#subject} must be populated to make
 * up the email subject and the {@link MaximalArgument#message} must be of the
 * form 'Template:[template name]' which should reference a valid template name
 * for this product.
 */
@ProductServiceCommand(serviceName = "SendViaPostService", commandName = "SendViaPost")
public class SendViaPostService extends BaseContactService {

    public static void invoke(ExecutePageActionArgument args) throws BaseException {
        new SendViaPostService().invoke(MaximalArgument.class);
    }

    public RestfulServiceReturn service(MaximalArgument arg) throws JSONException, PreconditionException, PostconditionException {
        CoreProxy coreProxy = PageFlowContext.getCoreProxy();
        coreProxy.logDebug("AIL.Base.SendLetterService argument: " + arg);

        if (StringUtils.isBlank(arg.userToContact)) {
            coreProxy.logInfo("SendViaPostService Invalid value for arg userToContact: " + arg.userToContact);
            return new ClientError(HTTP_BAD_REQUEST, "Invalid value for arg userToContact: " + arg.userToContact);
        }

        if (StringUtils.isBlank(arg.templateName)) {
            return new ClientError(HTTP_BAD_REQUEST, "Invalid value for arg templateName: " + arg.templateName);
        }

        Type type = new DataHelper().getType(CaseType.forName(arg.caseType), arg.caseId);
        Party party = getParty(type, arg.userToContact);
        sendLetterToAppropriateUser(party, arg.userToContact, arg.subject, arg.templateName, coreProxy);

        return new RestfulServiceReturn(HTTP_OK);
    }

    private void sendLetterToAppropriateUser(Party party, String userToContact, String subject, String templateName, CoreProxy coreProxy)
            throws JSONException, PostconditionException {
        if ("NotifyProposer".equalsIgnoreCase(templateName)) {
            sendLetterToProposer(coreProxy, templateName);
        } else if ("NotifyBroker".equalsIgnoreCase(templateName)) {
            sendLetterToBroker(coreProxy, templateName);
        } else {
            sendLetterToParty(coreProxy, party, subject, templateName);
        }
    }

    private void sendLetterToProposer(CoreProxy coreProxy, String templateName) {
    }

    private void sendLetterToBroker(CoreProxy coreProxy, String templateName) {
    }

    private void sendLetterToParty(CoreProxy coreProxy, Party party, String subject, String templateName) {
    }

}