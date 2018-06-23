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

import com.ail.annotation.TypeDefinition;

/**
 * This class represents a scripted command object. Scripted commands typically
 * represent 'soft logic'  (logic which may be altered in a live environment).
 * This type simply holds the script's details as a value object - it doesn't
 * offer any execution type functionality.
 */
@TypeDefinition()
public class CommandScript extends Type {
    private String script;
    private String namespace;
    private String commandName;
    private String type;
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Default constructor;
     */
    public CommandScript() {
    }

    /**
     * Constructor
     * @param script Text of script.
     * @param type Script's type (e.g. "BeanShell", "Drools", ...)
     * @param namespace Configuration namespace where the command belongs
     * @param commandName CommandImpl name associated with the script
     */
    public CommandScript(String script, String type, String namespace, String commandName) {
        this.script = script;
        this.type = type;
        this.namespace = namespace;
        this.commandName = commandName;
    }

    /**
     * Constructor
     * @param script Script text
     */
    public CommandScript(String script) {
        this.script=script;
    }

    /**
     * Get the text of the script itself.
     * @return The script.
     */
    public String getScript() {
        return script;
    }

    /**
     * Set the text of the script.
     * @param script
     */
    public void setScript(String script) {
        this.script = script;
    }

    /**
     * Get the script's namespace. This is the configuration namespace
     * that the script relates to (is part of).
     * @return Namespace
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * Set the script's namespace.
     * @see #getNamespace
     * @param namespace
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    /**
     * Get script's command name. This is the name that would be used
     * to execute the script as a command. i.e.:<br><br>
     * <code>
     * ...<br>
     * SomeCommand command=(SomeCommand)core.newCommand("<b>CommandName</b>");<br>
     * command.invoke()<br>
     * ...
     * </code>
     * @return The name of the command.
     */
    public String getCommandName() {
        return commandName;
    }

    /**
     * Set the command name.
     * @see #getCommandName
     * @param commandName
     */
    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    /**
     * Get the type of the script. This identifies which accessor would be
     * used to execute the script, which also indicates the type. Examples
     * might be BeanShellAccessor, or DroolsAccessor.
     * @return Type of script.
     */
    public String getType() {
        return type;
    }

    /**
     * Set the type of the script.
     * @see #getType
     * @param type Script type.
     */
    public void setType(String type) {
        this.type = type;
    }
}
