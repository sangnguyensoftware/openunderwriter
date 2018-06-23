/* Copyright Applied Industrial Logic Limited 2003. All rights Reserved */
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
package com.ail.core.workflow;

import java.util.List;
import java.util.Properties;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceInterface;
import com.ail.core.Type;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.core.workflow.jbpm.JBPMSignalWorkflowService;
/**
 * Service to send a signal to a workflow
 */
@ServiceInterface
public interface SignalWorkflowService {

    @ServiceArgument
    public interface SignalWorkflowArgument extends Argument {

        /**
         * @return the model
         */
        Type getModelArg();

        /**
         * Setter for the modelArg property.
         * @param modelArg the model to pass into the workflow
         */
        void setModelArg(Type modelArg);

        /**
         * @return the case type, e.g. policy, claim, or party
         */
        CaseType getCaseTypeArg();

        /**
         * Setter for the caseTypeArg property.
         * @param caseTypeArg the type of the case to pass into the workflow
         */
        void setCaseTypeArg(CaseType caseTypeArg);

        /**
         * @return the signalName
         */
        String getSignalNameArg();

        /**
         * Setter for the signalName property.
         * @param signalName the name of the signal to signal in the workflow
         */
        void setSignalNameArg(String signalName);

        /**
         * @return the signalValue
         */
        String getSignalValueArg();

        /**
         * Setter for the signalValue property.
         * @param signalValue the value of the signal to signal in the workflow
         */
        void setSignalValueArg(String signalValue);

        /**
         * Fields to check for changes
         */
        List<String> getOnChangeFieldArg();

        void setOnChangeFieldArg(List<String> onChangeField);

        /**
         * Server properties
         */
        Properties getPropertiesArg();

        void setPropertiesArg(Properties properties);

    }

    @ServiceCommand(defaultServiceClass=JBPMSignalWorkflowService.class)
    public interface SignalWorkflowCommand extends Command, SignalWorkflowArgument {
    }
}