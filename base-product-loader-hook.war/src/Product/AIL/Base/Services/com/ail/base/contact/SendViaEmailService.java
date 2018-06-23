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
import com.ail.pageflow.render.NotifyBrokerByEmailService.NotifyBrokerByEmailCommand;
import com.ail.pageflow.render.NotifyPartyByEmailService.NotifyPartyByEmailCommand;
import com.ail.pageflow.render.NotifyProposerByEmailService.NotifyProposerByEmailCommand;
import com.ail.party.Party;

/**
 * A service to send an email. If {@link MaximalArgument#userToContact} is
 * '*Proposer' or 'Broker' then this service will send out the default
 * PersonalProposer or Broker Notification email. If it begins 'Party:' then
 * whatever is after 'Party:' will be queried by xpath on the Policy to try to
 * retrieve a Party type to which to send the email. In this case then the
 * {@link MaximalArgument#subject} must be populated to make up the email
 * subject and the {@link MaximalArgument#message} must be of the form
 * 'Template:[template name]' which should reference a valid template name for
 * this product.
 */
@ProductServiceCommand(serviceName = "SendViaEmailService", commandName = "SendViaEmail")
public class SendViaEmailService extends BaseContactService {

    public static void invoke(ExecutePageActionArgument args) throws BaseException {
        new SendViaEmailService().invoke(MaximalArgument.class);
    }

    public RestfulServiceReturn service(MaximalArgument arg) throws JSONException, PreconditionException, PostconditionException {
        CoreProxy coreProxy = PageFlowContext.getCoreProxy();
        coreProxy.logDebug("SendEmailService argument: " + arg);

        if (StringUtils.isBlank(arg.userToContact)) {
            coreProxy.logInfo("SendEmailService Invalid value for arg userToContact: " + arg.userToContact);
            return new ClientError(HTTP_BAD_REQUEST, "Invalid value for arg userToContact: " + arg.userToContact);
        }

        emailAppropriateUser(arg, coreProxy);

        return new RestfulServiceReturn(HTTP_OK);
    }

    private void emailAppropriateUser(MaximalArgument arg, CoreProxy coreProxy) throws JSONException, PreconditionException, PostconditionException {
        if ("NotifyProposer".equalsIgnoreCase(arg.templateName)) {
            emailProposer(coreProxy, arg.message);
        } else if ("NotifyBroker".equalsIgnoreCase(arg.templateName)) {
            emailBroker(coreProxy, arg.message);
        } else {
            Type type = new DataHelper().getType(CaseType.forName(arg.caseType), arg.caseId);
            emailParty(coreProxy, getParty(type, arg.userToContact), arg.subject, arg.templateName, arg.message, arg.documentsToAttach, type);
        }
    }

    private void emailProposer(CoreProxy coreProxy, String message) {
        try {
            NotifyProposerByEmailCommand command = (NotifyProposerByEmailCommand) coreProxy.newCommand(NotifyProposerByEmailCommand.class);
            command.setPolicyArg(PageFlowContext.getPolicy());
            command.setTextArg(message);
            command.invoke();
        } catch (Exception e) {
            coreProxy.logError("Error invoking NotifyProposerByEmailCommand.", e);
        }
    }

    private void emailBroker(CoreProxy coreProxy, String message) {
        try {
            NotifyBrokerByEmailCommand command = (NotifyBrokerByEmailCommand) coreProxy.newCommand(NotifyBrokerByEmailCommand.class);
            command.setPolicyArg(PageFlowContext.getPolicy());
            command.setTextArg(message);
            command.invoke();
        } catch (Exception e) {
            coreProxy.logError("Error invoking NotifyBrokerByEmailCommand.", e);
        }
    }

    private void emailParty(CoreProxy coreProxy, Party party, String subject, String templateName, String message, String documentsToAttach, Type model) {
        try {
            NotifyPartyByEmailCommand command = (NotifyPartyByEmailCommand) coreProxy.newCommand(NotifyPartyByEmailCommand.class);
            command.setPolicyArg(PageFlowContext.getPolicy());
            command.setPartyArg(party);
            command.setSubjectArg(subject);
            command.setTemplateNameArg(templateName);
            command.setMessageArg(message);
            command.setModelArg(model);
            command.setProductTypeArg(PageFlowContext.getProductName());
            if (StringUtils.isNotBlank(documentsToAttach)) {
                command.setDocumentReferencesArg(documentsToAttach.split(","));
            }
            command.invoke();
        } catch (Exception e) {
            coreProxy.logError("Error invoking NotifyProposerByEmailCommand.", e);
        }
    }

}