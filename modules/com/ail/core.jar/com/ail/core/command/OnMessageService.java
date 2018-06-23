/* Copyright Applied Industrial Logic Limited 2005. All rights Reserved */
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

import javax.jms.TextMessage;

import com.ail.annotation.Configurable;
import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceImplementation;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.VersionEffectiveDate;
import com.ail.core.XMLString;

@Configurable
@ServiceImplementation
public class OnMessageService extends Service<OnMessageService.OnMessageArgument> {
    private VersionEffectiveDate ved;

    public OnMessageService() {
        ved = new VersionEffectiveDate();
    }

    /**
     * Arg interface for the ClearConfigurationCache entry point. The entry
     * point takes one argument: a namespace's name, and returns one result: a
     * collection of Strings representing the namesapces whose caches have been
     * cleared. objects representing the namespace's history.
     */
    @ServiceArgument
    public interface OnMessageArgument extends Argument {
        TextMessage getMessageArg();

        void setMessageArg(TextMessage messageArg);

        String getCommandExecutedRet();

        void setCommandExecutedRet(String commandExecutedRet);
    }

    @ServiceCommand(defaultServiceClass = OnMessageService.class)
    public interface OnMessageCommand extends Command, OnMessageArgument {
    }

    @Override
    public VersionEffectiveDate getVersionEffectiveDate() {
        return ved;
    }

    @Override
    public void invoke() throws PreconditionException, PostconditionException {
        try {
            if (args.getMessageArg() == null) {
                throw new PreconditionException("args.getMessageArg()==null");
            }

            if (!args.getMessageArg().propertyExists("VersionEffectiveDate")) {
                throw new PreconditionException("!args.getMessageArg().propertyExists(\"VersionEffectiveDate\")");
            }

            TextMessage message = (TextMessage) args.getMessageArg();

            ved = new VersionEffectiveDate(message.getLongProperty("VersionEffectiveDate"));

            // the command comes to us as a string of XML
            XMLString argumentXml = new XMLString(message.getText());

            Argument argument = (Argument) getCore().fromXML(argumentXml.getType(), argumentXml);

            // We take the base name of the class as the command name: i.e. if
            // the command class is "com.ail.core.logging.LoggerArgumentImpl"
            // the command name will be "LoggerArgumentImpl".
            String commandName = argument.getClass().getName();
            commandName = commandName.substring(commandName.lastIndexOf('.') + 1);

            args.setCommandExecutedRet(commandName);

            com.ail.core.command.Command command = getCore().newCommand(commandName, Command.class);
            command.setArgs(argument);
            command.setCallersCore(this);
            command.invoke();
        } catch(PreconditionException e) {
            throw e;
        } catch (Throwable t) {
            throw new PostconditionException("Message processing failed.", t);
        }
    }
}
