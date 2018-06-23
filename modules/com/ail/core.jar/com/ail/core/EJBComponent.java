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

package com.ail.core;

import java.security.Principal;

import com.ail.core.command.Argument;
import com.ail.core.command.Command;

/**
 * This class is use as a superclass by all EJB components. 
 */
public abstract class EJBComponent extends Component {
    private VersionEffectiveDate versionEffectiveDate = null;
    private Core core = null;
    private String namespace;

    /**
     * Returns the context passed to the EJB's setSessionContext method.
     * @return session context.
     */
    public abstract Principal getSecurityPrincipal();

    protected void initialise(String namespace) {
        this.namespace = namespace;
        core = new Core(this);
        versionEffectiveDate = new VersionEffectiveDate();
    }
    
    /**
     * Getter to return the core this component is using.
     * @return Core instance.
     */
    public Core getCore() {
        return core;
    }

    /**
     * Return the component's version effective date. This date will determine
     * the effective date that the component will run as - and hence the version
     * of configuration information it uses.
     * @return version effective date
     */
    public VersionEffectiveDate getVersionEffectiveDate() {
        return versionEffectiveDate;
    }

    /**
     * @see #getVersionEffectiveDate()
     * @param ved version effective date
     */
    public void setVersionEffectiveDate(VersionEffectiveDate ved) {
        this.versionEffectiveDate = ved;
    }

    /**
     * Hard code the namespace to "com.ail.insurance.quotation.QuotationBean". Generally,
     * the super class will automatically provide a namespace based on the class name, 
     * but for EJBs this can be a problem. Some app server generated containers effect
     * the name of the class causing the configuration to fail. Weblogic is one such.
     * @return The namespace of the configuration.
     */
    public String getConfigurationNamespace() {
        return namespace;
    }

    protected <T extends Argument> T invokeCommand(String name, T sourceArgument) {
        Command command = core.newCommand(name, Command.class);
        return invokeCommand(command, sourceArgument);
    }

    @SuppressWarnings("unchecked")
    private <T extends Argument> T invokeCommand(Command localCommand, T sourceArgument) {
        try {
            localCommand.setArgs(sourceArgument);
            localCommand.invoke();
            return (T)localCommand.getArgs();
        }
        catch (com.ail.core.BaseException e) {
            throw new com.ail.core.BaseServerException(e);
        }
        catch (com.ail.core.BaseError e) {
            throw new com.ail.core.BaseServerException(e);
        }
    }

}
