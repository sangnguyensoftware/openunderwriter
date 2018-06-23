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

package com.ail.core.command;

import com.ail.core.BaseException;
import com.ail.core.CoreUser;
import com.ail.core.configure.Configuration;

abstract public class CommandImpl extends AbstractCommand {
	private transient Accessor accessor=null;

    /**
     * Get hold of the caller's core. This will be needed
     * if the service needs to see the world (configuration) from
     * the caller's perspective.
     * @return Caller's core instance.
     */
    public CoreUser getCallersCore() {
		return getArgs().getCallersCore();
    }

    /**
     * Set the caller's core. This is called when the command is
     * invoked.
     * @param callersCore
     * @see #getCallersCore
     */
    public void setCallersCore(CoreUser callersCore) {
		getArgs().setCallersCore(callersCore);
    }

    /**
     * Get the accessor used by this command. The accessor is the
     * means by which the command communicates with the service.
     * @return Accessor
     */
	public Accessor getAccessor() {
        if (accessor==null) {
            String commandName=getConfiguration().findParameter("name").getValue();
            throw new AccessorError("Accessor parameter not defined (null) for command: "+commandName);
        }
        return accessor;
    }

    /**
     * Set the accessor property.
     * @param accessor The accessor to use.
     * @see #getAccessor
     */
	public void setAccessor(Accessor accessor) {
        this.accessor=accessor;
    }

    /**
     * Set the accessor property. This does exactly the same thing as setAccessor,
     * but exposing it as setService makes the configuration process more intuative.<p>
     * i.e. rather than:<pre>
     *  &lt;command name="mycommand"...&gt;
     *    &lt;parameter name="accessor"&gt;myService&lt;/parameter&gt;
     *  &lt;/command&gt;</pre>
     * you can use:<pre>
     *  &lt;command name="mycommand"...&gt;
     *    &lt;parameter name="<b>service</b>"&gt;myService&lt;/parameter&gt;
     *  &lt;/command&gt;</pre>
     * @param accessor The accessor to use.
     * @see #getAccessor
     */
	public void setService(Accessor accessor) {
        this.accessor=accessor;
    }

  /**
     * Invoke the service itself.
     * @throws BaseException Thrown by the service.
     */
    @Override
    public void invoke() throws BaseException {
		getAccessor().setArgs(this.getArgs());
		getAccessor().invoke();
		this.setArgs(getAccessor().getArgs());
    }

    /**
     * Get the service's configuration. This simply delegates
     * to the service's getConfiguration method.
     * @return The service's configuration.
     */
    @Override
    public Configuration getConfiguration() {
		return getAccessor().getConfiguration();
    }

    /**
     * Set the service's configuration. This method simply
     * delegates to the service's setConfiguration method.
     * @see #getConfiguration
     * @param config The configuration to pass to the service.
     */
    @Override
    public void setConfiguration(Configuration config) {
		getAccessor().setConfiguration(config);
    }

    /**
     * Clone this command.
     * @return The cloned command object.
     * @throws CloneNotSupportedException If the command (or one of its properties) cannot
     * be 'deep' cloned.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        CommandImpl clone=(CommandImpl)super.clone();

        if (accessor!=null) {
            clone.accessor=(Accessor)accessor.clone();
        }

        return clone;
    }
}
