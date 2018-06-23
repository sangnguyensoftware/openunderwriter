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
package com.ail.jbpm.handler;

import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.kie.api.runtime.process.WorkItem;
import org.kie.internal.runtime.StatefulKnowledgeSession;

/**
 * A custom WorkItemHandler for calling the OU contact party services.

 * There should also be a number of parameters on the request:
 * The 'case type' must be one of 'policy', 'party', 'claim'.
 * The 'case id' must be the external reference id for the case.
 * The 'product name' is required either where 'case type' is Party (as it can not be derived from Party), or where there is an overridden ContactParty service for the product.
 * The 'user to contact' must be either an xpath reference to the data item from the case, or a reference to a DataDictionary entry.
 * 'send an email' should be true if the contact should be an email, else set false or leave blank.
 * 'send an sms' should be true if the contact should be an sms, else set false or leave blank. NOT YET SUPPORTED.
 * 'send to portal' should be true if the contact should be through the portal, else set false or leave blank. NOT YET SUPPORTED.
 * 'send via post' should be true if the contact should be a letter, else set false or leave blank. NOT YET SUPPORTED.
 * 'send using preferred method' should be true if the contact should be by the preferred contact method of the user, else set false or leave blank. NOT YET SUPPORTED.
 * The 'subject' should be the subject for the email.
 * The 'message' should be the content of the message, where appropriate for the 'template name'.
 * The 'template name' will be the message template name appropriate for the particular context. Can be set to 'Proposer' or 'Broker' to send those particular default policy emails.
 * The 'documents to attach' will be a String of comma separated values. Each of these can either be the unique external id of a document, or the title of a document attached to the case.
 */
public class ContactPartyHandler extends ServiceWorkflowHandler {

    public ContactPartyHandler(StatefulKnowledgeSession ksession) {
        super(ksession);
    }

    @Override
    public CustomTask getCustomTask(WorkItem workItem) {
        return new ContactParty().initialise(workItem);
    }

    private Object getPartyContactPreference(String userToContact, WorkItem workItem) {
        GetDataHandler getDataHandler = new GetDataHandler(getSession());

        workItem.getParameters().put(CASE_TYPE, getParameterAsString(workItem, CASE_TYPE));
        workItem.getParameters().put(CASE_ID, getParameterAsString(workItem, CASE_ID));
        workItem.getParameters().put(DATA_DIRECTORY_ID, userToContact + "/contactPreferenceAsString");

        getDataHandler.executeWorkItem(workItem, getSession().getWorkItemManager());

        return workItem.getResults().get(VALUE);
    }

    public class ContactParty implements CustomTask {
        boolean sendViaEmail;
        boolean sendViaSMS;
        boolean sendToPortal;
        boolean sendViaPost;
        boolean sendUsingPreferredMethod;
        String contactPreference;

        @Override
        public CustomTask initialise(WorkItem workItem) {
            sendViaEmail = getParameterAsBoolean(workItem, SEND_AN_EMAIL);
            sendViaSMS = getParameterAsBoolean(workItem, SEND_AN_SMS);
            sendToPortal = getParameterAsBoolean(workItem, SEND_TO_PORTAL);
            sendViaPost = getParameterAsBoolean(workItem, SEND_VIA_POST);
            sendUsingPreferredMethod = getParameterAsBoolean(workItem, SEND_USING_PREFERRED_METHOD);

            if (sendUsingPreferredMethod) {
                Object partyContactPreference = getPartyContactPreference(getParameterAsString(workItem, USER_TO_CONTACT), workItem);
                if (partyContactPreference != null) {
                    contactPreference = (String) partyContactPreference;
                    if (contactPreference.equalsIgnoreCase("email")) {
                        sendViaEmail = true;
                    } else if (contactPreference.equalsIgnoreCase("post")) {
                        sendViaPost = true;
                    } else if (contactPreference.equalsIgnoreCase("sms")) {
                        sendViaSMS = true;
                    } else if (contactPreference.equalsIgnoreCase("portal")) {
                        sendToPortal = true;
                    }
                }
            }

            return this;
        }

        @Override
        public String getURLPath(WorkItem workItem) {
            String serviceName = "";
            if (sendViaEmail) {
                serviceName = "SendViaEmail";
            } else if (sendViaPost) {
                serviceName = "SendViaPost";
            } else if (sendViaSMS) {
                serviceName = "SendViaSMS";
            } else if (sendToPortal) {
                serviceName = "SendToPortal";
            }

            return getProductServiceURLPath(workItem, serviceName, true);
        }

        @Override
        public Object getArgument(WorkItem workItem) {
            if (sendViaEmail || sendViaPost || sendToPortal) {
                MaximalArgument arg = new MaximalArgument();
                arg.caseType = getParameterAsString(workItem, CASE_TYPE);
                arg.caseId = getParameterAsString(workItem, CASE_ID);
                arg.userToContact = getParameterAsString(workItem, USER_TO_CONTACT);
                arg.subject = getParameterAsString(workItem, SUBJECT);
                arg.message = getParameterAsString(workItem, MESSAGE);
                arg.templateName = getParameterAsString(workItem, TEMPLATE_NAME);
                arg.documentsToAttach = getParameterAsString(workItem, DOCUMENTS_TO_ATTACH);

                return arg;
            } else if (sendViaSMS) {
                MinimalArgument arg = new MinimalArgument();
                arg.caseType = getParameterAsString(workItem, CASE_TYPE);
                arg.caseId = getParameterAsString(workItem, CASE_ID);
                arg.userToContact = getParameterAsString(workItem, USER_TO_CONTACT);
                arg.message = getParameterAsString(workItem, MESSAGE);

                return arg;
            }

            return null;
        }

        @Override
        public void populateResults(JsonNode data, Map<String, Object> resultMap) {

        }

    }

    public class MaximalArgument {

        public String caseType;
        public String caseId;
        public String userToContact;
        public String subject;
        public String message;
        public String templateName;
        public String documentsToAttach;

    }

    public class MinimalArgument {

        public String caseType;
        public String caseId;
        public String userToContact;
        public String message;

    }
}