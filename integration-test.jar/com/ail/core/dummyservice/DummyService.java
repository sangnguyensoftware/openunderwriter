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

package com.ail.core.dummyservice;

import java.io.Writer;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceImplementation;
import com.ail.core.Attribute;
import com.ail.core.Service;
import com.ail.core.Version;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;

@ServiceImplementation
public class DummyService extends Service<DummyService.DummyArgument> {
    
    @ServiceArgument
    public interface DummyArgument extends Argument {
        /**
         * Fetch the value of the version argument. 
         * @see #setVersion
         * @return value of version
         */
        Version getVersionArgRet();
        
        /**
         * Set the value of the version argument.
         * @see #getVersion
         * @param versionArgRet New value for version argument.
         */
        void setVersionArgRet(Version versionArgRet);
        
        /**
         * Fetch the value of the x argument. First value for adder
         * @see #setX
         * @return value of x
         */
        int getX();

        /**
         * Set the value of the x argument. First value for adder
         * @see #getX
         * @param x New value for x argument.
         */
        void setX(int x);

        /**
         * Fetch the value of the y argument. Second value for adder
         * @see #setY
         * @return value of y
         */
        int getY();

        /**
         * Set the value of the y argument. Second value for adder
         * @see #getY
         * @param y New value for y argument.
         */
        void setY(int y);

        /**
         * Fetch the value of the r argument. Result from addition
         * @see #setR
         * @return value of r
         */
        int getR();

        /**
         * Set the value of the r argument. Result from addition
         * @see #getR
         * @param r New value for r argument.
         */
        void setR(int r);
        
        String getName();
        
        void setName(String name);

        Writer getWriterArg();
        
        void setWriterArg(Writer writer);
        
        /**
         * Fetch the value of the postconditionflag argument. Flag set by the pre-condition service to show that it has been run.
         * @see #setPostConditionFlag
         * @return value of postconditionflag
         */
        boolean getPostConditionFlag();

        /**
         * Set the value of the postconditionflag argument. Flag set by the pre-condition service to show that it has been run.
         * @see #getPostConditionFlag
         * @param postconditionflag New value for postconditionflag argument.
         */
        void setPostConditionFlag(boolean postconditionflag);

        /**
         * Fetch the value of the preconditionflag argument. Flag set by the pre condition service to show that it has been run.
         * @see #setPreConditionFlag
         * @return value of preconditionflag
         */
        boolean getPreConditionFlag();

        /**
         * Set the value of the preconditionflag argument. Flag set by the pre condition service to show that it has been run.
         * @see #getPreConditionFlag
         * @param preconditionflag New value for preconditionflag argument.
         */
        void setPreConditionFlag(boolean preconditionflag);

        void setDetailAttribute(Attribute attribute);
        
        Attribute getDetailAttribute();
    }
    
    @ServiceCommand(defaultServiceClass=DummyService.class)
    public interface DummyCommand extends Command, DummyArgument {
    }

    @Override
    public void invoke() {
        args.setR(args.getX()+args.getY());
    }
}


