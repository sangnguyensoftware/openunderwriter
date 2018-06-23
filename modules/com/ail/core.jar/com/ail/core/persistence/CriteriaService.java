/* Copyright Applied Industrial Logic Limited 2002. All rights Reserved */
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

package com.ail.core.persistence;

import org.hibernate.Criteria;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceInterface;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.core.persistence.hibernate.HibernateCriteriaService;

/**
 * Arguments required by criteria service - a service which creates search criteria.
 */
@ServiceInterface
public interface CriteriaService {

    /**
     * Arguments required by query service
     */
    @ServiceArgument
    public interface CriteriaArgument extends Argument {

        void setClassArg(Class<?> clazzArg);

        Class<?> getClassArg();

        void setAliasArg(String aliasArg);

        String getAliasArg();

        void setCriteriaRet(Criteria criteriaRet);

        Criteria getCriteriaRet();
    }

    @ServiceCommand(defaultServiceClass = HibernateCriteriaService.class)
    public interface CriteriaCommand extends Command, CriteriaArgument {}
}


