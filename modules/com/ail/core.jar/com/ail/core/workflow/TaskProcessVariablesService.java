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

import java.util.Map;
import java.util.Properties;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceInterface;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.core.workflow.jbpm.JBPMTaskProcessVariablesService;
/**
 * Task to retrieve all variable for the workflow Process that started a Task
 *
 */
@ServiceInterface
public interface TaskProcessVariablesService {

    @ServiceArgument
    public interface TaskProcessVariablesArgument extends Argument {

        /**
         * @return  the taskIdArg
         */
        String getTaskIdArg();

        /**
         * Setter for the taskIdArg property.
         * @param taskIdArg the id of the task whose process variables to retrieve
         */
        void setTaskIdArg(String taskIdArg);

        /**
         * @return the variablesRet
         */
        Map<String, String> getVariablesRet();

        /**
         * Setter for the variablesRet property.
         * @param variablesRet the map of variables to return
         */
        void setVariablesRet(Map<String, String> variablesRet);

        /**
         * Server properties
         */
        Properties getPropertiesArg();

        void setPropertiesArg(Properties properties);
    }

    @ServiceCommand(defaultServiceClass=JBPMTaskProcessVariablesService.class)
    public interface TaskProcessVariablesCommand extends Command, TaskProcessVariablesArgument {
    }

}
