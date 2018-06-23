/* Copyright Applied Industrial Logic Limited 2007. All rights Reserved */
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
package com.ail.core.command;

import static javax.ejb.TransactionAttributeType.NOT_SUPPORTED;
import static javax.ejb.TransactionManagementType.BEAN;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionManagement;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import com.ail.core.MessagingComponent;
import com.ail.core.ServerWarmChecker;
import com.ail.core.command.OnMessageService.OnMessageCommand;
import com.ail.core.persistence.hibernate.HibernateRunInTransaction;
import com.ail.pageflow.PageFlowContext;

/**
 * Message Driven Bean which listens on a queue for commands to execute.
 */
@TransactionManagement(BEAN)
@TransactionAttribute(NOT_SUPPORTED)
@MessageDriven(name = "LastChanceCommandServerBean", activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/OpenUnderwriterLastChanceCommandQueue"),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
        @ActivationConfigProperty(propertyName = "minSession", propertyValue = "25"),
        @ActivationConfigProperty(propertyName = "maxSession", propertyValue = "50") })
public class LastChanceServerBean extends MessagingComponent implements MessageListener {
    private ServerWarmChecker serverWarmChecker=new ServerWarmChecker();

    public LastChanceServerBean() {
        initialise("com.ail.core.command.LastChanceServerBean");
    }

    @Override
    @Resource
    public void setSessionContext(MessageDrivenContext context) {
        super.setSessionContext(context);
    }

    @Override
    public void onMessage(final Message msg) {
        String messageId = null;

        if (!serverWarmChecker.isServerWarmedUp()) {
            throw new ForceMessageRetry();
        }

        try {
            new HibernateRunInTransaction<Object>() {
                private String messageId;

                @Override
                public Object run() throws Throwable {
                    messageId = msg.getJMSMessageID();

                    OnMessageCommand command = getCore().newCommand(OnMessageCommand.class);
                    command.setMessageArg((TextMessage)msg);
                    command.invoke();

                    getCore().logInfo("Message (id=" + messageId + ") processed successfully.");

                    return null;
                }

                public HibernateRunInTransaction<Object> with(String messageId) {
                    this.messageId = messageId;
                    return this;
                }
            }.with(messageId).invoke();

        } catch (Throwable t) {
            getCore().logInfo("Message (id=" + messageId + ") failed to process after the maximum number of retries.");
            t.printStackTrace(System.err);
            throw new ForceMessageRetry();
        }
        finally {
            PageFlowContext.destroy();
        }
    }
}
