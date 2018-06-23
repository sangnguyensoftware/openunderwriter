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
import com.ail.core.Attribute;
import com.ail.core.Type;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.core.workflow.jbpm.JBPMStartWorkflowService;
/**
 * Service to start a workflow
 */
@ServiceInterface
public interface StartWorkflowService {

    @ServiceArgument
    public interface StartWorkflowArgument extends Argument {

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
         * @return the workflowIdArg
         */
        String getWorkflowIdArg();

        /**
         * Setter for the workflowIdArg property.
         * @param workflowIdArg the id of the workflow to be started
         */
        void setWorkflowIdArg(String workflowIdArg);

        /**
         * @return the abortExistingArg
         */
        boolean getAbortExistingArg();

        /**
         * Setter for the abortExistingArg property.
         * @param abortExistingArg whether or not to abort any existing instances of the workflow to be started
         */
        void setAbortExistingArg(boolean abortExistingArg);

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
         * @return the productIdArg
         */
        String getProductIdArg();

        /**
         * Setter for the productIdArg property.
         * @param productIdArg the id of the product to pass into the workflow
         */
        void setProductIdArg(String productIdArg);

        /**
         * Command attributes
         */
        List<Attribute> getAttributeArg();

        void setAttributeArg(List<Attribute> attribute);

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

    @ServiceCommand(defaultServiceClass=JBPMStartWorkflowService.class)
    public interface StartWorkflowCommand extends Command, StartWorkflowArgument {
    }
}