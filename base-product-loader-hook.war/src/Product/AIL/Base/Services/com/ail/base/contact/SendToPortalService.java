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
 * A service to send a portal message. Currently placeholder. If
 * {@link MaximalArgument#userToContact} is '*Proposer' or 'Broker' then this
 * service will send out the portal message to the PersonalProposer or Broker.
 * If it begins 'Party:' then whatever is after 'Party:' will be queried by
 * xpath on the Policy to try to retrieve a Party type to which to send the
 * portal message. In this case then the {@link MaximalArgument#subject} must be
 * populated to make up the email subject and the
 * {@link MaximalArgument#message} must be of the form 'Template:[template
 * name]' which should reference a valid template name for this product.
 */
@ProductServiceCommand(serviceName = "SendToPortalService", commandName = "SendToPortal")
public class SendToPortalService extends BaseContactService {

    public static void invoke(ExecutePageActionArgument args) throws BaseException {
        new SendToPortalService().invoke(MaximalArgument.class);
    }

    public RestfulServiceReturn service(MaximalArgument arg) throws JSONException, PreconditionException, PostconditionException {
        CoreProxy coreProxy = PageFlowContext.getCoreProxy();
        coreProxy.logDebug("SendToPortalService argument: " + arg);

        if (StringUtils.isBlank(arg.userToContact)) {
            coreProxy.logInfo("SendToPortalService Invalid value for arg userToContact: " + arg.userToContact);
            return new ClientError(HTTP_BAD_REQUEST, "Invalid value for arg userToContact: " + arg.userToContact);
        }

        if (StringUtils.isBlank(arg.templateName)) {
            coreProxy.logInfo("SendToPortalService Invalid value for arg templateName: " + arg.templateName);
            return new ClientError(HTTP_BAD_REQUEST, "Invalid value for arg templateName: " + arg.templateName);
        }

        Type type = new DataHelper().getType(CaseType.forName(arg.caseType), arg.caseId);
        Party party = getParty(type, arg.userToContact);
        sendPortalMessageToAppropriateUser(party, arg.userToContact, arg.subject, arg.templateName, coreProxy);

        return new RestfulServiceReturn(HTTP_OK);
    }

    private void sendPortalMessageToAppropriateUser(Party party, String userToContact, String subject, String templateName, CoreProxy coreProxy)
            throws JSONException, PostconditionException {
        if ("NotifyProposer".equalsIgnoreCase(templateName)) {
            sendPortalMessageToProposer(coreProxy, templateName);
        } else if ("NotifyBroker".equalsIgnoreCase(templateName)) {
            sendPortalMessageToBroker(coreProxy, templateName);
        } else {
            sendPortalMessageToParty(coreProxy, party, subject, templateName);
        }
    }

    private void sendPortalMessageToProposer(CoreProxy coreProxy, String message) {
    }

    private void sendPortalMessageToBroker(CoreProxy coreProxy, String message) {
    }

    private void sendPortalMessageToParty(CoreProxy coreProxy, Party party, String subject, String templateName) {
    }

}