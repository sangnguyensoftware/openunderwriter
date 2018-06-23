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
import com.ail.core.PostconditionException;
import com.ail.core.ServerWarmChecker;
import com.ail.core.command.OnMessageService.OnMessageCommand;
import com.ail.core.persistence.hibernate.HibernateRunInTransaction;
import com.ail.pageflow.PageFlowContext;

/**
 * Message Driven Bean which listens on a queue for commands to execute.
 */
@TransactionManagement(BEAN)
@TransactionAttribute(NOT_SUPPORTED)
@MessageDriven(name = "CommandServerBean", activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/OpenUnderwriterCommandQueue"),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
        @ActivationConfigProperty(propertyName = "minSession", propertyValue = "25"),
        @ActivationConfigProperty(propertyName = "maxSession", propertyValue = "50")})
public class CommandServerBean extends MessagingComponent implements MessageListener {
    private ServerWarmChecker serverWarmChecker=new ServerWarmChecker();

    public CommandServerBean() {
        initialise("com.ail.core.command.CommandServerBean");
    }

    @Override
    @Resource
    public void setSessionContext(MessageDrivenContext context) {
        super.setSessionContext(context);
    }

    @Override
    public void onMessage(final Message msg) {
        OnMessageCommand command = null;
        String messageId = null;

        if (!serverWarmChecker.isServerWarmedUp()) {
            throw new ForceMessageRetry();
        }

        try {
            messageId = msg.getJMSMessageID();

            new HibernateRunInTransaction<Object>() {
                private OnMessageCommand command;
                private String messageId;

                @Override
                public Object run() throws Throwable {
                    PageFlowContext.initialise();

                    command = getCore().newCommand(OnMessageCommand.class);
                    command.setMessageArg((TextMessage)msg);
                    command.invoke();

                    // Flush now to catch update collisions here and handle them through a retry
                    // rather than leaving it until the container commits.
                    getCore().flush();

                    getCore().logInfo("Message ("+buildLogMessage(messageId, command)+") processed successfully.");

                    return this;
                }

                public HibernateRunInTransaction<Object> with(OnMessageCommand command, String messageId) {
                    this.command = command;
                    this.messageId = messageId;
                    return this;
                }
            }.with(command, messageId).invoke();
        } catch(PostconditionException e) {
            getCore().logInfo("Message ("+buildLogMessage(messageId, command)+") requeued due to exception: " + e.getTarget());
            throw new ForceMessageRetry();
        } catch (Throwable t) {
            getCore().logInfo("Message ("+buildLogMessage(messageId, command)+") requeued due to exception: " + t);
            throw new ForceMessageRetry();
        }
        finally {
            PageFlowContext.destroy();
        }
    }

    private String buildLogMessage(String messageId, OnMessageCommand command) {
        return "id=" + messageId + ((command!=null) ? ", command="+command.getCommandExecutedRet() : "");
    }
}
